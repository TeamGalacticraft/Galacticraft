/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.mars.items;

import micdoodle8.mods.galacticraft.api.item.GCRarity;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineBase;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars;
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ItemBlockMachine extends ItemBlockDesc implements GCRarity
{

    public ItemBlockMachine(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState state)
    {
        int metaAt = itemStack.getItemDamage();

        // If it is a Cryogenic Chamber, check the space
        if (metaAt == BlockMachineMars.CRYOGENIC_CHAMBER_METADATA)
        {
            for (int y = 0; y < 3; y++)
            {
                IBlockState stateAt = world.getBlockState(pos.add(0, y, 0));

                if (this.getBlock() == MarsBlocks.machine)
                {
                    if (!stateAt.getMaterial().isReplaceable())
                    {
                        if (world.isRemote)
                        {
                            FMLClientHandler.instance().getClient().ingameGUI
                                .setOverlayMessage(new TextComponentString(GCCoreUtil.translate("gui.warning.noroom")).setStyle(new Style().setColor(TextFormatting.RED)).getFormattedText(), false);
                        }
                        return false;
                    }
                }
            }
        }
        return super.placeBlockAt(itemStack, player, world, pos, facing, hitX, hitY, hitZ, state);
    }

    @Override
    public String getTranslationKey(ItemStack itemstack)
    {
        int index = 0;
        int typenum = itemstack.getItemDamage() & 12;

        if (this.getBlock() == MarsBlocks.machine)
        {
            if (typenum == BlockMachineMars.LAUNCH_CONTROLLER_METADATA)
            {
                index = 2;
            } else if (typenum == BlockMachineMars.CRYOGENIC_CHAMBER_METADATA)
            {
                index = 1;
            }
        } else if (this.getBlock() == MarsBlocks.machineT2)
        {
            return ((BlockMachineBase) MarsBlocks.machineT2).getTranslationKey(typenum);
        }

        return this.getBlock().getTranslationKey() + "." + index;
    }

    @Override
    public String getTranslationKey()
    {
        return this.getBlock().getTranslationKey() + ".0";
    }
}
