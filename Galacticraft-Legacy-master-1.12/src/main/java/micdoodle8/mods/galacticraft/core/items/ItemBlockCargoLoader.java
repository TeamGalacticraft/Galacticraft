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

public class ItemBlockCargoLoader extends ItemBlockDesc implements GCRarity
{

    public ItemBlockCargoLoader(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getTranslationKey(ItemStack par1ItemStack)
    {
        String name = "";

        if (par1ItemStack.getItemDamage() < 4)
        {
            name = "loader";
        } else
        {
            name = "unloader";
        }

        return this.getBlock().getTranslationKey() + "." + name;
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
