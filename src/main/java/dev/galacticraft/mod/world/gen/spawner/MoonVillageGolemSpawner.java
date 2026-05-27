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

package dev.galacticraft.mod.world.gen.spawner;

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.MoonGolemEntity;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class MoonVillageGolemSpawner {
    private static final int MAX_PENDING_CHUNKS_PER_TICK = 2;
    private static final int MIN_SPAWN_DISTANCE = 4;
    private static final int MAX_SPAWN_DISTANCE = 18;
    private static final int POSITION_ATTEMPTS = 12;
    private static final double GOLEM_CHECK_RADIUS = 48.0D;
    private final LongLinkedOpenHashSet pendingChunks = new LongLinkedOpenHashSet();

    public void enqueueChunk(ServerLevel world, LevelChunk chunk) {
        var villageStructure = world.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(GCStructures.Moon.VILLAGE);
        if (!chunk.getReferencesForStructure(villageStructure).isEmpty()) {
            this.pendingChunks.add(chunk.getPos().toLong());
        }
    }

    public void tick(ServerLevel world) {
        for (int processed = 0; processed < MAX_PENDING_CHUNKS_PER_TICK && !this.pendingChunks.isEmpty(); ++processed) {
            ChunkPos chunkPos = new ChunkPos(this.pendingChunks.removeFirstLong());
            if (!world.hasChunk(chunkPos.x, chunkPos.z)) {
                continue;
            }

            this.tickChunk(world, world.getChunk(chunkPos.x, chunkPos.z));
        }
    }

    public boolean tickChunk(ServerLevel world, LevelChunk chunk) {
        var villageStructure = world.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(GCStructures.Moon.VILLAGE);
        if (chunk.getReferencesForStructure(villageStructure).isEmpty()) {
            return false;
        }

        MoonVillageAnchorLocator.VillageAnchor anchor = MoonVillageAnchorLocator.findVillageAnchor(world, chunk, villageStructure);
        if (anchor == null) {
            return false;
        }

        MoonVillageGolemSpawnData spawnData = MoonVillageGolemSpawnData.get(world);
        if (spawnData.hasSeeded(anchor.key())) {
            return false;
        }

        if (this.hasNearbyGolem(world, anchor.position())) {
            spawnData.markSeeded(anchor.key());
            return false;
        }

        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int attempts = 0; attempts < POSITION_ATTEMPTS; ++attempts) {
            if (!this.findSpawnPosition(world, anchor.position(), candidate, world.random)) {
                continue;
            }

            MoonGolemEntity golem = GCEntityTypes.MOON_GOLEM.create(world);
            if (golem == null) {
                return false;
            }

            golem.moveTo(candidate.getX() + 0.5D, candidate.getY(), candidate.getZ() + 0.5D, world.random.nextFloat() * 360.0F, 0.0F);
            if (!world.noCollision(golem)) {
                continue;
            }

            golem.finalizeSpawn(world, world.getCurrentDifficultyAt(candidate), MobSpawnType.EVENT, null);
            golem.setPlayerCreated(false);
            golem.setPersistenceRequired();
            world.addFreshEntityWithPassengers(golem);
            spawnData.markSeeded(anchor.key());
            return true;
        }

        return false;
    }

    private boolean hasNearbyGolem(ServerLevel world, BlockPos anchor) {
        return !world.getEntitiesOfClass(IronGolem.class, new AABB(anchor).inflate(GOLEM_CHECK_RADIUS), IronGolem::isAlive).isEmpty();
    }

    private boolean findSpawnPosition(ServerLevel world, BlockPos anchor, BlockPos.MutableBlockPos candidate, RandomSource random) {
        int offsetX = Mth.nextInt(random, MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE) * (random.nextBoolean() ? 1 : -1);
        int offsetZ = Mth.nextInt(random, MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE) * (random.nextBoolean() ? 1 : -1);
        candidate.set(anchor.getX() + offsetX, anchor.getY(), anchor.getZ() + offsetZ);

        BlockPos spawnPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
        candidate.set(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        return world.hasChunksAt(candidate.getX() - 10, candidate.getY() - 10, candidate.getZ() - 10, candidate.getX() + 10, candidate.getY() + 10, candidate.getZ() + 10);
    }
}