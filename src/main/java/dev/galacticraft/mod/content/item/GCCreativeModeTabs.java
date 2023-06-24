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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static dev.galacticraft.mod.content.item.GCItems.*;

@SuppressWarnings("unused") // groups are registered in the build() call
public class GCCreativeModeTabs {
    public static final CreativeModeTab ITEMS_GROUP = FabricItemGroup
            .builder(new ResourceLocation(Constant.MOD_ID, Constant.Item.ITEM_GROUP))
            .icon(() -> new ItemStack(GCItems.CANVAS))
            .displayItems((parameters, output) -> { // todo: add rockets here
                // MATERIALS
                output.accept(RAW_SILICON);

                output.accept(RAW_METEORIC_IRON);
                output.accept(METEORIC_IRON_INGOT);
                output.accept(METEORIC_IRON_NUGGET);
                output.accept(COMPRESSED_METEORIC_IRON);

                output.accept(RAW_DESH);
                output.accept(DESH_INGOT);
                output.accept(DESH_NUGGET);
                output.accept(COMPRESSED_DESH);

                output.accept(RAW_LEAD);
                output.accept(LEAD_INGOT);
                output.accept(LEAD_NUGGET);

                output.accept(RAW_ALUMINUM);
                output.accept(ALUMINUM_INGOT);
                output.accept(ALUMINUM_NUGGET);
                output.accept(COMPRESSED_ALUMINUM);

                output.accept(RAW_TIN);
                output.accept(TIN_INGOT);
                output.accept(TIN_NUGGET);
                output.accept(COMPRESSED_TIN);

                output.accept(RAW_TITANIUM);
                output.accept(TITANIUM_INGOT);
                output.accept(TITANIUM_NUGGET);
                output.accept(COMPRESSED_TITANIUM);

                output.accept(COMPRESSED_BRONZE);
                output.accept(COMPRESSED_COPPER);
                output.accept(COMPRESSED_IRON);
                output.accept(COMPRESSED_STEEL);

                output.accept(LUNAR_SAPPHIRE);
                output.accept(DESH_STICK);
                output.accept(CARBON_FRAGMENTS);
                output.accept(IRON_SHARD);
                output.accept(SOLAR_DUST);
                output.accept(BASIC_WAFER);
                output.accept(ADVANCED_WAFER);
                output.accept(BEAM_CORE);
                output.accept(CANVAS);

                output.accept(FLUID_MANIPULATOR);
                output.accept(OXYGEN_CONCENTRATOR);
                output.accept(OXYGEN_FAN);
                output.accept(OXYGEN_VENT);
                output.accept(SENSOR_LENS);
                output.accept(BLUE_SOLAR_WAFER);
                output.accept(SINGLE_SOLAR_MODULE);
                output.accept(FULL_SOLAR_PANEL);
                output.accept(SOLAR_ARRAY_WAFER);
                output.accept(STEEL_POLE);
                output.accept(COPPER_CANISTER);
                output.accept(TIN_CANISTER);
                output.accept(THERMAL_CLOTH);
                output.accept(ISOTHERMAL_FABRIC);
                output.accept(ORION_DRIVE);
                output.accept(ATMOSPHERIC_VALVE);
                output.accept(AMBIENT_THERMAL_CONTROLLER);

                // FOOD
                output.accept(MOON_BERRIES);
                output.accept(CHEESE_CURD);

                output.accept(CHEESE_SLICE);
                output.accept(BURGER_BUN);
                output.accept(GROUND_BEEF);
                output.accept(BEEF_PATTY);
                output.accept(CHEESEBURGER);

                output.accept(CANNED_DEHYDRATED_APPLE);
                output.accept(CANNED_DEHYDRATED_CARROT);
                output.accept(CANNED_DEHYDRATED_MELON);
                output.accept(CANNED_DEHYDRATED_POTATO);
                output.accept(CANNED_BEEF);

                // ROCKET PLATES
                output.accept(TIER_1_HEAVY_DUTY_PLATE);
                output.accept(TIER_2_HEAVY_DUTY_PLATE);
                output.accept(TIER_3_HEAVY_DUTY_PLATE);

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

                // BATTERIES
                output.accept(BATTERY);
                output.accept(INFINITE_BATTERY);

                //FLUID BUCKETS
                output.accept(CRUDE_OIL_BUCKET);
                output.accept(FUEL_BUCKET);

                //GALACTICRAFT INVENTORY
                output.accept(PARACHUTE);
                output.accept(ORANGE_PARACHUTE);
                output.accept(MAGENTA_PARACHUTE);
                output.accept(LIGHT_BLUE_PARACHUTE);
                output.accept(YELLOW_PARACHUTE);
                output.accept(LIME_PARACHUTE);
                output.accept(PINK_PARACHUTE);
                output.accept(GRAY_PARACHUTE);
                output.accept(LIGHT_GRAY_PARACHUTE);
                output.accept(CYAN_PARACHUTE);
                output.accept(PURPLE_PARACHUTE);
                output.accept(BLUE_PARACHUTE);
                output.accept(BROWN_PARACHUTE);
                output.accept(GREEN_PARACHUTE);
                output.accept(RED_PARACHUTE);
                output.accept(BLACK_PARACHUTE);

                output.accept(OXYGEN_MASK);
                output.accept(OXYGEN_GEAR);

                output.accept(SMALL_OXYGEN_TANK);
                output.accept(MEDIUM_OXYGEN_TANK);
                output.accept(LARGE_OXYGEN_TANK);
                output.accept(INFINITE_OXYGEN_TANK);

                output.accept(SHIELD_CONTROLLER);
                output.accept(FREQUENCY_MODULE);

                output.accept(THERMAL_PADDING_HELMET);
                output.accept(THERMAL_PADDING_CHESTPIECE);
                output.accept(THERMAL_PADDING_LEGGINGS);
                output.accept(THERMAL_PADDING_BOOTS);
                // ROCKETS
                output.accept(ROCKET.getDefaultInstance());

                // SCHEMATICS
                output.accept(TIER_2_ROCKET_SCHEMATIC);
                output.accept(CARGO_ROCKET_SCHEMATIC);
                output.accept(MOON_BUGGY_SCHEMATIC);
                output.accept(TIER_3_ROCKET_SCHEMATIC);
                output.accept(ASTRO_MINER_SCHEMATIC);
            })
            .build();

