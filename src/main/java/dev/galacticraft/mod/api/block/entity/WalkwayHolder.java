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

package dev.galacticraft.mod.api.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * A block entity of a pipe or wire that can have a walkway attached to one of its sides.
 * The walkway is stored here rather than in the block state, so it doesn't add block states to every pipe and wire.
 */
public interface WalkwayHolder {
    String WALKWAY_KEY = "walkway";

    /**
     * {@return the side the walkway is on, or {@code null} if there is no walkway}
     */
    @Nullable Direction getWalkway();

    /**
     * Sets the side the walkway is on, or removes the walkway with {@code null}, and sends the change to clients.
     */
    void setWalkway(@Nullable Direction walkway);

    default boolean hasWalkway() {
        return this.getWalkway() != null;
    }

    default void writeWalkwayNbt(CompoundTag nbt) {
        Direction walkway = this.getWalkway();
        if (walkway != null) {
            nbt.putString(WALKWAY_KEY, walkway.getSerializedName());
        }
    }

    /**
     * {@return the side of the walkway in the given NBT, or {@code null} if there is none}
     */
    static @Nullable Direction readWalkwayNbt(CompoundTag nbt) {
        return nbt.contains(WALKWAY_KEY) ? Direction.byName(nbt.getString(WALKWAY_KEY)) : null;
    }
}
