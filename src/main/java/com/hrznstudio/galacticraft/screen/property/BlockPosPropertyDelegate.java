/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.screen.property;

import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockPosPropertyDelegate implements PropertyDelegate {
    private final Supplier<BlockPos> supplier;
    private final Consumer<BlockPos> consumer;

    public BlockPosPropertyDelegate(Supplier<BlockPos> supplier, Consumer<BlockPos> consumer) {
        this.supplier = supplier;
        this.consumer = consumer;
    }

    @Override
    public int get(int index) {
        if (supplier.get() == null) return 0;
        if (index == 0) return supplier.get().getX();
        if (index == 1) return supplier.get().getY();
        return supplier.get().getZ();
    }

    @Override
    public void set(int index, int value) {
        if (index == 0) consumer.accept(new BlockPos(value, supplier.get().getY(), supplier.get().getZ()));
        else if (index == 1) consumer.accept(new BlockPos(supplier.get().getX(), value, supplier.get().getZ()));
        else consumer.accept(new BlockPos(supplier.get().getX(), supplier.get().getY(), value));
    }

    @Override
    public int size() {
        return 3;
    }
}
