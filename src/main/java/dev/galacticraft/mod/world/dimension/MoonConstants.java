package dev.galacticraft.mod.world.dimension;

public class MoonConstants {
    public static final int MOON_MIN_TERRAIN_HEIGHT = -128;
    public static final int MOON_MAX_HEIGHT = 384;

    public static final int COMET_TUNDRA_SURFACE_HEIGHT = 60;
    public static final int BASALTIC_MARE_SURFACE_HEIGHT = 60;
    public static final int LUNAR_LOW_LANDS_SURFACE_HEIGHT = 80;
    public static final int LUNAR_HIGH_LANDS_SURFACE_HEIGHT = 140;

    public static final int COMET_TUNDRA_SURFACE_HEIGHT_VARIATION = 3;
    public static final int BASALTIC_MARE_SURFACE_HEIGHT_VARIATION = 3;
    public static final int LUNAR_LOW_LANDS_SURFACE_HEIGHT_VARIATION = 20;
    public static final int LUNAR_HIGH_LANDS_SURFACE_HEIGHT_VARIATION = 3;

    public static final float COMET_TUNDRA_CONTINENTALNESS_MINIMUM = -1.2f;
    public static final float BASALTIC_MARE_CONTINENTALNESS_MINIMUM = -0.455f;
    public static final float LUNAR_LOW_LANDS_CONTINENTALNESS_MINIMUM = -0.11f;
    public static final float LUNAR_HIGH_LANDS_CONTINENTALNESS_MINIMUM = 0.3f;

    public static final float LUNAR_LOW_TO_HIGH_LANDS_RANGE = 0.05f;
    public static final float BASALTIC_MARE_TO_LUNAR_LOW_LANDS_RANGE = 0.1f;

    // --OLIVINE CAVES--
    public static final float OLIVINE_CAVE_PROBABILITY = 0.25f;
    public static final int OLIVINE_CAVE_MIN_HEIGHT = -68;
    public static final int OLIVINE_CAVE_MAX_HEIGHT = 30;
    public static final float OLIVINE_CAVE_Y_SCALE_MIN = 0.8f;
    public static final float OLIVINE_CAVE_Y_SCALE_MAX = 2.2f;
    public static final float OLIVINE_CAVE_BASALT_INTERIOR_CHANCE = 0.95f; //95%
    public static final int OLI_GRUB_MAX_EGG_SPAWN = 50;
    public static final int OLI_GRUB_MIN_EGG_SPAWN = -50; //95%
}
