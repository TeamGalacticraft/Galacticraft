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

package dev.galacticraft.impl.internal.mixin.gear;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import dev.galacticraft.impl.network.s2c.GearInvPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.world.inventory.GearInventory;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin extends Entity implements GearInventoryProvider, OwnableEntity {

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> DATA_HAS_MASK_ID = SynchedEntityData.defineId(
            TamableAnimal.class, EntityDataSerializers.BOOLEAN
    );

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> DATA_HAS_GEAR_ID = SynchedEntityData.defineId(
            TamableAnimal.class, EntityDataSerializers.BOOLEAN
    );

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<String> DATA_TANK_SIZE_ID = SynchedEntityData.defineId(
            TamableAnimal.class, EntityDataSerializers.STRING
    );

    private final @Unique SimpleContainer gearInv = this.galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, 3);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, 0);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, 1, 2);

    TamableAnimalMixin() {
        super(null, null);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void gc$sleepData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_HAS_MASK_ID, false);
        builder.define(DATA_HAS_GEAR_ID, false);
        builder.define(DATA_TANK_SIZE_ID, "");
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"), cancellable = true)
    protected void galacticraft$readOxygenSetupData(CompoundTag tag, CallbackInfo ci) {
        this.entityData.set(DATA_HAS_MASK_ID, tag.getBoolean(Constant.Nbt.HAS_MASK));
        this.entityData.set(DATA_HAS_GEAR_ID, tag.getBoolean(Constant.Nbt.HAS_GEAR));
        this.entityData.set(DATA_TANK_SIZE_ID, tag.getString(Constant.Nbt.OXYGEN_TANK));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"), cancellable = true)
    protected void galacticraft$addOxygenSetupData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean(Constant.Nbt.HAS_MASK, this.galacticraft$hasMask());
        tag.putBoolean(Constant.Nbt.HAS_GEAR, this.galacticraft$hasGear());
        tag.putString(Constant.Nbt.OXYGEN_TANK, this.galacticraft$tankSize(0));
    }

    @Unique
    private SimpleContainer galacticraft_createGearInventory() {
        SimpleContainer inv = new GearInventory();
        inv.addListener((inventory) -> this.syncGearToClients(inventory));
        this.syncGearToClients(inv);
        return inv;
    }

    @Unique
    private void syncGearToClients(Container inventory) {
        if (this.level().isClientSide) {
            return;
        }

        this.entityData.set(DATA_HAS_MASK_ID, inventory.getItem(1).is(GCItemTags.OXYGEN_MASKS));
        this.entityData.set(DATA_HAS_GEAR_ID, inventory.getItem(2).is(GCItemTags.OXYGEN_GEAR));
        String tankSize = "";
        if (inventory.getItem(3).is(GCItemTags.OXYGEN_TANKS)) {
            tankSize = inventory.getItem(3).getDescriptionId().replace("item.galacticraft.", "");
        }
        this.entityData.set(DATA_TANK_SIZE_ID, tankSize);

        ItemStack[] stacks = new ItemStack[inventory.getContainerSize()];
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            stacks[i] = inventory.getItem(i);
        }
        TamableAnimal animal = (TamableAnimal) (Object) this;
        GearInvPayload payload = new GearInvPayload(animal.getId(), stacks);

        Collection<ServerPlayer> tracking = PlayerLookup.tracking(animal);
        Constant.LOGGER.info(tracking.size());
        for (ServerPlayer remote : tracking) {
            ServerPlayNetworking.send(remote, payload);
        }
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
    public boolean galacticraft$hasMask() {
        return this.entityData.get(DATA_HAS_MASK_ID);
    }

    @Override
    public boolean galacticraft$hasGear() {
        return this.entityData.get(DATA_HAS_GEAR_ID);
    }

    @Override
    public String galacticraft$tankSize(int i) {
        if (i != 0) return "";
        return this.entityData.get(DATA_TANK_SIZE_ID);
    }

    @Override
    public void galacticraft$writeGearToNbt(CompoundTag tag) {
        tag.put(Constant.Nbt.GEAR_INV, this.gearInv.createTag(this.registryAccess()));
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
        this.gearInv.fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND), this.registryAccess());
        this.gearInv.setChanged();
    }
}
