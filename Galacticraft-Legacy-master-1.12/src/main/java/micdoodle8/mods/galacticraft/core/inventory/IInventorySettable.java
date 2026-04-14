/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.inventory.IInventory;

public interface IInventorySettable extends IInventory
{

    void setSizeInventory(int size);
}
