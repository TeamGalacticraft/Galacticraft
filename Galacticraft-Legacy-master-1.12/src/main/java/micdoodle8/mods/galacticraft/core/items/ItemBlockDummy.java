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

public class ItemBlockDummy extends ItemBlock implements GCRarity
{

    public ItemBlockDummy(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack itemstack)
    {
        int metadata = itemstack.getItemDamage();
        String blockName = "";

        switch (metadata)
        {
            case 1:
                blockName = "spaceStationBase";
                break;
            case 2:
                blockName = "launchPad";
                break;
            case 3:
                blockName = "nasaWorkbench";
                break;
            case 4:
                blockName = "solar";
                break;
            case 5:
                blockName = "cryogenicChamber";
                break;
            default:
                blockName = null;
                break;
        }

        return this.getBlock().getTranslationKey() + "." + blockName;
    }

    @Override
    public String getTranslationKey()
    {
        return this.getBlock().getTranslationKey() + ".0";
    }
}
