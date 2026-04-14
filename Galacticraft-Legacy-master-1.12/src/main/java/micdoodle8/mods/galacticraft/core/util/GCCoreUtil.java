/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.Language;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import micdoodle8.mods.galacticraft.annotations.ForRemoval;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.inventory.ContainerBuggy;
import micdoodle8.mods.galacticraft.core.inventory.ContainerParaChest;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;

/**
 * Starting with Release 4.1.0 this class will be removed and all methods moved
 * to a dedicated utility class. At this time all planned utility classes will
 * be internal and non-accessible.
 *
 */
@ForRemoval(deadline = "4.1.0")
public class GCCoreUtil
{

    public static int nextID = 0;
    private static boolean deobfuscated;
    private static String lastLang = "";
    public static boolean langDisable;
    private static MinecraftServer serverCached;

    static
    {
        try
        {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isDeobfuscated()
    {
        return deobfuscated;
    }

    public static void openBuggyInv(EntityPlayerMP player, IInventory buggyInv, int type)
    {
        player.getNextWindowId();
        player.closeContainer();
        int id = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_OPEN_PARACHEST_GUI, GCCoreUtil.getDimensionID(player.world), new Object[] {id, 0, 0}), player);
        player.openContainer = new ContainerBuggy(player.inventory, buggyInv, type, player);
        player.openContainer.windowId = id;
        player.openContainer.addListener(player);
    }

