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

package dev.galacticraft.mod.content;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.custom.AsteroidChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;


public class GCChunkGenerator {
    public static final GCChunkGeneratorRegistry CHUNK_GENERATOR = new GCChunkGeneratorRegistry();


    public static final MapCodec<AsteroidChunkGenerator> ASTEROID = CHUNK_GENERATOR.register("asteroid_chunk_generator", AsteroidChunkGenerator.CODEC);

    public static <T extends ChunkGenerator> MapCodec<T> register(String id, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.CHUNK_GENERATOR, Constant.id(id), codec);
    }

    public static void register() {

    }

}
