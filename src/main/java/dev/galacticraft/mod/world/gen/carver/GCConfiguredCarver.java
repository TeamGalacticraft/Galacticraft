/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.carver;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.GCTags;
import dev.galacticraft.mod.world.gen.carver.config.CraterCarverConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class GCConfiguredCarver {
    public static final Holder<ConfiguredWorldCarver<?>> MOON_CANYON_CARVER = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_CANYON_CARVER),
            WorldCarver.CANYON.configured(new CanyonCarverConfiguration(
                    0.05f,
                    UniformHeight.of(VerticalAnchor.absolute(10), VerticalAnchor.absolute(67)),
                    ConstantFloat.of(3.0f),
                    VerticalAnchor.aboveBottom(8),
                    CarverDebugSettings.DEFAULT,
                    Registry.BLOCK.getOrCreateTag(GCTags.MOON_CARVER_REPLACEABLES),
                    UniformFloat.of(-0.125f, 0.125f),
                    new CanyonCarverConfiguration.CanyonShapeConfiguration(
                            UniformFloat.of(0.75f, 1.0f),
                            TrapezoidFloat.of(0, 6, 2),
                            3,
                            UniformFloat.of(0.75f, 1.0f),
                            1.0f,
                            0.0f)
            )));

    public static final Holder<ConfiguredWorldCarver<?>> MOON_CRATER_CARVER = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_CRATER_CARVER),
            GCCarver.CRATERS.configured(new CraterCarverConfig(
                    0.05f,
                    ConstantHeight.of(VerticalAnchor.absolute(128)),
                    UniformFloat.of(0.4f, 0.6f),
                    CarverDebugSettings.DEFAULT,
                    27,
                    8,
                    8
            )));

    public static final Holder<ConfiguredWorldCarver<?>> MOON_HIGHLANDS_CAVE_CARVER = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_HIGHLANDS_CAVE_CARVER),
            GCCarver.LUNAR_CAVE.configured(new CaveCarverConfiguration(
                    0.15f,
                    UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                    UniformFloat.of(0.1f, 0.9f),
                    VerticalAnchor.aboveBottom(-64),
                    HolderSet.direct(),
                    UniformFloat.of(0.7f, 1.4f),
                    UniformFloat.of(0.8f, 1.3f),
                    UniformFloat.of(-1.0f, -0.4f)
            )));

    public static final Holder<ConfiguredWorldCarver<?>> MOON_MARE_CAVE_CARVER = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_MARE_CAVE_CARVER),
            GCCarver.LUNAR_CAVE.configured(new CaveCarverConfiguration(
                    0.18f,
                    UniformHeight.of(VerticalAnchor.aboveBottom(8), VerticalAnchor.absolute(180)),
                    UniformFloat.of(0.1f, 0.9f),
                    VerticalAnchor.aboveBottom(-64),
                    HolderSet.direct(),
                    UniformFloat.of(0.7f, 1.4f),
                    UniformFloat.of(0.8f, 1.3f),
                    UniformFloat.of(-1.0f, -0.4f)
            )));

    public static void register() {
    }
}
