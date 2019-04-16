package io.github.teamgalacticraft.galacticraft.blocks.machines;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.api.EnergyHolderItem;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public abstract class MachineBlockEntity extends BlockEntity {
    private SimpleEnergyAttribute energy = new SimpleEnergyAttribute(getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES);
    private SimpleFixedItemInv inventory = new SimpleFixedItemInv(getInvSize());

    /**
     * The max energy that this machine can hold. Override for machines that should hold more.
     *
     * @return
     */
    protected int getMaxEnergy() {
        return 15000;
    }

    public SimpleEnergyAttribute getEnergy() {
        return energy;
    }

    public MachineBlockEntity(BlockEntityType<?> blockEntityType_1) {
        super(blockEntityType_1);
    }

    // Tries charging the block entity with the given itemstack
    protected void attemptChargeFromStack(ItemStack battery) {
        if (GalacticraftEnergy.isEnergyItem(battery)) {
            int itemEnergy = GalacticraftEnergy.getBatteryEnergy(battery);
            EnergyHolderItem item = (EnergyHolderItem) battery.getItem();

            if (itemEnergy > 0 && energy.getCurrentEnergy() < energy.getMaxEnergy()) {
                int energyToRemove = 5;
                int amountFailedToInsert = item.extract(battery, energyToRemove);
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyToRemove - amountFailedToInsert, ActionType.PERFORM);
            }
        }
    }

    protected abstract int getInvSize();

    public final SimpleFixedItemInv getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Energy", getEnergy().getCurrentEnergy());
        tag.put("Inventory", inventory.toTag());

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        getEnergy().setCurrentEnergy(tag.getInt("Energy"));
        inventory.fromTag(tag.getCompound("Inventory"));
    }
}