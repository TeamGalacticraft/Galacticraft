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
import com.hrznstudio.galacticraft.world.gen.feature.cheesetree.CheeseTreeFeature;
import com.hrznstudio.galacticraft.world.gen.feature.moonvillage.MoonVillageFeature;
import com.hrznstudio.galacticraft.world.gen.stateprovider.MoonFlowerBlockStateProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFeatures {
    public static final CheeseTreeFeature CHEESE_TREE_FEATURE = Registry.register(Registry.FEATURE, new Identifier(Constants.MOD_ID, "cheese_tree"), new CheeseTreeFeature(BranchedTreeFeatureConfig::deserialize));
    public static final StructureFeature<DefaultFeatureConfig> MOON_VILLAGE = Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(Constants.MOD_ID, "moon_village"), Registry.register(Registry.FEATURE, new Identifier(Constants.MOD_ID, "moon_village"), new MoonVillageFeature(DefaultFeatureConfig::deserialize)));
    public static final RandomPatchFeatureConfig MOON_FLOWER_CONFIG = (new RandomPatchFeatureConfig.Builder(new MoonFlowerBlockStateProvider(), new SimpleBlockPlacer())).tries(64).build();

    public static void init() {
        Feature.STRUCTURES.forcePut(Registry.STRUCTURE_FEATURE.getId(MOON_VILLAGE).toString(), MOON_VILLAGE);
        Feature.JIGSAW_STRUCTURES.add(MOON_VILLAGE);
    }
}
