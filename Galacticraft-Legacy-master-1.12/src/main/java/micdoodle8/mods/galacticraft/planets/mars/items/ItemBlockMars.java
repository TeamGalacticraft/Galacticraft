/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMars extends ItemBlock implements GCRarity
{

    public ItemBlockMars(Block block)
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
        String name = "";

        switch (itemstack.getItemDamage())
        {
            case 0:
            {
                name = "coppermars";
                break;
            }
            case 1:
            {
                name = "tinmars";
                break;
            }
            case 3:
            {
                name = "ironmars";
                break;
            }
            case 2:
            {
                name = "deshmars";
                break;
            }
            case 4:
            {
                name = "marscobblestone";
                break;
            }
            case 5:
            {
                name = "marsgrass";
                break;
            }
            case 6:
            {
                name = "marsdirt";
                break;
            }
            case 7:
            {
                name = "marsdungeon";
                break;
            }
            case 8:
            {
                name = "marsdeco";
                break;
            }
            case 9:
            {
                name = "marsstone";
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
