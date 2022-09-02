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

package dev.galacticraft.mod.machine;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class LongProperty extends SnapshotParticipant<Long> {
    private long value;

    @Contract("_ -> new")
    public static @NotNull LongProperty create(long value) {
        return new LongProperty(value);
    }

    private LongProperty(long value) {
        this.value = value;
    }

    @Contract(pure = true)
    public long getValue() {
        return value;
    }

    public void setValue(long value, TransactionContext context) {
        updateSnapshots(context);
        this.value = value;
    }

    public void setValueUnsafe(long value) {
        this.value = value;
    }

    @Override
    protected Long createSnapshot() {
        return this.value;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.value = snapshot;
    }
}
