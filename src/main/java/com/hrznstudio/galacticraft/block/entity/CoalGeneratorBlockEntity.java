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
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {

    public static final Map<Item, Integer> FUEL_MAP = new HashMap<>(ImmutableMap.of(Items.COAL_BLOCK, 320 * 10, Items.COAL, 320, Items.CHARCOAL, 310));
    @SuppressWarnings("unchecked")
    private static final Predicate<ItemStack>[] SLOT_FILTERS = new Predicate[2];

    static {
        SLOT_FILTERS[0] = stack -> FUEL_MAP.containsKey(stack.getItem());
        SLOT_FILTERS[1] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    public CoalGeneratorStatus status = CoalGeneratorStatus.IDLE;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;
    private float heat = 0.0f;

    /*
     * Energy stats:
     * T1 machine: uses 30gj/t
     * Coal Generator: generates 120gj/t
     */

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_TYPE);
    }

    @Override
    public boolean canExtractEnergy() {
        return true;
    }

    @Override
    public boolean canInsertEnergy() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public CoalGeneratorStatus getStatusForTooltip() {
        return status;
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

    @Override
    public int getFluidTankSize() {
        return 0;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_OUTPUT, SideOption.ITEM_INPUT);
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
    }

    @Override
    public void tick() {
        if (this.world.isClient || disabled()) {
            return;
        }

        if (status == CoalGeneratorStatus.IDLE) {
            if (heat >= 1.0F) {
                heat -= 0.05F;
            } else {
                heat = 0;
            }
        }

        if (FUEL_MAP.containsKey(getInventory().getStack(0).getItem()) && getCapacitor().getCurrentEnergy() < getCapacitor().getMaxEnergy() && status == CoalGeneratorStatus.IDLE) {
            this.status = CoalGeneratorStatus.WARMING;

            this.fuelTimeMax = FUEL_MAP.get(getInventory().getStack(0).getItem());
            this.fuelTimeCurrent = 0;
            this.fuelEnergyPerTick = 120;

            ItemStack stack = getInventory().getStack(0).copy();
            stack.decrement(1);
            getInventory().setStack(0, stack);
        }

        if (this.status == CoalGeneratorStatus.WARMING) {
            if (this.heat >= 1.0f) {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.heat += 0.005f; //10 secs of heating - 1/8th of the time is spent heating (in this case) when it comes to coal/charcoal
        }

        if (status == CoalGeneratorStatus.ACTIVE || this.status == CoalGeneratorStatus.WARMING) {
            fuelTimeCurrent++;
            getCapacitor().generateEnergy(world, pos, (int) (Galacticraft.configManager.get().coalGeneratorEnergyProductionRate() * heat));

            if (fuelTimeCurrent >= fuelTimeMax) {
                this.status = CoalGeneratorStatus.IDLE;
                this.fuelTimeCurrent = 0;
            }
        }

        trySpreadEnergy();
        attemptDrainPowerToStack(1);
    }

    @Override
    public int getEnergyUsagePerTick() {
        return 0;
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return false;
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
        return false;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return false;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum CoalGeneratorStatus implements MachineStatus {
        /**
         * The generator is active and is generating energy.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active"), Formatting.GREEN),

        /**
         * The generator is warming up.
         */
        WARMING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.warming"), Formatting.GOLD),

        /**
         * The generator is full or out of fuel.
         */
        IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GOLD);

        private final Text text;

        CoalGeneratorStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static CoalGeneratorStatus get(int index) {
            if (index < 0) return ACTIVE;
            return CoalGeneratorStatus.values()[index % CoalGeneratorStatus.values().length];
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}