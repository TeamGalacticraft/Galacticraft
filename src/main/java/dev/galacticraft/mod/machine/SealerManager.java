/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.machine;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class SealerManager {
    public static final SealerManager INSTANCE = new SealerManager();

    // Map of dimension -> sealer positions -> sealers
    private final Map<ResourceKey<Level>, Map<BlockPos, OxygenSealerBlockEntity>> sealers = new HashMap<>();

    // Map of dimension -> sealed blocks
    private final Map<ResourceKey<Level>, Set<BlockPos>> sealedBlocks = new HashMap<>();

    private SealerManager() {
    }

    public void onBlockChange(BlockPos pos, BlockState newState, ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        BlockPos sealerPos = findNearbySealer(pos, dimension);
        if (sealerPos != null) {
            recalculateSealingStatus(sealerPos, level);
        }
    }

    public void addSealer(OxygenSealerBlockEntity sealer, ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        BlockPos pos = sealer.getBlockPos();

        Constant.LOGGER.info("Adding sealer at {} in dimension {}", pos, dimension.location());
        this.sealers.computeIfAbsent(dimension, k -> new HashMap<>()).put(pos, sealer);
    }

    public void loadSealer(OxygenSealerBlockEntity sealer, ServerLevel level) {
        addSealer(sealer, level);
        BlockPos pos = sealer.getBlockPos();
        recalculateSealingStatus(pos, level);
    }

    public void removeSealer(OxygenSealerBlockEntity sealer, ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        BlockPos pos = sealer.getBlockPos();

        Constant.LOGGER.info("Removing sealer at {} in dimension {}", pos, dimension.location());
        this.sealers.getOrDefault(dimension, Collections.emptyMap()).remove(pos);
        recalculateSealingStatus(pos, level);
    }

    private BlockPos findNearbySealer(BlockPos pos, ResourceKey<Level> dimension) {
        // Search for a sealer within the range in the same dimension
        Map<BlockPos, OxygenSealerBlockEntity> dimensionSealers = this.sealers.get(dimension);
        if (dimensionSealers != null) {
            for (Map.Entry<BlockPos, OxygenSealerBlockEntity> entry : dimensionSealers.entrySet()) {
                if (entry.getKey().distSqr(pos) <= OxygenSealerBlockEntity.SEALER_RANGE * OxygenSealerBlockEntity.SEALER_RANGE) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public void recalculateSealingStatus(BlockPos sealerPos, ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        if (level.getServer() == null || !level.getServer().isReady() || !level.isLoaded(sealerPos)) {
            Constant.LOGGER.info("World is not fully loaded, skipping sealing calculation");
            return;
        }
        // Find all powered sealers in the same dimension
        List<OxygenSealerBlockEntity> poweredSealers = new ArrayList<>();
        Map<BlockPos, OxygenSealerBlockEntity> dimensionSealers = this.sealers.get(dimension);
        if (dimensionSealers != null) {
            for (OxygenSealerBlockEntity sealer : dimensionSealers.values()) {
                if (sealer.hasEnergy() && sealer.hasOxygen() && !sealer.isBlocked()) {
                    poweredSealers.add(sealer);
                }
            }
        }

        // Calculate the maximum allowed room size
        int maxRoomSize = (int) (Galacticraft.CONFIG.maxSealingPower() * poweredSealers.size());

        // Calculate the maximum possible room size
        assert dimensionSealers != null;
        int maxPossibleRoomSize = (int) (Galacticraft.CONFIG.maxSealingPower() * dimensionSealers.size());

        // Perform flood fill to calculate room size from the block above the sealer
        Set<BlockPos> sealedArea = new HashSet<>();
        int roomSize = calculateRoomSize(sealerPos.offset(0, 1, 0), level, maxRoomSize, sealedArea);
        // Update sealing status
        boolean isSealed = roomSize <= maxRoomSize;

        for (OxygenSealerBlockEntity sealer : poweredSealers) {
            sealer.setSealed(isSealed);
        }
        // Update the sealed blocks for this dimension
        Set<BlockPos> dimensionSealedBlocks = this.sealedBlocks.computeIfAbsent(dimension, k -> new HashSet<>());
        if (isSealed) {
            dimensionSealedBlocks.addAll(sealedArea);
        } else {
            sealedArea = new HashSet<>();
            calculateRoomSize(sealerPos.offset(0, 1, 0), level, maxPossibleRoomSize, sealedArea);
            dimensionSealedBlocks.removeAll(sealedArea);
        }
    }

    private int calculateRoomSize(BlockPos startPos, ServerLevel level, int maxRoomSize, Set<BlockPos> sealedArea) {
        // Use flood fill to calculate the room size
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);
        int roomSize = 0;

        while (!queue.isEmpty() && roomSize < maxRoomSize + 1) {
            BlockPos current = queue.poll();
            roomSize++;
            sealedArea.add(current); // Add the block to the sealed area

            // Check adjacent blocks
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                if (!visited.contains(neighbor) && isAirOrSealableBlock(neighbor, level)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return roomSize;
    }

    private boolean isAirOrSealableBlock(BlockPos pos, ServerLevel level) {
        // Check if the block is air or a block that can be part of a sealed room
        BlockState state = level.getBlockState(pos);
        return state.isAir();
    }

    /**
     * Checks if a block is currently sealed by a sealer in the same dimension.
     *
     * @param pos       The position of the block to check.
     * @param dimension The dimension of the block.
     * @return {@code true} if the block is sealed, {@code false} otherwise.
     */
    public boolean isSealed(BlockPos pos, ResourceKey<Level> dimension) {
        Set<BlockPos> dimensionSealedBlocks = this.sealedBlocks.get(dimension);
        if (dimensionSealedBlocks != null) {
            return dimensionSealedBlocks.contains(pos);
        }
        return false;
    }
}