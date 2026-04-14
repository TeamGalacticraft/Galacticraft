/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.energy.tile;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import javax.annotation.Nonnull;
import mekanism.api.energy.IStrictEnergyAcceptor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.Event;

@InterfaceList(value = {
    @Interface(iface = "net.minecraft.util.ITickable", modid = CompatibilityManager.modidIC2), 
    @Interface(iface = "ic2.api.energy.tile.IEnergyAcceptor", modid = CompatibilityManager.modidIC2),
    @Interface(iface = "ic2.api.energy.tile.IEnergyEmitter", modid = CompatibilityManager.modidIC2),
    @Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = CompatibilityManager.modidIC2),
    @Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = CompatibilityManager.modidMekanism),
    @Interface(iface = "buildcraft.api.mj.IMjReceiver", modid = CompatibilityManager.modBCraftEnergy)
})
public abstract class TileBaseUniversalConductor extends TileBaseConductor implements IEnergyStorage, ITickable, IMjReceiver, IEnergyAcceptor, IEnergyEmitter, IEnergySink, IStrictEnergyAcceptor
{

    protected boolean isAddedToEnergyNet;
    private float IC2surplusJoules = 0F;

    @Override
    public void onNetworkChanged()
    {
    }

    @Override
    public TileEntity[] getAdjacentConnections()
    {
        return EnergyUtil.getAdjacentPowerConnections(this);
    }

    @Override
    @Method(modid = CompatibilityManager.modidIC2)
    public void update()
    {
        if (!this.isAddedToEnergyNet)
        {
            if (!this.world.isRemote)
            {
                this.initIC();
            }

            this.isAddedToEnergyNet = true;
        }
    }

    @Override
    public void invalidate()
    {
        this.IC2surplusJoules = 0F;
        this.unloadTileIC2();
        super.invalidate();
    }

    @Override
    public void onChunkUnload()
    {
        this.unloadTileIC2();
        super.onChunkUnload();
    }

