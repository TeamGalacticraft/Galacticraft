package dev.galacticraft.mod.world.gen.custom;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.GCDataGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class AsteroidSaveData extends SavedData{

    public static final String saveDataID = "dimensions/galacticraft/asteroid/data";

    public CompoundTag datacompound;

    public AsteroidSaveData(String s)
    {
        super();
        this.datacompound = new CompoundTag();
    }

    public void readFromNBT(CompoundTag nbt)
    {
        this.datacompound = nbt.getCompound("asteroids");
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.put("asteroids", this.datacompound);
        return nbt;
    }

    public static AsteroidSaveData load(CompoundTag compound) {
        String value = compound.getString("someValue");
        return new AsteroidSaveData(value);
    }
}
