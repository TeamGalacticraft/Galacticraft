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
import io.github.cottonmc.component.api.ActionType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;

import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AdvancedSolarPanelBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

    public double multiplier;

    public AdvancedSolarPanelStatus status = AdvancedSolarPanelStatus.NIGHT;

    public AdvancedSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.ADVANCED_SOLAR_PANEL_TYPE);
    }

    @Override
    protected int getInventorySize() {
        return 1;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return 0;
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    @Override
    protected boolean canExtractEnergy() {
        return true;
    }

    @Override
    protected boolean canInsertEnergy() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AdvancedSolarPanelStatus getStatusForTooltip() {
        return status;
    }

    @Override
    public void tick() {
        trySpreadEnergy();
        attemptDrainPowerToStack(0);

        int visiblePanels = 0;

        for (int z = -1; z < 2; z++) {
            for (int y = -1; y < 2; y++) {
                if (world.isSkyVisible(pos.add(z, 2, y))) {
                    visiblePanels++;
                }
            }
        }

        multiplier = ((double) visiblePanels) / 9D;

        if (world.isClient || disabled()) {
            return;
        }

        double time = (world.getTimeOfDay() % 24000);
        if (world.isRaining() || world.isThundering()) {
            status = AdvancedSolarPanelStatus.PARTIALLY_BLOCKED;
            multiplier *= 0.55;
        }

        if (time > 1000.0D && time < 11000.0D) {
            status = AdvancedSolarPanelStatus.COLLECTING;
            if (getCapacitatorComponent().getCurrentEnergy() >= getCapacitatorComponent().getMaxEnergy()) {
                status = AdvancedSolarPanelStatus.FULL;
                return;
            }
        } else {
            multiplier *= 0.15D;
            status = AdvancedSolarPanelStatus.NIGHT;
            return;
        }

        if (visiblePanels < 9) {
            if (status != AdvancedSolarPanelStatus.NIGHT) status = AdvancedSolarPanelStatus.PARTIALLY_BLOCKED;
            multiplier *= 0.8D;
        }

        if (visiblePanels == 0) {
            status = AdvancedSolarPanelStatus.BLOCKED;
            return;
        }

        if (time > 6000) {
            time = 6000D - (time - 6000D);
        }

        this.getCapacitatorComponent().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) Math.min(Galacticraft.configManager.get().solarPanelEnergyProductionRate(), (Galacticraft.configManager.get().solarPanelEnergyProductionRate() * (time / 6000D) * multiplier) * 4), ActionType.PERFORM);
    }

    @Override
    protected int getBatteryTransferRate() {
        return 10;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum AdvancedSolarPanelStatus implements MachineStatus {
        /**
         * Solar panel is active and is generating energy.
         */
        COLLECTING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.collecting"), Formatting.GREEN),

        /**
         * Solar Panel can generate energy, but the buffer is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full"), Formatting.GOLD),

        /**
         * Solar Panel is generating energy, but less efficiently as it is blocked or raining.
         */
        PARTIALLY_BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.partially_blocked"), Formatting.DARK_AQUA),

        /**
         * Solar Panel is generating very little energy as it is night.
         */
        NIGHT(new TranslatableText("ui.galacticraft-rewoven.machinestatus.night"), Formatting.BLUE),

        /**
         * The sun is not visible.
         */
        BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.blocked"), Formatting.DARK_GRAY);

        private final Text text;

        AdvancedSolarPanelStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        public static AdvancedSolarPanelStatus get(int index) {
            if (index < 0) index = 0;
            return AdvancedSolarPanelStatus.values()[index % AdvancedSolarPanelStatus.values().length];
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}