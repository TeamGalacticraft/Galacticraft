/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.google.gson.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.config.Config;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ConfigImpl implements Config {
    private transient final Gson gson;
    private transient final File file;

    private boolean debugLog = false;
    private long wireMaxTransferPerTick = 128;
    private long heavyWireMaxTransferPerTick = 256;
    private long machineEnergyStorageSize = 30_000;
    private long energyStorageModuleStorageSize = 300_000;
    private long coalGeneratorEnergyProductionRate = 120; // /t
    private long solarPanelEnergyProductionRate = 44;
    private long circuitFabricatorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long electricCompressorEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long electricFurnaceEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long electricArcFurnaceEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private float electricArcFurnaceBonusChance = 0.25F;
    private long oxygenCollectorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long oxygenCompressorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long oxygenDecompressorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long oxygenSealerEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long oxygenSealerOxygenConsumptionRate = 1000;
    private long maxSealingPower = 1024;
    private long refineryEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long fuelLoaderEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long foodCannerEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long fluidCanisterCapacity = FluidConstants.BUCKET;
    private long smallOxygenTankCapacity = FluidConstants.BUCKET;
    private long mediumOxygenTankCapacity = 2 * FluidConstants.BUCKET;
    private long largeOxygenTankCapacity = 3 * FluidConstants.BUCKET;
    private long playerOxygenConsumptionRate = 5 * FluidConstants.DROPLET;
    private long wolfOxygenConsumptionRate = 3 * FluidConstants.DROPLET;
    private long catOxygenConsumptionRate = 2 * FluidConstants.DROPLET;
    private long parrotOxygenConsumptionRate = 1 * FluidConstants.DROPLET;
    private boolean cannotEatInNoAtmosphere = true;
    private boolean cannotEatWithMask = true;
    private float meteorSpawnMultiplier = 1.0f;
    private double bossHealthMultiplier = 1.0;
    private boolean enableGcHouston = true;

    public ConfigImpl(File file) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(ConfigImpl.class, (InstanceCreator<ConfigImpl>) type -> this)
                .create();
        this.file = file;
        this.load();
    }

    @Override
    public boolean isDebugLogEnabled() {
        return this.debugLog;
    }

    public void setDebugLog(boolean flag) {
        this.debugLog = flag;
    }

    @Override
    public long wireTransferLimit() {
        return wireMaxTransferPerTick;
    }

    public void setWireTransferLimit(long amount) {
        this.wireMaxTransferPerTick = amount;
    }

    @Override
    public long heavyWireTransferLimit() {
        return heavyWireMaxTransferPerTick;
    }

    public void setHeavyWireTransferLimit(long amount) {
        this.heavyWireMaxTransferPerTick = amount;
    }

    @Override
    public long machineEnergyStorageSize() {
        return machineEnergyStorageSize;
    }

    public void setMachineEnergyStorageSize(long amount) {
        this.machineEnergyStorageSize = amount;
    }

    @Override
    public long energyStorageModuleStorageSize() {
        return energyStorageModuleStorageSize;
    }

    public void setEnergyStorageModuleStorageSize(long amount) {
        this.energyStorageModuleStorageSize = amount;
    }

    @Override
    public long coalGeneratorEnergyProductionRate() {
        return coalGeneratorEnergyProductionRate;
    }

    public void setCoalGeneratorEnergyProductionRate(long amount) {
        this.coalGeneratorEnergyProductionRate = amount;
    }

    @Override
    public long solarPanelEnergyProductionRate() {
        return solarPanelEnergyProductionRate;
    }

    public void setSolarPanelEnergyProductionRate(long amount) {
        this.solarPanelEnergyProductionRate = amount;
    }

    @Override
    public long circuitFabricatorEnergyConsumptionRate() {
        return circuitFabricatorEnergyConsumptionRate;
    }

    public void setCircuitFabricatorEnergyConsumptionRate(long amount) {
        this.circuitFabricatorEnergyConsumptionRate = amount;
    }

    @Override
    public long electricCompressorEnergyConsumptionRate() {
        return electricCompressorEnergyConsumptionRate;
    }

    public void setElectricCompressorEnergyConsumptionRate(long amount) {
        this.electricCompressorEnergyConsumptionRate = amount;
    }

    @Override
    public long electricFurnaceEnergyConsumptionRate() {
        return electricFurnaceEnergyConsumptionRate;
    }

    public void setElectricFurnaceEnergyConsumptionRate(long amount) {
        this.electricFurnaceEnergyConsumptionRate = amount;
    }

    @Override
    public long electricArcFurnaceEnergyConsumptionRate() {
        return electricArcFurnaceEnergyConsumptionRate;
    }

    public void setElectricArcFurnaceEnergyConsumptionRate(long amount) {
        this.electricArcFurnaceEnergyConsumptionRate = amount;
    }

    @Override
    public float electricArcFurnaceBonusChance() {
        return electricArcFurnaceBonusChance;
    }

    public void setElectricArcFurnaceBonusChance(float prob) {
        this.electricArcFurnaceBonusChance = prob;
    }

    @Override
    public long oxygenCollectorEnergyConsumptionRate() {
        return oxygenCollectorEnergyConsumptionRate;
    }

    public void setOxygenCollectorEnergyConsumptionRate(long amount) {
        this.oxygenCollectorEnergyConsumptionRate = amount;
    }

    @Override
    public long oxygenCompressorEnergyConsumptionRate() {
        return oxygenCompressorEnergyConsumptionRate;
    }

    public void setOxygenCompressorEnergyConsumptionRate(long amount) {
        this.oxygenCompressorEnergyConsumptionRate = amount;
    }

    @Override
    public long oxygenDecompressorEnergyConsumptionRate() {
        return oxygenDecompressorEnergyConsumptionRate;
    }

    public void setOxygenDecompressorEnergyConsumptionRate(long amount) {
        this.oxygenDecompressorEnergyConsumptionRate = amount;
    }

    @Override
    public long oxygenSealerEnergyConsumptionRate() {
        return oxygenSealerEnergyConsumptionRate;
    }

    public void setOxygenSealerEnergyConsumptionRate(long amount) {
        this.oxygenSealerEnergyConsumptionRate = amount;
    }

    @Override
    public long oxygenSealerOxygenConsumptionRate() {
        return oxygenSealerOxygenConsumptionRate;
    }

    public void setOxygenSealerOxygenConsumptionRate(long amount) {
        this.oxygenSealerOxygenConsumptionRate = amount;
    }

    @Override
    public long maxSealingPower() {
        return maxSealingPower;
    }

    public void setMaxSealingPower(long amount) {
        this.maxSealingPower = amount;
    }

    @Override
    public long refineryEnergyConsumptionRate() {
        return refineryEnergyConsumptionRate;
    }

    public void setRefineryEnergyConsumptionRate(long amount) {
        this.refineryEnergyConsumptionRate = amount;
    }

    @Override
    public long fuelLoaderEnergyConsumptionRate() {
        return fuelLoaderEnergyConsumptionRate;
    }

    public void setFuelLoaderEnergyConsumptionRate(long amount) {
        this.fuelLoaderEnergyConsumptionRate = amount;
    }

    @Override
    public long foodCannerEnergyConsumptionRate() {
        return foodCannerEnergyConsumptionRate;
    }

    public void setFoodCannerEnergyConsumptionRate(long amount) {
        this.foodCannerEnergyConsumptionRate = amount;
    }

    @Override
    public long fluidCanisterCapacity() {
        return this.fluidCanisterCapacity;
    }

    public void setFluidCanisterCapacity(long capacity) {
        this.fluidCanisterCapacity = capacity;
    }

    @Override
    public long smallOxygenTankCapacity() {
        return this.smallOxygenTankCapacity;
    }

    public void setSmallOxygenTankCapacity(long capacity) {
        this.smallOxygenTankCapacity = capacity;
    }

    @Override
    public long mediumOxygenTankCapacity() {
        return this.mediumOxygenTankCapacity;
    }

    public void setMediumOxygenTankCapacity(long capacity) {
        this.mediumOxygenTankCapacity = capacity;
    }

    @Override
    public long largeOxygenTankCapacity() {
        return this.largeOxygenTankCapacity;
    }

    public void setLargeOxygenTankCapacity(long capacity) {
        this.largeOxygenTankCapacity = capacity;
    }

    @Override
    public long playerOxygenConsumptionRate() {
        return this.playerOxygenConsumptionRate;
    }

    public void setPlayerOxygenConsumptionRate(long amount) {
        this.playerOxygenConsumptionRate = amount;
    }

    @Override
    public long wolfOxygenConsumptionRate() {
        return this.wolfOxygenConsumptionRate;
    }

    public void setWolfOxygenConsumptionRate(long amount) {
        this.wolfOxygenConsumptionRate = amount;
    }

    @Override
    public long catOxygenConsumptionRate() {
        return this.catOxygenConsumptionRate;
    }

    public void setCatOxygenConsumptionRate(long amount) {
        this.catOxygenConsumptionRate = amount;
    }

    @Override
    public long parrotOxygenConsumptionRate() {
        return this.parrotOxygenConsumptionRate;
    }

    public void setParrotOxygenConsumptionRate(long amount) {
        this.parrotOxygenConsumptionRate = amount;
    }

    @Override
    public boolean cannotEatInNoAtmosphere() {
        return this.cannotEatInNoAtmosphere;
    }

    public void setCannotEatInNoAtmosphere(boolean cannotEatInNoAtmosphere) {
        this.cannotEatInNoAtmosphere = cannotEatInNoAtmosphere;
    }

    @Override
    public boolean cannotEatWithMask() {
        return this.cannotEatWithMask;
    }

    public void setCannotEatWithMask(boolean cannotEatWithMask) {
        this.cannotEatWithMask = cannotEatWithMask;
    }

    @Override
    public float meteorSpawnMultiplier() {
        return this.meteorSpawnMultiplier;
    }

    public void setMeteorSpawnMultiplier(float meteorSpawnMultiplier) {
        this.meteorSpawnMultiplier = meteorSpawnMultiplier;
    }

    @Override
    public double bossHealthMultiplier() {
        return this.bossHealthMultiplier;
    }

    public void setBossHealthMultiplier(double bossHealthMultiplier) {
        this.bossHealthMultiplier = bossHealthMultiplier;
    }

    @Override
    public boolean enableGcHouston() {
        return this.enableGcHouston;
    }

    public void setEnableGcHouston(boolean enableGcHouston) {
        this.enableGcHouston = enableGcHouston;
    }

    public void load() {
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            Constant.LOGGER.info("Failed to find config file, creating one.");
            this.save();
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            this.gson.fromJson(reader, ConfigImpl.class);
            this.save();
        } catch (IOException | JsonSyntaxException e) {
            Constant.LOGGER.error("Failed to load config.", e);
        }
    }

    @Override
    public void save() {
        try (FileWriter writer = new FileWriter(this.file, StandardCharsets.UTF_8)) {
            this.gson.toJson(this, writer);
        } catch (IOException e) {
            Constant.LOGGER.error("Failed to save config.", e);
        }
    }
}
