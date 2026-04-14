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
import micdoodle8.mods.galacticraft.core.blocks.BlockAluminumWire;

public class ItemBlockAluminumWire extends ItemBlockDesc implements GCRarity
{

    public ItemBlockAluminumWire(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getTranslationKey(ItemStack itemstack)
    {
        String name = BlockAluminumWire.EnumWireType.values()[itemstack.getItemDamage()].getName();
        return this.getBlock().getTranslationKey() + "." + name;
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
