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

package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.util.Tickable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

    public int visiblePanels;

    public BasicSolarPanelStatus status = BasicSolarPanelStatus.NIGHT;

    public BasicSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 1;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    @Override
    public void tick() {
        if (world.isClient || !enabled()) {
            return;
        }
        double time = world.getTimeOfDay() % 24000;

        if (time > 0 && time < 12000) {
            if (getEnergyAttribute().getCurrentEnergy() < getEnergyAttribute().getMaxEnergy()) {
                if (world.isRaining() || world.isThundering()) {
                    status = BasicSolarPanelStatus.RAINING;
                } else {
                    status = BasicSolarPanelStatus.COLLECTING;
                }
            } else {
                status = BasicSolarPanelStatus.FULL;
            }
        } else {
            status = BasicSolarPanelStatus.NIGHT;
        }

        double i = 0;
        for (int z = -1; z < 2; z++) {
            for (int y = -1; y < 2; y++) {
                if (world.isSkyVisible(pos.add(z, 2, y))) {
                    i++;
                }
            }
        }
        visiblePanels = (int) i;
        i /= 9;
        if (i == 0) status = BasicSolarPanelStatus.BLOCKED;

        if (status == BasicSolarPanelStatus.COLLECTING) {
            if (time > 6000) {
                getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((int) ((6000D - (time - 6000D)) / 705.882353D) * i), Simulation.ACTION);
            } else {
                getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((time / 705.882353D) * i), Simulation.ACTION);
            }
        }

        if (status == BasicSolarPanelStatus.RAINING) {
            if (time > 6000) {
                getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((int) (((6000D - (time - 6000D)) / 705.882353D) / 3) * i), Simulation.ACTION);
            } else {
                getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((int) ((time / 705.882353D) / 3) * i), Simulation.ACTION);
            }
        }

        trySpreadEnergy();
        attemptDrainPowerToStack(0);
    }

    @Override
    protected int getBatteryTransferRate() {
        return 10;
    }
}