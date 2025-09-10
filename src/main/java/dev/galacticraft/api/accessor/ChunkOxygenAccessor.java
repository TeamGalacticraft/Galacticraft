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

import dev.galacticraft.api.block.entity.AtmosphereProvider;
import net.minecraft.core.BlockPos;

import java.util.Iterator;

public interface ChunkOxygenAccessor {
    /**
     * {@return the {@link AtmosphereProvider atmosphere providers} that service the chunk section at the given y-position}
     * @param y the block y-height of the section to check.
     */
    Iterator<AtmosphereProvider> galacticraft$getProviders(int y);

    /**
     * {@return the positions of the {@link AtmosphereProvider atmosphere providers} that service the chunk section at the given y-position}
     * @param y the block y-height of the section to check.
     */
    Iterator<BlockPos> galacticraft$getProviderPositions(int y);

    /**
     * Links the given atmospheric provider to the chunk section at the given index.
     * @param sectionIndex the index of the chunk section being provided to.
     * @param provider the location of the atmospheric provider being linked.
     */
    void galacticraft$addAtmosphericProvider(int sectionIndex, BlockPos provider);

    /**
     * Unlinks the given atmospheric provider to the chunk section at the given index.
     * @param sectionIndex the index of the chunk section that is no longer being provided to.
     * @param provider the location of the atmospheric provider being unlinked.
     */
    void galacticraft$removeAtmosphericProvider(int sectionIndex, BlockPos provider);
}
