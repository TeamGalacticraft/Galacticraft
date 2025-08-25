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

package dev.galacticraft.api.accessor;

import net.minecraft.core.BlockPos;

public interface LevelOxygenAccessor extends LevelOxygenAccessorRO {
    /**
     * Adds an atmospheric provider to the given block position.
     * The given provider MUST be allocated on the chunk section before being added.
     *
     * @param x the x-coordinate of the block gaining a provider
     * @param y the y-coordinate of the block gaining a provider
     * @param z the z-coordinate of the block gaining a provider
     * @param providerPos the position of the atmospheric provider
     * @see dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor#galacticraft$ensureSpaceFor(BlockPos)
     */
    default void galacticraft$addAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * Removes the given atmospheric provider from the given block position.
     * If the provider does not exist, nothing changes.
     */
    default void galacticraft$removeAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default void galacticraft$addAtmosphericProvider(BlockPos pos, BlockPos providerPos) {
        this.galacticraft$addAtmosphericProvider(pos.getX(), pos.getY(), pos.getZ(), providerPos);
    }

    default void galacticraft$removeAtmosphericProvider(BlockPos pos, BlockPos providerPos) {
        this.galacticraft$removeAtmosphericProvider(pos.getX(), pos.getY(), pos.getZ(), providerPos);
    }
}
