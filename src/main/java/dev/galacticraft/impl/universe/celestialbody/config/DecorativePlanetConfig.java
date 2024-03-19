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
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.codec.MiscCodecs;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record DecorativePlanetConfig(@NotNull MutableComponent name, @NotNull MutableComponent description,
                                     @NotNull ResourceKey<Galaxy> galaxy,
                                     @NotNull ResourceKey<CelestialBody<?, ?>> parent,
                                     @NotNull CelestialPosition<?, ?> position, @NotNull CelestialDisplay<?, ?> display, @NotNull CelestialRingDisplay<?, ?> ring,
                                     GasComposition atmosphere, float gravity,
                                     @NotNull Optional<SatelliteRecipe> satelliteRecipe) implements CelestialBodyConfig {
    public static final Codec<DecorativePlanetConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MiscCodecs.TRANSLATABLE_COMPONENT.fieldOf("name").forGetter(DecorativePlanetConfig::name),
            MiscCodecs.TRANSLATABLE_COMPONENT.fieldOf("description").forGetter(DecorativePlanetConfig::description),
            ResourceKey.codec(AddonRegistries.GALAXY).fieldOf("galaxy").forGetter(DecorativePlanetConfig::galaxy),
            ResourceKey.codec(AddonRegistries.CELESTIAL_BODY).fieldOf("parent").forGetter(DecorativePlanetConfig::parent),
            CelestialPosition.CODEC.fieldOf("position").forGetter(DecorativePlanetConfig::position),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(DecorativePlanetConfig::display),
            CelestialRingDisplay.CODEC.fieldOf("ring").forGetter(DecorativePlanetConfig::ring),
            GasComposition.CODEC.fieldOf("atmosphere").forGetter(DecorativePlanetConfig::atmosphere),
            Codec.FLOAT.fieldOf("gravity").forGetter(DecorativePlanetConfig::gravity),
            SatelliteRecipe.CODEC.optionalFieldOf("satellite_recipe").forGetter(DecorativePlanetConfig::satelliteRecipe)
    ).apply(instance, DecorativePlanetConfig::new));
}