    protected void initIC()
    {
        if (EnergyConfigHandler.isIndustrialCraft2Loaded() && !this.world.isRemote)
        {
            try
            {
                Object o = CompatibilityManager.classIC2tileEventLoad.getConstructor(IEnergyTile.class).newInstance(this);

                if (o != null && o instanceof Event)
                {
                    MinecraftForge.EVENT_BUS.post((Event) o);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void unloadTileIC2()
    {
        if (this.isAddedToEnergyNet && this.world != null)
        {
            if (!this.world.isRemote && EnergyConfigHandler.isIndustrialCraft2Loaded())
            {
                try
                {
                    Object o = CompatibilityManager.classIC2tileEventUnload.getConstructor(IEnergyTile.class).newInstance(this);

                    if (o != null && o instanceof Event)
                    {
                        MinecraftForge.EVENT_BUS.post((Event) o);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            this.isAddedToEnergyNet = false;
        }
    }

    @Override
    @Method(modid = CompatibilityManager.modidIC2)
    public double getDemandedEnergy()
    {
        if (this.getNetwork() == null)
        {
            return 0.0;
        }

        if (this.IC2surplusJoules < 0.001F)
        {
            this.IC2surplusJoules = 0F;
            return this.getNetwork().getRequest(this) / EnergyConfigHandler.IC2_RATIO;
        }

        this.IC2surplusJoules = this.getNetwork().produce(this.IC2surplusJoules, true, 1, this);
        if (this.IC2surplusJoules < 0.001F)
        {
            this.IC2surplusJoules = 0F;
            return this.getNetwork().getRequest(this) / EnergyConfigHandler.IC2_RATIO;
        }
        return 0D;
    }

    @Override
    @Method(modid = CompatibilityManager.modidIC2)
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage)
    {
        TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.world, directionFrom);
        int tier = ((int) voltage > 120) ? 2 : 1;
        if (tile instanceof IEnergySource && ((IEnergySource) tile).getOfferedEnergy() >= 128)
        {
            tier = 2;
        }
        float convertedEnergy = (float) amount * EnergyConfigHandler.IC2_RATIO;
        float surplus = this.getNetwork().produce(convertedEnergy, true, tier, this, tile);

        if (surplus >= 0.001F)
        {
            this.IC2surplusJoules = surplus;
        }
        else
        {
            this.IC2surplusJoules = 0F;
        }

        return 0D;
    }

    @Override
    @Method(modid = CompatibilityManager.modidIC2)
    public int getSinkTier()
    {
        return 3;
    }

    @Override
    @Method(modid = CompatibilityManager.modidIC2)
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side)
    {
        //Don't add connection to IC2 grid if it's a Galacticraft tile
        if (emitter instanceof IElectrical || emitter instanceof IConductor)
        {
            return false;
        }

        //Don't make connection with IC2 wires [don't want risk of multiple connections + there is a graphical glitch in IC2]
        try
        {
            if (EnergyUtil.clazzIC2Cable != null && EnergyUtil.clazzIC2Cable.isInstance(emitter))
            {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    @Method(modid = CompatibilityManager.modidIC2)
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side)
    {
        //Don't add connection to IC2 grid if it's a Galacticraft tile
        if (receiver instanceof IElectrical || receiver instanceof IConductor)
        {
            return false;
        }

        //Don't make connection with IC2 wires [don't want risk of multiple connections + there is a graphical glitch in IC2]
        try
        {
            if (EnergyUtil.clazzIC2Cable != null && EnergyUtil.clazzIC2Cable.isInstance(receiver))
            {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    // ForgeEnergy
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (this.getNetwork() == null || EnergyConfigHandler.disableFEInput)
        {
            return 0;
        }

        float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1);
        return MathHelper.floor(sentGC / EnergyConfigHandler.RF_RATIO);
    }

    // ForgeEnergy OR BuildCraft (method name clash!)
    @Override
    public boolean canReceive()
    {
        return !EnergyConfigHandler.disableBuildCraftInput || !EnergyConfigHandler.disableFEInput;
    }

    // ForgeEnergy
    @Override
    public int getEnergyStored()
    {
        return 0;
    }

    // ForgeEnergy
    @Override
    public int getMaxEnergyStored()
    {
        if (this.getNetwork() == null || EnergyConfigHandler.disableFEInput)
        {
            return 0;
        }

        return MathHelper.floor(this.getNetwork().getRequest(this) / EnergyConfigHandler.RF_RATIO);
    }

    // ForgeEnergy
    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
    }

    // ForgeEnergy
    @Override
    public boolean canExtract()
    {
        return false;
    }

    // Buildcraft 7
    @Override
    @Method(modid = CompatibilityManager.modBCraftEnergy)
    public long getPowerRequested()
    {
        if (this.getNetwork() == null || EnergyConfigHandler.disableBuildCraftInput)
        {
            return 0L;
        }

        return (long) (this.getNetwork().getRequest(this) / EnergyConfigHandler.BC8_INTERNAL_RATIO);
    }

    // Buildcraft 7
    @Override
    @Method(modid = CompatibilityManager.modBCraftEnergy)
    public long receivePower(long microJoules, boolean simulate)
    {
        if (this.getNetwork() == null || EnergyConfigHandler.disableBuildCraftInput)
        {
            return microJoules;
        }
        float receiveGC = microJoules * EnergyConfigHandler.BC8_INTERNAL_RATIO;
        float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1);
        return (long) (sentGC / EnergyConfigHandler.BC8_INTERNAL_RATIO);
    }

    // Buildcraft 7
    @Override
    @Method(modid = CompatibilityManager.modBCraftEnergy)
    public boolean canConnect(@Nonnull IMjConnector other)
    {
        return true;
    }

    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
    {
        if (this.getNetwork() == null)
        {
            return 0;
        }
        float receiveGC = maxReceive * EnergyConfigHandler.RF_RATIO;
        float sentGC = receiveGC - this.getNetwork().produce(receiveGC, !simulate, 1);
        return MathHelper.floor(sentGC / EnergyConfigHandler.RF_RATIO);
    }

    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
    {
        return 0;
    }

    public boolean canConnectEnergy(EnumFacing from)
    {
        // Do not form wire-to-wire connections with EnderIO conduits
        TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.world, from);
        try
        {
            if (EnergyUtil.clazzMekCable != null && EnergyUtil.clazzMekCable.isInstance(tile))
            {
                return false;
            }
        } catch (Exception e)
        {
        }
        return true;
    }

    public int getEnergyStored(EnumFacing from)
    {
        return 0;
    }

    public int getMaxEnergyStored(EnumFacing from)
    {
        if (this.getNetwork() == null)
        {
            return 0;
        }

        return MathHelper.floor(this.getNetwork().getRequest(this) / EnergyConfigHandler.RF_RATIO);
    }

    @Method(modid = CompatibilityManager.modidMekanism)
    public double transferEnergyToAcceptor(EnumFacing side, double amount)
    {
        if (EnergyConfigHandler.disableMekanismInput)
        {
            return 0.0;
        }

        if (!this.canReceiveEnergy(side))
        {
            return 0.0;
        }

        return amount - this.getNetwork().produce((float) amount * EnergyConfigHandler.MEKANISM_RATIO, true, 1, this) / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    @Method(modid = CompatibilityManager.modidMekanism)
    public double acceptEnergy(EnumFacing side, double amount, boolean simulate)
    {
        if (EnergyConfigHandler.disableMekanismInput)
        {
            return 0.0;
        }

        if (!this.canReceiveEnergy(side))
        {
            return 0.0;
        }

        return amount - this.getNetwork().produce((float) amount * EnergyConfigHandler.MEKANISM_RATIO, !simulate, 1, this) / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    @Method(modid = CompatibilityManager.modidMekanism)
    public boolean canReceiveEnergy(EnumFacing side)
    {
        if (this.getNetwork() == null)
        {
            return false;
        }

        TileEntity te = new BlockVec3(this).getTileEntityOnSide(this.world, side);
        try
        {
            if (EnergyUtil.clazzMekCable != null && EnergyUtil.clazzMekCable.isInstance(te))
            {
                return true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    @Method(modid = CompatibilityManager.modidMekanism)
    public double getEnergy()
    {
        return 0;
    }

    @Method(modid = CompatibilityManager.modidMekanism)
    public void setEnergy(double energy)
    {
    }

    @Method(modid = CompatibilityManager.modidMekanism)
    public double getMaxEnergy()
    {
        if (this.getNetwork() == null)
        {
            return 0;
        }
        return this.getNetwork().getRequest(this) / EnergyConfigHandler.MEKANISM_RATIO;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            return true;
        }
        if (EnergyConfigHandler.isBuildcraftLoaded() && (capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_CONNECTOR))
        {
            TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.world, facing);
            return !(CompatibilityManager.classBCTransportPipeTile.isInstance(tile));
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
            if (EnergyUtil.clazzEnderIOCable == null)
                return (T) this;
            TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.world, facing);
            return EnergyUtil.clazzEnderIOCable.isInstance(tile) ? null : (T) this;
        }
        if (EnergyConfigHandler.isBuildcraftLoaded() && (capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_CONNECTOR))
        {
            TileEntity tile = new BlockVec3(this).getTileEntityOnSide(this.world, facing);
            if (!(CompatibilityManager.classBCTransportPipeTile.isInstance(tile)))
            {
                return (T) this;
            }
        }
        return super.getCapability(capability, facing);
    }
}
