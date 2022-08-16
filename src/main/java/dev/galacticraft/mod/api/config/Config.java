/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.api.config;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface Config {

    static Config getInstance() {
        return ConfigManager.getInstance().get();
    }

    boolean isAlphaWarningHidden();

    void setAlphaWarningHidden(boolean flag);

    void setMoreMulticolorStars(boolean flag);

    boolean isDebugLogEnabled();

    void setDebugLog(boolean flag);

    long wireTransferLimit();

    void setWireTransferLimit(long amount);

    long heavyWireTransferLimit();

    void setHeavyWireTransferLimit(long amount);

    long coalGeneratorEnergyProductionRate();

    void setCoalGeneratorEnergyProductionRate(long amount);

    long solarPanelEnergyProductionRate();

    void setSolarPanelEnergyProductionRate(long amount);

    long circuitFabricatorEnergyConsumptionRate();

    void setCircuitFabricatorEnergyConsumptionRate(long amount);

    long electricCompressorEnergyConsumptionRate();

    void setElectricCompressorEnergyConsumptionRate(long amount);

    long electricArcFurnaceEnergyConsumptionRate();

    void setElectricArcFurnaceEnergyConsumptionRate(long amount);

    long oxygenCollectorEnergyConsumptionRate();

    void setOxygenCollectorEnergyConsumptionRate(long amount);

    long refineryEnergyConsumptionRate();

    void setRefineryEnergyConsumptionRate(long amount);

    long electricFurnaceEnergyConsumptionRate();

    void setElectricFurnaceEnergyConsumptionRate(long amount);

    int rocketAssemblerEnergyConsumptionRate();

    void setRocketAssemblerEnergyConsumptionRate(int amount);

    long energyStorageModuleStorageSize();

    void setEnergyStorageModuleStorageSize(long amount);

    long machineEnergyStorageSize();

    void setMachineEnergyStorageSize(long amount);

    long oxygenCompressorEnergyConsumptionRate();

    void setOxygenCompressorEnergyConsumptionRate(long amount);

    long oxygenDecompressorEnergyConsumptionRate();

    void setOxygenDecompressorEnergyConsumptionRate(long amount);

    int rocketAssemblerProcessTime();

    void setRocketAssemblerProcessTime(int amount);
}
