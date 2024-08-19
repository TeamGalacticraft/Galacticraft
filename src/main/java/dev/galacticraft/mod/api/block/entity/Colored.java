/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.api.block.entity;

import dev.galacticraft.mod.Constant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;

import java.util.Objects;

public interface Colored {
    DyeColor getColor();

    void setColor(DyeColor color);

    default boolean isColorCompatible(Colored colored) {
        return this.getColor() == colored.getColor() || this.getColor() == DyeColor.WHITE || colored.getColor() == DyeColor.WHITE;
    }

    default void writeColorNbt(CompoundTag nbt) {
        nbt.putByte(Constant.Nbt.COLOR, (byte) Objects.requireNonNullElse(this.getColor(), DyeColor.WHITE).ordinal());
    }

    default void readColorNbt(CompoundTag nbt) {
        this.setColor(DyeColor.values()[nbt.getByte(Constant.Nbt.COLOR)]);
    }
}
