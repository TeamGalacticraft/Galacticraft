/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenDecompressorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final Fraction MAX_OXYGEN = Fraction.of(1, 100).multiply(Fraction.ofWhole(5000));
    public static final int BATTERY_SLOT = 0;

    private final SimpleTankComponent tank = new SimpleTankComponent(1, MAX_OXYGEN) {
        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            if (fluid.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                return super.insertFluid(tank, fluid, action);
            } else {
                return fluid;
            }
        }

        @Override
        public void setFluid(int slot, FluidVolume stack) {
            if (stack.isEmpty() || stack.getFluid().isIn(GalacticraftTags.OXYGEN)) {
                super.setFluid(slot, stack);
            }
        }
    };

    public OxygenDecompressorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_DECOMPRESSOR_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public Fraction getFluidTankMaxCapacity() {
        return MAX_OXYGEN;
    }

    @Override
    public int getFluidTankSize() {
        return 1;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.FLUID_OUTPUT);
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        if (slot == 0) {
            return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        } else {
            return OxygenUtils::isOxygenItem;
        }
    }

    @Override
    protected MachineStatus getStatus(int index) {
        return Status.values()[index];
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }
        trySpreadFluids(0);
        attemptChargeFromStack(BATTERY_SLOT);
        if (this.getCapacitor().getCurrentEnergy() < getEnergyUsagePerTick()) {
            setStatus(Status.NOT_ENOUGH_ENERGY);
        } else if (this.getTank().getContents(0).getAmount().compareTo(this.getTank().getMaxCapacity(0)) == 0) {
            setStatus(Status.FULL);
        } else {
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(this.getInventory().getStack(1));
            if (component != null) {
                if (component.getContents(0).isEmpty()) {
                    setStatus(Status.NOT_ENOUGH_OXYGEN);
                } else {
                    setStatus(Status.DECOMPRESSING);
                }
            } else {
                setStatus(Status.NOT_ENOUGH_ITEMS);
            }
        }

        if (getStatus() == Status.DECOMPRESSING) {
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(this.getInventory().getStack(1));
            component.insertFluid(0, getTank().insertFluid(0, component.takeFluid(0, Fraction.of(1, 50), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
            this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tank.toTag(tag);

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        tank.fromTag(tag);
    }

    public SimpleTankComponent getTank() {
        return this.tank;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenDecompressorEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return true;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return true;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return true;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return false;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return volume.getFluid().isIn(GalacticraftTags.OXYGEN);
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY),
        NOT_ENOUGH_ITEMS(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_items"), Formatting.RED, StatusType.MISSING_FLUIDS),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft-rewoven.machinestatus.empty_canister"), Formatting.RED, StatusType.MISSING_ITEMS),
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD, StatusType.OUTPUT_FULL),
        DECOMPRESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.decompressing"), Formatting.GREEN, StatusType.WORKING);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static Status get(int index) {
            if (index < 0) return Status.values()[0];
            return Status.values()[index % Status.values().length];
        }

        @Override
        public @NotNull Text getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return ordinal();
        }
    }
}