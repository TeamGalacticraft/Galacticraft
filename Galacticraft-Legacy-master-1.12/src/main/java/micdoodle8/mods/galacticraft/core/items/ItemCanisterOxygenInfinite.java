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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.api.item.IItemOxygenSupply;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class ItemCanisterOxygenInfinite extends Item implements IItemOxygenSupply, ISortableItem, GCRarity
{

    public ItemCanisterOxygenInfinite(String assetName)
    {
        super();
        this.setMaxDamage(ItemCanisterGeneric.EMPTY);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setTranslationKey(assetName);
        this.setContainerItem(GCItems.oilCanister);
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.infinite_item.desc"));
        tooltip.add(EnumColor.RED + GCCoreUtil.translate("gui.creative_only.desc"));
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemstack)
    {
        if (super.getContainerItem(itemstack).isEmpty())
        {
            return ItemStack.EMPTY;
        }
        return itemstack;
    }

    @Override
    public int discharge(ItemStack itemStack, int amount)
    {
        return amount;
    }

    @Override
    public int getOxygenStored(ItemStack par1ItemStack)
    {
        return par1ItemStack.getMaxDamage();
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GEAR;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
    {
        ItemStack itemStack = player.getHeldItem(hand);
        if (player instanceof EntityPlayerMP)
        {
            GCPlayerStats stats = GCPlayerStats.get(player);
            ItemStack gear = stats.getExtendedInventory().getStackInSlot(2);
            ItemStack gear1 = stats.getExtendedInventory().getStackInSlot(3);

            if (gear.isEmpty())
            {
                stats.getExtendedInventory().setInventorySlotContents(2, itemStack.copy());
                itemStack = ItemStack.EMPTY;
            } else if (gear1.isEmpty())
            {
                stats.getExtendedInventory().setInventorySlotContents(3, itemStack.copy());
                itemStack = ItemStack.EMPTY;
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}
