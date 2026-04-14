/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.asteroids.items;

import java.util.List;
import javax.annotation.Nullable;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.BlockWalkway;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockWalkway extends ItemBlockDesc
{

    public ItemBlockWalkway(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (itemStack.getItemDamage() == BlockWalkway.EnumWalkwayType.WALKWAY_WIRE.getMeta())
        {
            tooltip.add(EnumColor.AQUA + GCCoreUtil.translate("tile.aluminum_wire.alu_wire.name"));
        } else if (itemStack.getItemDamage() == BlockWalkway.EnumWalkwayType.WALKWAY_PIPE.getMeta())
        {
            tooltip.add(EnumColor.AQUA + GCCoreUtil.translate(GCBlocks.oxygenPipe.getTranslationKey() + ".name"));
        }

        super.addInformation(itemStack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public String getTranslationKey(ItemStack itemstack)
    {
        String name = BlockWalkway.EnumWalkwayType.values()[itemstack.getItemDamage()].getName();
        return this.getBlock().getTranslationKey() + "." + name;
    }

    @Override
    public String getTranslationKey()
    {
        return this.getBlock().getTranslationKey() + ".0";
    }
}
