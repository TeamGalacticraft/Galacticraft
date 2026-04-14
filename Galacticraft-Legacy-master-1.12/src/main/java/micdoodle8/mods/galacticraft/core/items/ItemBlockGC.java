/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import micdoodle8.mods.galacticraft.api.item.GCRarity;

public class ItemBlockGC extends ItemBlock implements GCRarity
{

    public ItemBlockGC(Block block)
    {
        super(block);
    }

}
