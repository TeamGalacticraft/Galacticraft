/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.screen.sync;

import dev.galacticraft.machinelib.api.menu.sync.MenuSyncHandler;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class DoubleMenuSyncHandler implements MenuSyncHandler {
    private final DoubleSupplier supplier;
    private final DoubleConsumer consumer;
    private double value;

    public DoubleMenuSyncHandler(DoubleSupplier supplier, DoubleConsumer consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    public boolean needsSyncing() {
        return this.value != this.supplier.getAsDouble();
    }

    @Override
    public void sync(@NotNull FriendlyByteBuf buf) {
        this.value = this.supplier.getAsDouble();
        buf.writeDouble(this.value);
    }

    @Override
    public void read(@NotNull FriendlyByteBuf buf) {
        this.value = buf.readDouble();
        this.consumer.accept(this.value);
    }
}
