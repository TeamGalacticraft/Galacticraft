/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.world.gen.carver.config.CraterCarverConfig;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class GalacticraftConfiguredCarver {
    public static final RegistryEntry<ConfiguredCarver<?>> MOON_CANYON_CARVER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_CANYON_CARVER),
            Carver.RAVINE.configure(new RavineCarverConfig(
                    0.05f,
                    UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)),
                    ConstantFloatProvider.create(3.0f),
                    YOffset.aboveBottom(8),
                    CarverDebugConfig.DEFAULT,
                    Registry.BLOCK.getOrCreateEntryList(GalacticraftTag.MOON_CARVER_REPLACEABLES),
                    UniformFloatProvider.create(-0.125f, 0.125f),
                    new RavineCarverConfig.Shape(
                            UniformFloatProvider.create(0.75f, 1.0f),
                            TrapezoidFloatProvider.create(0, 6, 2),
                            3,
                            UniformFloatProvider.create(0.75f, 1.0f),
                            1.0f,
                            0.0f)
            )));

    public static final RegistryEntry<ConfiguredCarver<?>> MOON_CRATER_CARVER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_CRATER_CARVER),
            GalacticraftCarver.CRATERS.configure(new CraterCarverConfig(
                    0.05f,
                    ConstantHeightProvider.create(YOffset.fixed(128)),
                    UniformFloatProvider.create(0.4f, 0.6f),
                    CarverDebugConfig.DEFAULT,
                    27,
                    8,
                    8
            )));

    public static final RegistryEntry<ConfiguredCarver<?>> MOON_HIGHLANDS_CAVE_CARVER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_HIGHLANDS_CAVE_CARVER),
            GalacticraftCarver.LUNAR_CAVE.configure(new CaveCarverConfig(
                    0.15f,
                    UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)),
                    UniformFloatProvider.create(0.1f, 0.9f),
                    YOffset.aboveBottom(-64),
                    RegistryEntryList.of(),
                    UniformFloatProvider.create(0.7f, 1.4f),
                    UniformFloatProvider.create(0.8f, 1.3f),
                    UniformFloatProvider.create(-1.0f, -0.4f)
            )));

    public static final RegistryEntry<ConfiguredCarver<?>> MOON_MARE_CAVE_CARVER = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, Constant.id(Constant.Carver.MOON_MARE_CAVE_CARVER),
            GalacticraftCarver.LUNAR_CAVE.configure(new CaveCarverConfig(0.18f,
                    UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)),
                    UniformFloatProvider.create(0.1f, 0.9f),
                    YOffset.aboveBottom(-64),
                    RegistryEntryList.of(),
                    UniformFloatProvider.create(0.7f, 1.4f),
                    UniformFloatProvider.create(0.8f, 1.3f),
                    UniformFloatProvider.create(-1.0f, -0.4f)
            )));

    public static void register() {
    }
}
