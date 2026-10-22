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

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

/**
 * The energy storage a wire exposes to the blocks around it: the buffer of its grid,
 * limited to the transfer rate of the wire per tick.
 */
public class DuctEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    private final EnergyDuct duct;
    private long tick = Long.MIN_VALUE;
    private long received;

    public DuctEnergyStorage(EnergyDuct duct) {
        this.duct = duct;
    }

    private long remainingThisTick() {
        long now = this.duct.getHostLevel().getGameTime();
        if (now != this.tick) {
            this.tick = now;
            this.received = 0;
        }
        return Math.max(0, this.duct.getTransferRate() - this.received);
    }

    /**
     * {@return the grid this storage belongs to, or {@code null} if the wire isn't in one}
     */
    public @Nullable EnergyGrid getGrid() {
        return this.duct.getGrid();
    }

    @Override
    public boolean supportsInsertion() {
        return this.duct.getTransferRate() > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        EnergyGrid grid = this.duct.getGrid();
        if (grid == null) {
            return 0;
        }

        long allowed = Math.min(maxAmount, this.remainingThisTick());
        if (allowed <= 0) {
            return 0;
        }
        long inserted = grid.getStorage().insert(allowed, transaction);
        if (inserted > 0) {
            this.updateSnapshots(transaction);
            this.received += inserted;
        }
        return inserted;
    }

    @Override
    public boolean supportsExtraction() {
        return false; // The grid pushes energy out itself
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long getAmount() {
        EnergyGrid grid = this.duct.getGrid();
        return grid == null ? 0 : grid.getStorage().getEnergy();
    }

    @Override
    public long getCapacity() {
        EnergyGrid grid = this.duct.getGrid();
        return grid == null ? 0 : grid.getStorage().getCapacity();
    }

    @Override
    protected Long createSnapshot() {
        return this.received;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.received = snapshot;
    }
}
