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

package com.hrznstudio.galacticraft.world.gen.decorator;

import com.hrznstudio.galacticraft.world.gen.feature.CraterFeature;
import com.hrznstudio.galacticraft.world.gen.feature.CraterFeatureConfig;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftDecorators {
    public static final Decorator<CraterDecoratorConfig> CRATER = Registry.register(Registry.DECORATOR, "oil_lake", new CraterDecorator(CraterDecoratorConfig::deserialize));
    public static final ConfiguredFeature<CraterFeatureConfig, CraterFeature> CRATER_CONF = new ConfiguredFeature<>((CraterFeature) GalacticraftFeatures.CRATER_FEATURE, new CraterFeatureConfig());

    public static void init() {
    }
}
