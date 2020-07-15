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
import com.hrznstudio.galacticraft.energy.EnergyUtils;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
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
public class RefineryBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

    private static final Predicate<ItemStack>[] SLOT_FILTERS;

    static {
        SLOT_FILTERS = new Predicate[3];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = stack -> ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.TANK_COMPONENT)
                && ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0)
                .getFluid().equals(GalacticraftFluids.CRUDE_OIL);
        SLOT_FILTERS[2] = stack -> ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.TANK_COMPONENT)
                && (ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getFluid().equals(GalacticraftFluids.FUEL)
                || ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).isEmpty());
    }

    private final SimpleTankComponent tank = new SimpleTankComponent(2, Fraction.ofWhole(10)) {
        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            if (tank == 0 && fluid.getFluid() == GalacticraftFluids.CRUDE_OIL || fluid.isEmpty()) {
                super.insertFluid(tank, fluid, action);
            }
            if (tank == 1 && fluid.getFluid() == GalacticraftFluids.FUEL || fluid.isEmpty()) {
                super.insertFluid(tank, fluid, action);
            }
            return fluid;
        }
    };

    public RefineryStatus status = RefineryStatus.IDLE;

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    protected int getInventorySize() {
        return 3;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
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
    public RefineryStatus getStatusForTooltip() {
        return status;
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }

        attemptChargeFromStack(0);
        trySpreadEnergy();

        if (!tank.getMaxCapacity(0).subtract(this.tank.getContents(0).getAmount()).equals(Fraction.ZERO)) {
            if (ComponentProvider.fromItemStack(getInventory().getStack(1)).hasComponent(UniversalComponents.TANK_COMPONENT)
                    && ComponentProvider.fromItemStack(getInventory().getStack(1)).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getFluid().equals(GalacticraftFluids.CRUDE_OIL)) {
                TankComponent component = ComponentProvider.fromItemStack(getInventory().getStack(1)).getComponent(UniversalComponents.TANK_COMPONENT);
                Fraction needed = tank.getMaxCapacity(0).subtract(this.tank.getContents(0).getAmount());
                Fraction taken = component.takeFluid(0, needed, ActionType.PERFORM).getAmount();
                tank.insertFluid(0, new FluidVolume(GalacticraftFluids.CRUDE_OIL, taken), ActionType.PERFORM);
            }
        }

        if (!tank.getContents(1).isEmpty()) {
            ItemStack stack = getInventory().getStack(2);
            if (ComponentProvider.fromItemStack(stack).hasComponent(UniversalComponents.TANK_COMPONENT)
                    && (ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getFluid().equals(GalacticraftFluids.FUEL)
                    || ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).isEmpty())) {
                Fraction needed = ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getMaxCapacity(0)
                        .subtract(ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).getContents(0).getAmount());
                if (!needed.equals(Fraction.ZERO)) {
                    ComponentProvider.fromItemStack(stack).getComponent(UniversalComponents.TANK_COMPONENT).insertFluid(0, this.tank.takeFluid(0, needed, ActionType.PERFORM), ActionType.PERFORM);
                }
            }
        }

        if (getCapacitor().getCurrentEnergy() <= 0) {
            status = RefineryStatus.NOT_ENOUGH_ENERGY;
            return;
        }

        if (!tank.getContents(0).isEmpty() && (tank.getContents(1).getAmount().compareTo(tank.getMaxCapacity(1))) > 0) {
            this.status = RefineryStatus.ACTIVE;
        } else {
            this.status = RefineryStatus.IDLE;
        }
        if (status == RefineryStatus.IDLE) {
            idleEnergyDecrement(false);
        }

        if (status == RefineryStatus.ACTIVE) {
            this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, Galacticraft.configManager.get().refineryEnergyConsumptionRate(), ActionType.PERFORM); //x2 an average machine


            FluidVolume extracted = this.tank.takeFluid(0, Fraction.of(1, 1000), ActionType.PERFORM);
            this.tank.insertFluid(1, new FluidVolume(GalacticraftFluids.FUEL, extracted.getAmount()), ActionType.PERFORM);
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
    public int getEnergyUsagePerTick() {
        return EnergyUtils.Values.T2_MACHINE_ENERGY_USAGE;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum RefineryStatus implements MachineStatus {

        /**
         * Refinery is active and is refining oil into fuel.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.refining"), Formatting.GREEN),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GOLD),

        /**
         * The refinery is out of oil.
         */
        IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GRAY),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED);

        private final Text text;

        RefineryStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}