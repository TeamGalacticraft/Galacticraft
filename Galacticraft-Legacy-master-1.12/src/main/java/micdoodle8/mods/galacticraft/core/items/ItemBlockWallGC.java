/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.api.item.GCRarity;

public class ItemBlockWallGC extends ItemBlock implements GCRarity
{

    private static final String[] types = new String[]
    {"tin", "tin", "moon", "moon_bricks", "mars", "mars_bricks"};

    public ItemBlockWallGC(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public String getTranslationKey(ItemStack itemstack)
    {
        int meta = itemstack.getItemDamage();

        if (meta < 0 || meta >= types.length)
        {
            meta = 0;
        }
        return super.getTranslationKey() + "." + types[meta];
    }
}
