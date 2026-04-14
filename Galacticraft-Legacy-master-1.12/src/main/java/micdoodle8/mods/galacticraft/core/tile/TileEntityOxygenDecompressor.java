/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.tile;

import java.util.EnumSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

import micdoodle8.mods.galacticraft.annotations.ForRemoval;
import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite;
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank;

public class TileEntityOxygenDecompressor extends TileEntityOxygen implements IInventoryDefaults, ISidedInventory
{

    public static final int OUTPUT_PER_TICK = 100;
    private boolean usingEnergy = false;

    public TileEntityOxygenDecompressor()
    {
        super("container.oxygendecompressor.name", 1200, 0);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    }

    @Override
    public void update()
    {
        super.update();

        if (!this.world.isRemote)
        {
            this.usingEnergy = false;
            ItemStack tank1 = this.getInventory().get(0);

            if (!tank1.isEmpty() && this.hasEnoughEnergyToRun && this.getOxygenStored() < this.getMaxOxygenStored())
            {
                if (tank1.getItem() instanceof ItemOxygenTank && tank1.getItemDamage() < tank1.getMaxDamage())
                {
                    tank1.setItemDamage(tank1.getItemDamage() + 1);
                    this.receiveOxygen(1, true);
                    this.usingEnergy = true;
                } else if (tank1.getItem() instanceof ItemCanisterOxygenInfinite)
                {
                    this.receiveOxygen(1, true);
                    this.usingEnergy = true;
                }
            }

            this.produceOxygen();
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    // ISidedInventory Implementation:

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[]
        {0, 1};
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, EnumFacing side)
    {
        if (this.isItemValidForSlot(slotID, itemstack))
        {
            switch (slotID)
            {
                case 0:
                    return itemstack.getItemDamage() < itemstack.getMaxDamage();
                case 1:
                    return ItemElectricBase.isElectricItemCharged(itemstack);
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side)
    {
        if (this.isItemValidForSlot(slotID, itemstack))
        {
            switch (slotID)
            {
                case 0:
                    return itemstack.getItemDamage() == itemstack.getMaxDamage();
                case 1:
                    return ItemElectricBase.isElectricItemEmpty(itemstack);
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        switch (slotID)
        {
            case 0:
                return itemstack.getItem() instanceof ItemOxygenTank;
            case 1:
                return ItemElectricBase.isElectricItem(itemstack.getItem());
        }

        return false;
    }

    @Override
    public boolean shouldUseEnergy()
    {
        return this.usingEnergy;
    }

    @Override
    public EnumFacing byIndex()
    {
        IBlockState state = this.world.getBlockState(getPos());
        if (state.getBlock() instanceof BlockOxygenCompressor)
        {
            return state.getValue(BlockOxygenCompressor.FACING).rotateY();
        }
        return EnumFacing.NORTH;
    }

    @Override
    public EnumFacing getElectricInputDirection()
    {
        return this.byIndex();
    }

    @Override
    public ItemStack getBatteryInSlot()
    {
        return this.getStackInSlot(1);
    }

    public EnumFacing getOxygenOutputDirection()
    {
        return this.getElectricInputDirection().getOpposite();
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
    public int getOxygenProvide(EnumFacing direction)
    {
        return this.getOxygenOutputDirections().contains(direction) ? Math.min(TileEntityOxygenDecompressor.OUTPUT_PER_TICK, this.getOxygenStored()) : 0;
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
