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

import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.celestialbody.config.DecorativePlanetConfig;
import dev.galacticraft.impl.universe.celestialbody.config.StarConfig;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DecorativePlanetType extends CelestialBodyType<DecorativePlanetConfig> implements Orbitable<DecorativePlanetConfig> {
    public static final DecorativePlanetType INSTANCE = new DecorativePlanetType();

    private DecorativePlanetType() {
        super(DecorativePlanetConfig.CODEC);
    }

    @Override
    public @NotNull Component name(DecorativePlanetConfig config) {
        return config.name();
    }

    @Override
    public @Nullable Optional<ResourceKey<CelestialBody<?, ?>>> parent(DecorativePlanetConfig config) {
        return config.parent();
    }

    @Override
    public @NotNull Optional<ResourceKey<Galaxy>> galaxy(Registry<CelestialBody<?, ?>> registry, DecorativePlanetConfig config) {
        if (config.parent().isPresent()) {
            CelestialBody<?, ?> body = registry.get(config.parent().get());
            if (body != null && body.type() instanceof StarType starType) {
                return starType.galaxy(registry, (StarConfig) body.config());
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Component description(DecorativePlanetConfig config) {
        return config.description();
    }

    @Override
    public @NotNull CelestialPosition<?, ?> position(DecorativePlanetConfig config) {
        return config.position();
    }

    @Override
    public @NotNull CelestialDisplay<?, ?> display(DecorativePlanetConfig config) {
        return config.display();
    }

    @Override
    public @NotNull CelestialRingDisplay<?, ?> ring(DecorativePlanetConfig config) {
        return config.ring();
    }

    @Override
    public @NotNull GasComposition atmosphere(DecorativePlanetConfig config) {
        return config.atmosphere();
    }

    @Override
    public float gravity(DecorativePlanetConfig config) {
        return config.gravity();
    }

    @Override
    public @Nullable SatelliteRecipe satelliteRecipe(DecorativePlanetConfig config) {
        return config.satelliteRecipe();
    }
}
