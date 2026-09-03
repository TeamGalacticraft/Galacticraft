/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.*;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedBranchingCaveShape;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CheeseBranchingCave extends PlanetCave {
    public CheeseBranchingCave() {
        super(
                Constant.id("cheese_branching_cave"),
                MoonCaveShapeType.BRANCHING,
                new PathSolvedBranchingCaveShape(
                        4, 7,
                        2, 5,
                        5, 12,
                        4.2D, 8.5D,
                        2.0D, 6.0D,
                        2.0D, 3.8D,
                        20, 70,
                        -60, -50
                ),
                60,
                0.20F,
                -4,
                8,
                -60,
                24,
                Blocks.YELLOW_WOOL.defaultBlockState(),
                Blocks.ORANGE_WOOL.defaultBlockState(),
                Blocks.GOLD_BLOCK.defaultBlockState(),
                CaveTransitionConfig.weak(),
                java.util.List.of(),
                10
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new CheeseBranchingCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.CHEESE_CAVES);
    }

    @Override
    public BlockState wallBlock(CaveZone zone, int x, int y, int z, BlockState current) {
        int layer = y + layerNoise(x, z, 3312, 7);
        int pocket = Math.floorMod(blockHash(x >> 2, y >> 1, z >> 2, 7711), 100);

        if (zone == CaveZone.INNER_SHELL) {
            if (pocket >= 94) {
                return Blocks.GOLD_BLOCK.defaultBlockState();
            }

            if (Math.floorMod(layer, 17) <= 2) {
                return Blocks.ORANGE_WOOL.defaultBlockState();
            }

            return Blocks.YELLOW_WOOL.defaultBlockState();
        }

        if (zone == CaveZone.OUTER_SHELL) {
            if (pocket >= 97) {
                return Blocks.ORANGE_WOOL.defaultBlockState();
            }

            return Math.floorMod(layer, 23) < 8
                    ? GCBlocks.MOON_ROCK.defaultBlockState()
                    : GCBlocks.LUNASLATE.defaultBlockState();
        }

        return current;
    }
}