/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.venus.tick.VenusTickHandlerServer;
import micdoodle8.mods.galacticraft.planets.venus.tile.SolarModuleNetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PlanetsProxy implements IGuiHandler
{

    public void preInit(FMLPreInitializationEvent event)
    {
        for (IPlanetsModule module : GalacticraftPlanets.commonModules)
        {
            module.preInit(event);
        }
    }

    public void registerVariants()
    {

    }

    public void init(FMLInitializationEvent event)
    {
        for (IPlanetsModule module : GalacticraftPlanets.commonModules)
        {
            module.init(event);
        }
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        for (IPlanetsModule module : GalacticraftPlanets.commonModules)
        {
            module.postInit(event);
        }
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
        for (IPlanetsModule module : GalacticraftPlanets.commonModules)
        {
            module.serverStarting(event);
        }
    }

    public void serverInit(FMLServerStartedEvent event)
    {
        for (IPlanetsModule module : GalacticraftPlanets.commonModules)
        {
            module.serverInit(event);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (IPlanetsModule module : GalacticraftPlanets.commonModules)
        {
            List<Integer> guiIDs = new ArrayList<Integer>();
            module.getGuiIDs(guiIDs);
            if (guiIDs.contains(ID))
            {
                return module.getGuiElement(Side.SERVER, ID, player, world, x, y, z);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (IPlanetsModuleClient module : GalacticraftPlanets.clientModules)
        {
            List<Integer> guiIDs = new ArrayList<Integer>();
            module.getGuiIDs(guiIDs);
            if (guiIDs.contains(ID))
            {
                return module.getGuiElement(Side.CLIENT, ID, player, world, x, y, z);
            }
        }

        return null;
    }

    public void postRegisterItem(Item item)
    {
    }

    public void unregisterNetwork(SolarModuleNetwork solarNetwork)
    {
        if (GCCoreUtil.getEffectiveSide().isServer())
        {
            VenusTickHandlerServer.removeSolarNetwork(solarNetwork);
        }
    }

    public void registerNetwork(SolarModuleNetwork solarNetwork)
    {
        if (GCCoreUtil.getEffectiveSide().isServer())
        {
            VenusTickHandlerServer.addSolarNetwork(solarNetwork);
        }
    }
}
