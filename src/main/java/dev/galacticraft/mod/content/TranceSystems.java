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

package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attachments.GCAttachments;
import dev.galacticraft.mod.content.TranceData.Stage;
import dev.galacticraft.mod.network.s2c.HallucinationStatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.chunk.status.ChunkStatus;


public final class TranceSystems {
    private static final int CHUNK_RADIUS = 6; // ~view distance

    private TranceSystems() {}

    /* ---- helpers -------------------------------------------------------- */

    private static TranceData data(ServerPlayer sp) {
        TranceData d = sp.getAttached(GCAttachments.TRANCE);
        if (d == null) {
            d = new TranceData();
            sp.setAttached(GCAttachments.TRANCE, d);
            Constant.LOGGER.debug("[Trance] {}: created new TranceData attachment", name(sp));
        }
        return d;
    }

    private static String name(ServerPlayer sp) {
        return sp.getGameProfile().getName() + "/" + sp.getUUID();
    }

    /* ---- lifecycle ------------------------------------------------------ */

    public static void beginNausea(ServerPlayer sp) {
        TranceData d = data(sp);
        if (d.active) {
            Constant.LOGGER.debug("[Trance] {}: beginNausea ignored (already active, stage={})", name(sp), d.stage);
            return;
        }

        d.active = true;
        d.stage = Stage.NAUSEA;

        // pick a target dimension != current
        var current = sp.serverLevel().dimension();
        var options = sp.server.levelKeys().stream().filter(k -> !k.equals(current)).toList();

        if (options.isEmpty()) {
            Constant.LOGGER.warn("[Trance] {}: no alternate dimensions available; ending immediately", name(sp));
            end(sp);
            return;
        }

        d.targetDim = options.get(sp.getRandom().nextInt(options.size()));
        d.targetViewPos = sp.blockPosition();

        Constant.LOGGER.info("[Trance] {}: NAUSEA started. targetDim={}, targetPos={}",
                name(sp), d.targetDim.location(), d.targetViewPos);

        forceChunks(sp.server, d.targetDim, d.targetViewPos, CHUNK_RADIUS, true);
    }

    public static void end(ServerPlayer sp) {
        ServerPlayNetworking.send(sp, new HallucinationStatePacket(false));
        TranceData d = data(sp);
        if (!d.active) {
            Constant.LOGGER.debug("[Trance] {}: end() ignored (not active)", name(sp));
            return;
        }

        if (d.targetDim != null && d.targetViewPos != null) {
            Constant.LOGGER.info("[Trance] {}: ending trance; releasing forced chunks for dim={} center={}",
                    name(sp), d.targetDim.location(), d.targetViewPos);
            forceChunks(sp.server, d.targetDim, d.targetViewPos, CHUNK_RADIUS, false);
        }

        // Safe even if not present
        sp.removeEffect(GCEffects.MOON_TANGLE_TRANCE);
        sp.removeEffect(net.minecraft.world.effect.MobEffects.CONFUSION);

        Constant.LOGGER.info("[Trance] {}: trance cleared (effects removed)", name(sp));

        d.active = false;
        d.stage = null;
        d.targetDim = null;
        d.targetViewPos = null;
        d.hallucinateUntilGameTime = 0L;
    }

    /* ---- ticking -------------------------------------------------------- */

    public static void serverTick(ServerPlayer sp) {
        TranceData d = data(sp);
        if (!d.active) return;

        // If the trance effect was cleared (milk/command), end immediately
        if (!hasTranceEffect(sp)) {
            Constant.LOGGER.info("[Trance] {}: trance effect removed externally; ending trip", name(sp));
            end(sp);
            return;
        }

        if (d.stage == Stage.NAUSEA) {
            if (d.targetDim == null || d.targetViewPos == null) {
                Constant.LOGGER.warn("[Trance] {}: NAUSEA has null target; ending", name(sp));
                end(sp);
                return;
            }

            boolean ready = areChunksReady(sp.server, d);
            Constant.LOGGER.debug("[Trance] {}: NAUSEA tick — chunksReady={}", name(sp), ready);

            if (ready) {
                sp.removeEffect(net.minecraft.world.effect.MobEffects.CONFUSION);
                d.stage = Stage.HALLUCINATE;
                d.hallucinateUntilGameTime = sp.level().getGameTime() + (20L * 120L);
                ServerPlayNetworking.send(sp, new HallucinationStatePacket(true));
                Constant.LOGGER.info("[Trance] {}: -> HALLUCINATE (until gameTime={})",
                        name(sp), d.hallucinateUntilGameTime);
            }
        } else if (d.stage == Stage.HALLUCINATE) {
            long now = sp.level().getGameTime();
            if (now >= d.hallucinateUntilGameTime) {
                Constant.LOGGER.info("[Trance] {}: HALLUCINATE complete (now={}, until={})",
                        name(sp), now, d.hallucinateUntilGameTime);
                end(sp);
            }
        }
    }

    /* ---- chunks --------------------------------------------------------- */

    private static void forceChunks(MinecraftServer server, ResourceKey<Level> dim, BlockPos center, int radius, boolean add) {
        ServerLevel target = server.getLevel(dim);
        if (target == null) {
            Constant.LOGGER.warn("[Trance] forceChunks: target dim {} is null", dim.location());
            return;
        }

        ChunkPos base = new ChunkPos(center);
        var cache = target.getChunkSource();
        int count = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos pos = new ChunkPos(base.x + dx, base.z + dz);
                if (add) {
                    // Lightweight ticket used by Nether portals; keeps chunks active without global forceload.
                    cache.addRegionTicket(TicketType.PORTAL, pos, 1, center);
                } else {
                    cache.removeRegionTicket(TicketType.PORTAL, pos, 1, center);
                }
                count++;
            }
        }

        Constant.LOGGER.info("[Trance] tickets: {} {} dim={} center={} radius={} ({} chunks)",
                add ? "added" : "removed", TicketType.PORTAL, dim.location(), center, radius, count);
    }

    private static boolean areChunksReady(MinecraftServer server, TranceData d) {
        ServerLevel tl = server.getLevel(d.targetDim);
        if (tl == null) {
            Constant.LOGGER.debug("[Trance] areChunksReady: target level is null");
            return false;
        }

        ChunkPos c = new ChunkPos(d.targetViewPos);
        int r = 3;

        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                ChunkAccess chunk = tl.getChunkSource().getChunkNow(c.x + dx, c.z + dz);
                if (chunk == null || !chunk.getHighestGeneratedStatus().isOrAfter(ChunkStatus.FULL)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasTranceEffect(ServerPlayer sp) {
        return sp.hasEffect(GCEffects.MOON_TANGLE_TRANCE);
    }
}