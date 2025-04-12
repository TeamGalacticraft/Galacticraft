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

package dev.galacticraft.impl.universe.position.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.satellite.SatelliteOwnershipData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.display.CelestialDisplay;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplay;
import dev.galacticraft.api.universe.position.CelestialPosition;
import dev.galacticraft.mod.util.StreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Optional;

public class SatelliteConfig implements CelestialBodyConfig {
    private ResourceLocation id;
    private String customName;
    private Optional<ResourceKey<CelestialBody<?, ?>>> parent;
    private CelestialPosition<?, ?> position;
    private CelestialDisplay<?, ?> display;
    private CelestialRingDisplay<?, ?> ring;
    private SatelliteOwnershipData ownershipData;
    private ResourceKey<Level> world;
    private Holder<CelestialTeleporter<?, ?>> teleporter;
    private GasComposition atmosphere;
    private float gravity;
    private int accessWeight;
    private LevelStem options;

    public static final Codec<SatelliteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SatelliteConfig::getId),
            Codec.STRING.fieldOf("custom_name").forGetter(SatelliteConfig::getCustomName),
            CelestialBody.CODEC.optionalFieldOf("parent").forGetter(SatelliteConfig::getParent),
            CelestialPosition.CODEC.fieldOf("position").forGetter(SatelliteConfig::getPosition),
            CelestialDisplay.CODEC.fieldOf("display").forGetter(SatelliteConfig::getDisplay),
            CelestialRingDisplay.CODEC.fieldOf("ring").forGetter(SatelliteConfig::getRing),
            SatelliteOwnershipData.CODEC.fieldOf("ownership_data").forGetter(SatelliteConfig::getOwnershipData),
            Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(SatelliteConfig::getWorld),
            CelestialTeleporter.CODEC.fieldOf("teleporter").forGetter(SatelliteConfig::getTeleporter),
            GasComposition.CODEC.fieldOf("atmosphere").forGetter(SatelliteConfig::getAtmosphere),
            Codec.FLOAT.fieldOf("gravity").forGetter(SatelliteConfig::getGravity),
            Codec.INT.fieldOf("access_weight").forGetter(SatelliteConfig::getAccessWeight),
            LevelStem.CODEC.fieldOf("options").forGetter(SatelliteConfig::getOptions)
    ).apply(instance, SatelliteConfig::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SatelliteConfig> STREAM_CODEC = StreamCodecs.wrapCodec(CODEC);

    public SatelliteConfig(
            ResourceLocation id,
            String customName,
            Optional<ResourceKey<CelestialBody<?, ?>>> parent,
            CelestialPosition<?, ?> position,
            CelestialDisplay<?, ?> display,
            CelestialRingDisplay<?, ?> ring,
            SatelliteOwnershipData ownershipData,
            ResourceKey<Level> world,
            Holder<CelestialTeleporter<?, ?>> teleporter,
            GasComposition atmosphere,
            float gravity,
            int accessWeight,
            LevelStem options) {
        this.id = id;
        this.customName = customName;
        this.parent = parent;
        this.position = position;
        this.display = display;
        this.ring = ring;
        this.ownershipData = ownershipData;
        this.world = world;
        this.teleporter = teleporter;
        this.atmosphere = atmosphere;
        this.gravity = gravity;
        this.accessWeight = accessWeight;
        this.options = options;
    }

    // Getters
    public ResourceLocation getId() { return id; }
    public String getCustomName() { return customName; }
    public Optional<ResourceKey<CelestialBody<?, ?>>> getParent() { return parent; }
    public CelestialPosition<?, ?> getPosition() { return position; }
    public CelestialDisplay<?, ?> getDisplay() { return display; }
    public CelestialRingDisplay<?, ?> getRing() { return ring; }
    public SatelliteOwnershipData getOwnershipData() { return ownershipData; }
    public ResourceKey<Level> getWorld() { return world; }
    public Holder<CelestialTeleporter<?, ?>> getTeleporter() { return teleporter; }
    public GasComposition getAtmosphere() { return atmosphere; }
    public float getGravity() { return gravity; }
    public int getAccessWeight() { return accessWeight; }
    public LevelStem getOptions() { return options; }

    // Setters (optional, use as needed)
    public void setId(ResourceLocation id) { this.id = id; }
    public void setCustomName(String customName) { this.customName = customName; }
    public void setParent(Optional<ResourceKey<CelestialBody<?, ?>>> parent) { this.parent = parent; }
    public void setPosition(CelestialPosition<?, ?> position) { this.position = position; }
    public void setDisplay(CelestialDisplay<?, ?> display) { this.display = display; }
    public void setRing(CelestialRingDisplay<?, ?> ring) { this.ring = ring; }
    public void setOwnershipData(SatelliteOwnershipData ownershipData) { this.ownershipData = ownershipData; }
    public void setWorld(ResourceKey<Level> world) { this.world = world; }
    public void setTeleporter(Holder<CelestialTeleporter<?, ?>> teleporter) { this.teleporter = teleporter; }
    public void setAtmosphere(GasComposition atmosphere) { this.atmosphere = atmosphere; }
    public void setGravity(float gravity) { this.gravity = gravity; }
    public void setAccessWeight(int accessWeight) { this.accessWeight = accessWeight; }
    public void setOptions(LevelStem options) { this.options = options; }
}
