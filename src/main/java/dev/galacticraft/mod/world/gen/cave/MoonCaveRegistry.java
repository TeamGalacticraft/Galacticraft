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

package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.world.gen.cave.impl.CheeseBranchingCave;
import dev.galacticraft.mod.world.gen.cave.impl.CheeseLavaTubeCave;
import dev.galacticraft.mod.world.gen.cave.impl.GlacialLavaTubeCave;
import dev.galacticraft.mod.world.gen.cave.impl.OlivineBranchingCave;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public final class MoonCaveRegistry {
    private static final List<PlanetCave> CAVES = new ArrayList<>();

    static {
        registerDefaults();
    }

    private MoonCaveRegistry() {
    }

    public static void registerDefaults() {
        OlivineBranchingCave.register();
        GlacialLavaTubeCave.register();
        CheeseLavaTubeCave.register();
        CheeseBranchingCave.register();
    }

    public static void register(PlanetCave cave) {
        CAVES.add(cave);
    }

    public static PlanetCave pickForBiome(Holder<Biome> biome, RandomSource random) {
        List<PlanetCave> candidates = new ArrayList<>();

        for (PlanetCave cave : CAVES) {
            if (cave.matchesBiome(biome) && random.nextFloat() <= cave.spawnChance()) {
                candidates.add(cave);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return weightedPick(candidates, random);
    }

    public static PlanetCave findTransitionCave(Holder<Biome> biome, MoonCaveShapeType shapeType) {
        for (PlanetCave cave : CAVES) {
            if (cave.shapeType() == shapeType && cave.matchesBiome(biome)) {
                return cave;
            }
        }

        return null;
    }

    public static boolean isKnownCaveBlock(BlockStateLikeAccess state) {
        for (PlanetCave cave : CAVES) {
            if (state.is(cave.innerWall(0, 0, 0).getBlock())
                    || state.is(cave.outerWall(0, 0, 0).getBlock())
                    || state.is(cave.accent(0, 0, 0).getBlock())) {
                return true;
            }
        }

        return false;
    }

    private static PlanetCave weightedPick(List<PlanetCave> caves, RandomSource random) {
        int totalWeight = 0;

        for (PlanetCave cave : caves) {
            totalWeight += Math.max(0, cave.weight());
        }

        if (totalWeight <= 0) {
            return caves.get(0);
        }

        int roll = random.nextInt(totalWeight);

        for (PlanetCave cave : caves) {
            roll -= Math.max(0, cave.weight());

            if (roll < 0) {
                return cave;
            }
        }

        return caves.get(caves.size() - 1);
    }

    public static PlanetCave firstForBiome(Holder<Biome> biome) {
        for (PlanetCave cave : CAVES) {
            if (cave.matchesBiome(biome)) {
                return cave;
            }
        }

        return null;
    }

    public interface BlockStateLikeAccess {
        boolean is(net.minecraft.world.level.block.Block block);
    }

    public static PlanetCave surfacePainterForBiome(Holder<Biome> biome) {
        PlanetCave best = null;

        for (PlanetCave cave : CAVES) {
            if (!cave.matchesBiome(biome) || !cave.paintsSurface()) {
                continue;
            }

            if (best == null || cave.surfacePainterPriority() > best.surfacePainterPriority()) {
                best = cave;
            }
        }

        return best;
    }
}