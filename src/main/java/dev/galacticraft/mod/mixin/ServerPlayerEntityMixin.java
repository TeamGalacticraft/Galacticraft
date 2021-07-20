/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin;

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements GearInventoryProvider {
    private final @Unique FullFixedItemInv gearInv = createGearInv();

    private FullFixedItemInv createGearInv() {
        FullFixedItemInv inv = new FullFixedItemInv(12);
        inv.setOwnerListener((invView, slot, prev, cur) -> {
            ServerPlayNetworking.send(((ServerPlayerEntity) (Object) this), new Identifier(Constant.MOD_ID, "gear_inv_sync"), new PacketByteBuf(Unpooled.buffer().writeInt(((ServerPlayerEntity) (Object) this).getId()).writeByte(slot)).writeItemStack(cur));
            for (ServerPlayerEntity player : PlayerLookup.tracking(((ServerPlayerEntity) (Object) this))) {
                ServerPlayNetworking.send(player, new Identifier(Constant.MOD_ID, "gear_inv_sync"), new PacketByteBuf(Unpooled.buffer().writeInt(((ServerPlayerEntity) (Object) this).getId()).writeByte(slot)).writeItemStack(cur));
            }
        });
        return inv;
    }

    @Override
    public FullFixedItemInv getGearInv() {
        return this.gearInv;
    }

    @Override
    public NbtCompound writeGearToNbt(NbtCompound tag) {
        tag.put("GearInv", this.getGearInv().toTag());
        return tag;
    }

    @Override
    public void readGearFromNbt(NbtCompound tag) {
        this.getGearInv().fromTag(tag.getCompound("GearInv"));
    }
}
