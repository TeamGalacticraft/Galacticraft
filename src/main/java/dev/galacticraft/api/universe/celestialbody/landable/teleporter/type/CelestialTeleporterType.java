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

package dev.galacticraft.api.universe.celestialbody.landable.teleporter.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.config.CelestialTeleporterConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public abstract class CelestialTeleporterType<C extends CelestialTeleporterConfig> {
    private final MapCodec<CelestialTeleporter<C, CelestialTeleporterType<C>>> codec;
    public static final Set<RelativeMovement> NO_RELATIVE_MOVEMENT = Collections.<RelativeMovement>emptySet();

    public CelestialTeleporterType(Codec<C> codec) {
        this.codec = codec.fieldOf("config").xmap((config) -> new CelestialTeleporter<>(this, config), CelestialTeleporter::config);
    }

    public @NotNull MapCodec<CelestialTeleporter<C, CelestialTeleporterType<C>>> codec() {
        return this.codec;
    }

    public abstract void onEnterAtmosphere(ServerLevel level, Entity entity, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody, C config);
}
