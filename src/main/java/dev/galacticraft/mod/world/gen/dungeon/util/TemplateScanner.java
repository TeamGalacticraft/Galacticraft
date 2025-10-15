package dev.galacticraft.mod.world.gen.dungeon.util;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import dev.galacticraft.mod.world.gen.dungeon.records.TemplateMeta;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

import java.util.*;

/**
 * Scans structure templates for entrance/exit marker blocks and returns {@link TemplateMeta}.
 */
public final class TemplateScanner {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final StructureTemplateManager templateManager;
    private final ResourceLocation entranceMarkerId;
    private final ResourceLocation exitMarkerId;
    private final Block entranceMarker;
    private final Block exitMarker;

    private final Map<ResourceLocation, TemplateMeta> cache = new HashMap<>();
    /**
     * Cache of template → raw blocks
     */
    private final Map<ResourceLocation, TemplateBlocks> blocksCache = new HashMap<>();

    public TemplateScanner(StructureTemplateManager templateManager) {
        this(templateManager, Constant.id("dungeon_entrance_block"), Constant.id("dungeon_exit_block"));
    }

    public TemplateScanner(StructureTemplateManager templateManager,
                           ResourceLocation entranceMarkerId,
                           ResourceLocation exitMarkerId) {
        this.templateManager = Objects.requireNonNull(templateManager, "templateManager");
        this.entranceMarkerId = Objects.requireNonNull(entranceMarkerId, "entranceMarkerId");
        this.exitMarkerId = Objects.requireNonNull(exitMarkerId, "exitMarkerId");
        this.entranceMarker = BuiltInRegistries.BLOCK.get(this.entranceMarkerId);
        this.exitMarker = BuiltInRegistries.BLOCK.get(this.exitMarkerId);
    }

    private static PortDef[] clusterToPorts(List<BlockPos> pts, Vec3i size, boolean entrance, ResourceLocation id) {
        if (pts.isEmpty()) return new PortDef[0];

        ArrayList<PortDef> out = new ArrayList<>();
        HashSet<BlockPos> unvisited = new HashSet<>(pts);
        int idx = 0;

        while (!unvisited.isEmpty()) {
            BlockPos seed = unvisited.iterator().next();
            ArrayDeque<BlockPos> dq = new ArrayDeque<>();
            ArrayList<BlockPos> cluster = new ArrayList<>();
            dq.add(seed);
            unvisited.remove(seed);

            // 6-neighbor flood fill to get one rectangular portal cluster
            while (!dq.isEmpty()) {
                BlockPos p = dq.pollFirst();
                cluster.add(p);
                for (Direction d : Direction.values()) {
                    BlockPos n = p.relative(d);
                    if (unvisited.remove(n)) dq.addLast(n);
                }
            }

            Direction facing = inferFacing(cluster, size);
            if (facing == null) {
                LOGGER.warn("[TemplateScanner] {} [{} #{}] rejected cluster not on template face ({} pts)",
                        id, entrance ? "entr" : "exit", idx, cluster.size());
                idx++;
                continue;
            }

            BlockPos cmin = min(cluster);
            BlockPos cmax = max(cluster);

            // (Optional) validate it’s a flat plane on the inferred face
            boolean flat = switch (facing.getAxis()) {
                case X -> cmin.getX() == cmax.getX();
                case Y -> cmin.getY() == cmax.getY();
                case Z -> cmin.getZ() == cmax.getZ();
            };
            if (!flat) {
                LOGGER.warn("[TemplateScanner] {} [{} #{}] cluster not flat on face {}", id,
                        entrance ? "entr" : "exit", idx, facing);
            }

            String name = (entrance ? "entr_" : "exit_") + idx;
            out.add(new PortDef(
                    name,
                    entrance,
                    !entrance,
                    facing,
                    cmin,
                    cmax
            ));

            // (Optional) diagnostics
            int spanA = span(cluster, inPlaneAxisA(facing));
            int spanB = span(cluster, inPlaneAxisB(facing));
            int aperture = Math.max(1, Math.min(spanA, spanB));
            BlockPos center = new BlockPos(
                    (cmin.getX() + cmax.getX()) / 2,
                    (cmin.getY() + cmax.getY()) / 2,
                    (cmin.getZ() + cmax.getZ()) / 2
            );
            LOGGER.debug("[TemplateScanner] {} [{} #{}] face={} min={} max={} center={} aperture={}",
                    id, entrance ? "entr" : "exit", idx, facing, cmin, cmax, center, aperture);

            idx++;
        }

        if (out.isEmpty()) {
            LOGGER.warn("[TemplateScanner] {} [{}] markers present but no valid ports derived (off-face?)",
                    id, entrance ? "entr" : "exit");
        }
        return out.toArray(PortDef[]::new);
    }

