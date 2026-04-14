/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.mars.tile;

import java.util.ArrayList;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import micdoodle8.mods.galacticraft.annotations.ForRemoval;
import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.api.tile.IDisableableMachine;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IOxygenReceiver;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory;
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork;
import micdoodle8.mods.galacticraft.core.fluid.NetworkHelper;
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.FluidUtil;
import micdoodle8.mods.galacticraft.core.wrappers.FluidHandlerWrapper;
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMarsT2;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;

@Interface(modid = CompatibilityManager.modidMekanism, iface = "mekanism.api.gas.IGasHandler")
public class TileEntityGasLiquefier extends TileBaseElectricBlockWithInventory implements IDisableableMachine, IFluidHandlerWrapper, IOxygenReceiver, IGasHandler
{

    private final int tankCapacity = 2000;
    public static final int OUTPUT_PER_SECOND = 1;
    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank gasTank = new FluidTank(this.tankCapacity * 2);
    @NetworkedField(targetSide = Side.CLIENT)
    public FluidTank liquidTank = new FluidTank(this.tankCapacity);

    public int processTimeRequired = 3;
    @NetworkedField(targetSide = Side.CLIENT)
    public int processTicks = -10;
    private int airProducts = -1;

    @NetworkedField(targetSide = Side.CLIENT)
    public int gasTankType = -1;
    @NetworkedField(targetSide = Side.CLIENT)
    public int fluidTankType = -1;

    public enum TankGases
    {

        METHANE(0, "methane", ConfigManagerCore.useOldFuelFluidID ? "fuelgc" : "fuel"), OXYGEN(1, "oxygen", "liquidoxygen"), NITROGEN(2, "nitrogen", "liquidnitrogen"), ARGON(3, "argon", "liquidargon"), AIR(4, "atmosphericgases", "xxyyzz");

        int index;
        String gas;
        String liquid;

        TankGases(int id, String fluidname, String outputname)
        {
            this.index = id;
            this.gas = fluidname;
            this.liquid = outputname;
        }
    }

    public TileEntityGasLiquefier()
    {
        super("tile.mars_machine.4.name");
        this.storage.setMaxExtract(ConfigManagerCore.hardMode ? 90 : 60);
        this.setTierGC(2);
        this.inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;

        if (CompatibilityManager.isMekanismLoaded())
        {
            if (capability == Capabilities.GAS_HANDLER_CAPABILITY)
                return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidHandlerWrapper(this, facing));
        }

