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

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
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

import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCompressorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {
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

    public int collectionAmount = 0;
    public OxygenCompressorStatus status = OxygenCompressorStatus.INACTIVE;

    public OxygenCompressorBlockEntity() {
        super(GalacticraftBlockEntities.OXYGEN_COMPRESSOR_TYPE);
    }

    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    protected boolean canExtractEnergy() {
        return false;
    }

    @Override
    protected boolean canInsertEnergy() {
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
        } else if (this.getTank().isEmpty()) {
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
            getTank().insertFluid(0, component.insertFluid(0, getTank().takeFluid(0, Fraction.of(1, 50), ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
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

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(this.getCachedState(), tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
    }

    public SimpleTankComponent getTank() {
        return this.tank;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().oxygenCompressorEnergyConsumptionRate();
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