/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.config;

import com.google.gson.annotations.Expose;
import com.hrznstudio.galacticraft.api.config.Config;
import com.hrznstudio.galacticraft.util.EnergyUtils;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ConfigImpl implements Config {
    @Expose
    private boolean debugLog = false;
    @Expose
    private double wireMaxTransferPerTick = 128;
    @Expose
    private double heavyWireMaxTransferPerTick = 256;
    @Expose
    private double coalGeneratorEnergyProductionRate = 10; //total 4000
    @Expose
    private double solarPanelEnergyProductionRate = 3.6666666666666665;
    @Expose
    private double circuitFabricatorEnergyConsumptionRate = EnergyUtils.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private double electricCompressorEnergyConsumptionRate = EnergyUtils.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private double electricArcFurnaceEnergyConsumptionRate = EnergyUtils.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private double oxygenCollectorEnergyConsumptionRate = EnergyUtils.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private double refineryEnergyConsumptionRate = EnergyUtils.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private double electricFurnaceEnergyConsumptionRate = EnergyUtils.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private double energyStorageModuleStorageSize = 41666.6666665;
    @Expose
    private double machineEnergyStorageSize = 2500;
    @Expose
    private double oxygenCompressorEnergyConsumptionRate = EnergyUtils.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private double oxygenDecompressorEnergyConsumptionRate = EnergyUtils.Values.T1_MACHINE_ENERGY_USAGE;

    @Override
    public boolean isDebugLogEnabled() {
        return this.debugLog;
    }

    @Override
    public void setDebugLog(boolean flag) {
        this.debugLog = flag;
    }

    @Override
    public double wireTransferLimit() {
        return wireMaxTransferPerTick;
    }

    @Override
    public void setWireTransferLimit(double amount) {
        this.wireMaxTransferPerTick = amount;
    }

    @Override
    public double heavyWireTransferLimit() {
        return heavyWireMaxTransferPerTick;
    }

    @Override
    public void setHeavyWireTransferLimit(double amount) {
        this.heavyWireMaxTransferPerTick = amount;
    }

    @Override
    public double coalGeneratorEnergyProductionRate() {
        return coalGeneratorEnergyProductionRate;
    }

    @Override
    public void setCoalGeneratorEnergyProductionRate(double amount) {
        this.coalGeneratorEnergyProductionRate = amount;
    }

    @Override
    public double solarPanelEnergyProductionRate() {
        return solarPanelEnergyProductionRate;
    }

    @Override
    public void setSolarPanelEnergyProductionRate(double amount) {
        this.solarPanelEnergyProductionRate = amount;
    }

    @Override
    public double circuitFabricatorEnergyConsumptionRate() {
        return circuitFabricatorEnergyConsumptionRate;
    }

    @Override
    public void setCircuitFabricatorEnergyConsumptionRate(double amount) {
        this.circuitFabricatorEnergyConsumptionRate = amount;
    }

    @Override
    public double electricCompressorEnergyConsumptionRate() {
        return electricCompressorEnergyConsumptionRate;
    }

    @Override
    public void setElectricCompressorEnergyConsumptionRate(double amount) {
        this.electricCompressorEnergyConsumptionRate = amount;
    }

    @Override
    public double electricArcFurnaceEnergyConsumptionRate() {
        return electricArcFurnaceEnergyConsumptionRate;
    }

    @Override
    public void setElectricArcFurnaceEnergyConsumptionRate(double amount) {
        this.electricArcFurnaceEnergyConsumptionRate = amount;
    }

    @Override
    public double oxygenCollectorEnergyConsumptionRate() {
        return oxygenCollectorEnergyConsumptionRate;
    }

    @Override
    public void setOxygenCollectorEnergyConsumptionRate(double amount) {
        this.oxygenCollectorEnergyConsumptionRate = amount;
    }

    @Override
    public double refineryEnergyConsumptionRate() {
        return refineryEnergyConsumptionRate;
    }

    @Override
    public void setRefineryEnergyConsumptionRate(double amount) {
        this.refineryEnergyConsumptionRate = amount;
    }

    @Override
    public double electricFurnaceEnergyConsumptionRate() {
        return electricFurnaceEnergyConsumptionRate;
    }

    @Override
    public void setElectricFurnaceEnergyConsumptionRate(double amount) {
        this.electricFurnaceEnergyConsumptionRate = amount;
    }

    @Override
    public double energyStorageModuleStorageSize() {
        return energyStorageModuleStorageSize;
    }

    @Override
    public void setEnergyStorageModuleStorageSize(double amount) {
        this.energyStorageModuleStorageSize = amount;
    }

    @Override
    public double machineEnergyStorageSize() {
        return machineEnergyStorageSize;
    }

    @Override
    public void setMachineEnergyStorageSize(double amount) {
        this.machineEnergyStorageSize = amount;
    }

    @Override
    public double oxygenCompressorEnergyConsumptionRate() {
        return oxygenCompressorEnergyConsumptionRate;
    }

    @Override
    public void setOxygenCompressorEnergyConsumptionRate(double amount) {
        this.oxygenCompressorEnergyConsumptionRate = amount;
    }

    @Override
    public double oxygenDecompressorEnergyConsumptionRate() {
        return oxygenDecompressorEnergyConsumptionRate;
    }

    @Override
    public void setOxygenDecompressorEnergyConsumptionRate(double amount) {
        this.oxygenDecompressorEnergyConsumptionRate = amount;
    }
}
