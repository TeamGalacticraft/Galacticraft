/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

public abstract class ItemDesc extends Item implements IShiftDescription
{

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> info, ITooltipFlag flagIn)
    {
        if (this.showDescription(stack.getItemDamage()))
        {
            if (ClientUtil.isKeyPressed(Minecraft.getMinecraft().gameSettings.keyBindSneak))
            {
                info.addAll(FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(this.getShiftDescription(stack.getItemDamage()), 150));
            } else
            {
                info.add(GCCoreUtil.translateWithFormat("item_desc.shift.name", GameSettings.getKeyDisplayString(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())));
            }
        }
    }
}
