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
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.Tiered;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.celestialbody.config.PlanetConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlanetType extends CelestialBodyType<PlanetConfig> implements Tiered<PlanetConfig>, Orbitable<PlanetConfig> {
    public static final PlanetType INSTANCE = new PlanetType();

    protected PlanetType() {
        super(PlanetConfig.CODEC);
    }

    @Override
    public @NotNull Component name(PlanetConfig config) {
        return config.name();
    }

    @Override
    public @Nullable CelestialBody<?, ?> parent(Registry<CelestialBody<?, ?>> registry, PlanetConfig config) {
        return registry.get(config.parent());
    }

    @Override
    public @NotNull ResourceKey<Galaxy> galaxy(PlanetConfig config) {
        return config.galaxy();
    }

    @Override
    public @NotNull Component description(PlanetConfig config) {
        return config.description();
    }

    @Override
    public @NotNull CelestialPosition<?, ?> position(PlanetConfig config) {
        return config.position();
    }

    @Override
    public @NotNull CelestialDisplay<?, ?> display(PlanetConfig config) {
        return config.display();
    }

    @Override
    public @NotNull CelestialRingDisplay<?, ?> ring(PlanetConfig config) {
        return config.ring();
    }

    @Override
    public @NotNull ResourceKey<Level> world(PlanetConfig config) {
        return config.world();
    }

    @Override
    public Holder<CelestialTeleporter<?, ?>> teleporter(PlanetConfig config) {
        return config.teleporter();
    }

    @Override
    public @NotNull GasComposition atmosphere(PlanetConfig config) {
        return config.atmosphere();
    }

    @Override
    public float gravity(PlanetConfig config) {
        return config.gravity();
    }

    @Override
    public int accessWeight(PlanetConfig config) {
        return config.accessWeight();
    }

    @Override
    public long dayLength(PlanetConfig config) {
        return config.dayLength();
    }

    @Override
    public int temperature(RegistryAccess access, long time, PlanetConfig config) {
        return time % config.dayLength() < 12000 ? config.dayTemperature() : config.nightTemperature(); //todo: temperature providers?
    }

    @Override
    public @Nullable SatelliteRecipe satelliteRecipe(PlanetConfig config) {
        return config.satelliteRecipe().orElse(null);
    }
}
