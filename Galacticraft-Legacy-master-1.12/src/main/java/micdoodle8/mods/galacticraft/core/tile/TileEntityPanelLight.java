/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.tile;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver;
import micdoodle8.mods.galacticraft.core.network.PacketDynamic;
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;

import io.netty.buffer.ByteBuf;

public class TileEntityPanelLight extends TileEntity implements IPacketReceiver
{

    public int meta;
    private IBlockState superState;
    private static IBlockState defaultLook = AsteroidBlocks.blockBasic.getStateFromMeta(6);
    public int color = 0xf0f0e0;
    @SideOnly(Side.CLIENT) private AxisAlignedBB renderAABB;

    public TileEntityPanelLight()
    {
    }

    public void initialise(int type, EnumFacing facing, EntityPlayer player, boolean isRemote, IBlockState superStateClient)
    {
        this.meta = facing.ordinal();
        if (isRemote)
        {
            this.superState = superStateClient;
            this.color = BlockPanelLighting.color;
        } else
        {
            GCPlayerStats stats = GCPlayerStats.get(player);
            this.superState = stats.getPanelLightingBases()[type];
            this.color = stats.getPanelLightingColor();
        }
    }

    public IBlockState getBaseBlock()
    {
        if (this.superState != null && this.superState.getBlock() == Blocks.AIR)
        {
            this.superState = null;
        }
        return this.superState == null ? defaultLook : this.superState;
    }

    public BlockPanelLighting.PanelType getType()
    {
        if (this.world != null)
        {
            IBlockState b = this.world.getBlockState(this.pos);
            if (b.getBlock() instanceof BlockPanelLighting)
            {
                return b.getValue(BlockPanelLighting.TYPE);
            }
        }
        return BlockPanelLighting.PanelType.SQUARE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (this.renderAABB == null)
        {
            this.renderAABB = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        }
        return this.renderAABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return Constants.RENDERDISTANCE_LONG;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.meta = nbt.getInteger("meta");
        if (nbt.hasKey("col"))
        {
            this.color = nbt.getInteger("col");
        }
        NBTTagCompound tag = nbt.getCompoundTag("sust");
        if (!tag.isEmpty())
        {
            this.superState = readBlockState(tag);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("meta", this.meta);
        nbt.setInteger("col", this.color);
        if (this.superState != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            writeBlockState(tag, this.superState);
            nbt.setTag("sust", tag);
        }
        return nbt;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    //TODO Create datafixer to transition to NBTUtil methods

    /**
     * Reads a blockstate from the given tag. In MC1.10+ use NBTUtil instead!
     */
    public static IBlockState readBlockState(NBTTagCompound tag)
    {
        if (!tag.hasKey("Name", 8))
        {
            return Blocks.AIR.getDefaultState();
        } else
        {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(tag.getString("Name")));

            if (tag.hasKey("Meta"))
            {
                int meta = tag.getInteger("Meta");
                if (meta >= 0 && meta < 16)
                {
                    return block.getStateFromMeta(meta);
                }
            }

            return block.getDefaultState();
        }
    }

    /**
     * Writes the given blockstate to the given tag. In MC1.10+ use NBTUtil
     * instead!
     */
    public static NBTTagCompound writeBlockState(NBTTagCompound tag, IBlockState state)
    {

        tag.setString("Name", Block.REGISTRY.getNameForObject(state.getBlock()).toString());
        tag.setInteger("Meta", state.getBlock().getMetaFromState(state));
        return tag;
    }

    public static IBlockState readBlockState(String name, Integer meta)
    {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
        if (block == null)
        {
            return Blocks.AIR.getDefaultState();
        }
        return block.getStateFromMeta(meta);
    }

    @Override
    public void onLoad()
    {
        if (this.world.isRemote)
        {
            // Request any networked information from server on first client update
            GalacticraftCore.packetPipeline.sendToServer(new PacketDynamic(this));
        }
    }

    @Override
    public void getNetworkedData(ArrayList<Object> sendData)
    {
        if (this.world.isRemote)
        {
            return;
        }

        sendData.add((byte) this.meta);
        sendData.add(this.color);
        if (this.superState != null)
        {
            Block block = this.superState.getBlock();
            if (block == Blocks.AIR)
            {
                this.superState = null;
                return;
            }

            sendData.add(Block.REGISTRY.getNameForObject(block).toString());
            sendData.add((byte) block.getMetaFromState(this.superState));
        }
    }

    @Override
    public void decodePacketdata(ByteBuf buffer)
    {
        if (this.world.isRemote)
        {
            try
            {
                this.meta = buffer.readByte();
                this.color = buffer.readInt();
                if (buffer.readableBytes() > 0)
                {
                    String name = ByteBufUtils.readUTF8String(buffer);
                    int otherMeta = buffer.readByte();
                    this.superState = readBlockState(name, otherMeta);
                    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean getEnabled()
    {
        return !RedstoneUtil.isBlockReceivingRedstone(this.world, this.getPos());
    }
}
