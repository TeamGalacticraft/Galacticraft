package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.CaveTransitionConfig;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRegistry;
import dev.galacticraft.mod.world.gen.cave.MoonCaveShapeType;
import dev.galacticraft.mod.world.gen.cave.PlanetCave;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedLavaTubeCaveShape;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

public class CheeseLavaTubeCave extends PlanetCave {
    public CheeseLavaTubeCave() {
        super(
                Constant.id("cheese_lava_tube_cave"),
                MoonCaveShapeType.LAVA_TUBE,
                new PathSolvedLavaTubeCaveShape(
                        5, 9,
                        3, 7,
                        10, 22,
                        22, 58,
                        2.2D, 4.5D,
                        7.0D, 26.0D,
                        70, 180,
                        -60, -50
                ),
                100,
                0.34F,
                16,
                26,
                -60,
                32,
                Blocks.YELLOW_WOOL.defaultBlockState(),
                Blocks.ORANGE_WOOL.defaultBlockState(),
                Blocks.GOLD_BLOCK.defaultBlockState(),
                CaveTransitionConfig.weak()
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new CheeseLavaTubeCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.CHEESE_CAVES);
    }
}