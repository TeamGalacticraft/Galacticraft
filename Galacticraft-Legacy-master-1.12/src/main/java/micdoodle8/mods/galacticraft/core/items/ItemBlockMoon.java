/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.api.item.GCRarity;

public class ItemBlockMoon extends ItemBlockDesc implements GCRarity
{

    public ItemBlockMoon(Block block)
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
        String name;

        switch (itemstack.getItemDamage())
        {
            case 0:
            {
                name = "coppermoon";
                break;
            }
            case 1:
            {
                name = "tinmoon";
                break;
            }
            case 2:
            {
                name = "cheesestone";
                break;
            }
            case 3:
            {
                name = "moondirt";
                break;
            }
            case 4:
            {
                name = "moonstone";
                break;
            }
            case 5:
            {
                name = "moongrass";
                break;
            }
            case 6:
            {
                name = "sapphiremoon";
                break;
            }
            case 14:
            {
                name = "bricks";
                break;
            }
            default:
                name = "null";
        }

        return this.getBlock().getTranslationKey() + "." + name;
    }

    @Override
    public String getTranslationKey()
    {
        return this.getBlock().getTranslationKey() + ".0";
    }
}
