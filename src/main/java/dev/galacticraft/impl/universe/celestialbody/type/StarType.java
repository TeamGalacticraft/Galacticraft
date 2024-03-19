/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.star.Star;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.celestialbody.config.StarConfig;
import dev.galacticraft.impl.universe.display.type.ring.DefaultCelestialRingDisplayType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StarType extends CelestialBodyType<StarConfig> implements Star<StarConfig> {
    public static final StarType INSTANCE = new StarType();

    protected StarType() {
        super(StarConfig.CODEC);
    }

    @Override
    public @NotNull Component name(StarConfig config) {
        return config.name();
    }

    /**
     * Returns {@code null} as stars do not have parent celestial bodies
     *
     * @param registry the registry to query for the parent
     * @param config   the celestial body configuration to be queried
     * @return {@code null}
     */
    @Override
    public @Nullable CelestialBody<?, ?> parent(Registry<CelestialBody<?, ?>> registry, StarConfig config) {
        return config.parent().isPresent() ? registry.get(config.parent().get()) : null;
    }

    @Override
    public @NotNull ResourceKey<Galaxy> galaxy(StarConfig config) {
        return config.galaxy();
    }

    @Override
    public @NotNull Component description(StarConfig config) {
        return config.description();
    }

    @Override
    public @NotNull CelestialPosition<?, ?> position(StarConfig config) {
        return config.position();
    }

    @Override
    public @NotNull CelestialDisplay<?, ?> display(StarConfig config) {
        return config.display();
    }

    @Override
    public @NotNull CelestialRingDisplay<?, ?> ring(StarConfig config) {
        return config.ring();
    }

    /**
     * {@inheritDoc}
     * Treat as this star's photospheric composition
     */
    @Override
    public @NotNull GasComposition atmosphere(StarConfig config) {
        return config.photosphericComposition();
    }

    @Override
    public float gravity(StarConfig config) {
        return config.gravity();
    }

    @Override
    public double luminance(StarConfig config) {
        return config.luminance();
    }

    @Override
    public int surfaceTemperature(StarConfig config) {
        return config.surfaceTemperature();
    }
}
