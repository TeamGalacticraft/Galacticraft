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

package dev.galacticraft.impl.universe.position.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.satellite.SatelliteOwnershipData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPosition;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public final class SatelliteConfig implements CelestialBodyConfig {
    public static final Codec<SatelliteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("parent").xmap(id -> ResourceKey.create(AddonRegistry.CELESTIAL_BODY_KEY, id), ResourceKey::location).forGetter(SatelliteConfig::parent),
            ResourceLocation.CODEC.fieldOf("galaxy").xmap(id -> ResourceKey.create(AddonRegistry.GALAXY_KEY, id), ResourceKey::location).forGetter(SatelliteConfig::galaxy),
            CelestialPosition.CODEC.fieldOf("position").forGetter(SatelliteConfig::position),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(SatelliteConfig::display),
            SatelliteOwnershipData.CODEC.fieldOf("ownership_data").forGetter(SatelliteConfig::ownershipData),
            ResourceLocation.CODEC.fieldOf("world").xmap(id -> ResourceKey.create(Registry.DIMENSION_REGISTRY, id), ResourceKey::location).forGetter(SatelliteConfig::world),
            GasComposition.CODEC.fieldOf("atmosphere").forGetter(SatelliteConfig::atmosphere),
            Codec.FLOAT.fieldOf("gravity").forGetter(SatelliteConfig::gravity),
            Codec.INT.fieldOf("accessWeight").forGetter(SatelliteConfig::accessWeight),
            LevelStem.CODEC.fieldOf("dimension_options").forGetter(SatelliteConfig::dimensionOptions)
    ).apply(instance, SatelliteConfig::new));

    private final ResourceKey<CelestialBody<?, ?>> parent;
    private final ResourceKey<Galaxy> galaxy;
    private final CelestialPosition<?, ?> position;
    private final CelestialDisplay<?, ?> display;
    private final SatelliteOwnershipData ownershipData;
    private final ResourceKey<Level> world;
    private final GasComposition atmosphere;
    private final float gravity;
    private final int accessWeight;
    private final LevelStem options;
    private Component customName = Component.empty();

    public SatelliteConfig(ResourceKey<CelestialBody<?, ?>> parent, ResourceKey<Galaxy> galaxy, CelestialPosition<?, ?> position, CelestialDisplay<?, ?> display, SatelliteOwnershipData ownershipData, ResourceKey<Level> world, GasComposition atmosphere, float gravity, int accessWeight, LevelStem options) {
        this.parent = parent;
        this.galaxy = galaxy;
        this.position = position;
        this.display = display;
        this.ownershipData = ownershipData;
        this.world = world;
        this.atmosphere = atmosphere;
        this.gravity = gravity;
        this.accessWeight = accessWeight;
        this.options = options;
    }

    public ResourceKey<CelestialBody<?, ?>> parent() {return parent;}

    public ResourceKey<Galaxy> galaxy() {return galaxy;}

    public CelestialPosition<?, ?> position() {return position;}

    public CelestialDisplay<?, ?> display() {return display;}

    public SatelliteOwnershipData ownershipData() {return ownershipData;}

    public Component customName() {return customName;}

    public void customName(Component name) {this.customName = name;}

    public ResourceKey<Level> world() {return world;}

    public GasComposition atmosphere() {return atmosphere;}

    public float gravity() {return gravity;}

    public int accessWeight() {return accessWeight;}

    public LevelStem dimensionOptions() {return options;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        SatelliteConfig that = (SatelliteConfig) obj;
        return Objects.equals(this.parent, that.parent) &&
                Objects.equals(this.galaxy, that.galaxy) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.display, that.display) &&
                Objects.equals(this.ownershipData, that.ownershipData) &&
                Objects.equals(this.customName, that.customName) &&
                Objects.equals(this.world, that.world) &&
                Objects.equals(this.atmosphere, that.atmosphere) &&
                Float.floatToIntBits(this.gravity) == Float.floatToIntBits(that.gravity) &&
                this.accessWeight == that.accessWeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, galaxy, position, display, ownershipData, customName, world, atmosphere, gravity, accessWeight);
    }

    @Override
    public String toString() {
        return "SatelliteConfig[" +
                "parent=" + parent + ", " +
                "galaxy=" + galaxy + ", " +
                "position=" + position + ", " +
                "display=" + display + ", " +
                "ownershipData=" + ownershipData + ", " +
                "customName=" + customName + ", " +
                "world=" + world + ", " +
                "atmosphere=" + atmosphere + ", " +
                "gravity=" + gravity + ", " +
                "accessWeight=" + accessWeight + ']';
    }
}
