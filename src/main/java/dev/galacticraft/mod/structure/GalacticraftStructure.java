/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.feature.MoonPillagerBaseFeature;
import dev.galacticraft.mod.world.gen.feature.MoonRuinsFeature;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftStructure {
    private static final Codec<StructurePoolFeatureConfig> SPFC_CODEC_UNCAPPED = RecordCodecBuilder.create((instance) -> instance.group(
            StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(StructurePoolFeatureConfig::getStartPool),
            Codec.INT.fieldOf("size").forGetter(StructurePoolFeatureConfig::getSize)
    ).apply(instance, StructurePoolFeatureConfig::new));

    public static final StructurePieceType MOON_RUINS_PIECE = MoonRuinsGenerator.Piece::new;

    public static final MoonPillagerBaseFeature MOON_PILLAGER_BASE_FEATURE = new MoonPillagerBaseFeature(SPFC_CODEC_UNCAPPED);
    public static final MoonRuinsFeature MOON_RUINS = new MoonRuinsFeature(DefaultFeatureConfig.CODEC);


    public static void register() {
        Registry.register(Registry.STRUCTURE_PIECE, new Identifier(Constant.MOD_ID, "moon_ruins_piece"), MOON_RUINS_PIECE);

        FabricStructureBuilder.create(new Identifier(Constant.MOD_ID, "moon_pillager_base"), MOON_PILLAGER_BASE_FEATURE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(32, 16, 23789482).adjustsSurface()
                .register();
        FabricStructureBuilder.create(new Identifier(Constant.MOD_ID, "moon_ruins"), MOON_RUINS)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(24, 8, 1903453)
                .register();
    }
}
