/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.attribute.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeFluidInsertable implements FluidInsertable {
    private final Direction direction;
    private final FluidAmount maxTransfer;
    private final BlockPos pipe;
    private @Nullable PipeNetwork network;

    public PipeFluidInsertable(Direction direction, FluidAmount maxTransfer, BlockPos pipe) {
        this.direction = direction;
        this.maxTransfer = maxTransfer;
        this.pipe = pipe;
    }

    @Override
    public FluidVolume attemptInsertion(FluidVolume volume, Simulation simulation) {
        if (this.network != null) {
            return this.network.insert(this.pipe, volume, direction, simulation);
        }
        return volume;
    }

    @Override
    public FluidInsertable getPureInsertable() {
        return this;
    }

    public void setNetwork(@Nullable PipeNetwork network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return "PipeFluidInsertable{" +
                "direction=" + direction +
                ", maxTransfer=" + maxTransfer +
                ", pipe=" + pipe +
                ", network=" + network +
                '}';
    }
}
