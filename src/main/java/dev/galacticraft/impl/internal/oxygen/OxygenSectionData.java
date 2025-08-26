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

package dev.galacticraft.impl.internal.oxygen;

import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface OxygenSectionData {
    boolean isEmpty();

    boolean has(int index, @NotNull BlockPos pos);
    void remove(int index, @NotNull BlockPos pos);
    void add(int index, @NotNull BlockPos pos);
    Iterator<BlockPos> get(int index);

    void removeAll(@NotNull BlockPos pos);
    void deallocate(@NotNull BlockPos pos);

    int serializedSize();
    void write(FriendlyByteBuf buf);
    OxygenSectionData allocateSpaceFor(BlockPos pos);
    boolean isAllocated(BlockPos pos);

    static OxygenSectionData read(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        switch (count) {
            case 0 -> {
                return null;
            }
            case 1 -> {
                return new SingleSectionData(buf.readBlockPos(), TrackingBitSet.read(buf));
            }
            case 2, 3, 4 -> {
                BlockPos[] positions = new BlockPos[count];
                TrackingBitSet[] bits = new TrackingBitSet[count];
                for (int i = 0; i < count; i++) {
                    positions[i] = buf.readBlockPos();
                    bits[i] = TrackingBitSet.read(buf);
                }
                return new LinearSectionData(positions, bits);
            }
            default -> {
                Object2ObjectOpenHashMap<BlockPos, TrackingBitSet> map = new Object2ObjectOpenHashMap<>(count, Hash.VERY_FAST_LOAD_FACTOR);
                for (int i = 0; i < count; i++) {
                    map.put(buf.readBlockPos(), TrackingBitSet.read(buf));
                }
                return new HashMapSectionData(map);
            }
        }
    }

    void writeTag(CompoundTag tag);

    OxygenUpdatePayload.OxygenSectionData updatePayload();
}
