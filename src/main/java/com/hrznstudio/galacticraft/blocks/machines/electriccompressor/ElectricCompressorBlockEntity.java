package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorStatus;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ElectricCompressorBlockEntity extends CompressorBlockEntity {
    static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;
    private SimpleEnergyAttribute energy = new SimpleEnergyAttribute(MachineBlockEntity.DEFAULT_MAX_ENERGY, GalacticraftEnergy.GALACTICRAFT_JOULES);

    public ElectricCompressorBlockEntity() {
        super(GalacticraftBlockEntities.ELECTRIC_COMPRESSOR_TYPE);
    }

    @Override
    protected int getInventorySize() {
        return super.getInventorySize() + 1;
    }

    // Tries charging the block entity with the given itemstack
    protected void attemptChargeFromStack(ItemStack battery) {
        if (GalacticraftEnergy.isEnergyItem(battery)) {
            int itemEnergy = GalacticraftEnergy.getBatteryEnergy(battery);
            EnergyHolderItem item = (EnergyHolderItem) battery.getItem();

            if (itemEnergy > 0 && energy.getCurrentEnergy() < energy.getMaxEnergy()) {
                int energyToRemove = 5;
                int amountFailedToInsert = item.extract(battery, energyToRemove);
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyToRemove - amountFailedToInsert, Simulation.ACTION);
            }
        }
    }

    @Override
    public void tick() {
        attemptChargeFromStack(inventory.getInvStack(CompressorBlockEntity.FUEL_INPUT_SLOT));

        // Drain energy
        int extractEnergy = this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 2, Simulation.ACTION);
        if (extractEnergy == 0) {
            status = CompressorStatus.INACTIVE;
            return;
        } else {
            status = CompressorStatus.PROCESSING;
        }

        super.tick();
    }

    @Override
    protected boolean shouldUseFuel() {
        return false;
    }

    @Override
    protected void craftItem(ItemStack craftingResult) {
        boolean canCraftTwo = true;

        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getInvStack(i);

            // If slot is not empty ( must be an ingredient if we've made it this far ), and there is less than 2 items in the slot, we cannot craft two.
            if (!item.isEmpty() && item.getCount() < 2) {
                canCraftTwo = false;
                break;
            }
        }
        if (canCraftTwo) {
            if (inventory.getInvStack(OUTPUT_SLOT).getCount() > craftingResult.getMaxCount() || inventory.getInvStack(SECOND_OUTPUT_SLOT).getCount() > craftingResult.getMaxCount()) {
                // There would be too many items in the output slot. Just craft one.
                canCraftTwo = false;
            }
        }

        for (int i = 0; i < 9; i++) {
            inventory.getInvStack(i).decrement(canCraftTwo ? 2 : 1);
        }

        // <= because otherwise it loops only once and puts in only one slot
        for (int i = OUTPUT_SLOT; i <= SECOND_OUTPUT_SLOT; i++) {
            ItemStack output = inventory.getInvStack(i);
            if (output.isEmpty()) {
                inventory.setInvStack(i, craftingResult, Simulation.ACTION);
            } else {
                // Multiply the end result by 2.
                inventory.getInvStack(i).increment(craftingResult.getCount());
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Energy", energy.getCurrentEnergy());

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        energy.setCurrentEnergy(tag.getInt("Energy"));
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