        if (CompatibilityManager.isMekanismLoaded())
        {
            if (capability == Capabilities.GAS_HANDLER_CAPABILITY)
            {
                return Capabilities.GAS_HANDLER_CAPABILITY.cast(this);
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
    }

    @Override
    public void update()
    {
        super.update();

        if (this.airProducts == -1)
        {
            this.airProducts = this.getAirProducts();
        }

        if (!this.world.isRemote)
        {
            FluidStack currentgas = this.gasTank.getFluid();
            if (currentgas == null || currentgas.amount <= 0)
            {
                this.gasTankType = -1;
            } else
            {
                this.gasTankType = this.getIdFromName(currentgas.getFluid().getName());
            }

            // If somehow it has air in an airless dimension, flush it out
            if (this.airProducts == 0 && this.gasTankType == TankGases.AIR.index)
            {
                this.gasTank.drain(this.gasTank.getFluidAmount(), true);
            }

            FluidStack currentLiquid = this.liquidTank.getFluid();
            if (currentLiquid == null || currentLiquid.amount == 0)
            {
                this.fluidTankType = -1;
            } else
            {
                this.fluidTankType = this.getProductIdFromName(currentLiquid.getFluid().getName());
            }

            // First, see if any gas needs to be put into the gas storage
            ItemStack inputCanister = getInventory().get(1);
            if (!inputCanister.isEmpty())
            {
                if (inputCanister.getItem() instanceof ItemAtmosphericValve && this.airProducts > 0)
                {
                    // Air -> Air tank
                    if (this.gasTankType == -1 || (this.gasTankType == TankGases.AIR.index && this.gasTank.getFluid().amount < this.gasTank.getCapacity()))
                    {
                        IBlockState stateAbove = this.world.getBlockState(getPos().up());
                        if (stateAbove.getMaterial() == Material.AIR && stateAbove.getBlock() != GCBlocks.breatheableAir && stateAbove.getBlock() != GCBlocks.brightBreatheableAir)
                        {
                            FluidStack gcAtmosphere = FluidRegistry.getFluidStack(TankGases.AIR.gas, 4);
                            this.gasTank.fill(gcAtmosphere, true);
                            this.gasTankType = TankGases.AIR.index;
                        }
                    }
                } else if (inputCanister.getItem() instanceof ItemCanisterGeneric)
                {
                    int amount = ItemCanisterGeneric.EMPTY - inputCanister.getItemDamage();
                    if (amount > 0)
                    {
                        Item canisterType = inputCanister.getItem();
                        FluidStack canisterGas = null;
                        int factor = 1;
                        if (this.gasTankType <= 0 && canisterType == AsteroidsItems.methaneCanister)
                        {
                            this.gasTankType = TankGases.METHANE.index;
                            canisterGas = FluidRegistry.getFluidStack(TankGases.METHANE.gas, amount);
                        }
                        if ((this.gasTankType == TankGases.OXYGEN.index || this.gasTankType == -1) && canisterType == AsteroidsItems.canisterLOX)
                        {
                            this.gasTankType = TankGases.OXYGEN.index;
                            canisterGas = FluidRegistry.getFluidStack(TankGases.OXYGEN.gas, amount * 2);
                            factor = 2;
                        }
                        if ((this.gasTankType == TankGases.NITROGEN.index || this.gasTankType == -1) && canisterType == AsteroidsItems.canisterLN2)
                        {
                            this.gasTankType = TankGases.NITROGEN.index;
                            canisterGas = FluidRegistry.getFluidStack(TankGases.NITROGEN.gas, amount * 2);
                            factor = 2;
                        }

                        if (canisterGas != null)
                        {
                            int used = this.gasTank.fill(canisterGas, true) / factor;
                            if (used == amount)
                            {
                                getInventory().set(1, new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY));
                            } else
                            {
                                getInventory().set(1, new ItemStack(canisterType, 1, ItemCanisterGeneric.EMPTY - amount + used));
                            }
                        }
                    }
                } else
                {
                    FluidStack liquid = net.minecraftforge.fluids.FluidUtil.getFluidContained(inputCanister);
                    if (liquid != null && liquid.amount > 0)
                    {
                        String inputName = FluidRegistry.getFluidName(liquid);
                        // Methane -> Methane tank
                        if (this.gasTankType <= 0 && inputName.contains("methane"))
                        {
                            if (currentgas == null || currentgas.amount + liquid.amount <= this.gasTank.getCapacity())
                            {
                                FluidStack gcMethane = FluidRegistry.getFluidStack(TankGases.METHANE.gas, liquid.amount);
                                this.gasTank.fill(gcMethane, true);
                                this.gasTankType = 0;
                                ItemStack stack = FluidUtil.getUsedContainer(inputCanister);
                                getInventory().set(1, stack == null ? ItemStack.EMPTY : stack);
                            }
                            // Oxygen -> Oxygen tank
                        } else if ((this.gasTankType == TankGases.OXYGEN.index || this.gasTankType == -1) && inputName.contains("oxygen"))
                        {
                            int tankedAmount = liquid.amount * (inputName.contains("liquid") ? 2 : 1);
                            if (currentgas == null || currentgas.amount + tankedAmount <= this.gasTank.getCapacity())
                            {
                                FluidStack gcgas = FluidRegistry.getFluidStack(TankGases.OXYGEN.gas, tankedAmount);
                                this.gasTank.fill(gcgas, true);
                                this.gasTankType = TankGases.OXYGEN.index;
                                ItemStack stack = FluidUtil.getUsedContainer(inputCanister);
                                getInventory().set(1, stack == null ? ItemStack.EMPTY : stack);
                            }
                            // Nitrogen -> Nitrogen tank
                        } else if ((this.gasTankType == TankGases.NITROGEN.index || this.gasTankType == -1) && inputName.contains("nitrogen"))
                        {
                            int tankedAmount = liquid.amount * (inputName.contains("liquid") ? 2 : 1);
                            if (currentgas == null || currentgas.amount + tankedAmount <= this.gasTank.getCapacity())
                            {
                                FluidStack gcgas = FluidRegistry.getFluidStack(TankGases.NITROGEN.gas, tankedAmount);
                                this.gasTank.fill(gcgas, true);
                                this.gasTankType = TankGases.NITROGEN.index;
                                ItemStack stack = FluidUtil.getUsedContainer(inputCanister);
                                getInventory().set(1, stack == null ? ItemStack.EMPTY : stack);
                            }
                        }
                    }
                }
            }

            // Now see if any liquids from the output tanks need to be put into the output slot
            checkFluidTankTransfer(2, this.liquidTank);

            if (this.hasEnoughEnergyToRun && this.canProcess())
            {
                // 50% extra speed boost for Tier 2 machine if powered by Tier 2 power
                if (this.tierGC == 2)
                {
                    this.processTimeRequired = Math.max(1, 4 - this.poweredByTierGC);
                }

                if (this.processTicks <= 0)
                {
                    this.processTicks = this.processTimeRequired;
                } else
                {
                    if (--this.processTicks <= 0)
                    {
                        this.doLiquefaction();
                        this.processTicks = this.canProcess() ? this.processTimeRequired : 0;
                    }
                }
            } else
            {
                if (this.processTicks > 0)
                {
                    this.processTicks = 0;
                } else if (--this.processTicks <= -10)
                {
                    this.processTicks = -10;
                }
            }
            this.produceOutput(this.getGasInputDirection().getOpposite());

        }

    }

