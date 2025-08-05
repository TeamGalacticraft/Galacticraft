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

package dev.galacticraft.mod.data;

import dev.galacticraft.api.component.GCItemSubPredicates;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.advancements.critereon.*;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCDamageTypeTags;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static dev.galacticraft.mod.util.Translations.Advancements.*;

public class GCAdvancementProvider extends FabricAdvancementProvider {
    private final CompoundTag PARROT_ON_LEFT_SHOULDER = new CompoundTag();
    private final CompoundTag PARROT_ON_RIGHT_SHOULDER = new CompoundTag();

    public GCAdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
        CompoundTag parrot = new CompoundTag();
        parrot.putString("id", "minecraft:parrot");
        PARROT_ON_LEFT_SHOULDER.put("ShoulderEntityLeft", parrot);
        PARROT_ON_RIGHT_SHOULDER.put("ShoulderEntityRight", parrot);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder rootAdvancement = Advancement.Builder.advancement()
                .display(
                        GCItems.ROCKET, // The display icon
                        title(ROOT), // The title
                        description(ROOT), // The description
                        Constant.id("textures/gui/advancements/backgrounds/moon.png"), // Background image used
                        AdvancementType.TASK, // Options: TASK, CHALLENGE, GOAL
                        false, // Show toast top right
                        false, // Announce to chat
                        false // Hidden in the advancement tab
                )
                .addCriterion("root", PlayerTrigger.TriggerInstance.tick())
                .save(consumer, Constant.MOD_ID + "/root");

