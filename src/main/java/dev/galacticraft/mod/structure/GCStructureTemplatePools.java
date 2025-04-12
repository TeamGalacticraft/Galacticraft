/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.function.Function;

public final class GCStructureTemplatePools {
    private static final ResourceKey<StructureTemplatePool> EMPTY = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("empty"));

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
            public static final ResourceKey<StructureTemplatePool> DECOR = key("village/moon/highlands/decor");
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

    public static void bootstrapRegistries(BootstrapContext<StructureTemplatePool> context) {
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
        context.register(Moon.PillagerOutpost.BASE_TERMINATORS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_base_1"), emptyList), 3),
                        Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_base_2"), emptyList), 4),
                        Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_base_3"), emptyList), 4)
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

        context.register(Moon.Village.STARTS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/starts/start_1"), emptyList), 3),
                        Pair.of(single(Constant.id("village/moon/highlands/starts/start_2"), emptyList), 4),
                        Pair.of(single(Constant.id("village/moon/highlands/starts/start_3"), emptyList), 5),
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));

        context.register(Moon.Village.ANIMALS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));
        context.register(Moon.Village.DECOR, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/decor/lamp"), emptyList), 2),
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));
        context.register(Moon.Village.HOUSES, new StructureTemplatePool(
                templateLookup.getOrThrow(Moon.Village.TERMINATORS),
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/houses/house_1"), emptyList), 4),
                        Pair.of(single(Constant.id("village/moon/highlands/houses/greenhouse"), emptyList), 2),
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));
        context.register(Moon.Village.IRON_GOLEM, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));
        context.register(Moon.Village.SAPLINGS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/oak"), emptyList), 3),
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/birch"), emptyList), 3),
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/spruce"), emptyList), 3),
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/dark_oak"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/jungle"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/acacia"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/misc/saplings/dead"), emptyList), 10)
                ),
                Projection.RIGID
        ));
        context.register(Moon.Village.STREETS, new StructureTemplatePool(
                templateLookup.getOrThrow(Moon.Village.TERMINATORS),
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/streets/corner_01"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/corner_02"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/corner_03"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/straight_01"), emptyList), 4),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/straight_02"), emptyList), 4),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/straight_03"), emptyList), 7),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/straight_04"), emptyList), 7),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/straight_05"), emptyList), 3),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/straight_06"), emptyList), 4),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_01"), emptyList), 1),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_02"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_03"), emptyList), 1),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_04"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_05"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/crossroad_06"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/streets/turn_01"), emptyList), 3),
                        Pair.of(StructurePoolElement.empty(), 3)
                ),
                Projection.TERRAIN_MATCHING
        ));
        context.register(Moon.Village.TERMINATORS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_01"), emptyList), 1),
                        Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_02"), emptyList), 1),
                        Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_03"), emptyList), 1),
                        Pair.of(single(Constant.id("village/moon/highlands/terminators/terminator_04"), emptyList), 1)
                ),
                Projection.TERRAIN_MATCHING
        ));
        context.register(Moon.Village.TREES, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.empty(), 1)
                ),
                Projection.RIGID
        ));
        context.register(Moon.Village.VILLAGERS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(single(Constant.id("village/moon/highlands/villagers/nitwit"), emptyList), 2),
                        Pair.of(single(Constant.id("village/moon/highlands/villagers/baby"), emptyList), 1),
                        Pair.of(single(Constant.id("village/moon/highlands/villagers/unemployed"), emptyList), 10)
                ),
                Projection.RIGID
        ));
    }

    public static Function<Projection, LegacySinglePoolElement> single(ResourceLocation id, Holder<StructureProcessorList> list) { // Legacy means that air CAN be replaced by worldgen.
        return StructurePoolElement.legacy(id.toString(), list);
    }
}
