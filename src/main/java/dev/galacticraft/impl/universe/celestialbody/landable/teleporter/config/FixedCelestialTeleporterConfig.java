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

package dev.galacticraft.impl.universe.celestialbody.landable.teleporter.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.config.CelestialTeleporterConfig;
import net.minecraft.world.level.Level;

public record FixedCelestialTeleporterConfig(double x, double y, double z, float yaw, float pitch) implements CelestialTeleporterConfig {
    public static final Codec<FixedCelestialTeleporterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").orElse((double) Level.MAX_LEVEL_SIZE + 1).forGetter(FixedCelestialTeleporterConfig::x),
            Codec.DOUBLE.fieldOf("y").orElse(-10000.0).forGetter(FixedCelestialTeleporterConfig::y),
            Codec.DOUBLE.fieldOf("z").orElse((double) Level.MAX_LEVEL_SIZE + 1).forGetter(FixedCelestialTeleporterConfig::z),
            Codec.FLOAT.fieldOf("yaw").orElse(360.0f).forGetter(FixedCelestialTeleporterConfig::yaw),
            Codec.FLOAT.fieldOf("pitch").orElse(360.0f).forGetter(FixedCelestialTeleporterConfig::pitch)
    ).apply(instance, FixedCelestialTeleporterConfig::new));
}
