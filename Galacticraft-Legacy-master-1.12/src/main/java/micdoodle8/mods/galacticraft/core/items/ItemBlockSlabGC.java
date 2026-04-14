/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.blocks.BlockDoubleSlabGC;
import micdoodle8.mods.galacticraft.core.blocks.BlockSlabGC;

public class ItemBlockSlabGC extends ItemSlab implements GCRarity
{

    public ItemBlockSlabGC(Block block, BlockSlabGC singleSlab, BlockDoubleSlabGC doubleSlab)
    {
        super(block, singleSlab, doubleSlab);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta & 7;
    }

    @Override
    public String getTranslationKey(ItemStack itemStack)
    {
        BlockSlabGC slab = (BlockSlabGC) Block.getBlockFromItem(itemStack.getItem());
        return super.getTranslationKey() + "." + slab.getTranslationKey(itemStack.getItemDamage());
    }
}
