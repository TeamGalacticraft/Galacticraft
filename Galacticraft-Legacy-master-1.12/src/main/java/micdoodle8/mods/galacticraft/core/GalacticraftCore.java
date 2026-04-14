/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.client.IGameScreen;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialObject;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialType;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot;
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo;
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC;
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas;
import micdoodle8.mods.galacticraft.core.advancement.GCTriggers;
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating;
import micdoodle8.mods.galacticraft.core.client.gui.GuiHandler;
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenBasic;
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenCelestial;
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenText;
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds;
import micdoodle8.mods.galacticraft.core.command.CommandGCEnergyUnits;
import micdoodle8.mods.galacticraft.core.command.CommandGCHelp;
import micdoodle8.mods.galacticraft.core.command.CommandGCHouston;
import micdoodle8.mods.galacticraft.core.command.CommandGCInv;
import micdoodle8.mods.galacticraft.core.command.CommandGCKit;
import micdoodle8.mods.galacticraft.core.command.CommandJoinSpaceRace;
import micdoodle8.mods.galacticraft.core.command.CommandKeepDim;
import micdoodle8.mods.galacticraft.core.command.CommandPlanetTeleport;
import micdoodle8.mods.galacticraft.core.command.CommandSpaceStationAddOwner;
import micdoodle8.mods.galacticraft.core.command.CommandSpaceStationChangeOwner;
import micdoodle8.mods.galacticraft.core.command.CommandSpaceStationRemoveOwner;
import micdoodle8.mods.galacticraft.core.datafix.GCCoreDataFixers;
import micdoodle8.mods.galacticraft.core.dimension.GCDimensions;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeMoon;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOrbit;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOverworld;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOverworldOrbit;
import micdoodle8.mods.galacticraft.core.energy.EnergyConfigHandler;
import micdoodle8.mods.galacticraft.core.energy.grid.ChunkPowerHandler;
import micdoodle8.mods.galacticraft.core.energy.tile.TileCableIC2Sealed;
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager;
import micdoodle8.mods.galacticraft.core.entities.EntityBuggy;
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedWitch;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import micdoodle8.mods.galacticraft.core.entities.EntityFlag;
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor;
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk;
import micdoodle8.mods.galacticraft.core.entities.EntityParachest;
import micdoodle8.mods.galacticraft.core.entities.EntitySkeletonBoss;
import micdoodle8.mods.galacticraft.core.entities.EntityTier1Rocket;
import micdoodle8.mods.galacticraft.core.entities.player.GCCapabilities;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerBaseMP;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler;
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC;
import micdoodle8.mods.galacticraft.core.event.LootHandlerGC;
import micdoodle8.mods.galacticraft.core.items.ItemSchematic;
import micdoodle8.mods.galacticraft.core.network.ConnectionEvents;
import micdoodle8.mods.galacticraft.core.network.ConnectionPacket;
import micdoodle8.mods.galacticraft.core.network.GalacticraftChannelHandler;
import micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore;
import micdoodle8.mods.galacticraft.core.recipe.RecipeManagerGC;
import micdoodle8.mods.galacticraft.core.schematic.SchematicAdd;
import micdoodle8.mods.galacticraft.core.schematic.SchematicMoonBuggy;
import micdoodle8.mods.galacticraft.core.schematic.SchematicRocketT1;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLock;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAirLockController;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWire;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWireSwitch;
import micdoodle8.mods.galacticraft.core.tile.TileEntityArclamp;
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFueler;
import micdoodle8.mods.galacticraft.core.tile.TileEntityBuggyFuelerSingle;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoLoader;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCargoUnloader;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCircuitFabricator;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCompactNasaWorkbench;
import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDeconstructor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace;
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricIngotCompressor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityEmergencyBox;
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule;
import micdoodle8.mods.galacticraft.core.tile.TileEntityFallenMeteor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidPipe;
import micdoodle8.mods.galacticraft.core.tile.TileEntityFluidTank;
import micdoodle8.mods.galacticraft.core.tile.TileEntityFuelLoader;
import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad;
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPadSingle;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.core.tile.TileEntityNasaWorkbench;
import micdoodle8.mods.galacticraft.core.tile.TileEntityNull;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCollector;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCompressor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDecompressor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDetector;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDistributor;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenSealer;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenStorageModule;
import micdoodle8.mods.galacticraft.core.tile.TileEntityPainter;
import micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight;
import micdoodle8.mods.galacticraft.core.tile.TileEntityParaChest;
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlatform;
import micdoodle8.mods.galacticraft.core.tile.TileEntityPlayerDetector;
import micdoodle8.mods.galacticraft.core.tile.TileEntityRefinery;
import micdoodle8.mods.galacticraft.core.tile.TileEntityScreen;
import micdoodle8.mods.galacticraft.core.tile.TileEntitySolar;
import micdoodle8.mods.galacticraft.core.tile.TileEntitySpaceStationBase;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry;
import micdoodle8.mods.galacticraft.core.tile.TileEntityThruster;
import micdoodle8.mods.galacticraft.core.tile.TileEntityTreasureChest;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.CreativeTabGC;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GalacticLog;
import micdoodle8.mods.galacticraft.core.util.MapUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback;
import micdoodle8.mods.galacticraft.core.world.gen.BiomeMoon;
import micdoodle8.mods.galacticraft.core.world.gen.BiomeOrbit;
import micdoodle8.mods.galacticraft.core.world.gen.OreGenOtherMods;
import micdoodle8.mods.galacticraft.core.world.gen.OverworldGenerator;
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets;
import micdoodle8.mods.galacticraft.planets.asteroids.recipe.RecipeManagerAsteroids;
import micdoodle8.mods.galacticraft.planets.mars.recipe.RecipeManagerMars;
import micdoodle8.mods.galacticraft.planets.venus.recipe.RecipeManagerVenus;

