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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class LinearSectionData implements OxygenSectionData {
    private BlockPos[] positions;
    private TrackingBitSet[] datum;

    public LinearSectionData(BlockPos[] positions, TrackingBitSet[] datum) {
        this.positions = positions;
        this.datum = datum;
    }

    @Override
    public boolean isEmpty() {
        return this.positions.length == 0;
    }

    @Override
    public boolean has(int index, @NotNull BlockPos pos) {
        for (int i = 0; i < this.positions.length; i++) {
            if (this.positions[i].equals(pos)) return this.datum[i].get(index);
        }
        return false;
    }

    @Override
    public void remove(int index, @NotNull BlockPos pos) {
        for (int i = 0; i < this.positions.length; i++) {
            if (pos.equals(this.positions[i])) {
                this.datum[i].clear(index);
                return;
            }
        }
    }

    @Override
    public void add(int index, @NotNull BlockPos pos) {
        for (int i = 0; i < this.positions.length; i++) {
            if (this.positions[i].equals(pos)) {
                this.datum[i].set(index);
                return;
            }
        }
        throw new IllegalArgumentException("position not allocated!");
    }

    @Override
    public Iterator<BlockPos> get(int index) {
        return new BlockPosIterator(index);
    }

    @Override
    public void removeAll(@NotNull BlockPos pos) {
        for (int i = 0; i < this.positions.length; i++) {
            if (pos.equals(this.positions[i])) {
                this.datum[i].removeAll();
                return;
            }
        }
    }

    @Override
    public void deallocate(@NotNull BlockPos pos) {
        for (int i = 0; i < this.positions.length; i++) {
            if (pos.equals(this.positions[i])) {
                this.drop(i);
                return;
            }
        }
    }

    @Override
    public int serializedSize() {
        int size = VarInt.getByteSize(this.positions.length) + Long.BYTES * this.positions.length;
        for (int i = 0; i < this.positions.length; i++) {
            size += this.datum[i].serializedSize();
        }
        return size;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.positions.length);
        for (int i = 0; i < this.positions.length; i++) {
            buf.writeBlockPos(this.positions[i]);
            this.datum[i].write(buf);
        }
    }

    @Override
    public OxygenSectionData allocateSpaceFor(BlockPos pos) {
        for (BlockPos position : this.positions) {
            if (pos.equals(position)) return this;
        }

        if (this.positions.length < 4) {
            BlockPos[] newPositions = new BlockPos[this.positions.length + 1];
            TrackingBitSet[] newDatum = new TrackingBitSet[this.datum.length + 1];

            System.arraycopy(this.positions, 0, newPositions, 1, this.positions.length);
            System.arraycopy(this.datum, 0, newDatum, 1, this.datum.length);

            this.positions = newPositions;
            this.datum = newDatum;

            this.positions[0] = pos;
            this.datum[0] = new TrackingBitSet();
            return this;
        } else {
            Object2ObjectOpenHashMap<BlockPos, TrackingBitSet> map = new Object2ObjectOpenHashMap<>();
            map.put(pos, new TrackingBitSet());
            for (int i = 0; i < this.positions.length; i++) {
                map.put(this.positions[i], this.datum[i]);
            }
            return new HashMapSectionData(map);
        }
    }

    @Override
    public boolean isAllocated(BlockPos pos) {
        for (BlockPos position : this.positions) {
            if (pos.equals(position)) return true;
        }
        return false;
    }

    @Override
    public void writeTag(CompoundTag tag) {
        long[] pos = new long[this.positions.length];
        ListTag list = new ListTag();
        for (int i = 0; i < this.positions.length; i++) {
            pos[i] = this.positions[i].asLong();
            list.add(new LongArrayTag(this.datum[i].bits()));
        }
        tag.putLongArray("P", pos);
        tag.put("D", list);
    }

    @Override
    public OxygenUpdatePayload.OxygenSectionData updatePayload() {
        return new OxygenUpdatePayload.OxygenSectionData(this.positions, this.datum);
    }

    private void drop(int i) {
        BlockPos[] newPositions = new BlockPos[this.positions.length - 1];
        TrackingBitSet[] newDatum = new TrackingBitSet[this.datum.length - 1];

        System.arraycopy(this.positions, 0, newPositions, 0, i);
        System.arraycopy(this.positions, i + 1, newPositions, i, this.positions.length - i - 1);

        System.arraycopy(this.datum, 0, newDatum, 0, i);
        System.arraycopy(this.datum, i + 1, newDatum, i, this.datum.length - i - 1);

        this.positions = newPositions;
        this.datum = newDatum;
    }

    private class BlockPosIterator implements Iterator<BlockPos> {
        private final int index;
        private int i = 0;

        public BlockPosIterator(int index) {
            this.index = index;
            for (; this.i < LinearSectionData.this.datum.length; this.i++) {
                if (LinearSectionData.this.datum[i].get(this.index)) break;
            }
        }

        @Override
        public boolean hasNext() {
            return this.i < LinearSectionData.this.datum.length;
        }

        @Override
        public BlockPos next() {
            BlockPos pos = LinearSectionData.this.positions[i++];
            while (this.i < LinearSectionData.this.datum.length) {
                if (LinearSectionData.this.datum[i].get(this.index)) break;
                this.i++;
            }
            return pos;
        }
    }
}
