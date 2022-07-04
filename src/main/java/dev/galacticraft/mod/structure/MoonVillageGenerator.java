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

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import net.minecraft.structure.pool.LegacySinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePool.Projection;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

import java.util.function.Function;

public class MoonVillageGenerator {
    public static final RegistryEntry<StructurePool> START_POOL = StructurePools.register(new StructurePool(
            Constant.id("village/moon/highlands/starts"),
            Constant.Misc.EMPTY,
            ImmutableList.of(
                    Pair.of(single(Constant.id("village/moon/highlands/starts/start_1")), 3),
                    Pair.of(single(Constant.id("village/moon/highlands/starts/start_2")), 4),
                    Pair.of(single(Constant.id("village/moon/highlands/starts/start_3")), 5),
                    Pair.of(StructurePoolElement.ofEmpty(), 1)
            ),
            Projection.RIGID
    ));

    public static void register() {
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/animals"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.Misc.EMPTY), 1)
                        ),
                        Projection.RIGID
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/decor"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("village/moon/highlands/decor/lamp")), 2),
                                Pair.of(single(Constant.Misc.EMPTY), 1)
                        ),
                        Projection.RIGID
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/houses"),
                        Constant.id("village/moon/highlands/terminators"),
                        ImmutableList.of(
                                Pair.of(single(Constant.id("village/moon/highlands/houses/house_1")), 4),
                                Pair.of(single(Constant.id("village/moon/highlands/houses/greenhouse")), 2),
                                Pair.of(single(Constant.Misc.EMPTY), 1)
                        ),
                        Projection.RIGID
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/iron_golem"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.Misc.EMPTY), 1)
                        ),
                        Projection.RIGID
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/saplings"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/oak")), 3),
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/birch")), 3),
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/spruce")), 3),
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/dark_oak")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/jungle")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/acacia")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/dead")), 10)
                        ),
                        Projection.RIGID
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/streets"),
                        Constant.id("village/moon/highlands/terminators"),
                        ImmutableList.of(
                                Pair.of(single(Constant.id("village/moon/highlands/streets/corner_01")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/corner_02")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/corner_03")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_01")), 4),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_02")), 4),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_03")), 7),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_04")), 7),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_05")), 3),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_06")), 4),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_01")), 1),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_02")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_03")), 1),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_04")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_05")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_06")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/streets/turn_01")), 3),
                                Pair.of(StructurePoolElement.ofEmpty(), 3)
                        ),
                        Projection.TERRAIN_MATCHING
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/terminators"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_01")), 1),
                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_02")), 1),
                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_03")), 1),
                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_04")), 1)
                        ),
                        Projection.TERRAIN_MATCHING
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/trees"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.ofEmpty(), 1)
                        ),
                        Projection.RIGID
                )
        );
        StructurePools.register(
                new StructurePool(
                        Constant.id("village/moon/highlands/villagers"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("village/moon/highlands/villagers/nitwit")), 2),
                                Pair.of(single(Constant.id("village/moon/highlands/villagers/baby")), 1),
                                Pair.of(single(Constant.id("village/moon/highlands/villagers/unemployed")), 10)
                        ),
                        Projection.RIGID
                )
        );
    }
    
    public static Function<Projection, LegacySinglePoolElement> single(Identifier id) { // Legacy means that air CAN be replaced by worldgen.
        return projection -> new LegacySinglePoolElement(Either.left(id), StructureProcessorLists.EMPTY, projection);
    }
}