    private void checkFluidTankTransfer(int slot, FluidTank tank)
    {
        if (FluidUtil.isValidContainer(this.getInventory().get(slot)))
        {
            final FluidStack liquid = tank.getFluid();

            if (liquid != null && liquid.amount > 0)
            {
                String liquidname = liquid.getFluid().getName();
                if (liquidname.startsWith("fuel"))
                {
                    FluidUtil.tryFillContainerFuel(tank, this.getInventory(), slot);
                } else if (liquidname.equals(TankGases.OXYGEN.liquid))
                {
                    FluidUtil.tryFillContainer(tank, liquid, this.getInventory(), slot, AsteroidsItems.canisterLOX);
                } else if (liquidname.equals(TankGases.NITROGEN.liquid))
                {
                    FluidUtil.tryFillContainer(tank, liquid, this.getInventory(), slot, AsteroidsItems.canisterLN2);
                }
            }
        } else if (!this.getInventory().get(slot).isEmpty() && this.getInventory().get(slot).getItem() instanceof ItemAtmosphericValve)
        {
            tank.drain(4, true);
        }
    }

    public int getIdFromName(String gasname)
    {
        for (TankGases type : TankGases.values())
        {
            if (type.gas.equals(gasname))
            {
                return type.index;
            }
        }

        return -1;
    }

    public int getProductIdFromName(String gasname)
    {
        for (TankGases type : TankGases.values())
        {
            if (type.liquid.equals(gasname))
            {
                return type.index;
            }
        }

        return -1;
    }

    public int getScaledGasLevel(int i)
    {
        return this.gasTank.getFluid() != null ? this.gasTank.getFluid().amount * i / this.gasTank.getCapacity() : 0;
    }

    public int getScaledFuelLevel(int i)
    {
        return this.liquidTank.getFluid() != null ? this.liquidTank.getFluid().amount * i / this.liquidTank.getCapacity() : 0;
    }

