/*
 * Copyright (c) 2019 HRZN LTD
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
 */

package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.moonvillage.MoonVillageFeature;
import com.hrznstudio.galacticraft.world.gen.stateprovider.MoonFlowerBlockStateProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFeatures {
    public static final TreeFeatureConfig CHEESE_TREE_CONFIG = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(GalacticraftBlocks.CHEESE_LOG.getDefaultState()), new SimpleBlockStateProvider(GalacticraftBlocks.CHEESE_LEAVES.getDefaultState()), new AcaciaFoliagePlacer(2, 0, 0, 0), new ForkingTrunkPlacer(5, 2, 2), new TwoLayersFeatureSize(1, 0, 2))).method_27374().build();
    public static final ConfiguredFeature<TreeFeatureConfig, ?> CHEESE_TREE_FEATURE = Feature.TREE.configure(CHEESE_TREE_CONFIG);
    //    public static final CheeseTreeFeature CHEESE_TREE_FEATURE = Registry.register(Registry.FEATURE, new Identifier(Constants.MOD_ID, "cheese_tree"), new CheeseTreeFeature(BranchedTreeFeatureConfig::deserialize));
    public static final StructureFeature<DefaultFeatureConfig> MOON_VILLAGE = Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(Constants.MOD_ID, "moon_village"), Registry.register(Registry.FEATURE, new Identifier(Constants.MOD_ID, "moon_village"), new MoonVillageFeature(DefaultFeatureConfig::deserialize)));
    public static final RandomPatchFeatureConfig MOON_FLOWER_CONFIG = (new RandomPatchFeatureConfig.Builder(new MoonFlowerBlockStateProvider(), new SimpleBlockPlacer())).tries(64).build();
    public static final BlockPileFeatureConfig CHEESE_LOG_PILE_CONFIG = new BlockPileFeatureConfig(new PillarBlockStateProvider(GalacticraftBlocks.CHEESE_LOG));

    public static void register() {
        Feature.STRUCTURES.forcePut("Moon_Village", MOON_VILLAGE);
        Feature.JIGSAW_STRUCTURES.add(MOON_VILLAGE);
    }
}
