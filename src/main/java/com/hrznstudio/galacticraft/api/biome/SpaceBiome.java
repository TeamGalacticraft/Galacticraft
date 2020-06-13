package com.hrznstudio.galacticraft.api.biome;

public interface SpaceBiome {

    default boolean forceSmallCraters() {
        return false;
    }

    default boolean forceLargeCraters() {
        return false;
    }

    default boolean forceMediumCraters() {
        return false;
    }

    default boolean hasCraters() {
        return true;
    }

    default double getCraterChance() {
        return 100.0D;
    }
}