    public boolean canProcess()
    {
        if (this.gasTank.getFluid() == null || this.gasTank.getFluid().amount <= 0 || this.getDisabled(0))
        {
            return false;
        }

        if (this.fluidTankType == -1)
        {
            return true;
        }

        boolean tank1HasSpace = this.liquidTank.getFluidAmount() < this.liquidTank.getCapacity();

        if (this.gasTankType == TankGases.AIR.index)
        {
            int airProducts = this.airProducts;
            do
            {
                int thisProduct = (airProducts & 15) - 1;
                if (thisProduct == this.fluidTankType && tank1HasSpace)
                {
                    return true;
                }
                airProducts = airProducts >> 4;
            } while (airProducts > 0);
            return false;
        }

        return (this.gasTankType == this.fluidTankType && tank1HasSpace);
    }

    public int getAirProducts()
    {
        WorldProvider WP = this.world.provider;
        if (WP instanceof IGalacticraftWorldProvider)
        {
            int result = 0;
            ArrayList<EnumAtmosphericGas> atmos = ((IGalacticraftWorldProvider) WP).getCelestialBody().atmosphere.composition;
            if (atmos.size() > 0)
            {
                result = this.getIdFromName(atmos.get(0).name().toLowerCase()) + 1;
            }
            if (atmos.size() > 1)
            {
                result += 16 * (this.getIdFromName(atmos.get(1).name().toLowerCase()) + 1);
            }
            if (atmos.size() > 2)
            {
                result += 256 * (this.getIdFromName(atmos.get(2).name().toLowerCase()) + 1);
            }

            return result;
        }

        return 35;
    }

    public void doLiquefaction()
    {
        // Can't be called if the gasTank fluid is null
        final int gasAmount = this.gasTank.getFluid().amount;
        if (gasAmount == 0)
        {
            return;
        }

        if (this.gasTankType == TankGases.AIR.index)
        {
            int airProducts = this.airProducts;
            int amountToDrain = Math.min(gasAmount / 2, (airProducts > 15) ? 2 : 3);
            if (amountToDrain == 0)
            {
                amountToDrain = 1;
            }

            do
            {
                int thisProduct = (airProducts & 15) - 1;
                // -1 indicates a gas which can't be liquefied (e.g. Carbon Dioxide)
                if (thisProduct >= 0)
                {
                    this.gasTank.drain(this.placeIntoFluidTanks(thisProduct, amountToDrain) * 2, true);
                }
                airProducts = airProducts >> 4;
                amountToDrain = amountToDrain >> 1;
                if (amountToDrain == 0)
                {
                    amountToDrain = 1;
                }
            } while (airProducts > 0);
        } else
        {
            if (gasAmount == 1)
            {
                this.gasTank.drain(this.placeIntoFluidTanks(this.gasTankType, 1), true);
            } else
            {
                this.gasTank.drain(this.placeIntoFluidTanks(this.gasTankType, Math.min(gasAmount / 2, 3)) * 2, true);
            }
        }
    }

