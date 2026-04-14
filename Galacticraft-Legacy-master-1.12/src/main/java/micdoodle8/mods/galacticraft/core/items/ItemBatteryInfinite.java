/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class ItemBatteryInfinite extends ItemElectricBase implements ISortableItem, GCRarity
{

    public ItemBatteryInfinite(String assetName)
    {
        super();
        this.setTranslationKey(assetName);
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }

    @Override
    protected void setMaxTransfer()
    {
        this.transferMax = 1000;
    }

    @Override
    public int getTierGC(ItemStack itemStack)
    {
        return 3;
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.infinite_item.desc"));
        tooltip.add(EnumColor.RED + GCCoreUtil.translate("gui.creative_only.desc"));
    }

    @Override
    public float getElectricityStored(ItemStack itemStack)
    {
        return this.getMaxElectricityStored(itemStack);
    }

    @Override
    public void setElectricity(ItemStack itemStack, float joules)
    {
    }

    @Override
    public float getMaxElectricityStored(ItemStack itemStack)
    {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getTransfer(ItemStack itemStack)
    {
        return 0.0F;
    }

    @Override
    public float recharge(ItemStack theItem, float energy, boolean doReceive)
    {
        return 0F;
    }

    @Override
    public float discharge(ItemStack theItem, float energy, boolean doTransfer)
    {
        return energy;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH)
        {
            list.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GENERAL;
    }
}
