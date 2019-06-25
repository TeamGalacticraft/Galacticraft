package com.hrznstudio.galacticraft.blocks.machines.electriccompressor;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorStatus;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ElectricCompressorBlockEntity extends CompressorBlockEntity {
    static final int SECOND_OUTPUT_SLOT = OUTPUT_SLOT + 1;

    public ElectricCompressorBlockEntity() {
        super(GalacticraftBlockEntities.ELECTRIC_COMPRESSOR_TYPE);
    }

    @Override
    protected int getInvSize() {
        return super.getInvSize() + 1;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        if (slot == FUEL_INPUT_SLOT) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else {
            return super.getFilterForSlot(slot);
        }
    }

    @Override
    public int getMaxEnergy() {
        return ConfigurableElectricMachineBlockEntity.DEFAULT_MAX_ENERGY;
    }

    @Override
    public void tick() {
        attemptChargeFromStack(FUEL_INPUT_SLOT);

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
            ItemStack item = getInventory().getInvStack(i);

            // If slot is not empty ( must be an ingredient if we've made it this far ), and there is less than 2 items in the slot, we cannot craft two.
            if (!item.isEmpty() && item.getCount() < 2) {
                canCraftTwo = false;
                break;
            }
        }
        if (canCraftTwo) {
            if (getInventory().getInvStack(OUTPUT_SLOT).getCount() >= craftingResult.getMaxCount() || getInventory().getInvStack(SECOND_OUTPUT_SLOT).getCount() >= craftingResult.getMaxCount()) {
                // There would be too many items in the output slot. Just craft one.
                canCraftTwo = false;
            }
        }

        for (int i = 0; i < 9; i++) {
            getInventory().getSlot(i).extract(canCraftTwo ? 2 : 1);
        }

        // <= because otherwise it loops only once and puts in only one slot
        for (int i = OUTPUT_SLOT; i <= SECOND_OUTPUT_SLOT; i++) {
            getInventory().getSlot(i).insert(craftingResult);
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