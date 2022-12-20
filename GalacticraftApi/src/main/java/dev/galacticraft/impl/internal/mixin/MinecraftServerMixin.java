/*
 * Copyright (c) 2019-2022 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.impl.internal.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements SatelliteAccessor {
    @Unique private final Map<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> satellites = new HashMap<>();

    @Shadow @Final protected LevelStorageSource.LevelStorageAccess storageSource;
    @Shadow @Final private Executor executor;
    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> levels;
    @Shadow public abstract WorldData getWorldData();

    @Shadow @Nullable public abstract ServerLevel getLevel(ResourceKey<Level> key);

    @Override
    public @Unmodifiable Map<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> getSatellites() {
        return ImmutableMap.copyOf(this.satellites);
    }

    @Override
    public void addSatellite(ResourceLocation id, CelestialBody<SatelliteConfig, SatelliteType> satellite) {
        this.satellites.put(id, satellite);
    }

    @Override
    public void removeSatellite(ResourceLocation id) {
        this.satellites.remove(id);
    }

    @Inject(method = "saveEverything", at = @At("RETURN"))
    private void galacticraft_saveSatellites(boolean suppressLogs, boolean bl, boolean bl2, CallbackInfoReturnable<Boolean> cir) {
        Path path = this.storageSource.getLevelPath(LevelResource.ROOT);
        ListTag nbt = new ListTag();
        for (Map.Entry<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> entry : this.satellites.entrySet()) {
            CompoundTag compound = (CompoundTag) SatelliteConfig.CODEC.encode(entry.getValue().config(), NbtOps.INSTANCE, new CompoundTag()).get().orThrow();
            compound.putString("id", entry.getKey().toString());
            nbt.add(compound);
        }
        CompoundTag compound = new CompoundTag();
        compound.put("satellites", nbt);
        try {
            NbtIo.writeCompressed(compound, new File(path.toFile(), "satellites.dat"));
        } catch (Throwable exception) {
            Constant.LOGGER.fatal("Failed to write satellite data!", exception);
        }
    }

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z", shift = At.Shift.AFTER))
    private void galacticraft_loadSatellites(CallbackInfo ci) {
        File worldFile = this.storageSource.getLevelPath(LevelResource.ROOT).toFile();
        if (new File(worldFile, "satellites.dat").exists()) {
            try {
                ListTag nbt = NbtIo.readCompressed(new File(worldFile, "satellites.dat")).getList("satellites", NbtType.COMPOUND);
                assert nbt != null : "NBT list was null";
                for (Tag compound : nbt) {
                    assert compound instanceof CompoundTag : "Not a compound?!";
                    this.satellites.put(new ResourceLocation(((CompoundTag) compound).getString("id")), new CelestialBody<>(SatelliteType.INSTANCE, SatelliteConfig.CODEC.decode(NbtOps.INSTANCE, compound).get().orThrow().getFirst()));
                }

                WorldBorder worldBorder = getLevel(Level.OVERWORLD).getWorldBorder();
                for (Map.Entry<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> entry : this.satellites.entrySet()) {
                    ChunkGenerator chunkGenerator = entry.getValue().config().dimensionOptions().generator();
                    DerivedLevelData unmodifiableLevelProperties = new DerivedLevelData(getWorldData(), getWorldData().overworldData());
                    ServerLevel world = new ServerLevel((MinecraftServer) (Object) this, executor, storageSource, unmodifiableLevelProperties, ResourceKey.create(Registry.DIMENSION_REGISTRY, entry.getKey()), entry.getValue().config().dimensionOptions(), SatelliteType.EMPTY_PROGRESS_LISTENER, getWorldData().worldGenSettings().isDebug(), BiomeManager.obfuscateSeed(getWorldData().worldGenSettings().seed()), ImmutableList.of(), false);
                    worldBorder.addListener(new BorderChangeListener.DelegateBorderChangeListener(world.getWorldBorder()));
                    levels.put(ResourceKey.create(Registry.DIMENSION_REGISTRY, entry.getKey()), world);
                }
            } catch (Throwable exception) {
                throw new RuntimeException("Failed to reade satellite data!", exception);
            }
        }
    }
}
