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

package dev.galacticraft.mod.world.dimension;

public class MoonConstants {
    public static final int MOON_MIN_TERRAIN_HEIGHT = -128;
    public static final int MOON_MAX_HEIGHT = 384;

    public static final int COMET_TUNDRA_SURFACE_HEIGHT = 60;
    public static final int BASALTIC_MARE_SURFACE_HEIGHT = 60;
    public static final int LUNAR_LOW_LANDS_SURFACE_HEIGHT = 80;
    public static final int LUNAR_HIGH_LANDS_SURFACE_HEIGHT = 140;

    public static final int COMET_TUNDRA_SURFACE_HEIGHT_VARIATION = 3;
    public static final int BASALTIC_MARE_SURFACE_HEIGHT_VARIATION = 3;
    public static final int LUNAR_LOW_LANDS_SURFACE_HEIGHT_VARIATION = 20;
    public static final int LUNAR_HIGH_LANDS_SURFACE_HEIGHT_VARIATION = 3;

    public static final float COMET_TUNDRA_CONTINENTALNESS_MINIMUM = -1.2f;
    public static final float BASALTIC_MARE_CONTINENTALNESS_MINIMUM = -0.455f;
    public static final float LUNAR_LOW_LANDS_CONTINENTALNESS_MINIMUM = -0.11f;
    public static final float LUNAR_HIGH_LANDS_CONTINENTALNESS_MINIMUM = 0.3f;

    public static final float LUNAR_LOW_TO_HIGH_LANDS_RANGE = 0.05f;
    public static final float BASALTIC_MARE_TO_LUNAR_LOW_LANDS_RANGE = 0.1f;

    // --OLIVINE CAVES--
    public static final float OLIVINE_CAVE_PROBABILITY = 0.25f;
    public static final int OLIVINE_CAVE_MIN_HEIGHT = -68;
    public static final int OLIVINE_CAVE_MAX_HEIGHT = 30;
    public static final float OLIVINE_CAVE_Y_SCALE_MIN = 0.8f;
    public static final float OLIVINE_CAVE_Y_SCALE_MAX = 2.2f;
    public static final float OLIVINE_CAVE_BASALT_INTERIOR_CHANCE = 0.95f; //95%
    public static final int MAX_FEATURE_SPAWN = 55;
    public static final int MIN_FEATURE_SPAWN = -68; //95%
}
