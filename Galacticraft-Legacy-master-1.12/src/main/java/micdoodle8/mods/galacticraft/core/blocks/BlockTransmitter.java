/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.NetworkRegistry;

import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection;
import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.OxygenUtil;

public abstract class BlockTransmitter extends BlockAdvanced
{

    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");

    public BlockTransmitter(Material material)
    {
        super(material);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        TileEntity tile = worldIn.getTileEntity(pos);

        GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_UPDATE_WIRE_BOUNDS, GCCoreUtil.getDimensionID((World) worldIn), new Object[]
        {pos}), new NetworkRegistry.TargetPoint(GCCoreUtil.getDimensionID((World) worldIn), pos.getX(), pos.getY(), pos.getZ(), 10.0D));

        if (tile instanceof INetworkConnection)
        {
            ((INetworkConnection) tile).refresh();
        }
    }

    public abstract NetworkType getNetworkType(IBlockState state);

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof ITransmitter)
        {
            TileEntity[] connectable = new TileEntity[6];
            switch (this.getNetworkType(state))
            {
                case FLUID:
                    connectable = OxygenUtil.getAdjacentFluidConnections(tileEntity);
                    break;
                case POWER:
                    connectable = EnergyUtil.getAdjacentPowerConnections(tileEntity);
                    break;
                default:
                    break;
            }

            return state.withProperty(DOWN, Boolean.valueOf(connectable[EnumFacing.DOWN.ordinal()] != null)).withProperty(UP, Boolean.valueOf(connectable[EnumFacing.UP.ordinal()] != null))
                .withProperty(NORTH, Boolean.valueOf(connectable[EnumFacing.NORTH.ordinal()] != null)).withProperty(EAST, Boolean.valueOf(connectable[EnumFacing.EAST.ordinal()] != null))
                .withProperty(SOUTH, Boolean.valueOf(connectable[EnumFacing.SOUTH.ordinal()] != null)).withProperty(WEST, Boolean.valueOf(connectable[EnumFacing.WEST.ordinal()] != null));
        }

        return state;
    }
}
