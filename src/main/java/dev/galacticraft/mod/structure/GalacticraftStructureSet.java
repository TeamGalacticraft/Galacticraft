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

package dev.galacticraft.mod.structure;

import dev.galacticraft.mod.world.gen.structure.GalacticraftStructure;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSets;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;

import java.util.List;
import java.util.Optional;

public class GalacticraftStructureSet {
    public static final RegistryEntry<StructureSet> MOON_PILLAGER_BASES = StructureSets.register(
            GalacticraftStructureSetKeys.MOON_PILLAGER_BASES,
            GalacticraftStructure.MOON_PILLAGER_BASE,
            new RandomSpreadStructurePlacement(
                    Vec3i.ZERO,
                    StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1,
                    0.1F,
                    5927643,
                    Optional.of(new StructurePlacement.ExclusionZone(StructureSets.VILLAGES, 10)),
                    32,
                    8,
                    SpreadType.LINEAR
            )
    );
    public static final RegistryEntry<StructureSet> MOON_RUINS = StructureSets.register(
            GalacticraftStructureSetKeys.MOON_RUINS,
            new StructureSet(
                    List.of(StructureSet.createEntry(GalacticraftStructure.MOON_RUINS)),
                    new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 38245864)
            )
    );

    public static void register() {
        MoonPillagerOutpostGenerator.register();
        MoonVillageGenerator.register();
    }
}
