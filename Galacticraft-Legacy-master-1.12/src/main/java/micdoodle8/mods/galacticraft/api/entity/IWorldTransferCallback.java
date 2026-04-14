/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.entity;

import net.minecraft.world.World;

public interface IWorldTransferCallback
{

    void onWorldTransferred(World world);
}
