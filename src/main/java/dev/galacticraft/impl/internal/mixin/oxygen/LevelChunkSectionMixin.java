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

import com.google.common.collect.Iterators;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements ChunkSectionOxygenAccessor {
    private @Unique @NotNull BlockPos @Nullable [] providers = null;

    @Override
    public Iterator<BlockPos> galacticraft$getProviders() {
        return this.providers != null ? Iterators.forArray(providers) : ObjectIterators.emptyIterator();
    }

    @Override
    public boolean galacticraft$hasProvider(BlockPos pos) {
        if (this.providers == null) return false;
        for (BlockPos provider : this.providers) {
            if (pos.equals(provider)) return true;
        }
        return false;
    }

    @Override
    public void galacticraft$addProvider(BlockPos pos) {
        if (this.providers == null) {
            this.providers = new BlockPos[]{pos};
        } else if (!this.galacticraft$hasProvider(pos)) {
            BlockPos[] providers = new BlockPos[this.providers.length + 1];
            System.arraycopy(this.providers, 0, providers, 0, this.providers.length);
            providers[this.providers.length] = pos;
            this.providers = providers;
        }
    }

    @Override
    public void galacticraft$removeProvider(BlockPos pos) {
        if (this.providers == null) return;
        if (this.providers.length == 1) {
            if (this.providers[0].equals(pos)) {
                this.providers = null;
                return;
            }
        }

        for (int i = 0; i < this.providers.length; i++) {
            BlockPos provider = this.providers[i];
            if (pos.equals(provider)) {
                BlockPos[] providers = new BlockPos[this.providers.length - 1];
                System.arraycopy(this.providers, 0, providers, 0, i);
                System.arraycopy(this.providers, i + 1, providers, i, providers.length - i - 1);
                this.providers = providers;
                break;
            }
        }
    }

    @Override
    public boolean galacticraft$isEmpty() {
        return this.providers == null;
    }

    @Override
    public void galacticraft$writeTag(CompoundTag apiTag) {
        if (this.providers != null) {
            long[] serialized = new long[this.providers.length];
            for (int i = 0; i < this.providers.length; i++) {
                serialized[i] = this.providers[i].asLong();
            }
            apiTag.putLongArray(Constant.Nbt.SRC, serialized);
        }
    }

    @Override
    public void galacticraft$readTag(CompoundTag apiTag) {
        long[] serialized = apiTag.getLongArray(Constant.Nbt.SRC);
        if (serialized.length > 0) {
            this.providers = new BlockPos[serialized.length];
            for (int i = 0; i < this.providers.length; i++) {
                this.providers[i] = BlockPos.of(serialized[i]);
            }
        } else {
            this.providers = null;
        }
    }

    @Override
    public OxygenUpdatePayload.OxygenSectionData galacticraft$updatePayload() {
        return new OxygenUpdatePayload.OxygenSectionData(this.providers != null ? this.providers : new BlockPos[0]);
    }

    @Override
    public void galacticraft$loadData(OxygenUpdatePayload.OxygenSectionData data) {
        this.providers = data.positions(); //todo: does this fail c/s split?
    }

    @WrapMethod(method = "getSerializedSize")
    private int increaseChunkPacketSize(Operation<Integer> original) {
        if (this.providers != null) {
            return original.call() + VarInt.getByteSize(this.providers.length) + Long.BYTES * this.providers.length;
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
        if (this.providers != null) {
            buf.writeVarInt(this.providers.length);
            for (BlockPos provider : this.providers) {
                buf.writeLong(provider.asLong());
            }
        } else {
            buf.writeVarInt(0);
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void galacticraft_fromPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        int size = buf.readVarInt();
        if (size == 0) {
            this.providers = null;
        } else {
            this.providers = new BlockPos[size];
            for (int i = 0; i < size; i++) {
                this.providers[i] = BlockPos.of(buf.readLong());
            }
        }
    }
}