import api.player.server.ServerPlayerAPI;

//@noformat
@Mod(
	modid = Constants.MOD_ID_CORE, 
	name = GalacticraftCore.NAME, 
	version = Constants.VERSION, 
	dependencies = Constants.DEPENDENCIES_FORGE + Constants.DEPENDENCIES_MICCORE, 
	useMetadata = false, 
	acceptedMinecraftVersions = "[1.12, 1.13)", 
	guiFactory = "micdoodle8.mods.galacticraft.core.client.gui.screen.ConfigGuiFactoryCore"
)
public class GalacticraftCore
{
	public static final String NAME = "Galacticraft Core";
	//@format

    private File                             GCCoreSource;

    @SidedProxy(clientSide = "micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore", serverSide = "micdoodle8.mods.galacticraft.core.proxy.CommonProxyCore")
    public static CommonProxyCore            proxy;

    @Instance(Constants.MOD_ID_CORE)
    public static GalacticraftCore           instance;

    /**
     * This will be TRUE from this point forward at all times
     */
    @Deprecated
    @ReplaceWith("Remove all checks to this")
    public static boolean                    isPlanetsLoaded = true;

    public static boolean                    isHeightConflictingModInstalled;

    public static GalacticraftChannelHandler packetPipeline;
    public static GCPlayerHandler            handler;

    public static CreativeTabGC              galacticraftBlocksTab;
    public static CreativeTabGC              galacticraftItemsTab;

    public static SolarSystem                solarSystemSol;
    public static Planet                     planetMercury;
    public static Planet                     planetVenus;
    public static Planet                     planetMars;
    public static Planet                     planetOverworld;
    public static Planet                     planetJupiter;
    public static Planet                     planetSaturn;
    public static Planet                     planetUranus;
    public static Planet                     planetNeptune;
    public static Moon                       moonMoon;
    public static Satellite                  satelliteSpaceStation;

    public static LinkedList<ItemStack>      itemList        = new LinkedList<>();
    public static LinkedList<Item>           itemListTrue    = new LinkedList<>();
    public static LinkedList<Block>          blocksList      = new LinkedList<>();
    public static LinkedList<BiomeGenBaseGC> biomesList      = new LinkedList<>();

    public static ImageWriter                jpgWriter;
    public static ImageWriteParam            writeParam;
    public static boolean                    enableJPEG      = false;

    public static GalacticLog                logger;

    public GalacticraftCore()
    {
        logger = new GalacticLog(this);
    }

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GCCoreSource = event.getSourceFile();
        this.initModInfo(event.getModMetadata());
        GCCapabilities.register();

        GCCoreUtil.nextID = 0;

        if (CompatibilityManager.isSmartMovingLoaded || CompatibilityManager.isWitcheryLoaded)
        {
            isHeightConflictingModInstalled = true;
        }

        GalacticraftCore.solarSystemSol = new SolarSystem("sol", "milky_way").setMapPosition(new Vector3(0.0F, 0.0F, 0.0F));
        GalacticraftCore.planetOverworld = (Planet) new Planet("overworld").setParentSolarSystem(GalacticraftCore.solarSystemSol).setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift(0.0F);
        GalacticraftCore.moonMoon = (Moon) new Moon("moon").setParentPlanet(GalacticraftCore.planetOverworld).setRelativeSize(0.2667F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(13F, 13F)).setRelativeOrbitTime(1 / 0.01F);
        GalacticraftCore.satelliteSpaceStation = (Satellite) new Satellite("spacestation.overworld").setParentBody(GalacticraftCore.planetOverworld).setRelativeSize(0.2667F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(9F, 9F)).setRelativeOrbitTime(1 / 0.05F);

        MinecraftForge.EVENT_BUS.register(new EventHandlerGC());
        handler = new GCPlayerHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        GalacticraftCore.proxy.preInit(event);

