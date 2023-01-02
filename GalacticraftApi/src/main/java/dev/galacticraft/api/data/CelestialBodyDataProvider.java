/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.api.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.impl.universe.celestialbody.config.PlanetConfig;
import dev.galacticraft.impl.universe.celestialbody.config.StarConfig;
import dev.galacticraft.impl.universe.celestialbody.type.PlanetType;
import dev.galacticraft.impl.universe.celestialbody.type.StarType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public abstract class CelestialBodyDataProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final Map<ResourceLocation, Builder> celestialBodies = Maps.newLinkedHashMap();
    protected final DataGenerator.PathProvider pathProvider;

    public CelestialBodyDataProvider(DataGenerator dataGenerator) {
        this.pathProvider = dataGenerator.createPathProvider(DataGenerator.Target.DATA_PACK, "celestial_body");
    }

    @Override
    public void run(CachedOutput cachedOutput) {
        celestialBodies.clear();
        generateCelestialBodies();
        celestialBodies.forEach((key, builder) -> {
            JsonElement element = CelestialBody.CODEC.encodeStart(JsonOps.INSTANCE, builder.build()).getOrThrow(false, LOGGER::error);
            Path path = pathProvider.json(key);

            try {
                DataProvider.saveStable(cachedOutput, element, path);
            } catch (IOException var9) {
                LOGGER.error("Couldn't save celestial body to {}", path, var9);
            }
        });
    }

    public void celestialBody(ResourceKey<CelestialBody<?, ?>> key, CelestialBody<?, ?> celestialBody) {
        celestialBody(key.location(), celestialBody);
    }

    public void celestialBody(ResourceLocation key, CelestialBody<?, ?> celestialBody) {
        celestialBodies.put(key, new DefaultBuilder(celestialBody));
    }

    public StarBuilder star(ResourceLocation key) {
        StarBuilder builder = new StarBuilder(key);
        celestialBodies.put(key, builder);
        return builder;
    }

    public PlanetBuilder planet(ResourceLocation key) {
        PlanetBuilder builder = new PlanetBuilder(key);
        celestialBodies.put(key, builder);
        return builder;
    }

    public abstract void generateCelestialBodies();

    public interface Builder {
        CelestialBody<?, ?> build();
    }

    public interface CelestialBuilder extends Builder {
        Builder name(MutableComponent component);

        Builder description(MutableComponent component);

        Builder galaxy(ResourceKey<Galaxy> galaxy);

        Builder position(CelestialPosition<?, ?> position);

        Builder display(CelestialDisplay<?, ?> display);

        Builder gravity(float gravity);
    }

    public class DefaultBuilder implements Builder {
        private final CelestialBody<?, ?> body;

        public DefaultBuilder(CelestialBody<?, ?> body) {
            this.body = body;
        }

        @Override
        public CelestialBody<?, ?> build() {
            return body;
        }
    }

    public class StarBuilder implements CelestialBuilder {

        private final ResourceLocation key;
        private MutableComponent name, description;
        private ResourceKey<Galaxy> galaxy;
        private CelestialPosition<?, ?> position;
        private CelestialDisplay<?, ?> display;
        private GasComposition photosphericComposition;
        private float gravity;
        private double luminance;
        private int surfaceTemperature;

        StarBuilder(ResourceLocation key) {
            this.key = key;
        }

        @Override
        public StarBuilder name(MutableComponent component) {
            this.name = component;
            return this;
        }

        @Override
        public StarBuilder description(MutableComponent component) {
            this.description = component;
            return this;
        }

        @Override
        public StarBuilder galaxy(ResourceKey<Galaxy> galaxy) {
            this.galaxy = galaxy;
            return this;
        }

        @Override
        public StarBuilder position(CelestialPosition<?, ?> position) {
            this.position = position;
            return this;
        }

        @Override
        public StarBuilder display(CelestialDisplay<?, ?> display) {
            this.display = display;
            return this;
        }

        @Override
        public StarBuilder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public StarBuilder photosphericComposition(GasComposition photosphericComposition) {
            this.photosphericComposition = photosphericComposition;
            return this;
        }

        public StarBuilder luminance(double luminance) {
            this.luminance = luminance;
            return this;
        }

        public StarBuilder surfaceTemperature(int surfaceTemperature) {
            this.surfaceTemperature = surfaceTemperature;
            return this;
        }

        @Override
        public CelestialBody<?, ?> build() {
            return StarType.INSTANCE.configure(new StarConfig(this.name, this.description, this.galaxy, this.position, this.display, this.photosphericComposition, this.gravity, this.luminance, this.surfaceTemperature));
        }
    }

    public class PlanetBuilder implements CelestialBuilder {

        private final ResourceLocation key;
        private MutableComponent name, description;
        private ResourceKey<Galaxy> galaxy;
        private ResourceKey<CelestialBody<?, ?>> parent;
        private CelestialPosition<?, ?> position;
        private CelestialDisplay<?, ?> display;
        private ResourceKey<Level> world;
        private GasComposition atmosphere;
        private float gravity;
        private int accessWeight, dayTemperature, nightTemperature;
        private Optional<SatelliteRecipe> satelliteRecipe;

        PlanetBuilder(ResourceLocation key) {
            this.key = key;
        }

        @Override
        public PlanetBuilder name(MutableComponent component) {
            this.name = component;
            return this;
        }

        @Override
        public PlanetBuilder description(MutableComponent component) {
            this.description = component;
            return this;
        }

        @Override
        public PlanetBuilder galaxy(ResourceKey<Galaxy> galaxy) {
            this.galaxy = galaxy;
            return this;
        }

        public PlanetBuilder parent(ResourceKey<CelestialBody<?, ?>> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public PlanetBuilder position(CelestialPosition<?, ?> position) {
            this.position = position;
            return this;
        }

        @Override
        public PlanetBuilder display(CelestialDisplay<?, ?> display) {
            this.display = display;
            return this;
        }

        @Override
        public PlanetBuilder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public PlanetBuilder world(ResourceKey<Level> world) {
            this.world = world;
            return this;
        }

        public PlanetBuilder atmosphere(GasComposition atmosphere) {
            this.atmosphere = atmosphere;
            return this;
        }

        public PlanetBuilder accessWeight(int accessWeight) {
            this.accessWeight = accessWeight;
            return this;
        }

        public PlanetBuilder dayTemperature(int dayTemperature) {
            this.dayTemperature = dayTemperature;
            return this;
        }

        public PlanetBuilder nightTemperature(int nightTemperature) {
            this.nightTemperature = nightTemperature;
            return this;
        }

        public PlanetBuilder satelliteRecipe(Optional<SatelliteRecipe> satelliteRecipe) {
            this.satelliteRecipe = satelliteRecipe;
            return this;
        }

        @Override
        public CelestialBody<?, ?> build() {
            return PlanetType.INSTANCE.configure(new PlanetConfig(this.name, this.description, this.galaxy, this.parent, this.position, this.display, this.world, this.atmosphere, this.gravity, this.accessWeight, this.dayTemperature, this.nightTemperature, this.satelliteRecipe));
        }
    }
}
