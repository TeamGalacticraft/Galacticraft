/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.structure;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.gen.feature.MoonPillagerBaseFeature;
import com.hrznstudio.galacticraft.world.gen.feature.MoonRuinsFeature;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class GalacticraftStructures {
    public static final Codec<JigsawConfiguration> STRUCTURE_POOL_CONFIG_CODEC_UNCAPPED_SIZE = RecordCodecBuilder.create((instance) -> instance.group(StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawConfiguration::startPool), Codec.INT.fieldOf("size").forGetter(JigsawConfiguration::maxDepth)).apply(instance, JigsawConfiguration::new));

    public static final MoonPillagerBaseFeature MOON_PILLAGER_BASE_FEATURE = new MoonPillagerBaseFeature(STRUCTURE_POOL_CONFIG_CODEC_UNCAPPED_SIZE);
    public static final MoonRuinsFeature MOON_RUINS = new MoonRuinsFeature(NoneFeatureConfiguration.CODEC);

    public static final StructurePieceType MOON_RUINS_PIECE = StructurePieceType.setPieceId(MoonRuinsGenerator.Piece::new, Constants.MOD_ID + ":moon_ruins_piece");

    public static void register() {
        FabricStructureBuilder.create(new ResourceLocation(Constants.MOD_ID, "moon_pillager_base"), MOON_PILLAGER_BASE_FEATURE)
                .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
                .defaultConfig(32, 16, 23789482).adjustsSurface()
                .register();

        FabricStructureBuilder.create(new ResourceLocation(Constants.MOD_ID, "moon_ruins"), MOON_RUINS)
                .step(GenerationStep.Decoration.SURFACE_STRUCTURES)
                .defaultConfig(24, 8, 1903453)
                .register();
    }
}
