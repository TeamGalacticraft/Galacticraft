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

package dev.galacticraft.mod.storage;

import com.google.common.collect.Iterators;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class SingleTypeStorage<T, V extends TransferVariant<T>> extends SnapshotParticipant<Long> implements Storage<V>, StorageView<V> {
    private final V resource;
    private final long capacity;
    private final V blankResource;
    private long amount;

    public SingleTypeStorage(V resource, long capacity, V blankResource, long amount) {
        this.resource = resource;
        this.capacity = capacity;
        this.blankResource = blankResource;
        this.amount = amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public long insert(@NotNull V resource, long maxAmount, TransactionContext transaction) {
        if (resource.equals(this.resource)) {
            maxAmount = Math.min(maxAmount, this.capacity - this.amount);
            this.amount += maxAmount;
            return maxAmount;
        }
        return 0;
    }

    @Override
    public long extract(@NotNull V resource, long maxAmount, TransactionContext transaction) {
        if (resource.equals(this.resource)) {
            maxAmount = Math.min(maxAmount, this.amount);
            this.amount -= maxAmount;
            return maxAmount;
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return resource.isBlank();
    }

    @Override
    public V getResource() {
        return this.resource;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public long getCapacity() {
        return this.capacity;
    }

    @Override
    public Iterator<StorageView<V>> iterator() {
        return Iterators.singletonIterator(this);
    }

    @Override
    public @Nullable StorageView<V> exactView(TransactionContext transaction, V resource) {
        return this;
    }

    @Override
    protected Long createSnapshot() {
        return this.amount;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.amount = snapshot;
    }
}
