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

package dev.galacticraft.api.universe.celestialbody.landable.teleporter;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.BuiltInAddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.config.CelestialTeleporterConfig;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * This class is for determining how a celestial should handle a player teleporting to it.
 * Such as making custom landing sequences.
 */
public record CelestialTeleporter<C extends CelestialTeleporterConfig, T extends CelestialTeleporterType<C>>(T type, C config) {
    public static final Codec<CelestialTeleporter<?, ?>> DIRECT_CODEC = BuiltInAddonRegistries.CELESTIAL_TELEPORTER_TYPE.byNameCodec().dispatch(CelestialTeleporter::type, CelestialTeleporterType::codec);
    public static final Codec<Holder<CelestialTeleporter<?, ?>>> CODEC = RegistryFileCodec.create(AddonRegistries.CELESTIAL_TELEPORTER, DIRECT_CODEC);
    public static final Codec<HolderSet<CelestialTeleporter<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(AddonRegistries.CELESTIAL_TELEPORTER, DIRECT_CODEC);

    /**
     * @param level    The current world for the celestial body
     * @param player   The player.
     * @param body     The celestial body being landed on.
     * @param fromBody The previous celestial body the player is traveling from.
     */
    public void onEnterAtmosphere(ServerLevel level, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody) {
        this.type.onEnterAtmosphere(level, player, body, fromBody, this.config);
    }
}