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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public class ItemBasic extends Item implements ISortableItem, GCRarity
{

    public static final String[] names =
    {"solar_module_0", "solar_module_1", "raw_silicon", "ingot_copper", "ingot_tin", "ingot_aluminum", "compressed_copper", "compressed_tin", "compressed_aluminum", "compressed_steel",
            "compressed_bronze", "compressed_iron", "wafer_solar", "wafer_basic", "wafer_advanced", "dehydrated_apple", "dehydrated_carrot", "dehydrated_melon", "dehydrated_potato",
            "frequency_module", "ambient_thermal_controller"};
    public static final int WAFER_BASIC = 13;
    public static final int WAFER_ADVANCED = 14;

    public ItemBasic(String assetName)
    {
        super();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
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
        if (itemStack.getItemDamage() > 14 && itemStack.getItemDamage() < 19)
        {
            return this.getTranslationKey() + ".canned_food";
        }

        return this.getTranslationKey() + "." + ItemBasic.names[itemStack.getItemDamage()];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH)
        {
            for (int i = 0; i < 15; i++)
            {
                list.add(new ItemStack(this, 1, i));
            }
            for (int i = 19; i < ItemBasic.names.length; i++)
            {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (par1ItemStack.getItemDamage() > 14 && par1ItemStack.getItemDamage() < 19)
        {
            tooltip.add(EnumColor.BRIGHT_GREEN + GCCoreUtil.translate(this.getTranslationKey() + "." + ItemBasic.names[par1ItemStack.getItemDamage()] + ".name"));
        } else if (par1ItemStack.getItemDamage() == 19)
        {
            tooltip.add(EnumColor.AQUA + GCCoreUtil.translate("gui.frequency_module.desc.0"));
            tooltip.add(EnumColor.AQUA + GCCoreUtil.translate("gui.frequency_module.desc.1"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (itemStackIn.getItemDamage() == 19)
        {
            if (playerIn instanceof EntityPlayerMP)
            {
                GCPlayerStats stats = GCPlayerStats.get(playerIn);
                ItemStack gear = stats.getExtendedInventory().getStackInSlot(5);

                if (gear.isEmpty() && itemStackIn.getTagCompound() == null)
                {
                    stats.getExtendedInventory().setInventorySlotContents(5, itemStackIn.copy());
                    itemStackIn = ItemStack.EMPTY;
                }
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity)
    {
        if (itemStack.getItemDamage() != 19)
        {
            return false;
        }

        // Frequency module
        if (!player.world.isRemote && entity != null && !(entity instanceof EntityPlayer))
        {
            if (itemStack.getTagCompound() == null)
            {
                itemStack.setTagCompound(new NBTTagCompound());
            }

            itemStack.getTagCompound().setLong("linkedUUIDMost", entity.getUniqueID().getMostSignificantBits());
            itemStack.getTagCompound().setLong("linkedUUIDLeast", entity.getUniqueID().getLeastSignificantBits());

            player.sendMessage(new TextComponentString(GCCoreUtil.translate("gui.tracking.message")));
            return true;
        }
        return false;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        switch (meta)
        {
            case 3:
            case 4:
            case 5:
                return EnumSortCategoryItem.INGOT;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return EnumSortCategoryItem.PLATE;
            case 19:
                return EnumSortCategoryItem.GEAR;
        }
        return EnumSortCategoryItem.GENERAL;
    }

    @Override
    public float getSmeltingExperience(ItemStack item)
    {
        switch (item.getItemDamage())
        {
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return 1F;
        }
        return -1F;
    }
}
