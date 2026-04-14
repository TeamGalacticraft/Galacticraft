/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.dimension;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import micdoodle8.mods.galacticraft.core.Constants;

public class WorldDataSpaceRaces extends WorldSavedData
{

    public static final String saveDataID = Constants.GCDATAFOLDER + "GCSpaceRaceData";
    private NBTTagCompound dataCompound;

    public WorldDataSpaceRaces(String id)
    {
        super(id);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        SpaceRaceManager.loadSpaceRaces(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        return SpaceRaceManager.saveSpaceRaces(nbt);
    }

    public static WorldDataSpaceRaces initWorldData(World world)
    {
        WorldDataSpaceRaces worldData = (WorldDataSpaceRaces) world.loadData(WorldDataSpaceRaces.class, WorldDataSpaceRaces.saveDataID);

        if (worldData == null)
        {
            worldData = new WorldDataSpaceRaces(WorldDataSpaceRaces.saveDataID);
            world.setData(WorldDataSpaceRaces.saveDataID, worldData);
            worldData.dataCompound = new NBTTagCompound();
            worldData.markDirty();
        }

        return worldData;
    }

    @Override
    public boolean isDirty()
    {
        return true;
    }
}
