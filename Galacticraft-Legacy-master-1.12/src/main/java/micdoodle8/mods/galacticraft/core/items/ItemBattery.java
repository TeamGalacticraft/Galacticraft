/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;

public class ItemBattery extends ItemElectricBase implements ISortableItem, GCRarity
{

    public ItemBattery(String assetName)
    {
        super();
        this.setMaxStackSize(4);
        this.setTranslationKey(assetName);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public float getMaxElectricityStored(ItemStack itemStack)
    {
        return 15000;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GENERAL;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        if (stack.getItemDamage() < 100 || stack.hasTagCompound() && stack.getTagCompound().hasKey("electricity"))
        {
            return 1;
        }
        return this.getItemStackLimit();
    }
}
