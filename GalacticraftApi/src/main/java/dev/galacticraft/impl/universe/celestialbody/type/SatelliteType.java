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

package dev.galacticraft.impl.universe.celestialbody.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.satellite.Satellite;
import dev.galacticraft.api.satellite.SatelliteOwnershipData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.dyndims.api.DynamicDimensionRegistry;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.internal.mixin.MinecraftServerAccessor;
import dev.galacticraft.impl.internal.world.gen.SatelliteChunkGenerator;
import dev.galacticraft.impl.internal.world.gen.biome.GcApiBiomes;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.OrbitalCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.config.SatelliteConfig;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.DerivedLevelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalLong;

public class SatelliteType extends CelestialBodyType<SatelliteConfig> implements Satellite<SatelliteConfig>, Landable<SatelliteConfig> {
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
    private static final GasComposition EMPTY_GAS_COMPOSITION = new GasComposition.Builder().build();
    private static final Component NAME = Component.translatable("ui.galacticraft-api.satellite.name");
    private static final Component DESCRIPTION = Component.translatable("ui.galacticraft-api.satellite.description");

    protected SatelliteType(Codec<SatelliteConfig> codec) {
        super(codec);
    }

    @ApiStatus.Internal
    public static CelestialBody<SatelliteConfig, SatelliteType> registerSatellite(@NotNull MinecraftServer server, @NotNull ServerPlayer player, @NotNull CelestialBody<?, ?> parent, StructureTemplate structure) {
        ResourceLocation id = new ResourceLocation(Objects.requireNonNull(server.registryAccess().registryOrThrow(AddonRegistry.CELESTIAL_BODY_KEY).getKey(parent)) + "_" + player.getScoreboardName().toLowerCase(Locale.ROOT));
        DimensionType type = new DimensionType(OptionalLong.empty(), true, false, false, true, 1, false, false, 0, 256, 256, TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Constant.MOD_ID, "infiniburn_space")), new ResourceLocation(Constant.MOD_ID, "space_sky"), 0, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0));
        SatelliteChunkGenerator chunkGenerator = new SatelliteChunkGenerator(server.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), Holder.direct(GcApiBiomes.SPACE), structure);
        SatelliteOwnershipData ownershipData = SatelliteOwnershipData.create(player.getUUID(), player.getScoreboardName(), new LinkedList<>(), false);
        CelestialPosition<?, ?> position = new CelestialPosition<>(OrbitalCelestialPositionType.INSTANCE, new OrbitalCelestialPositionConfig(1550, 10.0f, 0.0F, false));
        CelestialDisplay<?, ?> display = new CelestialDisplay<>(IconCelestialDisplayType.INSTANCE, new IconCelestialDisplayConfig(new ResourceLocation(Constant.MOD_ID, "satellite"), 0, 0, 16, 16, 1));
        ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, id);
        DynamicDimensionRegistry registry = (DynamicDimensionRegistry) server;
        assert server.getLevel(key) == null : "World already registered?!";
        assert registry.dimensionExists(id) : "Dimension Type already registered?!";
        return create(id, server, parent, position, display, chunkGenerator, type, ownershipData, player.getGameProfile().getName() + "'s Space Station");
    }

    @ApiStatus.Internal
    public static CelestialBody<SatelliteConfig, SatelliteType> create(ResourceLocation id, MinecraftServer server, CelestialBody<?, ?> parent, CelestialPosition<?, ?> position, CelestialDisplay<?, ?> display,
                                                                       ChunkGenerator generator, DimensionType type, SatelliteOwnershipData ownershipData, String name) {
        Constant.LOGGER.debug("Attempting to create a world dynamically ({})", id);

        ((DynamicDimensionRegistry)server).addDynamicDimension(id, generator, type);

        SatelliteConfig config = new SatelliteConfig(ResourceKey.create(AddonRegistry.CELESTIAL_BODY_KEY, server.registryAccess().registryOrThrow(AddonRegistry.CELESTIAL_BODY_KEY).getKey(parent)), parent.galaxy(), position, display, ownershipData, ResourceKey.create(Registry.DIMENSION_REGISTRY, id), EMPTY_GAS_COMPOSITION, 0.0f, parent.type() instanceof Landable ? ((Landable) parent.type()).accessWeight(parent.config()) : 1, new LevelStem(Holder.direct(type), generator));
        config.customName(Component.translatable(name));
        CelestialBody<SatelliteConfig, SatelliteType> satellite = INSTANCE.configure(config);
        ((SatelliteAccessor) server).addSatellite(id, satellite);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            CompoundTag compound = (CompoundTag) SatelliteConfig.CODEC.encode(satellite.config(), NbtOps.INSTANCE, new CompoundTag()).get().orThrow();
            ServerPlayNetworking.send(player, new ResourceLocation(Constant.MOD_ID, "add_satellite"), new FriendlyByteBuf(Unpooled.buffer()).writeResourceLocation(id).writeNbt(compound));
        }
        return satellite;
    }

    @Override
    public @NotNull Component name(SatelliteConfig config) {
        return NAME;
    }

    @Override
    public @Nullable CelestialBody<?, ?> parent(Registry<CelestialBody<?, ?>> registry, SatelliteConfig config) {
        return registry.get(config.parent());
    }

    @Override
    public @NotNull ResourceKey<Galaxy> galaxy(SatelliteConfig config) {
        return config.galaxy();
    }

    @Override
    public @NotNull Component description(SatelliteConfig config) {
        return DESCRIPTION;
    }

    @Override
    public @NotNull CelestialPosition<?, ?> position(SatelliteConfig config) {
        return config.position();
    }

    @Override
    public @NotNull CelestialDisplay<?, ?> display(SatelliteConfig config) {
        return config.display();
    }

    @Override
    public SatelliteOwnershipData ownershipData(SatelliteConfig config) {
        return config.ownershipData();
    }

    @Override
    public void setCustomName(@NotNull Component text, SatelliteConfig config) {
        config.customName(text);
    }

    @Override
    public @NotNull Component getCustomName(SatelliteConfig config) {
        return config.customName();
    }

    @Override
    public @NotNull ResourceKey<Level> world(SatelliteConfig config) {
        return config.world();
    }

    @Override
    public @NotNull GasComposition atmosphere(SatelliteConfig config) {
        return config.atmosphere();
    }

    @Override
    public float gravity(SatelliteConfig config) {
        return config.gravity();
    }

    @Override
    public int accessWeight(SatelliteConfig config) {
        return config.accessWeight();
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
