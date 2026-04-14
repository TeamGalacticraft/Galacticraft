/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockSlimelingEgg;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockEgg extends ItemBlockDesc implements GCRarity
{

    public ItemBlockEgg(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public String getTranslationKey(ItemStack itemstack)
    {
        String name = BlockSlimelingEgg.EnumEggColor.values()[itemstack.getItemDamage() % 3].getName();
        return this.getBlock().getTranslationKey() + "." + name;
    }

    @Override
    public String getTranslationKey()
    {
        return this.getBlock().getTranslationKey() + ".0";
    }
}