        ConnectionPacket.bus = NetworkRegistry.INSTANCE.newEventDrivenChannel(ConnectionPacket.CHANNEL);
        ConnectionPacket.bus.register(new ConnectionPacket());

        this.handleConfigFilenameChange(event);

        GalacticraftCore.galacticraftBlocksTab = new CreativeTabGC(CreativeTabs.getNextID(), "galacticraft_blocks", null, null);
        GalacticraftCore.galacticraftItemsTab = new CreativeTabGC(CreativeTabs.getNextID(), "galacticraft_items", null, null);

        GCFluids.registerOilandFuel();

        if (CompatibilityManager.PlayerAPILoaded)
        {
            ServerPlayerAPI.register(Constants.MOD_ID_CORE, GCPlayerBaseMP.class);
        }

        GCBlocks.initBlocks();
        GCItems.initItems();

        GCFluids.registerFluids();

        // Force initialisation of GC biome types in preinit (after config load)
        // - this helps BiomeTweaker by initialising mod biomes in a fixed order
        // during mod loading
        GalacticraftCore.satelliteSpaceStation.setBiomeInfo(BiomeOrbit.space);
        GalacticraftCore.moonMoon.setBiomeInfo(BiomeMoon.moonFlat);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        GalacticraftCore.galacticraftBlocksTab.setItemForTab(new ItemStack(Item.getItemFromBlock(GCBlocks.machineBase2)));
        GalacticraftCore.galacticraftItemsTab.setItemForTab(new ItemStack(GCItems.rocketTier1));

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            GCBlocks.finalizeSort();
            GCItems.finalizeSort();
        }

        GalacticraftCore.proxy.init(event);

        GalacticraftCore.packetPipeline = GalacticraftChannelHandler.init();

        Star starSol = (Star) new Star("sol").setParentSolarSystem(GalacticraftCore.solarSystemSol).setTierRequired(-1);
        starSol.setBodyIcon(new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/sun.png"));
        GalacticraftCore.solarSystemSol.setMainStar(starSol);

        GalacticraftCore.planetOverworld.setBodyIcon(new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png"));
        GalacticraftCore.planetOverworld.setDimensionInfo(ConfigManagerCore.idDimensionOverworld, WorldProvider.class, false).setTierRequired(1);
        GalacticraftCore.planetOverworld.atmosphereComponent(EnumAtmosphericGas.NITROGEN).atmosphereComponent(EnumAtmosphericGas.OXYGEN).atmosphereComponent(EnumAtmosphericGas.ARGON).atmosphereComponent(EnumAtmosphericGas.WATER);
        GalacticraftCore.planetOverworld.addChecklistKeys("equip_parachute");

        GalacticraftCore.moonMoon.setDimensionInfo(ConfigManagerCore.idDimensionMoon, WorldProviderMoon.class).setTierRequired(1);
        GalacticraftCore.moonMoon.setBodyIcon(new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/moon.png"));
        GalacticraftCore.moonMoon.setAtmosphere(new AtmosphereInfo(false, false, false, 0.0F, 0.0F, 0.0F));
        GalacticraftCore.moonMoon.addMobInfo(new SpawnListEntry(EntityEvolvedZombie.class, 8, 2, 3));
        GalacticraftCore.moonMoon.addMobInfo(new SpawnListEntry(EntityEvolvedSpider.class, 8, 2, 3));
        GalacticraftCore.moonMoon.addMobInfo(new SpawnListEntry(EntityEvolvedSkeleton.class, 8, 2, 3));
        GalacticraftCore.moonMoon.addMobInfo(new SpawnListEntry(EntityEvolvedCreeper.class, 8, 2, 3));
        GalacticraftCore.moonMoon.addMobInfo(new SpawnListEntry(EntityEvolvedEnderman.class, 10, 1, 4));
        GalacticraftCore.moonMoon.addChecklistKeys("equip_oxygen_suit");

        // Satellites must always have a WorldProvider implementing
        // IOrbitDimension
        GalacticraftCore.satelliteSpaceStation.setDimensionInfo(ConfigManagerCore.idDimensionOverworldOrbit, ConfigManagerCore.idDimensionOverworldOrbitStatic, WorldProviderOverworldOrbit.class).setTierRequired(1);
        GalacticraftCore.satelliteSpaceStation.setBodyIcon(new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/space_station.png"));
        GalacticraftCore.satelliteSpaceStation.setAtmosphere(new AtmosphereInfo(false, false, false, 0.0F, 0.1F, 0.02F));
        GalacticraftCore.satelliteSpaceStation.addChecklistKeys("equip_oxygen_suit", "create_grapple");

        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingCallback());
        MinecraftForge.EVENT_BUS.register(new ConnectionEvents());

        SchematicRegistry.registerSchematicRecipe(new SchematicRocketT1());
        SchematicRegistry.registerSchematicRecipe(new SchematicMoonBuggy());
        SchematicRegistry.registerSchematicRecipe(new SchematicAdd());
        ChunkPowerHandler.initiate();
        EnergyConfigHandler.initGas();
        LootHandlerGC.registerAll();

        this.registerCreatures();
        this.registerOtherEntities();
        this.registerTileEntities();

        GalaxyRegistry.register(GalacticraftCore.solarSystemSol);
        GalaxyRegistry.register(GalacticraftCore.planetOverworld);
        GalaxyRegistry.register(GalacticraftCore.moonMoon);
        GalaxyRegistry.register(GalacticraftCore.satelliteSpaceStation);
        GCDimensions.ORBIT = GalacticraftRegistry.registerDimension("Space Station", "_orbit", ConfigManagerCore.idDimensionOverworldOrbit, WorldProviderOverworldOrbit.class, false);
        if (GCDimensions.ORBIT == null)
        {
            GalacticraftCore.logger.error("Failed to register space station dimension type with ID " + ConfigManagerCore.idDimensionOverworldOrbit);
        }
        GCDimensions.ORBIT_KEEPLOADED = GalacticraftRegistry.registerDimension("Space Station", "_orbit", ConfigManagerCore.idDimensionOverworldOrbitStatic, WorldProviderOverworldOrbit.class, true);
        if (GCDimensions.ORBIT_KEEPLOADED == null)
        {
            GalacticraftCore.logger.error("Failed to register space station dimension type with ID " + ConfigManagerCore.idDimensionOverworldOrbitStatic);
        }
        GalacticraftRegistry.registerTeleportType(WorldProviderSurface.class, new TeleportTypeOverworld());
        GalacticraftRegistry.registerTeleportType(WorldProviderOverworldOrbit.class, new TeleportTypeOrbit());
        GalacticraftRegistry.registerTeleportType(WorldProviderMoon.class, new TeleportTypeMoon());
        GalacticraftRegistry.registerRocketGui(WorldProviderOverworldOrbit.class, new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/overworld_rocket_gui.png"));
        GalacticraftRegistry.registerRocketGui(WorldProviderSurface.class, new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/overworld_rocket_gui.png"));
        GalacticraftRegistry.registerRocketGui(WorldProviderMoon.class, new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/moon_rocket_gui.png"));
        GalacticraftRegistry.addDungeonLoot(1, new ItemStack(GCItems.schematic, 1, 0));
        GalacticraftRegistry.addDungeonLoot(1, new ItemStack(GCItems.schematic, 1, 1));

        if (ConfigManagerCore.enableCopperOreGen)
        {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 5, 24, 0, 75, 7), 4);
        }

        if (ConfigManagerCore.enableTinOreGen)
        {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 6, 22, 0, 60, 7), 4);
        }

        if (ConfigManagerCore.enableAluminumOreGen)
        {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 7, 18, 0, 45, 7), 4);
        }

        if (ConfigManagerCore.enableSiliconOreGen)
        {
            GameRegistry.registerWorldGenerator(new OverworldGenerator(GCBlocks.basicBlock, 8, 3, 0, 25, 7), 4);
        }

        FMLInterModComms.sendMessage("OpenBlocks", "donateUrl", "http://www.patreon.com/micdoodle8");
        registerCoreGameScreens();

        GCFluids.registerLegacyFluids();
        GCFluids.registerDispenserBehaviours();
        if (CompatibilityManager.isBCraftEnergyLoaded())
            GCFluids.registerBCFuel();

        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_MASK, EnumExtendedInventorySlot.MASK, GCItems.oxMask);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_GEAR, EnumExtendedInventorySlot.GEAR, GCItems.oxygenGear);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_LIGHT, EnumExtendedInventorySlot.LEFT_TANK, GCItems.oxTankLight);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_LIGHT, EnumExtendedInventorySlot.RIGHT_TANK, GCItems.oxTankLight);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_MEDIUM, EnumExtendedInventorySlot.LEFT_TANK, GCItems.oxTankMedium);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_MEDIUM, EnumExtendedInventorySlot.RIGHT_TANK, GCItems.oxTankMedium);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_HEAVY, EnumExtendedInventorySlot.LEFT_TANK, GCItems.oxTankHeavy);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_HEAVY, EnumExtendedInventorySlot.RIGHT_TANK, GCItems.oxTankHeavy);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_INFINITE, EnumExtendedInventorySlot.LEFT_TANK, GCItems.oxygenCanisterInfinite);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_OXYGEN_TANK_INFINITE, EnumExtendedInventorySlot.RIGHT_TANK, GCItems.oxygenCanisterInfinite);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_PARACHUTE, EnumExtendedInventorySlot.PARACHUTE, GCItems.parachute);
        GalacticraftRegistry.registerGear(Constants.GEAR_ID_FREQUENCY_MODULE, EnumExtendedInventorySlot.FREQUENCY_MODULE, new ItemStack(GCItems.basicItem, 1, 19));

        GalacticraftCore.proxy.registerFluidTexture(GCFluids.fluidOil, new ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/underoil.png"));
        GalacticraftCore.proxy.registerFluidTexture(GCFluids.fluidFuel, new ResourceLocation(Constants.ASSET_PREFIX, "textures/misc/underfuel.png"));

        PermissionAPI.registerNode(Constants.PERMISSION_CREATE_STATION, DefaultPermissionLevel.ALL, "Allows players to create space stations");

        GCTriggers.registerTriggers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GalacticraftCore.planetMercury = makeDummyPlanet("mercury", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetMercury != null)
        {
            GalacticraftCore.planetMercury.setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift(1.45F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.5F, 0.5F)).setRelativeOrbitTime(0.24096385542168674698795180722892F);
        }
        GalacticraftCore.planetVenus = makeDummyPlanet("venus", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetVenus != null)
        {
            GalacticraftCore.planetVenus.setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift(2.0F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.75F, 0.75F)).setRelativeOrbitTime(0.61527929901423877327491785323111F);
        }
        GalacticraftCore.planetMars = makeDummyPlanet("mars", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetMars != null)
        {
            GalacticraftCore.planetMars.setRingColorRGB(0.67F, 0.1F, 0.1F).setPhaseShift(0.1667F).setRelativeSize(0.5319F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.25F, 1.25F)).setRelativeOrbitTime(1.8811610076670317634173055859803F);
        }
        GalacticraftCore.planetJupiter = makeDummyPlanet("jupiter", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetJupiter != null)
        {
            GalacticraftCore.planetJupiter.setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift((float) Math.PI).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.5F, 1.5F)).setRelativeOrbitTime(11.861993428258488499452354874042F);
        }
        GalacticraftCore.planetSaturn = makeDummyPlanet("saturn", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetSaturn != null)
        {
            GalacticraftCore.planetSaturn.setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift(5.45F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.75F, 1.75F)).setRelativeOrbitTime(29.463307776560788608981380065717F);
        }
        GalacticraftCore.planetUranus = makeDummyPlanet("uranus", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetUranus != null)
        {
            GalacticraftCore.planetUranus.setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift(1.38F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.0F, 2.0F)).setRelativeOrbitTime(84.063526834611171960569550930997F);
        }
        GalacticraftCore.planetNeptune = makeDummyPlanet("neptune", GalacticraftCore.solarSystemSol);
        if (GalacticraftCore.planetNeptune != null)
        {
            GalacticraftCore.planetNeptune.setRingColorRGB(0.1F, 0.9F, 0.6F).setPhaseShift(1.0F).setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(2.25F, 2.25F)).setRelativeOrbitTime(164.84118291347207009857612267251F);
        }

        MinecraftForge.EVENT_BUS.register(new OreGenOtherMods());

        GalacticraftCore.proxy.postInit(event);

        ArrayList<CelestialBody> cBodyList = new ArrayList<>();
        cBodyList.addAll(GalaxyRegistry.getPlanets());
        cBodyList.addAll(GalaxyRegistry.getMoons());

        for (CelestialBody body : cBodyList)
        {
            if (body.shouldAutoRegister())
            {
                int id = Arrays.binarySearch(ConfigManagerCore.staticLoadDimensions, body.getDimensionID());
                // It's important this is done in the same order as planets will
                // be registered by WorldUtil.registerPlanet();
                DimensionType type = GalacticraftRegistry.registerDimension(body.getTranslationKey(), body.getDimensionSuffix(), body.getDimensionID(), body.getWorldProvider(), body.getForceStaticLoad() || id < 0);
                if (type != null)
                {
                    body.initialiseMobSpawns();
                }
                else
                {
                    body.setUnreachable();
                    GalacticraftCore.logger.error("Tried to register dimension for body: " + body.getTranslationKey() + " hit conflict with ID " + body.getDimensionID());
                }
            }

            if (body.getSurfaceBlocks() != null)
            {
                TransformerHooks.spawnListAE2_GC.addAll(body.getSurfaceBlocks());
            }
        }

        GCDimensions.MOON = WorldUtil.getDimensionTypeById(ConfigManagerCore.idDimensionMoon);

        CompatibilityManager.checkForCompatibleMods();
        RecipeManagerGC.loadCompatibilityRecipes();
        TileEntityDeconstructor.initialiseRecipeList();
        ItemSchematic.registerSchematicItems();
        NetworkRegistry.INSTANCE.registerGuiHandler(GalacticraftCore.instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new TickHandlerServer());
        GalaxyRegistry.refreshGalaxies();

        GalacticraftRegistry.registerScreen(new GameScreenText()); // Screen API
        // demo
        // Note: add-ons can register their own screens in postInit by calling
        // GalacticraftRegistry.registerScreen(IGameScreen) like this.
        // [Called on both client and server: do not include any client-specific
        // code in the new game screen's constructor method.]

        try
        {
            jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            writeParam = jpgWriter.getDefaultWriteParam();
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(1.0f);
            enableJPEG = true;
        } catch (UnsatisfiedLinkError e)
        {
            GalacticraftCore.logger.error("Error initialising JPEG compressor - this is likely caused by OpenJDK - see https://wiki.micdoodle8.com/wiki/Compatibility#For_clients_running_OpenJDK");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event)
    {
        TickHandlerServer.restart();
    }

    @EventHandler
    public void serverInit(FMLServerStartedEvent event)
    {
        BlockVec3.chunkCacheDim = Integer.MAX_VALUE;
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        GCCoreUtil.notifyStarted(event.getServer());
        File worldFolder = DimensionManager.getCurrentSaveRootDirectory();
        moveLegacyGCFileLocations(worldFolder);

        event.registerServerCommand(new CommandSpaceStationAddOwner());
        event.registerServerCommand(new CommandSpaceStationChangeOwner());
        event.registerServerCommand(new CommandSpaceStationRemoveOwner());
        event.registerServerCommand(new CommandPlanetTeleport());
        event.registerServerCommand(new CommandKeepDim());
        event.registerServerCommand(new CommandGCInv());
        event.registerServerCommand(new CommandGCHelp());
        event.registerServerCommand(new CommandGCKit());
        event.registerServerCommand(new CommandGCHouston());
        event.registerServerCommand(new CommandGCEnergyUnits());
        event.registerServerCommand(new CommandJoinSpaceRace());

        WorldUtil.initialiseDimensionNames();
        WorldUtil.registerSpaceStations(event.getServer(), new File(worldFolder, "galacticraft"));

        ArrayList<CelestialBody> cBodyList = new ArrayList<>();
        cBodyList.addAll(GalaxyRegistry.getPlanets());
        cBodyList.addAll(GalaxyRegistry.getMoons());

        for (CelestialBody body : cBodyList)
        {
            if (body.shouldAutoRegister())
            {
                if (!WorldUtil.registerPlanet(body.getDimensionID(), body.isReachable(), 0))
                {
                    body.setUnreachable();
                }
            }
        }
    }

    private void handleConfigFilenameChange(FMLPreInitializationEvent event)
    {
        File oldCoreConfig = new File(event.getModConfigurationDirectory(), Constants.OLD_CONFIG_FILE);
        File oldPowerConfig = new File(event.getModConfigurationDirectory(), Constants.OLD_POWER_CONFIG_FILE);
        File oldChunkLoaderConfig = new File(event.getModConfigurationDirectory(), Constants.OLD_CHUNKLOADER_CONFIG_FILE);

        File coreConfig = new File(event.getModConfigurationDirectory(), Constants.CONFIG_FILE);
        File powerConfig = new File(event.getModConfigurationDirectory(), Constants.POWER_CONFIG_FILE);
        File chunkLoaderConfig = new File(event.getModConfigurationDirectory(), Constants.CHUNKLOADER_CONFIG_FILE);

        if (oldCoreConfig.exists())
        {
            oldCoreConfig.renameTo(coreConfig);
        }

        if (oldPowerConfig.exists())
        {
            oldPowerConfig.renameTo(powerConfig);
        }

        if (oldChunkLoaderConfig.exists())
        {
            oldChunkLoaderConfig.renameTo(chunkLoaderConfig);
        }

        ConfigManagerCore.initialize(coreConfig);
        EnergyConfigHandler.setDefaultValues(powerConfig);
        ChunkLoadingCallback.loadConfig(chunkLoaderConfig);
    }

    private void moveLegacyGCFileLocations(File worldFolder)
    {
        File destFolder = new File(worldFolder, "galacticraft");
        if (!destFolder.exists())
        {
            if (!destFolder.mkdirs())
                return;
        }
        File dataFolder = new File(worldFolder, "data");
        if (!dataFolder.exists())
            return;

        moveGCFile(new File(dataFolder, "GCAsteroidData.dat"), destFolder);
        moveGCFile(new File(dataFolder, "GCSpaceRaceData.dat"), destFolder);
        moveGCFile(new File(dataFolder, "GCSpinData.dat"), destFolder);
        moveGCFile(new File(dataFolder, "GCInv_savefile.dat"), destFolder);
        String[] names = dataFolder.list();
        for (String name : names)
        {
            if (name.startsWith("spacestation_") && name.endsWith(".dat"))
            {
                moveGCFile(new File(dataFolder, name), destFolder);
            }
        }
    }

    private void moveGCFile(File file, File destFolder)
    {
        if (file.exists())
        {
            File destPath = new File(destFolder, file.getName());
            if (destPath.exists())
            {
                GalacticraftCore.logger.info("Deleting duplicate Galacticraft data file: " + file.getName());
                file.delete();
                return;
            }
            try
            {
                java.nio.file.Files.move(file.toPath(), destPath.toPath());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent var1)
    {
        MapUtil.saveMapProgress();
    }

    @EventHandler
    public void onServerStop(FMLServerStoppedEvent var1)
    {
        // Unregister dimensions
        WorldUtil.unregisterPlanets();
        WorldUtil.unregisterSpaceStations();
        GCCoreUtil.notifyStarted(null);
    }

    private static void registerCoreGameScreens()
    {
        if (GCCoreUtil.getEffectiveSide() == Side.CLIENT)
        {
            IGameScreen rendererBasic = new GameScreenBasic();
            IGameScreen rendererCelest = new GameScreenCelestial();
            // Type 0 - blank
            GalacticraftRegistry.registerScreen(rendererBasic);
            // Type 1 - local satellite view
            GalacticraftRegistry.registerScreen(rendererBasic);
            // Type 2 - solar system
            GalacticraftRegistry.registerScreen(rendererCelest);
            // Type 3 - local planet
            GalacticraftRegistry.registerScreen(rendererCelest);
            // Type 4 - render test
            GalacticraftRegistry.registerScreen(rendererCelest);
        }
        else
        {
            GalacticraftRegistry.registerScreensServer(5);
        }
    }

    private void registerTileEntities()
    {
        register(TileEntityTreasureChest.class, "gc_treasure_chest");
        register(TileEntityOxygenDistributor.class, "gc_air_distributor");
        register(TileEntityOxygenCollector.class, "gc_air_collector");
        register(TileEntityFluidPipe.class, "gc_oxygen_pipe");
        register(TileEntityAirLock.class, "gc_air_lock_frame");
        register(TileEntityRefinery.class, "gc_refinery");
        register(TileEntityNasaWorkbench.class, "gc_nasa_workbench");
        register(TileEntityCompactNasaWorkbench.class, "gc_nasa_workbench_compact");
        register(TileEntityDeconstructor.class, "gc_deconstructor");
        register(TileEntityOxygenCompressor.class, "gc_air_compressor");
        register(TileEntityFuelLoader.class, "gc_fuel_loader");
        register(TileEntityLandingPadSingle.class, "gc_landing_pad");
        register(TileEntityLandingPad.class, "gc_landing_pad_full");
        register(TileEntitySpaceStationBase.class, "gc_space_station");
        register(TileEntityMulti.class, "gc_dummy_block");
        register(TileEntityOxygenSealer.class, "gc_air_sealer");
        register(TileEntityDungeonSpawner.class, "gc_dungeon_boss_spawner");
        register(TileEntityOxygenDetector.class, "gc_oxygen_detector");
        register(TileEntityBuggyFueler.class, "gc_buggy_fueler");
        register(TileEntityBuggyFuelerSingle.class, "gc_buggy_fueler_single");
        register(TileEntityCargoLoader.class, "gc_cargo_loader");
        register(TileEntityCargoUnloader.class, "gc_cargo_unloader");
        register(TileEntityParaChest.class, "gc_parachest_tile");
        register(TileEntitySolar.class, "gc_solar_panel");
        register(TileEntityDish.class, "gc_radio_telescope");
        register(TileEntityCrafting.class, "gc_magnetic_crafting_table");
        register(TileEntityEnergyStorageModule.class, "gc_energy_storage_module");
        register(TileEntityCoalGenerator.class, "gc_coal_generator");
        register(TileEntityElectricFurnace.class, "gc_electric_furnace");
        register(TileEntityAluminumWire.class, "gc_aluminum_wire");
        register(TileEntityAluminumWireSwitch.class, "gc_switchable_aluminum_wire");
        register(TileEntityFallenMeteor.class, "gc_fallen_meteor");
        register(TileEntityIngotCompressor.class, "gc_ingot_compressor");
        register(TileEntityElectricIngotCompressor.class, "gc_electric_ingot_compressor");
        register(TileEntityCircuitFabricator.class, "gc_circuit_fabricator");
        register(TileEntityAirLockController.class, "gc_air_lock_controller");
        register(TileEntityOxygenStorageModule.class, "gc_oxygen_storage_module");
        register(TileEntityOxygenDecompressor.class, "gc_oxygen_decompressor");
        register(TileEntityThruster.class, "gc_space_station_thruster");
        register(TileEntityArclamp.class, "gc_arc_lamp");
        register(TileEntityScreen.class, "gc_view_screen");
        register(TileEntityPanelLight.class, "gc_panel_lighting");
        register(TileEntityTelemetry.class, "gc_telemetry_unit");
        register(TileEntityPainter.class, "gc_painter");
        register(TileEntityFluidTank.class, "gc_fluid_tank");
        register(TileEntityPlayerDetector.class, "gc_player_detector");
        register(TileEntityPlatform.class, "gc_platform");
        register(TileEntityEmergencyBox.class, "gc_emergency_post");
        register(TileEntityNull.class, "gc_null_tile");
        if (CompatibilityManager.isIc2Loaded())
        {
            register(TileCableIC2Sealed.class, "gc_sealed_ic2_cable");
        }

        new GCCoreDataFixers().registerAll();
    }

    private void register(Class<? extends TileEntity> tileEntityClass, String key)
    {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(Constants.MOD_ID_CORE, key));
    }

    private void registerCreatures()
    {
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedSpider.class, "evolved_spider", 3419431, 11013646);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedZombie.class, "evolved_zombie", 44975, 7969893);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedCreeper.class, "evolved_creeper", 894731, 0);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedSkeleton.class, "evolved_skeleton", 12698049, 4802889);
        GCCoreUtil.registerGalacticraftCreature(EntitySkeletonBoss.class, "evolved_skeleton_boss", 12698049, 4802889);
        GCCoreUtil.registerGalacticraftCreature(EntityAlienVillager.class, "alien_villager", ColorUtil.to32BitColor(255, 103, 145, 181), 12422002);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedEnderman.class, "evolved_enderman", 1447446, 0);
        GCCoreUtil.registerGalacticraftCreature(EntityEvolvedWitch.class, "evolved_witch", 3407872, 5349438);
    }

    private void registerOtherEntities()
    {
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityTier1Rocket.class, "rocket_t1", 150, 1, false);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMeteor.class, "meteor", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityBuggy.class, "buggy", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityFlag.class, "gcflag", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityParachest.class, "para_chest", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityLander.class, "lander", 150, 5, false);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityMeteorChunk.class, "meteor_chunk", 150, 5, true);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityCelestialFake.class, "celestial_screen", 150, 5, false);
        GCCoreUtil.registerGalacticraftNonMobEntity(EntityHangingSchematic.class, "hanging_schematic", 150, 5, false);
    }

    private Planet makeDummyPlanet(String name, SolarSystem system)
    {
        // Loop through all planets to make sure it's not registered as a
        // reachable dimension first
        for (CelestialObject body : new ArrayList<>(GalaxyRegistry.getPlanets()))
        {
            if (body.isEqualTo(CelestialType.PLANET, name))
            {
                if (((Planet) body).getParentSolarSystem() == system)
                {
                    return null;
                }
            }
        }

        Planet planet = new Planet(name).setParentSolarSystem(system);
        planet.setBodyIcon(new ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/celestialbodies/" + name + ".png"));
        GalaxyRegistry.register(planet);
        return planet;
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID_CORE)
    public static class RegistrationHandler
    {

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            GCBlocks.registerBlocks(event.getRegistry());
            CompatibilityManager.registerMicroBlocks();
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            // First, the final steps of block registration
            IForgeRegistry<Block> blockRegistry = RegistryManager.ACTIVE.getRegistry(GameData.BLOCKS);
            GCBlocks.doOtherModsTorches(blockRegistry);
            BlockGrating.createForgeFluidVersions(blockRegistry);

            GCItems.registerItems(event.getRegistry());

            // RegisterSorted for blocks cannot be run until all the items have
            // been registered
            if (GCCoreUtil.getEffectiveSide() == Side.CLIENT)
            {
                for (Item item : GalacticraftCore.itemListTrue)
                {
                    GCItems.registerSorted(item);
                }
                for (Block block : GalacticraftCore.blocksList)
                {
                    GCBlocks.registerSorted(block);
                }
            }

            GCBlocks.oreDictRegistrations();
            GCItems.oreDictRegistrations();
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
        {
            RecipeManagerGC.addUniversalRecipes();
            RecipeManagerGC.setConfigurableRecipes();

            // PLANETS
            RecipeManagerAsteroids.addUniversalRecipes();
            RecipeManagerMars.addUniversalRecipes();
            RecipeManagerVenus.addUniversalRecipes();
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            proxy.registerVariants();
            GalacticraftPlanets.proxy.registerVariants();
        }

        @SubscribeEvent
        public static void registerBiomes(RegistryEvent.Register<Biome> event)
        {
            // First, final steps of item registration
            GalacticraftCore.handler.registerTorchTypes();
            GalacticraftCore.handler.registerItemChanges();

            for (BiomeGenBaseGC biome : GalacticraftCore.biomesList)
            {
                event.getRegistry().register(biome);
                if (!ConfigManagerCore.disableBiomeTypeRegistrations)
                {
                    biome.registerTypes(biome);
                }
            }
        }

        @SubscribeEvent
        public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
        {
            if (GCCoreUtil.getEffectiveSide() == Side.CLIENT)
            {
                GCSounds.registerSounds(event.getRegistry());
            }
        }
    }

    private void initModInfo(ModMetadata info)
    {
        info.modId = Constants.MOD_ID_CORE;
        info.name = Constants.MOD_NAME_SIMPLE;
        info.version = Constants.VERSION;
        info.description = "An advanced space travel mod for Minecraft!";
        info.authorList = Arrays.asList("micdoodle8", "radfast", "EzerArch", "fishtaco", "SpaceViking", "SteveKunG", "ROMVoid95");
        info.logoFile = "assets/galacticraftplanets/core-logo.png";
    }
}
