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

package dev.galacticraft.impl.universe.celestialbody.type;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.satellite.Satellite;
import dev.galacticraft.api.satellite.SatelliteOwnershipData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.Tiered;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.dynamicdimensions.api.DynamicDimensionRegistry;
import dev.galacticraft.impl.network.s2c.AddSatellitePayload;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.impl.universe.celestialbody.config.StarConfig;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.config.ring.DefaultCelestialRingDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.ring.DefaultCelestialRingDisplayType;
import dev.galacticraft.impl.universe.position.config.OrbitalCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.dimension.AsteroidSkyRenderer;
import dev.galacticraft.mod.data.gen.SatelliteChunkGenerator;
import dev.galacticraft.mod.tag.GCBlockTags;
import dev.galacticraft.mod.util.Translations;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalLong;

public class SatelliteType extends CelestialBodyType<SatelliteConfig> implements Satellite<SatelliteConfig>, Tiered<SatelliteConfig> {
    public static final SatelliteType INSTANCE = new SatelliteType(SatelliteConfig.CODEC);
    public static final ChunkProgressListener EMPTY_PROGRESS_LISTENER = new ChunkProgressListener() {
        @Override
        public void updateSpawnPos(ChunkPos spawnPos) {
        }

        @Override
        public void onStatusChange(ChunkPos pos, @Nullable ChunkStatus status) {
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }
    };
    private static final GasComposition EMPTY_GAS_COMPOSITION = new GasComposition.Builder()
            .temperature(-2.0)
            .pressure(0)
            .build();
    private static final Component NAME = Component.translatable(Translations.CelestialBody.SATELLITE);
    private static final Component DESCRIPTION = Component.translatable(Translations.CelestialBody.SATELLITE_DESC);

    protected SatelliteType(Codec<SatelliteConfig> codec) {
        super(codec);
    }

