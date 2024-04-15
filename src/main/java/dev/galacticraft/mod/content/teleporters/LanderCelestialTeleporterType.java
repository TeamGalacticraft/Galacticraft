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

package dev.galacticraft.mod.content.teleporters;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.config.CelestialTeleporterConfig;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.config.DefaultCelestialTeleporterConfig;
import dev.galacticraft.mod.content.entity.orbital.lander.LanderEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class LanderCelestialTeleporterType<Config extends CelestialTeleporterConfig> extends CelestialTeleporterType<Config> {
    public static final LanderCelestialTeleporterType<DefaultCelestialTeleporterConfig> INSTANCE = new LanderCelestialTeleporterType<>(DefaultCelestialTeleporterConfig.CODEC);

    public LanderCelestialTeleporterType(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public void onEnterAtmosphere(ServerLevel level, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody, Config config) {
        LanderEntity lander = new LanderEntity(player);
        level.addFreshEntity(lander);
        lander.setPos(player.getX(), 1100, player.getZ());
        player.teleportTo(level, player.getX(), 1100, player.getZ(), player.getYRot(), player.getXRot());
        player.startRiding(lander, true);
    }
}
