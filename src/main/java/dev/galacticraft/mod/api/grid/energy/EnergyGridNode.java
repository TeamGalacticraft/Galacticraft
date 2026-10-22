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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.grid.Duct;
import dev.galacticraft.mod.api.grid.GridNode;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import team.reborn.energy.api.EnergyStorage;

/**
 * A node of an {@link EnergyGrid}. Nodes next to blocks that accept energy push the energy of the grid into them.
 * Ported from ThermalDynamics' {@code EnergyGridNode}.
 */
public class EnergyGridNode extends GridNode<EnergyGrid> {
    private static final String TRANSFER_RATE = "TransferRate";
    private static final String NODE_CAPACITY = "NodeCapacity";

    protected Direction[] distArray = new Direction[0];
    protected int distIndex = 0;
    private long transferRate = -1;
    private long nodeCapacity = -1;

    protected EnergyGridNode(EnergyGrid grid) {
        super(grid);
    }

    /**
     * Reads the transfer rate and buffer size from the duct at this node, if its chunk is loaded.
     */
    void readDuct() {
        if (this.getDuct() instanceof EnergyDuct duct) {
            this.transferRate = duct.getTransferRate();
            this.nodeCapacity = duct.getNodeCapacity();
        }
    }

    public long getTransferRate() {
        return this.transferRate < 0 ? EnergyGrid.DEFAULT_TRANSFER_RATE : this.transferRate;
    }

    public long getNodeCapacity() {
        return this.nodeCapacity < 0 ? EnergyGrid.DEFAULT_NODE_CAPACITY : this.nodeCapacity;
    }

    protected void cacheConnections() {
        for (Direction dir : Constant.Misc.DIRECTIONS) {
            BlockPos neighbor = this.pos.relative(dir);
            if (this.getLevel().isLoaded(neighbor) && this.grid.canConnectOnSide(this.pos, dir)) {
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
        long energy = this.grid.getStorage().getEnergy();
        BlockPos neighbor = this.pos.relative(dir);
        if (energy <= 0 || !this.getLevel().isLoaded(neighbor)) {
            return 0;
        }
        EnergyStorage storage = EnergyStorage.SIDED.find(this.getLevel(), neighbor, dir.getOpposite());
        if (storage == null || !storage.supportsInsertion()) {
            return 0;
        }

        long offer = Math.min(energy, budget);
        if (storage instanceof DuctEnergyStorage duct) {
            // Another grid, like one of wires of another tier: only even out how full both grids are
            EnergyGrid other = duct.getGrid();
            if (other == null || other == this.grid) {
                return 0;
            }
            offer = Math.min(offer, this.grid.amountToBalance(other));
            if (offer <= 0) {
                return 0;
            }
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long moved = storage.insert(offer, transaction);
            this.grid.getStorage().extract(moved, transaction);
            transaction.commit();
            return moved;
        }
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putLong(TRANSFER_RATE, this.transferRate);
        tag.putLong(NODE_CAPACITY, this.nodeCapacity);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.transferRate = tag.contains(TRANSFER_RATE) ? tag.getLong(TRANSFER_RATE) : -1;
        this.nodeCapacity = tag.contains(NODE_CAPACITY) ? tag.getLong(NODE_CAPACITY) : -1;
    }
}
