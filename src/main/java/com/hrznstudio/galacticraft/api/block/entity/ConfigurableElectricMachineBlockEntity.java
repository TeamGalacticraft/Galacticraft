package com.hrznstudio.galacticraft.api.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.ItemInvSlotChangeListener.ItemInvSlotListener;
import alexiil.mc.lib.attributes.item.LimitedFixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.DelegatingFixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import alexiil.mc.lib.attributes.item.impl.SimpleLimitedFixedItemInv;
import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableElectricMachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static final int DEFAULT_MAX_ENERGY = 15000;
    public SimpleEnergyAttribute energy = new SimpleEnergyAttribute(getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES);
    public String owner = "";
    public boolean isParty = false;
    public boolean isPublic = true;
    public String redstoneOption = "DISABLED";

    private final SimpleFixedItemInv inventory = new SimpleFixedItemInv(getInvSize()) {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return getFilterForSlot(slot).matches(item);
        }

        @Override
        public ItemFilter getFilterForSlot(int slot) {
            return ConfigurableElectricMachineBlockEntity.this.getFilterForSlot(slot);
        }
    };

    private final LimitedFixedItemInv limitedInventory = inventory.createLimitedFixedInv();
    private final FixedItemInv exposedInventory = limitedInventory.asUnmodifiable();


    public ConfigurableElectricMachineBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.energy.listen(this::markDirty);
        this.inventory.setOwnerListener((ItemInvSlotListener) (inv, slot) -> markDirty());
    }

    public boolean isActive() {
        if (this.getWorld().isReceivingRedstonePower(pos) && redstoneOption.equals("OFF")) {
            return false;
        } else return this.getWorld().isReceivingRedstonePower(pos) || !redstoneOption.equals("ON");
    }

    /**
     * The max energy that this machine can hold. Override for machines that should hold more.
     *
     * @return Energy capacity of this machine.
     */
    public int getMaxEnergy() {
        return DEFAULT_MAX_ENERGY;
    }

    /** @return The {@link ItemFilter} for the given slot of {@link #getInventory()}. */
    protected ItemFilter getFilterForSlot(int slot) {
        return ConstantItemFilter.ANYTHING;
    }

    /** @return The maximum amount of energy that can be transfered to or from a battery in this machine per call to
     *         {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)} */
    protected int getBatteryTransferRate() {
        return 20;
    }

    public SimpleEnergyAttribute getEnergy() {
        return energy;
    }

    /**
     * Tries to charge this machine from the item in the given slot in this {@link #getInventory}.
     */
    protected void attemptChargeFromStack(int slot) {
        if (energy.getCurrentEnergy() >= energy.getMaxEnergy()) {
            return;
        }
        ItemStack stack = inventory.getInvStack(slot);
        if (GalacticraftEnergy.isEnergyItem(stack)) {
            int itemEnergy = GalacticraftEnergy.getBatteryEnergy(stack);
            EnergyHolderItem item = (EnergyHolderItem) stack.getItem();

            if (itemEnergy > 0) {
                stack = stack.copy();
                int energyToRemove = 5;
                int amountFailedToInsert = item.extract(stack, energyToRemove);
                energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyToRemove - amountFailedToInsert, Simulation.ACTION);
                inventory.forceSetInvStack(slot, stack);
            }
        }
    }

    /**
     * Tries to drain some of this machine's power into the item in the given slot in this {@link #getInventory}.
     */
    protected void attemptDrainPowerToStack(int slot) {
        int available = Math.min(getBatteryTransferRate(), energy.getCurrentEnergy());
        if (available <= 0) {
            return;
        }
        ItemStack stack = inventory.getInvStack(slot);
        if (GalacticraftEnergy.isEnergyItem(stack)) {
            int itemEnergy = GalacticraftEnergy.getBatteryEnergy(stack);
            int itemMaxEnergy = GalacticraftEnergy.getMaxBatteryEnergy(stack);
            EnergyHolderItem item = (EnergyHolderItem) stack.getItem();
            if (itemEnergy < itemMaxEnergy) {
                stack = stack.copy();
                int leftover = item.insert(stack, available);
                energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, available - leftover, Simulation.ACTION);
                inventory.forceSetInvStack(slot, stack);
            }
        }
    }

    protected abstract int getInvSize();

    public final SimpleFixedItemInv getInventory() {
        return inventory;
    }

    /** @return A {@link LimitedFixedItemInv} that can be used to limit what neighbouring blocks do with the
     *         {@link #getExposedInventory() exposed inventory}. */
    public final LimitedFixedItemInv getLimitedInventory() {
        return limitedInventory;
    }

    /** @return The {@link FixedItemInv} that is exposed to neighbouring blocks via attributes. */
    public final FixedItemInv getExposedInventory() {
        return exposedInventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Energy", getEnergy().getCurrentEnergy());
        tag.put("Inventory", inventory.toTag());
        tag.putString("Owner", owner);
        tag.putBoolean("Party", isParty);
        tag.putBoolean("Public", isPublic);
        tag.putString("Redstone", redstoneOption);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        getEnergy().setCurrentEnergy(tag.getInt("Energy"));
        inventory.fromTag(tag.getCompound("Inventory"));
        owner = tag.getString("Owner");
        isParty = tag.getBoolean("Party");
        isPublic = tag.getBoolean("Public");
        redstoneOption = tag.getString("Redstone");
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
