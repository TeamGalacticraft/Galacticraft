/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.config.Configuration;

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.PlayerUtil;

public class ChunkLoadingCallback implements LoadingCallback
{
    private static HashMap<String, HashMap<Integer, HashSet<BlockPos>>> chunkLoaderList = new HashMap<>();


    private static boolean       loaded;
    private static boolean       configLoaded;
    private static Configuration config;
    private static boolean       loadOnLogin;
    private static boolean       dirtyData;

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world)
    {
        for (Ticket ticket : tickets)
        {
            NBTTagCompound nbt = ticket.getModData();

            if (nbt != null)
            {
                int tileX = nbt.getInteger("ChunkLoaderTileX");
                int tileY = nbt.getInteger("ChunkLoaderTileY");
                int tileZ = nbt.getInteger("ChunkLoaderTileZ");
                TileEntity tile = world.getTileEntity(new BlockPos(tileX, tileY, tileZ));

                if (tile instanceof IChunkLoader)
                {
                    ((IChunkLoader) tile).onTicketLoaded(ticket, false);
                }
            }
        }
    }

    public static void loadConfig(File file)
    {
        if (!ChunkLoadingCallback.configLoaded)
        {
            ChunkLoadingCallback.config = new Configuration(file);
        }

        try
        {
            ChunkLoadingCallback.loadOnLogin = ChunkLoadingCallback.config.get("CHUNKLOADING", "LoadOnLogin", true, "If you don't want each player's chunks to load when they log in, set to false.").getBoolean(true);
        } catch (final Exception e)
        {
            GCLog.severe("Problem loading chunkloading config (\"core.conf\")");
        } finally
        {
            if (ChunkLoadingCallback.config.hasChanged())
            {
                ChunkLoadingCallback.config.save();
            }

            ChunkLoadingCallback.configLoaded = true;
        }
    }

    public static void addToList(World world, int x, int y, int z, String playerName)
    {
        HashMap<Integer, HashSet<BlockPos>> dimensionMap = ChunkLoadingCallback.chunkLoaderList.get(playerName);

        if (dimensionMap == null)
        {
            dimensionMap = new HashMap<>();
            ChunkLoadingCallback.chunkLoaderList.put(playerName, dimensionMap);
        }

        HashSet<BlockPos> chunkLoaders = dimensionMap.get(world.provider.getDimension());

        if (chunkLoaders == null)
        {
            chunkLoaders = new HashSet<>();
        }

        chunkLoaders.add(new BlockPos(x, y, z));
        dimensionMap.put(GCCoreUtil.getDimensionID(world), chunkLoaders);
        ChunkLoadingCallback.chunkLoaderList.put(playerName, dimensionMap);
        ChunkLoadingCallback.dirtyData = true;
    }

    public static void forceChunk(Ticket ticket, World world, int x, int y, int z, String playerName)
    {
        ChunkLoadingCallback.addToList(world, x, y, z, playerName);
        ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
        ForgeChunkManager.forceChunk(ticket, chunkPos);
    }

    public static void save(WorldServer world)
    {
        if (!ChunkLoadingCallback.dirtyData)
        {
            return;
        }

        File saveDir = ChunkLoadingCallback.getSaveDir();

        if (saveDir != null)
        {
            File saveFile = new File(saveDir, "chunkloaders.dat");

            if (!saveFile.exists())
            {
                try
                {
                    if (!saveFile.createNewFile())
                    {
                        GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath());
                    }
                } catch (IOException e)
                {
                    GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }

            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(saveFile);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            if (fos != null)
            {
                DataOutputStream dataStream = new DataOutputStream(fos);
                try
                {
                    dataStream.writeInt(ChunkLoadingCallback.chunkLoaderList.size());

                    for (Entry<String, HashMap<Integer, HashSet<BlockPos>>> playerEntry : ChunkLoadingCallback.chunkLoaderList.entrySet())
                    {
                        dataStream.writeUTF(playerEntry.getKey());
                        dataStream.writeInt(playerEntry.getValue().size());

                        for (Entry<Integer, HashSet<BlockPos>> dimensionEntry : playerEntry.getValue().entrySet())
                        {
                            dataStream.writeInt(dimensionEntry.getKey());
                            dataStream.writeInt(dimensionEntry.getValue().size());

                            for (BlockPos coords : dimensionEntry.getValue())
                            {
                                dataStream.writeInt(coords.getX());
                                dataStream.writeInt(coords.getY());
                                dataStream.writeInt(coords.getZ());
                            }
                        }
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    dataStream.close();
                    fos.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        ChunkLoadingCallback.dirtyData = false;
    }

    private static File getSaveDir()
    {
        if (DimensionManager.getWorld(0) != null)
        {
            File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "galacticraft");

            if (!saveDir.exists())
            {
                if (!saveDir.mkdirs())
                {
                    GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath());
                }
            }

            return saveDir;
        }

        return null;
    }

    public static void load(WorldServer world)
    {
        if (ChunkLoadingCallback.loaded)
        {
            return;
        }

        DataInputStream dataStream = null;

        try
        {
            File saveDir = ChunkLoadingCallback.getSaveDir();

            if (saveDir != null)
            {
                if (!saveDir.exists())
                {
                    if (!saveDir.mkdirs())
                    {
                        GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath());
                    }
                }

                File saveFile = new File(saveDir, "chunkloaders.dat");

                if (saveFile.exists())
                {
                    dataStream = new DataInputStream(new FileInputStream(saveFile));

                    int playerCount = dataStream.readInt();

                    for (int l = 0; l < playerCount; l++)
                    {
                        String ownerName = dataStream.readUTF();

                        int mapSize = dataStream.readInt();
                        HashMap<Integer, HashSet<BlockPos>> dimensionMap = new HashMap<>();

                        for (int i = 0; i < mapSize; i++)
                        {
                            int dimensionID = dataStream.readInt();
                            HashSet<BlockPos> coords = new HashSet<>();
                            dimensionMap.put(dimensionID, coords);
                            int coordSetSize = dataStream.readInt();

                            for (int j = 0; j < coordSetSize; j++)
                            {
                                coords.add(new BlockPos(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()));
                            }
                        }

                        ChunkLoadingCallback.chunkLoaderList.put(ownerName, dimensionMap);
                    }

                    dataStream.close();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();

            if (dataStream != null)
            {
                try
                {
                    dataStream.close();
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }

        ChunkLoadingCallback.loaded = true;
        ChunkLoadingCallback.dirtyData = false;
    }

    public static void onPlayerLogin(EntityPlayer player)
    {
        for (Entry<String, HashMap<Integer, HashSet<BlockPos>>> playerEntry : ChunkLoadingCallback.chunkLoaderList.entrySet())
        {
            if (PlayerUtil.getName(player).equals(playerEntry.getKey()))
            {
                for (Entry<Integer, HashSet<BlockPos>> dimensionEntry : playerEntry.getValue().entrySet())
                {
                    int dimID = dimensionEntry.getKey();

                    if (ChunkLoadingCallback.loadOnLogin)
                    {
                        player.world.getMinecraftServer().getWorld(dimID);
                    }
                }
            }
        }
    }
}
