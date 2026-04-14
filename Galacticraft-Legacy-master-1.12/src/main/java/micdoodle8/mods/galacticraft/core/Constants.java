/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core;

import micdoodle8.mods.galacticraft.annotations.ForRemoval;

public class Constants
{

    public static final String MOD_ID_CORE                           = "galacticraftcore";
    public static final String MOD_ID_PLANETS                        = "galacticraftplanets";
    public static final String MOD_NAME_SIMPLE                       = "Galacticraft";

    public static final String VERSION                               = "${version}";

    public static final String DEPENDENCIES_FORGE                    = "required-after:forge@[14.23.5.2847,); ";
    public static final String DEPENDENCIES_MICCORE                  = "required-after:micdoodlecore; ";
    @Deprecated
    @ForRemoval(deadline = "4.1.0")
    public static final String DEPENDENCIES_MODS                     = "after:ic2; after:tconstruct; after:mantle;";

    public static final String MCVERSION                             = "[1.12)";

    public static final String CONFIG_CATEGORY_DIMENSIONS            = "dimensions";
    public static final String CONFIG_CATEGORY_ENTITIES              = "entities";
    public static final String CONFIG_CATEGORY_SCHEMATIC             = "schematic";
    public static final String CONFIG_CATEGORY_GENERAL               = "general";
    public static final String CONFIG_CATEGORY_WORLDGEN              = "worldgen";
    public static final String CONFIG_CATEGORY_ACHIEVEMENTS          = "achievements";
    public static final String CONFIG_CATEGORY_CLIENT                = "client";
    public static final String CONFIG_CATEGORY_SERVER                = "server";
    public static final String CONFIG_CATEGORY_CONTROLS              = "controls";
    public static final String CONFIG_CATEGORY_KEYS                  = "keybindings_initial";
    public static final String CONFIG_CATEGORY_DIFFICULTY            = "difficulty";
    public static final String CONFIG_CATEGORY_COMPATIBILITY         = "compatibility";

    public static final int    OVERWORLD_SKYPROVIDER_STARTHEIGHT     = 200;
    public static final int    OVERWORLD_CLOUD_HEIGHT                = 130;

    public static final float  LOX_GAS_RATIO                         = 5F / 54;

    public static final String CONFIG_FILE                           = "Galacticraft/core.cfg";
    public static final String POWER_CONFIG_FILE                     = "Galacticraft/energy.cfg";
    public static final String CHUNKLOADER_CONFIG_FILE               = "Galacticraft/chunkloading.cfg";
    public static final String PLANETS_CONFIG_FILE                   = "Galacticraft/planets.cfg";

    public static final String OLD_CONFIG_FILE                       = "Galacticraft/core.conf";
    public static final String OLD_POWER_CONFIG_FILE                 = "Galacticraft/power-GC3.conf";
    public static final String OLD_CHUNKLOADER_CONFIG_FILE           = "Galacticraft/chunkloading.conf";

    public static final String ASSET_PREFIX                          = "galacticraftcore";
    public static final String TEXTURE_PREFIX                        = ASSET_PREFIX + ":";
    public static final String PREFIX                                = "micdoodle8.";
    public static final String GCDATAFOLDER                          = "../galacticraft/";

    public static final int    GEAR_ID_OXYGEN_MASK                   = 0;
    public static final int    GEAR_ID_OXYGEN_GEAR                   = 1;
    public static final int    GEAR_ID_OXYGEN_TANK_LIGHT             = 2;
    public static final int    GEAR_ID_OXYGEN_TANK_MEDIUM            = 3;
    public static final int    GEAR_ID_OXYGEN_TANK_HEAVY             = 4;
    public static final int    GEAR_ID_OXYGEN_TANK_INFINITE          = 5;
    public static final int    GEAR_ID_THERMAL_PADDING_T1_HELMET     = 6;
    public static final int    GEAR_ID_THERMAL_PADDING_T1_CHESTPLATE = 7;
    public static final int    GEAR_ID_THERMAL_PADDING_T1_LEGGINGS   = 8;
    public static final int    GEAR_ID_THERMAL_PADDING_T1_BOOTS      = 9;
    public static final int    GEAR_ID_THERMAL_PADDING_T2_HELMET     = 10;
    public static final int    GEAR_ID_THERMAL_PADDING_T2_CHESTPLATE = 11;
    public static final int    GEAR_ID_THERMAL_PADDING_T2_LEGGINGS   = 12;
    public static final int    GEAR_ID_THERMAL_PADDING_T2_BOOTS      = 13;
    public static final int    GEAR_ID_PARACHUTE                     = 14;
    public static final int    GEAR_ID_FREQUENCY_MODULE              = 15;
    public static final int    GEAR_ID_SHIELD_CONTROLLER             = 16;

    /**
     * 128 squared (8 chunks range) <br>
     * used for small + high frequency TESR tiles like Fluid Pipes
     */
    public static final double RENDERDISTANCE_SHORT                  = 16384D;
    /**
     * 256 squared (16 chunks range) <br>
     * used for standard block-sized TESR tiles like Fluid Tanks or Treasure Chests, <br>
     * also heavy render burden large tiles like the Dish and Display Screen
     */
    public static final double RENDERDISTANCE_MEDIUM                 = 65536D;
    /**
     * 512 squared (32 chunks max range) <br>
     * used for uncommon, large or extra bright TESR tiles like Oxygen Bubble Distributors, Solar Panels or Panel Lighting
     */
    public static final double RENDERDISTANCE_LONG                   = 262144D;

    public static final float  RADIANS_TO_DEGREES                    = 180F / 3.1415927F;
    public static final double RADIANS_TO_DEGREES_D                  = 180D / Math.PI;

    public static final float  twoPI                                 = (float) Math.PI * 2F;
    public static final float  halfPI                                = (float) Math.PI / 2F;

    public static final String PERMISSION_CREATE_STATION             = "galacticraft.station.create";
}
