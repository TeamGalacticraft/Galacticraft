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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.grid.Duct;
import dev.galacticraft.mod.api.grid.GridNode;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

/**
 * A node of a {@link FluidGrid}. Nodes next to blocks that accept fluid push the fluid of the grid into them.
 * Ported from ThermalDynamics' {@code FluidGridNode}.
 */
public class FluidGridNode extends GridNode<FluidGrid> {
    private static final String TRANSFER_RATE = "TransferRate";

    protected Direction[] distArray = new Direction[0];
    protected int distIndex = 0;
    private long transferRate = -1;

    protected FluidGridNode(FluidGrid grid) {
        super(grid);
    }

    void readDuct() {
        if (this.getDuct() instanceof FluidDuct duct) {
            this.transferRate = duct.getTransferRate();
        }
    }

    public long getTransferRate() {
        return this.transferRate < 0 ? FluidGrid.DEFAULT_TRANSFER_RATE : this.transferRate;
    }

    protected void cacheConnections() {
        for (Direction dir : Constant.Misc.DIRECTIONS) {
            BlockPos neighbor = this.pos.relative(dir);
            if (this.getLevel().isLoaded(neighbor) && this.grid.canConnectOnSide(neighbor, dir.getOpposite())) {
                this.connections.add(dir);
            }
        }
        this.distArray = this.connections.toArray(new Direction[0]);
        this.cached = true;
    }

    public void distributionTick() {
        if (!this.cached) {
            this.cacheConnections();
        }
        Duct<?, ?> duct = this.getDuct();
        if (duct == null || this.distArray.length == 0) {
            return;
        }

        this.distIndex = (this.distIndex + 1) % this.distArray.length;
        long budget = this.getTransferRate();
        for (int i = 0; i < this.distArray.length && budget > 0; i++) {
            budget -= this.tickDir(this.distArray[(this.distIndex + i) % this.distArray.length], budget);
        }
    }

    private long tickDir(Direction dir, long budget) {
        FluidGridStorage gridStorage = this.grid.getStorage();
        BlockPos neighbor = this.pos.relative(dir);
        if (gridStorage.isEmpty() || !this.getLevel().isLoaded(neighbor)) {
            return 0;
        }
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(this.getLevel(), neighbor, dir.getOpposite());
        if (storage == null || !storage.supportsInsertion()) {
            return 0;
        }

        FluidVariant variant = gridStorage.getVariant();
        try (Transaction transaction = Transaction.openOuter()) {
            long moved = storage.insert(variant, Math.min(gridStorage.getAmount(), budget), transaction);
            gridStorage.extract(variant, moved, transaction);
            transaction.commit();
            return moved;
        }
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putLong(TRANSFER_RATE, this.transferRate);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.transferRate = tag.contains(TRANSFER_RATE) ? tag.getLong(TRANSFER_RATE) : -1;
    }
}
