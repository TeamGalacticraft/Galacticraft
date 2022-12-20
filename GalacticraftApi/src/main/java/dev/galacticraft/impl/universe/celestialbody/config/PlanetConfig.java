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

package dev.galacticraft.impl.universe.celestialbody.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PlanetConfig(@NotNull MutableComponent name, @NotNull MutableComponent description,
                           @NotNull ResourceKey<Galaxy> galaxy, @NotNull ResourceKey<CelestialBody<?, ?>> parent,
                           @NotNull CelestialPosition<?, ?> position, @NotNull CelestialDisplay<?, ?> display,
                           @NotNull ResourceKey<Level> world, @NotNull GasComposition atmosphere, float gravity,
                           int accessWeight, int dayTemperature, int nightTemperature,
                           @NotNull Optional<SatelliteRecipe> satelliteRecipe) implements CelestialBodyConfig {
    public static final Codec<PlanetConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").xmap(Component::translatable, Component::getString).forGetter(PlanetConfig::name),
            Codec.STRING.fieldOf("description").xmap(Component::translatable, Component::getString).forGetter(PlanetConfig::description),
            ResourceLocation.CODEC.fieldOf("galaxy").xmap(id -> ResourceKey.create(AddonRegistry.GALAXY_KEY, id), ResourceKey::location).forGetter(PlanetConfig::galaxy),
            ResourceLocation.CODEC.fieldOf("parent").xmap(id -> ResourceKey.create(AddonRegistry.CELESTIAL_BODY_KEY, id), ResourceKey::location).forGetter(PlanetConfig::parent),
            CelestialPosition.CODEC.fieldOf("position").forGetter(PlanetConfig::position),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(PlanetConfig::display),
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(PlanetConfig::world),
            GasComposition.CODEC.fieldOf("atmosphere").forGetter(PlanetConfig::atmosphere),
            Codec.FLOAT.fieldOf("gravity").forGetter(PlanetConfig::gravity),
            Codec.INT.fieldOf("access_weight").forGetter(PlanetConfig::accessWeight),
            Codec.INT.fieldOf("day_temperature").forGetter(PlanetConfig::dayTemperature),
            Codec.INT.fieldOf("night_temperature").forGetter(PlanetConfig::nightTemperature),
            SatelliteRecipe.CODEC.optionalFieldOf("satellite_recipe").forGetter(PlanetConfig::satelliteRecipe)
    ).apply(instance, PlanetConfig::new));
}
