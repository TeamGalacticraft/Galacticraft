package dev.galacticraft.mod.world.gen.dungeon;

import dev.galacticraft.mod.world.gen.dungeon.records.BlockData;
import dev.galacticraft.mod.world.gen.dungeon.records.DungeonResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Queues dungeon block placements and applies them on server ticks,
 * but only when the target chunk is loaded.
 */
public final class DungeonPlacementManager {

    /**
     * Default per-tick placement budget (total blocks across all dimensions).
     */
    public static final int DEFAULT_BLOCK_BUDGET = 8192;

    // dim -> (chunk -> (pos -> state))  (all layers concurrent)
    private static final Map<ResourceKey<Level>,
            Map<ChunkPos, Map<BlockPos, BlockState>>> PENDING = new ConcurrentHashMap<>();

    private DungeonPlacementManager() {
    }

    /**
     * Enqueue all blocks for the given dimension. No level reference needed.
     * Last write to the same position wins.
     */
    public static void enqueue(ResourceKey<Level> dimension, DungeonResult result) {
        if (result == null || result.blockData() == null || result.blockData().isEmpty()) return;

        // Get / create per-dimension map (concurrent)
        Map<ChunkPos, Map<BlockPos, BlockState>> byChunk =
                PENDING.computeIfAbsent(dimension, d -> new ConcurrentHashMap<>());

        // Expand SectionPos+BlockData -> world BlockPos, bucket by ChunkPos, dedupe by BlockPos
        for (Map.Entry<SectionPos, List<BlockData>> e : result.blockData().entrySet()) {
            SectionPos sec = e.getKey();
            List<BlockData> list = e.getValue();
            if (list == null || list.isEmpty()) continue;

            for (BlockData bd : list) {
                BlockPos world = bd.toBlockPos(sec);
                ChunkPos cp = new ChunkPos(world);

                // concurrent per-chunk map
                Map<BlockPos, BlockState> writes =
                        byChunk.computeIfAbsent(cp, k -> new ConcurrentHashMap<>());

                // Last one wins for duplicates
                writes.put(world, bd.state());
            }
        }
    }

    /**
     * Server tick hook. Applies queued blocks up to a budget (to avoid spikes).
     */
    public static void tick(MinecraftServer server) {
        tick(server, DEFAULT_BLOCK_BUDGET);
    }

    public static void tick(MinecraftServer server, int blockBudget) {
        if (blockBudget <= 0 || PENDING.isEmpty()) return;

        int placed = 0;

        // Iterate dimensions (copy keys to avoid races on keySet)
        for (ResourceKey<Level> dim : new ArrayList<>(PENDING.keySet())) {
            if (placed >= blockBudget) break;

            ServerLevel level = server.getLevel(dim);
            if (level == null) continue; // dimension not loaded/available

            Map<ChunkPos, Map<BlockPos, BlockState>> byChunk = PENDING.get(dim);
            if (byChunk == null || byChunk.isEmpty()) {
                PENDING.remove(dim);
                continue;
            }

            // Iterate chunks (copy keys to avoid races)
            for (ChunkPos cp : new ArrayList<>(byChunk.keySet())) {
                if (placed >= blockBudget) break;

                // Only place if chunk is loaded
                if (!level.hasChunk(cp.x, cp.z)) continue;

                Map<BlockPos, BlockState> writes = byChunk.get(cp);
                if (writes == null || writes.isEmpty()) {
                    byChunk.remove(cp);
                    continue;
                }

                // Snapshot some entries up to remaining budget
                int allowance = blockBudget - placed;
                // Weakly-consistent snapshot; fine for gradual draining
                List<Map.Entry<BlockPos, BlockState>> snapshot = new ArrayList<>(Math.min(allowance, writes.size()));
                int taken = 0;
                for (Map.Entry<BlockPos, BlockState> en : writes.entrySet()) {
                    snapshot.add(en);
                    if (++taken >= allowance) break;
                }

                // Place & remove
                for (Map.Entry<BlockPos, BlockState> en : snapshot) {
                    BlockPos pos = en.getKey();
                    BlockState state = en.getValue();

                    level.setBlock(pos, state, 2); // flag 2 = send to clients, light neighbor updates
                    placed++;

                    // remove the exact entry we just applied (ok if already changed; then it remains for next tick)
                    writes.remove(pos, state);

                    if (placed >= blockBudget) break;
                }

                // Clean up chunk bucket if empty
                if (writes.isEmpty()) byChunk.remove(cp);
            }

            // Clean up dimension bucket if empty
            if (byChunk.isEmpty()) PENDING.remove(dim);
        }
    }

    /**
     * True if there is no pending work.
     */
    public static boolean isIdle() {
        return PENDING.isEmpty();
    }
}