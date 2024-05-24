/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.Optional;

public class GCStructureSets {
    public static final class Moon {
        public static final ResourceKey<StructureSet> PILLAGER_BASE = ResourceKey.create(Registries.STRUCTURE_SET, Constant.id("moon_pillager_bases"));
        public static final ResourceKey<StructureSet> RUINS = ResourceKey.create(Registries.STRUCTURE_SET, Constant.id("moon_ruins"));
        public static final ResourceKey<StructureSet> BOSS = ResourceKey.create(Registries.STRUCTURE_SET, Constant.id("moon_boss"));
    }

    public static void bootstrapRegistries(BootstapContext<StructureSet> context) {
        HolderGetter<Structure> structureLookup = context.lookup(Registries.STRUCTURE);
        HolderGetter<StructureSet> structureSetLookup = context.lookup(Registries.STRUCTURE_SET);

        context.register(Moon.BOSS, new StructureSet(
                structureLookup.getOrThrow(GCStructures.Moon.BOSS),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 52532672)
        ));
        context.register(Moon.RUINS,  new StructureSet(
                structureLookup.getOrThrow(GCStructures.Moon.RUINS),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 38245864)
        ));
        context.register(Moon.PILLAGER_BASE, new StructureSet(structureLookup.getOrThrow(GCStructures.Moon.PILLAGER_BASE), new RandomSpreadStructurePlacement(
                Vec3i.ZERO,
                StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1,
                0.1F,
                5927643,
                Optional.of(new StructurePlacement.ExclusionZone(structureSetLookup.getOrThrow(BuiltinStructureSets.VILLAGES), 10)),
                32,
                8,
                RandomSpreadType.LINEAR
        )));
    }
}
