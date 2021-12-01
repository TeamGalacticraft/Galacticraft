/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod;

import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BiomeSourceVisualizer {
    public static void run(BiomeLayerSampler sampler, Registry<Biome> registry) {
        Biome sample;
        Biome hlp = registry.get(GalacticraftBiome.Moon.HIGHLANDS), hle = registry.get(GalacticraftBiome.Moon.HIGHLANDS_EDGE);
        Biome mp = registry.get(GalacticraftBiome.Moon.MARE);
        ByteBuffer buf = MemoryUtil.memAlloc(2048 * 2048 * 4);
        IntBuffer bufi = buf.asIntBuffer();
        for (int z = 0; z < 2048; z++) {
            for (int x = 0; x < 2048; x++) {
                sample = this.sampler.sample(registry, x, z);
                if (sample == hlp) {
                    bufi.put(0xFFFFFFFF);
                } else if (sample == mp) {
                    bufi.put(0x000000FF);
                    if (!mare) {
                        mare = true;
                    }
                } else if (sample == hle) {
                    bufi.put(0xCCCCCCFF);
                } else { //edge
                    bufi.put(0x222222FF);
                }
            }
        }

        new File("biome_out.png").delete();
        STBImageWrite.stbi_write_png("biome_out.png", 2048, 2048, 4, buf, 2048 * 4);
        MemoryUtil.memFree(buf);
    }
}
