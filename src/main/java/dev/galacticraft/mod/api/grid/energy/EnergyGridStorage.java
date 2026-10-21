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

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.nbt.CompoundTag;

/**
 * The shared energy buffer of an {@link EnergyGrid}.
 * Its capacity grows with the energy that flows out of the grid: about four ticks of the average output, never less than the base capacity.
 * Ported from ThermalDynamics' {@code EnergyGridStorage}.
 */
public class EnergyGridStorage extends SnapshotParticipant<Long> {
    private static final int SAMPLES = 40;
    private static final String ENERGY = "Energy";
    private static final String CAPACITY = "Capacity";
    private static final String AVERAGE_OUT = "AverageOut";

    private final long[] samplesOut = new long[SAMPLES];
    private long baseCapacity;
    private long capacity;
    private long energy;
    private long rollingOut;
    private long averageOut;
    private int sample;

    public EnergyGridStorage(long baseCapacity) {
        this.baseCapacity = baseCapacity;
        this.capacity = baseCapacity;
    }

    /**
     * Called before the grid distributes its energy.
     */
    public void tick() {
        this.samplesOut[this.sample] = this.energy;
    }

    /**
     * Called after the grid distributed its energy.
     */
    public void postTick() {
        this.samplesOut[this.sample] = Math.max(0, this.samplesOut[this.sample] - this.energy);
        this.rollingOut += this.samplesOut[this.sample];
        this.averageOut = this.rollingOut / SAMPLES;
        if (++this.sample >= SAMPLES) {
            this.sample = 0;
            this.updateCapacity();
        }
        this.rollingOut -= this.samplesOut[this.sample];
    }

    private void updateCapacity() {
        this.capacity = Math.max(this.baseCapacity, 4 * this.averageOut);
    }

    public long insert(long maxAmount, TransactionContext transaction) {
        long inserted = Math.max(0, Math.min(this.capacity - this.energy, maxAmount));
        if (inserted > 0) {
            this.updateSnapshots(transaction);
            this.energy += inserted;
        }
        return inserted;
    }

    public long extract(long maxAmount, TransactionContext transaction) {
        long extracted = Math.max(0, Math.min(this.energy, maxAmount));
        if (extracted > 0) {
            this.updateSnapshots(transaction);
            this.energy -= extracted;
        }
        return extracted;
    }

    public long getEnergy() {
        return this.energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = Math.max(this.baseCapacity, capacity);
    }

    public long getBaseCapacity() {
        return this.baseCapacity;
    }

    public void setBaseCapacity(long baseCapacity) {
        this.baseCapacity = baseCapacity;
        this.capacity = Math.max(baseCapacity, 4 * this.averageOut);
    }

    public void save(CompoundTag tag) {
        tag.putLong(ENERGY, this.energy);
        tag.putLong(CAPACITY, this.capacity);
        tag.putLong(AVERAGE_OUT, this.averageOut);
    }

    public void load(CompoundTag tag) {
        this.energy = tag.getLong(ENERGY);
        this.averageOut = tag.getLong(AVERAGE_OUT);
        this.capacity = Math.max(this.baseCapacity, tag.getLong(CAPACITY));
    }

    @Override
    protected Long createSnapshot() {
        return this.energy;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.energy = snapshot;
    }
}
