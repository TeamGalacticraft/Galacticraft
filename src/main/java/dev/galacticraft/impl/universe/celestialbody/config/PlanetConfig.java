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

package dev.galacticraft.impl.universe.celestialbody.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.CelestialHandler;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.codec.MiscCodecs;
import dev.galacticraft.impl.codec.MoreProducts;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PlanetConfig(@NotNull MutableComponent name, @NotNull MutableComponent description,
                           @NotNull ResourceKey<Galaxy> galaxy, @NotNull ResourceKey<CelestialBody<?, ?>> parent,
                           @NotNull CelestialPosition<?, ?> position, @NotNull CelestialDisplay<?, ?> display, @NotNull CelestialRingDisplay<?, ?> ring,
                           @NotNull ResourceKey<Level> world, Holder<CelestialTeleporter<?, ?>> teleporter, @NotNull GasComposition atmosphere, float gravity,
                           CelestialHandler celestialHandler,
                           int accessWeight, int dayTemperature, int nightTemperature, long dayLength,
                           @NotNull Optional<SatelliteRecipe> satelliteRecipe) implements CelestialBodyConfig {
    public static final Codec<PlanetConfig> CODEC = RecordCodecBuilder.create(instance -> new MoreProducts.P17<>(
            MiscCodecs.TRANSLATABLE_COMPONENT.fieldOf("name").forGetter(PlanetConfig::name),
            MiscCodecs.TRANSLATABLE_COMPONENT.fieldOf("description").forGetter(PlanetConfig::description),
            ResourceKey.codec(AddonRegistries.GALAXY).fieldOf("galaxy").forGetter(PlanetConfig::galaxy),
            ResourceKey.codec(AddonRegistries.CELESTIAL_BODY).fieldOf("parent").forGetter(PlanetConfig::parent),
            CelestialPosition.CODEC.fieldOf("position").forGetter(PlanetConfig::position),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(PlanetConfig::display),
            CelestialRingDisplay.CODEC.fieldOf("ring").forGetter(PlanetConfig::ring),
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(PlanetConfig::world),
            RegistryFileCodec.create(AddonRegistries.CELESTIAL_TELEPORTER, CelestialTeleporter.DIRECT_CODEC).fieldOf("teleporter").forGetter(PlanetConfig::teleporter),
            GasComposition.CODEC.fieldOf("atmosphere").forGetter(PlanetConfig::atmosphere),
            Codec.FLOAT.fieldOf("gravity").forGetter(PlanetConfig::gravity),
            CelestialHandler.CODEC.fieldOf("celestial_handler").forGetter(PlanetConfig::celestialHandler),
            Codec.INT.fieldOf("access_weight").forGetter(PlanetConfig::accessWeight),
            Codec.INT.fieldOf("day_temperature").forGetter(PlanetConfig::dayTemperature),
            Codec.INT.fieldOf("night_temperature").forGetter(PlanetConfig::nightTemperature),
            Codec.LONG.fieldOf("day_length").forGetter(PlanetConfig::dayLength),
            SatelliteRecipe.CODEC.optionalFieldOf("satellite_recipe").forGetter(PlanetConfig::satelliteRecipe)
    ).apply(instance, PlanetConfig::new));
}
