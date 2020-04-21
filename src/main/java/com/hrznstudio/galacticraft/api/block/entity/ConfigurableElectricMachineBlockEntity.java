/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.api.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.ItemInvSlotChangeListener.ItemInvSlotListener;
import alexiil.mc.lib.attributes.item.LimitedFixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableElectricMachineBlockEntity extends BlockEntity implements BlockEntityClientSerializable, EnergyAttributeProvider {

    private final FullFixedItemInv inventory = new FullFixedItemInv(getInvSize()) {
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
    private final SimpleEnergyAttribute energy = new SimpleEnergyAttribute(getMaxEnergy(), GalacticraftEnergy.GALACTICRAFT_JOULES);

    private final SecurityInfo security = new SecurityInfo();
    private RedstoneState redstoneState = RedstoneState.DISABLED;

    public ConfigurableElectricMachineBlockEntity(BlockEntityType<? extends ConfigurableElectricMachineBlockEntity> blockEntityType) {
        super(blockEntityType);
        this.getEnergyAttribute().listen(this::markDirty);
        this.inventory.setOwnerListener((ItemInvSlotListener) (inv, slot) -> markDirty());
    }

    public RedstoneState getRedstoneState() {
        assert redstoneState != null;
        return redstoneState;
    }

    public void setRedstoneState(RedstoneState redstoneState) {
        if (redstoneState != null) {
            this.redstoneState = redstoneState;
        }
    }

    /**
     * Whether the current machine is enabled
     *
     * @return The state of the machine
     */
    public boolean enabled() {
        switch (this.redstoneState) {
            case OFF:
                return !this.getWorld().isReceivingRedstonePower(pos);
            case ON:
                return this.getWorld().isReceivingRedstonePower(pos);
            default:
                return true;
        }
    }

    /**
     * The max energy that this machine can hold. Override for machines that should hold more.
     *
     * @return Energy capacity of this machine.
     */
    public int getMaxEnergy() {
        return Galacticraft.configManager.get().machineEnergyStorageSize();
    }

    /**
     * @return The {@link ItemFilter} for the given slot of {@link #getInventory()}.
     */
    protected ItemFilter getFilterForSlot(int slot) {
        return ConstantItemFilter.ANYTHING;
    }

    /**
     * @return The maximum amount of energy that can be transferred to or from a battery in this machine per call to
     * {@link #attemptChargeFromStack(int)} or {@link #attemptDrainPowerToStack(int)}
     */
    protected int getBatteryTransferRate() {
        return 50;
    }

    @Override
    public SimpleEnergyAttribute getEnergyAttribute() {
        return energy;
    }

    /**
     * Tries to charge this machine from the item in the given slot in this {@link #getInventory}.
     */
    protected void attemptChargeFromStack(int slot) {
        if (getEnergyAttribute().getCurrentEnergy() >= getEnergyAttribute().getMaxEnergy()) {
            return;
        }
        ItemStack stack = inventory.getInvStack(slot).copy();
        int neededEnergy = Math.min(getBatteryTransferRate(), getEnergyAttribute().getMaxEnergy() - getEnergyAttribute().getCurrentEnergy());
        if (GalacticraftEnergy.isEnergyItem(stack)) {
            int amountFailedToExtract = GalacticraftEnergy.extractEnergy(stack, neededEnergy);
            this.getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, neededEnergy - amountFailedToExtract, Simulation.ACTION);
            inventory.forceSetInvStack(slot, stack);
        }
    }

    /**
     * Tries to drain some of this machine's power into the item in the given slot in this {@link #getInventory}.
     *
     * @param slot The slot id of the item
     */
    protected void attemptDrainPowerToStack(int slot) {
        int available = Math.min(getBatteryTransferRate(), getEnergyAttribute().getCurrentEnergy());
        if (available <= 0) {
            return;
        }
        ItemStack stack = inventory.getInvStack(slot).copy();
        if (GalacticraftEnergy.isEnergyItem(stack)) {
            if (GalacticraftEnergy.getEnergy(stack) < GalacticraftEnergy.getMaxEnergy(stack)) {
                int i = GalacticraftEnergy.insertEnergy(stack, available);
                this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, available - i, Simulation.ACTION);
                inventory.forceSetInvStack(slot, stack);
            }
        }
    }

    protected abstract int getInvSize();

    public final FullFixedItemInv getInventory() {
        return inventory;
    }

    /**
     * @return A {@link LimitedFixedItemInv} that can be used to limit what neighbouring blocks do with the
     * {@link #getExposedInventory() exposed inventory}.
     */
    public final LimitedFixedItemInv getLimitedInventory() {
        return limitedInventory;
    }

    /**
     * @return The {@link FixedItemInv} that is exposed to neighbouring blocks via attributes.
     */
    public final FixedItemInv getExposedInventory() {
        return exposedInventory;
    }

    @Nonnull
    public SecurityInfo getSecurity() {
        return security;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Energy", getEnergyAttribute().getCurrentEnergy());
        tag.put("Inventory", inventory.toTag());
        this.security.fromTag(tag);
        tag.putString("Redstone", redstoneState.asString());
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        getEnergyAttribute().setCurrentEnergy(tag.getInt("Energy"));
        inventory.fromTag(tag.getCompound("Inventory"));
        this.security.fromTag(tag);
        redstoneState = RedstoneState.fromString(tag.getString("Redstone"));
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public void trySpreadEnergy() {
        for (int i = 0; i < ConfigurableElectricMachineBlock.optionsToArray(this.world.getBlockState(pos)).length; i++) {
            if (ConfigurableElectricMachineBlock.optionsToArray(this.world.getBlockState(pos))[i] == SideOption.POWER_OUTPUT) {
                if (world.getBlockState(pos.offset(Direction.values()[i])).getBlock() instanceof ConfigurableElectricMachineBlock) {
                    if (((ConfigurableElectricMachineBlock) world.getBlockState(pos.offset(Direction.values()[i])).getBlock()).canWireConnect(world, Direction.values()[i], pos, pos.offset(Direction.values()[i])) == WireConnectionType.ENERGY_INPUT) {
                        EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, Direction.values()[i]);
                        if (energyAttribute.canExtractEnergy()) {
                            int failed = getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5, Simulation.ACTION);
                            this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5 - failed, Simulation.ACTION);
                        }
                    }
                } else {
                    EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, Direction.values()[i]);
                    if (energyAttribute.canExtractEnergy()) {
                        int failed = getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5, Simulation.ACTION);
                        this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5 - failed, Simulation.ACTION);
                    }
                }
            } else if (ConfigurableElectricMachineBlock.optionsToArray(this.world.getBlockState(pos))[i] == SideOption.POWER_INPUT) {
                if (world.getBlockState(pos.offset(Direction.values()[i])).getBlock() instanceof ConfigurableElectricMachineBlock) {
                    if (((ConfigurableElectricMachineBlock) world.getBlockState(pos.offset(Direction.values()[i])).getBlock()).canWireConnect(world, Direction.values()[i], pos, pos.offset(Direction.values()[i])) == WireConnectionType.ENERGY_OUTPUT) {
                        EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, Direction.values()[i]);
                        if (energyAttribute.canExtractEnergy()) {
                            int failed = getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5, Simulation.ACTION);
                            this.getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5 - failed, Simulation.ACTION);
                        }
                    }
                } else {
                    EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, Direction.values()[i]);
                    if (energyAttribute.canExtractEnergy()) {
                        int failed = getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5, Simulation.ACTION);
                        this.getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 5 - failed, Simulation.ACTION);
                    }
                }
            }
        }
    }

    public void idleEnergyDecrement(boolean off) {
        if (getEnergyUsagePerTick() > 0) {
            if (GalacticraftEnergy.Values.getTick() % ((75 * (getEnergyUsagePerTick() / 20)) * (off ? 2 : 1)) == 0) {
                getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
            }
        }
    }

    public abstract int getEnergyUsagePerTick();

    public enum RedstoneState implements StringIdentifiable {
        /**
         * Ignores redstone entirely.
         */
        DISABLED,

        /**
         * When powered with redstone, the machine turns off.
         */
        OFF,

        /**
         * When powered with redstone, the machine turns on.
         */
        ON;

        public static RedstoneState fromString(String string) {
            switch (string.toUpperCase()) {
                case "OFF":
                    return OFF;
                case "ON":
                    return ON;
                default:
                    return DISABLED;
            }
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }

    public static class SecurityInfo {
        private UUID owner;
        private String username;
        private Identifier team;
        private Publicity publicity;

        protected SecurityInfo() {
            this.owner = null;
            this.publicity = Publicity.PUBLIC;
            this.team = null;
            this.username = "";
        }

        public Publicity getPublicity() {
            return publicity;
        }

        public void setPublicity(Publicity publicity) {
            this.publicity = publicity;
        }

        public boolean hasOwner() {
            return this.owner != null;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public UUID getOwner() {
            return this.owner;
        }

        public void setOwner(PlayerEntity owner) {
            if (!this.hasOwner()) {
                this.owner = owner.getUuid();
            }
            this.username = owner.getEntityName();
        }

        public Identifier getTeam() {
            return team;
        }

        public boolean hasTeam() {
            return team != null;
        }

        public CompoundTag toTag(CompoundTag tag) {
            CompoundTag compoundTag = new CompoundTag();
            if (this.hasOwner()) {
                compoundTag.putUuid("owner", this.owner);
            }
            compoundTag.putString("username", this.username);
            compoundTag.putString("publicity", this.publicity.asString());
            if (this.hasTeam()) {
                compoundTag.putString("team", team.toString());
            }
            tag.put("security", compoundTag);
            return tag;
        }

        public void fromTag(CompoundTag tag) {
            CompoundTag compoundTag = tag.getCompound("security");
            if (compoundTag.contains("owner")) {
                if (!this.hasOwner()) {
                    this.owner = compoundTag.getUuid("owner");
                }
            }
            if (compoundTag.contains("team")) {
                if (!this.hasTeam()) {
                    this.team = new Identifier(compoundTag.getString("team"));
                }
            }

            this.username = compoundTag.getString("username");

            if (compoundTag.contains("publicity")) {
                this.publicity = Publicity.valueOf(compoundTag.getString("publicity"));
            }
        }


        public enum Publicity implements StringIdentifiable {
            PUBLIC,
            SPACE_RACE,
            PRIVATE;

            @Override
            public String asString() {
                return this.toString();
            }
        }
    }
}
