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

package dev.galacticraft.mod.api.pipe;

import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.grid.fluid.FluidDuct;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

/**
 * A fluid pipe: a duct of a {@link dev.galacticraft.mod.api.grid.fluid.FluidGrid}.
 */
public interface FluidPipe extends Connected, FluidDuct {
    /**
     * {@return the fluid storage this pipe exposes to the blocks around it}
     */
    Storage<FluidVariant> getFluidStorage();

    /**
     * Returns the maximum amount of fluid allowed to be transferred through this pipe per tick.
     *
     * @return the maximum amount of fluid allowed to be transferred through this pipe per tick.
     */
    long getMaxTransferRate();

    @Override
    default long getTransferRate() {
        return this.getMaxTransferRate();
    }
}
