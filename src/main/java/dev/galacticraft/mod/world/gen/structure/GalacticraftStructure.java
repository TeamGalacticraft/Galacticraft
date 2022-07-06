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
import dev.galacticraft.mod.tag.GalacticraftTag;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structures;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftStructure {
    public static final RegistryEntry<Structure> MOON_RUINS = BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, GalacticraftStructureKeys.MOON_RUINS, new MoonRuinsStructure(Structures.createConfig(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN)));

    public static final RegistryEntry<Structure> MOON_PILLAGER_BASE = BuiltinRegistries.add(BuiltinRegistries.STRUCTURE,
            GalacticraftStructureKeys.MOON_PILLAGER_BASE,
            new JigsawStructure(
                    new Structure.Config(BuiltinRegistries.BIOME.getOrCreateEntryList(GalacticraftTag.MOON_PILLAGER_BASE_HAS_STRUCTURE), Map.of(SpawnGroup.MONSTER,
                            new StructureSpawns(StructureSpawns.BoundingBox.STRUCTURE,
                                    Pool.of(new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_PILLAGER, 1, 1, 1))
                            )),
                            GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.BEARD_THIN),
                    MoonPillagerOutpostGenerator.ENTERANCE_POOL,
                    7,
                    ConstantHeightProvider.create(YOffset.fixed(0)),
                    true,
                    Heightmap.Type.WORLD_SURFACE_WG
            ));
    public static final RegistryEntry<Structure> MOON_VILLAGE_HIGHLANDS = BuiltinRegistries.add(BuiltinRegistries.STRUCTURE, GalacticraftStructureKeys.MOON_VILLAGE_HIGHLANDS,
            new JigsawStructure(
            new Structure.Config(BuiltinRegistries.BIOME.getOrCreateEntryList(GalacticraftTag.MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE),
                    Collections.emptyMap(),
                    GenerationStep.Feature.SURFACE_STRUCTURES,
                    StructureTerrainAdaptation.BEARD_THIN),
                    MoonVillageGenerator.START_POOL,
                    6,
                    ConstantHeightProvider.create(YOffset.fixed(0)),
                    true,
                    Heightmap.Type.WORLD_SURFACE_WG
            )
    );

    public static void register() {
    }
}
