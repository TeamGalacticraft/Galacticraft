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

package dev.galacticraft.mod.world.gen.structure;

import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.structure.MoonPillagerOutpostGenerator;
import dev.galacticraft.mod.structure.MoonVillageGenerator;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCStructure {
    public static final Holder<Structure> MOON_RUINS = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, GCStructureKeys.MOON_RUINS, new MoonRuinsStructure(Structures.structure(GCTags.MOON_RUINS_HAS_STRUCTURE, TerrainAdjustment.BEARD_THIN)));

    public static final Holder<Structure> MOON_PILLAGER_BASE = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES,
            GCStructureKeys.MOON_PILLAGER_BASE,
            new JigsawStructure(
                    new Structure.StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(GCTags.MOON_PILLAGER_BASE_HAS_STRUCTURE), Map.of(MobCategory.MONSTER,
                            new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                    WeightedRandomList.create(new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_PILLAGER, 1, 1, 1))
                            )),
                            GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
                    MoonPillagerOutpostGenerator.ENTERANCE_POOL,
                    7,
                    ConstantHeight.of(VerticalAnchor.absolute(0)),
                    true,
                    Heightmap.Types.WORLD_SURFACE_WG
            ));
    public static final Holder<Structure> MOON_VILLAGE_HIGHLANDS = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, GCStructureKeys.MOON_VILLAGE_HIGHLANDS,
            new JigsawStructure(
            new Structure.StructureSettings(BuiltinRegistries.BIOME.getOrCreateTag(GCTags.MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE),
                    Collections.emptyMap(),
                    GenerationStep.Decoration.SURFACE_STRUCTURES,
                    TerrainAdjustment.BEARD_THIN),
                    MoonVillageGenerator.START_POOL,
                    6,
                    ConstantHeight.of(VerticalAnchor.absolute(0)),
                    true,
                    Heightmap.Types.WORLD_SURFACE_WG
            )
    );

    public static void register() {
    }
}
