/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */
package micdoodle8.mods.galacticraft.core.tile;

import java.util.EnumSet;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;

import micdoodle8.mods.miccore.Annotations.NetworkedField;

import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectricalSource;
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults;
import micdoodle8.mods.galacticraft.core.util.ItemUtil;

public class TileEntityCoalGenerator extends TileBaseUniversalElectricalSource implements IInventoryDefaults, ISidedInventory, IConnector
{
    private final int dustCoalCookTime  = 220;
    private final int itemCoalCookTime  = 320;
    private final int blockCoalCookTime = 3200;
    //
    private final int dustCharcoalCookTime  = 120;
    private final int itemCharcoalCookTime  = 160;
    private final int blockCharcoalCookTime = 1600;
    // New energy rates:
    //
    // Tier 1 machine typically consumes 600 gJ/s = 30 gJ/t
    // Coal generator on max heat can power up to 4 Tier 1 machines
    // (fewer if one of them is an Electric Furnace)
    // Basic solar gen in full sun can power 1 Tier 1 machine
    // 1 lump of coal is equivalent to 38400 gJ
    // because on max heat it produces 120 gJ/t over 320 ticks
    // Below the min_generate, all heat is wasted
    // At max generate, 100% efficient conversion coal energy -> electric makes
    // 120 gJ/t
    public static final int    MAX_GENERATE_GJ_PER_TICK = 150;
    public static final int    MIN_GENERATE_GJ_PER_TICK = 30;
    private static final float BASE_ACCELERATION        = 0.3f;
    public float               prevGenerateWatts        = 0;
    @NetworkedField(targetSide = Side.CLIENT)
    public float               heatGJperTick            = 0;
    @NetworkedField(targetSide = Side.CLIENT)
    public int                 itemCookTime             = 0;

    public TileEntityCoalGenerator()
    {
        super("tile.machine.0.name");
        this.storage.setMaxExtract(TileEntityCoalGenerator.MAX_GENERATE_GJ_PER_TICK - TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK);
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    public void update()
    {
        if (!this.world.isRemote && this.heatGJperTick - TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK > 0)
        {
            this.receiveEnergyGC(null, (this.heatGJperTick - TileEntityCoalGenerator.MIN_GENERATE_GJ_PER_TICK), false);
        }
        super.update();
        if (!this.world.isRemote)
        {
            if (this.itemCookTime > 0)
            {
                this.itemCookTime--;
                this.heatGJperTick = Math.min(this.heatGJperTick + Math.max(this.heatGJperTick * 0.005F, TileEntityCoalGenerator.BASE_ACCELERATION), TileEntityCoalGenerator.MAX_GENERATE_GJ_PER_TICK);
            }
            if (this.itemCookTime <= 0 && !this.getInventory().get(0).isEmpty())
            {
                ItemStack stack = this.getInventory().get(0);
                if (stack.getCount() > 0)
                {
                    if (ItemUtil.hasOreDictSuffix(stack, "charcoal"))
                    {
                        if (ItemUtil.isBlock(stack))
                        {
                            this.handleFuel(blockCharcoalCookTime);
                        } else if (ItemUtil.isDust(stack))
                        {
                            this.handleFuel(dustCharcoalCookTime);
                        } else
                        {
                            this.handleFuel(itemCharcoalCookTime);
                        }
                    } else if (ItemUtil.hasOreDictSuffix(stack, "coal"))
                    {
                        if (ItemUtil.isBlock(stack))
                        {
                            this.handleFuel(blockCoalCookTime);
                        } else if (ItemUtil.isDust(stack))
                        {
                            this.handleFuel(dustCoalCookTime);
                        } else
                        {
                            this.handleFuel(itemCoalCookTime);
                        }
                    } else
                    {
                        if (stack.getItem() == Items.COAL)
                        {
                            this.itemCookTime = 320;
                            this.decrStackSize(0, 1);
                        } else if (stack.getItem() == Item.getItemFromBlock(Blocks.COAL_BLOCK) && stack.getCount() > 0)
                        {
                            this.itemCookTime = 320 * 10;
                            this.decrStackSize(0, 1);
                        }
                    }
                }
            }
            this.produce();
            if (this.itemCookTime <= 0)
            {
                this.heatGJperTick = Math.max(this.heatGJperTick - 0.3F, 0);
            }
            this.heatGJperTick = Math.min(Math.max(this.heatGJperTick, 0.0F), this.getMaxEnergyStoredGC());
        }
    }

    private void handleFuel(int cookTime)
    {
        this.itemCookTime = cookTime;
        this.decrStackSize(0, 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.itemCookTime = nbt.getInteger("itemCookTime");
        this.heatGJperTick = nbt.getInteger("generateRateInt");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("itemCookTime", this.itemCookTime);
        nbt.setFloat("generateRate", this.heatGJperTick);
        return nbt;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        //        if (itemstack.getItem() != Items.COAL || itemstack.getItem() != Item.getItemFromBlock(Blocks.COAL_BLOCK))
        //        {
        //            return ItemUtil.hasOreDictSuffix(itemstack, "coal") || ItemUtil.hasOreDictSuffix(itemstack, "charcoal");
        //        } else
        //        {
        //            return itemstack.getItem() == Item.getItemFromBlock(Blocks.COAL_BLOCK);
        //        }
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[] {0};
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing direction)
    {
        return slotID == 0;
    }

    @Override
    public float receiveElectricity(EnumFacing from, float energy, int tier, boolean doReceive)
    {
        return 0;
    }

    @Override
    public EnumSet<EnumFacing> getElectricalInputDirections()
    {
        return EnumSet.noneOf(EnumFacing.class);
    }

    @Override
    public EnumSet<EnumFacing> getElectricalOutputDirections()
    {
        return EnumSet.of(this.getElectricOutputDirection());
    }

    public EnumFacing byIndex()
    {
        return BlockMachineBase.byIndex(this.world.getBlockState(getPos()));
    }

    @Override
    public EnumFacing getElectricOutputDirection()
    {
        return byIndex().rotateY();
    }

    @Override
    public boolean canConnect(EnumFacing direction, NetworkType type)
    {
        if (direction == null || type != NetworkType.POWER)
        {
            return false;
        }
        return direction == this.getElectricOutputDirection();
    }
}
