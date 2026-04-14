/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.tile;

import java.util.EnumSet;

import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;

import micdoodle8.mods.miccore.Annotations.NetworkedField;

import micdoodle8.mods.galacticraft.annotations.ForRemoval;
import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GCFluids;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCollector;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;

public class TileEntityOxygenCollector extends TileEntityOxygen
{

    public boolean active;
    public static final int OUTPUT_PER_TICK = 100;
    public static float OXYGEN_PER_PLANT = 0.75F;
    @NetworkedField(targetSide = Side.CLIENT) public float lastOxygenCollected;
    private boolean noAtmosphericOxygen = true;
    private boolean isInitialised = false;
    private boolean producedLastTick = false;

    public TileEntityOxygenCollector()
    {
        super("container.oxygencollector.name", 6000, 0);
        this.noRedstoneControl = true;
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    public int getCappedScaledOxygenLevel(int scale)
    {
        return (int) Math.max(Math.min(Math.floor((double) this.getOxygenStored() / (double) this.getMaxOxygenStored() * scale), scale), 0);
    }

    @Override
    public void update()
    {
        super.update();

        if (!this.world.isRemote)
        {
            producedLastTick = this.getOxygenStored() < this.getMaxOxygenStored();

            this.produceOxygen();

            // Approximately once every 40 ticks, search out oxygen producing
            // blocks
            if (this.world.rand.nextInt(10) == 0)
            {
                if (this.hasEnoughEnergyToRun)
                {
                    // The later calculations are more efficient if power is a
                    // float, so
                    // there are fewer casts
                    float nearbyLeaves = 0;

                    if (!this.isInitialised)
                    {
                        this.noAtmosphericOxygen =
                            (this.world.provider instanceof IGalacticraftWorldProvider && !((IGalacticraftWorldProvider) this.world.provider).isGasPresent(EnumAtmosphericGas.OXYGEN));
                        this.isInitialised = true;
                    }

                    if (this.noAtmosphericOxygen)
                    {
                        // Pre-test to see if close to the map edges, so code
                        // doesn't have to continually test for map edges inside
                        // the
                        // loop
                        if (this.getPos().getX() > -29999995 && this.getPos().getY() < 2999995 && this.getPos().getZ() > -29999995 && this.getPos().getZ() < 29999995)
                        {
                            // Test the y coordinates, so code doesn't have to
                            // keep
                            // testing that either
                            int miny = this.getPos().getY() - 5;
                            int maxy = this.getPos().getY() + 5;
                            if (miny < 0)
                            {
                                miny = 0;
                            }
                            if (maxy >= this.world.getHeight())
                            {
                                maxy = this.world.getHeight() - 1;
                            }

                            // Loop the x and the z first, so the y loop will be
                            // at
                            // fixed (x,z) coordinates meaning fixed chunk
                            // coordinates
                            for (int x = this.getPos().getX() - 5; x <= this.getPos().getX() + 5; x++)
                            {
                                int chunkx = x >> 4;
                                int intrachunkx = x & 15;
                                // Preload the first chunk for the z loop -
                                // there
                                // can be a maximum of 2 chunks in the z loop
                                int chunkz = this.getPos().getZ() - 5 >> 4;
                                Chunk chunk = this.world.getChunk(chunkx, chunkz);
                                for (int z = this.getPos().getZ() - 5; z <= this.getPos().getZ() + 5; z++)
                                {
                                    if (z >> 4 != chunkz)
                                    {
                                        // moved across z chunk boundary into a
                                        // new
                                        // chunk, so load the new chunk
                                        chunkz = z >> 4;
                                        chunk = this.world.getChunk(chunkx, chunkz);
                                    }
                                    for (int y = miny; y <= maxy; y++)
                                    {
                                        // chunk.getBlockID is like
                                        // world.getBlock
                                        // but faster - needs to be given
                                        // intra-chunk coordinates though
                                        final IBlockState state = chunk.getBlockState(intrachunkx, y, z & 15);
                                        // Test for the two most common blocks
                                        // (air
                                        // and breatheable air) without looking
                                        // up
                                        // in the blocksList
                                        if (!(state.getBlock() instanceof BlockAir))
                                        {
                                            BlockPos pos = new BlockPos(x, y, z);
                                            if (state.getBlock().isLeaves(state, this.world, pos)
                                                || state.getBlock() instanceof IPlantable && ((IPlantable) state.getBlock()).getPlantType(this.world, pos) == EnumPlantType.Crop)
                                            {
                                                nearbyLeaves += OXYGEN_PER_PLANT;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else
                    {
                        nearbyLeaves = 9.3F * 10F;
                    }

                    nearbyLeaves = (float) Math.floor(nearbyLeaves);

                    this.lastOxygenCollected = nearbyLeaves / 10F;

                    this.tank.setFluid(new FluidStack(GCFluids.fluidOxygenGas, (int) Math.max(Math.min(this.getOxygenStored() + nearbyLeaves, this.getMaxOxygenStored()), 0)));
                } else
                {
                    this.lastOxygenCollected = 0;
                }
            }
        }
    }

    // ISidedInventory Implementation:

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[]
        {0};
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side)
    {
        return slotID == 0;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemstack.getItem());
    }

    @Override
    public boolean shouldUseEnergy()
    {
        return this.getOxygenStored() > 0F && producedLastTick;
    }

    @Override
    public EnumFacing byIndex()
    {
        IBlockState state = this.world.getBlockState(getPos());
        if (state.getBlock() instanceof BlockOxygenCollector)
        {
            return state.getValue(BlockOxygenCollector.FACING);
        }
        return EnumFacing.NORTH;
    }

    @Override
    public EnumFacing getElectricInputDirection()
    {
        return byIndex().rotateY();
    }

    @Override
    public ItemStack getBatteryInSlot()
    {
        return this.getStackInSlot(0);
    }

    @Override
    public boolean shouldPullOxygen()
    {
        return false;
    }

    @Override
    public boolean shouldUseOxygen()
    {
        return false;
    }

    @Override
    public EnumSet<EnumFacing> getOxygenInputDirections()
    {
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public EnumSet<EnumFacing> getOxygenOutputDirections()
    {
        return EnumSet.of(this.getElectricInputDirection().getOpposite());
    }

    @Override
    public int getOxygenProvide(EnumFacing direction)
    {
        return this.getOxygenOutputDirections().contains(direction) ? Math.min(TileEntityOxygenStorageModule.OUTPUT_PER_TICK, this.getOxygenStored()) : 0;
    }
    
    @Override
    @Deprecated
    @ForRemoval(deadline = "4.1.0")
    @ReplaceWith("byIndex()")
    public EnumFacing getFront()
    {
        return this.byIndex();
    }
}
