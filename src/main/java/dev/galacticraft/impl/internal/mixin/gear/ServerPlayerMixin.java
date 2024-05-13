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

package dev.galacticraft.impl.internal.mixin.gear;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.inventory.GearInventory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements GearInventoryProvider {
    @Shadow public ServerGamePacketListenerImpl connection;

    private final @Unique SimpleContainer gearInv = this.galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, 4, 5);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, 0, 1, 2, 3);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, 6, 7, 8, 9, 10, 11);

    @Unique
    private SimpleContainer galacticraft_createGearInventory() {
        SimpleContainer inv = new GearInventory();
        inv.addListener((inventory) -> {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(((ServerPlayer) (Object) this).getId());
            buf.writeInt(inventory.getContainerSize());
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                buf.writeItem(inventory.getItem(i));
            }

            if (this.connection != null) {
                Collection<ServerPlayer> tracking = PlayerLookup.tracking(((ServerPlayer) (Object) this));
                //noinspection SuspiciousMethodCalls
                if (!tracking.contains(this)) {
                    ServerPlayNetworking.send(((ServerPlayer) (Object) this), Constant.id("gear_inv_sync"), buf);
                }
                for (ServerPlayer player : tracking) {
                    ServerPlayNetworking.send(player, Constant.id("gear_inv_sync"), PacketByteBufs.copy(buf));
                }
            }
        });
        return inv;
    }

    @Override
    public SimpleContainer galacticraft$getGearInv() {
        return this.gearInv;
    }

    @Override
    public Container galacticraft$getOxygenTanks() {
        return this.tankInv;
    }

    @Override
    public Container galacticraft$getThermalArmor() {
        return this.thermalArmorInv;
    }

    @Override
    public Container galacticraft$getAccessories() {
        return this.accessoryInv;
    }

    @Override
    public void galacticraft$writeGearToNbt(CompoundTag tag) {
        tag.put(Constant.Nbt.GEAR_INV, this.galacticraft$getGearInv().createTag());
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
        this.galacticraft$getGearInv().fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND));
    }
}
