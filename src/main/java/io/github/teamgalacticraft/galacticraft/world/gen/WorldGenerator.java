package io.github.teamgalacticraft.galacticraft.world.gen;

public class WorldGenerator {

    public static void register() {
        registerOverworld();
        registerMoon();
        registerMars();
    }

    private static void registerOverworld() {
        OreGenerator.registerOres();
        OilPoolGenerator.registerOilLake();
    }

    private static void registerMoon() {

    }

    private static void registerMars() {

    }
}
