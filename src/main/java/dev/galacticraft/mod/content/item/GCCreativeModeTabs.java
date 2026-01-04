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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlockRegistry;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import static dev.galacticraft.mod.content.GCBlocks.*;
import static dev.galacticraft.mod.content.item.GCItems.*;

public class GCCreativeModeTabs {
    public static final CreativeModeTab BLOCKS_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(CHISELED_MOON_ROCK_BRICK))
            .title(Component.translatable(Translations.ItemGroup.BLOCKS))
            .displayItems((parameters, output) -> {
                // DECORATION BLOCKS
                for (GCBlockRegistry.DecorationSet decorationSet : BLOCKS.getDecorations()) {
                    output.accept(decorationSet.item());
                    output.accept(decorationSet.stairsItem());
                    output.accept(decorationSet.slabItem());
                    output.accept(decorationSet.wallItem());
                    output.accept(decorationSet.detailedItem());
                    output.accept(decorationSet.detailedStairsItem());
                    output.accept(decorationSet.detailedSlabItem());
                    output.accept(decorationSet.detailedWallItem());
                }

                // MOON NATURAL
                output.accept(MOON_TURF);
                output.accept(MOON_DIRT);
                output.accept(MOON_DIRT_PATH);
                output.accept(MOON_SURFACE_ROCK);
                output.accept(MOON_DUNGEON_BRICK);

                output.accept(MOON_ROCK);
                output.accept(MOON_ROCK_STAIRS);
                output.accept(MOON_ROCK_SLAB);
                output.accept(MOON_ROCK_WALL);

                output.accept(MOON_ROCK_BRICK);
                output.accept(MOON_ROCK_BRICK_STAIRS);
                output.accept(MOON_ROCK_BRICK_SLAB);
                output.accept(MOON_ROCK_BRICK_WALL);

                output.accept(CRACKED_MOON_ROCK_BRICK);
                output.accept(CRACKED_MOON_ROCK_BRICK_STAIRS);
                output.accept(CRACKED_MOON_ROCK_BRICK_SLAB);
                output.accept(CRACKED_MOON_ROCK_BRICK_WALL);

                output.accept(POLISHED_MOON_ROCK);
                output.accept(POLISHED_MOON_ROCK_STAIRS);
                output.accept(POLISHED_MOON_ROCK_SLAB);
                output.accept(POLISHED_MOON_ROCK_WALL);

                output.accept(CHISELED_MOON_ROCK_BRICK);
                output.accept(MOON_ROCK_PILLAR);

                output.accept(COBBLED_MOON_ROCK);
                output.accept(COBBLED_MOON_ROCK_STAIRS);
                output.accept(COBBLED_MOON_ROCK_SLAB);
                output.accept(COBBLED_MOON_ROCK_WALL);

                output.accept(LUNASLATE);
                output.accept(LUNASLATE_STAIRS);
                output.accept(LUNASLATE_SLAB);
                output.accept(LUNASLATE_WALL);

                output.accept(COBBLED_LUNASLATE);
                output.accept(COBBLED_LUNASLATE_STAIRS);
                output.accept(COBBLED_LUNASLATE_SLAB);
                output.accept(COBBLED_LUNASLATE_WALL);

                output.accept(MOON_BASALT);
                output.accept(MOON_BASALT_STAIRS);
                output.accept(MOON_BASALT_SLAB);
                output.accept(MOON_BASALT_WALL);

                output.accept(MOON_BASALT_BRICK);
                output.accept(MOON_BASALT_BRICK_STAIRS);
                output.accept(MOON_BASALT_BRICK_SLAB);
                output.accept(MOON_BASALT_BRICK_WALL);

                output.accept(CRACKED_MOON_BASALT_BRICK);
                output.accept(CRACKED_MOON_BASALT_BRICK_STAIRS);
                output.accept(CRACKED_MOON_BASALT_BRICK_SLAB);
                output.accept(CRACKED_MOON_BASALT_BRICK_WALL);

                // MARS NATURAL
                output.accept(MARS_SURFACE_ROCK);
                output.accept(MARS_SUB_SURFACE_ROCK);
                output.accept(MARS_STONE);
                output.accept(MARS_COBBLESTONE);
                output.accept(MARS_COBBLESTONE_STAIRS);
                output.accept(MARS_COBBLESTONE_SLAB);
                output.accept(MARS_COBBLESTONE_WALL);

                // ASTEROID NATURAL
                output.accept(ASTEROID_ROCK);
                output.accept(ASTEROID_ROCK_1);
                output.accept(ASTEROID_ROCK_2);

                // VENUS NATURAL
                output.accept(SOFT_VENUS_ROCK);
                output.accept(HARD_VENUS_ROCK);
                output.accept(SCORCHED_VENUS_ROCK);
                output.accept(VOLCANIC_ROCK);
                output.accept(PUMICE);
                output.accept(VAPOR_SPOUT);

                // ORES
                output.accept(SILICON_ORE);
                output.accept(DEEPSLATE_SILICON_ORE);
                output.accept(TIN_ORE);
                output.accept(DEEPSLATE_TIN_ORE);
                output.accept(ALUMINUM_ORE);
                output.accept(DEEPSLATE_ALUMINUM_ORE);

                output.accept(MOON_COPPER_ORE);
                output.accept(LUNASLATE_COPPER_ORE);
                output.accept(MOON_TIN_ORE);
                output.accept(LUNASLATE_TIN_ORE);
                output.accept(MOON_CHEESE_ORE);
                output.accept(LUNAR_SAPPHIRE_ORE);
                output.accept(OLIVINE_BASALT);
                output.accept(RICH_OLIVINE_BASALT);
                output.accept(FALLEN_METEOR);

                output.accept(MARS_IRON_ORE);
                output.accept(MARS_COPPER_ORE);
                output.accept(MARS_TIN_ORE);
                output.accept(DESH_ORE);

                output.accept(ASTEROID_IRON_ORE);
                output.accept(ASTEROID_ALUMINUM_ORE);
                output.accept(ILMENITE_ORE);

                output.accept(VENUS_COPPER_ORE);
                output.accept(VENUS_TIN_ORE);
                output.accept(VENUS_ALUMINUM_ORE);
                output.accept(GALENA_ORE);
                output.accept(SOLAR_ORE);

                // COMPACT MINERAL BLOCKS
                output.accept(RAW_TIN_BLOCK);
                output.accept(RAW_ALUMINUM_BLOCK);
                output.accept(RAW_METEORIC_IRON_BLOCK);
                output.accept(RAW_DESH_BLOCK);
                output.accept(RAW_TITANIUM_BLOCK);
                output.accept(RAW_LEAD_BLOCK);

                output.accept(SILICON_BLOCK);
                output.accept(LUNAR_SAPPHIRE_BLOCK);
                output.accept(OLIVINE_BLOCK);
                output.accept(OLIVINE_CLUSTER);

                output.accept(TIN_BLOCK);
                output.accept(ALUMINUM_BLOCK);
                output.accept(METEORIC_IRON_BLOCK);
                output.accept(DESH_BLOCK);
                output.accept(TITANIUM_BLOCK);
                output.accept(LEAD_BLOCK);

                // CHEESE BLOCKS
                output.accept(MOON_CHEESE_BLOCK);
                output.accept(MOON_CHEESE_LOG);
                output.accept(MOON_CHEESE_LEAVES);

                // MOON VILLAGER SPECIAL
                output.accept(LUNAR_CARTOGRAPHY_TABLE);

                // TORCHES
                output.accept(GCItems.GLOWSTONE_TORCH);
                output.accept(GCItems.UNLIT_TORCH);
                output.accept(GCItems.UNLIT_SOUL_TORCH);

                // LANTERNS
                output.accept(GCItems.GLOWSTONE_LANTERN);
                output.accept(GCItems.UNLIT_LANTERN);
                output.accept(GCItems.UNLIT_SOUL_LANTERN);

                // MISC DECOR
                output.accept(WALKWAY);
                output.accept(WIRE_WALKWAY);
                output.accept(FLUID_PIPE_WALKWAY);
                output.accept(TIN_LADDER);
                output.accept(IRON_GRATING);

                // SPECIAL
                output.accept(ALUMINUM_WIRE);
                output.accept(SEALABLE_ALUMINUM_WIRE);
                output.accept(HEAVY_SEALABLE_ALUMINUM_WIRE);
                for (PipeColor color : PipeColor.byRainbowOrder()) {
                    output.accept(GLASS_FLUID_PIPES.get(color));
                }
                output.accept(ROCKET_LAUNCH_PAD);
                output.accept(FUELING_PAD);
                output.accept(ROCKET_WORKBENCH);

                // MISC MACHINES
                output.accept(CRYOGENIC_CHAMBER);
                output.accept(PLAYER_TRANSPORT_TUBE);

                for (DyeColor color : GCBlockRegistry.COLOR_ORDER) {
                    ItemStack stack = new ItemStack(PARACHEST);
                    stack.set(DataComponents.BASE_COLOR, color);
                    output.accept(stack);
                }

                // VACUUM GLASS
                output.accept(VACUUM_GLASS);
                output.accept(CLEAR_VACUUM_GLASS);
                output.accept(STRONG_VACUUM_GLASS);

                // MISC WORLD GEN
                output.accept(CAVERNOUS_VINES);
            }).build();

    public static final CreativeModeTab MACHINES_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(COAL_GENERATOR))
            .title(Component.translatable(Translations.ItemGroup.MACHINES))
            .displayItems((parameters, output) -> {
                output.accept(CIRCUIT_FABRICATOR);
                output.accept(COMPRESSOR);
                output.accept(ELECTRIC_COMPRESSOR);
                output.accept(COAL_GENERATOR);
                output.accept(BASIC_SOLAR_PANEL);
                output.accept(ADVANCED_SOLAR_PANEL);
                output.accept(ENERGY_STORAGE_MODULE);
                output.accept(ELECTRIC_FURNACE);
                output.accept(ELECTRIC_ARC_FURNACE);
                output.accept(REFINERY);
                output.accept(FUEL_LOADER);
                output.accept(OXYGEN_COLLECTOR);
                output.accept(OXYGEN_SEALER);
                output.accept(OXYGEN_BUBBLE_DISTRIBUTOR);
                output.accept(OXYGEN_DECOMPRESSOR);
                output.accept(OXYGEN_COMPRESSOR);
                output.accept(OXYGEN_STORAGE_MODULE);
                output.accept(FOOD_CANNER);
                output.accept(AIR_LOCK_FRAME);
                output.accept(AIR_LOCK_CONTROLLER);
            }).build();

    public static final CreativeModeTab ITEMS_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(CANVAS))
            .title(Component.translatable(Translations.ItemGroup.ITEMS))
            .displayItems((parameters, output) -> {
                // BATTERIES
                output.accept(BATTERY);
                ItemStack chargedBattery = new ItemStack(BATTERY);
                BATTERY.setStoredEnergy(chargedBattery, BATTERY.getEnergyCapacity(chargedBattery));
                output.accept(chargedBattery);
                output.accept(INFINITE_BATTERY);

                output.accept(SMALL_OXYGEN_TANK);
                output.accept(OxygenTankItem.getFullTank(SMALL_OXYGEN_TANK));
                output.accept(MEDIUM_OXYGEN_TANK);
                output.accept(OxygenTankItem.getFullTank(MEDIUM_OXYGEN_TANK));
                output.accept(LARGE_OXYGEN_TANK);
                output.accept(OxygenTankItem.getFullTank(LARGE_OXYGEN_TANK));
                output.accept(INFINITE_OXYGEN_TANK);

                // GEAR
                output.accept(OXYGEN_MASK);
                output.accept(OXYGEN_GEAR);
                output.accept(FREQUENCY_MODULE);
                output.accept(SHIELD_CONTROLLER);

                output.accept(PARACHUTE);
                for (DyeColor color : GCBlockRegistry.COLOR_ORDER) {
                    output.accept(DYED_PARACHUTES.get(color));
                }

                output.accept(EMERGENCY_KIT);

                // MATERIALS
                output.accept(RAW_TIN);
                output.accept(RAW_ALUMINUM);
                output.accept(RAW_METEORIC_IRON);
                output.accept(RAW_DESH);
                output.accept(RAW_TITANIUM);
                output.accept(RAW_LEAD);
                output.accept(SILICON);
                output.accept(LUNAR_SAPPHIRE);
                output.accept(OLIVINE_SHARD);

                output.accept(TIN_NUGGET);
                output.accept(ALUMINUM_NUGGET);
                output.accept(METEORIC_IRON_NUGGET);
                output.accept(DESH_NUGGET);
                output.accept(TITANIUM_NUGGET);
                output.accept(LEAD_NUGGET);

                output.accept(TIN_INGOT);
                output.accept(ALUMINUM_INGOT);
                output.accept(METEORIC_IRON_INGOT);
                output.accept(DESH_INGOT);
                output.accept(TITANIUM_INGOT);
                output.accept(LEAD_INGOT);

                output.accept(COMPRESSED_IRON);
                output.accept(COMPRESSED_COPPER);
                output.accept(COMPRESSED_TIN);
                output.accept(COMPRESSED_ALUMINUM);
                output.accept(COMPRESSED_STEEL);
                output.accept(COMPRESSED_BRONZE);
                output.accept(COMPRESSED_METEORIC_IRON);
                output.accept(COMPRESSED_DESH);
                output.accept(COMPRESSED_TITANIUM);

                output.accept(TIER_1_HEAVY_DUTY_PLATE);
                output.accept(TIER_2_HEAVY_DUTY_PLATE);
                output.accept(TIER_3_HEAVY_DUTY_PLATE);

                output.accept(COPPER_CANISTER);
                output.accept(TIN_CANISTER);
                output.accept(STEEL_POLE);
                output.accept(DESH_STICK);

                output.accept(BASIC_WAFER);
                output.accept(ADVANCED_WAFER);
                output.accept(BLUE_SOLAR_WAFER);
                output.accept(SINGLE_SOLAR_MODULE);
                output.accept(FULL_SOLAR_PANEL);
                output.accept(SOLAR_DUST);
                output.accept(SOLAR_ARRAY_WAFER);
                output.accept(SOLAR_ARRAY_PANEL);

                output.accept(OXYGEN_CONCENTRATOR);
                output.accept(OXYGEN_FAN);
                output.accept(OXYGEN_VENT);

                output.accept(CANVAS);
                output.accept(THERMAL_CLOTH);
                output.accept(ISOTHERMAL_FABRIC);

                output.accept(BEAM_CORE);
                output.accept(SENSOR_LENS);
                output.accept(CARBON_FRAGMENTS);
                output.accept(ATMOSPHERIC_VALVE);
                output.accept(FLUID_MANIPULATOR);
                output.accept(AMBIENT_THERMAL_CONTROLLER);
                output.accept(ORION_DRIVE);

                // FOOD
                output.accept(MOON_CHEESE_CURD);
                output.accept(GCItems.MOON_CHEESE_WHEEL);
                output.accept(MOON_CHEESE_SLICE);
                output.accept(CRACKER);
                output.accept(CHEESE_CRACKER);
                output.accept(GROUND_BEEF);
                output.accept(BEEF_PATTY);
                output.accept(BURGER_BUN);
                output.accept(CHEESEBURGER);

                output.accept(THROWABLE_METEOR_CHUNK);
                output.accept(HOT_THROWABLE_METEOR_CHUNK);

                // ROCKET PARTS
                output.accept(NOSE_CONE);
                output.accept(HEAVY_NOSE_CONE);
                output.accept(ROCKET_FIN);
                output.accept(HEAVY_ROCKET_FIN);
                output.accept(ROCKET_ENGINE);
                output.accept(HEAVY_ROCKET_ENGINE);
                output.accept(ROCKET_BOOSTER);

                // BUGGY PARTS
                output.accept(BUGGY_WHEEL);
                output.accept(BUGGY_SEAT);
                output.accept(BUGGY_STORAGE);

                // ROCKETS
                var tier1 = new ItemStack(ROCKET);
                tier1.set(GCDataComponents.ROCKET_DATA, RocketPrefabs.TIER_1);
                output.accept(tier1);

                var creativeRocket = new ItemStack(ROCKET);
                creativeRocket.set(GCDataComponents.CREATIVE, true);
                creativeRocket.set(GCDataComponents.ROCKET_DATA, RocketPrefabs.TIER_1);
                output.accept(creativeRocket);

                // SCHEMATICS
                output.accept(TIER_2_ROCKET_SCHEMATIC);
                output.accept(CARGO_ROCKET_SCHEMATIC);
                output.accept(MOON_BUGGY_SCHEMATIC);
                output.accept(TIER_3_ROCKET_SCHEMATIC);
                output.accept(ASTRO_MINER_SCHEMATIC);

                // SMITHING TEMPLATES
                output.accept(TITANTIUM_UPGRADE_SMITHING_TEMPLATE);

                // TOOLS + WEAPONS
                output.accept(HEAVY_DUTY_SWORD);
                output.accept(HEAVY_DUTY_SHOVEL);
                output.accept(HEAVY_DUTY_PICKAXE);
                output.accept(HEAVY_DUTY_AXE);
                output.accept(HEAVY_DUTY_HOE);

                output.accept(DESH_SWORD);
                output.accept(DESH_SHOVEL);
                output.accept(DESH_PICKAXE);
                output.accept(DESH_AXE);
                output.accept(DESH_HOE);

                output.accept(TITANIUM_SWORD);
                output.accept(TITANIUM_SHOVEL);
                output.accept(TITANIUM_PICKAXE);
                output.accept(TITANIUM_AXE);
                output.accept(TITANIUM_HOE);

                output.accept(STANDARD_WRENCH);

                // ARMOR
                output.accept(HEAVY_DUTY_HELMET);
                output.accept(HEAVY_DUTY_CHESTPLATE);
                output.accept(HEAVY_DUTY_LEGGINGS);
                output.accept(HEAVY_DUTY_BOOTS);

                output.accept(DESH_HELMET);
                output.accept(DESH_CHESTPLATE);
                output.accept(DESH_LEGGINGS);
                output.accept(DESH_BOOTS);

                output.accept(TITANIUM_HELMET);
                output.accept(TITANIUM_CHESTPLATE);
                output.accept(TITANIUM_LEGGINGS);
                output.accept(TITANIUM_BOOTS);

                output.accept(SENSOR_GLASSES);

                // THERMAL PADDING
                output.accept(THERMAL_PADDING_HELMET);
                output.accept(THERMAL_PADDING_CHESTPIECE);
                output.accept(THERMAL_PADDING_LEGGINGS);
                output.accept(THERMAL_PADDING_BOOTS);

                output.accept(ISOTHERMAL_PADDING_HELMET);
                output.accept(ISOTHERMAL_PADDING_CHESTPIECE);
                output.accept(ISOTHERMAL_PADDING_LEGGINGS);
                output.accept(ISOTHERMAL_PADDING_BOOTS);

                // LEGACY MUSIC DISCS
                output.accept(LEGACY_MUSIC_DISC_MARS);
                output.accept(LEGACY_MUSIC_DISC_MIMAS);
                output.accept(LEGACY_MUSIC_DISC_ORBIT);
                output.accept(LEGACY_MUSIC_DISC_SPACERACE);

                // FLUID BUCKETS
                output.accept(CRUDE_OIL_BUCKET);
                output.accept(FUEL_BUCKET);
                output.accept(SULFURIC_ACID_BUCKET);
            })
            .build();

    public static final CreativeModeTab CANNED_FOOD_GROUP = FabricItemGroup
            .builder()
            .icon(() -> new ItemStack(GCItems.CANNED_FOOD))
            .title(Component.translatable(Translations.ItemGroup.CANNED_FOOD))
            .displayItems((parameters, output) -> {
                output.accept(EMPTY_CAN);
                CannedFoodItem.getDefaultCannedFoods().forEach(cannedFoodItem -> output.accept(cannedFoodItem));
            }).build();

    public static void registerSpawnEggs() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(content -> {
            content.addAfter(ItemStack.EMPTY, MOON_VILLAGER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_ZOMBIE_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_CREEPER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_SKELETON_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_SPIDER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_ENDERMAN_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_WITCH_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_PILLAGER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_EVOKER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, EVOLVED_VINDICATOR_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, GAZER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, RUMBLER_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, COMET_CUBE_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, OLI_GRUB_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, GREY_SPAWN_EGG);
            content.addAfter(ItemStack.EMPTY, ARCH_GREY_SPAWN_EGG);
        });
    }

    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id("1." + Constant.Block.ITEM_GROUP_BLOCKS), BLOCKS_GROUP);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id("2." + Constant.Block.ITEM_GROUP_MACHINES), MACHINES_GROUP);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id("3." + Constant.Item.ITEM_GROUP), ITEMS_GROUP);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Constant.id("4." + Constant.Item.ITEM_GROUP_CANS), CANNED_FOOD_GROUP);
        registerSpawnEggs();
    }
}