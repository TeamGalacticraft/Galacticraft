/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;

public class ItemBlockLandingPad extends ItemBlockDesc implements GCRarity
{

    public ItemBlockLandingPad(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getTranslationKey(ItemStack par1ItemStack)
    {
        String name = "";

        switch (par1ItemStack.getItemDamage())
        {
            case 0:
                name = "landing_pad";
                break;
            case 1:
                name = "buggy_fueler";
                break;
            case 2:
                name = "cargo_pad";
                break;
        }

        return this.getBlock().getTranslationKey() + "." + name;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote && stack.getItemDamage() == 0 && player instanceof EntityPlayerSP)
        {
            ClientProxyCore.playerClientHandler.onBuild(5, (EntityPlayerSP) player);
        }
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
