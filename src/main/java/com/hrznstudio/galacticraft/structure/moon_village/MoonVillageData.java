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
 */

package com.hrznstudio.galacticraft.structure.moon_village;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonVillageData {

    public static final StructurePool BASE_POOL;

    private static final String BASE_ID = Constants.MOD_ID + ":moon_village/";

    private static final Identifier STREETS = new Identifier(Constants.MOD_ID, "moon_village/streets");
    private static final Identifier TERMINATORS = new Identifier(Constants.MOD_ID, "moon_village/terminators");
    private static final Identifier HOUSES = new Identifier(Constants.MOD_ID, "moon_village/houses");
    private static final Identifier TREES = new Identifier(Constants.MOD_ID, "moon_village/trees");
    private static final Identifier DECOR = new Identifier(Constants.MOD_ID, "moon_village/decor");
    private static final Identifier VILLAGERS = new Identifier(Constants.MOD_ID, "moon_village/villagers");
    private static final Identifier ANIMALS = new Identifier(Constants.MOD_ID, "moon_village/animals");
    private static final Identifier IRON_GOLEM = new Identifier(Constants.MOD_ID, "moon_village/iron_golem");
    private static final Identifier SAPLINGS = new Identifier(Constants.MOD_ID, "moon_village/misc/saplings");

    public static final Identifier STARTS = new Identifier(Constants.MOD_ID, "moon_village/starts");

    static {
        BASE_POOL = StructurePools.register(new StructurePool(STARTS,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "starts/start_1"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "starts/start_2"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "starts/start_3"), 5),
                        Pair.of((projection) -> EmptyPoolElement.INSTANCE, 1) //todo
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(STREETS,
                TERMINATORS,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/corner_01"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/corner_02"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/corner_03"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_01"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_02"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_03"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_04"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_05"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_06"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_01"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_02"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_03"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_04"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_05"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_06"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/turn_01"), 3)
                ),
                StructurePool.Projection.TERRAIN_MATCHING));

        StructurePools.register(new StructurePool(HOUSES,
                TERMINATORS,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/house_1"), 5),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/greenhouse"), 1),
                        Pair.of((projection) -> EmptyPoolElement.INSTANCE, 1)
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(TERMINATORS,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_01"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_02"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_03"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_04"), 1)
                ),
                StructurePool.Projection.TERRAIN_MATCHING));

        StructurePools.register(new StructurePool(TREES,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of((projection) -> EmptyPoolElement.INSTANCE, 10) //todo
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(SAPLINGS,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/oak"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/birch"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/spruce"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/dark_oak"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/jungle"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/acacia"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "misc/saplings/dead"), 2)
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(DECOR,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "lamp"), 2),
                        Pair.of(StructurePoolElement.method_30421(Feature.BLOCK_PILE.configure(GalacticraftFeatures.CHEESE_LOG_PILE_CONFIG)), 1),
                        Pair.of(StructurePoolElement.method_30438(), 2)
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(VILLAGERS,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/nitwit"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/baby"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/unemployed"), 10)
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(ANIMALS,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of((projection) -> EmptyPoolElement.INSTANCE, 5) //todo
                ),
                StructurePool.Projection.RIGID));

        StructurePools.register(new StructurePool(IRON_GOLEM,
                Constants.Misc.EMPTY,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "iron_golem"), 1)
                ),
                StructurePool.Projection.RIGID));
    }

    public static void register() {

    }
}
