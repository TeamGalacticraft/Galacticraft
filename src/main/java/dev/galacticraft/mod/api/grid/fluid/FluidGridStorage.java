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

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

/**
 * The shared tank of a {@link FluidGrid}. It holds one fluid at a time.
 * Its capacity grows with the fluid that flows out of the grid, like {@link dev.galacticraft.mod.api.grid.energy.EnergyGridStorage}.
 * Ported from ThermalDynamics' {@code FluidGridStorage}.
 */
public class FluidGridStorage extends SnapshotParticipant<ResourceAmount<FluidVariant>> {
    private static final int SAMPLES = 40;
    private static final String FLUID = "Fluid";
    private static final String AMOUNT = "Amount";
    private static final String CAPACITY = "Capacity";
    private static final String AVERAGE_OUT = "AverageOut";

    private final long[] samplesOut = new long[SAMPLES];
    private FluidVariant variant = FluidVariant.blank();
    private long amount;
    private long baseCapacity;
    private long capacity;
    private long rollingOut;
    private long averageOut;
    private int sample;

    public FluidGridStorage(long baseCapacity) {
        this.baseCapacity = baseCapacity;
        this.capacity = baseCapacity;
    }

    public void tick() {
        this.samplesOut[this.sample] = this.amount;
    }

    public void postTick() {
        this.samplesOut[this.sample] = Math.max(0, this.samplesOut[this.sample] - this.amount);
        this.rollingOut += this.samplesOut[this.sample];
        this.averageOut = this.rollingOut / SAMPLES;
        if (++this.sample >= SAMPLES) {
            this.sample = 0;
            this.capacity = Math.max(this.baseCapacity, 4 * this.averageOut);
        }
        this.rollingOut -= this.samplesOut[this.sample];
    }

    public boolean canAccept(FluidVariant variant) {
        return !variant.isBlank() && (this.isEmpty() || this.variant.equals(variant));
    }

    public long insert(FluidVariant variant, long maxAmount, TransactionContext transaction) {
        if (!this.canAccept(variant)) {
            return 0;
        }
        long inserted = Math.max(0, Math.min(this.capacity - this.amount, maxAmount));
        if (inserted > 0) {
            this.updateSnapshots(transaction);
            this.variant = variant;
            this.amount += inserted;
        }
        return inserted;
    }

    public long extract(FluidVariant variant, long maxAmount, TransactionContext transaction) {
        if (this.isEmpty() || !this.variant.equals(variant)) {
            return 0;
        }
        long extracted = Math.max(0, Math.min(this.amount, maxAmount));
        if (extracted > 0) {
            this.updateSnapshots(transaction);
            this.amount -= extracted;
            if (this.amount == 0) {
                this.variant = FluidVariant.blank();
            }
        }
        return extracted;
    }

    public boolean isEmpty() {
        return this.amount <= 0 || this.variant.isBlank();
    }

    public FluidVariant getVariant() {
        return this.isEmpty() ? FluidVariant.blank() : this.variant;
    }

    public long getAmount() {
        return this.isEmpty() ? 0 : this.amount;
    }

    public void setFluid(FluidVariant variant, long amount) {
        this.variant = amount > 0 ? variant : FluidVariant.blank();
        this.amount = variant.isBlank() ? 0 : amount;
    }

    public long getCapacity() {
        return this.capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = Math.max(this.baseCapacity, capacity);
    }

    public void setBaseCapacity(long baseCapacity) {
        this.baseCapacity = baseCapacity;
        this.capacity = Math.max(baseCapacity, 4 * this.averageOut);
    }

    public void save(CompoundTag tag, HolderLookup.Provider registries) {
        if (!this.isEmpty()) {
            FluidVariant.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.variant).result()
                    .ifPresent(fluid -> tag.put(FLUID, fluid));
            tag.putLong(AMOUNT, this.amount);
        }
        tag.putLong(CAPACITY, this.capacity);
        tag.putLong(AVERAGE_OUT, this.averageOut);
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        FluidVariant variant = tag.contains(FLUID)
                ? FluidVariant.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(FLUID)).result().orElse(FluidVariant.blank())
                : FluidVariant.blank();
        this.setFluid(variant, tag.getLong(AMOUNT));
        this.averageOut = tag.getLong(AVERAGE_OUT);
        this.capacity = Math.max(this.baseCapacity, tag.getLong(CAPACITY));
    }

    @Override
    protected ResourceAmount<FluidVariant> createSnapshot() {
        return new ResourceAmount<>(this.variant, this.amount);
    }

    @Override
    protected void readSnapshot(ResourceAmount<FluidVariant> snapshot) {
        this.variant = snapshot.resource();
        this.amount = snapshot.amount();
    }
}
