package io.github.teamgalacticraft.galacticraft.blocks.machines.oxygencollector;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class OxygenCollectorBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {
    private SimpleEnergyAttribute energy = new SimpleEnergyAttribute(5000, GalacticraftEnergy.GALACTICRAFT_JOULES);
    private SimpleEnergyAttribute oxygen = new SimpleEnergyAttribute(15000, GalacticraftEnergy.GALACTICRAFT_OXYGEN);
    SimpleFixedItemInv inventory = new SimpleFixedItemInv(1);
    public static int BATTERY_SLOT = 0;

    public OxygenCollectorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COLLECTOR_TYPE);
        this.energy.listen(this::markDirty);
    }

    public EnergyAttribute getEnergy() {
        return this.energy;
    }

    private void attemptChargeFromStack(ItemStack itemStack) {
        if (GalacticraftEnergy.isEnergyItem(itemStack)) {
            int itemEnergy = GalacticraftEnergy.getBatteryEnergy(itemStack);
            if (itemEnergy > 0 && energy.getCurrentEnergy() < energy.getMaxEnergy()) {
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                GalacticraftEnergy.decrementEnergy(itemStack, 1);
            }
        }
    }

    private int collectOxygen() {
        return 5;
    }

    @Override
    public void tick() {
        attemptChargeFromStack(inventory.getInvStack(BATTERY_SLOT));

        if (this.energy.getCurrentEnergy() > 0) {
            this.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
            this.oxygen.insertEnergy(GalacticraftEnergy.GALACTICRAFT_OXYGEN, collectOxygen(), ActionType.PERFORM);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.put("Inventory", inventory.toTag());
        tag.putInt("Energy", energy.getCurrentEnergy());
        tag.putInt("Oxygen", oxygen.getCurrentEnergy());

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.inventory.fromTag(tag.getCompound("Inventory"));
        this.energy.setCurrentEnergy(tag.getInt("Energy"));
        this.oxygen.setCurrentEnergy(tag.getInt("Oxygen"));
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