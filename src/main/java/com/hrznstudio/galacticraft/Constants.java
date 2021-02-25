/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft;

import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Constants {

    public static final String MOD_ID = "galacticraft-rewoven";

    // Blocks
    public static class Blocks {
        public static final String ITEM_GROUP_BLOCKS = "blocks";
        public static final String ITEM_GROUP_MACHINES = "machines";

        // Natural
        public static final String MOON_TURF = "moon_turf";
        public static final String MOON_SURFACE_ROCK = "moon_surface_rock";
        public static final String MOON_ROCK = "moon_rock";
        public static final String COBBLED_MOON_ROCK = "cobbled_moon_rock";
        public static final String MOON_BASALT = "moon_basalt";
        public static final String MOON_DIRT = "moon_dirt";
        public static final String MARS_SURFACE_ROCK = "mars_surface_rock";
        public static final String MARS_SUB_SURFACE_ROCK = "mars_sub_surface_rock";
        public static final String MARS_STONE = "mars_stone";
        public static final String MARS_COBBLESTONE = "mars_cobblestone";
        public static final String ASTEROID_ROCK = "asteroid_rock";
        public static final String ASTEROID_ROCK_1 = "asteroid_rock_block_1";
        public static final String ASTEROID_ROCK_2 = "asteroid_rock_block_2";

        public static final String SOFT_VENUS_ROCK = "soft_venus_rock";
        public static final String HARD_VENUS_ROCK = "hard_venus_rock";
        public static final String SCORCHED_VENUS_ROCK = "scorched_venus_rock";
        public static final String VOLCANIC_ROCK = "volcanic_rock";
        public static final String PUMICE = "pumice";
        public static final String VAPOR_SPOUT = "vapor_spout";

        // Ore
        public static final String ASTEROID_ALUMINUM_ORE = "asteroid_aluminum_ore";
        public static final String MOON_CHEESE_ORE = "moon_cheese_ore";
        public static final String MOON_COPPER_ORE = "moon_copper_ore";
        public static final String MARS_COPPER_ORE = "mars_copper_ore";
        public static final String DESH_ORE = "desh_ore";
        public static final String ILMENITE_ORE = "ilmenite_ore";
        public static final String MARS_IRON_ORE = "mars_iron_ore";
        public static final String ASTEROID_IRON_ORE = "asteroid_iron_ore";
        public static final String SILICON_ORE = "silicon_ore";
        public static final String MOON_TIN_ORE = "moon_tin_ore";
        public static final String MARS_TIN_ORE = "mars_tin_ore";
        public static final String GALENA_ORE = "galena_ore";

        // Solid Blocks
        public static final String SILICON_BLOCK = "silicon_block";
        public static final String METEORIC_IRON_BLOCK = "meteoric_iron_block";
        public static final String DESH_BLOCK = "desh_block";
        public static final String TITANIUM_BLOCK = "titanium_block";
        public static final String LEAD_BLOCK = "lead_block";
        public static final String LUNAR_SAPPHIRE_BLOCK = "lunar_sapphire_block";

        // Decorative BLocks
        public static final String ALUMINUM_DECORATION = "aluminum_decoration";
        public static final String BRONZE_DECORATION = "bronze_decoration";
        public static final String COPPER_DECORATION = "copper_decoration";
        public static final String IRON_DECORATION = "iron_decoration";
        public static final String METEORIC_IRON_DECORATION = "meteoric_iron_decoration";
        public static final String STEEL_DECORATION = "steel_decoration";
        public static final String TIN_DECORATION = "tin_decoration";
        public static final String TITANIUM_DECORATION = "titanium_decoration";
        public static final String DARK_DECORATION = "dark_decoration";
        public static final String GRATING = "grating";
        public static final String TIN_LADDER = "tin_ladder";
        public static final String SQUARE_LIGHT_PANEL = "square_light_panel";
        public static final String SPOTLIGHT_LIGHT_PANEL = "spotlight_light_panel";
        public static final String LINEAR_LIGHT_PANEL = "linear_light_panel";
        public static final String DASHED_LIGHT_PANEL = "dashed_light_panel";
        public static final String DIAGONAL_LIGHT_PANEL = "diagonal_light_panel";
        public static final String VACUUM_GLASS = "vacuum_glass";
        public static final String CLEAR_VACUUM_GLASS = "vacuum_glass_clear";
        public static final String STRONG_VACUUM_GLASS = "vacuum_glass_strong";
        public static final String WALKWAY = "walkway";
        public static final String WIRE_WALKWAY = "wire_walkway";
        public static final String PIPE_WALKWAY = "pipe_walkway";
        public static final String MOON_CHEESE_LEAVES = "moon_cheese_leaves";
        public static final String MOON_CHEESE_LOG = "moon_cheese_log";

        //  Environment
        public static final String GLOWSTONE_TORCH = "glowstone_torch";
        public static final String GLOWSTONE_WALL_TORCH = "glowstone_wall_torch";
        public static final String GLOWSTONE_LANTERN = "glowstone_lantern";
        public static final String UNLIT_TORCH = "unlit_torch";
        public static final String UNLIT_WALL_TORCH = "unlit_wall_torch";
        public static final String UNLIT_LANTERN = "unlit_lantern";
        public static final String CAVERNOUS_VINE = "cavernous_vine";
        public static final String POISONOUS_CAVERNOUS_VINE = "poisonous_cavernous_vine";
        public static final String MOON_BERRY_BUSH = "moon_berry_bush";
        public static final String WEB_TORCH = "web_torch";
        public static final String FALLEN_METEOR = "fallen_meteor";
        public static final String SLIMELING_EGG = "slimeling_egg";
        public static final String CREEPER_EGG = "creeper_egg";

        // Special
        public static final String PARACHEST = "parachest";
        public static final String SPACE_STATION_ARRIVAL = "space_station_arrival";
        public static final String TREASURE_CHEST_TIER_1 = "treasure_chest_tier_1";
        public static final String TREASURE_CHEST_TIER_2 = "treasure_chest_tier_2";
        public static final String TREASURE_CHEST_TIER_3 = "treasure_chest_tier_3";
        public static final String MOON_CHEESE_BLOCK = "moon_cheese_block";
        public static final String CRASHED_PROBE_BLOCK = "crashed_probe";

        // Liquids
        public static final String FUEL = "fuel";
        public static final String CRUDE_OIL = "crude_oil";

        // Machines
        public static final String CIRCUIT_FABRICATOR = "circuit_fabricator";
        public static final String COMPRESSOR = "compressor";
        public static final String ELECTRIC_COMPRESSOR = "electric_compressor";
        public static final String ELECTRIC_FURNACE = "electric_furnace";
        public static final String ELECTRIC_ARC_FURNACE = "electric_arc_furnace";
        public static final String OXYGEN_BUBBLE_DISTRIBUTOR = "oxygen_bubble_distributor";
        public static final String OXYGEN_COLLECTOR = "oxygen_collector";
        public static final String OXYGEN_COMPRESSOR = "oxygen_compressor";
        public static final String OXYGEN_DECOMPRESSOR = "oxygen_decompressor";
        public static final String OXYGEN_DETECTOR = "oxygen_detector";
        public static final String OXYGEN_SEALER = "oxygen_sealer";
        public static final String FLUID_PIPE = "fluid_pipe";
        public static final String GLASS_FLUID_PIPE = "glass_fluid_pipe";
        public static final String REFINERY = "refinery";
        public static final String TERRAFORMER = "terraformer";
        public static final String DECONSTRUCTOR = "deconstructor";
        public static final String WATER_ELECTROLYZER = "water_electrolyzer";
        public static final String METHANE_SYNTHESIZIER = "methane_synthesizer";
        public static final String GAS_LIQUEFIER = "gas_liquefier";

        // Pad Blocks
        public static final String BUGGY_FUELING_PAD = "buggy_fueling";
        public static final String ROCKET_LAUNCH_PAD = "rocket_launch_pad";
        public static final String FUEL_LOADER = "fuel_loader";
        public static final String CARGO_LOADER = "cargo_loader";
        public static final String CARGO_UNLOADER = "cargo_unloader";
        public static final String LAUNCH_CONTROLLER = "launch_controller";

        // Space Base
        public static final String HYDRAULIC_PLATFORM = "hydraulic_platform";
        public static final String MAGNETIC_CRAFTING_TABLE = "magnetic_crafting_table";
        public static final String NASA_WORKBENCH = "nasa_workbench";
        public static final String AIR_LOCK_FRAME = "air_lock_frame";
        public static final String AIR_LOCK_CONTROLLER = "air_lock_controller";
        public static final String AIR_LOCK_SEAL = "air_lock_seal";
        public static final String CHROMATIC_APPLICATOR = "chromatic_applicator";
        public static final String DISPLAY_SCREEN = "display_screen";
        public static final String TELEMETRY_UNIT = "telemetry_unit";
        public static final String COMMUNICATIONS_DISH = "communications_dish";
        public static final String ARC_LAMP = "arc_lamp";
        public static final String SPIN_THRUSTER = "spin_thruster";
        public static final String CRYOGENIC_CHAMBER = "cryogenic_chamber";
        public static final String ASTRO_MINER_BASE = "astro_miner_base";
        public static final String SHORT_RANGE_TELEPAD = "short_range_telepad";

        // Power
        public static final String BASIC_SOLAR_PANEL = "basic_solar_panel";
        public static final String SOLAR_PANEL_PART = "solar_panel_part";
        public static final String ADVANCED_SOLAR_PANEL = "advanced_solar_panel";
        public static final String COAL_GENERATOR = "coal_generator";
        public static final String GEOTHERMAL_GENERATOR = "geothermal_generator";
        public static final String ENERGY_STORAGE_MODULE = "energy_storage_module";
        public static final String ENERGY_STORAGE_CLUSTER = "energy_storage_cluster";
        public static final String ALUMINUM_WIRE = "aluminum_wire";
        public static final String HEAVY_ALUMINUM_WIRE = "heavy_aluminum_wire";
        public static final String SWITCHABLE_ALUMINUM_WIRE = "switchable_aluminum_wire";
        public static final String SEALABLE_ALUMINUM_WIRE = "sealable_aluminum_wire";
        public static final String HEAVY_SEALABLE_ALUMINUM_WIRE = "heavy_sealable_aluminum_wire";
        public static final String BEAM_REFLECTOR = "beam_reflector";
        public static final String BEAM_RECEIVER = "beam_receiver";
        public static final String SOLAR_ARRAY_MODULE = "solar_array_module";
        public static final String OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK = "oxygen_distributor_bubble_dummy_block";
        public static final String MOON_BASALT_BRICK = "moon_basalt_brick";
        public static final String CRACKED_MOON_BASALT_BRICK = "cracked_moon_basalt_brick";
        public static final String LUNAR_CARTOGRAPHY_TABLE = "lunar_cartography_table";
        public static final String OXYGEN_STORAGE_MODULE = "oxygen_storage_module";
    }

    // Fluids
    public static class Fluids {
        public static final String CRUDE_OIL_FLOWING = "crude_oil_flowing";
        public static final String CRUDE_OIL_STILL = "crude_oil_still";
        public static final String FUEL_FLOWING = "fuel_flowing";
        public static final String FUEL_STILL = "fuel_still";
        public static final String BACTERIAL_ACID_FLOWING = "bacterial_acid_flowing";
        public static final String BACTERIAL_ACID_STILL = "bacterial_acid_still";
        public static final String SULFURIC_ACID_FLOWING = "sulfuric_acid_flowing";
        public static final String SULFURIC_ACID_STILL = "sulfuric_acid_still";
        public static final String OXYGEN_GAS = "oxygen_gas";
        public static final String LIQUID_OXYGEN = "liquid_oxygen";

        public static Identifier getIdentifier(String s) {
            return new Identifier(Constants.MOD_ID, "block/" + s);
        }
    }

    //Items
    public static class Items {
        public static final String ITEM_GROUP = "items";
        public static final String LEAD_INGOT = "lead_ingot";
        public static final String RAW_SILICON = "raw_silicon";
        public static final String RAW_METEORIC_IRON = "raw_meteoric_iron";
        public static final String METEORIC_IRON_INGOT = "meteoric_iron_ingot";
        public static final String LUNAR_SAPPHIRE = "lunar_sapphire";
        public static final String UNREFINED_DESH = "raw_desh";
        public static final String DESH_INGOT = "desh_ingot";
        public static final String DESH_STICK = "desh_stick";
        public static final String CARBON_FRAGMENTS = "carbon_fragments";
        public static final String IRON_SHARD = "iron_shard";
        public static final String TITANIUM_SHARD = "titanium_shard";
        public static final String TITANIUM_INGOT = "titanium_ingot";
        public static final String TITANIUM_DUST = "titanium_dust";
        public static final String SOLAR_DUST = "solar_dust";
        public static final String BASIC_WAFER = "basic_wafer";
        public static final String ADVANCED_WAFER = "advanced_wafer";
        public static final String BEAM_CORE = "beam_core";
        public static final String CANVAS = "canvas";
        public static final String COMPRESSED_ALUMINUM = "compressed_aluminum";
        public static final String COMPRESSED_COPPER = "compressed_copper";
        public static final String COMPRESSED_TIN = "compressed_tin";
        public static final String COMPRESSED_BRONZE = "compressed_bronze";
        public static final String COMPRESSED_IRON = "compressed_iron";
        public static final String COMPRESSED_STEEL = "compressed_steel";
        public static final String COMPRESSED_METEORIC_IRON = "compressed_meteoric_iron";
        public static final String COMPRESSED_DESH = "compressed_desh";
        public static final String COMPRESSED_TITANIUM = "compressed_titanium";
        public static final String FLUID_MANIPULATOR = "fluid_manipulator";
        public static final String OXYGEN_CONCENTRATOR = "oxygen_concentrator";
        public static final String OXYGEN_FAN = "oxygen_fan";
        public static final String OXYGEN_VENT = "oxygen_vent";
        public static final String SENSOR_LENS = "sensor_lens";
        public static final String BLUE_SOLAR_WAFER = "blue_solar_wafer";
        public static final String SINGLE_SOLAR_MODULE = "single_solar_module";
        public static final String FULL_SOLAR_PANEL = "full_solar_panel";
        public static final String SOLAR_ARRAY_WAFER = "solar_array_wafer";
        public static final String STEEL_POLE = "steel_pole";
        public static final String COPPER_CANISTER = "copper_canister";
        public static final String TIN_CANISTER = "tin_canister";
        public static final String THERMAL_CLOTH = "thermal_cloth";
        public static final String ISOTHERMAL_FABRIC = "thermal_cloth_t2";
        public static final String ORION_DRIVE = "orion_drive";
        public static final String ATMOSPHERIC_VALVE = "atmospheric_valve";
        public static final String LIQUID_CANISTER = "liquid_canister";
        //FOOD
        public static final String MOON_BERRIES = "moon_berries";
        public static final String CHEESE_CURD = "cheese_curd";
        public static final String CHEESE_SLICE = "cheese_slice";
        public static final String BURGER_BUN = "burger_bun";
        public static final String GROUND_BEEF = "ground_beef";
        public static final String BEEF_PATTY = "beef_patty";
        public static final String CHEESEBURGER = "cheeseburger";
        //CANNED FOOD
        public static final String CANNED_DEHYDRATED_APPLE = "canned_dehydrated_apple";
        public static final String CANNED_DEHYDRATED_CARROT = "canned_dehydrated_carrot";
        public static final String CANNED_DEHYDRATED_MELON = "canned_dehydrated_melon";
        public static final String CANNED_DEHYDRATED_POTATO = "canned_dehydrated_potato";
        public static final String CANNED_BEEF = "canned_beef";
        //ROCKET PARTS
        public static final String TIER_1_HEAVY_DUTY_PLATE = "heavy_plating";
        public static final String TIER_2_HEAVY_DUTY_PLATE = "heavy_plating_t2";
        public static final String TIER_3_HEAVY_DUTY_PLATE = "heavy_plating_t3";
        //THROWABLE METEOR CHUNKS
        public static final String THROWABLE_METEOR_CHUNK = "throwable_meteor_chunk";
        public static final String HOT_THROWABLE_METEOR_CHUNK = "hot_throwable_meteor_chunk";
        //ARMOR
        public static final String HEAVY_DUTY_HELMET = "heavy_duty_helmet";
        public static final String HEAVY_DUTY_CHESTPLATE = "heavy_duty_chestplate";
        public static final String HEAVY_DUTY_LEGGINGS = "heavy_duty_leggings";
        public static final String HEAVY_DUTY_BOOTS = "heavy_duty_boots";
        public static final String DESH_HELMET = "desh_helmet";
        public static final String DESH_CHESTPLATE = "desh_chestplate";
        public static final String DESH_LEGGINGS = "desh_leggings";
        public static final String DESH_BOOTS = "desh_boots";
        public static final String TITANIUM_HELMET = "titanium_helmet";
        public static final String TITANIUM_CHESTPLATE = "titanium_chestplate";
        public static final String TITANIUM_LEGGINGS = "titanium_leggings";
        public static final String TITANIUM_BOOTS = "titanium_boots";
        public static final String SENSOR_GLASSES = "sensor_glasses";
        //TOOLS + WEAPONS
        public static final String HEAVY_DUTY_SWORD = "heavy_duty_sword";
        public static final String HEAVY_DUTY_SHOVEL = "heavy_duty_shovel";
        public static final String HEAVY_DUTY_PICKAXE = "heavy_duty_pickaxe";
        public static final String HEAVY_DUTY_AXE = "heavy_duty_axe";
        public static final String HEAVY_DUTY_HOE = "heavy_duty_hoe";

        public static final String DESH_SWORD = "desh_sword";
        public static final String DESH_SHOVEL = "desh_shovel";
        public static final String DESH_PICKAXE = "desh_pickaxe";
        public static final String DESH_AXE = "desh_axe";
        public static final String DESH_HOE = "desh_hoe";

        public static final String TITANIUM_SWORD = "titanium_sword";
        public static final String TITANIUM_SHOVEL = "titanium_shovel";
        public static final String TITANIUM_PICKAXE = "titanium_pickaxe";
        public static final String TITANIUM_AXE = "titanium_axe";
        public static final String TITANIUM_HOE = "titanium_hoe";

        public static final String STANDARD_WRENCH = "standard_wrench";
        public static final String BATTERY = "battery";
        public static final String INFINITE_BATTERY = "infinite_battery";

        //Fluid buckets
        public static final String CRUDE_OIL_BUCKET = "crude_oil_bucket";
        public static final String FUEL_BUCKET = "fuel_bucket";

        //GC INVENTORY
        public static final String PARACHUTE = "parachute";
        public static final String ORANGE_PARACHUTE = "orange_parachute";
        public static final String MAGENTA_PARACHUTE = "magenta_parachute";
        public static final String LIGHT_BLUE_PARACHUTE = "light_blue_parachute";
        public static final String YELLOW_PARACHUTE = "yellow_parachute";
        public static final String LIME_PARACHUTE = "lime_parachute";
        public static final String PINK_PARACHUTE = "pink_parachute";
        public static final String GRAY_PARACHUTE = "gray_parachute";
        public static final String LIGHT_GRAY_PARACHUTE = "light_gray_parachute";
        public static final String CYAN_PARACHUTE = "cyan_parachute";
        public static final String PURPLE_PARACHUTE = "purple_parachute";
        public static final String BLUE_PARACHUTE = "blue_parachute";
        public static final String BROWN_PARACHUTE = "brown_parachute";
        public static final String GREEN_PARACHUTE = "green_parachute";
        public static final String RED_PARACHUTE = "red_parachute";
        public static final String BLACK_PARACHUTE = "black_parachute";

        public static final String OXYGEN_MASK = "oxygen_mask";
        public static final String OXYGEN_GEAR = "oxygen_gear";

        public static final String SHIELD_CONTROLLER = "shield_controller";
        public static final String FREQUENCY_MODULE = "frequency_module";

        public static final String SMALL_OXYGEN_TANK = "small_oxygen_tank";
        public static final String MEDIUM_OXYGEN_TANK = "medium_oxygen_tank";
        public static final String LARGE_OXYGEN_TANK = "large_oxygen_tank";
        public static final String INFINITE_OXYGEN_TANK = "infinite_oxygen_tank";

        public static final String THERMAL_PADDING_HELMET = "thermal_padding_helmet";
        public static final String THERMAL_PADDING_CHESTPIECE = "thermal_padding_chestpiece";
        public static final String THERMAL_PADDING_LEGGINGS = "thermal_padding_leggings";
        public static final String THERMAL_PADDING_BOOTS = "thermal_padding_boots";

        public static final String ISOTHERMAL_PADDING_HELMET = "isothermal_padding_helmet";
        public static final String ISOTHERMAL_PADDING_CHESTPIECE = "isothermal_padding_chestpiece";
        public static final String ISOTHERMAL_PADDING_LEGGINGS = "isothermal_padding_leggings";
        public static final String ISOTHERMAL_PADDING_BOOTS = "isothermal_padding_boots";

        public static final String TIER_2_ROCKET_SCHEMATIC = "tier_2_rocket_schematic";
        public static final String TIER_3_ROCKET_SCHEMATIC = "tier_3_rocket_schematic";
        public static final String CARGO_ROCKET_SCHEMATIC = "cargo_rocket_schematic";
        public static final String MOON_BUGGY_SCHEMATIC = "moon_buggy_schematic";
        public static final String ASTRO_MINER_SCHEMATIC = "astro_miner_schematic";

        public static final String ROCKET_SPAWN_EGG_T1 = "rocket_spawn_egg_t1";
        public static final String MOON_VILLAGER_SPAWN_EGG = "moon_villager_spawn_egg";
        public static final String EVOLVED_ZOMBIE_SPAWN_EGG = "evolved_zombie_spawn_egg";
    }

    public static class Particles {
        public static final String DRIPPING_FUEL_PARTICLE = "dripping_fuel_particle";
        public static final String DRIPPING_CRUDE_OIL_PARTICLE = "dripping_crude_oil_particle";
    }

    public static class Config {
        public static final String TITLE = "config.galacticraft-rewoven.title";
        public static final String RESET = "config.galacticraft-rewoven.reset";

        public static final String DEBUG = "config.galacticraft-rewoven.debug";
        public static final String DEBUG_LOGGING = "config.galacticraft-rewoven.debug.logging";

        public static final String ENERGY = "config.galacticraft-rewoven.energy";

        public static final String WIRES = "config.galacticraft-rewoven.energy.wires";
        public static final String WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft-rewoven.energy.wires.transfer_limit";
        public static final String HEAVY_WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft-rewoven.energy.wires.heavy_transfer_limit";

        public static final String MACHINES = "config.galacticraft-rewoven.energy.machines";
        public static final String COAL_GENERATOR_ENERGY_PRODUCTION_RATE = "config.galacticraft-rewoven.energy.machines.coal_generator_energy_production_rate";
        public static final String SOLAR_PANEL_ENERGY_PRODUCTION_RATE = "config.galacticraft-rewoven.energy.machines.solar_panel_energy_production_rate";
        public static final String CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.circuit_fabricator_energy_consumption_rate";
        public static final String ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.electric_compressor_energy_consumption_rate";
        public static final String OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.oxygen_collector_energy_consumption_rate";
        public static final String REFINERY_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.refinery_energy_consumption_rate";
        public static final String ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.electric_furnace_energy_consumption_rate";
        public static final String ENERGY_STORAGE_MODULE_STORAGE_SIZE = "config.galacticraft-rewoven.energy.machines.energy_storage_module_storage_size";
        public static final String ENERGY_STORAGE_SIZE = "config.galacticraft-rewoven.energy.machines.energy_storage_size";
        public static final String OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.oxygen_compressor_energy_consumption_rate";
        public static final String OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft-rewoven.energy.machines.oxygen_decompressor_energy_consumption_rate";
    }

    public static class Energy {
        public static final String GALACTICRAFT_JOULES = "galacticraft_joules";
    }

    public static class ScreenTextures {
        public static final String COAL_GENERATOR_SCREEN = "gui/coal_generator_screen";
        public static final String SOLAR_PANEL_SCREEN = "gui/solar_panel_screen";
        public static final String CIRCUIT_FABRICATOR_SCREEN = "gui/circuit_fabricator_screen";
        public static final String REFINERY_SCREEN = "gui/refinery_screen";
        public static final String ELECTRIC_FURNACE_SCREEN = "gui/electric_furnace_screen";
        public static final String ELECTRIC_ARC_FURNACE_SCREEN = "gui/electric_arc_furnace_screen";
        public static final String COMPRESSOR_SCREEN = "gui/compressor_screen";
        public static final String ELECTRIC_COMPRESSOR_SCREEN = "gui/electric_compressor_screen";
        public static final String ENERGY_STORAGE_MODULE_SCREEN = "gui/energy_storage_module_screen";
        public static final String OXYGEN_COLLECTOR_SCREEN = "gui/oxygen_collector_screen";

        public static final String MACHINE_CONFIG_TABS = "gui/machine_config_tabs";
        public static final String MACHINE_CONFIG_PANELS = "gui/machine_config_panels";
        public static final String PLAYER_INVENTORY_SCREEN = "gui/player_inventory_screen";
        public static final String PLAYER_INVENTORY_TABS = "gui/player_inventory_switch_tabs";
        public static final String OVERLAY = "gui/overlay";

        public static final String MAP_SCREEN = "gui/map";
        public static final String PLANET_ICONS = "gui/planet_icons";
        public static final String BUBBLE_DISTRIBUTOR_SCREEN = "gui/oxygen_bubble_distributor_screen";
        public static final String OXYGEN_COMPRESSOR_SCREEN = "gui/oxygen_compressor_screen";
        public static final String OXYGEN_STORAGE_MODULE_SCREEN = "gui/oxygen_storage_module_screen";
        public static final String OXYGEN_SEALER_SCREEN = "gui/oxygen_sealer_screen";

        public static String getRaw(String path) {
            return "textures/" + path + ".png";
        }
    }

    public static class SlotSprites {
        public static final String THERMAL_HEAD = "slot/thermal_helmet";
        public static final String THERMAL_CHEST = "slot/thermal_chestpiece";
        public static final String THERMAL_PANTS = "slot/thermal_leggings";
        public static final String THERMAL_BOOTS = "slot/thermal_boots";
        public static final String OXYGEN_MASK = "slot/oxygen_mask";
        public static final String OXYGEN_GEAR = "slot/oxygen_gear";
        public static final String OXYGEN_TANK = "slot/oxygen_tank";
    }

    public static class Entities {
        public static final String MOON_VILLAGER = "moon_villager";
        public static final String EVOLVED_ZOMBIE = "evolved_zombie";
        public static final String EVOLVED_CREEPER = "evolved_creeper";
        public static final String T1_ROCKET = "t1_rocket";
        public static final String BUBBLE = "bubble";
        public static final String EVOLVED_SKELETON = "evolved_skeleton";
        public static final String EVOLVED_SPIDER = "evolved_spider";
        public static final String EVOLVED_PILLAGER = "evolved_pillager";
        public static final String EVOLVED_EVOKER = "evolved_evoker";
        public static final String EVOLVED_VINDICATOR = "evolved_vindicator";
    }

    public static class TextureCoordinates {
        public static final int OVERLAY_WIDTH = 16;
        public static final int OVERLAY_HEIGHT = 48;

        public static final int ENERGY_DARK_X = 0;
        public static final int ENERGY_DARK_Y = 0;
        public static final int ENERGY_LIGHT_X = 16;
        public static final int ENERGY_LIGHT_Y = 0;

        public static final int OXYGEN_DARK_X = 0;
        public static final int OXYGEN_DARK_Y = 50;
        public static final int OXYGEN_LIGHT_X = 16;
        public static final int OXYGEN_LIGHT_Y = 50;

        public static final int FLUID_TANK_WIDTH = 18;

        private static final int BASE_FLUID_TANK_Y = 49;

        public static final int FLUID_TANK_8_16_X = 32;
        public static final int FLUID_TANK_8_16_Y = BASE_FLUID_TANK_Y;
        public static final int FLUID_TANK_8_16_HEIGHT = 49;

        public static final int FLUID_TANK_7_14_X = FLUID_TANK_8_16_X + FLUID_TANK_WIDTH;
        public static final int FLUID_TANK_7_14_Y = BASE_FLUID_TANK_Y;
        public static final int FLUID_TANK_7_14_HEIGHT = FLUID_TANK_8_16_HEIGHT - 6; // segment size

        public static final int FLUID_TANK_6_12_X = FLUID_TANK_7_14_X + FLUID_TANK_WIDTH;
        public static final int FLUID_TANK_6_12_Y = BASE_FLUID_TANK_Y;
        public static final int FLUID_TANK_6_12_HEIGHT = FLUID_TANK_7_14_HEIGHT - 6;

        public static final int FLUID_TANK_5_10_X = FLUID_TANK_6_12_X + FLUID_TANK_WIDTH;
        public static final int FLUID_TANK_5_10_Y = BASE_FLUID_TANK_Y;
        public static final int FLUID_TANK_5_10_HEIGHT = FLUID_TANK_6_12_HEIGHT - 6;

        public static final int FLUID_TANK_4_8_X = FLUID_TANK_5_10_X + FLUID_TANK_WIDTH;
        public static final int FLUID_TANK_4_8_Y = BASE_FLUID_TANK_Y;
        public static final int FLUID_TANK_4_8_HEIGHT = FLUID_TANK_5_10_HEIGHT - 6;

        public static final int FLUID_TANK_3_6_X = FLUID_TANK_5_10_X;
        public static final int FLUID_TANK_3_6_Y = FLUID_TANK_5_10_Y - FLUID_TANK_5_10_HEIGHT;
        public static final int FLUID_TANK_3_6_HEIGHT = FLUID_TANK_4_8_HEIGHT - 6;

        public static final int FLUID_TANK_2_4_X = FLUID_TANK_6_12_X;
        public static final int FLUID_TANK_2_4_Y = FLUID_TANK_6_12_Y - FLUID_TANK_6_12_HEIGHT;
        public static final int FLUID_TANK_2_4_HEIGHT = FLUID_TANK_3_6_HEIGHT - 6;

        public static final int FLUID_TANK_1_2_X = FLUID_TANK_7_14_X;
        public static final int FLUID_TANK_1_2_Y = FLUID_TANK_7_14_Y - FLUID_TANK_7_14_HEIGHT;
        public static final int FLUID_TANK_1_2_HEIGHT = FLUID_TANK_2_4_HEIGHT - 6;

        public static final int FLUID_TANK_UNDERLAY_OFFSET = -49;

        public static final int BUTTON_WIDTH = 13;
        public static final int BUTTON_HEIGHT = 13;

        public static final int BUTTON_RED_X = 0;
        public static final int BUTTON_RED_Y = 115;
        public static final int BUTTON_RED_HOVER_X = 0;
        public static final int BUTTON_RED_HOVER_Y = 102;

        public static final int BUTTON_GREEN_X = 13;
        public static final int BUTTON_GREEN_Y = 115;
        public static final int BUTTON_GREEN_HOVER_X = 13;
        public static final int BUTTON_GREEN_HOVER_Y = 102;

        public static final int BUTTON_NORMAL_X = 26;
        public static final int BUTTON_NORMAL_Y = 115;
        public static final int BUTTON_NORMAL_HOVER_X = 26;
        public static final int BUTTON_NORMAL_HOVER_Y = 102;

        public static final int ARROW_VERTICAL_WIDTH = 11;
        public static final int ARROW_VERTICAL_HEIGHT = 10;

        public static final int ARROW_UP_X = 39;
        public static final int ARROW_UP_Y = 108;
        public static final int ARROW_UP_HOVER_X = 50;
        public static final int ARROW_UP_HOVER_Y = 108;

        public static final int ARROW_DOWN_X = 39;
        public static final int ARROW_DOWN_Y = 118;
        public static final int ARROW_DOWN_HOVER_X = 50;
        public static final int ARROW_DOWN_HOVER_Y = 118;

    }

    public static class ScreenHandler {
        public static final String COAL_GENERATOR_SCREEN_HANDLER = "coal_generator_screen_handler";
        public static final String BASIC_SOLAR_SCREEN_HANDLER = "basic_solar_panel_screen_handler";
        public static final String ADVANCED_SOLAR_SCREEN_HANDLER = "advanced_solar_panel_screen_handler";
        public static final String CIRCUIT_FABRICATOR_SCREEN_HANDLER = "circuit_fabricator_screen_handler";
        public static final String COMPRESSOR_SCREEN_HANDLER = "compressor_screen_handler";
        public static final String ELECTRIC_COMPRESSOR_SCREEN_HANDLER = "electric_compressor_screen_handler";
        public static final String PLAYER_INVENTORY_SCREEN_HANDLER = "player_inventory_screen_handler";
        public static final String ENERGY_STORAGE_MODULE_SCREEN_HANDLER = "energy_storage_module_screen_handler";
        public static final String REFINERY_SCREEN_HANDLER = "refinery_screen_handler";
        public static final String ELECTRIC_FURNACE_SCREEN_HANDLER = "electric_furnace_screen_handler";
        public static final String ELECTRIC_ARC_FURNACE_SCREEN_HANDLER = "electric_arc_furnace_screen_handler";
        public static final String OXYGEN_COLLECTOR_SCREEN_HANDLER = "oxygen_collector_screen_handler";
        public static final String BUBBLE_DISTRIBUTOR_SCREEN_HANDLER = "bubble_distributor_screen_handler";
        public static final String OXYGEN_COMPRESSOR_SCREEN_HANDLER = "oxygen_compressor_screen_handler";
        public static final String OXYGEN_DECOMPRESSOR_SCREEN_HANDLER = "oxygen_decompressor_screen_handler";
        public static final String OXYGEN_STORAGE_MODULE_SCREEN_HANDLER = "oxygen_storage_module_screen_handler";
        public static final String OXYGEN_SEALER_SCREEN_HANDLER = "oxygen_sealer";
    }

    public static class Biomes {
        public static class Moon {
            public static final String HIGHLANDS_PLAINS = "moon_highlands_plains";
            public static final String HIGHLANDS_ROCKS = "moon_highlands_rocks";
            public static final String HIGHLANDS_VALLEY = "moon_highlands_valley";
            public static final String MARE_PLAINS = "moon_mare_plains";
            public static final String MARE_ROCKS = "moon_mare_rocks";
            public static final String MARE_EDGE = "moon_mare_edge";
        }

    }

    public static class LootTables {
        public static final String BASIC_MOON_RUINS_CHEST = "chests/moon_ruins/basic_chest";
    }

    public static class Misc {
        public static final Text EMPTY_TEXT = new LiteralText("");
        public static final Style TOOLTIP_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);
        public static final Identifier EMPTY = new Identifier("empty");
        public static final Predicate<?> ALWAYS_FALSE = o -> false;
        public static final Predicate<?> ALWAYS_TRUE = o -> true;
        public static final FluidFilter LOX_ONLY = key -> GalacticraftTags.OXYGEN.contains(key.getRawFluid());

        @SuppressWarnings("unchecked")
        public static <T> Predicate<T> alwaysFalse() {
            return (Predicate<T>) ALWAYS_FALSE;
        }

        @SuppressWarnings("unchecked")
        public static <T> Predicate<T> alwaysTrue() {
            return (Predicate<T>) ALWAYS_TRUE;
        }
    }

    public static class Nbt {
        public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    }
}
