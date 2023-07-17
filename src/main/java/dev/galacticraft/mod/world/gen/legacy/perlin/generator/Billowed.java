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

package dev.galacticraft.mod.world.gen.legacy.perlin.generator;

import dev.galacticraft.mod.world.gen.legacy.perlin.FishyNoise;
import dev.galacticraft.mod.world.gen.legacy.perlin.NoiseModule;
import net.minecraft.util.RandomSource;

public class Billowed extends NoiseModule {
    private final FishyNoise noiseGen;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final int numOctaves;
    private final float persistance;

    public Billowed(long seed, int nOctaves, float p) {
        this.numOctaves = nOctaves;
        this.persistance = p;
        final RandomSource rand = RandomSource.create(seed);
        this.offsetX = rand.nextFloat() / 2 + 0.01F;
        this.offsetY = rand.nextFloat() / 2 + 0.01F;
        this.offsetZ = rand.nextFloat() / 2 + 0.01F;
        this.noiseGen = new FishyNoise(seed);
    }

    @Override
    public double evalNoise(double x) {
        x *= this.freqX;
        float val = 0;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++) {
            val += Math.abs(this.noiseGen.eval(x + this.offsetX, this.offsetY) * curAmplitude);
            x *= 2;
            curAmplitude *= this.persistance;
        }
        return val;
    }

    @Override
    public double evalNoise(double x, double y) {
        x *= this.freqX;
        y *= this.freqY;
        if (this.numOctaves == 2) {
            return Math.abs(this.noiseGen.eval(x + this.offsetX, y + this.offsetY) * this.ampl)
                + Math.abs(this.noiseGen.eval(x + x + this.offsetX, y + y + this.offsetY) * this.ampl * this.persistance);
        }

        float val = 0;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++) {
            val += Math.abs(this.noiseGen.eval(x + this.offsetX, y + this.offsetY) * curAmplitude);
            x += x;
            y += y;
            curAmplitude *= this.persistance;
        }
        return val;
    }

    @Override
    public double evalNoise(double x, double y, double z) {
        x *= this.freqX;
        y *= this.freqY;
        z *= this.freqZ;
        float val = 0;
        for (int n = 0; n < this.numOctaves; n++) {
            val += Math.abs(this.noiseGen.eval(x + this.offsetX, y + this.offsetY, z + this.offsetZ) * this.ampl);
            x *= 2;
            y *= 2;
            z *= 2;
        }
        return val;
    }
}