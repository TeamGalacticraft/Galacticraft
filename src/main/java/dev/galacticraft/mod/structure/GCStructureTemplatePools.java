/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import java.util.function.Function;

import dev.galacticraft.mod.data.content.GeneratingBootstrapContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public final class GCStructureTemplatePools {
    private static final ResourceKey<StructureTemplatePool> EMPTY = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"));
    public static final class Moon {
        public static final class PillagerOutpost {
            public static final ResourceKey<StructureTemplatePool> ENTRANCE = key("moon_pillager_outpost/entrances");
            public static final ResourceKey<StructureTemplatePool> BASE = key("moon_pillager_outpost/base");
            public static final ResourceKey<StructureTemplatePool> BASE_TERMINATORS = key("moon_pillager_outpost/base_terminators");
            public static final ResourceKey<StructureTemplatePool> CONNECTOR = key("moon_pillager_outpost/connector");
            public static final ResourceKey<StructureTemplatePool> CONNECTOR_TERMINATORS = key("moon_pillager_outpost/connector_terminators");
            public static final ResourceKey<StructureTemplatePool> ANY_3x3x3 = key("moon_pillager_outpost/any_3x3x3");
            public static final ResourceKey<StructureTemplatePool> MACHINE_3x1x1 = key("moon_pillager_outpost/machine_3x1");
            public static final ResourceKey<StructureTemplatePool> OPTIONAL_3x3 = key("moon_pillager_outpost/optional_3x3");
            public static final ResourceKey<StructureTemplatePool> PILLAGERS = key("moon_pillager_outpost/pillagers");
        }

        public static final class Village {
            public static final ResourceKey<StructureTemplatePool> STARTS = key("village/moon/highlands/starts");
            public static final ResourceKey<StructureTemplatePool> ANIMALS = key("village/moon/highlands/animals");
            public static final ResourceKey<StructureTemplatePool> HOUSES = key("village/moon/highlands/houses");
            public static final ResourceKey<StructureTemplatePool> TERMINATORS = key("village/moon/highlands/terminators");
            public static final ResourceKey<StructureTemplatePool> IRON_GOLEM = key("village/moon/highlands/iron_golem");
            public static final ResourceKey<StructureTemplatePool> SAPLINGS = key("village/moon/highlands/saplings");
            public static final ResourceKey<StructureTemplatePool> STREETS = key("village/moon/highlands/streets");
            public static final ResourceKey<StructureTemplatePool> TREES = key("village/moon/highlands/trees");
            public static final ResourceKey<StructureTemplatePool> VILLAGERS = key("village/moon/highlands/villagers");
        }
    }

    private static ResourceKey<StructureTemplatePool> key(String id) {
        return Constant.key(Registries.TEMPLATE_POOL, id);
    }

    public static void boostrapRegistries(BootstapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> templateLookup = context.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> empty = templateLookup.getOrThrow(EMPTY);
        Holder<StructureProcessorList> emptyList = context.lookup(Registries.PROCESSOR_LIST).getOrThrow(ProcessorLists.EMPTY);

        context.register(Moon.PillagerOutpost.ENTRANCE, new StructureTemplatePool(empty, ImmutableList.of(
                    Pair.of(single(Constant.id("moon_pillager_outpost/entrance/entrance_1"), emptyList), 5),
                    Pair.of(StructurePoolElement.empty(), 1)
            ), Projection.RIGID));

        context.register(Moon.PillagerOutpost.BASE, new StructureTemplatePool(
                templateLookup.getOrThrow(Moon.PillagerOutpost.BASE_TERMINATORS),
                ImmutableList.of(
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/base_1"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/base_2s"), emptyList), 4),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/base_2t"), emptyList), 5),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/base_3"), emptyList), 2),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/base_4"), emptyList), 1),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_1"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_2s"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_2t"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_3"), emptyList), 2),
                        Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_4"), emptyList), 1)
                ),
                Projection.RIGID
        ));
        context.register(Moon.PillagerOutpost.CONNECTOR, new StructureTemplatePool(
                templateLookup.getOrThrow(Moon.PillagerOutpost.CONNECTOR_TERMINATORS),
                ImmutableList.of(
                        Pair.of(single(Constant.id("moon_pillager_outpost/connector/connector_1"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/connector/connector_2"), emptyList), 4),
                        Pair.of(single(Constant.id("moon_pillager_outpost/connector/connector_3"), emptyList), 3)
                ),
                Projection.TERRAIN_MATCHING
        ));
        context.register(Moon.PillagerOutpost.CONNECTOR_TERMINATORS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_connector_1"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_connector_2"), emptyList), 4),
                        Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_connector_3"), emptyList), 3)
                ),
                Projection.RIGID
        ));

        context.register(Moon.PillagerOutpost.ANY_3x3x3, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/cage"), emptyList), 1),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/pillagers"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/cake"), emptyList), 1),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/seating"), emptyList), 4),
                        Pair.of(StructurePoolElement.empty(), 2)
                ),
                Projection.RIGID
        ));

        context.register(Moon.PillagerOutpost.MACHINE_3x1x1, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.empty(), 2)
                ),
                Projection.RIGID
        ));

        context.register(Moon.PillagerOutpost.OPTIONAL_3x3, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/decentloot_1"), emptyList), 1),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/decentloot_2"), emptyList), 1),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/decentloot_3"), emptyList), 1),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/basicloot_1"), emptyList), 2),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/basicloot_2"), emptyList), 2),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/cartography"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/desk"), emptyList), 4),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/exam_tab"), emptyList), 2),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/computer"), emptyList), 2),
                        Pair.of(single(Constant.id("moon_pillager_outpost/optional/seat"), emptyList), 4),
                        Pair.of(StructurePoolElement.empty(), 3)
                ),
                Projection.RIGID
        ));

        context.register(Moon.PillagerOutpost.PILLAGERS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));

    }

    //    public static final Holder<StructureTemplatePool> START_POOL = Pools.register(new StructureTemplatePool(
    //            Constant.id("village/moon/highlands/starts"),
    //            Constant.Misc.EMPTY,
    //            ImmutableList.of(
    //                    Pair.of(single(Constant.id("village/moon/highlands/starts/start_1")), 3),
    //                    Pair.of(single(Constant.id("village/moon/highlands/starts/start_2")), 4),
    //                    Pair.of(single(Constant.id("village/moon/highlands/starts/start_3")), 5),
    //                    Pair.of(StructurePoolElement.empty(), 1)
    //            ),
    //            Projection.RIGID
    //    ));
    //
    //    public static void register() {
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/animals"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.Misc.EMPTY), 1)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/decor"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.id("village/moon/highlands/decor/lamp")), 2),
    //                                Pair.of(single(Constant.Misc.EMPTY), 1)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/houses"),
    //                        Constant.id("village/moon/highlands/terminators"),
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.id("village/moon/highlands/houses/house_1")), 4),
    //                                Pair.of(single(Constant.id("village/moon/highlands/houses/greenhouse")), 2),
    //                                Pair.of(single(Constant.Misc.EMPTY), 1)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/iron_golem"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.Misc.EMPTY), 1)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/saplings"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/oak")), 3),
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/birch")), 3),
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/spruce")), 3),
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/dark_oak")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/jungle")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/acacia")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/dead")), 10)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/streets"),
    //                        Constant.id("village/moon/highlands/terminators"),
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/corner_01")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/corner_02")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/corner_03")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_01")), 4),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_02")), 4),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_03")), 7),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_04")), 7),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_05")), 3),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/straight_06")), 4),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_01")), 1),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_02")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_03")), 1),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_04")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_05")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_06")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/streets/turn_01")), 3),
    //                                Pair.of(StructurePoolElement.empty(), 3)
    //                        ),
    //                        Projection.TERRAIN_MATCHING
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/terminators"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_01")), 1),
    //                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_02")), 1),
    //                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_03")), 1),
    //                                Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_04")), 1)
    //                        ),
    //                        Projection.TERRAIN_MATCHING
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/trees"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(StructurePoolElement.empty(), 1)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //        Pools.register(
    //                new StructureTemplatePool(
    //                        Constant.id("village/moon/highlands/villagers"),
    //                        Constant.Misc.EMPTY,
    //                        ImmutableList.of(
    //                                Pair.of(single(Constant.id("village/moon/highlands/villagers/nitwit")), 2),
    //                                Pair.of(single(Constant.id("village/moon/highlands/villagers/baby")), 1),
    //                                Pair.of(single(Constant.id("village/moon/highlands/villagers/unemployed")), 10)
    //                        ),
    //                        Projection.RIGID
    //                )
    //        );
    //    }

    public static Function<Projection, LegacySinglePoolElement> single(ResourceLocation id, Holder<StructureProcessorList> list) { // Legacy means that air CAN be replaced by worldgen.
        return projection -> new LegacySinglePoolElement(Either.left(id), list, projection);
    }
}
