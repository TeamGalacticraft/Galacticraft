/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.item;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.entity.player.EntityPlayer;

public interface IHoldableItemCustom extends IHoldableItem
{

    Vector3 getLeftHandRotation(EntityPlayer player);

    Vector3 getRightHandRotation(EntityPlayer player);
}
