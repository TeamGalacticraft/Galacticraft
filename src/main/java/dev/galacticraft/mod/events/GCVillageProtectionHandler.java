/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.events;

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.block.decoration.MeteoricIronDoorBlock;
import dev.galacticraft.mod.content.block.entity.LunarHomeAnchorBlockEntity;
import dev.galacticraft.mod.content.entity.MoonGolemEntity;
import dev.galacticraft.mod.util.Translations;
import dev.galacticraft.mod.world.poi.GCPointOfInterestTypes;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class GCVillageProtectionHandler {
    private static final int GOLEM_SEARCH_RANGE = 48;
    private static final double GOLEM_CLOSE_ENOUGH = 24.0;
    private static final double SPAWN_DISTANCE_BEHIND = 12.0;

    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide) return;
            if (player.isCreative() || player.isSpectator()) return;
            if (MeteoricIronDoorBlock.hasVillageAccessKey(player)) return;
            onBlockBroken((ServerLevel) level, player, pos);
        });
    }

    private static void onBlockBroken(ServerLevel level, Player player, BlockPos brokenPos) {
        int radius = LunarHomeAnchorBlockEntity.PROTECTION_RADIUS;
        PoiManager poiManager = level.getPoiManager();

        Optional<BlockPos> nearestAnchor = poiManager.findClosest(
                holder -> holder.is(GCPointOfInterestTypes.LUNAR_HOME),
                brokenPos,
                radius,
                PoiManager.Occupancy.ANY
        );

        if (nearestAnchor.isPresent()) {
            alertGolems(level, player, nearestAnchor.get());
        }
    }

    private static void alertGolems(ServerLevel level, Player player, BlockPos anchorPos) {
        AABB searchBox = new AABB(anchorPos).inflate(GOLEM_SEARCH_RANGE);
        List<MoonGolemEntity> golems = level.getEntitiesOfClass(MoonGolemEntity.class, searchBox);

        boolean hasCloseGolem = false;
        for (MoonGolemEntity golem : golems) {
            golem.gc$setVillageAggroTarget(player);
            if (golem.distanceToSqr(player) <= GOLEM_CLOSE_ENOUGH * GOLEM_CLOSE_ENOUGH) {
                hasCloseGolem = true;
            }
        }

        if (!hasCloseGolem) {
            spawnGolemBehindPlayer(level, player);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable(Translations.Chat.VILLAGE_PROTECTION_ALERT), true);
        }
    }

    private static void spawnGolemBehindPlayer(ServerLevel level, Player player) {
        float yawRad = player.getYRot() * Mth.DEG_TO_RAD;
        double behindX = player.getX() + Mth.sin(yawRad) * SPAWN_DISTANCE_BEHIND;
        double behindZ = player.getZ() - Mth.cos(yawRad) * SPAWN_DISTANCE_BEHIND;

        int spawnX = Mth.floor(behindX);
        int spawnZ = Mth.floor(behindZ);

        BlockPos surfacePos = findSpawnablePos(level, spawnX, spawnZ);
        if (surfacePos == null) {
            for (int attempt = 0; attempt < 4; attempt++) {
                int ox = spawnX + level.random.nextIntBetweenInclusive(-3, 3);
                int oz = spawnZ + level.random.nextIntBetweenInclusive(-3, 3);
                surfacePos = findSpawnablePos(level, ox, oz);
                if (surfacePos != null) break;
            }
        }

        if (surfacePos == null) return;

        MoonGolemEntity golem = GCEntityTypes.MOON_GOLEM.create(level);
        if (golem == null) return;

        golem.moveTo(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5, level.random.nextFloat() * 360.0F, 0.0F);
        golem.setPlayerCreated(false);
        golem.setPersistenceRequired();
        level.addFreshEntity(golem);
        golem.gc$setVillageAggroTarget(player);
    }

    private static BlockPos findSpawnablePos(ServerLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (level.getBlockState(pos.below()).isSolid()
                && level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir()
                && level.getBlockState(pos.above(2)).isAir()) {
            return pos;
        }
        return null;
    }
}
