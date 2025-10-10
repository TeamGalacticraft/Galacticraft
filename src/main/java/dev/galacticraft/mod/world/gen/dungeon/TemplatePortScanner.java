package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.Constant;
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

public final class TemplatePortScanner {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation ENTRANCE_BLOCK_ID = Constant.id("dungeon_entrance_block");
    private static final ResourceLocation EXIT_BLOCK_ID = Constant.id("dungeon_exit_block");

    private TemplatePortScanner() {
    }

    /**
     * Load template, detect size and port clusters (aperture & facing), return local-space ports.
     */
    public static ScannedTemplate scan(StructureTemplateManager tm, ResourceLocation templateId) {
        StructureTemplate tpl = tm.getOrCreate(templateId);
        Vec3i size = tpl.getSize();

        Block entranceBlock = BuiltInRegistries.BLOCK.get(ENTRANCE_BLOCK_ID);
        Block exitBlock = BuiltInRegistries.BLOCK.get(EXIT_BLOCK_ID);

        var settings = new StructurePlaceSettings(); // identity rotation/mirror
        List<StructureTemplate.StructureBlockInfo> entranceInfos = tpl.filterBlocks(BlockPos.ZERO, settings, entranceBlock);
        List<StructureTemplate.StructureBlockInfo> exitInfos = tpl.filterBlocks(BlockPos.ZERO, settings, exitBlock);

        List<BlockPos> entrances = entranceInfos.stream().map(StructureTemplate.StructureBlockInfo::pos).toList();
        List<BlockPos> exits = exitInfos.stream().map(StructureTemplate.StructureBlockInfo::pos).toList();

        //LOGGER.info("[PortScan] id={} size={} markers: entrances={} exits={}", templateId, size, entrances.size(), exits.size());

        var entrancePorts = clusterToPorts(entrances, size, "entrance", templateId);
        var exitPorts = clusterToPorts(exits, size, "exit", templateId);

        //LOGGER.info("[PortScan] id={} ports: entrances={} exits={}", templateId, entrancePorts.size(), exitPorts.size());
        return new ScannedTemplate(size, entrancePorts, exitPorts);
    }

    /**
     * Group 6-connected blocks into clusters -> each cluster becomes a single Port.
     */
    private static List<Port> clusterToPorts(List<BlockPos> points, Vec3i size, String kind, ResourceLocation id) {
        List<Port> out = new ArrayList<>();
        Set<BlockPos> unvisited = new HashSet<>(points);
        int idx = 0;

        while (!unvisited.isEmpty()) {
            BlockPos seed = unvisited.iterator().next();
            List<BlockPos> cluster = new ArrayList<>();
            Deque<BlockPos> dq = new ArrayDeque<>();
            dq.add(seed);
            unvisited.remove(seed);

            while (!dq.isEmpty()) {
                BlockPos p = dq.poll();
                cluster.add(p);
                for (var d : Direction.values()) {
                    BlockPos n = p.relative(d);
                    if (unvisited.remove(n)) dq.add(n);
                }
            }

            Direction facing = inferFacing(cluster, size);
            if (facing == null) {
                LOGGER.info("[PortScan] id={} [{} #{}] REJECT: cluster not on template face; pts={}", id, kind, idx, cluster.size());
                continue;
            }

            int spanA = span(cluster, inPlaneAxisA(facing));
            int spanB = span(cluster, inPlaneAxisB(facing));
            int aperture = Math.min(spanA, spanB);

            BlockPos cmin = min(cluster);
            BlockPos cmax = max(cluster);
            BlockPos center = new BlockPos((cmin.getX() + cmax.getX()) / 2, (cmin.getY() + cmax.getY()) / 2, (cmin.getZ() + cmax.getZ()) / 2);

            //LOGGER.info("[PortScan] id={} [{} #{}] face={} bboxMin={} bboxMax={} center={} spanA={} spanB={} aperture={}",
            //        id, kind, idx, facing, cmin, cmax, center, spanA, spanB, aperture);

            out.add(new Port(center, facing, aperture, (kind.equals("entrance"))));
            idx++;
        }
        if (out.isEmpty() && !points.isEmpty()) {
            LOGGER.info("[PortScan] id={} [{}] WARNING: markers found but no valid ports derived.", id, kind);
        }
        return out;
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

    private static BlockPos min(List<BlockPos> pts) {
        int x = Integer.MAX_VALUE, y = Integer.MAX_VALUE, z = Integer.MAX_VALUE;
        for (var p : pts) {
            x = Math.min(x, p.getX());
            y = Math.min(y, p.getY());
            z = Math.min(z, p.getZ());
        }
        return new BlockPos(x, y, z);
    }

    private static BlockPos max(List<BlockPos> pts) {
        int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE, z = Integer.MIN_VALUE;
        for (var p : pts) {
            x = Math.max(x, p.getX());
            y = Math.max(y, p.getY());
            z = Math.max(z, p.getZ());
        }
        return new BlockPos(x, y, z);
    }
}