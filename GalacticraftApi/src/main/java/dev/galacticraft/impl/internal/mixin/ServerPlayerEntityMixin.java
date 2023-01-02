/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.accessor.ServerResearchAccessor;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements ServerResearchAccessor, GearInventoryProvider {
    private final @Unique List<ResourceLocation> unlockedResearch = new ArrayList<>();
    private final @Unique Object2BooleanMap<ResourceLocation> changes = new Object2BooleanArrayMap<>();

    private final @Unique SimpleContainer gearInv = this.galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, 4, 5);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, 0, 1, 2, 3);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, 6, 7, 8, 9, 10, 11);

    @Override
    public boolean hasUnlockedResearch(ResourceLocation id) {
        return unlockedResearch.contains(id);
    }

    @Override
    public void unlockResearch(ResourceLocation id, boolean unlocked) {
        if (unlocked) {
            if (!this.unlockedResearch.contains(id)) {
                this.unlockedResearch.add(id);
                this.changes.put(id, true);
            }
        } else {
            if (unlockedResearch.remove(id)) {
                this.changes.put(id, false);
            }
        }
    }

    @Override
    public boolean isResearchDirty() {
        return !this.changes.isEmpty();
    }

    @Override
    public FriendlyByteBuf writeResearchChanges(FriendlyByteBuf buf) {
        buf.writeByte(this.changes.size());

        for (Object2BooleanMap.Entry<ResourceLocation> entry : this.changes.object2BooleanEntrySet()) {
            buf.writeBoolean(entry.getBooleanValue());
            buf.writeUtf(entry.getKey().toString());
        }
        this.changes.clear();
        return buf;
    }

    @Override
    public CompoundTag writeResearchToNbt(CompoundTag nbt) {
        nbt.putInt("size", this.unlockedResearch.size());
        int i = 0;
        for (ResourceLocation id : this.unlockedResearch) {
            nbt.putString("id_" + i, id.toString());
            i++;
        }
        return nbt;
    }

    @Override
    public void readResearchFromNbt(CompoundTag nbt) {
        this.unlockedResearch.clear();
        int size = nbt.getInt("size");
        for (int i = 0; i < size; i++) {
            this.unlockedResearch.add(new ResourceLocation(nbt.getString("id_" + i)));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void galacticraft_readCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        this.readResearchFromNbt(nbt);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void galacticraft_writeCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        this.writeResearchToNbt(nbt);
    }

    @Unique
    private SimpleContainer galacticraft_createGearInventory() {
        SimpleContainer inv = new SimpleContainer(12);
        inv.addListener((inventory) -> {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(((ServerPlayer) (Object) this).getId());
            buf.writeInt(inventory.getContainerSize());
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                buf.writeItem(inventory.getItem(i));
            }

            Collection<ServerPlayer> tracking = PlayerLookup.tracking(((ServerPlayer) (Object) this));
            //noinspection SuspiciousMethodCalls
            if (!tracking.contains(this)) {
                ServerPlayNetworking.send(((ServerPlayer) (Object) this), new ResourceLocation(Constant.MOD_ID, "gear_inv_sync"), buf);
            }
            for (ServerPlayer player : tracking) {
                ServerPlayNetworking.send(player, new ResourceLocation(Constant.MOD_ID, "gear_inv_sync"), PacketByteBufs.copy(buf));
            }
        });
        return inv;
    }

    @Override
    public SimpleContainer getGearInv() {
        return this.gearInv;
    }

    @Override
    public Container getOxygenTanks() {
        return this.tankInv;
    }

    @Override
    public Container getThermalArmor() {
        return this.thermalArmorInv;
    }

    @Override
    public Container getAccessories() {
        return this.accessoryInv;
    }

    @Override
    public void writeGearToNbt(CompoundTag tag) {
        tag.put(Constant.Nbt.GEAR_INV, this.getGearInv().createTag());
    }

    @Override
    public void readGearFromNbt(CompoundTag tag) {
        this.getGearInv().fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND));
    }
}
