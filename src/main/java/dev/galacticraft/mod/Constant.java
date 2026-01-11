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
import net.minecraft.util.FastColor;
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
    int CLOUD_HEIGHT = 200;
    int CLOUD_LIMIT = CLOUD_HEIGHT + 50;
    int SPACE_HEIGHT = 1000;
    int REENTRY_HEIGHT = 1100;
    int ESCAPE_HEIGHT = 1200;

    @Contract(value = "_ -> new", pure = true)
    static @NotNull ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull <T> ResourceKey<T> key(ResourceKey<Registry<T>> registry, String id) {
        return ResourceKey.create(registry, Constant.id(id));
    }

    String CAPES = "https://raw.githubusercontent.com/TeamGalacticraft/Galacticraft/main/capes_roles.json";

    interface Cape {
        String EARTH = "earth";
        String JUPITER = "jupiter";
        String MARS = "mars";
        String MERCURY = "mercury";
        String MOON = "moon";
        String NEPTUNE = "neptune";
        String PLAIN = "plain";
        String SPACE_STATION = "space_station";
        String SUN = "sun";
        String URANUS = "uranus";
        String VENUS = "venus";
        String DEVELOPER = "developer";
        String REWOVEN = "rewoven";
        String DEVELOPER_RED = "developer_red";
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
        String DENSE_ICE = "dense_ice";

        String SOFT_VENUS_ROCK = "soft_venus_rock";
        String HARD_VENUS_ROCK = "hard_venus_rock";
        String SCORCHED_VENUS_ROCK = "scorched_venus_rock";
        String VOLCANIC_ROCK = "volcanic_rock";
        String PUMICE = "pumice";
        String VAPOR_SPOUT = "vapor_spout";

        // Ore
        String MARS_IRON_ORE = "mars_iron_ore";
        String MARS_TIN_ORE = "mars_tin_ore";
        String MARS_COPPER_ORE = "mars_copper_ore";
        String ASTEROID_IRON_ORE = "asteroid_iron_ore";
        String MOON_COPPER_ORE = "moon_copper_ore";
        String LUNASLATE_COPPER_ORE = "lunaslate_copper_ore";
        String VENUS_COPPER_ORE = "venus_copper_ore";
        String SILICON_ORE = "silicon_ore";
        String DEEPSLATE_SILICON_ORE = "deepslate_silicon_ore";
        String TIN_ORE = "tin_ore";
        String DEEPSLATE_TIN_ORE = "deepslate_tin_ore";
        String MOON_TIN_ORE = "moon_tin_ore";
        String LUNASLATE_TIN_ORE = "lunaslate_tin_ore";
        String ASTEROID_ALUMINUM_ORE = "asteroid_aluminum_ore";
        String ASTEROID_SILICON_ORE = "asteroid_silicon_ore";
        String VENUS_TIN_ORE = "venus_tin_ore";
        String ALUMINUM_ORE = "aluminum_ore";
        String DEEPSLATE_ALUMINUM_ORE = "deepslate_aluminum_ore";
        String VENUS_ALUMINUM_ORE = "venus_aluminum_ore";
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
        String UNLIT_SOUL_TORCH = "unlit_soul_torch";
        String UNLIT_SOUL_WALL_TORCH = "unlit_soul_wall_torch";
        String UNLIT_SOUL_LANTERN = "unlit_soul_lantern";
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
        String MOON_CHEESE_BLOCK = "moon_cheese_block";
        String MOON_CHEESE_LOG = "moon_cheese_log";
        String MOON_CHEESE_LEAVES = "moon_cheese_leaves";

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
        String FOOD_CANNER = "food_canner";
        String OXYGEN_DECOMPRESSOR = "oxygen_decompressor";
        String OXYGEN_DETECTOR = "oxygen_detector";
        String OXYGEN_SEALER = "oxygen_sealer";
        String FLUID_PIPE = "fluid_pipe";
        String REFINERY = "refinery";
        String TERRAFORMER = "terraformer";
        String DECONSTRUCTOR = "deconstructor";
        String WATER_ELECTROLYZER = "water_electrolyzer";
        String METHANE_SYNTHESIZIER = "methane_synthesizer";
        String GAS_LIQUEFIER = "gas_liquefier";

        // Glass fluid pipes
        String GLASS_FLUID_PIPE = "glass_fluid_pipe";
        String WHITE_GLASS_FLUID_PIPE = "white_glass_fluid_pipe";
        String ORANGE_GLASS_FLUID_PIPE = "orange_glass_fluid_pipe";
        String MAGENTA_GLASS_FLUID_PIPE = "magenta_glass_fluid_pipe";
        String LIGHT_BLUE_GLASS_FLUID_PIPE = "light_blue_glass_fluid_pipe";
        String YELLOW_GLASS_FLUID_PIPE = "yellow_glass_fluid_pipe";
        String LIME_GLASS_FLUID_PIPE = "lime_glass_fluid_pipe";
        String PINK_GLASS_FLUID_PIPE = "pink_glass_fluid_pipe";
        String GRAY_GLASS_FLUID_PIPE = "gray_glass_fluid_pipe";
        String LIGHT_GRAY_GLASS_FLUID_PIPE = "light_gray_glass_fluid_pipe";
        String CYAN_GLASS_FLUID_PIPE = "cyan_glass_fluid_pipe";
        String PURPLE_GLASS_FLUID_PIPE = "purple_glass_fluid_pipe";
        String BLUE_GLASS_FLUID_PIPE = "blue_glass_fluid_pipe";
        String BROWN_GLASS_FLUID_PIPE = "brown_glass_fluid_pipe";
        String GREEN_GLASS_FLUID_PIPE = "green_glass_fluid_pipe";
        String RED_GLASS_FLUID_PIPE = "red_glass_fluid_pipe";
        String BLACK_GLASS_FLUID_PIPE = "black_glass_fluid_pipe";

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
        String CANNED_FOOD = "canned_food";
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
        String ITEM_GROUP_CANS = "cans";
        String ITEM_GROUP = "items";
        String SILICON = "silicon";
        String EMPTY_CAN = "empty_can";
        String CANNED_FOOD = "canned_food";
        String CANNED_FOOD_LABEL = "canned_food_label";
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
        String CRACKER = "cracker";
        String CHEESE_CRACKER = "cheese_cracker";
        String BURGER_BUN = "burger_bun";
        String GROUND_BEEF = "ground_beef";
        String BEEF_PATTY = "beef_patty";
        String CHEESEBURGER = "cheeseburger";
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

        String EMERGENCY_KIT = "emergency_kit";

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
        String HEAVY_ROCKET_FIN = "heavy_rocket_fin";
        String ROCKET_ENGINE = "rocket_engine";
        String HEAVY_ROCKET_ENGINE = "heavy_rocket_engine";
        String ROCKET_BOOSTER = "rocket_booster";
        String BUGGY_WHEEL = "buggy_wheel";
        String BUGGY_SEAT = "buggy_seat";
        String BUGGY_STORAGE = "buggy_storage";
        String BASIC_ROCKET_CONE_SCHEMATIC = "basic_rocket_cone_schematic";
        String BASIC_ROCKET_BODY_SCHEMATIC = "basic_rocket_body_schematic";
        String BASIC_ROCKET_FINS_SCHEMATIC = "basic_rocket_fins_schematic";
        String BASIC_ROCKET_ENGINE_SCHEMATIC = "basic_rocket_engine_schematic";
    }

    interface Particle {
        String DRIPPING_CRUDE_OIL = "dripping_crude_oil";
        String FALLING_CRUDE_OIL = "falling_crude_oil";
        String DRIPPING_FUEL = "dripping_fuel";
        String FALLING_FUEL = "falling_fuel";
        String DRIPPING_SULFURIC_ACID = "dripping_sulfuric_acid";
        String FALLING_SULFURIC_ACID = "falling_sulfuric_acid";

        String CRYOGENIC_PARTICLE = "cryogenic_particle";
        String LANDER_FLAME = "lander_flame_particle";
        String SPARK = "spark";
        String LAUNCH_SMOKE = "launch_smoke";
        String LAUNCH_FLAME = "launch_flame";
        String LAUNCH_FLAME_LAUNCHED = "launch_flame_launched";
        String ACID_VAPOR_PARTICLE = "acid_vapor";
        String SPLASH_VENUS = "splash_venus";
    }

    interface ScreenTexture {
        ResourceLocation COAL_GENERATOR_SCREEN = id("textures/gui/coal_generator_screen.png");
        ResourceLocation SOLAR_PANEL_SCREEN = id("textures/gui/solar_panel_screen.png");
        ResourceLocation REFINERY_SCREEN = id("textures/gui/refinery_screen.png");
        ResourceLocation ENERGY_STORAGE_MODULE_SCREEN = id("textures/gui/energy_storage_module_screen.png");
        ResourceLocation OXYGEN_COLLECTOR_SCREEN = id("textures/gui/oxygen_collector_screen.png");
        ResourceLocation ROCKET_SELECTION = id("textures/gui/rocket_part_selection.png");

        ResourceLocation PLAYER_INVENTORY_SCREEN = id("textures/gui/player_inventory_screen.png");
        ResourceLocation PET_INVENTORY_SCREEN = id("textures/gui/pet_inventory_screen.png");
        ResourceLocation ROCKET_INVENTORY = id("textures/gui/rocket.png");
        ResourceLocation OVERLAY = id("textures/gui/overlay.png");
        ResourceLocation WARNING_SIGN = id("textures/gui/warning.png");

        ResourceLocation BUBBLE_DISTRIBUTOR_SCREEN = id("textures/gui/oxygen_bubble_distributor_screen.png");
        ResourceLocation OXYGEN_COMPRESSOR_SCREEN = id("textures/gui/oxygen_compressor_screen.png");
        ResourceLocation OXYGEN_STORAGE_MODULE_SCREEN = id("textures/gui/oxygen_storage_module_screen.png");
        ResourceLocation OXYGEN_SEALER_SCREEN = id("textures/gui/oxygen_sealer_screen.png");
        ResourceLocation FUEL_LOADER_SCREEN = id("textures/gui/fuel_loader_screen.png");
        ResourceLocation SOLAR_PANEL_DAY = id("textures/gui/solar_panel/day.png");
        ResourceLocation SOLAR_PANEL_NIGHT = id("textures/gui/solar_panel/night.png");
        ResourceLocation SOLAR_PANEL_BLOCKED = id("textures/gui/solar_panel/blocked.png");
    }

    interface CircuitFabricator {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/circuit_fabricator_screen.png");

        int DIAMOND_X = 31;
        int DIAMOND_Y = 17;
        int SILICON_X_1 = 62;
        int SILICON_Y_1 = 47;
        int SILICON_X_2 = 62;
        int SILICON_Y_2 = 65;
        int REDSTONE_X = 107;
        int REDSTONE_Y = 72;
        int INGREDIENT_X = 134;
        int INGREDIENT_Y = 17;
        int OUTPUT_X = 152;
        int OUTPUT_Y = 72;

        int PROGRESS_X = 48;
        int PROGRESS_Y = 23;
        int PROGRESS_WIDTH = 113;
        int PROGRESS_HEIGHT = 51;
        int PROGRESS_BACKGROUND_U = 128;
        int PROGRESS_BACKGROUND_V = 180;

        int RECIPE_VIEWER_X = 30;
        int RECIPE_VIEWER_Y = 16;
        int RECIPE_VIEWER_WIDTH = 139;
        int RECIPE_VIEWER_HEIGHT = 73;
    }

    interface Compressor {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/compressor_screen.png");

        int GRID_X = 17;
        int GRID_Y = 17;
        int OUTPUT_X = 143;
        int OUTPUT_Y = 36;

        int PROGRESS_X = 82;
        int PROGRESS_Y = 26;
        int PROGRESS_U = 178;
        int PROGRESS_V = 0;
        int PROGRESS_WIDTH = 52;
        int PROGRESS_HEIGHT = 24;
        int PROGRESS_BACKGROUND_U = PROGRESS_U;
        int PROGRESS_BACKGROUND_V = PROGRESS_HEIGHT + 1;

        int FIRE_X = 83;
        int FIRE_Y = 25;
        int FIRE_U = 242;
        int FIRE_V = 0;
        int FIRE_WIDTH = 14;
        int FIRE_HEIGHT = 14;

        int FUEL_X = FIRE_X;
        int FUEL_Y = FIRE_Y + 22;

        int RECIPE_VIEWER_X = 16;
        int RECIPE_VIEWER_Y = 16;
        int RECIPE_VIEWER_WIDTH = 148;
        int RECIPE_VIEWER_HEIGHT = 54;
    }

    interface ElectricCompressor {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/electric_compressor_screen.png");

        int GRID_X = 30;
        int GRID_Y = 17;
        int OUTPUT_X_1 = 148;
        int OUTPUT_Y_1 = 22;
        int OUTPUT_X_2 = 148;
        int OUTPUT_Y_2 = 48;

        int PROGRESS_X = 87;
        int PROGRESS_Y = 27;
        int PROGRESS_U = 204;
        int PROGRESS_V = 0;
        int PROGRESS_WIDTH = 52;
        int PROGRESS_HEIGHT = 25;
        int PROGRESS_BACKGROUND_U = PROGRESS_U;
        int PROGRESS_BACKGROUND_V = PROGRESS_HEIGHT + 1;

        int RECIPE_VIEWER_X = 29;
        int RECIPE_VIEWER_Y = 16;
        int RECIPE_VIEWER_WIDTH = 140;
        int RECIPE_VIEWER_HEIGHT = 54;
    }

    interface ElectricFurnace {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/electric_furnace_screen.png");

        int INPUT_X = 52;
        int INPUT_Y = 35;
        int OUTPUT_X = 113;
        int OUTPUT_Y = 35;

        int PROGRESS_X = 74;
        int PROGRESS_Y = 34;
        int PROGRESS_U = 177;
        int PROGRESS_V = 0;
        int PROGRESS_WIDTH = 30;
        int PROGRESS_HEIGHT = 16;
        int PROGRESS_BACKGROUND_U = 208;
        int PROGRESS_BACKGROUND_V = 0;

        int REI_X = 51;
        int REI_Y = 21;
        int REI_WIDTH = 83;
        int REI_HEIGHT = 39;

        int JEI_X = 51;
        int JEI_Y = 26;
        int JEI_WIDTH = 83;
        int JEI_HEIGHT = 34;

        int EMI_X = 51;
        int EMI_Y = 30;
        int EMI_WIDTH = 83;
        int EMI_HEIGHT = 38;
    }

    interface ElectricArcFurnace {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/electric_arc_furnace_screen.png");

        int INPUT_X = 44;
        int INPUT_Y = 35;
        int OUTPUT_X_1 = 108;
        int OUTPUT_Y_1 = 35;
        int OUTPUT_X_2 = 134;
        int OUTPUT_Y_2 = 35;

        int PROGRESS_X = 68;
        int PROGRESS_Y = 34;
        int PROGRESS_U = 177;
        int PROGRESS_V = 0;
        int PROGRESS_WIDTH = 26;
        int PROGRESS_HEIGHT = 16;
        int PROGRESS_BACKGROUND_U = 204;
        int PROGRESS_BACKGROUND_V = 0;

        int REI_X = 43;
        int REI_Y = 21;
        int REI_WIDTH = 112;
        int REI_HEIGHT = 39;

        int JEI_X = 43;
        int JEI_Y = 26;
        int JEI_WIDTH = 112;
        int JEI_HEIGHT = 34;

        int EMI_X = 43;
        int EMI_Y = 30;
        int EMI_WIDTH = 112;
        int EMI_HEIGHT = 38;
    }

    interface FoodCanner {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/food_canner_screen.png");

        int INPUT_X = 62;
        int INPUT_Y = 13;
        int CURRENT_X = 62;
        int CURRENT_Y = 40;
        int OUTPUT_X = 62;
        int OUTPUT_Y = 67;
        int GRID_X = 98;
        int GRID_Y = 13;

        int PROGRESS_X = 68;
        int PROGRESS_Y = 19;
        int PROGRESS_WIDTH = 29;
        int PROGRESS_HEIGHT = 57;
        int PROGRESS_BACKGROUND_U = 180;
        int PROGRESS_BACKGROUND_V = 80;

        int TRANSFER_INPUT = 9;
        int START_ROW_1 = TRANSFER_INPUT + 1;
        int START_ROW_2 = START_ROW_1 + 28;
        int SKIP_ROW_2 = START_ROW_2 + 9;
        int START_ROW_4 = START_ROW_2 + 21;
        int START_ROW_3 = START_ROW_4 + 27;
        int SKIP_ROW_3 = START_ROW_3 + 8;
        int FINAL_PROGRESS = START_ROW_3 + 21;
        int TRANSFER_OUTPUT = FINAL_PROGRESS + 7;
        int MAX_PROGRESS = TRANSFER_OUTPUT + 9;

        int[] ROW_ORDER = {0, 1, 3, 2};
        int[] ROW_PROGRESS = {START_ROW_1, START_ROW_2, START_ROW_3, START_ROW_4};

        int RECIPE_VIEWER_X = 61;
        int RECIPE_VIEWER_Y = 12;
        int RECIPE_VIEWER_WIDTH = 108;
        int RECIPE_VIEWER_HEIGHT = 72;
    }

    interface RocketWorkbench {
        ResourceLocation SCREEN_TEXTURE = id("textures/gui/rocket_workbench.png");

        int CENTER_X = 53;

        int SLOT_U = 200;
        int SLOT_V = 0;
        int SLOT_WIDTH = 18;
        int SLOT_HEIGHT = 18;

        int CHEST_X = 44;
        int CHEST_Y = 140;
        int CHEST_U = 178;
        int CHEST_V = 0;
        int CHEST_WIDTH = 20;
        int CHEST_HEIGHT = 20;

        int OUTPUT_X = 125;
        int OUTPUT_Y = 135;
        int OUTPUT_X_OFFSET = 9;
        int OUTPUT_Y_OFFSET = 9;
        int OUTPUT_U = 178;
        int OUTPUT_V = 126;
        int OUTPUT_WIDTH = 34;
        int OUTPUT_HEIGHT = 34;
        int OUTPUT_INNER_WIDTH = 24;
        int OUTPUT_INNER_HEIGHT = 24;

        int ROCKET_X = 133;
        int ROCKET_Y = 104;

        int PREVIEW_X = 100;
        int PREVIEW_Y = 26;
        int PREVIEW_U = 100;
        int PREVIEW_V = 26;
        int PREVIEW_WIDTH = 65;
        int PREVIEW_HEIGHT = 94;

        int PREVIEW_DARK_U = 178;
        int PREVIEW_DARK_V = 26;

        int RECIPE_VIEWER_X = 16;
        int RECIPE_VIEWER_Y = 26;
        int RECIPE_VIEWER_WIDTH = 149;
        int RECIPE_VIEWER_HEIGHT = 134;
    }

    interface CelestialScreen {
        ResourceLocation CELESTIAL_SELECTION = id("textures/gui/celestial_selection.png");
        ResourceLocation CELESTIAL_SELECTION_1 = id("textures/gui/celestial_selection_1.png");
        ResourceLocation SELECTION_CURSOR = id("textures/gui/selection_cursor.png");

        ResourceLocation SOL = id("sol");

        // String colours
        int BLACK = FastColor.ARGB32.color(255, 0, 0, 0);
        int GREY3 = FastColor.ARGB32.color(255, 120, 120, 120);
        int GREY4 = FastColor.ARGB32.color(255, 140, 140, 140);
        int GREY5 = FastColor.ARGB32.color(255, 150, 150, 150);
        int GREY6 = FastColor.ARGB32.color(255, 165, 165, 165);
        int WHITE = FastColor.ARGB32.color(255, 255, 255, 255);
        int RED = FastColor.ARGB32.color(255, 255, 0, 0);
        int RED3 = FastColor.ARGB32.color(255, 255, 100, 100);
        int GREEN = FastColor.ARGB32.color(255, 0, 255, 0);
        int GREEN1 = FastColor.ARGB32.color(255, 0, 255, 25);
        int BLUE = FastColor.ARGB32.color(255, 0, 153, 255);
        int YELLOW = FastColor.ARGB32.color(255, 255, 255, 0);

        int BORDER_EDGE_TOP_LEFT = FastColor.ARGB32.color(255, 40, 40, 40);
        int BORDER_EDGE_BOTTOM_RIGHT = FastColor.ARGB32.color(255, 80, 80, 80);
        int BORDER_GREY = FastColor.ARGB32.color(255, 100, 100, 100);
        int BORDER_Z = 799; // Just below text in toasts to prevent clipping

        int MAX_SPACE_STATION_NAME_LENGTH = 32;

        int SELECTION_CURSOR_U = 0;
        int SELECTION_CURSOR_V = 0;
        int SELECTION_CURSOR_SIZE = 64;

        int SIDE_PANEL_U = 0;
        int SIDE_PANEL_V = 0;
        int SIDE_PANEL_WIDTH = 95;
        int SIDE_PANEL_HEIGHT = 137;

        int CATALOG_U = 0;
        int CATALOG_V = 197;
        int CATALOG_WIDTH = 74;
        int CATALOG_HEIGHT = 11;

        int CATALOG_BACKING_U = 0;
        int CATALOG_BACKING_V = 221;
        int CATALOG_BACKING_WIDTH = 83;
        int CATALOG_BACKING_HEIGHT = 12;

        int ZOOM_INFO_TAB_U = 134;
        int ZOOM_INFO_TAB_V = 67;
        int ZOOM_INFO_TAB_WIDTH = 83;
        int ZOOM_INFO_TAB_HEIGHT = 38;

        int PROFILE_UPPER_TAB_U = 134;
        int PROFILE_UPPER_TAB_V = 0;
        int PROFILE_UPPER_TAB_WIDTH = 86;
        int PROFILE_UPPER_TAB_HEIGHT = 15;

        int PARENT_LABEL_U = 134;
        int PARENT_LABEL_V = 151;
        int PARENT_LABEL_WIDTH = 95;
        int PARENT_LABEL_HEIGHT = 41;

        int GRANDPARENT_LABEL_U = 134;
        int GRANDPARENT_LABEL_V = 193;
        int GRANDPARENT_LABEL_WIDTH = 93;
        int GRANDPARENT_LABEL_HEIGHT = 17;

        int SIDE_BUTTON_U = 134;
        int SIDE_BUTTON_V = 223;
        int SIDE_BUTTON_WIDTH = 92;
        int SIDE_BUTTON_HEIGHT = 12;

        int SIDE_BUTTON_GRADIENT_U = 0;
        int SIDE_BUTTON_GRADIENT_V = 234;
        int SIDE_BUTTON_GRADIENT_WIDTH = 86;
        int SIDE_BUTTON_GRADIENTn_HEIGHT = 20;

        int TOP_RIGHT_ACTION_BUTTON_U = 134;
        int TOP_RIGHT_ACTION_BUTTON_V = 211;
        int TOP_RIGHT_ACTION_BUTTON_WIDTH = 74;
        int TOP_RIGHT_ACTION_BUTTON_HEIGHT = 11;

        int TOPBAR_U = 134;
        int TOPBAR_V = 138;
        int TOPBAR_WIDTH = 94;
        int TOPBAR_HEIGHT = 12;

        int TOPBAR_SUB_U = 0;
        int TOPBAR_SUB_V = 209;
        int TOPBAR_SUB_WIDTH = 94;
        int TOPBAR_SUB_HEIGHT = 11;

        int CREATE_SS_PANEL_U = 0;
        int CREATE_SS_PANEL_V = 137;
        int CREATE_SS_PANEL_WIDTH = 93;
        int CREATE_SS_PANEL_HEIGHT = 47;
        int CREATE_SS_PANEL_CAP_U = 0;
        int CREATE_SS_PANEL_CAP_V = 185;
        int CREATE_SS_PANEL_CAP_WIDTH = 61;
        int CREATE_SS_PANEL_CAP_HEIGHT = 4;
        int CREATE_SS_PANEL_BUTTON_U = 134;
        int CREATE_SS_PANEL_BUTTON_V = 236;
        int CREATE_SS_PANEL_BUTTON_WIDTH = 93;
        int CREATE_SS_PANEL_BUTTON_HEIGHT = 12;
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

    interface CelestialOverlay {
        ResourceLocation EARTH = Constant.id("textures/gui/rocket/overworld_rocket_gui.png");
        ResourceLocation MOON = Constant.id("textures/gui/rocket/moon_rocket_gui.png");
        ResourceLocation MARS = Constant.id("textures/gui/rocket/mars_rocket_gui.png");
        ResourceLocation VENUS = Constant.id("textures/gui/rocket/venus_rocket_gui.png");
    }

    interface Skybox {
        ResourceLocation MOON_PHASES = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
        ResourceLocation SUN = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
        ResourceLocation SUN_MOON = id("textures/environment/sun_moon.png");
        ResourceLocation SUN_VENUS = id("textures/environment/sun_venus.png");
        ResourceLocation EARTH = id("textures/environment/earth.png");
        ResourceLocation CLOUDS = id("textures/environment/clouds.png");
        ResourceLocation ATMOSPHERE = id("textures/environment/atmosphere.png");
    }

    interface SlotSprite {
        ResourceLocation ENERGY = id("slot/energy");
        ResourceLocation BUCKET = id("slot/bucket");
        ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("item/empty_slot_diamond");
        ResourceLocation DUST = ResourceLocation.withDefaultNamespace("item/empty_slot_redstone_dust");
        ResourceLocation SILICON = id("slot/silicon");

        ResourceLocation OXYGEN_MASK = id("slot/oxygen_mask");
        ResourceLocation OXYGEN_GEAR = id("slot/oxygen_gear");
        ResourceLocation OXYGEN_TANK = id("slot/oxygen_tank");
        ResourceLocation FREQUENCY_MODULE = id("slot/frequency_module");
        ResourceLocation PARACHUTE = id("slot/parachute");
        ResourceLocation SHIELD_CONTROLLER = id("slot/shield_controller");
        ResourceLocation GENERIC_ACCESSORY = null;
        ResourceLocation THERMAL_HEAD = id("slot/thermal_helmet");
        ResourceLocation THERMAL_CHEST = id("slot/thermal_chestpiece");
        ResourceLocation THERMAL_PANTS = id("slot/thermal_leggings");
        ResourceLocation THERMAL_BOOTS = id("slot/thermal_boots");
        ResourceLocation WOLF_ARMOR = id("slot/wolf_armor");

        ResourceLocation ROCKET_CONE = id("slot/rocket_cone");
        ResourceLocation ROCKET_PLATING = id("slot/rocket_plating");
        ResourceLocation ROCKET_BOOSTER = id("slot/rocket_booster");
        ResourceLocation ROCKET_FIN_LEFT = id("slot/rocket_fin_left");
        ResourceLocation ROCKET_FIN_RIGHT = id("slot/rocket_fin_right");
        ResourceLocation ROCKET_ENGINE = id("slot/rocket_engine");
        ResourceLocation CHEST = id("slot/chest");
        ResourceLocation FOOD_CAN = id("slot/food_can");
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
        String FALLING_METEOR = "falling_meteor";
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
        String EVOLVED_SPIDER = "textures/entity/evolved/spider.png";
        String EVOLVED_SPIDER_EYES = "textures/entity/evolved/spider_eyes.png";
        String MOON_VILLAGER = "textures/entity/villager/moon_villager.png";
        String GREY = "textures/entity/grey.png";
        String ARCH_GREY = "textures/entity/arch_grey.png";
        String RUMBLER = "textures/entity/rumbler.png";
        String OLI_GRUB = "textures/entity/oli_grub.png";
        String COMET_CUBE = "textures/entity/comet_cube.png";
        String GAZER = "textures/entity/gazer.png";
        String LANDER = "textures/entity/lander.png";
        String SKELETON_BOSS = "textures/entity/skeletonboss.png";
    }

    interface GearTexture {
        String OXYGEN_TANKS = "textures/entity/gear/oxygen_tanks.png";
        String OXYGEN_GEAR = "textures/entity/gear/oxygen_gear.png";
        String PET_GEAR = "textures/entity/gear/pet_gear.png";
        String PARROT_GEAR = "textures/entity/gear/parrot_gear.png";
        String SPIDER_GEAR = "textures/entity/gear/spider_gear.png";
        String WITCH_GEAR = "textures/entity/gear/witch_gear.png";
        String ILLAGER_GEAR = "textures/entity/gear/illager_gear.png";
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
        String PET_INVENTORY_MENU = "pet_inventory_menu";
        String ENERGY_STORAGE_MODULE_MENU = "energy_storage_module_menu";
        String REFINERY_MENU = "refinery_menu";
        String ELECTRIC_FURNACE_MENU = "electric_furnace_menu";
        String ELECTRIC_ARC_FURNACE_MENU = "electric_arc_furnace_menu";
        String OXYGEN_COLLECTOR_MENU = "oxygen_collector_menu";
        String BUBBLE_DISTRIBUTOR_MENU = "bubble_distributor_menu";
        String OXYGEN_COMPRESSOR_MENU = "oxygen_compressor_menu";
        String FOOD_CANNER_MENU = "food_canner_menu";
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
        String HAS_MASK = "HasMask";
        String HAS_GEAR = "HasGear";
        String OXYGEN_TANK_1 = "OxygenTank1";
        String OXYGEN_TANK_2 = "OxygenTank2";
        String BLOCK_ENTITY_TAG = "BlockEntityTag";
        String NO_DROP = "NoDrop";
        String OWNER = "Owner";
        String PROGRESS = "Progress";
        String TRANSFERRING_CAN = "TransferringCan";
        String TRANSFERRING_FOOD = "TransferringFood";
        String STORAGE = "StorageStack";
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
        String SHAPELESS = "Shapeless";
        String PROCESSING_TIME = "ProcessingTime";
        String XP = "xp";
        String ITEMS = "Items";
        String GASES = "Gases";
        String CRYOGENIC_COOLDOWN = "CryogenicCooldown";
        String DOCKED_UUID = "DockedUuid";
        String CAN_CONTENTS = "CanContents";
        String CAN_COUNT = "CanCount";
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
        String ELECTRIC_COMPRESSING = "electric_compressing";
        String ELECTRIC_SMELTING = "electric_smelting";
        String ELECTRIC_BLASTING = "electric_blasting";
        String CANNING = "canning";
        String ROCKET = "rocket";

        interface Serializer {
            String FABRICATION = "fabrication";
            String COMPRESSING_SHAPELESS = "compressing_shapeless";
            String COMPRESSING_SHAPED = "compressing_shaped";
            String CANNING = "canning";
            String ROCKET = "rocket";
            String EMERGENCY_KIT = "crafting_special_emergencykit";
        }
    }

    @Environment(EnvType.CLIENT)
    interface ModelPartName {
        String OXYGEN_MASK = "oxygen_mask";
        String REAL_OXYGEN_MASK = "real_oxygen_mask";
        String OXYGEN_PIPE = "oxygen_pipe";
        String OXYGEN_PIPE_SITTING = "oxygen_pipe_sitting";
        String OXYGEN_TANK = "oxygen_tank";
        String ILLAGER_NOSE_COMPARTMENT = "illager_nose_compartment";
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
        String ROCKET_WORKBENCH_TOP = "rocket_workbench_top";
        String ROCKET_WORKBENCH_PLIER_TOOL = "rocket_workbench_plier_tool";
        String ROCKET_WORKBENCH_PLIER_TOOL_ARM = "rocket_workbench_plier_tool_arm";
        String ROCKET_WORKBENCH_PLIER_TOOL_SMALL_ARM = "rocket_workbench_plier_tool_small_arm";
        String ROCKET_WORKBENCH_PLIER_TOOL_PLIERS = "rocket_workbench_plier_tool_pliers";
        String ROCKET_WORKBENCH_DRILL_TOOL = "rocket_workbench_drill_tool";
        String ROCKET_WORKBENCH_DRILL_TOOL_SMALL_ARM = "rocket_workbench_drill_tool_small_arm";
        String ROCKET_WORKBENCH_DRILL_TOOL_DRILL = "rocket_workbench_drill_tool_drill";
        String ROCKET_WORKBENCH_DRILL_TOOL_DRILL_BIT = "rocket_workbench_drill_tool_drill_bit";
        String ROCKET_WORKBENCH_FLASHLIGHT = "rocket_workbench_flashlight";
        String ROCKET_WORKBENCH_FLASHLIGHT_LIGHT = "rocket_workbench_flashlight_light";
        String ROCKET_WORKBENCH_FLASHLIGHT_HANDLE = "rocket_workbench_flashlight_handle";
        String ROCKET_WORKBENCH_FLASHLIGHT_HOLDER = "rocket_workbench_flashlight_holder";
        String ROCKET_WORKBENCH_DISPLAY = "rocket_workbench_display";
    }

    // Used in Data Generator
    interface BakedModel {
        ResourceLocation WALKWAY_CONNECTOR_MARKER = id("autogenerated/walkway_connector");
        ResourceLocation WALKWAY_CENTER_MARKER = id("autogenerated/walkway_center");
        ResourceLocation PIPE_WALKWAY_CENTER_MARKER = id("autogenerated/glass_fluid_pipe_walkway_center");
        ResourceLocation WIRE_WALKWAY_CENTER_MARKER = id("autogenerated/aluminum_wire_walkway_center");
        ResourceLocation FLUID_PIPE_WALKWAY_MARKER = id("autogenerated/fluid_pipe_walkway");
        ResourceLocation WIRE_WALKWAY_MARKER = id("autogenerated/wire_walkway");
        ResourceLocation WIRE_MARKER = id("autogenerated/aluminum_wire");
        ResourceLocation GLASS_FLUID_PIPE_MARKER = id("autogenerated/glass_fluid_pipe");
        ResourceLocation VACUUM_GLASS_MODEL = id("vacuum_glass");
        ResourceLocation CANNED_FOOD = id("block/canned_food");
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
        String LEAVE_ROCKET_DURING_COUNTDOWN = "leave_rocket_during_countdown";
        String ROCKET_LAUNCH = "launch_rocket";
        String SAFE_LANDING = "safe_landing";
        String FIND_MOON_BOSS = "boss_moon";
        String CREATE_SPACE_STATION = "create_space_station";
    }
}
