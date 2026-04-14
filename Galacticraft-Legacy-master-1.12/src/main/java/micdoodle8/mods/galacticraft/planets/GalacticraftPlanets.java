/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GalacticLog;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids;
import micdoodle8.mods.galacticraft.planets.datafix.GCPlanetsDataFixers;
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import micdoodle8.mods.galacticraft.planets.venus.ConfigManagerVenus;
import micdoodle8.mods.galacticraft.planets.venus.VenusModule;

@Mod(modid = Constants.MOD_ID_PLANETS,
    name = GalacticraftPlanets.NAME,
    version = Constants.VERSION,
    dependencies = "required-after:galacticraftcore;",
    useMetadata = false,
    acceptedMinecraftVersions = "[1.12, 1.13)",
    guiFactory = "micdoodle8.mods.galacticraft.planets.ConfigGuiFactoryPlanets")
public class GalacticraftPlanets
{

    public static final String NAME = "Galacticraft Planets";
    private File GCPlanetsSource;

    @Instance(Constants.MOD_ID_PLANETS)
    public static GalacticraftPlanets instance;

    public static List<IPlanetsModule> commonModules = new ArrayList<>();
    public static List<IPlanetsModuleClient> clientModules = new ArrayList<>();

    public static final String ASSET_PREFIX = "galacticraftplanets";
    public static final String TEXTURE_PREFIX = ASSET_PREFIX + ":";

    @SidedProxy(clientSide = "micdoodle8.mods.galacticraft.planets.PlanetsProxyClient", serverSide = "micdoodle8.mods.galacticraft.planets.PlanetsProxy")
    public static PlanetsProxy proxy;

    public static Map<String, List<String>> propOrder = new TreeMap<>();

    public static GalacticLog logger;

    public GalacticraftPlanets()
    {
        logger = new GalacticLog(this);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GCPlanetsSource = event.getSourceFile();
        this.initModInfo(event.getModMetadata());
        MinecraftForge.EVENT_BUS.register(this);

        // Initialise configs, converting mars.conf + asteroids.conf to
        // planets.conf if necessary
        File oldMarsConf = new File(event.getModConfigurationDirectory(), "Galacticraft/mars.conf");
        File newPlanetsConf = new File(event.getModConfigurationDirectory(), "Galacticraft/planets.conf");
        boolean update = false;
        if (oldMarsConf.exists())
        {
            oldMarsConf.renameTo(newPlanetsConf);
            update = true;
        }

        File planetsConfig = new File(event.getModConfigurationDirectory(), Constants.PLANETS_CONFIG_FILE);

        if (newPlanetsConf.exists())
        {
            newPlanetsConf.renameTo(planetsConfig);
        }

        this.configSyncStart();
        new ConfigManagerMars(planetsConfig, update);
        new ConfigManagerAsteroids(new File(event.getModConfigurationDirectory(), "Galacticraft/asteroids.conf"));
        new ConfigManagerVenus(new File(event.getModConfigurationDirectory(), "Galacticraft/venus.conf"));
        this.configSyncEnd(true);

        GalacticraftPlanets.commonModules.add(new MarsModule());
        GalacticraftPlanets.commonModules.add(new AsteroidsModule());
        GalacticraftPlanets.commonModules.add(new VenusModule());
        GalacticraftPlanets.proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        GalacticraftPlanets.proxy.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(GalacticraftPlanets.instance, GalacticraftPlanets.proxy);

        new GCPlanetsDataFixers().registerAll();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GalacticraftPlanets.proxy.postInit(event);
        TileEntityDeconstructor.initialiseRecipeListPlanets();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        GalacticraftPlanets.proxy.serverStarting(event);
    }

    @EventHandler
    public void serverInit(FMLServerStartedEvent event)
    {
        GalacticraftPlanets.proxy.serverInit(event);
    }

    public static void spawnParticle(String particleID, Vector3 position, Vector3 motion, Object... extraData)
    {
        for (IPlanetsModuleClient module : GalacticraftPlanets.clientModules)
        {
            module.spawnParticle(particleID, position, motion, extraData);
        }
    }

    public static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<>();

        // Get the last planet to be configured only, as all will reference and
        // re-use the same planets.conf config file
        IPlanetsModule module = GalacticraftPlanets.commonModules.get(GalacticraftPlanets.commonModules.size() - 1);
        list.addAll(new ConfigElement(module.getConfiguration().getCategory(Constants.CONFIG_CATEGORY_ENTITIES)).getChildElements());
        list.addAll(new ConfigElement(module.getConfiguration().getCategory(Constants.CONFIG_CATEGORY_ACHIEVEMENTS)).getChildElements());
        list.addAll(new ConfigElement(module.getConfiguration().getCategory(Constants.CONFIG_CATEGORY_GENERAL)).getChildElements());
        list.addAll(new ConfigElement(module.getConfiguration().getCategory(Constants.CONFIG_CATEGORY_WORLDGEN)).getChildElements());
        list.addAll(new ConfigElement(module.getConfiguration().getCategory(Constants.CONFIG_CATEGORY_DIMENSIONS)).getChildElements());
        list.addAll(new ConfigElement(module.getConfiguration().getCategory(Constants.CONFIG_CATEGORY_SCHEMATIC)).getChildElements());

        return list;
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event)
    {
        if (event.getModID().equals(Constants.MOD_ID_PLANETS))
        {
            this.configSyncStart();
            for (IPlanetsModule module : GalacticraftPlanets.commonModules)
            {
                module.syncConfig();
            }
            this.configSyncEnd(false);
        }
    }

    private void configSyncEnd(boolean load)
    {
        // Cleanup older GC config files
        ConfigManagerCore.cleanConfig(ConfigManagerMars.config, propOrder);

        // Always save - this is last to be called both at load time and at
        // mid-game
        if (ConfigManagerMars.config.hasChanged())
        {
            ConfigManagerMars.config.save();
        }
    }

    private void configSyncStart()
    {
        propOrder.clear();
    }

    public static void finishProp(Property prop, String currentCat)
    {
        if (propOrder.get(currentCat) == null)
        {
            propOrder.put(currentCat, new ArrayList<>());
        }
        propOrder.get(currentCat).add(prop.getName());
    }

    private void initModInfo(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = Constants.MOD_ID_PLANETS;
        info.name = GalacticraftPlanets.NAME;
        info.version = Constants.VERSION;
        info.description = "Planets addon for Galacticraft.";
        info.authorList = Arrays.asList("micdoodle8", "radfast", "EzerArch", "fishtaco", "SpaceViking", "SteveKunG");
        info.logoFile = "assets/galacticraftplanets/planets-logo.png";
    }
}
