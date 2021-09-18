/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.config;

import com.google.gson.annotations.Expose;
import dev.galacticraft.mod.api.config.Config;
import dev.galacticraft.mod.util.EnergyUtil;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConfigImpl implements Config {
    @Expose
    private boolean debugLog = false;
    @Expose
    private int wireMaxTransferPerTick = 128;
    @Expose
    private int heavyWireMaxTransferPerTick = 256;
    @Expose
    private int coalGeneratorEnergyProductionRate = 120; // /t
    @Expose
    private int solarPanelEnergyProductionRate = 44;
    @Expose
    private int circuitFabricatorEnergyConsumptionRate = EnergyUtil.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private int electricCompressorEnergyConsumptionRate = EnergyUtil.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private int electricArcFurnaceEnergyConsumptionRate = EnergyUtil.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private int oxygenCollectorEnergyConsumptionRate = EnergyUtil.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private int refineryEnergyConsumptionRate = EnergyUtil.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private int electricFurnaceEnergyConsumptionRate = EnergyUtil.Values.T2_MACHINE_ENERGY_USAGE;
    @Expose
    private int energyStorageModuleStorageSize = 300_000;
    @Expose
    private int machineEnergyStorageSize = 30_000;
    @Expose
    private int oxygenCompressorEnergyConsumptionRate = EnergyUtil.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private int oxygenDecompressorEnergyConsumptionRate = EnergyUtil.Values.T1_MACHINE_ENERGY_USAGE;
    @Expose
    private boolean hide_alpha_warning = false;
    @Expose
    private boolean moreMulticolorStars = false;

    @Override
    public boolean isAlphaWarningHidden() {
        return this.hide_alpha_warning;
    }

    @Override
    public void setAlphaWarningHidden(boolean flag) {
        this.hide_alpha_warning = flag;
    }

    @Override
    public boolean areMoreMulticoloredStarsEnabled() {
        return this.moreMulticolorStars;
    }

    @Override
    public void setMoreMulticolorStars(boolean flag) {
        this.moreMulticolorStars = flag;
    }

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
    public int electricArcFurnaceEnergyConsumptionRate() {
        return electricArcFurnaceEnergyConsumptionRate;
    }

    @Override
    public void setElectricArcFurnaceEnergyConsumptionRate(int amount) {
        this.electricArcFurnaceEnergyConsumptionRate = amount;
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
    public int electricFurnaceEnergyConsumptionRate() {
        return electricFurnaceEnergyConsumptionRate;
    }

    @Override
    public void setElectricFurnaceEnergyConsumptionRate(int amount) {
        this.electricFurnaceEnergyConsumptionRate = amount;
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
