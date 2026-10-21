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

package dev.galacticraft.mod.api.grid.fluid;

import dev.galacticraft.mod.api.grid.Duct;
import net.minecraft.core.Direction;

/**
 * A duct of a {@link FluidGrid}, like a fluid pipe.
 * Two fluid ducts only connect while their grids hold the same fluid, or one of them is empty.
 */
public interface FluidDuct extends Duct<FluidGrid, FluidGridNode> {
    /**
     * {@return the most fluid (in droplets) that can enter or leave the grid through this duct per tick}
     */
    long getTransferRate();

    /**
     * {@return whether this duct could ever connect to the given fluid duct, ignoring the fluids in their grids}
     */
    boolean canConnectToDuct(FluidDuct other, Direction direction);

    @Override
    default boolean canEverConnectTo(Duct<?, ?> other, Direction direction) {
        return other instanceof FluidDuct duct && this.canConnectToDuct(duct, direction);
    }

    @Override
    default boolean canConnectTo(Duct<?, ?> other, Direction direction) {
        return this.canEverConnectTo(other, direction) && FluidGrid.canHoldTogether(this.getGrid(), ((FluidDuct) other).getGrid());
    }
}
