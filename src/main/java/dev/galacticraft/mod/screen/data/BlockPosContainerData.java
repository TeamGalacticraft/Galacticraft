/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.screen.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record BlockPosContainerData(Supplier<@NotNull BlockPos> supplier, Consumer<@NotNull BlockPos> consumer) implements ContainerData {

    @Override
    public int get(int index) {
        BlockPos pos = supplier.get();
        if (index == 0) return pos.getX();
        if (index == 1) return pos.getY();
        return pos.getZ();
    }

    @Override
    public void set(int index, int value) {
        BlockPos pos = supplier.get();
        if (index == 0) {
            consumer.accept(new BlockPos(value, pos.getY(), pos.getZ()));
        } else if (index == 1) {
            consumer.accept(pos.atY(value));
        } else {
            consumer.accept(new BlockPos(pos.getX(), pos.getY(), value));
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}