    private static Direction inferFacing(List<BlockPos> cluster, Vec3i size) {
        boolean atMinX = cluster.stream().allMatch(p -> p.getX() == 0);
        boolean atMaxX = cluster.stream().allMatch(p -> p.getX() == size.getX() - 1);
        boolean atMinZ = cluster.stream().allMatch(p -> p.getZ() == 0);
        boolean atMaxZ = cluster.stream().allMatch(p -> p.getZ() == size.getZ() - 1);
        boolean atMinY = cluster.stream().allMatch(p -> p.getY() == 0);
        boolean atMaxY = cluster.stream().allMatch(p -> p.getY() == size.getY() - 1);

        if (atMinX) return Direction.WEST;
        if (atMaxX) return Direction.EAST;
        if (atMinZ) return Direction.NORTH;
        if (atMaxZ) return Direction.SOUTH;
        if (atMinY) return Direction.DOWN;
        if (atMaxY) return Direction.UP;
        return null;
    }

    private static Direction.Axis inPlaneAxisA(Direction facing) {
        return switch (facing) {
            case NORTH, SOUTH -> Direction.Axis.X;
            case EAST, WEST -> Direction.Axis.Z;
            case UP, DOWN -> Direction.Axis.X;
        };
    }

    private static Direction.Axis inPlaneAxisB(Direction facing) {
        return switch (facing) {
            case NORTH, SOUTH, EAST, WEST -> Direction.Axis.Y;
            case UP, DOWN -> Direction.Axis.Z;
        };
    }

    private static int span(List<BlockPos> pts, Direction.Axis axis) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (BlockPos p : pts) {
            int v = switch (axis) {
                case X -> p.getX();
                case Y -> p.getY();
                case Z -> p.getZ();
            };
            if (v < min) min = v;
            if (v > max) max = v;
        }
        return (max - min) + 1;
    }

    // ---- internals ----

    private static BlockPos min(List<BlockPos> pts) {
        int x = Integer.MAX_VALUE, y = Integer.MAX_VALUE, z = Integer.MAX_VALUE;
        for (var p : pts) {
            if (p.getX() < x) x = p.getX();
            if (p.getY() < y) y = p.getY();
            if (p.getZ() < z) z = p.getZ();
        }
        return new BlockPos(x, y, z);
    }

    private static BlockPos max(List<BlockPos> pts) {
        int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE, z = Integer.MIN_VALUE;
        for (var p : pts) {
            if (p.getX() > x) x = p.getX();
            if (p.getY() > y) y = p.getY();
            if (p.getZ() > z) z = p.getZ();
        }
        return new BlockPos(x, y, z);
    }

    /**
     * Ensure raw blocks for this template are loaded & cached.
     */
    public TemplateBlocks loadBlocks(ResourceLocation templateId) {
        TemplateBlocks hit = blocksCache.get(templateId);
        if (hit != null) return hit;

        StructureTemplate tpl = templateManager.getOrCreate(templateId);
        var size = tpl.getSize();
        // NOTE: palette(0).blocks() is in LOCAL template coords; states are UN-processed
        List<StructureTemplate.StructureBlockInfo> raw = List.copyOf(tpl.palettes.getFirst().blocks());

        TemplateBlocks tb = new TemplateBlocks(size.getX(), size.getY(), size.getZ(), raw);
        blocksCache.put(templateId, tb);
        return tb;
    }

    /**
     * Optional convenience: load both ports & raw blocks in one call.
     */
    public TemplateMeta scanAndWarm(ResourceLocation templateId) {
        TemplateMeta meta = scan(templateId);   // your existing scan() fills port cache
        loadBlocks(templateId);                 // warm block cache too
        return meta;
    }

    public TemplateMeta scan(String templateId) {
        return scan(ResourceLocation.parse(templateId));
    }

    public TemplateMeta scan(ResourceLocation templateId) {
        TemplateMeta hit = cache.get(templateId);
        if (hit != null) return hit;

        StructureTemplate tpl = templateManager.getOrCreate(templateId);
        Vec3i size = tpl.getSize();

        var place = new StructurePlaceSettings();
        List<StructureTemplate.StructureBlockInfo> eInfos = tpl.filterBlocks(BlockPos.ZERO, place, entranceMarker);
        List<StructureTemplate.StructureBlockInfo> xInfos = tpl.filterBlocks(BlockPos.ZERO, place, exitMarker);

        List<BlockPos> entrances = eInfos.stream().map(StructureTemplate.StructureBlockInfo::pos).toList();
        List<BlockPos> exits = xInfos.stream().map(StructureTemplate.StructureBlockInfo::pos).toList();

        if (entrances.isEmpty() && exits.isEmpty()) {
            LOGGER.warn("[TemplateScanner] {} has no entrance/exit markers. size={}", templateId, size);
        }

        PortDef[] ePorts = clusterToPorts(entrances, size, true, templateId);
        PortDef[] xPorts = clusterToPorts(exits, size, false, templateId);

        TemplateMeta meta = new TemplateMeta(
                size.getX(), size.getY(), size.getZ(),
                ePorts, xPorts
        );

        cache.put(templateId, meta);
        LOGGER.info("[TemplateScanner] {} scanned: size={} entrances={} exits={}",
                templateId, size, ePorts.length, xPorts.length);
        return meta;
    }

    public StructureTemplateManager templateManager() {
        return this.templateManager;
    }

    /**
     * Raw, unprocessed blocks (palette 0) + size
     */
    public record TemplateBlocks(int sx, int sy, int sz, List<StructureTemplate.StructureBlockInfo> rawBlocks) {
    }
}