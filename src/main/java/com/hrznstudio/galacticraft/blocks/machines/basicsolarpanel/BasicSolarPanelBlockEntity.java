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
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, EnergyStorage {

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

    private long tick = 0;
    @Override
    public void tick() {

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

        if (world.isClient || !enabled()) {
            return;
        }
        tick++;
        double time = world.getTimeOfDay() % 24000;
        if (time > 1000.0D && time < 11300.0D) {
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

        if (i == 0) status = BasicSolarPanelStatus.BLOCKED;

        if (status == BasicSolarPanelStatus.COLLECTING) {
            if (time > 6000) {
                if (((int) ((6000D - (time - 6000D)) / 705.882353D) * i) < 1) {
                    if (tick % 2 == 0) {
                        getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
                    }
                } else {
                    getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((((6000D - (time - 6000D)) / 705.882353D) + 0.5D) * i), Simulation.ACTION);
                }
            } else {
                if (((time / 705.882353D) * i) < 1) {
                    if (tick % 2 == 0) {
                        getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION);
                    }
                } else {
                    getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (((time / 705.882353D) + 0.5D) * i), Simulation.ACTION);
                }
            }
        }

        if (status == BasicSolarPanelStatus.RAINING) {
            if (time > 6000) {
                getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (((((6000D - (time - 6000D)) + 0.5D) / 705.882353D) / 3.0D) * i), Simulation.ACTION);
            } else {
                getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((((time / 705.882353D) + 0.5D) / 3.0D) * i), Simulation.ACTION);
            }
        }

        trySpreadEnergy();
        attemptDrainPowerToStack(0);
    }

    @Override
    protected int getBatteryTransferRate() {
        return 10;
    }
    @Override
    public double getStored(EnergySide face) {
        return GalacticraftEnergy.convertToTR(this.getEnergyAttribute().getCurrentEnergy());
    }

    @Override
    public void setStored(double amount) {
        this.getEnergyAttribute().setCurrentEnergy(GalacticraftEnergy.convertFromTR(amount));
    }

    @Override
    public double getMaxStoredPower() {
        return GalacticraftEnergy.convertToTR(getEnergyAttribute().getMaxEnergy());
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

}