/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.api.grid.energy;

import dev.galacticraft.mod.api.grid.Duct;
import net.minecraft.core.Direction;

/**
 * A duct of an {@link EnergyGrid}, like a wire.
 */
public interface EnergyDuct extends Duct<EnergyGrid, EnergyGridNode> {
    /**
     * {@return the most energy (in gJ) that can enter or leave the grid through this duct per tick}
     */
    long getTransferRate();

    /**
     * {@return how much this duct adds to the buffer of its grid when it is a node}
     */
    long getNodeCapacity();

    @Override
    default boolean canConnectTo(Duct<?, ?> other, Direction direction) {
        return other instanceof EnergyDuct;
    }
}
