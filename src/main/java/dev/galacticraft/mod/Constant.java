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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Constant {
    String MOD_ID = "galacticraft";
    String COMMON_NAMESPACE = "c";
    Logger LOGGER = LogManager.getLogger("Galacticraft");

    double RADIANS_TO_DEGREES = 180.0 / Math.PI;
    int OVERWORLD_SKYPROVIDER_STARTHEIGHT = 200;

    @Contract(value = "_ -> new", pure = true)
    static @NotNull ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
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
        String MOON_DUNGEON_BRICK = "moon_dungeon_brick";

        String MOON_ROCK = "moon_rock";
        String MOON_ROCK_SLAB = "moon_rock_slab";
        String MOON_ROCK_STAIRS = "moon_rock_stairs";
        String MOON_ROCK_WALL = "moon_rock_wall";

        String MOON_ROCK_BRICK = "moon_rock_brick";
        String MOON_ROCK_BRICK_SLAB = "moon_rock_brick_slab";
        String MOON_ROCK_BRICK_STAIRS = "moon_rock_brick_stairs";
        String MOON_ROCK_BRICK_WALL = "moon_rock_brick_wall";

        String CRACKED_MOON_ROCK_BRICK = "cracked_moon_rock_brick";
        String CRACKED_MOON_ROCK_BRICK_SLAB = "cracked_moon_rock_brick_slab";
        String CRACKED_MOON_ROCK_BRICK_STAIRS = "cracked_moon_rock_brick_stairs";
        String CRACKED_MOON_ROCK_BRICK_WALL = "cracked_moon_rock_brick_wall";

        String POLISHED_MOON_ROCK = "polished_moon_rock";
        String POLISHED_MOON_ROCK_SLAB = "polished_moon_rock_slab";
        String POLISHED_MOON_ROCK_STAIRS = "polished_moon_rock_stairs";
        String POLISHED_MOON_ROCK_WALL = "polished_moon_rock_wall";

        String CHISELED_MOON_ROCK_BRICK = "chiseled_moon_rock_brick";
        String MOON_ROCK_PILLAR = "moon_rock_pillar";

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

        String OLIVINE_CLUSTER = "olivine_cluster";
        String OLIVINE_BASALT = "olivine_basalt";
        String RICH_OLIVINE_BASALT = "rich_olivine_basalt";

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
        String SILICON_ORE = "silicon_ore";
        String DEEPSLATE_SILICON_ORE = "deepslate_silicon_ore";
        String TIN_ORE = "tin_ore";
        String DEEPSLATE_TIN_ORE = "deepslate_tin_ore";
        String MOON_TIN_ORE = "moon_tin_ore";
        String LUNASLATE_TIN_ORE = "lunaslate_tin_ore";
        String ALUMINUM_ORE = "aluminum_ore";
        String DEEPSLATE_ALUMINUM_ORE = "deepslate_aluminum_ore";
        String MOON_CHEESE_ORE = "moon_cheese_ore";
        String LUNAR_SAPPHIRE_ORE = "lunar_sapphire_ore";
        String DESH_ORE = "desh_ore";
        String ILMENITE_ORE = "ilmenite_ore";
        String GALENA_ORE = "galena_ore";
        String SOLAR_ORE = "solar_ore";

        // Solid Blocks
        String SILICON_BLOCK = "silicon_block";
        String METEORIC_IRON_BLOCK = "meteoric_iron_block";
        String DESH_BLOCK = "desh_block";
        String ALUMINUM_BLOCK = "aluminum_block";
        String TIN_BLOCK = "tin_block";
        String TITANIUM_BLOCK = "titanium_block";
        String LEAD_BLOCK = "lead_block";
        String LUNAR_SAPPHIRE_BLOCK = "lunar_sapphire_block";
        String OLIVINE_BLOCK = "olivine_block";
        String RAW_METEORIC_IRON_BLOCK = "raw_meteoric_iron_block";
        String RAW_DESH_BLOCK = "raw_desh_block";
        String RAW_ALUMINUM_BLOCK = "raw_aluminum_block";
        String RAW_TIN_BLOCK = "raw_tin_block";
        String RAW_TITANIUM_BLOCK = "raw_titanium_block";
        String RAW_LEAD_BLOCK = "raw_lead_block";

        // Decorative BLocks
        String ALUMINUM_DECORATION = "aluminum_decoration";
        String BRONZE_DECORATION = "bronze_decoration";
        String COPPER_DECORATION = "copper_decoration";
        String IRON_DECORATION = "iron_decoration";
        String METEORIC_IRON_DECORATION = "meteoric_iron_decoration";
        String STEEL_DECORATION = "steel_decoration";
        String TIN_DECORATION = "tin_decoration";
        String TITANIUM_DECORATION = "titanium_decoration";
        String DARK_DECORATION = "dark_decoration";

        String IRON_GRATING = "iron_grating";
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
        String FLUID_PIPE_WALKWAY = "fluid_pipe_walkway";

        //  Environment
        String GLOWSTONE_TORCH = "glowstone_torch";
        String GLOWSTONE_WALL_TORCH = "glowstone_wall_torch";
        String GLOWSTONE_LANTERN = "glowstone_lantern";
        String UNLIT_TORCH = "unlit_torch";
        String UNLIT_WALL_TORCH = "unlit_wall_torch";
        String UNLIT_LANTERN = "unlit_lantern";
        String CAVERNOUS_VINES = "cavernous_vines";
        String CAVERNOUS_VINES_PLANT = "cavernous_vines_plant";
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
        String CRASHED_PROBE_BLOCK = "crashed_probe";
        String BOSS_SPAWNER = "boss_spawner";

        // Moon Cheese
        String MOON_CHEESE_WHEEL = "moon_cheese_wheel";
        String CANDLE_MOON_CHEESE_WHEEL = "candle_moon_cheese_wheel";
        String WHITE_CANDLE_MOON_CHEESE_WHEEL = "white_candle_moon_cheese_wheel";
        String ORANGE_CANDLE_MOON_CHEESE_WHEEL = "orange_candle_moon_cheese_wheel";
        String MAGENTA_CANDLE_MOON_CHEESE_WHEEL = "magenta_candle_moon_cheese_wheel";
        String LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL = "light_blue_candle_moon_cheese_wheel";
        String YELLOW_CANDLE_MOON_CHEESE_WHEEL = "yellow_candle_moon_cheese_wheel";
        String LIME_CANDLE_MOON_CHEESE_WHEEL = "lime_candle_moon_cheese_wheel";
        String PINK_CANDLE_MOON_CHEESE_WHEEL = "pink_candle_moon_cheese_wheel";
        String GRAY_CANDLE_MOON_CHEESE_WHEEL = "gray_candle_moon_cheese_wheel";
        String LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL = "light_gray_candle_moon_cheese_wheel";
        String CYAN_CANDLE_MOON_CHEESE_WHEEL = "cyan_candle_moon_cheese_wheel";
        String PURPLE_CANDLE_MOON_CHEESE_WHEEL = "purple_candle_moon_cheese_wheel";
        String BLUE_CANDLE_MOON_CHEESE_WHEEL = "blue_candle_moon_cheese_wheel";
        String BROWN_CANDLE_MOON_CHEESE_WHEEL = "brown_candle_moon_cheese_wheel";
        String GREEN_CANDLE_MOON_CHEESE_WHEEL = "green_candle_moon_cheese_wheel";
        String RED_CANDLE_MOON_CHEESE_WHEEL = "red_candle_moon_cheese_wheel";
        String BLACK_CANDLE_MOON_CHEESE_WHEEL = "black_candle_moon_cheese_wheel";

        // Liquids
        String FUEL = "fuel";
        String CRUDE_OIL = "crude_oil";
        String SULFURIC_ACID = "sulfuric_acid";

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
        String FUELING_PAD = "fueling_pad";
        String ROCKET_LAUNCH_PAD = "rocket_launch_pad";
        String FUEL_LOADER = "fuel_loader";
        String CARGO_LOADER = "cargo_loader";
        String CARGO_UNLOADER = "cargo_unloader";
        String LAUNCH_CONTROLLER = "launch_controller";

        // Space Base
        String HYDRAULIC_PLATFORM = "hydraulic_platform";
        String MAGNETIC_CRAFTING_TABLE = "magnetic_crafting_table";
        String ROCKET_WORKBENCH = "rocket_workbench";
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

        static ResourceLocation fluidId(String s) {
            return Constant.id("block/fluid/" + s);
        }
    }

    interface Item {
        String ITEM_GROUP = "items";
        String SILICON = "silicon";
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
        String OLIVINE_SHARD = "olivine_shard";
        String DESH_STICK = "desh_stick";
        String CARBON_FRAGMENTS = "carbon_fragments";
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
        String SOLAR_ARRAY_PANEL = "solar_array_panel";
        String STEEL_POLE = "steel_pole";
        String COPPER_CANISTER = "copper_canister";
        String TIN_CANISTER = "tin_canister";
        String THERMAL_CLOTH = "thermal_cloth";
        String ISOTHERMAL_FABRIC = "isothermal_fabric";
        String ORION_DRIVE = "orion_drive";
        String ATMOSPHERIC_VALVE = "atmospheric_valve";
        String AMBIENT_THERMAL_CONTROLLER = "ambient_thermal_controller";
        String LIQUID_CANISTER = "liquid_canister";
        //FOOD
        String MOON_CHEESE_WHEEL = "moon_cheese_wheel";
        String MOON_CHEESE_CURD = "moon_cheese_curd";
        String MOON_CHEESE_SLICE = "moon_cheese_slice";
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
        String TITANTIUM_UPGRADE_SMITHING_TEMPLATE = "titanium_upgrade_smithing_template";
        String BATTERY = "battery";
        String INFINITE_BATTERY = "infinite_battery";
        String INFINITE_INDICATOR = "infinite_indicator";

        //Fluid buckets
        String CRUDE_OIL_BUCKET = "crude_oil_bucket";
        String FUEL_BUCKET = "fuel_bucket";
        String SULFURIC_ACID_BUCKET = "sulfuric_acid_bucket";

        //GC INVENTORY
        String PARACHUTE = "parachute";

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

        String BUGGY = "buggy";
        String ROCKET = "rocket";

        String TIER_2_ROCKET_SCHEMATIC = "tier_2_rocket_schematic";
        String TIER_3_ROCKET_SCHEMATIC = "tier_3_rocket_schematic";
        String CARGO_ROCKET_SCHEMATIC = "cargo_rocket_schematic";
        String MOON_BUGGY_SCHEMATIC = "moon_buggy_schematic";
        String ASTRO_MINER_SCHEMATIC = "astro_miner_schematic";

        String LEGACY_MUSIC_DISC_MARS = "legacy_music_disc_mars";
        String LEGACY_MUSIC_DISC_MIMAS = "legacy_music_disc_mimas";
        String LEGACY_MUSIC_DISC_ORBIT = "legacy_music_disc_orbit";
        String LEGACY_MUSIC_DISC_SPACERACE = "legacy_music_disc_spacerace";
        String NOSE_CONE = "nose_cone";
        String HEAVY_NOSE_CONE = "heavy_nose_cone";
        String ROCKET_FIN = "rocket_fin";
        String ROCKET_ENGINE = "rocket_engine";
        String BASIC_ROCKET_CONE_SCHEMATIC = "basic_rocket_cone_schematic";
        String BASIC_ROCKET_BODY_SCHEMATIC = "basic_rocket_body_schematic";
        String BASIC_ROCKET_FINS_SCHEMATIC = "basic_rocket_fins_schematic";
        String BASIC_ROCKET_ENGINE_SCHEMATIC = "basic_rocket_engine_schematic";
    }

    interface Particle {
        String DRIPPING_FUEL = "dripping_fuel";
        String FALLING_FUEL = "falling_fuel";
        String DRIPPING_CRUDE_OIL = "dripping_crude_oil";
        String FALLING_CRUDE_OIL = "falling_crude_oil";
        String CRYOGENIC_PARTICLE = "cryogenic_particle";
        String LANDER_FLAME = "lander_flame_particle";
        String SPARK = "spark";
        String DRIPPING_SULFURIC_ACID = "dripping_sulfuric_acid";
        String FALLING_SULFURIC_ACID = "falling_sulfuric_acid";
        String LAUNCH_SMOKE = "launch_smoke";
        String LAUNCH_FLAME = "launch_flame";
        String LAUNCH_FLAME_LAUNCHED = "launch_flame_launched";
        String ACID_VAPOR_PARTICLE = "acid_vapor";
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
        ResourceLocation ROCKET_WORKBENCH_SCREEN = id("textures/gui/rocket_workbench.png");
        ResourceLocation ROCKET_SELECTION = id("textures/gui/rocket_part_selection.png");

        ResourceLocation PLAYER_INVENTORY_SCREEN = id("textures/gui/player_inventory_screen.png");
        ResourceLocation ROCKET_INVENTORY = id("textures/gui/rocket.png");
        ResourceLocation OVERLAY = id("textures/gui/overlay.png");

        ResourceLocation MAP_SCREEN = id("textures/gui/map.png");
        ResourceLocation PLANET_ICONS = id("textures/gui/planet_icons.png");
        ResourceLocation BUBBLE_DISTRIBUTOR_SCREEN = id("textures/gui/oxygen_bubble_distributor_screen.png");
        ResourceLocation OXYGEN_COMPRESSOR_SCREEN = id("textures/gui/oxygen_compressor_screen.png");
        ResourceLocation OXYGEN_STORAGE_MODULE_SCREEN = id("textures/gui/oxygen_storage_module_screen.png");
        ResourceLocation OXYGEN_SEALER_SCREEN = id("textures/gui/oxygen_sealer_screen.png");
        ResourceLocation FUEL_LOADER_SCREEN = id("textures/gui/fuel_loader_screen.png");
        ResourceLocation SOLAR_PANEL_DAY = id("textures/gui/solar_panel/day.png");
        ResourceLocation SOLAR_PANEL_NIGHT = id("textures/gui/solar_panel/night.png");
        ResourceLocation SOLAR_PANEL_BLOCKED = id("textures/gui/solar_panel/blocked.png");
    }

    interface CelestialBody {
        ResourceLocation SOL = id("textures/gui/celestialbodies/sol.png");
        ResourceLocation SOL_OVERCAST = id("textures/gui/celestialbodies/sol_overcast.png");
        ResourceLocation SOL_FROM_MOON = id("textures/gui/celestialbodies/sol_from_moon.png");
        ResourceLocation MERCURY = id("textures/gui/celestialbodies/mercury.png");
        ResourceLocation VENUS = id("textures/gui/celestialbodies/venus.png");
        ResourceLocation EARTH = id("textures/gui/celestialbodies/earth.png");
        ResourceLocation SPACE_STATION = id("textures/gui/celestialbodies/space_station.png");
        ResourceLocation MOON = id("textures/gui/celestialbodies/moon.png");
        ResourceLocation MARS = id("textures/gui/celestialbodies/mars.png");
        ResourceLocation ASTEROID = id("textures/gui/celestialbodies/asteroid.png");
        ResourceLocation SATURN = id("textures/gui/celestialbodies/saturn.png");
        ResourceLocation SATURN_RINGS = id("textures/gui/celestialbodies/saturn_rings.png");
        ResourceLocation JUPITER = id("textures/gui/celestialbodies/jupiter.png");
        ResourceLocation CALLISTO = id("textures/gui/celestialbodies/callisto.png");
        ResourceLocation EUROPA = id("textures/gui/celestialbodies/europa.png");
        ResourceLocation GANYMEDE = id("textures/gui/celestialbodies/ganymede.png");
        ResourceLocation IO = id("textures/gui/celestialbodies/io.png");
        ResourceLocation URANUS = id("textures/gui/celestialbodies/uranus.png");
        ResourceLocation URANUS_RINGS = id("textures/gui/celestialbodies/uranus_rings.png");
        ResourceLocation NEPTUNE = id("textures/gui/celestialbodies/neptune.png");
    }

    interface RecipeViewer {
        ResourceLocation RECIPE_VIEWER_DISPLAY_TEXTURE = id("textures/gui/rei_display.png");

        int CIRCUIT_FABRICATOR_U = 0;
        int CIRCUIT_FABRICATOR_V = 0;
        int CIRCUIT_FABRICATOR_WIDTH = 139;
        int CIRCUIT_FABRICATOR_HEIGHT = 73;

        int DIAMOND_X = 1;
        int DIAMOND_Y = 1;
        int SILICON_X_1 = 32;
        int SILICON_Y_1 = 31;
        int SILICON_X_2 = 32;
        int SILICON_Y_2 = 49;
        int REDSTONE_X = 77;
        int REDSTONE_Y = 56;
        int INGREDIENT_X = 104;
        int INGREDIENT_Y = 1;
        int WAFER_X = 122;
        int WAFER_Y = 56;

        int COMPRESSOR_U = 0;
        int COMPRESSOR_V = 74;
        int COMPRESSOR_WIDTH = 148;
        int COMPRESSOR_HEIGHT = 54;

        int FIRE_X = 67;
        int FIRE_Y = 9;
        int FUEL_X = 67;
        int FUEL_Y = 31;
        int COMPRESSED_X = 127;
        int COMPRESSED_Y = 20;
    }

    interface SlotSprite {
        ResourceLocation ENERGY = id("slot/energy");
        ResourceLocation BUCKET = id("slot/bucket");
        ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("item/empty_slot_diamond");
        ResourceLocation DUST = ResourceLocation.withDefaultNamespace("item/empty_slot_redstone_dust");
        ResourceLocation SILICON = id("slot/silicon");
        ResourceLocation THERMAL_HEAD = id("slot/thermal_helmet");
        ResourceLocation THERMAL_CHEST = id("slot/thermal_chestpiece");
        ResourceLocation THERMAL_PANTS = id("slot/thermal_leggings");
        ResourceLocation THERMAL_BOOTS = id("slot/thermal_boots");
        ResourceLocation OXYGEN_MASK = id("slot/oxygen_mask");
        ResourceLocation OXYGEN_GEAR = id("slot/oxygen_gear");
        ResourceLocation OXYGEN_TANK = id("slot/oxygen_tank");

        ResourceLocation ROCKET_CONE = id("slot/rocket_cone");
        ResourceLocation ROCKET_PLATING = id("slot/rocket_plating");
        ResourceLocation ROCKET_BOOSTER = id("slot/rocket_booster");
        ResourceLocation ROCKET_FIN = id("slot/rocket_fin");
        ResourceLocation ROCKET_ENGINE = id("slot/rocket_engine");
        ResourceLocation CHEST = id("slot/chest");
    }

    interface Entity {
        String MOON_VILLAGER = "moon_villager";
        String EVOLVED_ZOMBIE = "evolved_zombie";
        String EVOLVED_CREEPER = "evolved_creeper";
        String EVOLVED_SKELETON = "evolved_skeleton";
        String EVOLVED_SPIDER = "evolved_spider";
        String EVOLVED_ENDERMAN = "evolved_enderman";
        String EVOLVED_WITCH = "evolved_witch";
        String EVOLVED_PILLAGER = "evolved_pillager";
        String EVOLVED_EVOKER = "evolved_evoker";
        String EVOLVED_VINDICATOR = "evolved_vindicator";
        String T1_ROCKET = "t1_rocket";
        String ROCKET = "rocket";
        String LANDER = "lander";
        String BUGGY = "buggy";
        String PARACHEST = "parachest";
        String BUBBLE = "bubble";
        String GREY = "grey";
        String ARCH_GREY = "arch_grey";
        String RUMBLER = "rumbler";
        String OLI_GRUB = "oli_grub";
        String COMET_CUBE = "comet_cube";
        String GAZER = "gazer";
        String EVOLVED_SKELETON_BOSS = "evolved_skeleton_boss";
    }

    interface SpawnEgg {
        String MOON_VILLAGER = "moon_villager_spawn_egg";
        String EVOLVED_ZOMBIE = "evolved_zombie_spawn_egg";
        String EVOLVED_CREEPER = "evolved_creeper_spawn_egg";
        String EVOLVED_SKELETON = "evolved_skeleton_spawn_egg";
        String EVOLVED_SPIDER = "evolved_spider_spawn_egg";
        String EVOLVED_ENDERMAN = "evolved_enderman_spawn_egg";
        String EVOLVED_WITCH = "evolved_witch_spawn_egg";
        String EVOLVED_PILLAGER = "evolved_pillager_spawn_egg";
        String EVOLVED_EVOKER = "evolved_evoker_spawn_egg";
        String EVOLVED_VINDICATOR = "evolved_vindicator_spawn_egg";
        String GREY = "grey_spawn_egg";
        String ARCH_GREY = "arch_grey_spawn_egg";
        String RUMBLER = "rumbler_spawn_egg";
        String OLI_GRUB = "oli_grub_spawn_egg";
        String COMET_CUBE = "comet_cube_spawn_egg";
        String GAZER = "gazer_spawn_egg";
    }

    interface EntityTexture {
        String GREY = "textures/entity/grey.png";
        String ARCH_GREY = "textures/entity/arch_grey.png";
        String RUMBLER = "textures/entity/rumbler.png";
        String OLI_GRUB = "textures/entity/oli_grub.png";
        String COMET_CUBE = "textures/entity/comet_cube.png";
        String GAZER = "textures/entity/gazer.png";
        String LANDER = "textures/entity/lander.png";
        String SKELETON_BOSS = "textures/entity/skeletonboss.png";
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
        String ROCKET_WORKBENCH_MENU = "rocket_workbench_menu";
        String ROCKET = "rocket";
        String PARACHEST = "parachest";
        String BUGGY_BENCH = "buggy_bench";
        String TIER_1_ROCKET = "tier_1_rocket";
    }

    interface LootTable {
        String BASIC_MOON_RUINS_CHEST = "chests/moon_ruins/basic_chest";
    }

    interface Text {
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

        static int getStorageLevelColor(double scale) {
            return ((int) (255 * scale) << 16) + (((int) (255 * (1.0 - scale))) << 8);
        }

        static Style getStorageLevelStyle(double scale) {
            return Style.EMPTY.withColor(TextColor.fromRgb(getStorageLevelColor(scale)));
        }

        static int getCoolingColor(double scale) {
            return (255 << 16) + (((int) (255 * 0.75 * (1.0 - scale))) << 8);
        }

        static Style getCoolingStyle(double scale) {
            return Style.EMPTY.withColor(TextColor.fromRgb(getCoolingColor(scale)));
        }
    }

    interface Nbt {
        String GC_API = "GCApi";
        String CHANGE_COUNT = "Modified";
        String OXYGEN = "Inversion";
        String GEAR_INV = "GearInv";
        String BLOCK_ENTITY_TAG = "BlockEntityTag";
        String NO_DROP = "NoDrop";
        String OWNER = "Owner";
        String PROGRESS = "Progress";
        String SIZE = "Size";
        String MAX_SIZE = "MaxSize";
        String VISIBLE = "Visible";
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
        String ROCKET_UUID = "RocketUuid";
    }

    interface Chunk {
        int WIDTH = 16;
        int SECTION_HEIGHT = 16;
        int CHUNK_SECTION_AREA = WIDTH * WIDTH * SECTION_HEIGHT;
    }

    interface Energy {
        long T1_MACHINE_ENERGY_USAGE = 100; // TODO: adjust these later
        long T2_MACHINE_ENERGY_USAGE = 200;
    }

    interface Landing {
        double SAFE_VELOCITY = 1.0D; // meters per tick (~1/20 second)
        double EXPLOSION_SCALE = 4.0D;
    }

    @ApiStatus.Internal
    interface Misc {
        ResourceLocation INVALID = Constant.id("invalid");
        ResourceLocation EMPTY = ResourceLocation.withDefaultNamespace("empty");
        Direction[] DIRECTIONS = Direction.values();
        Direction[] HORIZONTALS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        Direction[] VERTICALS = {Direction.UP, Direction.DOWN};
        String LOGGER_PREFIX = "[Galacticraft] ";
        boolean DEBUG = false;
        int MAX_STRING_READ = 32767;
    }

    interface Recipe {
        String FABRICATION = "fabrication";
        String COMPRESSING = "compressing";
        String ROCKET = "rocket";

        interface Serializer {
            String FABRICATION = "fabrication";
            String COMPRESSING_SHAPELESS = "compressing_shapeless";
            String COMPRESSING_SHAPED = "compressing_shaped";
            String ROCKET = "rocket";
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

    // Used in Data Generator
    interface BakedModel {
        ResourceLocation WALKWAY_MARKER = id("autogenerated/walkway");
        ResourceLocation FLUID_PIPE_WALKWAY_MARKER = id("autogenerated/fluid_pipe_walkway");
        ResourceLocation WIRE_WALKWAY_MARKER = id("autogenerated/wire_walkway");
        ResourceLocation WIRE_MARKER = id("autogenerated/aluminum_wire");
        ResourceLocation GLASS_FLUID_PIPE_MARKER = id("autogenerated/glass_fluid_pipe");
        ResourceLocation VACUUM_GLASS_MODEL = id("vacuum_glass");
    }

    interface Carver {
        String MOON_CANYON_CARVER = "moon_canyon_carver";
        String MOON_CRATER_CARVER = "moon_crater_carver";
        String MOON_HIGHLANDS_CAVE_CARVER = "moon_highlands_cave_carver";
        String MOON_MARE_CAVE_CARVER = "moon_mare_cave_carver";
    }

    interface Packet {
        ResourceLocation STREAM_CODECBUBBLE_SIZE = id("bubble_size");
        ResourceLocation BUBBLE_MAX = id("bubble_max");
        ResourceLocation BUBBLE_VISIBLE = id("bubble_visible");
        ResourceLocation OPEN_GC_INVENTORY = id("open_gc_inv");
        ResourceLocation OPEN_GC_ROCKET = id("open_gc_rocket");
        ResourceLocation CREATE_SATELLITE = id("create_satellite");
        ResourceLocation OPEN_SCREEN = id("open_screen");
        ResourceLocation PLANET_MENU_PACKET = id("planet_menu_open");
        ResourceLocation SELECT_PART = id("select_part");

        ResourceLocation CONTROLLABLE_ENTITY = id("controllable_entity");
        ResourceLocation PLANET_TP = id("planet_tp");
        ResourceLocation ROCKET_SPAWN = id("rocket_spawn");
        ResourceLocation FOOTPRINT = id("footprint");
        ResourceLocation FOOTPRINT_REMOVED = id("footprint_removed");
        ResourceLocation RESET_THIRD_PERSON = id("reset_camera_type");
    }

    interface Structure {
        ResourceLocation SPACE_STATION = id("space_station");
    }

    interface Command {
        String HOUSTON = "gchouston";
        String DIMENSION_TP = "dimensiontp";
        String DIMTP = "dimtp";
        String OPEN_CELESTIAL_SCREEN = "opencelestialscreen";
    }

    interface Attachments {
        String SERVER_PLAYER = "server_player";
        String CLIENT_PLAYER = "client_player";
    }

    interface Teleporters {
        String LANDER = "lander";
        String OVERWORLD = "overworld";
    }

    interface Triggers {
        String ROCKET_LAUNCH = "launch_rocket";
        String FIND_MOON_BOSS = "boss_moon";
        String CREATE_SPACE_STATION = "create_space_station";
    }
}
