/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.config.Config;
import dev.galacticraft.mod.api.config.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.LongFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConfigManagerImpl implements ConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "galacticraft/config.json");
    private Config config = new ConfigImpl();

    public ConfigManagerImpl() {
        this.load();
    }

    @Override
    public void save() {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(this.file, this.gson.toJson(this.config), Charset.defaultCharset());
        } catch (IOException e) {
            Constant.LOGGER.error("Failed to save config.", e);
        }
    }

    @Override
    public void load() {
        try {
            this.file.getParentFile().mkdirs();
            if (!this.file.exists()) {
                Constant.LOGGER.info("Failed to find config file, creating one.");
                this.save();
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get(this.file.getPath()));
                this.config = this.gson.fromJson(new String(bytes, Charset.defaultCharset()), ConfigImpl.class);
            }
        } catch (IOException e) {
            Constant.LOGGER.error("Failed to load config.", e);
        }
    }

    @Override
    public Config get() {
        return this.config;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Screen getScreen(Screen parent) {
        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable(Constant.Config.TITLE))
                .setSavingRunnable(this::save);

        SubCategoryBuilder dB = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.DEBUG));

        dB.add(new BooleanToggleBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.DEBUG_LOGGING),
                this.config.isDebugLogEnabled())
                .setSaveConsumer(flag -> this.config.setDebugLog(flag))
                .setDefaultValue(false)
                .build()
        );

        dB.add(new BooleanToggleBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.HIDE_ALPHA_WARNING),
                this.config.isAlphaWarningHidden())
                .setSaveConsumer(flag -> this.config.setAlphaWarningHidden(flag))
                .setDefaultValue(false)
                .build()
        );

        SubCategoryBuilder wires = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.WIRES));

        wires.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.WIRE_ENERGY_TRANSFER_LIMIT),
                this.config.wireTransferLimit())
                .setSaveConsumer(this.config::setWireTransferLimit)
                .setDefaultValue(480)
                .build()
        );

        wires.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT),
                this.config.heavyWireTransferLimit())
                .setSaveConsumer(this.config::setHeavyWireTransferLimit)
                .setDefaultValue(1440)
                .build()
        );

        SubCategoryBuilder machines = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.MACHINES));

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE),
                this.config.coalGeneratorEnergyProductionRate())
                .setSaveConsumer(this.config::setCoalGeneratorEnergyProductionRate)
                .setDefaultValue(120)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE),
                this.config.solarPanelEnergyProductionRate())
                .setSaveConsumer(this.config::setSolarPanelEnergyProductionRate)
                .setDefaultValue(44)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE),
                this.config.circuitFabricatorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setCircuitFabricatorEnergyConsumptionRate)
                .setDefaultValue(20)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                this.config.electricCompressorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setElectricCompressorEnergyConsumptionRate)
                .setDefaultValue(75)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE),
                this.config.oxygenCollectorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setOxygenCollectorEnergyConsumptionRate)
                .setDefaultValue(10)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.REFINERY_ENERGY_CONSUMPTION_RATE),
                this.config.refineryEnergyConsumptionRate())
                .setSaveConsumer(this.config::setRefineryEnergyConsumptionRate)
                .setDefaultValue(60)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE),
                this.config.electricFurnaceEnergyConsumptionRate())
                .setSaveConsumer(this.config::setElectricFurnaceEnergyConsumptionRate)
                .setDefaultValue(20)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE),
                this.config.energyStorageModuleStorageSize())
                .setSaveConsumer(this.config::setEnergyStorageModuleStorageSize)
                .setDefaultValue(500_000)
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.ENERGY_STORAGE_SIZE),
                this.config.machineEnergyStorageSize())
                .setSaveConsumer(this.config::setMachineEnergyStorageSize)
                .setDefaultValue(30_000)
                .requireRestart()
                .build()
        );
        
        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                this.config.oxygenCompressorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setOxygenCompressorEnergyConsumptionRate)
                .setDefaultValue(15)
                .requireRestart()
                .build()
        );

        machines.add(new LongFieldBuilder(
                Component.translatable(Constant.Config.RESET),
                Component.translatable(Constant.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE),
                this.config.oxygenDecompressorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setOxygenDecompressorEnergyConsumptionRate)
                .setDefaultValue(15)
                .requireRestart()
                .build()
        );

        SubCategoryBuilder skybox = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Constant.Config.SKYBOX));

        b.getOrCreateCategory(Component.translatable(Constant.Config.DEBUG)).addEntry(dB.build());
        b.getOrCreateCategory(Component.translatable(Constant.Config.ENERGY)).addEntry(wires.build()).addEntry(machines.build());
        b.getOrCreateCategory(Component.translatable(Constant.Config.CLIENT)).addEntry(skybox.build());

        return b.build();
    }
}
