/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.dynamicdimensions.api.event.DynamicDimensionLoadCallback;
import dev.galacticraft.dynamicdimensions.impl.registry.RegistryUtil;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements SatelliteAccessor {
    @Unique
    private final Map<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> satellites = new HashMap<>();

    @Shadow
    @Final
    protected LevelStorageSource.LevelStorageAccess storageSource;

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Override
    public @Unmodifiable Map<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> galacticraft$getSatellites() {
        return ImmutableMap.copyOf(this.satellites);
    }

    @Override
    public void galacticraft$addSatellite(ResourceLocation id, CelestialBody<SatelliteConfig, SatelliteType> satellite) {
        this.satellites.put(id, satellite);
        RegistryUtil.registerUnfreeze(this.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY), id, satellite);
        Constant.LOGGER.info("Added satellite with id {}", id);
    }

    @Override
    public void galacticraft$removeSatellite(ResourceLocation id) {
        this.satellites.remove(id);
        RegistryUtil.unregister(this.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY), id);
        Constant.LOGGER.info("Removed satellite with id {}", id);
    }

    @Inject(method = "saveEverything", at = @At("RETURN"))
    private void galacticraft_saveSatellites(boolean suppressLogs, boolean bl, boolean bl2, CallbackInfoReturnable<Boolean> cir) {
        Path path = this.storageSource.getLevelPath(LevelResource.ROOT);
        ListTag nbt = new ListTag();
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess());
        for (Map.Entry<ResourceLocation, CelestialBody<SatelliteConfig, SatelliteType>> entry : this.satellites.entrySet()) {
            CompoundTag compound = (CompoundTag) SatelliteConfig.CODEC.encode(entry.getValue().config(), ops, new CompoundTag()).getOrThrow();
            compound.putString("id", entry.getKey().toString());
            nbt.add(compound);
        }
        CompoundTag compound = new CompoundTag();
        compound.put("satellites", nbt);
        try {
            NbtIo.writeCompressed(compound, path.resolve("satellites.dat"));
        } catch (Throwable exception) {
            Constant.LOGGER.fatal("Failed to write satellite data!", exception);
        }
    }

    @Override
    public void galacticraft$loadSatellites(DynamicDimensionLoadCallback.DynamicDimensionLoader dynamicDimensionLoader) {
        Path worldFile = this.storageSource.getLevelPath(LevelResource.ROOT);
        if (Files.exists(worldFile.resolve("satellites.dat"))) {
            try {
                ListTag nbt = NbtIo.readCompressed(worldFile.resolve("satellites.dat"), NbtAccounter.unlimitedHeap()).getList("satellites", Tag.TAG_COMPOUND);
                RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess());
                Constant.LOGGER.info("Loading {} satellites", nbt.size());
                for (Tag compound : nbt) {
                    assert compound instanceof CompoundTag : "Not a compound?!";
                    ResourceLocation id = ResourceLocation.parse(((CompoundTag) compound).getString("id"));
                    DataResult<Pair<SatelliteConfig, Tag>> decode = SatelliteConfig.CODEC.decode(ops, compound);
                    if (decode.error().isPresent()) {
                        Constant.LOGGER.error("Skipping satellite '{}' - {}", id, decode.error().get().message());
                        continue;
                    }
                    CelestialBody<SatelliteConfig, SatelliteType> satellite = new CelestialBody<>(SatelliteType.INSTANCE, decode.getOrThrow().getFirst());
                    this.galacticraft$addSatellite(id, satellite);

                    LevelStem levelStem = satellite.config().getOptions();
                    dynamicDimensionLoader.loadDynamicDimension(id, levelStem.generator(), levelStem.type().value());
                }
            } catch (Throwable exception) {
                throw new RuntimeException("Failed to read satellite data!", exception);
            }
        } else {
            Constant.LOGGER.info("File not found: satellites.dat");
        }
    }
}
