/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.world;

import net.minecraft.entity.Entity;

public interface IZeroGDimension
{

    boolean inFreefall(Entity entity);

    void setInFreefall(Entity entity);
}
