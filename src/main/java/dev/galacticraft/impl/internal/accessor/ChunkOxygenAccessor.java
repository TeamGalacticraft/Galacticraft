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

package dev.galacticraft.impl.internal.accessor;

public interface ChunkOxygenAccessor {
    /**
     * Returns whether the supplied position in the chunk is breathable for entities
     *
     * @param x the position to test on the X-axis, normalized from 0 to 15
     * @param y the position to test on the Y-axis, must be within world height
     * @param z the position to test on the Z-axis, normalized from 0 to 15
     * @return whether the supplied position in the chunk is breathable for entities
     */
    boolean galacticraft$isInverted(int x, int y, int z);

    /**
     * Sets the breathable state for entities for the supplied position
     *
     * @param x     the position to test on the X-axis, normalized from 0 to 15
     * @param y     the position to test on the Y-axis, must be within world height
     * @param z     the position to test on the Z-axis, normalized from 0 to 15
     * @param inverted whether the supplied position is breathable
     */
    void galacticraft$setInverted(int x, int y, int z, boolean inverted);
}