    public static void openParachestInv(EntityPlayerMP player, EntityLanderBase landerInv)
    {
        player.getNextWindowId();
        player.closeContainer();
        int windowId = player.currentWindowId;
        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_OPEN_PARACHEST_GUI, GCCoreUtil.getDimensionID(player.world), new Object[] {windowId, 1, landerInv.getEntityId()}), player);
        player.openContainer = new ContainerParaChest(player.inventory, landerInv, player);
        player.openContainer.windowId = windowId;
        player.openContainer.addListener(player);
    }

    public static int nextInternalID()
    {
        GCCoreUtil.nextID++;
        return GCCoreUtil.nextID - 1;
    }

    public static void registerGalacticraftCreature(Class<? extends Entity> clazz, String name, int back, int fore)
    {
        registerGalacticraftNonMobEntity(clazz, name, 80, 3, true);
        int nextEggID = getNextValidID();
        if (nextEggID < 65536)
        {
            ResourceLocation resourcelocation = new ResourceLocation(Constants.MOD_ID_CORE, name);
            EntityList.ENTITY_EGGS.put(resourcelocation, new EntityList.EntityEggInfo(resourcelocation, back, fore));
        }
    }

    public static int getNextValidID()
    {
        int eggID = 255;

        // Non-global entity IDs - for egg ID purposes - can be greater than 255
        // The spawn egg will have this metadata. Metadata up to 65535 is
        // acceptable (see potions).

        do
        {
            eggID++;
        } while (net.minecraftforge.registries.GameData.getEntityRegistry().getValue(eggID) != null);

        return eggID;
    }

    public static void registerGalacticraftNonMobEntity(Class<? extends Entity> var0, String var1, int trackingDistance, int updateFreq, boolean sendVel)
    {
        ResourceLocation registryName = new ResourceLocation(Constants.MOD_ID_CORE, var1);
        EntityRegistry.registerModEntity(registryName, var0, var1, nextInternalID(), GalacticraftCore.instance, trackingDistance, updateFreq, sendVel);
    }

    public static void registerGalacticraftItem(String key, Item item)
    {
        registerGalacticraftItem(key, new ItemStack(item));
    }

    public static void registerGalacticraftItem(String key, Item item, int metadata)
    {
        registerGalacticraftItem(key, new ItemStack(item, 1, metadata));
    }

    public static void registerGalacticraftItem(String key, ItemStack stack)
    {
        GalacticraftCore.itemList.add(stack);
    }

    public static void registerGalacticraftBlock(String key, Block block)
    {
        GalacticraftCore.blocksList.add(block);
    }

    public static String translate(String key)
    {
        String result = I18n.translateToLocal(key);
        int comment = result.indexOf('#');
        String ret = (comment > 0) ? result.substring(0, comment).trim() : result;
        for (int i = 0; i < key.length(); ++i)
        {
            Character c = key.charAt(i);
            if (Character.isUpperCase(c))
            {
                System.err.println(ret);
            }
        }
        return ret;
    }

    public static List<String> translateWithSplit(String key)
    {
        String translated = translate(key);
        int comment = translated.indexOf('#');
        translated = (comment > 0) ? translated.substring(0, comment).trim() : translated;
        return Arrays.asList(translated.split("\\$"));
    }

    public static String translateWithFormat(String key, Object... values)
    {
        String result = I18n.translateToLocalFormatted(key, values);
        int comment = result.indexOf('#');
        String ret = (comment > 0) ? result.substring(0, comment).trim() : result;
        for (int i = 0; i < key.length(); ++i)
        {
            Character c = key.charAt(i);
            if (Character.isUpperCase(c))
            {
                System.err.println(ret);
            }
        }
        return ret;
    }

    public static void drawStringRightAligned(String string, int x, int y, int color, FontRenderer fontRendererObj)
    {
        fontRendererObj.drawString(string, x - fontRendererObj.getStringWidth(string), y, color);
    }

    public static void drawStringCentered(String string, int x, int y, int color, FontRenderer fontRendererObj)
    {
        fontRendererObj.drawString(string, x - fontRendererObj.getStringWidth(string) / 2, y, color);
    }

    public static String lowerCaseNoun(String string)
    {
        Language l = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        if (l.getLanguageCode().equals("de_DE"))
        {
            return string;
        }
        return GCCoreUtil.translate(string).toLowerCase();
    }

    public static int getDimensionID(World world)
    {
        return world.provider.getDimension();
    }

    public static int getDimensionID(WorldProvider provider)
    {
        return provider.getDimension();
    }

    public static int getDimensionID(TileEntity tileEntity)
    {
        return tileEntity.getWorld().provider.getDimension();
    }

    public static WorldServer[] getWorldServerList()
    {
        MinecraftServer server = getServer();
        if (server != null)
        {
            return server.worlds;
        }
        return new WorldServer[0];
    }

    public static WorldServer[] getWorldServerList(World world)
    {
        if (world instanceof WorldServer)
        {
            return ((WorldServer) world).getMinecraftServer().worlds;
        }
        return GCCoreUtil.getWorldServerList();
    }

    public static void sendToAllDimensions(EnumSimplePacket packetType, Object[] data)
    {
        for (WorldServer world : GCCoreUtil.getWorldServerList())
        {
            int id = getDimensionID(world);
            GalacticraftCore.packetPipeline.sendToDimension(new PacketSimple(packetType, id, data), id);
        }
    }

    public static void sendToAllAround(PacketSimple packet, World world, int dimID, BlockPos pos, double radius)
    {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        double r2 = radius * radius;
        for (EntityPlayer playerMP : world.playerEntities)
        {
            if (playerMP.dimension == dimID)
            {
                final double dx = x - playerMP.posX;
                final double dy = y - playerMP.posY;
                final double dz = z - playerMP.posZ;

                if (dx * dx + dy * dy + dz * dz < r2)
                {
                    GalacticraftCore.packetPipeline.sendTo(packet, (EntityPlayerMP) playerMP);
                }
            }
        }
    }

    /**
     * Call this to obtain a seeded random which will be the SAME on client and
     * server. This means EntityItems won't jump position, for example.
     */
    public static Random getRandom(BlockPos pos)
    {
        long blockSeed = ((pos.getY() << 28) + pos.getX() + 30000000 << 28) + pos.getZ() + 30000000;
        return new Random(blockSeed);
    }

    /**
     * Returns the angle of the compass (0 - 360 degrees) needed to reach the
     * given position offset
     */
    public static float getAngleForRelativePosition(double nearestX, double nearestZ)
    {
        return ((float) MathHelper.atan2(nearestX, -nearestZ) * Constants.RADIANS_TO_DEGREES + 360F) % 360F;
    }

    /**
     * Custom getEffectiveSide method, covering more cases than FMLCommonHandler
     */
    public static Side getEffectiveSide()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER || Thread.currentThread().getName().startsWith("Netty Epoll Server IO"))
        {
            return Side.SERVER;
        }

        return Side.CLIENT;
    }

    public static MinecraftServer getServer()
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null)
        {
            // This may be called from a different thread e.g. MapUtil
            // If on a different thread,
            // FMLCommonHandler.instance().getMinecraftServerInstance() can
            // return null on LAN servers
            // (I think because the FMLCommonHandler wrongly picks the client
            // proxy if it's not in the Integrated Server thread)
            return serverCached;
        }
        return server;
    }

    public static ItemStack getMatchingItemEitherHand(EntityPlayer player, Item item)
    {
        ItemStack stack = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (stack != null && stack.getItem() == item)
        {
            return stack;
        }
        stack = player.inventory.offHandInventory.get(0);
        if (stack != null && stack.getItem() == item)
        {
            return stack;
        }
        return null;
    }

    // For performance
    public static List<BlockPos> getPositionsAdjoining(BlockPos pos)
    {
        LinkedList<BlockPos> result = new LinkedList<>();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (y > 0)
            result.add(new BlockPos(x, y - 1, z));
        if (y < 255)
            result.add(new BlockPos(x, y + 1, z));
        result.add(new BlockPos(x, y, z - 1));
        result.add(new BlockPos(x, y, z + 1));
        result.add(new BlockPos(x - 1, y, z));
        result.add(new BlockPos(x + 1, y, z));
        return result;
    }

    // For performance
    public static void getPositionsAdjoining(int x, int y, int z, List<BlockPos> result)
    {
        result.clear();
        if (y > 0)
            result.add(new BlockPos(x, y - 1, z));
        if (y < 255)
            result.add(new BlockPos(x, y + 1, z));
        result.add(new BlockPos(x, y, z - 1));
        result.add(new BlockPos(x, y, z + 1));
        result.add(new BlockPos(x - 1, y, z));
        result.add(new BlockPos(x + 1, y, z));
    }

    public static void getPositionsAdjoiningLoaded(int x, int y, int z, List<BlockPos> result, World world)
    {
        result.clear();
        if (y > 0)
            result.add(new BlockPos(x, y - 1, z));
        if (y < 255)
            result.add(new BlockPos(x, y + 1, z));
        BlockPos pos = new BlockPos(x, y, z - 1);
        if ((z & 15) > 0 || world.isBlockLoaded(pos, false))
            result.add(pos);
        pos = new BlockPos(x, y, z + 1);
        if ((z & 15) < 15 || world.isBlockLoaded(pos, false))
            result.add(pos);
        pos = new BlockPos(x - 1, y, z);
        if ((x & 15) > 0 || world.isBlockLoaded(pos, false))
            result.add(pos);
        pos = new BlockPos(x + 1, y, z);
        if ((x & 15) < 15 || world.isBlockLoaded(pos, false))
            result.add(pos);
    }

    // For performance
    public static void getPositionsAdjoining(BlockPos pos, List<BlockPos> result)
    {
        result.clear();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (y > 0)
            result.add(new BlockPos(x, y - 1, z));
        if (y < 255)
            result.add(new BlockPos(x, y + 1, z));
        result.add(new BlockPos(x, y, z - 1));
        result.add(new BlockPos(x, y, z + 1));
        result.add(new BlockPos(x - 1, y, z));
        result.add(new BlockPos(x + 1, y, z));
    }

    public static void spawnItem(World world, BlockPos pos, ItemStack stack)
    {
        float var = 0.7F;
        while (!stack.isEmpty())
        {
            double dx = world.rand.nextFloat() * var + (1.0F - var) * 0.5D;
            double dy = world.rand.nextFloat() * var + (1.0F - var) * 0.5D;
            double dz = world.rand.nextFloat() * var + (1.0F - var) * 0.5D;
            EntityItem entityitem = new EntityItem(world, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, stack.splitStack(world.rand.nextInt(21) + 10));

            entityitem.setPickupDelay(10);

            world.spawnEntity(entityitem);
        }
    }

    public static void notifyStarted(MinecraftServer server)
    {
        serverCached = server;
    }
}
