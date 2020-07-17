/*
 * Copyright (c) 2020 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.stateprovider.MoonFloraBlockStateProvider;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFeatures {
    public static final StructureFeature<StructurePoolFeatureConfig> MOON_VILLAGE = StructureFeature.register(new Identifier(Constants.MOD_ID, "moon_village").toString(), new MoonVillageFeature(StructurePoolFeatureConfig.CODEC), GenerationStep.Feature.SURFACE_STRUCTURES);

    public static final RandomPatchFeatureConfig MOON_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(new MoonFloraBlockStateProvider(), new SimpleBlockPlacer()).tries(64).build();
    public static final BlockPileFeatureConfig CHEESE_LOG_PILE_CONFIG = new BlockPileFeatureConfig(new PillarBlockStateProvider(GalacticraftBlocks.MOON_CHEESE_LOG));

    public static void register() {
        for (Biome biome : Biome.BIOMES) {
            if (!biome.getCategory().equals(Biome.Category.NETHER) && !biome.getCategory().equals(Biome.Category.THEEND)) {
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(new RuleTest() {
                    @Override
                    public boolean test(BlockState state, Random random) {
                        return state.isIn(BlockTags.BASE_STONE_OVERWORLD);
                    }

                    @Override
                    protected RuleTestType<?> getType() {
                        return RuleTestType.TAG_MATCH;
                    }
                }, GalacticraftBlocks.SILICON_ORE.getDefaultState(), 5)));
            }
        }
    }
}
