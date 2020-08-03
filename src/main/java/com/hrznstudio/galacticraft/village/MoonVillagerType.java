package com.hrznstudio.galacticraft.village;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerType;

public class MoonVillagerType {
    public static final VillagerType MOON_HIGHLANDS = Registry.register(Registry.VILLAGER_TYPE, new Identifier(Constants.MOD_ID, "moon_hl"), new VillagerType() {
        public String toString() {
            return "GC:R - Moon Highlands Villager";
        }
    });

    public static final VillagerType MOON_HIGHLANDS_ROCKS = Registry.register(Registry.VILLAGER_TYPE, new Identifier(Constants.MOD_ID, "moon_hl_rocks"), new VillagerType() {
        public String toString() {
            return "GC:R - Moon Highlands Rocks Villager";
        }
    });

    public static void register() {
        VillagerType.BIOME_TO_TYPE.put(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS, MOON_HIGHLANDS);
        VillagerType.BIOME_TO_TYPE.put(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS, MOON_HIGHLANDS_ROCKS);
        VillagerType.BIOME_TO_TYPE.put(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY, MOON_HIGHLANDS_ROCKS);
    }
}
