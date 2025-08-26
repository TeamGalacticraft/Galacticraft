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

package dev.galacticraft.impl.internal.mixin.oxygen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.internal.oxygen.*;
import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements ChunkSectionOxygenAccessor {
    private @Unique @Nullable OxygenSectionData data = null;

    @Override
    public Iterator<BlockPos> galacticraft$get(int x, int y, int z) {
        return this.data != null ? this.data.get(index(x, y, z)) : ObjectIterators.emptyIterator();
    }

    @Override
    public boolean galacticraft$has(int x, int y, int z, BlockPos pos) {
        return this.data != null && this.data.has(index(x, y, z), pos);
    }

    @Override
    public void galacticraft$ensureSpaceFor(BlockPos pos) {
        if (this.data == null) {
            this.data = new SingleSectionData(pos, new TrackingBitSet());
        } else {
            this.data = this.data.allocateSpaceFor(pos);
        }
    }

    @Override
    public void galacticraft$add(int x, int y, int z, BlockPos pos) {
        assert this.data != null && this.data.isAllocated(pos);

        this.data.add(index(x, y, z), pos);
    }

    @Override
    public void galacticraft$removeAll(BlockPos pos) {
        if (this.data != null) this.data.removeAll(pos);
    }

    @Override
    public void galacticraft$deallocate(BlockPos pos) {
        if (this.data != null) this.data.deallocate(pos);
    }

    @Override
    public void galacticraft$remove(int x, int y, int z, BlockPos pos) {
        if (this.data != null) this.data.remove(index(x, y, z), pos);
    }

    @Override
    public boolean galacticraft$isEmpty() {
        return this.data == null || this.data.isEmpty();
    }

    @Override
    public void galacticraft$writeTag(CompoundTag apiTag) {
        if (this.data != null && !this.data.isEmpty()) {
            this.data.writeTag(apiTag);
        }
    }

    @Override
    public void galacticraft$readTag(CompoundTag apiTag) {
        long[] ps = apiTag.getLongArray("P");
        ListTag d = apiTag.getList("D", Tag.TAG_LONG_ARRAY);
        assert ps.length == d.size();

        switch (ps.length) {
            case 0 -> this.data = null;
            case 1 -> this.data = new SingleSectionData(BlockPos.of(ps[0]), new TrackingBitSet(d.getLongArray(0)));
            case 2, 3, 4 -> {
                BlockPos[] pos = new BlockPos[ps.length];
                TrackingBitSet[] bits = new TrackingBitSet[ps.length];
                for (int i = 0; i < ps.length; i++) {
                    pos[i] = BlockPos.of(ps[i]);
                    bits[i] = new TrackingBitSet(d.getLongArray(i));
                }
                this.data = new LinearSectionData(pos, bits);
            }
            default -> {
                Object2ObjectOpenHashMap<BlockPos, TrackingBitSet> map = new Object2ObjectOpenHashMap<>(ps.length, Hash.VERY_FAST_LOAD_FACTOR);
                for (int i = 0; i < ps.length; i++) {
                    map.put(BlockPos.of(ps[i]), new TrackingBitSet(d.getLongArray(i)));
                }
                this.data = new HashMapSectionData(map);
            }
        }
    }

    @Override
    public OxygenUpdatePayload.OxygenSectionData galacticraft$updatePayload() {
        if (this.data == null) return new OxygenUpdatePayload.OxygenSectionData(new BlockPos[0], new TrackingBitSet[0]);
        return this.data.updatePayload();
    }

    @Override
    public void galacticraft$loadData(OxygenUpdatePayload.OxygenSectionData data) {
        BlockPos[] positions = data.positions();
        TrackingBitSet[] bits = data.data();
        switch (bits.length) {
            case 0 -> this.data = null;
            case 1 -> this.data = new SingleSectionData(positions[0], bits[0]);
            case 2, 3, 4 -> this.data = new LinearSectionData(positions, bits);
            default -> this.data = new HashMapSectionData(new Object2ObjectOpenHashMap<>(positions, bits, Hash.VERY_FAST_LOAD_FACTOR));
        }
    }

    @WrapMethod(method = "getSerializedSize")
    private int increaseChunkPacketSize(Operation<Integer> original) {
        if (this.data != null) {
            return original.call() + this.data.serializedSize();
        } else {
            return original.call() + VarInt.getByteSize(0);
        }
    }

    @WrapMethod(method = "hasOnlyAir()Z")
    private boolean verifyOxygenEmpty(Operation<Boolean> original) {
        return original.call() && this.galacticraft$isEmpty();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeOxygenDataToPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        if (this.data != null) {
            this.data.write(buf);
        } else {
            buf.writeVarInt(0);
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void galacticraft_fromPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        this.data = OxygenSectionData.read(buf);
    }

    @Unique
    private static int index(int x, int y, int z) {
        return x + (y << 4) + (z << 8);
    }
}
