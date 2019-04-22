package com.hrznstudio.galacticraft.world.gen;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WorldGenerator {

    public static void register() {
        registerOverworld();
        registerMoon();
        registerMars();
    }

    private static void registerOverworld() {
        OreGenerator.registerOverworldOres();
        OilPoolGenerator.registerOilLake();
    }

    private static void registerMoon() {
        OreGenerator.registerMoonOres();
    }

    private static void registerMars() {
        OreGenerator.registerMarsOres();
    }

}
