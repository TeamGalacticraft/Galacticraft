/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.RawFluidTagFilter;
import dev.galacticraft.mod.api.block.util.BlockFace;
import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.util.ColorUtil;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface Constant {
    String MOD_ID = "galacticraft";

    interface Block {
        String ITEM_GROUP_BLOCKS = "blocks";
        String ITEM_GROUP_MACHINES = "machines";

        // Natural
        String MOON_TURF = "moon_turf";
        String MOON_SURFACE_ROCK = "moon_surface_rock";
        String MOON_ROCK = "moon_rock";
        String COBBLED_MOON_ROCK = "cobbled_moon_rock";
        String MOON_BASALT = "moon_basalt";
        String MOON_DIRT = "moon_dirt";
        String MARS_SURFACE_ROCK = "mars_surface_rock";
        String MARS_SUB_SURFACE_ROCK = "mars_sub_surface_rock";
        String MARS_STONE = "mars_stone";
        String MARS_COBBLESTONE = "mars_cobblestone";
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
        String ASTEROID_ALUMINUM_ORE = "asteroid_aluminum_ore";
        String MOON_CHEESE_ORE = "moon_cheese_ore";
        String MOON_COPPER_ORE = "moon_copper_ore";
        String MARS_COPPER_ORE = "mars_copper_ore";
        String DESH_ORE = "desh_ore";
        String ILMENITE_ORE = "ilmenite_ore";
        String MARS_IRON_ORE = "mars_iron_ore";
        String ASTEROID_IRON_ORE = "asteroid_iron_ore";
        String SILICON_ORE = "silicon_ore";
        String MOON_TIN_ORE = "moon_tin_ore";
        String MARS_TIN_ORE = "mars_tin_ore";
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
        String BRONZE_DECORATION = "bronze_decoration";
        String COPPER_DECORATION = "copper_decoration";
        String IRON_DECORATION = "iron_decoration";
        String METEORIC_IRON_DECORATION = "meteoric_iron_decoration";
        String STEEL_DECORATION = "steel_decoration";
        String TIN_DECORATION = "tin_decoration";
        String TITANIUM_DECORATION = "titanium_decoration";
        String DARK_DECORATION = "dark_decoration";
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
        String MOON_CHEESE_LEAVES = "moon_cheese_leaves";
        String MOON_CHEESE_LOG = "moon_cheese_log";

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
        String ASTRO_MINER_BASE = "astro_miner_base";
        String SHORT_RANGE_TELEPAD = "short_range_telepad";

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
        String MOON_BASALT_BRICK = "moon_basalt_brick";
        String CRACKED_MOON_BASALT_BRICK = "cracked_moon_basalt_brick";
        String LUNAR_CARTOGRAPHY_TABLE = "lunar_cartography_table";
        String OXYGEN_STORAGE_MODULE = "oxygen_storage_module";
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

        static Identifier getIdentifier(String s) {
            return new Identifier(Constant.MOD_ID, "block/" + s);
        }
    }

    interface Item {
        String ITEM_GROUP = "items";
        String LEAD_INGOT = "lead_ingot";
        String RAW_SILICON = "raw_silicon";
        String RAW_METEORIC_IRON = "raw_meteoric_iron";
        String METEORIC_IRON_INGOT = "meteoric_iron_ingot";
        String LUNAR_SAPPHIRE = "lunar_sapphire";
        String UNREFINED_DESH = "raw_desh";
        String DESH_INGOT = "desh_ingot";
        String DESH_STICK = "desh_stick";
        String CARBON_FRAGMENTS = "carbon_fragments";
        String IRON_SHARD = "iron_shard";
        String TITANIUM_SHARD = "titanium_shard";
        String TITANIUM_INGOT = "titanium_ingot";
        String TITANIUM_DUST = "titanium_dust";
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
        String WHITE_OXYGEN_MASK = "white_oxygen_mask";
        String GREY_OXYGEN_MASK = "grey_oxygen_mask";
        String BLACK_OXYGEN_MASK = "black_oxygen_mask";
        String ORANGE_OXYGEN_MASK = "orange_oxygen_mask";
        String MAGENTA_OXYGEN_MASK = "magenta_oxygen_mask";
        String LIGHT_BLUE_OXYGEN_MASK = "light_blue_oxygen_mask";
        String YELLOW_OXYGEN_MASK = "yellow_oxygen_mask";
        String LIME_OXYGEN_MASK = "lime_oxygen_mask";
        String PINK_OXYGEN_MASK = "pink_oxygen_mask";
        String LIGHT_GREY_OXYGEN_MASK = "light_grey_oxygen_mask";
        String CYAN_OXYGEN_MASK = "cyan_oxygen_mask";
        String PURPLE_OXYGEN_MASK = "purple_oxygen_mask";
        String BLUE_OXYGEN_MASK = "blue_oxygen_mask";
        String BROWN_OXYGEN_MASK = "brown_oxygen_mask";
        String GREEN_OXYGEN_MASK = "green_oxygen_mask";
        String RED_OXYGEN_MASK = "red_oxygen_mask";

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

        String TIER_2_ROCKET_SCHEMATIC = "tier_2_rocket_schematic";
        String TIER_3_ROCKET_SCHEMATIC = "tier_3_rocket_schematic";
        String CARGO_ROCKET_SCHEMATIC = "cargo_rocket_schematic";
        String MOON_BUGGY_SCHEMATIC = "moon_buggy_schematic";
        String ASTRO_MINER_SCHEMATIC = "astro_miner_schematic";

        String MOON_VILLAGER_SPAWN_EGG = "moon_villager_spawn_egg";
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
    }

    interface ScreenTexture {
        Identifier COAL_GENERATOR_SCREEN = new Identifier(MOD_ID, "textures/gui/coal_generator_screen.png");
        Identifier SOLAR_PANEL_SCREEN = new Identifier(MOD_ID, "textures/gui/solar_panel_screen.png");
        Identifier CIRCUIT_FABRICATOR_SCREEN = new Identifier(MOD_ID, "textures/gui/circuit_fabricator_screen.png");
        Identifier REFINERY_SCREEN = new Identifier(MOD_ID, "textures/gui/refinery_screen.png");
        Identifier ELECTRIC_FURNACE_SCREEN = new Identifier(MOD_ID, "textures/gui/electric_furnace_screen.png");
        Identifier ELECTRIC_ARC_FURNACE_SCREEN = new Identifier(MOD_ID, "textures/gui/electric_arc_furnace_screen.png");
        Identifier COMPRESSOR_SCREEN = new Identifier(MOD_ID, "textures/gui/compressor_screen.png");
        Identifier ELECTRIC_COMPRESSOR_SCREEN = new Identifier(MOD_ID, "textures/gui/electric_compressor_screen.png");
        Identifier ENERGY_STORAGE_MODULE_SCREEN = new Identifier(MOD_ID, "textures/gui/energy_storage_module_screen.png");
        Identifier OXYGEN_COLLECTOR_SCREEN = new Identifier(MOD_ID, "textures/gui/oxygen_collector_screen.png");

        Identifier MACHINE_CONFIG_PANELS = new Identifier(MOD_ID, "textures/gui/machine_config.png");
        Identifier PLAYER_INVENTORY_SCREEN = new Identifier(MOD_ID, "textures/gui/player_inventory_screen.png");
        Identifier PLAYER_INVENTORY_TABS = new Identifier(MOD_ID, "textures/gui/player_inventory_switch_tabs.png");
        Identifier OVERLAY = new Identifier(MOD_ID, "textures/gui/overlay.png");

        Identifier MAP_SCREEN = new Identifier(MOD_ID, "textures/gui/map.png");
        Identifier PLANET_ICONS = new Identifier(MOD_ID, "textures/gui/planet_icons.png");
        Identifier BUBBLE_DISTRIBUTOR_SCREEN = new Identifier(MOD_ID, "textures/gui/oxygen_bubble_distributor_screen.png");
        Identifier OXYGEN_COMPRESSOR_SCREEN = new Identifier(MOD_ID, "textures/gui/oxygen_compressor_screen.png");
        Identifier OXYGEN_STORAGE_MODULE_SCREEN = new Identifier(MOD_ID, "textures/gui/oxygen_storage_module_screen.png");
        Identifier OXYGEN_SEALER_SCREEN = new Identifier(MOD_ID, "textures/gui/oxygen_sealer_screen.png");

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
        String MOON_VILLAGER = "moon_villager";
        String EVOLVED_ZOMBIE = "evolved_zombie";
        String EVOLVED_CREEPER = "evolved_creeper";
        String T1_ROCKET = "t1_rocket";
        String BUBBLE = "bubble";
        String EVOLVED_SKELETON = "evolved_skeleton";
        String EVOLVED_SPIDER = "evolved_spider";
        String EVOLVED_PILLAGER = "evolved_pillager";
        String EVOLVED_EVOKER = "evolved_evoker";
        String EVOLVED_VINDICATOR = "evolved_vindicator";
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

        int FLUID_TANK_8_16_X = 32;
        int FLUID_TANK_8_16_Y = BASE_FLUID_TANK_Y;
        int FLUID_TANK_8_16_HEIGHT = 49;

        int FLUID_TANK_7_14_X = FLUID_TANK_8_16_X + FLUID_TANK_WIDTH;
        int FLUID_TANK_7_14_Y = BASE_FLUID_TANK_Y;
        int FLUID_TANK_7_14_HEIGHT = FLUID_TANK_8_16_HEIGHT - 6; // segment size

        int FLUID_TANK_6_12_X = FLUID_TANK_7_14_X + FLUID_TANK_WIDTH;
        int FLUID_TANK_6_12_Y = BASE_FLUID_TANK_Y;
        int FLUID_TANK_6_12_HEIGHT = FLUID_TANK_7_14_HEIGHT - 6;

        int FLUID_TANK_5_10_X = FLUID_TANK_6_12_X + FLUID_TANK_WIDTH;
        int FLUID_TANK_5_10_Y = BASE_FLUID_TANK_Y;
        int FLUID_TANK_5_10_HEIGHT = FLUID_TANK_6_12_HEIGHT - 6;

        int FLUID_TANK_4_8_X = FLUID_TANK_5_10_X + FLUID_TANK_WIDTH;
        int FLUID_TANK_4_8_Y = BASE_FLUID_TANK_Y;
        int FLUID_TANK_4_8_HEIGHT = FLUID_TANK_5_10_HEIGHT - 6;

        int FLUID_TANK_3_6_X = FLUID_TANK_5_10_X;
        int FLUID_TANK_3_6_Y = FLUID_TANK_5_10_Y - FLUID_TANK_5_10_HEIGHT;
        int FLUID_TANK_3_6_HEIGHT = FLUID_TANK_4_8_HEIGHT - 6;

        int FLUID_TANK_2_4_X = FLUID_TANK_6_12_X;
        int FLUID_TANK_2_4_Y = FLUID_TANK_6_12_Y - FLUID_TANK_6_12_HEIGHT;
        int FLUID_TANK_2_4_HEIGHT = FLUID_TANK_3_6_HEIGHT - 6;

        int FLUID_TANK_1_2_X = FLUID_TANK_7_14_X;
        int FLUID_TANK_1_2_Y = FLUID_TANK_7_14_Y - FLUID_TANK_7_14_HEIGHT;
        int FLUID_TANK_1_2_HEIGHT = FLUID_TANK_2_4_HEIGHT - 6;

        int FLUID_TANK_UNDERLAY_OFFSET = -49;

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

    interface FeatureRendererTexture {
        String EVOLVED_CREEPER_CHARGE = "textures/entity/creeper/creeper_armor.png";
        String EVOLVED_SPIDER_EYES = "textures/entity/evolved/spider_eyes.png";

        String GEAR = "textures/entity/gear/galacticraft_gear.png";
        int GEAR_WIDTH = 128;
        int GEAR_HEIGHT = 32;

        String OXYGEN_MASK = "textures/entity/gear/oxygen_mask.png";
        int OXYGEN_MASK_WIDTH = 128;
        int OXYGEN_MASK_HEIGHT = 128;

        String OXYGEN_TANK = "textures/entity/gear/oxygen_tank.png";
        int OXYGEN_TANK_WIDTH = 128;
        int OXYGEN_TANK_HEIGHT = 128;

        String FREQUENCY_MODULE = "textures/entity/gear/frequency_module.png";
        int FREQUENCY_MODULE_WIDTH = 16;
        int FREQUENCY_MODULE_HEIGHT = 16;
    }

    interface ScreenHandler {
        String COAL_GENERATOR_SCREEN_HANDLER = "coal_generator_screen_handler";
        String BASIC_SOLAR_PANEL_SCREEN_HANDLER = "basic_solar_panel_screen_handler";
        String ADVANCED_SOLAR_PANEL_SCREEN_HANDLER = "advanced_solar_panel_screen_handler";
        String CIRCUIT_FABRICATOR_SCREEN_HANDLER = "circuit_fabricator_screen_handler";
        String COMPRESSOR_SCREEN_HANDLER = "compressor_screen_handler";
        String ELECTRIC_COMPRESSOR_SCREEN_HANDLER = "electric_compressor_screen_handler";
        String PLAYER_INVENTORY_SCREEN_HANDLER = "player_inventory_screen_handler";
        String ENERGY_STORAGE_MODULE_SCREEN_HANDLER = "energy_storage_module_screen_handler";
        String REFINERY_SCREEN_HANDLER = "refinery_screen_handler";
        String ELECTRIC_FURNACE_SCREEN_HANDLER = "electric_furnace_screen_handler";
        String ELECTRIC_ARC_FURNACE_SCREEN_HANDLER = "electric_arc_furnace_screen_handler";
        String OXYGEN_COLLECTOR_SCREEN_HANDLER = "oxygen_collector_screen_handler";
        String BUBBLE_DISTRIBUTOR_SCREEN_HANDLER = "bubble_distributor_screen_handler";
        String OXYGEN_COMPRESSOR_SCREEN_HANDLER = "oxygen_compressor_screen_handler";
        String OXYGEN_DECOMPRESSOR_SCREEN_HANDLER = "oxygen_decompressor_screen_handler";
        String OXYGEN_STORAGE_MODULE_SCREEN_HANDLER = "oxygen_storage_module_screen_handler";
        String OXYGEN_SEALER_SCREEN_HANDLER = "oxygen_sealer";
    }

    interface Biome {
        interface Moon {
            String HIGHLANDS = "moon_highlands";
            String HIGHLANDS_EDGE = "moon_highlands_edge";
            String MARE = "moon_mare";
            String MARE_EDGE = "moon_mare_edge";
        }

    }

    interface LootTable {
        String BASIC_MOON_RUINS_CHEST = "chests/moon_ruins/basic_chest";
    }

    interface Filter {
        FluidFilter LOX_ONLY = new RawFluidTagFilter(GalacticraftTag.LIQUID_OXYGEN);
        FluidFilter OIL = new RawFluidTagFilter(GalacticraftTag.OIL);
        FluidFilter FUEL = new RawFluidTagFilter(GalacticraftTag.FUEL);
    }

    interface Text {
        Style DARK_GRAY_STYLE = Style.EMPTY.withColor(Formatting.DARK_GRAY);
        Style GOLD_STYLE = Style.EMPTY.withColor(Formatting.GOLD);
        Style GREEN_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
        Style RED_STYLE = Style.EMPTY.withColor(Formatting.RED);
        Style BLUE_STYLE = Style.EMPTY.withColor(Formatting.BLUE);
        Style AQUA_STYLE = Style.EMPTY.withColor(Formatting.AQUA);
        Style GRAY_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
        Style DARK_RED_STYLE = Style.EMPTY.withColor(Formatting.DARK_RED);
        Style LIGHT_PURPLE_STYLE = Style.EMPTY.withColor(Formatting.LIGHT_PURPLE);
        Style YELLOW_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);
        Style WHITE_STYLE = Style.EMPTY.withColor(Formatting.WHITE);

        static Style getStorageLevelColor(double scale) {
            return Style.EMPTY.withColor(TextColor.fromRgb(((int)(255 * scale) << 16) + (((int)(255 * ( 1.0 - scale))) << 8)));
        }

        static Style getRainbow(int ticks) {
            return Style.EMPTY.withColor(TextColor.fromRgb(ColorUtil.hsbToRGB(ticks / 500.0f, 1, 1)));
        }
    }

    interface Nbt {
        String BLOCK_ENTITY_TAG = "BlockEntityTag";
        String NO_DROP = "NoDrop";
        String TOTAL_OXYGEN = "TotalOxygen";
        String OXYGEN = "Oxygen";
        String OWNER = "Owner";
        String PROGRESS = "Progress";
        String SIZE = "Size";
        String MAX_SIZE = "MaxSize";
        String GC_DATA = "GCData";
        String FUEL_TIME = "FuelTime";
        String TEAM = "Team";
        String ACCESSIBILITY = "Accessibility";
        String SECURITY = "Security";
        String CONFIGURATION = "Configuration";
        String AMOUNT = "Amount";
        String PATH = "Path";
        String HAS_DIRECTION = "HasDirection";
        String VALUE = "Value";
        String ENERGY = "Energy";
        String AUTOMATION_TYPE = "AutomationType";
        String BABY = "Baby";
        String DIRECTION = "Direction";
        String SOURCE = "Source";
        String REDSTONE_INTERACTION_TYPE = "RedstoneInteraction";
        String MATCH = "Match";
        String INTEGER = "Integer";
    }

    interface Property {
        BooleanProperty ACTIVE = BooleanProperty.of("active");
    }

    interface Misc {
        Identifier EMPTY = new Identifier("empty");
        Direction[] DIRECTIONS = Direction.values();
        BlockFace[] BLOCK_FACES = BlockFace.values();
        String LOGGER_PREFIX = "[Galacticraft] ";
        boolean DEBUG = false;
        int MAX_STRING_READ = 32767;
    }

    interface Recipe {
        String FABRICATION = "fabrication";
        String COMPRESSING = "compressing";
        interface Serializer {
            String COMPRESSING_SHAPELESS = "compressing_shapeless";
            String COMPRESSING_SHAPED = "compressing_shaped";
        }
    }
}
