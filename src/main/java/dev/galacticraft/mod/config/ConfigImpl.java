/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.config.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConfigImpl implements Config {
    private transient final Gson gson;
    private transient final File file;
    private boolean debugLog = false;
    private long wireMaxTransferPerTick = 128;
    private long heavyWireMaxTransferPerTick = 256;
    private long coalGeneratorEnergyProductionRate = 120; // /t
    private long solarPanelEnergyProductionRate = 44;
    private long circuitFabricatorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long electricCompressorEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long electricArcFurnaceEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long oxygenCollectorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long refineryEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long electricFurnaceEnergyConsumptionRate = Constant.Energy.T2_MACHINE_ENERGY_USAGE;
    private long energyStorageModuleStorageSize = 300_000;
    private long machineEnergyStorageSize = 30_000;
    private long oxygenCompressorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long oxygenDecompressorEnergyConsumptionRate = Constant.Energy.T1_MACHINE_ENERGY_USAGE;
    private long playerOxygenConsumptionRate = FluidConstants.DROPLET;
    private boolean hide_alpha_warning = false;

    public ConfigImpl(File file) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(ConfigImpl.class, (InstanceCreator<ConfigImpl>) type -> this)
                .create();
        this.file = file;
        this.load();
    }

    @Override
    public boolean isAlphaWarningHidden() {
        return this.hide_alpha_warning;
    }

    public void setAlphaWarningHidden(boolean flag) {
        this.hide_alpha_warning = flag;
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
    public long electricArcFurnaceEnergyConsumptionRate() {
        return electricArcFurnaceEnergyConsumptionRate;
    }

    public void setElectricArcFurnaceEnergyConsumptionRate(long amount) {
        this.electricArcFurnaceEnergyConsumptionRate = amount;
    }

    @Override
    public long oxygenCollectorEnergyConsumptionRate() {
        return oxygenCollectorEnergyConsumptionRate;
    }

    public void setOxygenCollectorEnergyConsumptionRate(long amount) {
        this.oxygenCollectorEnergyConsumptionRate = amount;
    }

    @Override
    public long refineryEnergyConsumptionRate() {
        return refineryEnergyConsumptionRate;
    }

    public void setRefineryEnergyConsumptionRate(long amount) {
        this.refineryEnergyConsumptionRate = amount;
    }

    @Override
    public long electricFurnaceEnergyConsumptionRate() {
        return electricFurnaceEnergyConsumptionRate;
    }

    public void setElectricFurnaceEnergyConsumptionRate(long amount) {
        this.electricFurnaceEnergyConsumptionRate = amount;
    }

    @Override
    public long energyStorageModuleStorageSize() {
        return energyStorageModuleStorageSize;
    }

    public void setEnergyStorageModuleStorageSize(long amount) {
        this.energyStorageModuleStorageSize = amount;
    }

    @Override
    public long machineEnergyStorageSize() {
        return machineEnergyStorageSize;
    }

    public void setMachineEnergyStorageSize(long amount) {
        this.machineEnergyStorageSize = amount;
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
    public long playerOxygenConsuptionRate() {
        return this.playerOxygenConsumptionRate;
    }

    public void setPlayerOxygenConsumptionRate(long amount) {
        this.playerOxygenConsumptionRate = amount;
    }

    public void load() {
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            Constant.LOGGER.info("Failed to find config file, creating one.");
            this.save();
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            this.gson.fromJson(reader, ConfigImpl.class);
        } catch (IOException e) {
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
                    .setTitle(Component.translatable(Constant.Config.TITLE))
                    .setSavingRunnable(config::save);

            SubCategoryBuilder dB = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.DEBUG));

            dB.add(new BooleanToggleBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.DEBUG_LOGGING),
                    config.isDebugLogEnabled())
                    .setSaveConsumer(config::setDebugLog)
                    .setDefaultValue(false)
                    .build()
            );

            dB.add(new BooleanToggleBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.HIDE_ALPHA_WARNING),
                    config.isAlphaWarningHidden())
                    .setSaveConsumer(config::setAlphaWarningHidden)
                    .setDefaultValue(false)
                    .build()
            );

            SubCategoryBuilder wires = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.WIRES));

            wires.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.WIRE_ENERGY_TRANSFER_LIMIT),
                    config.wireTransferLimit())
                    .setSaveConsumer(config::setWireTransferLimit)
                    .setDefaultValue(480)
                    .build()
            );

            wires.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT),
                    config.heavyWireTransferLimit())
                    .setSaveConsumer(config::setHeavyWireTransferLimit)
                    .setDefaultValue(1440)
                    .build()
            );

            SubCategoryBuilder machines = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.MACHINES));

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE),
                    config.coalGeneratorEnergyProductionRate())
                    .setSaveConsumer(config::setCoalGeneratorEnergyProductionRate)
                    .setDefaultValue(120)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE),
                    config.solarPanelEnergyProductionRate())
                    .setSaveConsumer(config::setSolarPanelEnergyProductionRate)
                    .setDefaultValue(44)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE),
                    config.circuitFabricatorEnergyConsumptionRate())
                    .setSaveConsumer(config::setCircuitFabricatorEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.electricCompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setElectricCompressorEnergyConsumptionRate)
                    .setDefaultValue(75)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenCollectorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenCollectorEnergyConsumptionRate)
                    .setDefaultValue(10)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.REFINERY_ENERGY_CONSUMPTION_RATE),
                    config.refineryEnergyConsumptionRate())
                    .setSaveConsumer(config::setRefineryEnergyConsumptionRate)
                    .setDefaultValue(60)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE),
                    config.electricFurnaceEnergyConsumptionRate())
                    .setSaveConsumer(config::setElectricFurnaceEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE),
                    config.energyStorageModuleStorageSize())
                    .setSaveConsumer(config::setEnergyStorageModuleStorageSize)
                    .setDefaultValue(500_000)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.ENERGY_STORAGE_SIZE),
                    config.machineEnergyStorageSize())
                    .setSaveConsumer(config::setMachineEnergyStorageSize)
                    .setDefaultValue(30_000)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenCompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenCompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenDecompressorEnergyConsumptionRate())
                    .setSaveConsumer(config::setOxygenDecompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            SubCategoryBuilder skybox = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.SKYBOX));

            SubCategoryBuilder lifeSupport = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.PLAYER_LIFE_SUPPORT));

            // TODO: If set to 0, disable the player oxygen system
            lifeSupport.add(new LongFieldBuilder(
                    Component.translatable(Constant.Config.RESET),
                    Component.translatable(Constant.Config.PLAYER_OXYGEN_CONSUMPTION_RATE),
                    config.playerOxygenConsuptionRate())
                    .setSaveConsumer(config::setPlayerOxygenConsumptionRate)
                    .setDefaultValue(1)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            b.getOrCreateCategory(Component.translatable(Constant.Config.DEBUG)).addEntry(dB.build());
            b.getOrCreateCategory(Component.translatable(Constant.Config.ENERGY)).addEntry(wires.build()).addEntry(machines.build());
            b.getOrCreateCategory(Component.translatable(Constant.Config.CLIENT)).addEntry(skybox.build());
            b.getOrCreateCategory(Component.translatable(Constant.Config.PLAYER)).addEntry(lifeSupport.build());

            return b.build();
        }
    }
}
