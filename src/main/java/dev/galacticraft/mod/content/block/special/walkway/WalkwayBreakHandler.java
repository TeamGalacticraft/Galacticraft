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

package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.entity.WalkwayHolder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Breaks only the part of a pipe or wire with a walkway that the player aims at, see {@link WalkwayAttachable}.
 */
public final class WalkwayBreakHandler {
    /**
     * Block entities to send to the players that broke part of them. The client of the player already removed the block,
     * so it ignores block entity data until the server confirms the break at the end of the tick.
     * Without sending it again, the block comes back with an empty block entity, e.g. without its connections.
     */
    private static final List<PendingUpdate> PENDING = new ArrayList<>();

    private WalkwayBreakHandler() {
    }

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (state.getBlock() instanceof WalkwayAttachable && blockEntity instanceof WalkwayHolder holder && holder.hasWalkway()) {
                HitResult hit = player.pick(player.blockInteractionRange(), 1.0f, false);
                // Without a hit on this block, remove the walkway first
                Vec3 location = hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(pos) ? hit.getLocation() : null;
                WalkwayAttachable.breakPart(level, player, pos, state, holder, location);
                if (player instanceof ServerPlayer serverPlayer) {
                    PENDING.add(new PendingUpdate(serverPlayer, pos.immutable()));
                }
                return false;
            }
            return true;
        });

        // The break is confirmed when the connection ticks, which is before the end of the server tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (PendingUpdate update : PENDING) {
                BlockEntity blockEntity = update.player().level().getBlockEntity(update.pos());
                Packet<?> packet = blockEntity != null ? blockEntity.getUpdatePacket() : null;
                if (packet != null && !update.player().hasDisconnected()) {
                    update.player().connection.send(packet);
                }
            }
            PENDING.clear();
        });
    }

    private record PendingUpdate(ServerPlayer player, BlockPos pos) {
    }
}