        AdvancementHolder coalGeneratorAdvancement = Advancement.Builder.advancement().parent(rootAdvancement)
                .display(
                        GCBlocks.COAL_GENERATOR,
                        title(COAL_GENERATOR),
                        description(COAL_GENERATOR),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_coal_generator", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.COAL_GENERATOR))
                .save(consumer, Constant.MOD_ID + "/coal_generator");

        AdvancementHolder circuitFabricatorAdvancement = Advancement.Builder.advancement().parent(coalGeneratorAdvancement)
                .display(
                        GCBlocks.CIRCUIT_FABRICATOR,
                        title(CIRCUIT_FABRICATOR),
                        description(CIRCUIT_FABRICATOR),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_circuit_fabricator", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.CIRCUIT_FABRICATOR))
                .save(consumer, Constant.MOD_ID + "/circuit_fabricator");

        AdvancementHolder basicWaferAdvancement = Advancement.Builder.advancement().parent(circuitFabricatorAdvancement)
                .display(
                        GCItems.BASIC_WAFER,
                        title(BASIC_WAFER),
                        description(BASIC_WAFER),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_basic_wafer", InventoryChangeTrigger.TriggerInstance.hasItems(GCItems.BASIC_WAFER))
                .save(consumer, Constant.MOD_ID + "/basic_wafer");

        AdvancementHolder advancedWaferAdvancement = Advancement.Builder.advancement().parent(basicWaferAdvancement)
                .display(
                        GCItems.ADVANCED_WAFER,
                        title(ADVANCED_WAFER),
                        description(ADVANCED_WAFER),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_advanced_wafer", InventoryChangeTrigger.TriggerInstance.hasItems(GCItems.ADVANCED_WAFER))
                .save(consumer, Constant.MOD_ID + "/advanced_wafer");

        AdvancementHolder basicSolarPanelAdvancement = Advancement.Builder.advancement().parent(basicWaferAdvancement)
                .display(
                        GCBlocks.BASIC_SOLAR_PANEL,
                        title(BASIC_SOLAR_PANEL),
                        description(BASIC_SOLAR_PANEL),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_basic_solar_panel", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.BASIC_SOLAR_PANEL))
                .save(consumer, Constant.MOD_ID + "/basic_solar_panel");

        AdvancementHolder advancedSolarPanelAdvancement = Advancement.Builder.advancement().parent(basicSolarPanelAdvancement)
                .display(
                        GCBlocks.ADVANCED_SOLAR_PANEL,
                        title(ADVANCED_SOLAR_PANEL),
                        description(ADVANCED_SOLAR_PANEL),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_advanced_solar_panel", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.ADVANCED_SOLAR_PANEL))
                .save(consumer, Constant.MOD_ID + "/advanced_solar_panel");

        AdvancementHolder compressorAdvancement = Advancement.Builder.advancement().parent(basicWaferAdvancement)
                .display(
                        GCBlocks.COMPRESSOR,
                        title(COMPRESSOR),
                        description(COMPRESSOR),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_compressor", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.COMPRESSOR))
                .save(consumer, Constant.MOD_ID + "/compressor");

        AdvancementHolder electricCompressorAdvancement = Advancement.Builder.advancement().parent(compressorAdvancement)
                .display(
                        GCBlocks.ELECTRIC_COMPRESSOR,
                        title(ELECTRIC_COMPRESSOR),
                        description(ELECTRIC_COMPRESSOR),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_electric_compressor", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.ELECTRIC_COMPRESSOR))
                .save(consumer, Constant.MOD_ID + "/electric_compressor");

        AdvancementHolder oilAdvancement = Advancement.Builder.advancement().parent(compressorAdvancement)
                .display(
                        GCItems.CRUDE_OIL_BUCKET,
                        title(OIL),
                        description(OIL),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("in_oil", EnterBlockTrigger.TriggerInstance.entersBlock(GCBlocks.CRUDE_OIL))
                .save(consumer, Constant.MOD_ID + "/oil");

        AdvancementHolder refineryAdvancement = Advancement.Builder.advancement().parent(oilAdvancement)
                .display(
                        GCBlocks.REFINERY,
                        title(REFINERY),
                        description(REFINERY),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_refinery", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.REFINERY))
                .save(consumer, Constant.MOD_ID + "/refinery");

        AdvancementHolder fuelAdvancement = Advancement.Builder.advancement().parent(refineryAdvancement)
                .display(
                        GCItems.FUEL_BUCKET,
                        title(FUEL),
                        description(FUEL),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("refine_fuel", InventoryChangeTrigger.TriggerInstance.hasItems(GCItems.FUEL_BUCKET))
                .save(consumer, Constant.MOD_ID + "/fuel");

        AdvancementHolder oxygenCollectorAdvancement = Advancement.Builder.advancement().parent(compressorAdvancement)
                .display(
                        GCBlocks.OXYGEN_COLLECTOR,
                        title(OXYGEN_COLLECTOR),
                        description(OXYGEN_COLLECTOR),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_oxygen_collector", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.OXYGEN_COLLECTOR))
                .save(consumer, Constant.MOD_ID + "/oxygen_collector");

        AdvancementHolder oxygenCompressorAdvancement = Advancement.Builder.advancement().parent(oxygenCollectorAdvancement)
                .display(
                        GCBlocks.OXYGEN_COMPRESSOR,
                        title(OXYGEN_COMPRESSOR),
                        description(OXYGEN_COMPRESSOR),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_oxygen_compressor", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.OXYGEN_COMPRESSOR))
                .save(consumer, Constant.MOD_ID + "/oxygen_compressor");

        AdvancementHolder fillTankAdvancement = Advancement.Builder.advancement().parent(oxygenCompressorAdvancement)
                .display(
                        GCItems.MEDIUM_OXYGEN_TANK,
                        title(FILL_TANK),
                        description(FILL_TANK),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("fill_small_oxygen_tank", InventoryChangeTrigger.TriggerInstance.hasItems(
                        fullTank(GCItems.SMALL_OXYGEN_TANK)
                ))
                .addCriterion("fill_medium_oxygen_tank", InventoryChangeTrigger.TriggerInstance.hasItems(
                        fullTank(GCItems.MEDIUM_OXYGEN_TANK)
                ))
                .addCriterion("fill_large_oxygen_tank", InventoryChangeTrigger.TriggerInstance.hasItems(
                        fullTank(GCItems.LARGE_OXYGEN_TANK)
                ))
                .save(consumer, Constant.MOD_ID + "/fill_tank");

        AdvancementHolder fillAllTanksAdvancement = Advancement.Builder.advancement().parent(fillTankAdvancement)
                .display(
                        GCItems.LARGE_OXYGEN_TANK,
                        title(FILL_ALL_TANKS),
                        description(FILL_ALL_TANKS),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("fill_small_oxygen_tank", InventoryChangeTrigger.TriggerInstance.hasItems(
                        fullTank(GCItems.SMALL_OXYGEN_TANK)
                ))
                .addCriterion("fill_medium_oxygen_tank", InventoryChangeTrigger.TriggerInstance.hasItems(
                        fullTank(GCItems.MEDIUM_OXYGEN_TANK)
                ))
                .addCriterion("fill_large_oxygen_tank", InventoryChangeTrigger.TriggerInstance.hasItems(
                        fullTank(GCItems.LARGE_OXYGEN_TANK)
                ))
                .save(consumer, Constant.MOD_ID + "/fill_all_tanks");

        AdvancementHolder oxygenGearAdvancement = Advancement.Builder.advancement().parent(fillTankAdvancement)
                .display(
                        GCItems.OXYGEN_MASK,
                        title(OXYGEN_GEAR),
                        description(OXYGEN_GEAR),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("craft_oxygen_gear_small", InventoryChangeTrigger.TriggerInstance.hasItems(
                        GCItems.OXYGEN_MASK, GCItems.OXYGEN_GEAR, GCItems.SMALL_OXYGEN_TANK
                ))
                .addCriterion("craft_oxygen_gear_medium", InventoryChangeTrigger.TriggerInstance.hasItems(
                        GCItems.OXYGEN_MASK, GCItems.OXYGEN_GEAR, GCItems.MEDIUM_OXYGEN_TANK
                ))
                .addCriterion("craft_oxygen_gear_large", InventoryChangeTrigger.TriggerInstance.hasItems(
                        GCItems.OXYGEN_MASK, GCItems.OXYGEN_GEAR, GCItems.LARGE_OXYGEN_TANK
                ))
                .save(consumer, Constant.MOD_ID + "/oxygen_gear");

        AdvancementHolder rocketWorkbenchAdvancement = Advancement.Builder.advancement().parent(compressorAdvancement)
                .display(
                        GCBlocks.ROCKET_WORKBENCH,
                        title(ROCKET_WORKBENCH),
                        description(ROCKET_WORKBENCH),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_rocket_workbench", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.ROCKET_WORKBENCH))
                .save(consumer, Constant.MOD_ID + "/rocket_workbench");

        AdvancementHolder rocketAdvancement = Advancement.Builder.advancement().parent(rocketWorkbenchAdvancement)
                .display(
                        GCItems.ROCKET,
                        title(ROCKET),
                        description(ROCKET),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_rocket", InventoryChangeTrigger.TriggerInstance.hasItems(GCItems.ROCKET))
                .save(consumer, Constant.MOD_ID + "/rocket");

        AdvancementHolder fuelLoaderAdvancement = Advancement.Builder.advancement().parent(rocketAdvancement)
                .display(
                        GCBlocks.FUEL_LOADER,
                        title(FUEL_LOADER),
                        description(FUEL_LOADER),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("craft_fuel_loader", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.FUEL_LOADER))
                .save(consumer, Constant.MOD_ID + "/fuel_loader");

        AdvancementHolder leaveRocketAdvancement = Advancement.Builder.advancement().parent(fuelLoaderAdvancement)
                .display(
                        Items.CLOCK,
                        title(LEAVE_ROCKET_DURING_COUNTDOWN),
                        description(LEAVE_ROCKET_DURING_COUNTDOWN),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        true
                )
                .addCriterion("leave_rocket_during_countdown", LeaveRocketDuringCountdownTrigger.TriggerInstance.left())
                .save(consumer, Constant.MOD_ID + "/leave_rocket");

        AdvancementHolder launchRocketAdvancement = Advancement.Builder.advancement().parent(fuelLoaderAdvancement)
                .display(
                        Items.FLINT_AND_STEEL,
                        title(LAUNCH_ROCKET),
                        description(LAUNCH_ROCKET),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("launch_rocket", LaunchRocketTrigger.TriggerInstance.launched())
                .save(consumer, Constant.MOD_ID + "/launch_rocket");

        AdvancementHolder moonAdvancement = Advancement.Builder.advancement().parent(launchRocketAdvancement)
                .display(
                        GCBlocks.MOON_TURF,
                        title(MOON),
                        description(MOON),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("moon_landing", SafeLandingTrigger.TriggerInstance.landed(
                        LocationPredicate.Builder.inDimension(GCDimensions.MOON)
                ))
                .save(consumer, Constant.MOD_ID + "/moon");

        AdvancementHolder parrotLandingAdvancement = Advancement.Builder.advancement().parent(moonAdvancement)
                .display(
                        Items.FEATHER,
                        title(PARROT_LANDING),
                        description(PARROT_LANDING),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("parrot_on_left_shoulder", SafeLandingTrigger.TriggerInstance.landed(
                        EntityPredicate.Builder.entity().nbt(new NbtPredicate(PARROT_ON_LEFT_SHOULDER))
                ))
                .addCriterion("parrot_on_right_shoulder", SafeLandingTrigger.TriggerInstance.landed(
                        EntityPredicate.Builder.entity().nbt(new NbtPredicate(PARROT_ON_RIGHT_SHOULDER))
                ))
                .save(consumer, Constant.MOD_ID + "/parrot_landing");

        AdvancementHolder eatMoonCheeseCurdAdvancement = Advancement.Builder.advancement().parent(moonAdvancement)
                .display(
                        GCItems.MOON_CHEESE_CURD,
                        title(EAT_MOON_CHEESE_CURD),
                        description(EAT_MOON_CHEESE_CURD),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("eat_moon_cheese_curd", ConsumeItemTrigger.TriggerInstance.usedItem(GCItems.MOON_CHEESE_CURD))
                .save(consumer, Constant.MOD_ID + "/eat_moon_cheese_curd");

        AdvancementHolder cheeseAndCrackersAdvancement = Advancement.Builder.advancement().parent(eatMoonCheeseCurdAdvancement)
                .display(
                        GCItems.CHEESE_CRACKER,
                        title(CHEESE_AND_CRACKERS),
                        description(CHEESE_AND_CRACKERS),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("eat_cheese_cracker", ConsumeItemTrigger.TriggerInstance.usedItem(GCItems.CHEESE_CRACKER))
                .save(consumer, Constant.MOD_ID + "/cheese_and_crackers");

        AdvancementHolder cheeseTaxAdvancement = Advancement.Builder.advancement().parent(eatMoonCheeseCurdAdvancement)
                .display(
                        GCItems.MOON_CHEESE_SLICE,
                        title(CHEESE_TAX),
                        description(CHEESE_TAX),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("feed_wolf_cheese", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                        ItemPredicate.Builder.item().of(GCItemTags.CHEESE_FOODS),
                        Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(EntityType.WOLF)))
                ))
                .save(consumer, Constant.MOD_ID + "/cheese_tax");

        AdvancementHolder throwMeteorChunkAdvancement = Advancement.Builder.advancement().parent(moonAdvancement)
                .display(
                        GCItems.HOT_THROWABLE_METEOR_CHUNK,
                        title(THROW_METEOR_CHUNK),
                        description(THROW_METEOR_CHUNK),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        true
                )
                .addCriterion("throw_meteor_chunk", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
                        DamagePredicate.Builder.damageInstance()
                                .type(DamageSourcePredicate.Builder.damageType()
                                .tag(TagPredicate.is(GCDamageTypeTags.IS_METEOR)))
                ))
                .save(consumer, Constant.MOD_ID + "/throw_meteor_chunk");

        AdvancementHolder spaceStationAdvancement = Advancement.Builder.advancement().parent(moonAdvancement)
                .display(
                        GCItems.FULL_SOLAR_PANEL,
                        title(SPACE_STATION),
                        description(SPACE_STATION),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("create_space_station", CreateSpaceStationTrigger.TriggerInstance.created())
                .save(consumer, Constant.MOD_ID + "/space_station");

        AdvancementHolder moonDungeonAdvancement = Advancement.Builder.advancement().parent(moonAdvancement)
                .display(
                        GCBlocks.MOON_DUNGEON_BRICK,
                        title(MOON_DUNGEON),
                        description(MOON_DUNGEON),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("found_moon_dungeon", FindMoonBossTrigger.TriggerInstance.found())
                .save(consumer, Constant.MOD_ID + "/moon_dungeon");

        AdvancementHolder moonDungeonKeyAdvancement = Advancement.Builder.advancement().parent(moonDungeonAdvancement)
                .display(
                        Items.TRIAL_KEY,
                        title(MOON_DUNGEON_KEY),
                        description(MOON_DUNGEON_KEY),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("moon_dungeon_key", InventoryChangeTrigger.TriggerInstance.hasItems(GCBlocks.BOSS_SPAWNER))
                .save(consumer, Constant.MOD_ID + "/moon_dungeon_key");

        AdvancementHolder buggySchematicAdvancement = Advancement.Builder.advancement().parent(moonDungeonKeyAdvancement)
                .display(
                        GCItems.MOON_BUGGY_SCHEMATIC,
                        title(BUGGY_SCHEMATIC),
                        description(BUGGY_SCHEMATIC),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("buggy_schematic", InventoryChangeTrigger.TriggerInstance.hasItems(GCItems.MOON_BUGGY_SCHEMATIC))
                .save(consumer, Constant.MOD_ID + "/buggy_schematic");

        AdvancementHolder buggyAdvancement = Advancement.Builder.advancement().parent(buggySchematicAdvancement)
                .display(
                        GCItems.BUGGY,
                        title(BUGGY),
                        description(BUGGY),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_buggy", InventoryChangeTrigger.TriggerInstance.hasItems(GCItems.BUGGY))
                .save(consumer, Constant.MOD_ID + "/buggy");
    }

    private static Component title(String translationKey) {
        return Component.translatable(translationKey + ".title");
    }

    private static Component description(String translationKey) {
        return Component.translatable(translationKey + ".description");
    }

    private static ItemPredicate.Builder fullTank(ItemLike item) {
        return ItemPredicate.Builder.item().of(item)
                .withSubPredicate(
                        GCItemSubPredicates.FULL_TANK,
                        ItemFullTankPredicate.any()
                );
    }
}
