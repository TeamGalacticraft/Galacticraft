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

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements ChunkSectionOxygenAccessor {
    private @Unique @Nullable ArrayList<BlockPos> providers = null;

    @Override
    public boolean galacticraft$hasProvider(BlockPos pos) {
        if (this.providers == null) return false;
        return this.providers.contains(pos);
    }

    @Override
    public boolean galacticraft$addProvider(BlockPos pos) {
        if (this.providers == null) {
            this.providers = Lists.newArrayList(pos);
            return true;
        } else if (!this.galacticraft$hasProvider(pos)) {
            this.providers.add(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean galacticraft$removeProvider(BlockPos pos) {
        if (this.providers == null) return false;
        if (this.providers.size() == 1) {
            if (this.providers.getFirst().equals(pos)) {
                this.providers = null;
                return true;
            }
            return false;
        }

        return this.providers.remove(pos);
    }

    @Override
    public boolean galacticraft$isEmpty() {
        return this.providers == null;
    }

    @Override
    public void galacticraft$writeTag(CompoundTag apiTag) {
        if (this.providers != null) {
            long[] serialized = new long[this.providers.size()];
            for (int i = 0; i < this.providers.size(); i++) {
                serialized[i] = this.providers.get(i).asLong();
            }
            apiTag.putLongArray(Constant.Nbt.SRC, serialized);
        }
    }

    @Override
    public void galacticraft$readTag(CompoundTag apiTag) {
        long[] serialized = apiTag.getLongArray(Constant.Nbt.SRC);
        if (serialized.length > 0) {
            this.providers = new ArrayList<>(serialized.length);
            for (long l : serialized) {
                this.providers.add(BlockPos.of(l));
            }
        } else {
            this.providers = null;
        }
    }

    @Override
    public OxygenUpdatePayload.OxygenSectionData galacticraft$updatePayload() {
        return new OxygenUpdatePayload.OxygenSectionData(this.providers != null ? this.providers.toArray(BlockPos[]::new) : new BlockPos[0]);
    }

    @Override
    public void galacticraft$loadData(OxygenUpdatePayload.OxygenSectionData data) {
        BlockPos[] positions = data.positions();
        if (positions.length == 0) {
            this.providers = null;
        } else {
            this.providers = Lists.newArrayList(positions);
        }
    }

    @Override
    public ArrayList<BlockPos> galacticraft$getRawProviders() {
        return this.providers;
    }

    @ModifyReturnValue(method = "getSerializedSize", at = @At("RETURN"))
    private int increaseChunkPacketSize(int original) {
        if (this.providers != null) {
            return original + VarInt.getByteSize(this.providers.size()) + Long.BYTES * this.providers.size();
        } else {
            return original + VarInt.getByteSize(0);
        }
    }

    @ModifyReturnValue(method = "hasOnlyAir()Z", at = @At("RETURN"))
    private boolean verifyOxygenEmpty(boolean original) {
        return original && this.galacticraft$isEmpty();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeOxygenDataToPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        if (this.providers != null) {
            buf.writeVarInt(this.providers.size());
            for (BlockPos provider : this.providers) {
                buf.writeLong(provider.asLong());
            }
        } else {
            buf.writeVarInt(0);
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void readOxygenFromPacket(FriendlyByteBuf buf, CallbackInfo ci) {
        int size = buf.readVarInt();
        if (size == 0) {
            this.providers = null;
        } else {
            this.providers = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                this.providers.add(BlockPos.of(buf.readLong()));
            }
        }
    }
}
