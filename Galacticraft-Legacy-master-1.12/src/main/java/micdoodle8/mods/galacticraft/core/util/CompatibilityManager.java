/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */
package micdoodle8.mods.galacticraft.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
//import cpw.mods.fml.common.Loader;
//import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkProviderServer;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CompatibilityManager
{
    public final static String modidIC2               = "ic2";
    public final static String modidMekanism          = "mekanism";
    public final static String modidBuildcraft        = "buildcraftcore";
    public final static String modBCraftTransport     = "buildcrafttransport";
    public final static String modBCraftEnergy        = "buildcraftenergy";
    public static boolean      PlayerAPILoaded        = Loader.isModLoaded("PlayerAPI");
    public static boolean      RenderPlayerAPILoaded  = Loader.isModLoaded("RenderPlayerAPI");
    public static boolean      modJEILoaded           = Loader.isModLoaded("jei");
    private static boolean     modIc2Loaded           = Loader.isModLoaded(modidIC2);
    private static boolean     modICClassicLoaded;
    public static boolean      modBCraftLoaded        = Loader.isModLoaded(modidBuildcraft);
    private static boolean     modBCraftEnergyLoaded  = Loader.isModLoaded(modBCraftEnergy);
    private static boolean     modBCraftTransportLoaded;
    private static boolean     modGTLoaded;
    private static boolean     modTELoaded            = Loader.isModLoaded("thermalexpansion");
    private static boolean     modMekLoaded           = Loader.isModLoaded(modidMekanism);
    private static boolean     modAetherIILoaded;
    private static boolean     modAppEngLoaded;
    private static boolean     modPneumaticCraftLoaded;
    private static boolean     modBOPLoaded           = Loader.isModLoaded("biomesoplenty");
    private static boolean     modEIOLoaded           = Loader.isModLoaded("enderio");
    public static boolean      modAALoaded            = Loader.isModLoaded("actuallyadditions");
    private static boolean     spongeLoaded;
    private static boolean     modMatterOverdriveLoaded;
    private static boolean     wailaLoaded;
    public static boolean      isSmartMovingLoaded    = Loader.isModLoaded("smartmoving");
    public static boolean      isTConstructLoaded     = Loader.isModLoaded("tconstruct");
    public static boolean      isWitcheryLoaded       = Loader.isModLoaded("witchery");
    public static boolean      isCubicChunksLoaded;
    public static Class<?>     classGTOre             = null;
    private static Method      spongeOverrideSet      = null;
    private static Method      spongeOverrideGet      = null;
    public static Class        classBCTransport;
    public static Class        classBCTransportPipeTile;
    public static Class        classBOPWorldType      = null;
    public static Class        classBOPws             = null;
    public static Class        classBOPwcm            = null;
    public static Class        classIC2wrench         = null;
    public static Class        classIC2wrenchElectric = null;
    public static Class        classIC2tileEventLoad;
    public static Class        classIC2tileEventUnload;
    public static Field        fieldIC2tickhandler;
    public static Field        fieldIC2networkManager;
    public static Class        classIc2ClassicNetworkManager;
    public static Class        classIC2cableType      = null;
    public static Constructor  constructorIC2cableTE  = null;
    private static Method      androidPlayerGet;
    private static Method      androidPlayerIsAndroid;
    public static Class        classIc2ClassicTileCable;

    public static void checkForCompatibleMods()
    {
        if (Loader.isModLoaded("gregtech") || Loader.isModLoaded("gregtech_addon"))
        {
            CompatibilityManager.modGTLoaded = true;
        }
        if (CompatibilityManager.modMekLoaded)
        {
            GalacticraftCore.logger.info("Activating Mekanism compatibility.");
        }
        if (CompatibilityManager.modTELoaded)
        {
            GalacticraftCore.logger.info("Activating ThermalExpansion compatibility features.");
        }
        if (CompatibilityManager.isTConstructLoaded)
        {
            GalacticraftCore.logger.info("Activating Tinker's Construct compatibility features.");
        }
        if (CompatibilityManager.modIc2Loaded)
        {
            if (tryClass("ic2.api.classic.addon.IC2Plugin") != null)
            {
                modICClassicLoaded = true;
                classIc2ClassicTileCable = tryClass("ic2.core.block.wiring.tile.TileEntityCable");
                classIc2ClassicNetworkManager = tryClass("ic2.core.network.NetworkManager");
            } else
            {
                classIC2cableType = tryClass("ic2.core.block.wiring.CableType");
            }
            try
            {
                classIC2wrench = tryClass("ic2.core.item.tool.ItemToolWrench");
                classIC2wrenchElectric = tryClass("ic2.core.item.tool.ItemToolWrenchElectric");
                classIC2tileEventLoad = tryClass("ic2.api.energy.event.EnergyTileLoadEvent");
                classIC2tileEventUnload = tryClass("ic2.api.energy.event.EnergyTileUnloadEvent");
                Class clazzIC2 = tryClass("ic2.core.IC2");
                fieldIC2networkManager = clazzIC2.getDeclaredField("network");
                fieldIC2tickhandler = clazzIC2.getDeclaredField("tickHandler");
                Class classIC2cable;
                try
                {
                    classIC2cable = Class.forName("ic2.core.block.wiring.TileEntityCable");
                } catch (ClassNotFoundException e)
                {
                    try
                    {
                        classIC2cable = Class.forName("ic2.core.block.wiring.tile.TileEntityCable");
                    } catch (Exception e1)
                    {
                        classIC2cable = null;
                    }
                }
                if (classIC2cable != null)
                {
                    try
                    {
                        BlockEnclosed.onBlockNeighbourChangeIC2a = classIC2cable.getMethod("onNeighborChange", Block.class);
                    } catch (Exception e)
                    {
                        try
                        {
                            BlockEnclosed.onBlockNeighbourChangeIC2b = classIC2cable.getMethod("onNeighborChange", Block.class, BlockPos.class);
                        } catch (Exception e1)
                        {
                            BlockEnclosed.onBlockNeighbourChangeIC2a = classIC2cable.getMethod("onBlockUpdate", Block.class);
                        }
                    }
                    Constructor<?>[] constructors = classIC2cable.getDeclaredConstructors();
                    for (Constructor<?> constructor2 : constructors)
                    {
                        if (constructor2.getGenericParameterTypes().length == 2)
                        {
                            constructorIC2cableTE = constructor2;
                            break;
                        }
                    }
                }
                GalacticraftCore.logger.info("Activating IndustrialCraft2 compatibility features.");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (Loader.isModLoaded(modBCraftTransport))
        {
            CompatibilityManager.modBCraftTransportLoaded = true;
            try
            {
                classBCTransport = Class.forName("buildcraft.transport.BCTransportItems");
                classBCTransportPipeTile = Class.forName("buildcraft.transport.tile.TilePipeHolder");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            BlockEnclosed.initialiseBC();
            if (CompatibilityManager.classBCTransportPipeTile == null)
            {
                CompatibilityManager.modBCraftTransportLoaded = false;
            } else
            {
                GalacticraftCore.logger.info("Activating BuildCraft Transport (Pipes) compatibility features.");
            }
        }
        if (CompatibilityManager.modBOPLoaded)
        {
            try
            {
                classBOPWorldType = Class.forName("biomesoplenty.common.world.WorldTypeBOP");
                classBOPws = Class.forName("biomesoplenty.common.world.BOPWorldSettings");
                classBOPwcm = Class.forName("biomesoplenty.common.world.BiomeProviderBOP");
                GalacticraftCore.logger.info("Activating Biomes O'Plenty compatibility feature.");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (Loader.isModLoaded("aetherii"))
        {
            CompatibilityManager.modAetherIILoaded = true;
            GalacticraftCore.logger.info("Activating AetherII compatibility feature.");
        }
        if (Loader.isModLoaded("appliedenergistics2"))
        {
            CompatibilityManager.modAppEngLoaded = true;
            GalacticraftCore.logger.info("Activating AppliedEnergistics2 compatibility features.");
        }
        if (Loader.isModLoaded("pneumaticcraft"))
        {
            CompatibilityManager.modPneumaticCraftLoaded = true;
            GalacticraftCore.logger.info("Activating PneumaticCraft compatibility features.");
        }
        if (Loader.isModLoaded("waila"))
        {
            CompatibilityManager.wailaLoaded = true;
            GalacticraftCore.logger.info("Activating WAILA compatibility features.");
        }
        if (Loader.isModLoaded("sponge"))
        {
            try
            {
                Class clazz = Class.forName("org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer");
                spongeOverrideSet = clazz.getMethod("setForceChunkRequests", boolean.class);
                spongeOverrideGet = clazz.getMethod("getForceChunkRequests");
                spongeLoaded = true;
            } catch (Exception e)
            {
                try
                {
                    Class clazz = Class.forName("org.spongepowered.common.bridge.world.chunk.ChunkProviderServerBridge");
                    spongeOverrideSet = clazz.getMethod("bridge$setForceChunkRequests", boolean.class);
                    spongeOverrideGet = clazz.getMethod("bridge$getForceChunkRequests");
                    spongeLoaded = true;
                } catch (Exception enew)
                {
                    enew.printStackTrace();
                }
            }
        }
        if (Loader.isModLoaded("cubicchunks"))
        {
            CompatibilityManager.isCubicChunksLoaded = true;
        }
        if (Loader.isModLoaded("matteroverdrive"))
        {
            try
            {
                Class<?> androidPlayer     = Class.forName("matteroverdrive.entity.android_player.AndroidPlayer");
                Class<?> androidCapability = Class.forName("matteroverdrive.entity.player.MOPlayerCapabilityProvider");
                CompatibilityManager.androidPlayerGet = androidCapability.getMethod("GetAndroidCapability", Entity.class);
                CompatibilityManager.androidPlayerIsAndroid = androidPlayer.getMethod("isAndroid");
                CompatibilityManager.modMatterOverdriveLoaded = true;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (CompatibilityManager.isIc2ClassicLoaded())
        {
            Item ic2Coal = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2", "itemmisc"));
            // Coal Chunk
            OreDictionary.registerOre("coal", new ItemStack(ic2Coal, 1, 252));
            // Coal Ball
            OreDictionary.registerOre("coal", new ItemStack(ic2Coal, 1, 250));
            // Compressed Coal Ball
            OreDictionary.registerOre("compressedCoal", new ItemStack(ic2Coal, 1, 251));
            // Charcoal Block
            OreDictionary.registerOre("blockCharcoal", new ItemStack(ic2Coal, 1, 250));
        } else if (CompatibilityManager.isIc2Loaded())
        {
            Item ic2Coal = ForgeRegistries.ITEMS.getValue(new ResourceLocation("ic2", "crafting"));
            // Coal Chunk
            OreDictionary.registerOre("coal", new ItemStack(ic2Coal, 1, 18));
            // Coal Ball
            OreDictionary.registerOre("coal", new ItemStack(ic2Coal, 1, 16));
            // Compressed Coal Ball
            OreDictionary.registerOre("compressedCoal", new ItemStack(ic2Coal, 1, 17));
        }
        OreDictionary.registerOre("charcoal", new ItemStack(Items.COAL, 1, 1));
    }

    @Nullable
    private static Class<?> tryClass(String name)
    {
        try
        {
            return Class.forName(name);
        } catch (ClassNotFoundException e)
        {
            return null;
        }
    }

    public static boolean isIc2Loaded()
    {
        return CompatibilityManager.modIc2Loaded;
    }

    public static boolean isIc2ClassicLoaded()
    {
        return CompatibilityManager.modICClassicLoaded;
    }

    public static boolean isBCraftTransportLoaded()
    {
        return CompatibilityManager.modBCraftTransportLoaded;
    }

    public static boolean isBCraftEnergyLoaded()
    {
        return CompatibilityManager.modBCraftEnergyLoaded;
    }

    public static boolean isTELoaded()
    {
        return CompatibilityManager.modTELoaded;
    }

    public static boolean isMekanismLoaded()
    {
        return CompatibilityManager.modMekLoaded;
    }

    public static boolean isGTLoaded()
    {
        return CompatibilityManager.modGTLoaded;
    }

    public static boolean isAIILoaded()
    {
        return CompatibilityManager.modAetherIILoaded;
    }

    public static boolean isAppEngLoaded()
    {
        return CompatibilityManager.modAppEngLoaded;
    }

    public static boolean isBOPLoaded()
    {
        return CompatibilityManager.modBOPLoaded;
    }

    public static boolean isBOPWorld(WorldType worldType)
    {
        if (modBOPLoaded && classBOPWorldType != null && classBOPws != null && classBOPwcm != null)
        {
            return classBOPWorldType.isInstance(worldType);
        }
        return false;
    }

    public static boolean isPneumaticCraftLoaded()
    {
        return CompatibilityManager.modPneumaticCraftLoaded;
    }

    public static boolean isWailaLoaded()
    {
        return CompatibilityManager.wailaLoaded;
    }

    public static void spongeOverrideStart(WorldServer w)
    {
    }

    public static boolean forceLoadChunks(WorldServer w)
    {
        Boolean spongeForceChunksPrevious = null;
        if (spongeLoaded)
        {
            ChunkProviderServer cps = w.getChunkProvider();
            try
            {
                spongeForceChunksPrevious = (Boolean) spongeOverrideGet.invoke(cps);
                spongeOverrideSet.invoke(cps, true);
            } catch (Exception ignore)
            {
            }
        }
        return Boolean.TRUE.equals(spongeForceChunksPrevious);
    }

    public static void forceLoadChunksEnd(WorldServer w, boolean previous)
    {
        if (spongeLoaded)
        {
            try
            {
                spongeOverrideSet.invoke(w.getChunkProvider(), previous);
            } catch (Exception ignore)
            {
            }
        }
    }

    public static void registerMicroBlocks()
    {
        try
        {
            Class clazz = Class.forName("codechicken.microblock.MicroMaterialRegistry");
            if (clazz != null)
            {
                Method   registerMethod = null;
                Method[] methodz        = clazz.getMethods();
                for (Method m : methodz)
                {
                    if (m.getName().equals("registerMaterial"))
                    {
                        registerMethod = m;
                        break;
                    }
                }
                Class<?> clazzbm = Class.forName("codechicken.microblock.BlockMicroMaterial");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.basicBlock, 3), "tile.gcBlockCore.decoblock1");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.basicBlock, 4), "tile.gcBlockCore.decoblock2");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.basicBlock, 9), "tile.gcBlockCore.copperBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.basicBlock, 10), "tile.gcBlockCore.tinBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.basicBlock, 11), "tile.gcBlockCore.aluminumBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.basicBlock, 12), "tile.gcBlockCore.meteorironBlock");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.blockMoon, 3), "tile.moonBlock.moondirt");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.blockMoon, 4), "tile.moonBlock.moonstone");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.blockMoon, 5), "tile.moonBlock.moongrass");
                registerMethod.invoke(null, clazzbm.getConstructor(Block.class, int.class).newInstance(GCBlocks.blockMoon, 14), "tile.moonBlock.bricks");
                GalacticraftCore.logger.info("Activating CodeChicken Microblocks compatibility.");
            }
        } catch (Exception e)
        {
        }
    }

    public static boolean isAndroid(EntityPlayer player)
    {
        if (CompatibilityManager.modMatterOverdriveLoaded)
        {
            //          Equivalent to:
            //            AndroidPlayer androidPlayer = AndroidPlayer.get(player);
            //            return (androidPlayer != null && androidPlayer.isAndroid());
            try
            {
                Object androidPlayer = CompatibilityManager.androidPlayerGet.invoke(null, player);
                if (androidPlayer != null)
                {
                    return (Boolean) CompatibilityManager.androidPlayerIsAndroid.invoke(androidPlayer);
                }
            } catch (Exception ignore)
            {
            }
        }
        return false;
    }

    public static boolean useAluDust()
    {
        return modIc2Loaded || modAppEngLoaded || modTELoaded || modEIOLoaded || modAALoaded;
    }
}
