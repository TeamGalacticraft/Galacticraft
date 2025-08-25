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
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class HashMapSectionData implements OxygenSectionData{
    private final Object2ObjectOpenHashMap<BlockPos, TrackingBitSet> map;

    public HashMapSectionData(int expected) {
        this.map = new Object2ObjectOpenHashMap<>(expected, Hash.VERY_FAST_LOAD_FACTOR);
    }

    public HashMapSectionData(Object2ObjectOpenHashMap<BlockPos, TrackingBitSet> map) {
        this.map = map;
    }

    @Override
    public boolean isEmpty() {
        for (TrackingBitSet value : this.map.values()) {
            if (value.count() > 0) return false;
        }
        return true;
    }

    @Override
    public boolean has(int index, @NotNull BlockPos pos) {
        TrackingBitSet set = this.map.get(pos);
        return set != null && set.get(index);
    }

    @Override
    public void remove(int index, @NotNull BlockPos pos) {
        TrackingBitSet set = this.map.get(pos);
        if (set != null) {
            set.clear(index);
        }
    }

    @Override
    public void add(int index, @NotNull BlockPos pos) {
        this.map.get(pos).set(index);
    }

    @Override
    public Iterator<BlockPos> get(int index) {
        return new Iter(index);
    }

    @Override
    public void removeAll(@NotNull BlockPos pos) {
        TrackingBitSet bits = this.map.get(pos);
        if (bits != null) {
            bits.removeAll();
        }
    }

    @Override
    public void deallocate(@NotNull BlockPos pos) {
        this.map.remove(pos);
    }

    @Override
    public int serializedSize() {
        int size = VarInt.getByteSize(this.map.size()) + Long.BYTES * this.map.size();
        for (TrackingBitSet value : this.map.values()) {
            size += value.serializedSize();
        }
        return size;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.map.size());
        for (Map.Entry<BlockPos, TrackingBitSet> e : this.map.entrySet()) {
            buf.writeBlockPos(e.getKey());
            e.getValue().write(buf);
        }
    }

    @Override
    public OxygenSectionData allocateSpaceFor(BlockPos pos) {
        this.map.putIfAbsent(pos, new TrackingBitSet());
        return this;
    }

    @Override
    public boolean isAllocated(BlockPos pos) {
        return this.map.containsKey(pos);
    }

    @Override
    public void writeTag(CompoundTag tag) {
        long[] pos = new long[this.map.size()];
        ListTag list = new ListTag();
        int i = 0;
        for (Map.Entry<BlockPos, TrackingBitSet> e : this.map.entrySet()) {
            pos[i++] = e.getKey().asLong();
            list.add(new LongArrayTag(e.getValue().bits()));
        }
        tag.putLongArray("P", pos);
        tag.put("D", list);
    }

    @Override
    public OxygenUpdatePayload.OxygenSectionData updatePayload() {
        BlockPos[] positions = new BlockPos[this.map.size()];
        TrackingBitSet[] values = new TrackingBitSet[this.map.size()];
        int i = 0;
        for (Map.Entry<BlockPos, TrackingBitSet> e : this.map.entrySet()) {
            positions[i] = e.getKey();
            values[i] = e.getValue();
        }
        return new OxygenUpdatePayload.OxygenSectionData(positions, values);
    }

    private final class Iter implements Iterator<BlockPos> {
        private final ObjectIterator<Object2ObjectMap.Entry<BlockPos, TrackingBitSet>> iterator = HashMapSectionData.this.map.object2ObjectEntrySet().fastIterator();
        private final int index;
        private BlockPos next = null;

        private Iter(int index) {
            this.index = index;
            while (this.iterator.hasNext()) {
                Object2ObjectMap.Entry<BlockPos, TrackingBitSet> e = this.iterator.next();
                if (e.getValue().get(this.index)) {
                    this.next = e.getKey();
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public BlockPos next() {
            BlockPos result = this.next;
            this.next = null;

            while (this.iterator.hasNext()) {
                Object2ObjectMap.Entry<BlockPos, TrackingBitSet> e = this.iterator.next();
                if (e.getValue().get(this.index)) {
                    this.next = e.getKey();
                    break;
                }
            }

            return result;
        }
    }
}
