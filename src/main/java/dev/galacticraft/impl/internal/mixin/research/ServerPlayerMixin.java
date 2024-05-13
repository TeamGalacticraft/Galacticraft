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

package dev.galacticraft.impl.internal.mixin.research;

import dev.galacticraft.api.accessor.ServerResearchAccessor;
import dev.galacticraft.mod.Constant;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements ServerResearchAccessor {
    @Shadow public abstract ServerLevel serverLevel();

    @Shadow public abstract boolean isCreative();

    private final @Unique Set<ResourceLocation> unlockedRecipes = new HashSet<>();

    @Override
    public boolean galacticraft$isUnlocked(ResourceLocation id) {
        if (this.isCreative()) return true;
        return this.unlockedRecipes.contains(id);
    }

    @Override
    public void galacticraft$unlockRocketPartRecipes(ResourceLocation... ids) {
        Collections.addAll(this.unlockedRecipes, ids);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(ids.length);
        buf.writeBoolean(true);
        for (ResourceLocation id : ids) {
            buf.writeUtf(id.toString());
        }
        ServerPlayNetworking.send((ServerPlayer) (Object)this,
                Constant.id("research_update"),
                buf);
    }

    @Override
    public void galacticraft$unlearnRocketPartRecipes(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            this.unlockedRecipes.remove(id);
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(ids.length);
        buf.writeBoolean(false);
        for (ResourceLocation id : ids) {
            buf.writeUtf(id.toString());
        }
        ServerPlayNetworking.send((ServerPlayer) (Object)this,
                Constant.id("research_update"),
                buf);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void galacticraft_readCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        this.unlockedRecipes.clear();
        ListTag list = nbt.getList("gcResearch", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            this.unlockedRecipes.add(new ResourceLocation(list.getString(i)));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void galacticraft_writeCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (ResourceLocation id : this.unlockedRecipes) {
            list.add(StringTag.valueOf(id.toString()));
        }
        nbt.put("gcResearch", list);
    }
}
