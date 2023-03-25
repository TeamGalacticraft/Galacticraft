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

package dev.galacticraft.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface Constant {
    String MOD_ID = "galacticraft";
    String ADDON_API_ID = "galacticraft-api";
    String COMMON_NAMESPACE = "c";

    @Contract(value = "_ -> new", pure = true)
    static @NotNull ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);  
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull <T> ResourceKey<T> key(ResourceKey<Registry<T>> registry, String id) {
        return ResourceKey.create(registry, Constant.id(id));
    }

    interface Block {
        String ITEM_GROUP_BLOCKS = "blocks";
        String ITEM_GROUP_MACHINES = "machines";

        // Natural
        String MOON_TURF = "moon_turf";
        String MOON_SURFACE_ROCK = "moon_surface_rock";
        String MOON_ROCK = "moon_rock";
        String MOON_ROCK_SLAB = "moon_rock_slab";
        String MOON_ROCK_STAIRS = "moon_rock_stairs";
        String MOON_ROCK_WALL = "moon_rock_wall";
        String COBBLED_MOON_ROCK = "cobbled_moon_rock";
        String COBBLED_MOON_ROCK_SLAB = "cobbled_moon_rock_slab";
        String COBBLED_MOON_ROCK_STAIRS = "cobbled_moon_rock_stairs";
        String COBBLED_MOON_ROCK_WALL = "cobbled_moon_rock_wall";
        String LUNASLATE = "lunaslate";
        String LUNASLATE_SLAB = "lunaslate_slab";
        String LUNASLATE_STAIRS = "lunaslate_stairs";
        String LUNASLATE_WALL = "lunaslate_wall";
        String COBBLED_LUNASLATE = "cobbled_lunaslate";
        String COBBLED_LUNASLATE_SLAB = "cobbled_lunaslate_slab";
        String COBBLED_LUNASLATE_STAIRS = "cobbled_lunaslate_stairs";
        String COBBLED_LUNASLATE_WALL = "cobbled_lunaslate_wall";
        String MOON_BASALT = "moon_basalt";
        String MOON_BASALT_SLAB = "moon_basalt_slab";
        String MOON_BASALT_STAIRS = "moon_basalt_stairs";
        String MOON_BASALT_WALL = "moon_basalt_wall";
        String MOON_BASALT_BRICK = "moon_basalt_brick";
        String MOON_BASALT_BRICK_SLAB = "moon_basalt_brick_slab";
        String MOON_BASALT_BRICK_STAIRS = "moon_basalt_brick_stairs";
        String MOON_BASALT_BRICK_WALL = "moon_basalt_brick_wall";
        String CRACKED_MOON_BASALT_BRICK = "cracked_moon_basalt_brick";
        String CRACKED_MOON_BASALT_BRICK_SLAB = "cracked_moon_basalt_brick_slab";
        String CRACKED_MOON_BASALT_BRICK_STAIRS = "cracked_moon_basalt_brick_stairs";
        String CRACKED_MOON_BASALT_BRICK_WALL = "cracked_moon_basalt_brick_wall";

        String MOON_DIRT = "moon_dirt";
        String MARS_SURFACE_ROCK = "mars_surface_rock";
        String MARS_SUB_SURFACE_ROCK = "mars_sub_surface_rock";
        String MARS_STONE = "mars_stone";
        String MARS_STONE_SLAB = "mars_stone_slab";
        String MARS_STONE_STAIRS = "mars_stone_stairs";
        String MARS_STONE_WALL = "mars_stone_wall";
        String MARS_COBBLESTONE = "mars_cobblestone";
        String MARS_COBBLESTONE_SLAB = "mars_cobblestone_slab";
        String MARS_COBBLESTONE_STAIRS = "mars_cobblestone_stairs";
        String MARS_COBBLESTONE_WALL = "mars_cobblestone_wall";

        String ASTEROID_ROCK = "asteroid_rock";
        String ASTEROID_ROCK_1 = "asteroid_rock_block_1";
        String ASTEROID_ROCK_2 = "asteroid_rock_block_2";

        String SOFT_VENUS_ROCK = "soft_venus_rock";
        String HARD_VENUS_ROCK = "hard_venus_rock";
        String SCORCHED_VENUS_ROCK = "scorched_venus_rock";
        String VOLCANIC_ROCK = "volcanic_rock";
        String PUMICE = "pumice";
        String VAPOR_SPOUT = "vapor_spout";

        // Ore
        String MOON_COPPER_ORE = "moon_copper_ore";
        String LUNASLATE_COPPER_ORE = "lunaslate_copper_ore";
        String DESH_ORE = "desh_ore";
        String ILMENITE_ORE = "ilmenite_ore";
        String SILICON_ORE = "silicon_ore";
        String DEEPSLATE_SILICON_ORE = "deepslate_silicon_ore";
        String TIN_ORE = "tin_ore";
        String DEEPSLATE_TIN_ORE = "deepslate_tin_ore";
        String MOON_TIN_ORE = "moon_tin_ore";
        String LUNASLATE_TIN_ORE = "lunaslate_tin_ore";
        String ALUMINUM_ORE = "aluminum_ore";
        String DEEPSLATE_ALUMINUM_ORE = "deepslate_aluminum_ore";
        String GALENA_ORE = "galena_ore";

        // Solid Blocks
        String SILICON_BLOCK = "silicon_block";
        String METEORIC_IRON_BLOCK = "meteoric_iron_block";
        String DESH_BLOCK = "desh_block";
        String TITANIUM_BLOCK = "titanium_block";
        String LEAD_BLOCK = "lead_block";
        String LUNAR_SAPPHIRE_BLOCK = "lunar_sapphire_block";

        // Decorative BLocks
        String ALUMINUM_DECORATION = "aluminum_decoration";
        String ALUMINUM_DECORATION_SLAB = "aluminum_decoration_slab";
        String ALUMINUM_DECORATION_STAIRS = "aluminum_decoration_stairs";
        String ALUMINUM_DECORATION_WALL = "aluminum_decoration_wall";
        String DETAILED_ALUMINUM_DECORATION = "detailed_aluminum_decoration";
        String DETAILED_ALUMINUM_DECORATION_SLAB = "detailed_aluminum_decoration_slab";
        String DETAILED_ALUMINUM_DECORATION_STAIRS = "detailed_aluminum_decoration_stairs";
        String DETAILED_ALUMINUM_DECORATION_WALL = "detailed_aluminum_decoration_wall";

        String BRONZE_DECORATION = "bronze_decoration";
        String BRONZE_DECORATION_SLAB = "bronze_decoration_slab";
        String BRONZE_DECORATION_STAIRS = "bronze_decoration_stairs";
        String BRONZE_DECORATION_WALL = "bronze_decoration_wall";
        String DETAILED_BRONZE_DECORATION = "detailed_bronze_decoration";
        String DETAILED_BRONZE_DECORATION_SLAB = "detailed_bronze_decoration_slab";
        String DETAILED_BRONZE_DECORATION_STAIRS = "detailed_bronze_decoration_stairs";
        String DETAILED_BRONZE_DECORATION_WALL = "detailed_bronze_decoration_wall";

        String COPPER_DECORATION = "copper_decoration";
        String COPPER_DECORATION_SLAB = "copper_decoration_slab";
        String COPPER_DECORATION_STAIRS = "copper_decoration_stairs";
        String COPPER_DECORATION_WALL = "copper_decoration_wall";
        String DETAILED_COPPER_DECORATION = "detailed_copper_decoration";
        String DETAILED_COPPER_DECORATION_SLAB = "detailed_copper_decoration_slab";
        String DETAILED_COPPER_DECORATION_STAIRS = "detailed_copper_decoration_stairs";
        String DETAILED_COPPER_DECORATION_WALL = "detailed_copper_decoration_wall";

        String IRON_DECORATION = "iron_decoration";
        String IRON_DECORATION_SLAB = "iron_decoration_slab";
        String IRON_DECORATION_STAIRS = "iron_decoration_stairs";
        String IRON_DECORATION_WALL = "iron_decoration_wall";
        String DETAILED_IRON_DECORATION = "detailed_iron_decoration";
        String DETAILED_IRON_DECORATION_SLAB = "detailed_iron_decoration_slab";
        String DETAILED_IRON_DECORATION_STAIRS = "detailed_iron_decoration_stairs";
        String DETAILED_IRON_DECORATION_WALL = "detailed_iron_decoration_wall";

        String METEORIC_IRON_DECORATION = "meteoric_iron_decoration";
        String METEORIC_IRON_DECORATION_SLAB = "meteoric_iron_decoration_slab";
        String METEORIC_IRON_DECORATION_STAIRS = "meteoric_iron_decoration_stairs";
        String METEORIC_IRON_DECORATION_WALL = "meteoric_iron_decoration_wall";
        String DETAILED_METEORIC_IRON_DECORATION = "detailed_meteoric_iron_decoration";
        String DETAILED_METEORIC_IRON_DECORATION_SLAB = "detailed_meteoric_iron_decoration_slab";
        String DETAILED_METEORIC_IRON_DECORATION_STAIRS = "detailed_meteoric_iron_decoration_stairs";
        String DETAILED_METEORIC_IRON_DECORATION_WALL = "detailed_meteoric_iron_decoration_wall";

        String STEEL_DECORATION = "steel_decoration";
        String STEEL_DECORATION_SLAB = "steel_decoration_slab";
        String STEEL_DECORATION_STAIRS = "steel_decoration_stairs";
        String STEEL_DECORATION_WALL = "steel_decoration_wall";
        String DETAILED_STEEL_DECORATION = "detailed_steel_decoration";
        String DETAILED_STEEL_DECORATION_SLAB = "detailed_steel_decoration_slab";
        String DETAILED_STEEL_DECORATION_STAIRS = "detailed_steel_decoration_stairs";
        String DETAILED_STEEL_DECORATION_WALL = "detailed_steel_decoration_wall";

        String TIN_DECORATION = "tin_decoration";
        String TIN_DECORATION_SLAB = "tin_decoration_slab";
        String TIN_DECORATION_STAIRS = "tin_decoration_stairs";
        String TIN_DECORATION_WALL = "tin_decoration_wall";
        String DETAILED_TIN_DECORATION = "detailed_tin_decoration";
        String DETAILED_TIN_DECORATION_SLAB = "detailed_tin_decoration_slab";
        String DETAILED_TIN_DECORATION_STAIRS = "detailed_tin_decoration_stairs";
        String DETAILED_TIN_DECORATION_WALL = "detailed_tin_decoration_wall";

        String TITANIUM_DECORATION = "titanium_decoration";
        String TITANIUM_DECORATION_SLAB = "titanium_decoration_slab";
        String TITANIUM_DECORATION_STAIRS = "titanium_decoration_stairs";
        String TITANIUM_DECORATION_WALL = "titanium_decoration_wall";
        String DETAILED_TITANIUM_DECORATION = "detailed_titanium_decoration";
        String DETAILED_TITANIUM_DECORATION_SLAB = "detailed_titanium_decoration_slab";
        String DETAILED_TITANIUM_DECORATION_STAIRS = "detailed_titanium_decoration_stairs";
        String DETAILED_TITANIUM_DECORATION_WALL = "detailed_titanium_decoration_wall";

        String DARK_DECORATION = "dark_decoration";
        String DARK_DECORATION_SLAB = "dark_decoration_slab";
        String DARK_DECORATION_STAIRS = "dark_decoration_stairs";
        String DARK_DECORATION_WALL = "dark_decoration_wall";
        String DETAILED_DARK_DECORATION = "detailed_dark_decoration";
        String DETAILED_DARK_DECORATION_SLAB = "detailed_dark_decoration_slab";
        String DETAILED_DARK_DECORATION_STAIRS = "detailed_dark_decoration_stairs";
        String DETAILED_DARK_DECORATION_WALL = "detailed_dark_decoration_wall";

        String GRATING = "grating";
        String TIN_LADDER = "tin_ladder";
        String SQUARE_LIGHT_PANEL = "square_light_panel";
        String SPOTLIGHT_LIGHT_PANEL = "spotlight_light_panel";
        String LINEAR_LIGHT_PANEL = "linear_light_panel";
        String DASHED_LIGHT_PANEL = "dashed_light_panel";
        String DIAGONAL_LIGHT_PANEL = "diagonal_light_panel";
        String VACUUM_GLASS = "vacuum_glass";
        String CLEAR_VACUUM_GLASS = "vacuum_glass_clear";
        String STRONG_VACUUM_GLASS = "vacuum_glass_strong";
        String WALKWAY = "walkway";
        String WIRE_WALKWAY = "wire_walkway";
        String PIPE_WALKWAY = "pipe_walkway";

        //  Environment
        String GLOWSTONE_TORCH = "glowstone_torch";
        String GLOWSTONE_WALL_TORCH = "glowstone_wall_torch";
        String GLOWSTONE_LANTERN = "glowstone_lantern";
        String UNLIT_TORCH = "unlit_torch";
        String UNLIT_WALL_TORCH = "unlit_wall_torch";
        String UNLIT_LANTERN = "unlit_lantern";
        String CAVERNOUS_VINE = "cavernous_vine";
        String POISONOUS_CAVERNOUS_VINE = "poisonous_cavernous_vine";
        String MOON_BERRY_BUSH = "moon_berry_bush";
        String WEB_TORCH = "web_torch";
        String FALLEN_METEOR = "fallen_meteor";
        String SLIMELING_EGG = "slimeling_egg";
        String CREEPER_EGG = "creeper_egg";

        // Special
        String PARACHEST = "parachest";
        String SPACE_STATION_ARRIVAL = "space_station_arrival";
        String TREASURE_CHEST_TIER_1 = "treasure_chest_tier_1";
        String TREASURE_CHEST_TIER_2 = "treasure_chest_tier_2";
        String TREASURE_CHEST_TIER_3 = "treasure_chest_tier_3";
        String MOON_CHEESE_BLOCK = "moon_cheese_block";
        String CRASHED_PROBE_BLOCK = "crashed_probe";

        // Liquids
        String FUEL = "fuel";
        String CRUDE_OIL = "crude_oil";

        // Machines
        String CIRCUIT_FABRICATOR = "circuit_fabricator";
        String COMPRESSOR = "compressor";
        String ELECTRIC_COMPRESSOR = "electric_compressor";
        String ELECTRIC_FURNACE = "electric_furnace";
        String ELECTRIC_ARC_FURNACE = "electric_arc_furnace";
        String OXYGEN_BUBBLE_DISTRIBUTOR = "oxygen_bubble_distributor";
        String OXYGEN_COLLECTOR = "oxygen_collector";
        String OXYGEN_COMPRESSOR = "oxygen_compressor";
        String OXYGEN_DECOMPRESSOR = "oxygen_decompressor";
        String OXYGEN_DETECTOR = "oxygen_detector";
        String OXYGEN_SEALER = "oxygen_sealer";
        String FLUID_PIPE = "fluid_pipe";
        String GLASS_FLUID_PIPE = "glass_fluid_pipe";
        String REFINERY = "refinery";
        String TERRAFORMER = "terraformer";
        String DECONSTRUCTOR = "deconstructor";
        String WATER_ELECTROLYZER = "water_electrolyzer";
        String METHANE_SYNTHESIZIER = "methane_synthesizer";
        String GAS_LIQUEFIER = "gas_liquefier";

        // Pad Blocks
        String BUGGY_FUELING_PAD = "buggy_fueling";
        String ROCKET_LAUNCH_PAD = "rocket_launch_pad";
        String FUEL_LOADER = "fuel_loader";
        String CARGO_LOADER = "cargo_loader";
        String CARGO_UNLOADER = "cargo_unloader";
        String LAUNCH_CONTROLLER = "launch_controller";

        // Space Base
        String HYDRAULIC_PLATFORM = "hydraulic_platform";
        String MAGNETIC_CRAFTING_TABLE = "magnetic_crafting_table";
        String NASA_WORKBENCH = "nasa_workbench";
        String AIR_LOCK_FRAME = "air_lock_frame";
        String AIR_LOCK_CONTROLLER = "air_lock_controller";
        String AIR_LOCK_SEAL = "air_lock_seal";
        String CHROMATIC_APPLICATOR = "chromatic_applicator";
        String DISPLAY_SCREEN = "display_screen";
        String TELEMETRY_UNIT = "telemetry_unit";
        String COMMUNICATIONS_DISH = "communications_dish";
        String ARC_LAMP = "arc_lamp";
        String SPIN_THRUSTER = "spin_thruster";
        String CRYOGENIC_CHAMBER = "cryogenic_chamber";
        String CRYOGENIC_CHAMBER_PART = "cryogenic_chamber_part";
        String ASTRO_MINER_BASE = "astro_miner_base";
        String SHORT_RANGE_TELEPAD = "short_range_telepad";
        String PLAYER_TRANSPORT_TUBE = "player_transport_tube";
        String HYPERLOOP = "hyperloop";

        // Power
        String BASIC_SOLAR_PANEL = "basic_solar_panel";
        String SOLAR_PANEL_PART = "solar_panel_part";
        String ADVANCED_SOLAR_PANEL = "advanced_solar_panel";
        String COAL_GENERATOR = "coal_generator";
        String GEOTHERMAL_GENERATOR = "geothermal_generator";
        String ENERGY_STORAGE_MODULE = "energy_storage_module";
        String ENERGY_STORAGE_CLUSTER = "energy_storage_cluster";
        String ALUMINUM_WIRE = "aluminum_wire";
        String HEAVY_ALUMINUM_WIRE = "heavy_aluminum_wire";
        String SWITCHABLE_ALUMINUM_WIRE = "switchable_aluminum_wire";
        String SEALABLE_ALUMINUM_WIRE = "sealable_aluminum_wire";
        String HEAVY_SEALABLE_ALUMINUM_WIRE = "heavy_sealable_aluminum_wire";
        String BEAM_REFLECTOR = "beam_reflector";
        String BEAM_RECEIVER = "beam_receiver";
        String SOLAR_ARRAY_MODULE = "solar_array_module";
        String OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK = "oxygen_distributor_bubble_dummy_block";
        String LUNAR_CARTOGRAPHY_TABLE = "lunar_cartography_table";
        String OXYGEN_STORAGE_MODULE = "oxygen_storage_module";
        String MOON_DIRT_PATH = "moon_dirt_path";
    }

    interface Fluid {
        String CRUDE_OIL_FLOWING = "crude_oil_flowing";
        String CRUDE_OIL_STILL = "crude_oil_still";
        String FUEL_FLOWING = "fuel_flowing";
        String FUEL_STILL = "fuel_still";
        String BACTERIAL_ACID_FLOWING = "bacterial_acid_flowing";
        String BACTERIAL_ACID_STILL = "bacterial_acid_still";
        String SULFURIC_ACID_FLOWING = "sulfuric_acid_flowing";
        String SULFURIC_ACID_STILL = "sulfuric_acid_still";
        String OXYGEN_GAS = "oxygen_gas";
        String LIQUID_OXYGEN = "liquid_oxygen";

        static ResourceLocation getId(String s) {
            return new ResourceLocation(Constant.MOD_ID, "block/" + s);
        }
    }

    interface Item {
        String ITEM_GROUP = "items";
        String RAW_SILICON = "raw_silicon";
        String RAW_METEORIC_IRON = "raw_meteoric_iron";
        String METEORIC_IRON_INGOT = "meteoric_iron_ingot";
        String METEORIC_IRON_NUGGET = "meteoric_iron_nugget";
        String RAW_DESH = "raw_desh";
        String DESH_INGOT = "desh_ingot";
        String DESH_NUGGET = "desh_nugget";
        String RAW_LEAD = "raw_lead";
        String LEAD_INGOT = "lead_ingot";
        String LEAD_NUGGET = "lead_nugget";
        String RAW_ALUMINUM = "raw_aluminum";
        String ALUMINUM_INGOT = "aluminum_ingot";
        String ALUMINUM_NUGGET = "aluminum_nugget";
        String RAW_TIN = "raw_tin";
        String TIN_INGOT = "tin_ingot";
        String TIN_NUGGET = "tin_nugget";
        String RAW_TITANIUM = "raw_titanium";
        String TITANIUM_INGOT = "titanium_ingot";
        String TITANIUM_NUGGET = "titanium_nugget";
        String STEEL_INGOT = "steel_ingot";
        String LUNAR_SAPPHIRE = "lunar_sapphire";
        String DESH_STICK = "desh_stick";
        String CARBON_FRAGMENTS = "carbon_fragments";
        String IRON_SHARD = "iron_shard";
        String SOLAR_DUST = "solar_dust";
        String BASIC_WAFER = "basic_wafer";
        String ADVANCED_WAFER = "advanced_wafer";
        String BEAM_CORE = "beam_core";
        String CANVAS = "canvas";
        String COMPRESSED_ALUMINUM = "compressed_aluminum";
        String COMPRESSED_COPPER = "compressed_copper";
        String COMPRESSED_TIN = "compressed_tin";
        String COMPRESSED_BRONZE = "compressed_bronze";
        String COMPRESSED_IRON = "compressed_iron";
        String COMPRESSED_STEEL = "compressed_steel";
        String COMPRESSED_METEORIC_IRON = "compressed_meteoric_iron";
        String COMPRESSED_DESH = "compressed_desh";
        String COMPRESSED_TITANIUM = "compressed_titanium";
        String FLUID_MANIPULATOR = "fluid_manipulator";
        String OXYGEN_CONCENTRATOR = "oxygen_concentrator";
        String OXYGEN_FAN = "oxygen_fan";
        String OXYGEN_VENT = "oxygen_vent";
        String SENSOR_LENS = "sensor_lens";
        String BLUE_SOLAR_WAFER = "blue_solar_wafer";
        String SINGLE_SOLAR_MODULE = "single_solar_module";
        String FULL_SOLAR_PANEL = "full_solar_panel";
        String SOLAR_ARRAY_WAFER = "solar_array_wafer";
        String STEEL_POLE = "steel_pole";
        String COPPER_CANISTER = "copper_canister";
        String TIN_CANISTER = "tin_canister";
        String THERMAL_CLOTH = "thermal_cloth";
        String ISOTHERMAL_FABRIC = "thermal_cloth_t2";
        String ORION_DRIVE = "orion_drive";
        String ATMOSPHERIC_VALVE = "atmospheric_valve";
        String AMBIENT_THERMAL_CONTROLLER = "ambient_thermal_controller";
        String LIQUID_CANISTER = "liquid_canister";
        //FOOD
        String MOON_BERRIES = "moon_berries";
        String CHEESE_CURD = "cheese_curd";
        String CHEESE_SLICE = "cheese_slice";
        String BURGER_BUN = "burger_bun";
        String GROUND_BEEF = "ground_beef";
        String BEEF_PATTY = "beef_patty";
        String CHEESEBURGER = "cheeseburger";
        //CANNED FOOD
        String CANNED_DEHYDRATED_APPLE = "canned_dehydrated_apple";
        String CANNED_DEHYDRATED_CARROT = "canned_dehydrated_carrot";
        String CANNED_DEHYDRATED_MELON = "canned_dehydrated_melon";
        String CANNED_DEHYDRATED_POTATO = "canned_dehydrated_potato";
        String CANNED_BEEF = "canned_beef";
        //ROCKET PARTS
        String TIER_1_HEAVY_DUTY_PLATE = "heavy_plating";
        String TIER_2_HEAVY_DUTY_PLATE = "heavy_plating_t2";
        String TIER_3_HEAVY_DUTY_PLATE = "heavy_plating_t3";
        //THROWABLE METEOR CHUNKS
        String THROWABLE_METEOR_CHUNK = "throwable_meteor_chunk";
        String HOT_THROWABLE_METEOR_CHUNK = "hot_throwable_meteor_chunk";
        //ARMOR
        String HEAVY_DUTY_HELMET = "heavy_duty_helmet";
        String HEAVY_DUTY_CHESTPLATE = "heavy_duty_chestplate";
        String HEAVY_DUTY_LEGGINGS = "heavy_duty_leggings";
        String HEAVY_DUTY_BOOTS = "heavy_duty_boots";
        String DESH_HELMET = "desh_helmet";
        String DESH_CHESTPLATE = "desh_chestplate";
        String DESH_LEGGINGS = "desh_leggings";
        String DESH_BOOTS = "desh_boots";
        String TITANIUM_HELMET = "titanium_helmet";
        String TITANIUM_CHESTPLATE = "titanium_chestplate";
        String TITANIUM_LEGGINGS = "titanium_leggings";
        String TITANIUM_BOOTS = "titanium_boots";
        String SENSOR_GLASSES = "sensor_glasses";
        //TOOLS + WEAPONS
        String HEAVY_DUTY_SWORD = "heavy_duty_sword";
        String HEAVY_DUTY_SHOVEL = "heavy_duty_shovel";
        String HEAVY_DUTY_PICKAXE = "heavy_duty_pickaxe";
        String HEAVY_DUTY_AXE = "heavy_duty_axe";
        String HEAVY_DUTY_HOE = "heavy_duty_hoe";

        String DESH_SWORD = "desh_sword";
        String DESH_SHOVEL = "desh_shovel";
        String DESH_PICKAXE = "desh_pickaxe";
        String DESH_AXE = "desh_axe";
        String DESH_HOE = "desh_hoe";

        String TITANIUM_SWORD = "titanium_sword";
        String TITANIUM_SHOVEL = "titanium_shovel";
        String TITANIUM_PICKAXE = "titanium_pickaxe";
        String TITANIUM_AXE = "titanium_axe";
        String TITANIUM_HOE = "titanium_hoe";

        String STANDARD_WRENCH = "standard_wrench";
        String BATTERY = "battery";
        String INFINITE_BATTERY = "infinite_battery";

        //Fluid buckets
        String CRUDE_OIL_BUCKET = "crude_oil_bucket";
        String FUEL_BUCKET = "fuel_bucket";

        //GC INVENTORY
        String PARACHUTE = "parachute";
        String ORANGE_PARACHUTE = "orange_parachute";
        String MAGENTA_PARACHUTE = "magenta_parachute";
        String LIGHT_BLUE_PARACHUTE = "light_blue_parachute";
        String YELLOW_PARACHUTE = "yellow_parachute";
        String LIME_PARACHUTE = "lime_parachute";
        String PINK_PARACHUTE = "pink_parachute";
        String GRAY_PARACHUTE = "gray_parachute";
        String LIGHT_GRAY_PARACHUTE = "light_gray_parachute";
        String CYAN_PARACHUTE = "cyan_parachute";
        String PURPLE_PARACHUTE = "purple_parachute";
        String BLUE_PARACHUTE = "blue_parachute";
        String BROWN_PARACHUTE = "brown_parachute";
        String GREEN_PARACHUTE = "green_parachute";
        String RED_PARACHUTE = "red_parachute";
        String BLACK_PARACHUTE = "black_parachute";

        String OXYGEN_MASK = "oxygen_mask";
        String OXYGEN_GEAR = "oxygen_gear";

        String SHIELD_CONTROLLER = "shield_controller";
        String FREQUENCY_MODULE = "frequency_module";

        String SMALL_OXYGEN_TANK = "small_oxygen_tank";
        String MEDIUM_OXYGEN_TANK = "medium_oxygen_tank";
        String LARGE_OXYGEN_TANK = "large_oxygen_tank";
        String INFINITE_OXYGEN_TANK = "infinite_oxygen_tank";

        String THERMAL_PADDING_HELMET = "thermal_padding_helmet";
        String THERMAL_PADDING_CHESTPIECE = "thermal_padding_chestpiece";
        String THERMAL_PADDING_LEGGINGS = "thermal_padding_leggings";
        String THERMAL_PADDING_BOOTS = "thermal_padding_boots";

        String ISOTHERMAL_PADDING_HELMET = "isothermal_padding_helmet";
        String ISOTHERMAL_PADDING_CHESTPIECE = "isothermal_padding_chestpiece";
        String ISOTHERMAL_PADDING_LEGGINGS = "isothermal_padding_leggings";
        String ISOTHERMAL_PADDING_BOOTS = "isothermal_padding_boots";

        String ROCKET = "rocket";

        String TIER_2_ROCKET_SCHEMATIC = "tier_2_rocket_schematic";
        String TIER_3_ROCKET_SCHEMATIC = "tier_3_rocket_schematic";
        String CARGO_ROCKET_SCHEMATIC = "cargo_rocket_schematic";
        String MOON_BUGGY_SCHEMATIC = "moon_buggy_schematic";
        String ASTRO_MINER_SCHEMATIC = "astro_miner_schematic";

        String EVOLVED_ZOMBIE_SPAWN_EGG = "evolved_zombie_spawn_egg";
        String EVOLVED_PILLAGER_SPAWN_EGG = "evolved_pillager_spawn_egg";
        String EVOLVED_VINDICATOR_SPAWN_EGG = "evolved_vindicator_spawn_egg";
        String EVOLVED_EVOKER_SPAWN_EGG = "evolved_evoker_spawn_egg";
        String EVOLVED_SPIDER_SPAWN_EGG = "evolved_spider_spawn_egg";
        String EVOLVED_SKELETON_SPAWN_EGG = "evolved_skeleton_spawn_egg";
        String EVOLVED_CREEPER_SPAWN_EGG = "evolved_creeper_spawn_egg";

        String LEGACY_MUSIC_DISC_MARS = "legacy_music_disc_mars";
        String LEGACY_MUSIC_DISC_MIMAS = "legacy_music_disc_mimas";
        String LEGACY_MUSIC_DISC_ORBIT = "legacy_music_disc_orbit";
        String LEGACY_MUSIC_DISC_SPACERACE = "legacy_music_disc_spacerace";
    }

    interface Particle {
        String DRIPPING_FUEL_PARTICLE = "dripping_fuel_particle";
        String DRIPPING_CRUDE_OIL_PARTICLE = "dripping_crude_oil_particle";
        String CRYOGENIC_PARTICLE = "cryogenic_particle";
    }

    interface Config {
        String TITLE = "config.galacticraft.title";
        String RESET = "config.galacticraft.reset";

        String DEBUG = "config.galacticraft.debug";
        String DEBUG_LOGGING = "config.galacticraft.debug.logging";
        String HIDE_ALPHA_WARNING = "config.galacticraft.debug.hide_alpha_warning";

        String ENERGY = "config.galacticraft.energy";

        String WIRES = "config.galacticraft.energy.wires";
        String WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft.energy.wires.transfer_limit";
        String HEAVY_WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft.energy.wires.heavy_transfer_limit";

        String MACHINES = "config.galacticraft.energy.machines";
        String COAL_GENERATOR_ENERGY_PRODUCTION_RATE = "config.galacticraft.energy.machines.coal_generator_energy_production_rate";
        String SOLAR_PANEL_ENERGY_PRODUCTION_RATE = "config.galacticraft.energy.machines.solar_panel_energy_production_rate";
        String CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.circuit_fabricator_energy_consumption_rate";
        String ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_compressor_energy_consumption_rate";
        String OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_collector_energy_consumption_rate";
        String REFINERY_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.refinery_energy_consumption_rate";
        String ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_furnace_energy_consumption_rate";
        String ENERGY_STORAGE_MODULE_STORAGE_SIZE = "config.galacticraft.energy.machines.energy_storage_module_storage_size";
        String ENERGY_STORAGE_SIZE = "config.galacticraft.energy.machines.energy_storage_size";
        String OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_compressor_energy_consumption_rate";
        String OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_decompressor_energy_consumption_rate";

        String CLIENT = "config.galacticraft.client";
        String SKYBOX = "config.galacticraft.client.skybox";
        String MULTICOLOR_STARS = "config.galacticraft.client.skybox.multicolor_stars";
    }

    interface ScreenTexture {
        ResourceLocation COAL_GENERATOR_SCREEN = id("textures/gui/coal_generator_screen.png");
        ResourceLocation SOLAR_PANEL_SCREEN = id("textures/gui/solar_panel_screen.png");
        ResourceLocation CIRCUIT_FABRICATOR_SCREEN = id("textures/gui/circuit_fabricator_screen.png");
        ResourceLocation REFINERY_SCREEN = id("textures/gui/refinery_screen.png");
        ResourceLocation ELECTRIC_FURNACE_SCREEN = id("textures/gui/electric_furnace_screen.png");
        ResourceLocation ELECTRIC_ARC_FURNACE_SCREEN = id("textures/gui/electric_arc_furnace_screen.png");
        ResourceLocation COMPRESSOR_SCREEN = id("textures/gui/compressor_screen.png");
        ResourceLocation ELECTRIC_COMPRESSOR_SCREEN = id("textures/gui/electric_compressor_screen.png");
        ResourceLocation ENERGY_STORAGE_MODULE_SCREEN = id("textures/gui/energy_storage_module_screen.png");
        ResourceLocation OXYGEN_COLLECTOR_SCREEN = id("textures/gui/oxygen_collector_screen.png");

        ResourceLocation MACHINE_CONFIG_PANELS = id("textures/gui/machine_config.png");
        ResourceLocation PLAYER_INVENTORY_SCREEN = id("textures/gui/player_inventory_screen.png");
        ResourceLocation PLAYER_INVENTORY_TABS = id("textures/gui/player_inventory_switch_tabs.png");
        ResourceLocation OVERLAY = id("textures/gui/overlay.png");

        ResourceLocation MAP_SCREEN = id("textures/gui/map.png");
        ResourceLocation PLANET_ICONS = id("textures/gui/planet_icons.png");
        ResourceLocation BUBBLE_DISTRIBUTOR_SCREEN = id("textures/gui/oxygen_bubble_distributor_screen.png");
        ResourceLocation OXYGEN_COMPRESSOR_SCREEN = id("textures/gui/oxygen_compressor_screen.png");
        ResourceLocation OXYGEN_STORAGE_MODULE_SCREEN = id("textures/gui/oxygen_storage_module_screen.png");
        ResourceLocation OXYGEN_SEALER_SCREEN = id("textures/gui/oxygen_sealer_screen.png");
        ResourceLocation FUEL_LOADER_SCREEN = id("textures/gui/fuel_loader_screen.png");
        ResourceLocation DEFAULT_SOLAR_PANELS = id("textures/solar_panel/default_solar_panels.png");
        ResourceLocation DEFAULT_LIGHT_SOURCES = id("textures/solar_panel/default_light_sources.png");
        ResourceLocation MOON_LIGHT_SOURCES = id("textures/solar_panel/moon_light_sources.png");

        ResourceLocation RECIPE_VEIWER_DISPLAY_TEXTURE = id("textures/gui/rei_display.png");
    }

    interface SlotSprite {
        String THERMAL_HEAD = "slot/thermal_helmet";
        String THERMAL_CHEST = "slot/thermal_chestpiece";
        String THERMAL_PANTS = "slot/thermal_leggings";
        String THERMAL_BOOTS = "slot/thermal_boots";
        String OXYGEN_MASK = "slot/oxygen_mask";
        String OXYGEN_GEAR = "slot/oxygen_gear";
        String OXYGEN_TANK = "slot/oxygen_tank";
    }

    interface Entity {
        String EVOLVED_ZOMBIE = "evolved_zombie";
        String EVOLVED_CREEPER = "evolved_creeper";
        String T1_ROCKET = "t1_rocket";
        String ROCKET = "rocket";
        String BUBBLE = "bubble";
        String EVOLVED_SKELETON = "evolved_skeleton";
        String EVOLVED_SPIDER = "evolved_spider";
        String EVOLVED_PILLAGER = "evolved_pillager";
        String EVOLVED_EVOKER = "evolved_evoker";
        String EVOLVED_VINDICATOR = "evolved_vindicator";
        String GREY = "grey";
        String ARCH_GREY = "arch_grey";
        String RUMBLER = "rumbler";
        String OLI_GRUB = "oli_grub";
        String COMET_CUBE = "comet_cube";
        String GAZER = "gazer";
    }

    interface EntityTexture {
        String GREY = "textures/entity/grey.png";
        String ARCH_GREY = "textures/entity/arch_grey.png";
        String RUMBLER = "textures/entity/rumbler.png";
        String OLI_GRUB = "textures/entity/oli_grub.png";
        String COMET_CUBE = "textures/entity/comet_cube.png";
        String GAZER = "textures/entity/gazer.png";
    }

    interface TextureCoordinate {
        int OVERLAY_WIDTH = 16;
        int OVERLAY_HEIGHT = 48;

        int ENERGY_DARK_X = 0;
        int ENERGY_DARK_Y = 0;
        int ENERGY_LIGHT_X = 16;
        int ENERGY_LIGHT_Y = 0;

        int OXYGEN_DARK_X = 0;
        int OXYGEN_DARK_Y = 50;
        int OXYGEN_LIGHT_X = 16;
        int OXYGEN_LIGHT_Y = 50;

        int FLUID_TANK_WIDTH = 18;

        int BASE_FLUID_TANK_Y = 49;

        int BUTTON_WIDTH = 13;
        int BUTTON_HEIGHT = 13;

        int BUTTON_RED_X = 0;
        int BUTTON_RED_Y = 115;
        int BUTTON_RED_HOVER_X = 0;
        int BUTTON_RED_HOVER_Y = 102;

        int BUTTON_GREEN_X = 13;
        int BUTTON_GREEN_Y = 115;
        int BUTTON_GREEN_HOVER_X = 13;
        int BUTTON_GREEN_HOVER_Y = 102;

        int BUTTON_NORMAL_X = 26;
        int BUTTON_NORMAL_Y = 115;
        int BUTTON_NORMAL_HOVER_X = 26;
        int BUTTON_NORMAL_HOVER_Y = 102;

        int ARROW_VERTICAL_WIDTH = 11;
        int ARROW_VERTICAL_HEIGHT = 10;

        int ARROW_UP_X = 39;
        int ARROW_UP_Y = 108;
        int ARROW_UP_HOVER_X = 50;
        int ARROW_UP_HOVER_Y = 108;

        int ARROW_DOWN_X = 39;
        int ARROW_DOWN_Y = 118;
        int ARROW_DOWN_HOVER_X = 50;
        int ARROW_DOWN_HOVER_Y = 118;

    }

    interface Menu {
        String COAL_GENERATOR_MENU = "coal_generator_menu";
        String BASIC_SOLAR_PANEL_MENU = "basic_solar_panel_menu";
        String ADVANCED_SOLAR_PANEL_MENU = "advanced_solar_panel_menu";
        String CIRCUIT_FABRICATOR_MENU = "circuit_fabricator_menu";
        String COMPRESSOR_MENU = "compressor_menu";
        String ELECTRIC_COMPRESSOR_MENU = "electric_compressor_menu";
        String PLAYER_INVENTORY_MENU = "player_inventory_menu";
        String ENERGY_STORAGE_MODULE_MENU = "energy_storage_module_menu";
        String REFINERY_MENU = "refinery_menu";
        String ELECTRIC_FURNACE_MENU = "electric_furnace_menu";
        String ELECTRIC_ARC_FURNACE_MENU = "electric_arc_furnace_menu";
        String OXYGEN_COLLECTOR_MENU = "oxygen_collector_menu";
        String BUBBLE_DISTRIBUTOR_MENU = "bubble_distributor_menu";
        String OXYGEN_COMPRESSOR_MENU = "oxygen_compressor_menu";
        String OXYGEN_DECOMPRESSOR_MENU = "oxygen_decompressor_menu";
        String OXYGEN_STORAGE_MODULE_MENU = "oxygen_storage_module_menu";
        String OXYGEN_SEALER_MENU = "oxygen_sealer_menu";
        String FUEL_LOADER_MENU = "fuel_loader_menu";
        String AIR_LOCK_CONTROLLER_MENU = "air_lock_menu";
    }

    interface LootTable {
        String BASIC_MOON_RUINS_CHEST = "chests/moon_ruins/basic_chest";
    }

    interface Text {
        interface Color {
            Style DARK_GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
            Style GOLD_STYLE = Style.EMPTY.withColor(ChatFormatting.GOLD);
            Style GREEN_STYLE = Style.EMPTY.withColor(ChatFormatting.GREEN);
            Style RED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
            Style BLUE_STYLE = Style.EMPTY.withColor(ChatFormatting.BLUE);
            Style AQUA_STYLE = Style.EMPTY.withColor(ChatFormatting.AQUA);
            Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
            Style DARK_RED_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_RED);
            Style LIGHT_PURPLE_STYLE = Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
            Style YELLOW_STYLE = Style.EMPTY.withColor(ChatFormatting.YELLOW);
            Style WHITE_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE);
            Style DARK_BLUE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_BLUE);

            static Style getStorageLevelColor(double scale) {
                return Style.EMPTY.withColor(TextColor.fromRgb(((int)(255 * scale) << 16) + (((int)(255 * ( 1.0 - scale))) << 8)));
            }

            static Style getRainbow(int ticks) {
                return Style.EMPTY.withColor(TextColor.fromRgb(Mth.hsvToRgb(ticks / 1000.0f, 1, 1)));
            }
        }
        
        interface TranslationKey {
              String NOT_ENOUGH_LEAVES = "ui.galacticraft.machine.status.not_enough_leaves";
              String ACTIVE = "ui.galacticraft.machine.status.active";
              String IDLE = "ui.galacticraft.machine.status.idle";
              String WARMING = "ui.galacticraft.machine.status.warming";
              String INACTIVE = "ui.galacticraft.machine.status.inactive";
              String NOT_ENOUGH_ENERGY = "ui.galacticraft.machine.status.not_enough_energy";
              String OFF = "ui.galacticraft.machine.status.off";
              String PROCESSING = "ui.galacticraft.machine.status.processing";
              String COLLECTING = "ui.galacticraft.machine.status.collecting";
              String COMPRESSING = "ui.galacticraft.machine.status.compressing";
              String DECOMPRESSING = "ui.galacticraft.machine.status.decompressing";
              String PARTIALLY_BLOCKED = "ui.galacticraft.machine.status.partially_blocked";
              String NIGHT = "ui.galacticraft.machine.status.night";
              String FULL = "ui.galacticraft.machine.status.full";
              String EMPTY_CANISTER = "ui.galacticraft.machine.status.empty_canister";
              String BLOCKED = "ui.galacticraft.machine.status.blocked";
              String DISTRIBUTING = "ui.galacticraft.machine.status.distributing";
              String NOT_ENOUGH_OXYGEN = "ui.galacticraft.machine.status.not_enough_oxygen";
              String NOT_ENOUGH_ITEMS = "ui.galacticraft.machine.status.not_enough_items";
        }
    }

    interface Nbt {
        String BLOCK_ENTITY_TAG = "BlockEntityTag";
        String NO_DROP = "NoDrop";
        String OWNER = "Owner";
        String PROGRESS = "Progress";
        String SIZE = "Size";
        String MAX_SIZE = "MaxSize";
        String FUEL_TIME = "FuelTime";
        String FUEL_LENGTH = "FuelLength";
        String TEAM = "Team";
        String ACCESSIBILITY = "Accessibility";
        String SECURITY = "Security";
        String CONFIGURATION = "Configuration";
        String VALUE = "Value";
        String ENERGY = "Energy";
        String AUTOMATION_TYPE = "AutomationType";
        String BABY = "Baby";
        String DIRECTION = "Direction";
        String REDSTONE_INTERACTION_TYPE = "RedstoneInteraction";
        String MATCH = "Match";
        String IS_SLOT_ID = "IsSlotId";
        String MAX_PROGRESS = "MaxProgress";
        String COLOR = "Color";
        String PULL = "Pull";
        String HEAT = "Heat";
        String INPUTS = "Inputs";
        String OUTPUTS = "Outputs";
        String SHAPED = "Shaped";
        String ITEMS = "Items";
        String GASES = "Gases";
        String CRYOGENIC_COOLDOWN = "cryogenic_cooldown";
    }

    interface Property {
        BooleanProperty ACTIVE = BooleanProperty.create("active");
    }

    interface Energy {
        long T1_MACHINE_ENERGY_USAGE = 0;
        long T2_MACHINE_ENERGY_USAGE = 0;
    }

    @ApiStatus.Internal
    interface Misc {
        ResourceLocation EMPTY = new ResourceLocation("empty");
        Direction[] DIRECTIONS = Direction.values();
        String LOGGER_PREFIX = "[Galacticraft] ";
        boolean DEBUG = false;
        int MAX_STRING_READ = 32767;
    }

    @ApiStatus.Internal
    interface Mixin {
        String STRUCTURE_POOL_DEBUG = "StructurePoolGeneratorMixin";
        String DATAGEN_COMPRESSION = "DataProviderMixin";
    }

    interface Recipe {
        String FABRICATION = "fabrication";
        String COMPRESSING = "compressing";
        interface Serializer {
            String FABRICATION = "fabrication";
            String COMPRESSING_SHAPELESS = "compressing_shapeless";
            String COMPRESSING_SHAPED = "compressing_shaped";
        }
    }

    @Environment(EnvType.CLIENT)
    interface ModelPartName {
        String OXYGEN_MASK = "oxygen_mask";
        String OXYGEN_TANK = "oxygen_tank";
        String OXYGEN_PIPE = "oxygen_pipe";
        String MOON_VILLAGER_BRAIN = "moon_villager_brain";
        String SOLAR_PANEL_POLE = "solar_panel_pole";
        String SOLAR_PANEL_PANEL = "solar_panel_panel";
        String SOLAR_PANEL_PANEL_HORIZONTAL_1 = "solar_panel_panel_horizontal_1";
        String SOLAR_PANEL_PANEL_HORIZONTAL_2 = "solar_panel_panel_horizontal_2";
        String SOLAR_PANEL_PANEL_HORIZONTAL_3 = "solar_panel_panel_horizontal_3";
        String SOLAR_PANEL_PANEL_HORIZONTAL_4 = "solar_panel_panel_horizontal_4";
        String SOLAR_PANEL_PANEL_VERTICAL_1 = "solar_panel_panel_vertical_1";
        String SOLAR_PANEL_PANEL_VERTICAL_2 = "solar_panel_panel_vertical_2";
        String SOLAR_PANEL_PANEL_VERTICAL_3 = "solar_panel_panel_vertical_3";
    }

    interface Carver {
        String MOON_CANYON_CARVER = "moon_canyon_carver";
        String MOON_CRATER_CARVER = "moon_crater_carver";
        String MOON_HIGHLANDS_CAVE_CARVER = "moon_highlands_cave_carver";
        String MOON_MARE_CAVE_CARVER = "moon_mare_cave_carver";
    }

    interface Packet {
        ResourceLocation BUBBLE_SIZE = id("bubble_size");
        ResourceLocation BUBBLE_MAX = id("bubble_max");
        ResourceLocation BUBBLE_VISIBLE = id("bubble_visible");
        ResourceLocation DISABLE_SEAL = id("toggle_seal");
        ResourceLocation OPEN_GC_INVENTORY = id("open_gc_inv");
        ResourceLocation ENTITY_SPAWN = id("entity_spawn");
        ResourceLocation CREATE_SATELLITE = id("create_satellite");
        ResourceLocation ROCKET_JUMP = id("rocket_jump");
        ResourceLocation ROCKET_PITCH = id("rocket_pitch");
        ResourceLocation ROCKET_YAW = id("rocket_yaw");
    }

    interface Structure {
        ResourceLocation SPACE_STATION = id("space_station");
    }
}
