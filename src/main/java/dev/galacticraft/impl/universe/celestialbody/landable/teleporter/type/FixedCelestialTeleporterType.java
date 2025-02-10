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

package dev.galacticraft.impl.universe.celestialbody.landable.teleporter.type;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.config.FixedCelestialTeleporterConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class FixedCelestialTeleporterType extends CelestialTeleporterType<FixedCelestialTeleporterConfig> {
    public static final FixedCelestialTeleporterType INSTANCE = new FixedCelestialTeleporterType();

    private FixedCelestialTeleporterType() {
        super(FixedCelestialTeleporterConfig.CODEC);
    }

    @Override
    public void onEnterAtmosphere(ServerLevel level, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody, FixedCelestialTeleporterConfig config) {
        double x = config.x();
        double y = config.y();
        double z = config.z();
        float yaw = config.yaw();
        float pitch = config.pitch();
        if (x > Level.MAX_LEVEL_SIZE || x < -Level.MAX_LEVEL_SIZE) {
            x = player.getX();
        }
        if (y <= -10000) {
            y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, player.getBlockX(), player.getBlockZ());
            if (y == level.getMinBuildHeight()) {
                y = level.getMaxBuildHeight() * 2;
            }
        }
        if (z > Level.MAX_LEVEL_SIZE || z < -Level.MAX_LEVEL_SIZE) {
            z = player.getZ();
        }
        if (yaw == 360.0f) { //equivalent to 0.0
            yaw = player.getYRot();
        }
        if (pitch == 360.0f) {
            pitch = player.getXRot();
        }
        player.teleportTo(level, x, y, z, yaw, pitch);
    }
}
