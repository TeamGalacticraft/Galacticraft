/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti;

public interface IMultiBlock extends ITickable
{

    /**
     * Called when activated
     */
    boolean onActivated(EntityPlayer entityPlayer);

    /**
     * Called when this multiblock is created
     *
     * @param placedPosition - The position the block was placed at
     */
    void onCreate(World world, BlockPos placedPosition);

    /**
     * Called when one of the multiblocks of this block is destroyed
     *
     * @param callingBlock - The tile entity who called the onDestroy function
     */
    void onDestroy(TileEntity callingBlock);

    void getPositions(BlockPos placedPosition, List<BlockPos> positions);

    BlockMulti.EnumBlockMultiType getMultiType();
}
