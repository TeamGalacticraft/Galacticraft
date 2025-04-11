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

package dev.galacticraft.api.universe.celestialbody;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.BuiltInAddonRegistries;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.mod.util.StreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record CelestialBody<C extends CelestialBodyConfig, T extends CelestialBodyType<C>>(T type, C config) {
    public static final Codec<CelestialBody<?, ?>> DIRECT_CODEC = BuiltInAddonRegistries.CELESTIAL_BODY_TYPE.byNameCodec().dispatch(CelestialBody::type, CelestialBodyType::codec);
    public static final Codec<ResourceKey<CelestialBody<?, ?>>> CODEC = ResourceKey.codec(AddonRegistries.CELESTIAL_BODY);
    public static final Codec<HolderSet<CelestialBody<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(AddonRegistries.CELESTIAL_BODY, DIRECT_CODEC); //fixme: can maybe remove?
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CelestialBody<?, ?>>> STREAM_CODEC = StreamCodecs.ofHolder(AddonRegistries.CELESTIAL_BODY);

    public static double getGravity(Entity entity) {
        return modifyGravity(entity.level(), entity instanceof LivingEntity living ? living.getAttributeBaseValue(Attributes.GRAVITY) : 0.08d);
    }

    public static double modifyGravity(Level level, double d) {
        Holder<CelestialBody<?, ?>> holder = level.galacticraft$getCelestialBody();
        return holder != null ? holder.value().gravity() * d : d;
    }

    /**
     * Returns the name of this celestial body
     * Be sure to {@link Component#copy() copy} the returned text if you intend on stylizing it.
     *
     * @return the name of this celestial body
     */
    public @NotNull Component name() {
        return this.type.name(this.config);
    }

    /**
     * Returns the description of this celestial body
     * Be sure to {@link Component#copy() copy} the returned text if you intend on stylizing it.
     *
     * @return the description of this celestial body
     */
    public @NotNull Component description() {
        return this.type.description(this.config);
    }

    /**
     * Returns this celestial body's parent {@link ResourceKey}, or {@code Optional.empty} if it does not have one
     *
     * @return this celestial body's parent {@link ResourceKey}
     */
    public <P> Optional<ResourceKey<P>> parent() {
        return this.type.parent(this.config);
    }

    /**
     * Returns this celestial body's parent value, or {@code Null} if it does not exist
     *
     * @param registry The server/client's {@link CelestialBody} registry
     * @return this celestial body's parent value
     */
    public CelestialBody<?, ?> parentValue(Registry<CelestialBody<?, ?>> registry) {
        return registry.getOrThrow(this.type.parent(this.config).orElseThrow().cast(AddonRegistries.CELESTIAL_BODY).orElseThrow());
    }

    /**
     * Returns this celestial body's parent galaxy's {@link ResourceKey}
     *
     * @param registry The server/client's {@link CelestialBody} registry
     * @return this celestial body's parent galaxy's id {@link ResourceKey}
     */
    public Optional<ResourceKey<Galaxy>> galaxy(Registry<CelestialBody<?, ?>> registry) {
        return this.type.galaxy(registry, this.config);
    }

    /**
     * Returns this celestial body's parent galaxy's value
     *
     * @param galaxyRegistry The server/client's {@link Galaxy} registry
     * @param celestialBodyRegistry The server/client's {@link CelestialBody} registry
     * @return this celestial body's parent galaxy's value
     */
    public Galaxy galaxyValue(Registry<Galaxy> galaxyRegistry, Registry<CelestialBody<?, ?>> celestialBodyRegistry) {
        return galaxyRegistry.getOrThrow(this.galaxy(celestialBodyRegistry).orElseThrow());
    }

    /**
     * Returns this celestial body's position provider
     *
     * @return this celestial body's position provider
     * @see CelestialPosition
     */
    public @NotNull CelestialPosition<?, ?> position() {
        return this.type.position(this.config);
    }

    /**
     * Returns this celestial body's display provider
     *
     * @return this celestial body's display provider
     * @see CelestialDisplay
     */
    public @NotNull CelestialDisplay<?, ?> display() {
        return this.type.display(this.config);
    }

    /**
     * Returns this celestial body's ring display provider
     *
     * @return this celestial body's ring display provider
     * @see CelestialRingDisplay
     */
    public @NotNull CelestialRingDisplay<?, ?> ring() {
        return this.type.ring(this.config);
    }

    /**
     * Returns this celestial body's atmospheric composition
     *
     * @return this celestial body's atmospheric composition
     */
    public @NotNull GasComposition atmosphere() {
        return this.type.atmosphere(this.config);
    }

    /**
     * Returns this celestial body's length of a single day on this celestial body
     *
     * @return this celestial body's length of a single day on this celestial body
     */
    public long dayLength() {
        return this.type.dayLength(this.config);
    }

    /**
     * Returns this celestial body's gravity
     *
     * @return this celestial body's gravity
     */
    public float gravity() {
        return this.type.gravity(this.config);
    }

    public ResourceKey<CelestialBody<?, ?>> getKey(Registry<CelestialBody<?, ?>> registry) {
        return registry.getResourceKey(this).orElseThrow();
    }

    public boolean isStar() {
        return this.type.isStar();
    }

    public boolean isPlanet() {
        return this.type.isPlanet();
    }

    public boolean isSatellite() {
        return this.type.isSatellite();
    }

    public boolean isOrbitable() {
        return this.type.isOrbitable();
    }

    public boolean isDecorativePlanet() {
        return this.type.isDecorativePlanet();
    }
}