    @ApiStatus.Internal
    public static CelestialBody<SatelliteConfig, SatelliteType> registerSatellite(@NotNull MinecraftServer server, @NotNull ServerPlayer player, ResourceKey<CelestialBody<?, ?>> parent, StructureTemplate structure, Registry<CelestialBody<?, ?>> celestialBodyRegistry) {
        ResourceLocation id = ResourceLocation.parse(parent.location() + "_" + player.getScoreboardName().toLowerCase(Locale.ROOT));
        DimensionType type = new DimensionType(
                OptionalLong.empty(), // fixedTime
                true, // hasSkyLight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                false, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                GCBlockTags.INFINIBURN_SATELLITE, // infiniburn
                Constant.id("satellite"), // effectsLocation
                0, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformInt.of(0, 7), 0)
        );
        SatelliteChunkGenerator chunkGenerator = new SatelliteChunkGenerator(server.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(GCBiomes.SPACE), structure);
        SatelliteOwnershipData ownershipData = SatelliteOwnershipData.create(player.getUUID(), player.getScoreboardName(), new LinkedList<>(), false);
        CelestialPosition<?, ?> position = new CelestialPosition<>(OrbitalCelestialPositionType.INSTANCE, new OrbitalCelestialPositionConfig(1550, 10.0f, 0.0F, false));
        CelestialDisplay<?, ?> display = new CelestialDisplay<>(IconCelestialDisplayType.INSTANCE, new IconCelestialDisplayConfig(Constant.CelestialBody.SPACE_STATION, 0, 0, 16, 16));
        CelestialRingDisplay<?, ?> ring = new CelestialRingDisplay<>(DefaultCelestialRingDisplayType.INSTANCE, new DefaultCelestialRingDisplayConfig());
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, id);
        DynamicDimensionRegistry registry = DynamicDimensionRegistry.from(server);
        assert server.getLevel(key) == null : "World already registered?!";
        assert registry.anyDimensionExists(id) : "Dimension Type already registered?!";
        return create(id, server, parent, position, display, ring, chunkGenerator, type, ownershipData, "", celestialBodyRegistry, key);
    }

    @ApiStatus.Internal
    public static CelestialBody<SatelliteConfig, SatelliteType> create(ResourceLocation id, MinecraftServer server, ResourceKey<CelestialBody<?, ?>> parentResourceKey, CelestialPosition<?, ?> position, CelestialDisplay<?, ?> display, CelestialRingDisplay<?, ?> ring,
                                                                       ChunkGenerator generator, DimensionType type, SatelliteOwnershipData ownershipData, String name, Registry<CelestialBody<?, ?>> celestialBodyRegistry, ResourceKey<Level> key) {
        Constant.LOGGER.debug("Attempting to create a world dynamically ({})", id);

        Holder<CelestialTeleporter<?, ?>> direct = server.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_TELEPORTER).getHolderOrThrow(BuiltinObjects.DIRECT_CELESTIAL_TELEPORTER);

        CelestialBody<?, ?> parent = celestialBodyRegistry.get(parentResourceKey);

        // DimensionRenderingRegistry.registerSkyRenderer(key, AsteroidSkyRenderer.INSTANCE);

        assert parent != null;
        SatelliteConfig config = new SatelliteConfig(id, name, Optional.of(parentResourceKey), position, display, ring, ownershipData, ResourceKey.create(Registries.DIMENSION, id), direct, EMPTY_GAS_COMPOSITION, 1.0f, parent.type() instanceof Tiered<?> ? ((Tiered) parent.type()).accessWeight(parent.config()) : 1, new LevelStem(Holder.direct(type), generator));
        CelestialBody<SatelliteConfig, SatelliteType> satellite = INSTANCE.configure(config);

        ((SatelliteAccessor) server).galacticraft$addSatellite(id, satellite);
        ((DynamicDimensionRegistry) server).createDynamicDimension(id, generator, type);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, new AddSatellitePayload(id, satellite.config()));
        }
        return satellite;
    }

    @Override
    public @NotNull Component name(SatelliteConfig config) {
        return NAME;
    }

    @Override
    public @Nullable Optional<ResourceKey<CelestialBody<?, ?>>> parent(SatelliteConfig config) {
        return config.getParent();
    }

    @Override
    public @NotNull Optional<ResourceKey<Galaxy>> galaxy(Registry<CelestialBody<?, ?>> registry, SatelliteConfig config) {
        if (config.getParent().isPresent()) {
            CelestialBody<?, ?> body = registry.get(config.getParent().get());
            if (body != null && body.type() instanceof StarType starType) {
                return starType.galaxy(registry, (StarConfig) body.config());
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Component description(SatelliteConfig config) {
        return DESCRIPTION;
    }

    @Override
    public @NotNull CelestialPosition<?, ?> position(SatelliteConfig config) {
        return config.getPosition();
    }

    @Override
    public @NotNull CelestialDisplay<?, ?> display(SatelliteConfig config) {
        return config.getDisplay();
    }

    @Override
    public @NotNull CelestialRingDisplay<?, ?> ring(SatelliteConfig config) {
        return config.getRing();
    }

    @Override
    public SatelliteOwnershipData ownershipData(SatelliteConfig config) {
        return config.getOwnershipData();
    }

    @Override
    public void setCustomName(@NotNull String text, SatelliteConfig config) {
        config.setCustomName(text);
    }

    @Override
    public @NotNull String getCustomName(SatelliteConfig config) {
        return config.getCustomName();
    }

    @Override
    public @NotNull ResourceKey<Level> world(SatelliteConfig config) {
        return config.getWorld();
    }

    @Override
    public Holder<CelestialTeleporter<?, ?>> teleporter(SatelliteConfig config) {
        return config.getTeleporter();
    }

    @Override
    public @NotNull GasComposition atmosphere(SatelliteConfig config) {
        return config.getAtmosphere();
    }

    @Override
    public float gravity(SatelliteConfig config) {
        return config.getGravity();
    }

    @Override
    public int accessWeight(SatelliteConfig config) {
        return config.getAccessWeight();
    }

    @Override
    public CelestialBody<SatelliteConfig, SatelliteType> configure(SatelliteConfig config) {
        return new CelestialBody<>(this, config);
    }

    @Override
    public int temperature(RegistryAccess access, long time, SatelliteConfig config) {
        return time % 24000 < 12000 ? 121 : -157; //todo: gradual temperature change
    }
}
