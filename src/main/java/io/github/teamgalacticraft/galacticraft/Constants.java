package io.github.teamgalacticraft.galacticraft;

import org.lwjgl.system.CallbackI;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class Constants {

    public static final String MOD_ID = "galacticraft-fabric";

    // Blocks
    public static class Blocks {
        // Natural
        public static final String ITEM_GROUP = "block";
        public static final String MOON_TURF = "moon_turf_block";
        public static final String MOON_ROCK = "moon_rock_block";
        public static final String MOON_DIRT = "moon_dirt_block";
        public static final String MOON_DUNGEON_BRICK_BLOCK = "moon_dungeon_brick_block";
        public static final String MARS_SURFACE_ROCK_BLOCK = "mars_surface_rock_block";
        public static final String MARS_SUB_SURFACE_ROCK_BLOCK = "mars_sub_surface_rock_block";
        public static final String MARS_STONE = "mars_stone_block";
        public static final String MARS_COBBLESTONE = "mars_cobblestone_block";
        public static final String MARS_DUNGEON_BRICK_BLOCK = "mars_dungeon_brick_block";
        public static final String DENSE_ICE_BLOCK = "dense_ice_block";
        public static final String ASTEROID_ROCK_BLOCK = "asteroid_rock_block";
        public static final String VENUS_ROCK_BLOCK = "venus_rock_block";
        public static final String VOLCANIC_ROCK_BLOCK = "volcanic_rock_block";
        public static final String SCORCHED_ROCK_BLOCK = "scorched_rock_block";
        public static final String PUMICE_BLOCK = "pumice_block";
        public static final String VAPOR_SPOUT_BLOCK = "vapor_spout_block";
        public static final String TIN_DECORATION_BLOCK = "tin_decoration_block";
        public static final String TIN_WALL_BLOCK = "tin_wall_block";
        public static final String COPPER_ORE_BLOCK = "copper_ore_block";
        public static final String TIN_ORE_BLOCK = "tin_ore_block";
        public static final String ALUMINUM_ORE_BLOCK = "aluminum_ore_block";
        public static final String SILICON_ORE_BLOCk = "silicon_ore_block";
        public static final String COPPER_BLOCK = "copper_block";
        public static final String TIN_BLOCK = "tin_block";
        public static final String ALUMINUM_BLOCK = "aluminum_block";
        public static final String SILICON_BLOCK = "silicon_block";
    }

    //Items
    public static class Items {
        public static final String ITEM_GROUP = "items";
        public static final String ALUMINUM_INGOT = "aluminum_ingot";
        public static final String COPPER_INGOT = "copper_ingot";
        public static final String TIN_INGOT = "tin_ingot";
        public static final String LEAD_INGOT = "lead_ingot";
        public static final String RAW_SILICON = "raw_silicon";
        public static final String RAW_METEORIC_IRON = "raw_meteoric_iron";
        public static final String METEORIC_IRON_INGOT = "meteoric_iron_ingot";
        public static final String LUNAR_SAPPHIRE = "lunar_sapphire";
        public static final String UNREFINED_DESH = "unrefined_desh";
        public static final String DESH_INGOT = "desh_ingot";
        public static final String DESH_STICK = "desh_stick";
        public static final String FRAGMENTED_CARBON = "fragmented_carbon";
        public static final String IRON_SHARD = "iron_shard";
        public static final String TITANIUM_SHARD = "titanium_shard";
        public static final String TITANIUM_INGOT = "titanium_ingot";
        public static final String TITANIUM_DUST = "titanium_dust";
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
        public static final String BLUE_SOLAR_PANEL = "blue_solar_panel";
        public static final String STEEL_POLE = "steel_pole";
        public static final String COPPER_CANISTER = "copper_canister";
        public static final String TIN_CANISTER = "tin_canister";
        public static final String THERMAL_CLOTH = "thermal_cloth";
        public static final String ISOTHERMAL_FABRIC = "isothermal_fabric";
        public static final String ORION_DRIVE = "orion_drive";
        //FOOD
        public static final String CHEESE_CURD = "cheese_curd";
        public static final String CHEESE_SLICE = "cheese_slice";
        public static final String BURGER_BUN = "burger_bun";
        public static final String GROUND_BEEF = "ground_beef";
        public static final String BEEF_PATTY = "beef_patty";
        public static final String CHEESEBURGER = "cheeseburger";
        //CANNED FOOD
        public static final String DEHYDRATED_APPLE = "dehydrated_apple";
        public static final String DEHYDRATED_CARROT = "dehydrated_carrot";
        public static final String DEHYDRATED_MELON = "dehydrated_melon";
        public static final String DEHYDRATED_POTATO = "dehydrated_potato";
        public static final String CANNED_BEEF = "canned_beef";
        //ROCKET PARTS
        public static final String TIER_1_HEAVY_DUTY_PLATE = "tier_1_heavy_duty_plate";
        public static final String TIER_2_HEAVY_DUTY_PLATE = "tier_2_heavy_duty_plate";
        public static final String TIER_3_HEAVY_DUTY_PLATE = "tier_3_heavy_duty_plate";
        public static final String NOSE_CONE = "nose_cone";
        public static final String HEAVY_NOSE_CONE = "heavy_nose_cone";
        public static final String ROCKET_ENGINE = "rocket_engine";
        public static final String HEAVY_ROCKET_ENGINE = "heavy_rocket_engine";
        public static final String ROCKET_FIN = "rocket_fin";
        public static final String HEAVY_ROCKET_FIN = "heavy_rocket_fin";
        public static final String TIER_1_BOOSTER = "tier_1_booster";
        //BUGGY
        public static final String BUGGY_SEAT = "buggy_seat";
        public static final String BUGGY_STORAGE_BOX = "buggy_storage_box";
        public static final String BUGGY_WHEEL = "buggy_wheel";
    }
}
