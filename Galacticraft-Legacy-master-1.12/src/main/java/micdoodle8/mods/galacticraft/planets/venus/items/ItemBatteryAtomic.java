/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.venus.items;

import java.util.List;
import javax.annotation.Nullable;
import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.items.ISortableItem;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemBatteryAtomic extends ItemElectricBase implements ISortableItem, GCRarity
{

    public ItemBatteryAtomic(String assetName)
    {
        super();
        this.setTranslationKey(assetName);
    }

    @Override
    protected void setMaxTransfer()
    {
        this.transferMax = 7;
    }

    @Override
    public int getTierGC(ItemStack itemStack)
    {
        return 2;
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
        tooltip.add(EnumColor.ORANGE + GCCoreUtil.translate("gui.message.low_energy_output.name"));
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
        return super.discharge(theItem, energy, doTransfer);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> par3List)
    {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH)
        {
            par3List.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GENERAL;
    }
}
