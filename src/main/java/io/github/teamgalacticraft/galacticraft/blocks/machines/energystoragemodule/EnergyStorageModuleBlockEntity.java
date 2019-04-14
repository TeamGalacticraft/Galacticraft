package io.github.teamgalacticraft.galacticraft.blocks.machines.energystoragemodule;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class EnergyStorageModuleBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    private SimpleEnergyAttribute energy = new SimpleEnergyAttribute(60000, GalacticraftEnergy.GALACTICRAFT_JOULES);
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(2);
    public static int CHARGE_BATTERY_SLOT = 0;
    public static int DRAIN_BATTERY_SLOT = 1;

    private int powerToChargePerTick = 5;

    public EnergyStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.ENERGY_STORAGE_TYPE);
        this.energy.listen(this::markDirty);
    }

    public EnergyAttribute getEnergy() {
        return this.energy;
    }

    @Override
    public void tick() {
        // Charge the battery, drain internal energy buffer.
        ItemStack batteryToCharge = inventory.getInvStack(this.CHARGE_BATTERY_SLOT);
        if (GalacticraftEnergy.isEnergyItem(batteryToCharge) && this.energy.getCurrentEnergy() > 0) {
            int currentBatteryCharge = GalacticraftEnergy.getBatteryEnergy(batteryToCharge);
            int maxBatteryCharge = GalacticraftEnergy.getMaxBatteryEnergy(batteryToCharge);
            int chargeRoom = maxBatteryCharge - currentBatteryCharge;

            int chargeToAdd = Math.min(powerToChargePerTick, chargeRoom);
            chargeToAdd = Math.min(chargeToAdd, this.energy.getCurrentEnergy());

            this.energy.setCurrentEnergy(energy.getCurrentEnergy() - chargeToAdd);

            GalacticraftEnergy.incrementEnergy(batteryToCharge, chargeToAdd);
        }

        // Drain the battery, charge internal energy buffer.
        ItemStack batteryToDrain = inventory.getInvStack(DRAIN_BATTERY_SLOT);
        if (GalacticraftEnergy.isEnergyItem(batteryToDrain) && this.energy.getCurrentEnergy() < this.energy.getMaxEnergy()) {
            int currentBatteryCharge = GalacticraftEnergy.getBatteryEnergy(batteryToDrain);

            int chargeToRemove = Math.min(powerToChargePerTick, currentBatteryCharge);
            int newInternalBuffer = energy.getCurrentEnergy() + chargeToRemove;

            this.energy.setCurrentEnergy(newInternalBuffer);
            GalacticraftEnergy.decrementEnergy(batteryToDrain, chargeToRemove);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Inventory", inventory.toTag());
        tag.putInt("Energy", energy.getCurrentEnergy());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inventory.fromTag(tag.getCompound("Inventory"));
        this.energy.setCurrentEnergy(tag.getInt("Energy"));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }
}