    public static final CreativeModeTab BLOCKS_GROUP = FabricItemGroup
            .builder(new ResourceLocation(Constant.MOD_ID, Constant.Block.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GCBlocks.MOON_TURF))
            .displayItems((parameters, output) -> {
                output.accept(GLOWSTONE_TORCH);
                output.accept(UNLIT_TORCH);

                // LANTERNS
                output.accept(GLOWSTONE_LANTERN);

                // DECORATION BLOCKS
                output.accept(ALUMINUM_DECORATION);
                output.accept(ALUMINUM_DECORATION_SLAB);
                output.accept(ALUMINUM_DECORATION_STAIRS);
                output.accept(ALUMINUM_DECORATION_WALL);
                output.accept(DETAILED_ALUMINUM_DECORATION);
                output.accept(DETAILED_ALUMINUM_DECORATION_SLAB);
                output.accept(DETAILED_ALUMINUM_DECORATION_STAIRS);
                output.accept(DETAILED_ALUMINUM_DECORATION_WALL);

                output.accept(BRONZE_DECORATION);
                output.accept(BRONZE_DECORATION_SLAB);
                output.accept(BRONZE_DECORATION_STAIRS);
                output.accept(BRONZE_DECORATION_WALL);
                output.accept(DETAILED_BRONZE_DECORATION);
                output.accept(DETAILED_BRONZE_DECORATION_SLAB);
                output.accept(DETAILED_BRONZE_DECORATION_STAIRS);
                output.accept(DETAILED_BRONZE_DECORATION_WALL);

                output.accept(COPPER_DECORATION);
                output.accept(COPPER_DECORATION_SLAB);
                output.accept(COPPER_DECORATION_STAIRS);
                output.accept(COPPER_DECORATION_WALL);
                output.accept(DETAILED_COPPER_DECORATION);
                output.accept(DETAILED_COPPER_DECORATION_SLAB);
                output.accept(DETAILED_COPPER_DECORATION_STAIRS);
                output.accept(DETAILED_COPPER_DECORATION_WALL);

                output.accept(IRON_DECORATION);
                output.accept(IRON_DECORATION_SLAB);
                output.accept(IRON_DECORATION_STAIRS);
                output.accept(IRON_DECORATION_WALL);
                output.accept(DETAILED_IRON_DECORATION);
                output.accept(DETAILED_IRON_DECORATION_SLAB);
                output.accept(DETAILED_IRON_DECORATION_STAIRS);
                output.accept(DETAILED_IRON_DECORATION_WALL);

                output.accept(METEORIC_IRON_DECORATION);
                output.accept(METEORIC_IRON_DECORATION_SLAB);
                output.accept(METEORIC_IRON_DECORATION_STAIRS);
                output.accept(METEORIC_IRON_DECORATION_WALL);
                output.accept(DETAILED_METEORIC_IRON_DECORATION);
                output.accept(DETAILED_METEORIC_IRON_DECORATION_SLAB);
                output.accept(DETAILED_METEORIC_IRON_DECORATION_STAIRS);
                output.accept(DETAILED_METEORIC_IRON_DECORATION_WALL);

                output.accept(STEEL_DECORATION);
                output.accept(STEEL_DECORATION_SLAB);
                output.accept(STEEL_DECORATION_STAIRS);
                output.accept(STEEL_DECORATION_WALL);
                output.accept(DETAILED_STEEL_DECORATION);
                output.accept(DETAILED_STEEL_DECORATION_SLAB);
                output.accept(DETAILED_STEEL_DECORATION_STAIRS);
                output.accept(DETAILED_STEEL_DECORATION_WALL);

                output.accept(TIN_DECORATION);
                output.accept(TIN_DECORATION_SLAB);
                output.accept(TIN_DECORATION_STAIRS);
                output.accept(TIN_DECORATION_WALL);
                output.accept(DETAILED_TIN_DECORATION);
                output.accept(DETAILED_TIN_DECORATION_SLAB);
                output.accept(DETAILED_TIN_DECORATION_STAIRS);
                output.accept(DETAILED_TIN_DECORATION_WALL);

                output.accept(TITANIUM_DECORATION);
                output.accept(TITANIUM_DECORATION_SLAB);
                output.accept(TITANIUM_DECORATION_STAIRS);
                output.accept(TITANIUM_DECORATION_WALL);
                output.accept(DETAILED_TITANIUM_DECORATION);
                output.accept(DETAILED_TITANIUM_DECORATION_SLAB);
                output.accept(DETAILED_TITANIUM_DECORATION_STAIRS);
                output.accept(DETAILED_TITANIUM_DECORATION_WALL);

                output.accept(DARK_DECORATION);
                output.accept(DARK_DECORATION_SLAB);
                output.accept(DARK_DECORATION_STAIRS);
                output.accept(DARK_DECORATION_WALL);
                output.accept(DETAILED_DARK_DECORATION);
                output.accept(DETAILED_DARK_DECORATION_SLAB);
                output.accept(DETAILED_DARK_DECORATION_STAIRS);
                output.accept(DETAILED_DARK_DECORATION_WALL);

                // MOON NATURAL
                output.accept(MOON_TURF);
                output.accept(MOON_DIRT);
                output.accept(MOON_DIRT_PATH);
                output.accept(MOON_SURFACE_ROCK);

                output.accept(MOON_ROCK);
                output.accept(MOON_ROCK_SLAB);
                output.accept(MOON_ROCK_STAIRS);
                output.accept(MOON_ROCK_WALL);

                output.accept(COBBLED_MOON_ROCK);
                output.accept(COBBLED_MOON_ROCK_SLAB);
                output.accept(COBBLED_MOON_ROCK_STAIRS);
                output.accept(COBBLED_MOON_ROCK_WALL);

                output.accept(LUNASLATE);
                output.accept(LUNASLATE_SLAB);
                output.accept(LUNASLATE_STAIRS);
                output.accept(LUNASLATE_WALL);

                output.accept(COBBLED_LUNASLATE);
                output.accept(COBBLED_LUNASLATE_SLAB);
                output.accept(COBBLED_LUNASLATE_STAIRS);
                output.accept(COBBLED_LUNASLATE_WALL);

                output.accept(MOON_BASALT);
                output.accept(MOON_BASALT_SLAB);
                output.accept(MOON_BASALT_STAIRS);
                output.accept(MOON_BASALT_WALL);

                output.accept(MOON_BASALT_BRICK);
                output.accept(MOON_BASALT_BRICK_SLAB);
                output.accept(MOON_BASALT_BRICK_STAIRS);
                output.accept(MOON_BASALT_BRICK_WALL);

                output.accept(CRACKED_MOON_BASALT_BRICK);
                output.accept(CRACKED_MOON_BASALT_BRICK_SLAB);
                output.accept(CRACKED_MOON_BASALT_BRICK_STAIRS);
                output.accept(CRACKED_MOON_BASALT_BRICK_WALL);

                // MARS NATURAL
                output.accept(MARS_SURFACE_ROCK);
                output.accept(MARS_SUB_SURFACE_ROCK);
                output.accept(MARS_STONE);
                output.accept(MARS_COBBLESTONE);
                output.accept(MARS_COBBLESTONE_SLAB);
                output.accept(MARS_COBBLESTONE_STAIRS);
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

                // MISC DECOR
                output.accept(WALKWAY);
                output.accept(PIPE_WALKWAY);
                output.accept(WIRE_WALKWAY);
                output.accept(TIN_LADDER);
                output.accept(GRATING);

                // SPECIAL
                output.accept(ALUMINUM_WIRE);
                output.accept(SEALABLE_ALUMINUM_WIRE);
                output.accept(HEAVY_SEALABLE_ALUMINUM_WIRE);
                output.accept(GLASS_FLUID_PIPE);
                output.accept(ROCKET_LAUNCH_PAD);

                // LIGHT PANELS
                output.accept(SQUARE_LIGHT_PANEL);
                output.accept(SPOTLIGHT_LIGHT_PANEL);
                output.accept(LINEAR_LIGHT_PANEL);
                output.accept(DASHED_LIGHT_PANEL);
                output.accept(DIAGONAL_LIGHT_PANEL);

                // VACUUM GLASS
                output.accept(VACUUM_GLASS);
                output.accept(CLEAR_VACUUM_GLASS);
                output.accept(STRONG_VACUUM_GLASS);

                // ORES
                output.accept(SILICON_ORE);
                output.accept(DEEPSLATE_SILICON_ORE);

                output.accept(MOON_COPPER_ORE);
                output.accept(LUNASLATE_COPPER_ORE);

                output.accept(TIN_ORE);
                output.accept(DEEPSLATE_TIN_ORE);
                output.accept(MOON_TIN_ORE);
                output.accept(LUNASLATE_TIN_ORE);

                output.accept(ALUMINUM_ORE);
                output.accept(DEEPSLATE_ALUMINUM_ORE);

                output.accept(DESH_ORE);

                output.accept(ILMENITE_ORE);

                output.accept(GALENA_ORE);

                // COMPACT MINERAL BLOCKS
                output.accept(MOON_CHEESE_BLOCK);
                output.accept(SILICON_BLOCK);
                output.accept(METEORIC_IRON_BLOCK);
                output.accept(DESH_BLOCK);
                output.accept(TITANIUM_BLOCK);
                output.accept(LEAD_BLOCK);
                output.accept(LUNAR_SAPPHIRE_BLOCK);

                // MOON VILLAGER SPECIAL
                output.accept(LUNAR_CARTOGRAPHY_TABLE);

                // MISC WORLD GEN
                output.accept(CAVERNOUS_VINE);
                output.accept(POISONOUS_CAVERNOUS_VINE);

                // MISC MACHINES
                output.accept(CRYOGENIC_CHAMBER);
                output.accept(PLAYER_TRANSPORT_TUBE);

                // MACHINES
            }).build();

    public static final CreativeModeTab MACHINES_GROUP = FabricItemGroup
            .builder(new ResourceLocation(Constant.MOD_ID, Constant.Block.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GCBlocks.COAL_GENERATOR))
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
                output.accept(OXYGEN_COLLECTOR);
                output.accept(OXYGEN_SEALER);
                output.accept(OXYGEN_BUBBLE_DISTRIBUTOR);
                output.accept(OXYGEN_DECOMPRESSOR);
                output.accept(OXYGEN_COMPRESSOR);
                output.accept(OXYGEN_STORAGE_MODULE);
                output.accept(FUEL_LOADER);
            }).build();

    public static void register() {
    }
}
