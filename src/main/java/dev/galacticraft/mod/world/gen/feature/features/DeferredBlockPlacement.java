package dev.galacticraft.mod.world.gen.feature.features;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeferredBlockPlacement {
    private static final Map<ChunkPos, List<Placement>> QUEUE = new HashMap<>();

    public static void queue(BlockPos pos, BlockState state) {
        ChunkPos chunk = new ChunkPos(pos);
        QUEUE.computeIfAbsent(chunk, c -> new ArrayList<>()).add(new Placement(pos.immutable(), state));
    }

    public static void flush(WorldGenLevel level, ChunkPos chunk) {
        List<Placement> placements = QUEUE.remove(chunk);
        if (placements == null) return;

        for (Placement p : placements) {
            try {
                if (level.getBlockState(p.pos).isAir() || level.getBlockState(p.pos).is(Blocks.GLASS)) {
                    level.setBlock(p.pos, p.state, 2);
                }
            } catch (IllegalStateException ignored) {
                // If still unloaded (somehow), requeue
                queue(p.pos, p.state);
            }
        }
    }

    private record Placement(BlockPos pos, BlockState state) {}
}