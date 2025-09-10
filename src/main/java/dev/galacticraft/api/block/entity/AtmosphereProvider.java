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

package dev.galacticraft.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Describes a {@link BlockEntity} that generates an atmosphere somewhere in the world.
 *
 * @see dev.galacticraft.api.accessor.LevelOxygenAccessor
 * @see dev.galacticraft.api.accessor.ChunkOxygenAccessor
 */
public interface AtmosphereProvider {
    /**
     * {@return whether the given point is breathable}
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @param z the z-coordinate to check
     */
    boolean canBreathe(double x, double y, double z);

    /**
     * {@return whether the given block position is breathable}
     * It is not defined where within the block is (or is not) breathable.
     * @param x the x-position to check
     * @param y the y-position to check
     * @param z the z-position to check
     */
    default boolean canBreathe(int x, int y, int z) {
        return this.canBreathe(new BlockPos(x, y, z));
    }

    /**
     * {@return whether the given block position is breathable}
     * It is not defined where within the block is (or is not) breathable.
     * @param pos the position to check
     */
    boolean canBreathe(BlockPos pos);

    /**
     * Called when a block state is updated within a chunk that has this atmospheric provider attached.
     * @param pos the position of block that was changed
     * @param newState the new state being placed at the position.
     */
    void notifyStateChange(BlockPos pos, BlockState newState);

    /**
     * Helper method to get a block entity instance.
     * @return {@code this}
     */
    default BlockEntity be() {
        return (BlockEntity) this;
    }
}
