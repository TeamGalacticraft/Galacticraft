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

public class GradientNoise extends NoiseModule {

    private final FishyNoise noiseGen;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final int numOctaves;
    private final double persistance;

    public GradientNoise(long seed, int nOctaves, double p) {
        this.numOctaves = nOctaves;
        this.persistance = p;
        RandomSource rand = RandomSource.create(seed);
        this.offsetX = (rand.nextFloat() / 2.0F) + 0.0D;
        this.offsetY = (rand.nextFloat() / 2.0F) + 0.0D;
        this.offsetZ = (rand.nextFloat() / 2.0F) + 0.0D;
        this.noiseGen = new FishyNoise(seed);
    }

    @Override
    public double evalNoise(double i) {
        i *= this.freqX;
        double val = 0.0D;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++) {
            val += this.noiseGen.eval(i + this.offsetX, this.offsetY) * curAmplitude;
            i *= 2.0D;
            curAmplitude *= this.persistance;
        }
        return val;
    }

    @Override
    public double evalNoise(double x, double y) {
        if (this.numOctaves == 1)
            return this.noiseGen.eval(x * this.freqX + this.offsetX, y * this.freqY + this.offsetY) * this.ampl;
        x *= this.freqX;
        y *= this.freqY;
        double val = 0.0D;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++) {
            val += this.noiseGen.eval(x + this.offsetX, y + this.offsetY) * curAmplitude;
            x *= 2.0D;
            y *= 2.0D;
            curAmplitude *= this.persistance;
        }
        return val;
    }

    @Override
    public double evalNoise(double x, double y, double z) {
        if (this.numOctaves == 1)
            return this.noiseGen.eval(x * this.freqX + this.offsetX, y * this.freqY + this.offsetY, z * this.freqZ + this.offsetZ) * this.ampl;
        x *= this.freqX;
        y *= this.freqY;
        z *= this.freqZ;
        double val = 0.0D;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++) {
            val += this.noiseGen.eval(x + this.offsetX, y + this.offsetY, z + this.offsetZ) * curAmplitude;
            x *= 2.0D;
            y *= 2.0D;
            z *= 2.0D;
            curAmplitude *= this.persistance;
        }
        return val;
    }
}
