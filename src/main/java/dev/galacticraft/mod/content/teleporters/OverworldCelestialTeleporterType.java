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
import dev.galacticraft.impl.universe.celestialbody.config.PlanetConfig;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.config.DefaultCelestialTeleporterConfig;
import dev.galacticraft.mod.attachments.GCServerPlayer;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.ParachestEntity;
import dev.galacticraft.mod.content.item.ParachuteItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.DyeColor;

public class OverworldCelestialTeleporterType<Config extends CelestialTeleporterConfig> extends CelestialTeleporterType<Config> {
    public static final OverworldCelestialTeleporterType<DefaultCelestialTeleporterConfig> INSTANCE = new OverworldCelestialTeleporterType<>(DefaultCelestialTeleporterConfig.CODEC);

    public OverworldCelestialTeleporterType(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public void onEnterAtmosphere(ServerLevel level, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody, Config config) {
        if (body.config() instanceof PlanetConfig planetConfig) {
            var chestSpawn = planetConfig.celestialHandler().getParaChestSpawnLocation(player.serverLevel(), player, player.getRandom());
            if (chestSpawn != null) {
                var gcPlayer = GCServerPlayer.get(player);
                var rocketInv = gcPlayer.getRocketStacks();
                ParachestEntity chest = new ParachestEntity(GCEntityTypes.PARACHEST, level, rocketInv, gcPlayer.getFuel());
                rocketInv.clear();

                chest.setPos(chestSpawn);

                Container gearInv = player.galacticraft$getGearInv();
                DyeColor color = DyeColor.WHITE;
                for (int slot = 0; slot < gearInv.getContainerSize(); slot++) {
                    if (player.galacticraft$getGearInv().getItem(slot).getItem() instanceof ParachuteItem parachute) {
                        color = parachute.getColor();
                        break;
                    }
                }
                chest.color = color;

                level.addFreshEntity(chest);
            }
        }
        player.teleportTo(level, player.getX(), level.getMaxBuildHeight() + 20.0, player.getZ(), player.getYRot(), player.getXRot());
    }
}
