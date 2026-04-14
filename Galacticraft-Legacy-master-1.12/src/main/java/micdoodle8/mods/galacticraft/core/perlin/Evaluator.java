/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.perlin;

public interface Evaluator
{

    double evalNoise(double x);

    double evalNoise(double x, double y);

    double evalNoise(double x, double y, double z);
}
