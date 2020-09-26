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
 *
 */

package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCompressorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    public static final Fraction MAX_OXYGEN = Fraction.of(1, 100).multiply(Fraction.ofWhole(5000));
    public static final int BATTERY_SLOT = 0;

    public int collectionAmount = 0;
    public OxygenCompressorStatus status = OxygenCompressorStatus.INACTIVE;

    public OxygenCompressorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COMPRESSOR_TYPE);
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
        return Lists.asList(SideOption.DEFAULT, SideOption.POWER_INPUT, new SideOption[]{SideOption.FLUID_INPUT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT});
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
    public MachineStatus getStatusForTooltip() {
        return this.status;
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }
        attemptChargeFromStack(BATTERY_SLOT);
        trySpreadEnergy();
        if (this.getCapacitor().getCurrentEnergy() < getEnergyUsagePerTick()) {
            status = OxygenCompressorStatus.NOT_ENOUGH_ENERGY;
        } else if (this.getFluidTank().isEmpty()) {
            status = OxygenCompressorStatus.NOT_ENOUGH_OXYGEN;
        } else {
            TankComponent component = ComponentProvider.fromItemStack(this.getInventory().getStack(1)).getComponent(UniversalComponents.TANK_COMPONENT);
            if (component != null) {
                if (component.getContents(0).getAmount().compareTo(component.getMaxCapacity(0)) == 0) {
                    status = OxygenCompressorStatus.CONTAINER_FULL;
                } else {
                    status = OxygenCompressorStatus.COMPRESSING;
                }
            } else {
                status = OxygenCompressorStatus.INACTIVE;
            }
        }

        if (status == OxygenCompressorStatus.COMPRESSING) {
            TankComponent component = ComponentProvider.fromItemStack(this.getInventory().getStack(1)).getComponent(UniversalComponents.TANK_COMPONENT);
            getFluidTank().insertFluid(0, component.insertFluid(0, getFluidTank().takeFluid(0, Fraction.of(1, 50), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
            this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM);
        }
    }
    
    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenCompressorEnergyConsumptionRate();
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
        return false;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return true;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return volume.getFluid().isIn(GalacticraftTags.OXYGEN);
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum OxygenCompressorStatus implements MachineStatus {
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED),
        NOT_ENOUGH_OXYGEN(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_oxygen"), Formatting.RED),
        INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive"), Formatting.GRAY),
        CONTAINER_FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD),
        COMPRESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.compressing"), Formatting.GREEN);

        private final Text text;

        OxygenCompressorStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static OxygenCompressorStatus get(int index) {
            if (index < 0) return OxygenCompressorStatus.values()[0];
            return OxygenCompressorStatus.values()[index % OxygenCompressorStatus.values().length];
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}