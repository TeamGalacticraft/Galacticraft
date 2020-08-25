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

package com.hrznstudio.galacticraft.structure;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.structure.moon_village.MoonVillageData;
import com.hrznstudio.galacticraft.world.gen.feature.MoonRuinsFeature;
import com.hrznstudio.galacticraft.world.gen.feature.MoonVillageFeature;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class GalacticraftStructures {
    public static final MoonVillageFeature MOON_VILLAGE = new MoonVillageFeature(StructurePoolFeatureConfig.CODEC);
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, MoonVillageFeature> CONFIGURED_MOON_VILLAGE = new ConfiguredStructureFeature<>(MOON_VILLAGE, new StructurePoolFeatureConfig(() -> MoonVillageData.BASE_POOL, 6));

    public static final MoonRuinsFeature MOON_RUINS = new MoonRuinsFeature(DefaultFeatureConfig.CODEC);
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, MoonRuinsFeature> CONFIGURED_MOON_RUINS = new ConfiguredStructureFeature<>(MOON_RUINS, DefaultFeatureConfig.INSTANCE);
    public static final StructurePieceType MOON_RUINS_PIECE = StructurePieceType.register(MoonRuinsGenerator.Piece::new, "galacticraft-rewoven:moon_ruins_piece");

    public static void register() {
        FabricStructureBuilder.create(new Identifier(Constants.MOD_ID, "moon_village"), MOON_VILLAGE)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(32, 8, 1278983)
                .superflatFeature(CONFIGURED_MOON_VILLAGE)
                .register();

        FabricStructureBuilder.create(new Identifier(Constants.MOD_ID, "moon_ruins"), MOON_RUINS)
                .step(GenerationStep.Feature.SURFACE_STRUCTURES)
                .defaultConfig(24, 8, 1903453)
                .superflatFeature(CONFIGURED_MOON_RUINS)
                .register();
    }
}
