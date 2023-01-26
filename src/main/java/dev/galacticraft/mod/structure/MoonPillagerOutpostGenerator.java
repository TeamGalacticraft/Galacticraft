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
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;

public class MoonPillagerOutpostGenerator {
    public static final Holder<StructureTemplatePool> ENTERANCE_POOL = Pools.register(new StructureTemplatePool(
            Constant.id("moon_pillager_outpost/entrances"),
            Constant.Misc.EMPTY,
            ImmutableList.of(
                    Pair.of(single(Constant.id("moon_pillager_outpost/entrance/entrance_1")), 5),
                    Pair.of(StructurePoolElement.empty(), 1)
            ),
            Projection.RIGID
    ));

    public static void register() {
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/base"),
                        Constant.id("moon_pillager_outpost/base_terminators"),
                        ImmutableList.of(
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/base_1")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/base_2s")), 4),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/base_2t")), 5),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/base_3")), 2),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/base_4")), 1),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_1")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_2s")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_2t")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_3")), 2),
                                Pair.of(single(Constant.id("moon_pillager_outpost/base/small_base_4")), 1)
                        ),
                        Projection.RIGID
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/base_terminators"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_base_1")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_base_2")), 4),
                                Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_base_3")), 4)
                        ),
                        Projection.RIGID
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/connector"),
                        Constant.id("moon_pillager_outpost/connector_terminators"),
                        ImmutableList.of(
                                Pair.of(single(Constant.id("moon_pillager_outpost/connector/connector_1")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/connector/connector_2")), 4),
                                Pair.of(single(Constant.id("moon_pillager_outpost/connector/connector_3")), 3)
                        ),
                        Projection.TERRAIN_MATCHING
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/connector_terminators"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_connector_1")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_connector_2")), 4),
                                Pair.of(single(Constant.id("moon_pillager_outpost/terminator/terminator_connector_3")), 3)
                        ),
                        Projection.RIGID
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/any_3x3x3"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/cage")), 1),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/pillagers")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/cake")), 1),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional_3x3x3/seating")), 4),
                                Pair.of(StructurePoolElement.empty(), 2)
                        ),
                        Projection.RIGID
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/machine_3x1"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.empty(), 2)
                        ),
                        Projection.RIGID
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/optional_3x3"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/decentloot_1")), 1),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/decentloot_2")), 1),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/decentloot_3")), 1),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/basicloot_1")), 2),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/basicloot_2")), 2),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/cartography")), 3),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/desk")), 4),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/exam_tab")), 2),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/computer")), 2),
                                Pair.of(single(Constant.id("moon_pillager_outpost/optional/seat")), 4),
                                Pair.of(StructurePoolElement.empty(), 3)
                        ),
                        Projection.RIGID
                )
        );
        Pools.register(
                new StructureTemplatePool(
                        Constant.id("moon_pillager_outpost/pillagers"),
                        Constant.Misc.EMPTY,
                        ImmutableList.of(
                                Pair.of(StructurePoolElement.empty(), 1)
                        ),
                        Projection.RIGID
                )
        );
    }
    
    public static Function<Projection, LegacySinglePoolElement> single(ResourceLocation id) { // Legacy means that air CAN be replaced by worldgen.
        return projection -> new LegacySinglePoolElement(Either.left(id), ProcessorLists.EMPTY, projection);
    }
}
