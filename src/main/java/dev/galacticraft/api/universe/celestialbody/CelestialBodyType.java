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
import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.celestialbody.type.DecorativePlanetType;
import dev.galacticraft.impl.universe.celestialbody.type.PlanetType;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.impl.universe.celestialbody.type.StarType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class CelestialBodyType<C extends CelestialBodyConfig> {
    private final MapCodec<CelestialBody<C, CelestialBodyType<C>>> codec;

    public CelestialBodyType(Codec<C> codec) {
        this.codec = codec.fieldOf("config").xmap((config) -> new CelestialBody<>(this, config), CelestialBody::config);
    }

    /**
     * Returns the name of the celestial body
     * Be sure to {@link Component#copy() copy} the returned text if you intend on stylizing it.
     *
     * @param config the celestial body configuration to be queried
     * @return the name of the celestial body
     */
    public abstract @NotNull Component name(C config);

    /**
     * Returns the description of the celestial body
     * Be sure to {@link Component#copy() copy} the returned text if you intend on stylizing it.
     *
     * @param config the celestial body configuration to be queried
     * @return the description of the celestial body
     */
    public abstract @NotNull Component description(C config);

    /**
     * Returns the celestial body's parent {@link ResourceKey}, or {@code Optional.empty} if it does not have one
     *
     * @param config the celestial body configuration to be queried
     * @return the celestial body's parent {@link ResourceKey}
     */
    public abstract <T> Optional<ResourceKey<T>> parent(C config);
    /**
     * Returns the celestial body's parent galaxy's {@link ResourceKey}
     *
     * @param registry the registry of all the celestial bodies to be used
     * @param config the celestial body configuration to be queried
     * @return the celestial body's parent galaxy's {@link ResourceKey}
     */
    public abstract Optional<ResourceKey<Galaxy>> galaxy(Registry<CelestialBody<?, ?>> registry, C config);

    /**
     * Returns the celestial body's position provider
     *
     * @param config the celestial body configuration to be queried
     * @return the celestial body's position provider
     * @see CelestialPosition
     */
    public abstract @NotNull CelestialPosition<?, ?> position(C config);

    /**
     * Returns the celestial body's display provider
     *
     * @param config the celestial body configuration to be queried
     * @return the celestial body's display provider
     * @see CelestialDisplay
     */
    public abstract @NotNull CelestialDisplay<?, ?> display(C config);

    /**
     * Returns the celestial body's ring display provider
     *
     * @param config the celestial body configuration to be queried
     * @return the celestial body's ring display provider
     * @see CelestialRingDisplay
     */
    public abstract @NotNull CelestialRingDisplay<?, ?> ring(C config);

    /**
     * Returns the {@link GasComposition atmospheric information} of this celestial body
     *
     * @param config the celestial body configuration to be queried
     * @return the registry key of the {@link Level} this celestial body is linked to
     * @see GasComposition#breathable() to see the requirements for a celestial body to be considered breatheable
     */
    public abstract @NotNull GasComposition atmosphere(C config);

    /**
     * Returns the gravity of this celestial body, relative to earth
     *
     * @param config the celestial body configuration to be queried
     * @return the gravity of this celestial body
     */
    public abstract float gravity(C config);

    /**
     * The length of day in this dimension <p> Vanilla: 24000
     */
    public long dayLength(C config) {
        return 24000;
    }

    /**
     * Returns a codec that will (de)serialize a fully-configured celestial body of this type.
     *
     * @return a codec that will (de)serialize a fully-configured celestial body of this type.
     */
    public MapCodec<CelestialBody<C, CelestialBodyType<C>>> codec() {
        return this.codec;
    }

    public CelestialBody<C, ? extends CelestialBodyType<C>> configure(C config) {
        return new CelestialBody<>(this, config);
    }

    public boolean isStar() {
        return this instanceof StarType;
    }

    public boolean isPlanet() {
        return this instanceof PlanetType;
    }

    public boolean isSatellite() {
        return this instanceof SatelliteType;
    }

    public boolean isOrbitable() {
        return this instanceof Orbitable;
    }

    public boolean isDecorativePlanet() {
        return this instanceof DecorativePlanetType;
    }
}
