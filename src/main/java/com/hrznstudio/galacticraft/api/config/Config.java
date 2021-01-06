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

package com.hrznstudio.galacticraft.api.config;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface Config {

    static Config getInstance() {
        return ConfigManager.getInstance().get();
    }

    boolean isDebugLogEnabled();

    void setDebugLog(boolean flag);

    int wireTransferLimit();

    void setWireTransferLimit(int amount);

    int heavyWireTransferLimit();

    void setHeavyWireTransferLimit(int amount);

    int coalGeneratorEnergyProductionRate();

    void setCoalGeneratorEnergyProductionRate(int amount);

    int solarPanelEnergyProductionRate();

    void setSolarPanelEnergyProductionRate(int amount);

    int circuitFabricatorEnergyConsumptionRate();

    void setCircuitFabricatorEnergyConsumptionRate(int amount);

    int electricCompressorEnergyConsumptionRate();

    void setElectricCompressorEnergyConsumptionRate(int amount);

    int oxygenCollectorEnergyConsumptionRate();

    void setOxygenCollectorEnergyConsumptionRate(int amount);

    int refineryEnergyConsumptionRate();

    void setRefineryEnergyConsumptionRate(int amount);

    int electricFurnaceEnergyConsumptionRate();

    void setElectricFurnaceEnergyConsumptionRate(int amount);

    int energyStorageModuleStorageSize();

    void setEnergyStorageModuleStorageSize(int amount);

    int machineEnergyStorageSize();

    void setMachineEnergyStorageSize(int amount);

    int oxygenCompressorEnergyConsumptionRate();

    void setOxygenCompressorEnergyConsumptionRate(int amount);

    int oxygenDecompressorEnergyConsumptionRate();

    void setOxygenDecompressorEnergyConsumptionRate(int amount);
}
