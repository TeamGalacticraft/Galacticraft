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
import net.minecraft.world.level.block.state.BlockState;

/**
 * @see LevelOxygenAccessorRO
 */
public interface LevelOxygenAccessor extends LevelOxygenAccessorRO {
    /**
     * Adds an atmosphere provider to the chunk section at the given position
     * @param sectionX the section x-position
     * @param sectionY the section y-position
     * @param sectionZ the section z-position
     * @param provider the position of the provider to add
     */
    void galacticraft$addAtmosphereProvider(int sectionX, int sectionY, int sectionZ, BlockPos provider);

    /**
     * Removes an atmosphere provider from the chunk section at the given position
     * @param sectionX the section x-position
     * @param sectionY the section y-position
     * @param sectionZ the section z-position
     * @param provider the position of the provider to remove
     */
    void galacticraft$removeAtmosphereProvider(int sectionX, int sectionY, int sectionZ, BlockPos provider);

    /**
     * Notifies the block at the given position that the atmosphere has changed.
     * @param pos the position where the atmosphere changed
     * @param state the current block state of the block where the atmosphere changed
     */
    default void galacticraft$notifyAtmosphereChange(BlockPos pos, BlockState state) {}
}
