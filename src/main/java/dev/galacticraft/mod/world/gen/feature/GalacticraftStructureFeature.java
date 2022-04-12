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

package dev.galacticraft.mod.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.structure.MoonPillagerOutpostGenerator;
import dev.galacticraft.mod.structure.MoonVillageGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class GalacticraftStructureFeature {
    private static final Codec<StructurePoolFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool),
            Codec.INT.fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)
    ).apply(instance, StructurePoolFeatureConfig::new));

    public static final MoonPillagerOutpostFeature MOON_PILLAGER_OUTPOST = new MoonPillagerOutpostFeature(CODEC);
    public static final MoonRuinsFeature MOON_RUINS = new MoonRuinsFeature(DefaultFeatureConfig.CODEC);

    public static void register() {
        MoonPillagerOutpostGenerator.register();
        MoonVillageGenerator.register();

        FabricStructureBuilder.create(new Identifier(Constant.MOD_ID, "moon_pillager_outpost"), GalacticraftStructureFeature.MOON_PILLAGER_OUTPOST)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(32, 16, 23789482)
                .adjustsSurface()
                .register();
        FabricStructureBuilder.create(new Identifier(Constant.MOD_ID, "moon_ruins"), GalacticraftStructureFeature.MOON_RUINS)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(24, 8, 1903453)
                .register();
    }
}
