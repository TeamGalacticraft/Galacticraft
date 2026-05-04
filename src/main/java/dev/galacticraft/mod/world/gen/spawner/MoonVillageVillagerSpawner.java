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
import dev.galacticraft.mod.content.entity.MoonVillagerEntity;
import dev.galacticraft.mod.village.MoonVillagerTypes;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class MoonVillageVillagerSpawner {
    private static final int MAX_PENDING_CHUNKS_PER_TICK = 2;
    private static final int MIN_VILLAGERS_PER_VILLAGE = 2;
    private static final int MIN_SPAWN_DISTANCE = 2;
    private static final int MAX_SPAWN_DISTANCE = 16;
    private static final int POSITION_ATTEMPTS = 12;
    private static final double VILLAGER_CHECK_RADIUS = 48.0D;
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

    public int tickChunk(ServerLevel world, LevelChunk chunk) {
        var villageStructure = world.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(GCStructures.Moon.VILLAGE);
        if (chunk.getReferencesForStructure(villageStructure).isEmpty()) {
            return 0;
        }

        MoonVillageAnchorLocator.VillageAnchor anchor = MoonVillageAnchorLocator.findVillageAnchor(world, chunk, villageStructure);
        if (anchor == null) {
            return 0;
        }

        MoonVillageVillagerSpawnData spawnData = MoonVillageVillagerSpawnData.get(world);
        if (spawnData.hasSeeded(anchor.key())) {
            return 0;
        }

        int existingVillagers = this.countNearbyVillagers(world, anchor.position());
        if (existingVillagers >= MIN_VILLAGERS_PER_VILLAGE) {
            spawnData.markSeeded(anchor.key());
            return 0;
        }

        int neededVillagers = MIN_VILLAGERS_PER_VILLAGE - existingVillagers;
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        int spawned = 0;

        for (int villagerIndex = 0; villagerIndex < neededVillagers; ++villagerIndex) {
            boolean villagerSpawned = false;
            for (int attempt = 0; attempt < POSITION_ATTEMPTS; ++attempt) {
                if (!this.findSpawnPosition(world, anchor.position(), candidate, world.random)) {
                    continue;
                }

                MoonVillagerEntity villager = GCEntityTypes.MOON_VILLAGER.create(world);
                if (villager == null) {
                    return spawned;
                }

                villager.moveTo(candidate.getX() + 0.5D, candidate.getY(), candidate.getZ() + 0.5D, world.random.nextFloat() * 360.0F, 0.0F);
                if (!this.canSpawn(world, candidate, villager)) {
                    continue;
                }

                villager.finalizeSpawn(world, world.getCurrentDifficultyAt(candidate), MobSpawnType.EVENT, null);
                villager.setVillagerData(villager.getVillagerData()
                        .setType(MoonVillagerTypes.MOON_HIGHLANDS)
                        .setProfession(VillagerProfession.NONE)
                        .setLevel(1));
                villager.setVillagerXp(0);
                villager.setPersistenceRequired();
                world.addFreshEntityWithPassengers(villager);
                ++spawned;
                villagerSpawned = true;
                break;
            }

            if (!villagerSpawned) {
                break;
            }
        }

        if (existingVillagers + spawned >= MIN_VILLAGERS_PER_VILLAGE) {
            spawnData.markSeeded(anchor.key());
        }

        return spawned;
    }

    private int countNearbyVillagers(ServerLevel world, BlockPos anchor) {
        return world.getEntitiesOfClass(MoonVillagerEntity.class, new AABB(anchor).inflate(VILLAGER_CHECK_RADIUS), MoonVillagerEntity::isAlive).size();
    }

    private boolean findSpawnPosition(ServerLevel world, BlockPos anchor, BlockPos.MutableBlockPos candidate, RandomSource random) {
        int offsetX = Mth.nextInt(random, MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE) * (random.nextBoolean() ? 1 : -1);
        int offsetZ = Mth.nextInt(random, MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE) * (random.nextBoolean() ? 1 : -1);
        candidate.set(anchor.getX() + offsetX, anchor.getY(), anchor.getZ() + offsetZ);

        BlockPos spawnPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
        candidate.set(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        return world.hasChunksAt(candidate.getX() - 10, candidate.getY() - 10, candidate.getZ() - 10, candidate.getX() + 10, candidate.getY() + 10, candidate.getZ() + 10);
    }

    private boolean canSpawn(ServerLevel world, BlockPos pos, MoonVillagerEntity villager) {
        BlockState blockState = world.getBlockState(pos);
        return NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), villager.getType())
                && world.noCollision(villager)
                && villager.checkSpawnObstruction(world);
    }
}