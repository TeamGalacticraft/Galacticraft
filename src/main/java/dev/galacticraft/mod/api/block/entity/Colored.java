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

package dev.galacticraft.mod.api.block.entity;

import dev.galacticraft.mod.Constant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;

public interface Colored {
    PipeColor getColor();

    void setColor(PipeColor color);

    default void setColor(DyeColor dye) {
        this.setColor(PipeColor.fromDye(dye));
    }

    default boolean canConnectTo(Colored other) {
        return this.getColor() == other.getColor() || this.getColor() == PipeColor.CLEAR || other.getColor() == PipeColor.CLEAR;
    }

    default boolean dyeCanBeApplied(DyeColor dye) {
        return this.getColor() != PipeColor.fromDye(dye);
    }

    default void writeColorNbt(CompoundTag nbt) {
        nbt.putByte(Constant.Nbt.COLOR, (byte) this.getColor().ordinal());
    }

    default void readColorNbt(CompoundTag nbt) {
        if (nbt.contains(Constant.Nbt.COLOR)) {
            this.setColor(PipeColor.values()[nbt.getByte(Constant.Nbt.COLOR)]);
        } else {
            this.setColor(PipeColor.CLEAR);
        }
    }
}
