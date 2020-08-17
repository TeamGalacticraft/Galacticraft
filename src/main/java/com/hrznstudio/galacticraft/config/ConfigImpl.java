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

package com.hrznstudio.galacticraft.config;

import com.google.gson.annotations.Expose;
import com.hrznstudio.galacticraft.api.config.Config;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ConfigImpl implements Config {

    @Expose
    private boolean debugLog = false;
    @Expose
    private int wireMaxTransferPerTick = 480;
    @Expose
    private int heavyWireMaxTransferPerTick = 1440;
    @Expose
    private int coalGeneratorEnergyProductionRate = 120;
    @Expose
    private int solarPanelEnergyProductionRate = 44;
    @Expose
    private int circuitFabricatorEnergyConsumptionRate = 20;
    @Expose
    private int electricCompressorEnergyConsumptionRate = 75;
    @Expose
    private int oxygenCollectorEnergyConsumptionRate = 10;
    @Expose
    private int refineryEnergyConsumptionRate = 60;
    @Expose
    private int energyStorageModuleStorageSize = 500_000;
    @Expose
    private int machineEnergyStorageSize = 30_000;
    @Expose
    private int oxygenCompressorEnergyConsumptionRate = 15;
    @Expose
    private int oxygenDecompressorEnergyConsumptionRate = 15;

    @Override
    public boolean isDebugLogEnabled() {
        return this.debugLog;
    }

    @Override
    public void setDebugLog(boolean flag) {
        this.debugLog = flag;
    }

    @Override
    public int wireTransferLimit() {
        return wireMaxTransferPerTick;
    }

    @Override
    public void setWireTransferLimit(int amount) {
        this.wireMaxTransferPerTick = amount;
    }

    @Override
    public int heavyWireTransferLimit() {
        return heavyWireMaxTransferPerTick;
    }

    @Override
    public void setHeavyWireTransferLimit(int amount) {
        this.heavyWireMaxTransferPerTick = amount;
    }

    @Override
    public int coalGeneratorEnergyProductionRate() {
        return coalGeneratorEnergyProductionRate;
    }

    @Override
    public void setCoalGeneratorEnergyProductionRate(int amount) {
        this.coalGeneratorEnergyProductionRate = amount;
    }

    @Override
    public int solarPanelEnergyProductionRate() {
        return solarPanelEnergyProductionRate;
    }

    @Override
    public void setSolarPanelEnergyProductionRate(int amount) {
        this.solarPanelEnergyProductionRate = amount;
    }

    @Override
    public int circuitFabricatorEnergyConsumptionRate() {
        return circuitFabricatorEnergyConsumptionRate;
    }

    @Override
    public void setCircuitFabricatorEnergyConsumptionRate(int amount) {
        this.circuitFabricatorEnergyConsumptionRate = amount;
    }

    @Override
    public int electricCompressorEnergyConsumptionRate() {
        return electricCompressorEnergyConsumptionRate;
    }

    @Override
    public void setElectricCompressorEnergyConsumptionRate(int amount) {
        this.electricCompressorEnergyConsumptionRate = amount;
    }

    @Override
    public int oxygenCollectorEnergyConsumptionRate() {
        return oxygenCollectorEnergyConsumptionRate;
    }

    @Override
    public void setOxygenCollectorEnergyConsumptionRate(int amount) {
        this.oxygenCollectorEnergyConsumptionRate = amount;
    }

    @Override
    public int refineryEnergyConsumptionRate() {
        return refineryEnergyConsumptionRate;
    }

    @Override
    public void setRefineryEnergyConsumptionRate(int amount) {
        this.refineryEnergyConsumptionRate = amount;
    }

    @Override
    public int energyStorageModuleStorageSize() {
        return energyStorageModuleStorageSize;
    }

    @Override
    public void setEnergyStorageModuleStorageSize(int amount) {
        this.energyStorageModuleStorageSize = amount;
    }

    @Override
    public int machineEnergyStorageSize() {
        return machineEnergyStorageSize;
    }

    @Override
    public void setMachineEnergyStorageSize(int amount) {
        this.machineEnergyStorageSize = amount;
    }

    @Override
    public int oxygenCompressorEnergyConsumptionRate() {
        return oxygenCompressorEnergyConsumptionRate;
    }

    @Override
    public void setOxygenCompressorEnergyConsumptionRate(int amount) {
        this.oxygenCompressorEnergyConsumptionRate = amount;
    }

    @Override
    public int oxygenDecompressorEnergyConsumptionRate() {
        return oxygenDecompressorEnergyConsumptionRate;
    }

    @Override
    public void setOxygenDecompressorEnergyConsumptionRate(int amount) {
        this.oxygenDecompressorEnergyConsumptionRate = amount;
    }
}
