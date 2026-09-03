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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.HashMap;
import java.util.Map;

public final class PlanetCaveResolverCache {
    private final ChunkPos chunkPos;
    private final int minY;
    private final int maxY;
    private final BiomeSource biomeSource;
    private final RandomState randomState;
    private final Map<ResourceLocation, PlanetCaveResolver> resolvers = new HashMap<>();

    public PlanetCaveResolverCache(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            BiomeSource biomeSource,
            RandomState randomState
    ) {
        this.chunkPos = chunkPos;
        this.minY = minY;
        this.maxY = maxY;
        this.biomeSource = biomeSource;
        this.randomState = randomState;
    }

    public PlanetCave resolve(int x, int y, int z, PlanetCave fallback) {
        PlanetCaveResolver resolver = this.resolvers.computeIfAbsent(
                fallback.id(),
                ignored -> new PlanetCaveResolver(
                        this.chunkPos,
                        this.minY,
                        this.maxY,
                        this.biomeSource,
                        this.randomState,
                        fallback
                )
        );

        return resolver.resolve(x, y, z, fallback);
    }
}