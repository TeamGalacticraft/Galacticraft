/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.perlin;

import micdoodle8.mods.galacticraft.annotations.ReplaceWith;

public abstract class NoiseModule
{

    public double freqX = 1.0D;
    public double freqY = 1.0D;
    public double freqZ = 1.0D;
    public double ampl = 1.0D;

    public void setFrequencyAll(double frequency)
    {
        this.freqX = frequency;
        this.freqY = frequency;
        this.freqZ = frequency;
    }

    @Deprecated
    @ReplaceWith("freqX")
    public float frequencyX = 1;
    @Deprecated
    @ReplaceWith("freqY")
    public float frequencyY = 1;
    @Deprecated
    @ReplaceWith("freqZ")
    public float frequencyZ = 1;
    @Deprecated
    @ReplaceWith("ampl")
    public float amplitude = 1;

    @Deprecated
    @ReplaceWith("evalNoise(double x)")
    public float getNoise(float i)
    {
        return i;
    }

    @Deprecated
    @ReplaceWith("evalNoise(double x, double y)")
    public float getNoise(float i, float j)
    {
        return i;
    }

    @Deprecated
    @ReplaceWith("evalNoise(double x, double y, double z)")
    public float getNoise(float i, float j, float k)
    {
        return i;
    }

    @Deprecated
    @ReplaceWith("setFrequencyAll(double frequency)")
    public void setFrequency(float frequency)
    {
        this.frequencyX = frequency;
        this.frequencyY = frequency;
        this.frequencyZ = frequency;
    }
}
