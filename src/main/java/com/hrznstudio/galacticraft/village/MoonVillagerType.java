package com.hrznstudio.galacticraft.village;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerTypeHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerType;

public class MoonVillagerType {
    public static final VillagerType MOON_HIGHLANDS = VillagerTypeHelper.register(new Identifier(Constants.MOD_ID, "moon_highlands"));
    public static final VillagerType MOON_HIGHLANDS_ROCKS = VillagerTypeHelper.register(new Identifier(Constants.MOD_ID, "moon_highlands_rocks"));

    public static void register() {
        VillagerTypeHelper.addVillagerTypeToBiome(GalacticraftBiomes.Moon.HIGHLANDS_PLAINS, MOON_HIGHLANDS);
        VillagerTypeHelper.addVillagerTypeToBiome(GalacticraftBiomes.Moon.HIGHLANDS_ROCKS, MOON_HIGHLANDS_ROCKS);
        VillagerTypeHelper.addVillagerTypeToBiome(GalacticraftBiomes.Moon.HIGHLANDS_VALLEY, MOON_HIGHLANDS_ROCKS);
    }
}
