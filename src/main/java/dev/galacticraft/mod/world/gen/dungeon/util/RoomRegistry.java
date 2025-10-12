package dev.galacticraft.mod.world.gen.dungeon.util;

import dev.galacticraft.mod.world.gen.dungeon.enums.RoomType;
import dev.galacticraft.mod.world.gen.dungeon.records.Constraints;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import dev.galacticraft.mod.world.gen.dungeon.records.RoomDef;
import dev.galacticraft.mod.world.gen.dungeon.records.TemplateMeta;
import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.function.Predicate;

public final class RoomRegistry {
    private final Map<String, RoomDef> byId = new HashMap<>();
    private final EnumMap<RoomType, Pool> pools = new EnumMap<>(RoomType.class);
    private boolean sealed = false;

    public RoomRegistry() {
        for (RoomType t : RoomType.values()) pools.put(t, new Pool());
    }

    /** Bootstrap entirely from code. Call this once at server/datapack init. */
    public void loadFromCode(TemplateScanner scanner, java.util.function.Consumer<Registrar> bootstrapper) {
        byId.clear();
        pools.values().forEach(Pool::clear);
        sealed = false;

        bootstrapper.accept(new Registrar(this, scanner));

        // finalize weighted pools
        pools.values().forEach(Pool::seal);
        sealed = true;
    }

    RoomDef registerRaw(RoomDef def) {
        if (sealed) throw new IllegalStateException("RoomRegistry already sealed");
        validate(def);
        byId.put(def.id(), def);
        pools.get(def.type()).add(def);
        return def;
    }

    public RoomDef get(String id) { return byId.get(id); }

    public RoomDef require(String id) {
        RoomDef d = byId.get(id);
        if (d == null) throw new IllegalArgumentException("Unknown room id: " + id);
        return d;
    }

    public RoomDef pick(RandomSource r, RoomType type, Predicate<RoomDef> filter) {
        return pools.get(type).pick(r, filter);
    }

    private static void validate(RoomDef d) {
        if (d.id() == null || d.id().isBlank()) throw new IllegalArgumentException("room id missing");
        if (d.template() == null || d.template().isBlank()) throw new IllegalArgumentException("template missing for " + d.id());
        switch (d.type()) {
            case ENTRANCE -> {
                if (!d.hasAtLeastOneExit()) throw new IllegalArgumentException("Entrance must have an EXIT port: " + d.id());
            }
            case END -> {
                if (!d.hasAtLeastOneEntrance()) throw new IllegalArgumentException("End must have ENTRANCE port(s): " + d.id());
                if (d.hasAtLeastOneExit()) throw new IllegalArgumentException("End must not have EXIT ports: " + d.id());
            }
            case TREASURE -> {
                if (d.hasAtLeastOneExit()) throw new IllegalArgumentException("Treasure must not have EXIT ports: " + d.id());
                if (!d.hasAtLeastOneEntrance()) throw new IllegalArgumentException("Treasure needs at least one ENTRANCE: " + d.id());
            }
            default -> {
                if (!d.hasAtLeastOneEntrance() || !d.hasAtLeastOneExit())
                    throw new IllegalArgumentException("Room must have both ENTRANCE and EXIT: " + d.id());
            }
        }
    }

    // ---- Registrar & Builder -------------------------------------------------

    public static final class Registrar {
        private final RoomRegistry reg;
        private final TemplateScanner scanner;

        Registrar(RoomRegistry reg, TemplateScanner scanner) {
            this.reg = reg; this.scanner = scanner;
        }

        public Builder room(String id, RoomType type, String template) {
            return new Builder(reg, scanner, id, type, template);
        }

        // Convenience factories
        public Builder entrance(String id, String template)   { return room(id, RoomType.ENTRANCE, template); }
        public Builder end(String id, String template)        { return room(id, RoomType.END, template); }
        public Builder queen(String id, String template)      { return room(id, RoomType.QUEEN, template); }
        public Builder basic(String id, String template)      { return room(id, RoomType.BASIC, template); }
        public Builder treasure(String id, String template)   { return room(id, RoomType.TREASURE, template); }}

    public static final class Builder {
        private final RoomRegistry reg;
        private final TemplateScanner scanner;

        private final String id;
        private final RoomType type;
        private final String template;
        private int weight = 1;
        private final List<String> tags = new ArrayList<>();
        private Constraints constraints = Constraints.any();

        Builder(RoomRegistry reg, TemplateScanner scanner, String id, RoomType type, String template) {
            this.reg = reg; this.scanner = scanner;
            this.id = Objects.requireNonNull(id);
            this.type = Objects.requireNonNull(type);
            this.template = Objects.requireNonNull(template);
        }

        public Builder weight(int w) { this.weight = Math.max(1, w); return this; }
        public Builder tag(String t) { if (t != null && !t.isBlank()) tags.add(t); return this; }
        public Builder tags(String... ts) { if (ts != null) for (String t : ts) tag(t); return this; }
        public Builder constraints(Constraints c) { if (c != null) this.constraints = c; return this; }

        /** Scans template once; produces a canonical RoomDef and registers it. */
        public RoomDef register() {
            TemplateMeta meta = scanner.scan(template);
            PortDef[] entrances = meta.entrances();
            PortDef[] exits     = meta.exits();
            RoomDef def = new RoomDef(
                    id, type, weight, template,
                    tags.toArray(String[]::new), constraints,
                    entrances, exits,
                    meta.sizeX(), meta.sizeY(), meta.sizeZ()
            );
            return reg.registerRaw(def);
        }
    }

    // ---- Weighted Pool -------------------------------------------------------

    private static final class Pool {
        private RoomDef[] items = new RoomDef[0];
        private int[] cum = new int[0];
        private int total = 0;
        private boolean sealed = false;

        void clear() { items = new RoomDef[0]; cum = new int[0]; total = 0; sealed = false; }

        void add(RoomDef d) {
            if (sealed) throw new IllegalStateException("pool sealed");
            items = Arrays.copyOf(items, items.length + 1);
            items[items.length - 1] = d;
        }

        void seal() {
            if (sealed) return;
            cum = new int[items.length];
            int acc = 0;
            for (int i = 0; i < items.length; i++) {
                acc += Math.max(1, items[i].weight());
                cum[i] = acc;
            }
            total = acc;
            sealed = true;
        }

        RoomDef pick(RandomSource r, Predicate<RoomDef> filter) {
            if (!sealed) seal();
            if (items.length == 0) return null;

            // a few fast attempts without allocation
            for (int tries = 0; tries < 8; tries++) {
                RoomDef d = weighted(r);
                if (filter == null || filter.test(d)) return d;
            }

            // fallback: filtered list (one pass)
            int[] idx = new int[items.length];
            int n = 0, wSum = 0;
            for (int i = 0; i < items.length; i++) {
                RoomDef d = items[i];
                if (filter == null || filter.test(d)) {
                    idx[n++] = i;
                    wSum += Math.max(1, d.weight());
                }
            }
            if (n == 0) return null;

            int k = r.nextInt(wSum), acc = 0;
            for (int t = 0; t < n; t++) {
                RoomDef d = items[idx[t]];
                acc += Math.max(1, d.weight());
                if (k < acc) return d;
            }
            return items[idx[n - 1]];
        }

        private RoomDef weighted(RandomSource r) {
            int k = r.nextInt(total);
            int i = Arrays.binarySearch(cum, k + 1);
            if (i < 0) i = ~i;
            if (i >= items.length) i = items.length - 1;
            return items[i];
        }
    }
}