    private int placeIntoFluidTanks(int thisProduct, int amountToDrain)
    {
        final int fuelSpace = this.liquidTank.getCapacity() - this.liquidTank.getFluidAmount();

        if ((thisProduct == this.fluidTankType || this.fluidTankType == -1) && fuelSpace > 0)
        {
            if (amountToDrain > fuelSpace)
            {
                amountToDrain = fuelSpace;
            }
            this.liquidTank.fill(FluidRegistry.getFluidStack(TankGases.values()[thisProduct].liquid, amountToDrain), true);
            this.fluidTankType = thisProduct;
        } else
        {
            amountToDrain = 0;
        }
        return amountToDrain;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.processTicks = nbt.getInteger("smeltingTicks");

        if (nbt.hasKey("gasTank"))
        {
            this.gasTank.readFromNBT(nbt.getCompoundTag("gasTank"));
        }

        if (nbt.hasKey("liquidTank"))
        {
            this.liquidTank.readFromNBT(nbt.getCompoundTag("liquidTank"));
        }

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("smeltingTicks", this.processTicks);

        if (this.gasTank.getFluid() != null)
        {
            nbt.setTag("gasTank", this.gasTank.writeToNBT(new NBTTagCompound()));
        }

        if (this.liquidTank.getFluid() != null)
        {
            nbt.setTag("liquidTank", this.liquidTank.writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    @Override
    public boolean hasCustomName()
    {
        return true;
    }

    // ISidedInventory Implementation:

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
       
       
        return side == EnumFacing.UP ? new int[] { 0 } : new int[] { 0, 1, 2 };
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, EnumFacing side)
    {
        if (this.isItemValidForSlot(slotID, itemstack))
        {
            switch (slotID)
            {
                case 0:
                    return ItemElectricBase.isElectricItemCharged(itemstack);
                case 1:
                    return FluidUtil.isMethaneContainerAny(itemstack);
                case 2:
                    return FluidUtil.isEmptyContainerFor(itemstack, this.liquidTank.getFluid());

                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side)
    {
        switch (slotID)
        {
            case 0:
                return ItemElectricBase.isElectricItemEmpty(itemstack);
            case 1:
                return FluidUtil.isEmptyContainer(itemstack);
            case 2:
        
                return FluidUtil.isFullContainer(itemstack);

            default:
                return false;
        }
    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        switch (slotID)
        {
            case 0:
                return ItemElectricBase.isElectricItem(itemstack.getItem());
            case 1:
            case 2:
            
                return FluidUtil.isValidContainer(itemstack);
        }

        return false;
    }

    @Override
    public boolean shouldUseEnergy()
    {
        return this.canProcess();
    }

    @Override
    public double getPacketRange()
    {
        return 320.0D;
    }

    @Override
    public EnumFacing getElectricInputDirection()
    {
        return EnumFacing.DOWN;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid)
    {


        if (from == getGasInputDirection().getOpposite())
        {
            return this.liquidTank.getFluid() != null && this.liquidTank.getFluidAmount() > 0;
        }

        return false;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
    {


        if (from == getGasInputDirection().getOpposite())
        {
            if (resource != null && resource.isFluidEqual(this.liquidTank.getFluid()))
            {
                return this.liquidTank.drain(resource.amount, doDrain);
            }
        }

        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
    {


        if (from == getGasInputDirection().getOpposite())
        {
            return this.liquidTank.drain(maxDrain, doDrain);
        }

        return null;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid)
    {
        if (from.equals(this.getGasInputDirection()))
        {
            // Can fill with gases
            return fluid == null || this.getIdFromName(fluid.getName()) > -1;
        }

        return false;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill)
    {
        int used = 0;

        if (resource != null && this.canFill(from, resource.getFluid()))
        {
            int type = this.getIdFromName(FluidRegistry.getFluidName(resource));

            if (this.gasTankType == -1 || (this.gasTankType == type && this.gasTank.getFluidAmount() < this.gasTank.getCapacity()))
            {
                used = this.gasTank.fill(resource, doFill);
            }
        }

        return used;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from)
    {
        FluidTankInfo[] tankInfo = new FluidTankInfo[] {};

        if (from == this.getGasInputDirection())
        {
            tankInfo = new FluidTankInfo[] {new FluidTankInfo(this.gasTank)};
        }

        if (getGasInputDirection().getOpposite() == from)
        {
            tankInfo = new FluidTankInfo[] {new FluidTankInfo(this.liquidTank)};
        }

        return tankInfo;
    }

    @Override
    public int getBlockMetadata()
    {
        return getBlockType().getMetaFromState(this.world.getBlockState(getPos())) & 3;
    }

    @Override
    public boolean shouldPullOxygen()
    {
        return this.gasTankType == -1 || (this.gasTankType == 1 && this.gasTank.getFluidAmount() < this.gasTank.getCapacity());
    }

    @Override
    public int receiveOxygen(EnumFacing from, int receive, boolean doReceive)
    {
        if (from == this.getGasInputDirection() && this.shouldPullOxygen())
        {
            // Special conversion ratio for breathable air
            float conversion = 2F * Constants.LOX_GAS_RATIO;
            FluidStack fluidToFill = new FluidStack(AsteroidsModule.fluidOxygenGas, (int) (receive * conversion));
            int used = MathHelper.ceil(this.gasTank.fill(fluidToFill, doReceive) / conversion);
            return used;
        }

        return 0;
    }

    @Override
    public int provideOxygen(EnumFacing from, int request, boolean doProvide)
    {
        return 0;
    }

    @Override
    public int getOxygenRequest(EnumFacing direction)
    {
        return this.receiveOxygen(direction, 1000000, false);
    }

    @Override
    public int getOxygenProvide(EnumFacing direction)
    {
        return 0;
    }

    @Override
    public boolean canConnect(EnumFacing direction, NetworkType type)
    {
        if (direction == null)
        {
            return false;
        }

        if (type == NetworkType.FLUID)
        {
            return direction == getGasInputDirection() || direction == this.getGasInputDirection().getOpposite() || direction == this.getGasInputDirection().rotateY();
        }

        if (type == NetworkType.POWER)
        {
            return direction == EnumFacing.DOWN;
        }

        return false;
    }

    @Override
    public EnumFacing byIndex()
    {
        IBlockState state = this.world.getBlockState(getPos());
        if (state.getBlock() instanceof BlockMachineMarsT2)
        {
            return state.getValue(BlockMachineMarsT2.FACING);
        }
        return EnumFacing.NORTH;
    }

    public EnumFacing getGasInputDirection()
    {
        return this.byIndex().rotateY();
    }

    private boolean produceOutput(EnumFacing outputDirection)
    {
        // Used produceOutput() function from TileEntityMethaneSynthesizer as reference for this one

        int provide = Math.min(this.liquidTank.getFluidAmount(), TileEntityGasLiquefier.OUTPUT_PER_SECOND);

        if (provide > 0)
        {
            TileEntity outputTile = new BlockVec3(this).getTileEntityOnSide(this.world, outputDirection);
            FluidNetwork outputNetwork = NetworkHelper.getFluidNetworkFromTile(outputTile, outputDirection);

            if (outputNetwork != null)
            {
                int fuelRequested = outputNetwork.getRequest();

                if (fuelRequested > 0)
                {
                    int usedFuel = outputNetwork.emitToBuffer(new FluidStack(this.liquidTank.getFluid(), provide), true);
                    this.liquidTank.drain(usedFuel, true);

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    @Method(modid = "mekanism")
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer)
    {
        if (!stack.getGas().getName().equals("oxygen") || !this.shouldPullOxygen())
        {
            return 0;
        }
        int used = 0;
        if (this.gasTank.getFluidAmount() < this.gasTank.getCapacity())
        {
            used = this.gasTank.fill(FluidRegistry.getFluidStack("oxygen", stack.amount), doTransfer);
        }
        return used;
    }

    @Method(modid = "mekanism")
    public int receiveGas(EnumFacing side, GasStack stack)
    {
        return this.receiveGas(side, stack, true);
    }

    @Override
    @Method(modid = "mekanism")
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer)
    {
        return null;
    }

    @Method(modid = "mekanism")
    public GasStack drawGas(EnumFacing side, int amount)
    {
        return null;
    }

    @Override
    @Method(modid = "mekanism")
    public boolean canReceiveGas(EnumFacing side, Gas type)
    {
        return this.shouldPullOxygen() && type.getName().equals("oxygen") && side.equals(this.getGasInputDirection());
    }

    @Override
    @Method(modid = "mekanism")
    public boolean canDrawGas(EnumFacing side, Gas type)
    {
        return false;
    }

    @Method(modid = "mekanism")
    public boolean canTubeConnect(EnumFacing side)
    {
        return side.equals(this.getGasInputDirection());
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