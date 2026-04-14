/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.perlin.generator;

import java.util.Random;
import micdoodle8.mods.galacticraft.core.perlin.Evaluator;
import micdoodle8.mods.galacticraft.core.perlin.FishyNoise;
import micdoodle8.mods.galacticraft.core.perlin.NoiseModule;

public class GradientNoise extends NoiseModule implements Evaluator
{

    private final FishyNoise noiseGen;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final int numOctaves;
    private final double persistance;

    public GradientNoise(long seed, int nOctaves, double p)
    {
        this.numOctaves = nOctaves;
        this.persistance = p;
        Random rand = new Random(seed);
        this.offsetX = (rand.nextFloat() / 2.0F) + 0.0D;
        this.offsetY = (rand.nextFloat() / 2.0F) + 0.0D;
        this.offsetZ = (rand.nextFloat() / 2.0F) + 0.0D;
        this.noiseGen = new FishyNoise(seed);
    }

    @Override
    public double evalNoise(double i)
    {
        i *= this.freqX;
        double val = 0.0D;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++)
        {
            val += this.noiseGen.eval(i + this.offsetX, this.offsetY) * curAmplitude;
            i *= 2.0D;
            curAmplitude *= this.persistance;
        }
        return val;
    }

    @Override
    public double evalNoise(double i, double j)
    {
        if (this.numOctaves == 1)
            return this.noiseGen.eval(i * this.freqX + this.offsetX, j * this.freqY + this.offsetY) * this.ampl;
        i *= this.freqX;
        j *= this.freqY;
        double val = 0.0D;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++)
        {
            val += this.noiseGen.eval(i + this.offsetX, j + this.offsetY) * curAmplitude;
            i *= 2.0D;
            j *= 2.0D;
            curAmplitude *= this.persistance;
        }
        return val;
    }

    @Override
    public double evalNoise(double i, double j, double k)
    {
        if (this.numOctaves == 1)
            return this.noiseGen.eval(i * this.freqX + this.offsetX, j * this.freqY + this.offsetY, k * this.freqZ + this.offsetZ) * this.ampl;
        i *= this.freqX;
        j *= this.freqY;
        k *= this.freqZ;
        double val = 0.0D;
        double curAmplitude = this.ampl;
        for (int n = 0; n < this.numOctaves; n++)
        {
            val += this.noiseGen.eval(i + this.offsetX, j + this.offsetY, k + this.offsetZ) * curAmplitude;
            i *= 2.0D;
            j *= 2.0D;
            k *= 2.0D;
            curAmplitude *= this.persistance;
        }
        return val;
    }
}
