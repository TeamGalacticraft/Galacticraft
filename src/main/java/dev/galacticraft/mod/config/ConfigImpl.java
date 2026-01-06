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
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FloatFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.LongFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Function;

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
    private boolean squareCannedFood = false;
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
    private boolean hideAlphaWarning = false;
    private boolean enableGcHouston = true;
    private boolean enableCreativeGearInv = true;

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
    public boolean squareCannedFood() {
        return this.squareCannedFood;
    }

    public void setSquareCannedFood(boolean squareCannedFood) {
        boolean reload = this.squareCannedFood != squareCannedFood;
        this.squareCannedFood = squareCannedFood;
        if (reload) {
            Constant.LOGGER.info("Reload resource packs");
            Minecraft.getInstance().reloadResourcePacks();
        }
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

    @Override
    public boolean enableCreativeGearInv() {
        return this.enableCreativeGearInv;
    }

    public void setCreativeGearInv(boolean enableCreativeGearInv) {
        this.enableCreativeGearInv = enableCreativeGearInv;
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

            Font font = Minecraft.getInstance().font;
            final int maxLabelWidth = Math.max(80, Minecraft.getInstance().getWindow().getGuiScaledWidth() - 240);;

            // Use for normal Labels under a category.
            Function<String, Component> label =
                    key -> ellipsize(Component.translatable(key), font, maxLabelWidth);

            // Use for labels under a subcategory.
            Function<String, Component> labelSub =
                    key -> ellipsize(Component.translatable(key), font, maxLabelWidth - 15);

            // Use for tooltips with description e.g. (name, description)
            BiFunction<String, String, Component> tooltipWithDesc =
                    (id, optDescId) -> buildTooltip(
                            Component.translatable(id),
                            font,
                            maxLabelWidth,
                            (optDescId != null && !optDescId.isEmpty())
                                    ? Component.translatable(optDescId)
                                    : null
                    );

            // Use for configs with no description e.g. (name)
            Function<String, Component> tooltipSingular =
                    id -> tooltipWithDesc.apply(id, null);

            // Same but for subcategories
            BiFunction<String, String, Component> tooltipWithDescSub =
                    (id, optDescId) -> buildTooltip(
                            Component.translatable(id),
                            font,
                            maxLabelWidth - 15,
                            (optDescId != null && !optDescId.isEmpty())
                                    ? Component.translatable(optDescId)
                                    : null
                    );

            // Same but for subcategories
            Function<String, Component> tooltipSingularSub =
                    id -> tooltipWithDescSub.apply(id, null);

            // --- DEBUG CONFIG ---
            ConfigCategory dB = b.getOrCreateCategory(Component.translatable(Translations.Config.DEBUG));

            dB.addEntry(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.DEBUG_LOGGING),
                    config.isDebugLogEnabled())
                    .setTooltip(tooltipSingular.apply(Translations.Config.RESET))
                    .setSaveConsumer(config::setDebugLog)
                    .setDefaultValue(false)
                    .build()
            );

            dB.addEntry(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.HIDE_ALPHA_WARNING),
                    config.isAlphaWarningHidden())
                    .setTooltip(tooltipSingular.apply(Translations.Config.HIDE_ALPHA_WARNING))
                    .setSaveConsumer(config::setAlphaWarningHidden)
                    .setDefaultValue(false)
                    .build()
            );

            // --- WIRES CONFIG ---

            SubCategoryBuilder wires = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.WIRES));

            wires.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.WIRE_ENERGY_TRANSFER_LIMIT),
                    config.wireTransferLimit())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.WIRE_ENERGY_TRANSFER_LIMIT))
                    .setSaveConsumer(config::setWireTransferLimit)
                    .setDefaultValue(480)
                    .build()
            );

            wires.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT),
                    config.heavyWireTransferLimit())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT))
                    .setSaveConsumer(config::setHeavyWireTransferLimit)
                    .setDefaultValue(1440)
                    .build()
            );

            // --- MACHINES CONFIG ---

            SubCategoryBuilder machines = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.MACHINES));

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.ENERGY_STORAGE_SIZE),
                    config.machineEnergyStorageSize())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.ENERGY_STORAGE_SIZE))
                    .setSaveConsumer(config::setMachineEnergyStorageSize)
                    .setDefaultValue(30_000)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE),
                    config.energyStorageModuleStorageSize())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE))
                    .setSaveConsumer(config::setEnergyStorageModuleStorageSize)
                    .setDefaultValue(500_000)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE),
                    config.coalGeneratorEnergyProductionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE))
                    .setSaveConsumer(config::setCoalGeneratorEnergyProductionRate)
                    .setDefaultValue(120)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE),
                    config.solarPanelEnergyProductionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE))
                    .setSaveConsumer(config::setSolarPanelEnergyProductionRate)
                    .setDefaultValue(44)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE),
                    config.circuitFabricatorEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setCircuitFabricatorEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.electricCompressorEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setElectricCompressorEnergyConsumptionRate)
                    .setDefaultValue(75)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE),
                    config.electricFurnaceEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setElectricFurnaceEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.ELECTRIC_ARC_FURNACE_ENERGY_CONSUMPTION_RATE),
                    config.electricArcFurnaceEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.ELECTRIC_ARC_FURNACE_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setElectricArcFurnaceEnergyConsumptionRate)
                    .setDefaultValue(20)
                    .build()
            );

            machines.add(new FloatFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.ELECTRIC_ARC_FURNACE_BONUS_CHANCE),
                    config.electricArcFurnaceBonusChance())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.ELECTRIC_ARC_FURNACE_BONUS_CHANCE))
                    .setSaveConsumer(config::setElectricArcFurnaceBonusChance)
                    .setDefaultValue(0.25F)
                    .setMin(0.0F)
                    .setMax(1.0F)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenCollectorEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setOxygenCollectorEnergyConsumptionRate)
                    .setDefaultValue(10)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenCompressorEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setOxygenCompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenDecompressorEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setOxygenDecompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.OXYGEN_SEALER_ENERGY_CONSUMPTION_RATE),
                    config.oxygenSealerEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.OXYGEN_SEALER_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setOxygenSealerEnergyConsumptionRate)
                    .setDefaultValue(10)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.OXYGEN_SEALER_OXYGEN_CONSUMPTION_RATE),
                    config.oxygenSealerOxygenConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.OXYGEN_SEALER_OXYGEN_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setOxygenSealerOxygenConsumptionRate)
                    .setDefaultValue(1000)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.MAX_SEALING_POWER),
                    config.maxSealingPower())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.MAX_SEALING_POWER))
                    .setSaveConsumer(config::setMaxSealingPower)
                    .setDefaultValue(1024)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.REFINERY_ENERGY_CONSUMPTION_RATE),
                    config.refineryEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.REFINERY_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setRefineryEnergyConsumptionRate)
                    .setDefaultValue(60)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.FUEL_LOADER_ENERGY_CONSUMPTION_RATE),
                    config.fuelLoaderEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.FUEL_LOADER_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setFuelLoaderEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.FOOD_CANNER_ENERGY_CONSUMPTION_RATE),
                    config.foodCannerEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.FOOD_CANNER_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setFoodCannerEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            machines.add(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    labelSub.apply(Translations.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE),
                    config.oxygenDecompressorEnergyConsumptionRate())
                    .setTooltip(tooltipSingularSub.apply(Translations.Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setOxygenDecompressorEnergyConsumptionRate)
                    .setDefaultValue(15)
                    .requireRestart()
                    .build()
            );

            b.getOrCreateCategory(Component.translatable(Translations.Config.ENERGY)).addEntry(wires.build()).addEntry(machines.build());

            // --- CLIENT CONFIG ---

            ConfigCategory client = b.getOrCreateCategory(Component.translatable(Translations.Config.CLIENT));

            client.addEntry(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.SQUARE_CANNED_FOOD),
                    config.squareCannedFood())
                    .setTooltip(tooltipSingular.apply(Translations.Config.SQUARE_CANNED_FOOD))
                    .setSaveConsumer(config::setSquareCannedFood)
                    .setDefaultValue(false)
                    .build()
            );

            // --- SKYBOX CONFIG ---

            SubCategoryBuilder skybox = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.SKYBOX));

            // --- CREATIVE CONFIG ---

            SubCategoryBuilder creative = ConfigEntryBuilder.create().startSubCategory(Component.translatable(Translations.Config.CREATIVE));

            creative.add(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    Component.translatable(Translations.Config.ENABLE_CREATIVE_GEARINV),
                    config.enableCreativeGearInv)
                    .setSaveConsumer(config::setCreativeGearInv)
                    .setDefaultValue(true)
                    .build()
            );

            b.getOrCreateCategory(Component.translatable(Translations.Config.MISC)).addEntry(creative.build());

            // --- LIFE SUPPORT CONFIG ---

            ConfigCategory lifeSupport = b.getOrCreateCategory(Component.translatable(Translations.Config.LIFE_SUPPORT));

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.SMALL_OXYGEN_TANK_CAPACITY),
                    config.smallOxygenTankCapacity())
                    .setTooltip(tooltipSingular.apply(Translations.Config.SMALL_OXYGEN_TANK_CAPACITY))
                    .setSaveConsumer(config::setSmallOxygenTankCapacity)
                    .setDefaultValue(FluidConstants.BUCKET)
                    .setMin(0)
                    .setMax(Long.MAX_VALUE)
                    .build()
            );

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.MEDIUM_OXYGEN_TANK_CAPACITY),
                    config.mediumOxygenTankCapacity())
                    .setTooltip(tooltipSingular.apply(Translations.Config.MEDIUM_OXYGEN_TANK_CAPACITY))
                    .setSaveConsumer(config::setMediumOxygenTankCapacity)
                    .setDefaultValue(2 * FluidConstants.BUCKET)
                    .setMin(0)
                    .setMax(Long.MAX_VALUE)
                    .build()
            );

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.LARGE_OXYGEN_TANK_CAPACITY),
                    config.largeOxygenTankCapacity())
                    .setTooltip(tooltipSingular.apply(Translations.Config.LARGE_OXYGEN_TANK_CAPACITY))
                    .setSaveConsumer(config::setLargeOxygenTankCapacity)
                    .setDefaultValue(3 * FluidConstants.BUCKET)
                    .setMin(0)
                    .setMax(Long.MAX_VALUE)
                    .build()
            );

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.PLAYER_OXYGEN_CONSUMPTION_RATE),
                    config.playerOxygenConsumptionRate())
                    .setTooltip(tooltipSingular.apply(Translations.Config.PLAYER_OXYGEN_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setPlayerOxygenConsumptionRate)
                    .setDefaultValue(5 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.WOLF_OXYGEN_CONSUMPTION_RATE),
                    config.wolfOxygenConsumptionRate())
                    .setTooltip(tooltipSingular.apply(Translations.Config.WOLF_OXYGEN_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setWolfOxygenConsumptionRate)
                    .setDefaultValue(3 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.CAT_OXYGEN_CONSUMPTION_RATE),
                    config.catOxygenConsumptionRate())
                    .setTooltip(tooltipSingular.apply(Translations.Config.CAT_OXYGEN_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setCatOxygenConsumptionRate)
                    .setDefaultValue(2 * FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.addEntry(new LongFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.PARROT_OXYGEN_CONSUMPTION_RATE),
                    config.parrotOxygenConsumptionRate())
                    .setTooltip(tooltipSingular.apply(Translations.Config.PARROT_OXYGEN_CONSUMPTION_RATE))
                    .setSaveConsumer(config::setParrotOxygenConsumptionRate)
                    .setDefaultValue(FluidConstants.DROPLET)
                    .setMin(0)
                    .setMax(100000)
                    .build()
            );

            lifeSupport.addEntry(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.CANNOT_EAT_IN_NO_ATMOSPHERE),
                    config.cannotEatInNoAtmosphere())
                    .setTooltip(tooltipSingular.apply(Translations.Config.CANNOT_EAT_IN_NO_ATMOSPHERE))
                    .setSaveConsumer(config::setCannotEatInNoAtmosphere)
                    .setDefaultValue(true)
                    .build()
            );

            lifeSupport.addEntry(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.CANNOT_EAT_WITH_MASK),
                    config.cannotEatWithMask())
                    .setTooltip(tooltipSingular.apply(Translations.Config.CANNOT_EAT_WITH_MASK))
                    .setSaveConsumer(config::setCannotEatWithMask)
                    .setDefaultValue(true)
                    .build()
            );

            // --- COMMANDS CONFIG ---

            ConfigCategory commands = b.getOrCreateCategory(Component.translatable(Translations.Config.COMMANDS));

            commands.addEntry(new BooleanToggleBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.ENABLE_GC_HOUSTON),
                    config.enableGcHouston())
                    .setTooltip(tooltipSingular.apply(Translations.Config.ENABLE_GC_HOUSTON))
                    .setSaveConsumer(config::setEnableGcHouston)
                    .setDefaultValue(true)
                    .build()
            );

            // --- DIFFICULTY CONFIG ---

            ConfigCategory difficulty = b.getOrCreateCategory(Component.translatable(Translations.Config.DIFFICULTY));

            difficulty.addEntry(new FloatFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.METEOR_SPAWN_MULTIPLIER),
                    config.meteorSpawnMultiplier())
                    .setTooltip(tooltipSingular.apply(Translations.Config.METEOR_SPAWN_MULTIPLIER))
                    .setSaveConsumer(config::setMeteorSpawnMultiplier)
                    .setDefaultValue(1.0f)
                    .setMin(Mth.EPSILON)
                    .build()
            );

            difficulty.addEntry(new DoubleFieldBuilder(
                    Component.translatable(Translations.Config.RESET),
                    label.apply(Translations.Config.BOSS_HEALTH_MODIFIER),
                    config.bossHealthMultiplier())
                    .setTooltip(tooltipWithDesc.apply(Translations.Config.BOSS_HEALTH_MODIFIER, Translations.Config.BOSS_HEALTH_MODIFIER_DESC))
                    .setSaveConsumer(config::setBossHealthMultiplier)
                    .setDefaultValue(1)
                    .build()
            );

            return b.build();
        }

        private Component buildTooltip(MutableComponent name, Font font, int maxLw, @Nullable MutableComponent desc) {
            FormattedCharSequence fcs = name.getVisualOrderText();

            if (font.width(fcs) <= maxLw) {
                return desc != null ? desc : Component.empty();
            }

            MutableComponent tooltip = Component.empty().append(name);
            if (desc != null && !desc.getString().isEmpty()) {
                tooltip.append(Component.literal("\n")).append(desc);
            }
            return tooltip;
        }

        private static Component ellipsize(Component full, Font font, int maxPx) {
            FormattedCharSequence fcs = full.getVisualOrderText();
            if (font.width(fcs) <= maxPx) return full;

            String s = full.getString();
            int ell = font.width("…");
            if (ell >= maxPx) return Component.literal("");

            String cut = font.plainSubstrByWidth(s, maxPx - ell);
            return Component.literal(cut.trim() + "…");
        }
    }
}
