package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MoonCavePlan {
    private static final int PADDING = 5;

    private final ResourceLocation definitionId;
    private final MoonCaveCellPos cell;
    private final double priority;
    private final EnumSet<MoonCaveStyle> styles;
    private final MoonCaveBounds bounds = new MoonCaveBounds();
    private final List<MoonCaveRoom> rooms = new ArrayList<>();
    private final List<MoonCaveTunnel> tunnels = new ArrayList<>();

    public MoonCavePlan(ResourceLocation definitionId, MoonCaveCellPos cell, double priority, MoonCaveStyle style) {
        this.definitionId = definitionId;
        this.cell = cell;
        this.priority = priority;
        this.styles = EnumSet.of(style);
    }

    public ResourceLocation definitionId() {
        return this.definitionId;
    }

    public MoonCaveCellPos cell() {
        return this.cell;
    }

    public double priority() {
        return this.priority;
    }

    public EnumSet<MoonCaveStyle> styles() {
        return this.styles;
    }

    public MoonCaveStyle primaryStyle() {
        return this.styles.iterator().next();
    }

    public MoonCaveBounds bounds() {
        return this.bounds;
    }

    public void addStyle(MoonCaveStyle style) {
        this.styles.add(style);
    }

    public void addRoom(MoonCaveRoom room) {
        this.rooms.add(room);
        this.bounds.includeRoom(room.center(), room.radiusX(), room.radiusY(), room.radiusZ(), PADDING);
    }

    public void addTunnel(MoonCaveTunnel tunnel) {
        this.tunnels.add(tunnel);
        this.bounds.includeTunnel(tunnel.start(), tunnel.end(), tunnel.radius(), tunnel.curve(), PADDING);
    }

    public void mergeFrom(MoonCavePlan other) {
        this.styles.addAll(other.styles);

        for (MoonCaveRoom room : other.rooms) {
            this.addRoom(room);
        }

        for (MoonCaveTunnel tunnel : other.tunnels) {
            this.addTunnel(tunnel);
        }
    }

    public CaveZone zone(int x, int y, int z) {
        CaveZone best = CaveZone.NONE;

        for (MoonCaveRoom room : this.rooms) {
            best = max(best, room.zone(x, y, z));
            if (best == CaveZone.AIR) {
                return CaveZone.AIR;
            }
        }

        for (MoonCaveTunnel tunnel : this.tunnels) {
            best = max(best, tunnel.zone(x, y, z));
            if (best == CaveZone.AIR) {
                return CaveZone.AIR;
            }
        }

        return best;
    }

    private static CaveZone max(CaveZone first, CaveZone second) {
        return second.ordinal() > first.ordinal() ? second : first;
    }
}