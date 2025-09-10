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

public interface MoonConstants {
    interface Dimension {
        int MIN_DIMENSION_HEIGHT = -64;
        int MAX_DIMENSION_HEIGHT = 384;
    }

    interface SurfaceHeights {
        int COMET_TUNDRA = 60;
        int BASALTIC_MARE = 60;
        int LUNAR_LOW_LANDS = 80;
        int LUNAR_HIGH_LANDS = 140;
    }

    interface HeightVariations {
        int COMET_TUNDRA = 3;
        int BASALTIC_MARE = 3;
        int LUNAR_LOW_LANDS = 20;
        int LUNAR_HIGH_LANDS = 2;
    }

    interface MinimumContinentalness {
        float COMET_TUNDRA = -1.2f;
        float BASALTIC_MARE = -0.455f;
        float LUNAR_LOW_LANDS = -0.11f;
        float LUNAR_HIGH_LANDS = 0.3f;
    }

    interface Ranges {
        float LOW_TO_HIGH = 0.05f;
        float BASALTIC_TO_LOW = 0.1f;
    }

    interface OlivineCaves {
        float PROBABILITY = 0.25f;
        int MAX_HEIGHT = 30;
        int MIN_HEIGHT = -30;
        float Y_SCALE_MAX = 2.2f;
        float Y_SCALE_MIN = 0.8f;
        float BASALT_INTERIOR_CHANCE = 0.95f;
        int MAX_FEATURE_SPAWN = 55;
        int MIN_FEATURE_SPAWN = -30;
    }

    interface LunarHighLands {
        double NOISE_SCALE_XZ = 3;
    }
}
