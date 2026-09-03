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
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedLavaTubeCaveShape;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CheeseLavaTubeCave extends PlanetCave {
    public CheeseLavaTubeCave() {
        super(
                Constant.id("cheese_lava_tube_cave"),
                MoonCaveShapeType.LAVA_TUBE,
                new PathSolvedLavaTubeCaveShape(
                        5, 9,
                        3, 7,
                        10, 22,
                        22, 58,
                        2.2D, 4.5D,
                        3.5D, 13.0D,
                        20, 70,
                        -60, -50
                ),
                100,
                0.34F,
                16,
                26,
                -60,
                32,
                Blocks.YELLOW_WOOL.defaultBlockState(),
                Blocks.ORANGE_WOOL.defaultBlockState(),
                Blocks.GOLD_BLOCK.defaultBlockState(),
                CaveTransitionConfig.weak(),
                java.util.List.of(),
                10
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new CheeseLavaTubeCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.CHEESE_CAVES);
    }

    @Override
    public BlockState wallBlock(CaveZone zone, int x, int y, int z, BlockState current) {
        int layer = y + layerNoise(x, z, 4418, 6);
        int pocket = Math.floorMod(blockHash(x >> 2, y >> 1, z >> 2, 1842), 100);

        if (zone == CaveZone.INNER_SHELL) {
            if (pocket >= 96) {
                return Blocks.GOLD_BLOCK.defaultBlockState();
            }

            if (pocket >= 82) {
                return Blocks.ORANGE_WOOL.defaultBlockState();
            }

            return Math.floorMod(layer, 11) <= 3
                    ? Blocks.YELLOW_WOOL.defaultBlockState()
                    : GCBlocks.MOON_ROCK.defaultBlockState();
        }

        if (zone == CaveZone.OUTER_SHELL) {
            return Math.floorMod(layer, 19) < 6
                    ? GCBlocks.LUNASLATE.defaultBlockState()
                    : GCBlocks.MOON_ROCK.defaultBlockState();
        }

        return current;
    }
}