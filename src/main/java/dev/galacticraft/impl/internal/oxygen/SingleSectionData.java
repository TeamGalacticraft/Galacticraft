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

import com.google.common.collect.Iterators;
import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class SingleSectionData implements OxygenSectionData {
    private @Nullable BlockPos pos;
    private final TrackingBitSet data;

    public SingleSectionData(@Nullable BlockPos pos, TrackingBitSet data) {
        this.pos = pos;
        this.data = data;
    }

    @Override
    public boolean isEmpty() {
        return this.data.count() == 0;
    }

    @Override
    public boolean has(int index, @NotNull BlockPos pos) {
        return pos.equals(this.pos) && this.data.get(index);
    }

    @Override
    public void remove(int index, @NotNull BlockPos pos) {
        if (!pos.equals(this.pos)) return;
        this.data.clear(index);
    }

    @Override
    public void add(int index, @NotNull BlockPos pos) {
        assert this.pos != null && this.pos.equals(pos);
        this.data.set(index);
    }

    @Override
    public Iterator<BlockPos> get(int index) {
        if (this.data.get(index)) return Iterators.singletonIterator(this.pos);
        return ObjectIterators.emptyIterator();
    }

    @Override
    public void removeAll(@NotNull BlockPos pos) {
        if (pos.equals(this.pos)) {
            this.data.removeAll();
        }
    }

    @Override
    public void deallocate(@NotNull BlockPos pos) {
        if (pos.equals(this.pos)) {
            this.data.removeAll();
            this.pos = null;
        }
    }

    @Override
    public int serializedSize() {
        return this.pos == null ? VarInt.getByteSize(0) : (VarInt.getByteSize(1) + this.data.serializedSize());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        if (this.pos == null) {
            buf.writeVarInt(0);
        } else {
            buf.writeVarInt(1);
            buf.writeBlockPos(this.pos);
            this.data.write(buf);
        }
    }

    @Override
    public OxygenSectionData allocateSpaceFor(BlockPos pos) {
        if (this.pos == null || this.pos == pos) {
            this.pos = pos;
            return this;
        } else {
            return new LinearSectionData(new BlockPos[]{pos, this.pos}, new TrackingBitSet[]{new TrackingBitSet(), this.data});
        }
    }

    @Override
    public boolean isAllocated(BlockPos pos) {
        return this.pos == pos;
    }

    @Override
    public void writeTag(CompoundTag tag) {
        if (this.pos != null) {
            tag.putLongArray("P", new long[]{this.pos.asLong()});
            ListTag element = new ListTag();
            element.add(new LongArrayTag(this.data.bits()));
            tag.put("D", element);
        }
    }

    @Override
    public OxygenUpdatePayload.OxygenSectionData updatePayload() {
        if (this.pos == null) return new OxygenUpdatePayload.OxygenSectionData(new BlockPos[0], new TrackingBitSet[0]);
        return new OxygenUpdatePayload.OxygenSectionData(new BlockPos[]{this.pos}, new TrackingBitSet[]{this.data});
    }
}
