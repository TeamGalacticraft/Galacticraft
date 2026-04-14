/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.transmission.grid;

import micdoodle8.mods.galacticraft.api.vector.Vector3;

public interface IReflectorNode
{

    Vector3 getInputPoint();

    Vector3 getOutputPoint(boolean offset);
}
