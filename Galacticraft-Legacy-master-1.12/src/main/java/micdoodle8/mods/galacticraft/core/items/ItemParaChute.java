/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;

import static net.minecraft.item.EnumDyeColor.BLACK;
import static net.minecraft.item.EnumDyeColor.BLUE;
import static net.minecraft.item.EnumDyeColor.BROWN;
import static net.minecraft.item.EnumDyeColor.CYAN;
import static net.minecraft.item.EnumDyeColor.GRAY;
import static net.minecraft.item.EnumDyeColor.GREEN;
import static net.minecraft.item.EnumDyeColor.LIGHT_BLUE;
import static net.minecraft.item.EnumDyeColor.LIME;
import static net.minecraft.item.EnumDyeColor.MAGENTA;
import static net.minecraft.item.EnumDyeColor.ORANGE;
import static net.minecraft.item.EnumDyeColor.PINK;
import static net.minecraft.item.EnumDyeColor.PURPLE;
import static net.minecraft.item.EnumDyeColor.RED;
import static net.minecraft.item.EnumDyeColor.SILVER;
import static net.minecraft.item.EnumDyeColor.WHITE;
import static net.minecraft.item.EnumDyeColor.YELLOW;

public class ItemParaChute extends Item implements ISortableItem, IClickableItem, GCRarity
{

    public static final String[] names =
    {"plain", // 0
            "black", // 1
            "blue", // 2
            "lime", // 3
            "brown", // 4
            "darkblue", // 5
            "darkgray", // 6
            "darkgreen", // 7
            "gray", // 8
            "magenta", // 9
            "orange", // 10
            "pink", // 11
            "purple", // 12
            "red", // 13
            "teal", // 14
            "yellow"}; // 15

    public ItemParaChute(String assetName)
    {
        super();
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setTranslationKey(assetName);
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (tab == GalacticraftCore.galacticraftItemsTab || tab == CreativeTabs.SEARCH)
        {
            for (int i = 0; i < ItemParaChute.names.length; i++)
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
    public String getTranslationKey(ItemStack itemStack)
    {
        return this.getTranslationKey() + "_" + ItemParaChute.names[itemStack.getItemDamage()];
    }

    public static EnumDyeColor getDyeEnumFromParachuteDamage(int damage)
    {
        switch (damage)
        {
            case 1:
                return BLACK;
            case 13:
                return RED;
            case 7:
                return GREEN;
            case 4:
                return BROWN;
            case 5:
                return BLUE;
            case 12:
                return PURPLE;
            case 14:
                return CYAN;
            case 8:
                return SILVER;
            case 6:
                return GRAY;
            case 11:
                return PINK;
            case 3:
                return LIME;
            case 15:
                return YELLOW;
            case 2:
                return LIGHT_BLUE;
            case 9:
                return MAGENTA;
            case 10:
                return ORANGE;
            case 0:
                return WHITE;
        }

        return WHITE;
    }

    public static int getParachuteDamageValueFromDyeEnum(EnumDyeColor color)
    {
        switch (color)
        {
            case BLACK:
                return 1;
            case RED:
                return 13;
            case GREEN:
                return 7;
            case BROWN:
                return 4;
            case BLUE:
                return 5;
            case PURPLE:
                return 12;
            case CYAN:
                return 14;
            case SILVER:
                return 8;
            case GRAY:
                return 6;
            case PINK:
                return 11;
            case LIME:
                return 3;
            case YELLOW:
                return 15;
            case LIGHT_BLUE:
                return 2;
            case MAGENTA:
                return 9;
            case ORANGE:
                return 10;
            case WHITE:
                return 0;
        }

        return -1;
    }

    public static int getParachuteDamageValueFromDye(int meta)
    {
        return getParachuteDamageValueFromDyeEnum(EnumDyeColor.byDyeDamage(meta));
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
            if (itemStack.getItem() instanceof IClickableItem)
            {
                itemStack = ((IClickableItem) itemStack.getItem()).onItemRightClick(itemStack, worldIn, player);
            }

            if (itemStack.isEmpty())
            {
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, itemStack);
    }

    public ItemStack onItemRightClick(ItemStack itemStack, World worldIn, EntityPlayer player)
    {
        GCPlayerStats stats = GCPlayerStats.get(player);
        ItemStack gear = stats.getExtendedInventory().getStackInSlot(4);

        if (gear.isEmpty())
        {
            stats.getExtendedInventory().setInventorySlotContents(4, itemStack.copy());
            itemStack = ItemStack.EMPTY;
        }

        return itemStack;
    }
}
