package io.github.teamgalacticraft.galacticraft.world.gen;

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

    }
}
