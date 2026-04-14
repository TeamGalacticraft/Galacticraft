/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;

public class ItemIC2Compat extends Item implements ISortableItem, GCRarity
{

    public static final String[] types =
    {"dust", "ore_purified", "ore_crushed", "dust_small"};
    public static final String[] names =
    {"alu", "titanium"};

    public ItemIC2Compat(String assetName)
    {
        super();
        this.setMaxDamage(0);
        this.setHasSubtypes(CompatibilityManager.isIc2Loaded());
        this.setTranslationKey(assetName);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public String getTranslationKey(ItemStack itemStack)
    {
        int meta = itemStack.getItemDamage();
        if (!CompatibilityManager.isIc2Loaded())
            meta = 0;
        return this.getTranslationKey() + "." + ItemIC2Compat.types[meta % 4] + "_" + ItemIC2Compat.names[meta / 4];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> par3List)
    {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH)
        {
            par3List.add(new ItemStack(this, 1, 0));
            if (CompatibilityManager.isIc2Loaded())
            {
                par3List.add(new ItemStack(this, 1, 1));
                par3List.add(new ItemStack(this, 1, 2));
                par3List.add(new ItemStack(this, 1, 7));
            }
        }
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GENERAL;
    }
}
