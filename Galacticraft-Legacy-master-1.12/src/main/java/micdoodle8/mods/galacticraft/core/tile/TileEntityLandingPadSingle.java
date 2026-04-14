/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.tile;

import java.util.ArrayList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.GCBlocks;

public class TileEntityLandingPadSingle extends TileEntity implements ITickable
{

    private int corner = 0;

    @Override
    public void update()
    {
        if (!this.world.isRemote && this.corner == 0)
        {
            final ArrayList<TileEntity> attachedLaunchPads = new ArrayList<>();

            for (int x = this.getPos().getX() - 1; x < this.getPos().getX() + 2; x++)
            {
                for (int z = this.getPos().getZ() - 1; z < this.getPos().getZ() + 2; z++)
                {
                    final TileEntity tile = this.world.getTileEntity(new BlockPos(x, this.getPos().getY(), z));

                    if (tile instanceof TileEntityLandingPadSingle && !tile.isInvalid() && ((TileEntityLandingPadSingle) tile).corner == 0)
                    {
                        attachedLaunchPads.add(tile);
                    }
                }
            }

            if (attachedLaunchPads.size() == 9)
            {
                for (final TileEntity tile : attachedLaunchPads)
                {
                    this.world.markTileEntityForRemoval(tile);
                    ((TileEntityLandingPadSingle) tile).corner = 1;
                }

                this.world.setBlockState(this.getPos(), GCBlocks.landingPadFull.getDefaultState(), 2);
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }
}
