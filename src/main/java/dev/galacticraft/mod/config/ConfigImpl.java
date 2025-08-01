/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.config.Config;
import dev.galacticraft.mod.util.Translations;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FloatFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.LongFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
    private long smallOxygenTankCapacity = FluidConstants.BUCKET;
    private long mediumOxygenTankCapacity = 2 * FluidConstants.BUCKET;
    private long largeOxygenTankCapacity = 3 * FluidConstants.BUCKET;
    private long playerOxygenConsumptionRate = 5 * FluidConstants.DROPLET;
    private long wolfOxygenConsumptionRate = 3 * FluidConstants.DROPLET;
    private long catOxygenConsumptionRate = 2 * FluidConstants.DROPLET;
    private long parrotOxygenConsumptionRate = 1 * FluidConstants.DROPLET;
    private boolean cannotEatInNoAtmosphere = true;
    private boolean cannotEatWithMask = true;
    private double bossHealthMultiplier = 1.0;
    private boolean hideAlphaWarning = false;
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
    public boolean isAlphaWarningHidden() {
        return this.hideAlphaWarning;
    }

    public void setAlphaWarningHidden(boolean flag) {
        this.hideAlphaWarning = flag;
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

    public static class ConfigScreen implements ConfigScreenFactory<Screen> {
        public static final ConfigScreen INSTANCE = new ConfigScreen();

        private ConfigScreen() {
        }

        @Override
        public Screen create(Screen parent) {
            ConfigImpl config = (ConfigImpl) Galacticraft.CONFIG;

            ConfigBuilder b = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable(Translations.Config.TITLE))
                    .setSavingRunnable(config::save);

            SubCategoryBuilder dB = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.DEBUG));

            dB.add(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.DEBUG_LOGGING),
                    config.isDebugLogEnabled())
                    .setSaveConsumer(config::setDebugLog)
                    .setDefaultValue(false)
                    .build()
            );

            dB.add(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.HIDE_ALPHA_WARNING),
                    config.isAlphaWarningHidden())
                    .setSaveConsumer(config::setAlphaWarningHidden)
                    .setDefaultValue(false)
                    .build()
            );

            SubCategoryBuilder wires = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.WIRES));

            wires.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.WIRE_ENERGY_TRANSFER_LIMIT),
                    config.wireTransferLimit())
                    .setSaveConsumer(config::setWireTransferLimit)
                    .setDefaultValue(480)
                    .build()
            );

            wires.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT),
                    config.heavyWireTransferLimit())
                    .setSaveConsumer(config::setHeavyWireTransferLimit)
                    .setDefaultValue(1440)
                    .build()
            );

            SubCategoryBuilder machines = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.MACHINES));

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ENERGY_STORAGE_SIZE),
                    config.machineEnergyStorageSize())
                    .setSaveConsumer(config::setMachineEnergyStorageSize)
                    .setDefaultValue(30_000)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE),
                    config.energyStorageModuleStorageSize())
                    .setSaveConsumer(config::setEnergyStorageModuleStorageSize)
                    .setDefaultValue(500_000)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE),
                    config.coalGeneratorEnergyProductionRate())
                    .setSaveConsumer(config::setCoalGeneratorEnergyProductionRate)
                    .setDefaultValue(120)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE),
                    config.solarPanelEnergyProductionRate())
                    .setSaveConsumer(config::setSolarPanelEnergyProductionRate)
                    .setDefaultValue(44)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE),
                    config.circuitFabricatorEnergyConsumptionRate())
                    .setSaveConsumer(config::setCircuitFabricatorEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.electricCompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setElectricCompressorEnergyConsumptionRate)
                    .setDefaultValue(75)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE),
                    config.electricFurnaceEnergyConsumptionRate())
                    .setSaveConsumer(config::setElectricFurnaceEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ELECTRIC_ARC_FURNACE_ENERGY_CONSUMPTION_RATE),
                    config.electricArcFurnaceEnergyConsumptionRate())
                    .setSaveConsumer(config::setElectricArcFurnaceEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new FloatFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ELECTRIC_ARC_FURNACE_BONUS_CHANCE),
                    config.electricArcFurnaceBonusChance())
                    .setSaveConsumer(config::setElectricArcFurnaceBonusChance)
                    .setDefaultValue(0.25F)
                    .setMin(0.0F)
                    .setMax(1.0F)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenCollectorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenCollectorEnergyConsumptionRate)
                    .setDefaultValue(10)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenCompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenCompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenDecompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenDecompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.OXYGEN_SEALER_ENERGY_CONSUMPTION_RATE),
                    config.oxygenSealerEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenSealerEnergyConsumptionRate)
                    .setDefaultValue(10)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.OXYGEN_SEALER_OXYGEN_CONSUMPTION_RATE),
                    config.oxygenSealerOxygenConsumptionRate())
                    .setSaveConsumer(config::setOxygenSealerOxygenConsumptionRate)
                    .setDefaultValue(1000)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.MAX_SEALING_POWER),
                    config.maxSealingPower())
                    .setSaveConsumer(config::setMaxSealingPower)
                    .setDefaultValue(1024)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.REFINERY_ENERGY_CONSUMPTION_RATE),
                    config.refineryEnergyConsumptionRate())
                    .setSaveConsumer(config::setRefineryEnergyConsumptionRate)
                    .setDefaultValue(60)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.FUEL_LOADER_ENERGY_CONSUMPTION_RATE),
                    config.fuelLoaderEnergyConsumptionRate())
                    .setSaveConsumer(config::setFuelLoaderEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.FOOD_CANNER_ENERGY_CONSUMPTION_RATE),
                    config.foodCannerEnergyConsumptionRate())
                    .setSaveConsumer(config::setFoodCannerEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenDecompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenDecompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            SubCategoryBuilder skybox = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.SKYBOX));


            SubCategoryBuilder lifeSupport = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.LIFE_SUPPORT));

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.SMALL_OXYGEN_TANK_CAPACITY),
                    config.smallOxygenTankCapacity())
                    .setSaveConsumer(config::setSmallOxygenTankCapacity)
                    .setDefaultValue(FluidConstants.BUCKET)
                    .setMin(0)
                    .setMax(Long.MAX_VALUE)
                    .build()
            );

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.MEDIUM_OXYGEN_TANK_CAPACITY),
                    config.mediumOxygenTankCapacity())
                    .setSaveConsumer(config::setMediumOxygenTankCapacity)
                    .setDefaultValue(2 * FluidConstants.BUCKET)
                    .setMin(0)
                    .setMax(Long.MAX_VALUE)
                    .build()
            );

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.LARGE_OXYGEN_TANK_CAPACITY),
                    config.largeOxygenTankCapacity())
                    .setSaveConsumer(config::setLargeOxygenTankCapacity)
                    .setDefaultValue(3 * FluidConstants.BUCKET)
                    .setMin(0)
                    .setMax(Long.MAX_VALUE)
                    .build()
            );

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.PLAYER_OXYGEN_CONSUMPTION_RATE),
                    config.playerOxygenConsumptionRate())
                    .setSaveConsumer(config::setPlayerOxygenConsumptionRate)
                    .setDefaultValue(5 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.WOLF_OXYGEN_CONSUMPTION_RATE),
                    config.wolfOxygenConsumptionRate())
                    .setSaveConsumer(config::setWolfOxygenConsumptionRate)
                    .setDefaultValue(3 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.CAT_OXYGEN_CONSUMPTION_RATE),
                    config.catOxygenConsumptionRate())
                    .setSaveConsumer(config::setCatOxygenConsumptionRate)
                    .setDefaultValue(2 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.PARROT_OXYGEN_CONSUMPTION_RATE),
                    config.parrotOxygenConsumptionRate())
                    .setSaveConsumer(config::setParrotOxygenConsumptionRate)
                    .setDefaultValue(1 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.add(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.CANNOT_EAT_IN_NO_ATMOSPHERE),
                    config.cannotEatInNoAtmosphere())
                    .setSaveConsumer(config::setCannotEatInNoAtmosphere)
                    .setDefaultValue(true)
                    .build()
            );

            lifeSupport.add(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.CANNOT_EAT_WITH_MASK),
                    config.cannotEatWithMask())
                    .setSaveConsumer(config::setCannotEatWithMask)
                    .setDefaultValue(true)
                    .build()
            );

            SubCategoryBuilder difficulty = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.DIFFICULTY));

            difficulty.add(new DoubleFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.BOSS_HEALTH_MODIFIER),
                    config.bossHealthMultiplier())
                    .setTooltip(Component.translatable(Translations.Config.BOSS_HEALTH_MODIFIER_DESC))
                    .setSaveConsumer(config::setBossHealthMultiplier)
                    .setDefaultValue(1)
                    .build()
            );

            b.getOrCreateCategory(Component.translatable(Translations.Config.DEBUG)).addEntry(dB.build());
            b.getOrCreateCategory(Component.translatable(Translations.Config.ENERGY)).addEntry(wires.build()).addEntry(machines.build());

            SubCategoryBuilder commands = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.COMMANDS));

            commands.add(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ENABLE_GC_HOUSTON),
                    config.enableGcHouston())
                    .setSaveConsumer(config::setEnableGcHouston)
                    .setDefaultValue(true)
                    .build()
            );

            return b.build();
        }
    }
}
