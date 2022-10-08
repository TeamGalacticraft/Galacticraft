/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.impl.fluid.FluidStack;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeFluidInsertable implements Storage<FluidVariant>, StorageView<FluidVariant> {
    private final Direction direction;
    private final long maxTransfer;
    private final BlockPos pipe;
    private @Nullable PipeNetwork network;

    public PipeFluidInsertable(Direction direction, long maxTransfer, BlockPos pipe) {
        this.direction = direction;
        this.maxTransfer = maxTransfer;
        this.pipe = pipe;
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

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        if (this.network != null) {
            return this.network.insert(this.pipe, new FluidStack(resource, maxAmount), direction, transaction);
        }
        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long simulateExtract(FluidVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return true;
    }

    @Override
    public FluidVariant getResource() {
        return FluidVariant.blank();
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return this.maxTransfer;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return Collections.emptyIterator();
    }
}
