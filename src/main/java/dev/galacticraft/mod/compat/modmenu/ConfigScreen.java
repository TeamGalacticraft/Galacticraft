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

package dev.galacticraft.mod.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.GalacticraftClient;
import dev.galacticraft.mod.config.ClientConfigImpl;
import dev.galacticraft.mod.config.ConfigImpl;
import dev.galacticraft.mod.util.Translations;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfigScreen implements ConfigScreenFactory<Screen> {
    public static final ConfigScreen INSTANCE = new ConfigScreen();

    private Function<String, Component> label;
    private Function<String, Component> labelSub;
    private BiFunction<String, String, Component> tooltipWithDesc;
    private Function<String, Component> tooltipSingular;
    private BiFunction<String, String, Component> tooltipWithDescSub;
    private Function<String, Component> tooltipSingularSub;

    private ConfigScreen() {
    }

    @Override
    public Screen create(Screen parent) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        final int maxLabelWidth = Math.max(80, minecraft.getWindow().getGuiScaledWidth() - 240);

        // Use for normal Labels under a category.
        label =
                key -> ellipsize(Component.translatable(key), font, maxLabelWidth);

        // Use for labels under a subcategory.
        labelSub =
                key -> ellipsize(Component.translatable(key), font, maxLabelWidth - 15);

        // Use for tooltips with description e.g. (name, description)
        tooltipWithDesc =
                (id, optDescId) -> buildTooltip(
                        Component.translatable(id),
                        font,
                        maxLabelWidth,
                        (optDescId != null && !optDescId.isEmpty())
                                ? Component.translatable(optDescId)
                                : null
                );

        // Use for configs with no description e.g. (name)
        tooltipSingular =
                id -> tooltipWithDesc.apply(id, null);

        // Same but for subcategories
        tooltipWithDescSub =
                (id, optDescId) -> buildTooltip(
                        Component.translatable(id),
                        font,
                        maxLabelWidth - 15,
                        (optDescId != null && !optDescId.isEmpty())
                                ? Component.translatable(optDescId)
                                : null
                );

        // Same but for subcategories
        tooltipSingularSub =
                id -> tooltipWithDescSub.apply(id, null);

        ClientConfigImpl clientConfig = (ClientConfigImpl) GalacticraftClient.CONFIG;
        ConfigImpl config = (ConfigImpl) Galacticraft.CONFIG;

        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable(Translations.Config.TITLE))
                .setSavingRunnable(() -> {
                    clientConfig.save();

                    if (minecraft.isSingleplayer()) {
                        config.save();
                    }
                });

        this.addClientConfig(clientConfig, b);

        if (minecraft.isSingleplayer()) {
            this.addConfig(config, b);
        }

        return b.build();
    }

    public void addClientConfig(ClientConfigImpl clientConfig, ConfigBuilder b) {
        ConfigCategory client = b.getOrCreateCategory(Component.translatable(Translations.Config.CLIENT));

        client.addEntry(new BooleanToggleBuilder(
                Component.translatable(Translations.Config.RESET),
                label.apply(Translations.Config.HIDE_ALPHA_WARNING),
                clientConfig.isAlphaWarningHidden())
                .setTooltip(tooltipSingular.apply(Translations.Config.HIDE_ALPHA_WARNING))
                .setSaveConsumer(clientConfig::setAlphaWarningHidden)
                .setDefaultValue(false)
                .build()
        );

        client.addEntry(new BooleanToggleBuilder(
                Component.translatable(Translations.Config.RESET),
                Component.translatable(Translations.Config.ENABLE_CREATIVE_GEARINV),
                clientConfig.enableCreativeGearInv())
                .setSaveConsumer(clientConfig::setCreativeGearInv)
                .setDefaultValue(true)
                .build()
        );

        client.addEntry(new BooleanToggleBuilder(
                Component.translatable(Translations.Config.RESET),
                label.apply(Translations.Config.SQUARE_CANNED_FOOD),
                clientConfig.squareCannedFood())
                .setTooltip(tooltipSingular.apply(Translations.Config.SQUARE_CANNED_FOOD))
                .setSaveConsumer(clientConfig::setSquareCannedFood)
                .setDefaultValue(false)
                .build()
        );
    }

    public void addConfig(ConfigImpl config, ConfigBuilder b) {
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

        ConfigCategory misc = b.getOrCreateCategory(Component.translatable(Translations.Config.MISC));

        misc.addEntry(new LongFieldBuilder(
                Component.translatable(Translations.Config.RESET),
                label.apply(Translations.Config.FLUID_CANISTER_CAPACITY),
                config.fluidCanisterCapacity())
                .setTooltip(tooltipSingular.apply(Translations.Config.FLUID_CANISTER_CAPACITY))
                .setSaveConsumer(config::setFluidCanisterCapacity)
                .setDefaultValue(FluidConstants.BUCKET)
                .setMin(0)
                .setMax(Long.MAX_VALUE)
                .build()
        );

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
