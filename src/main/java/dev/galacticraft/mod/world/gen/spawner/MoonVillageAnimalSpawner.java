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
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class MoonVillageAnimalSpawner {
    private static final int MAX_PENDING_CHUNKS_PER_TICK = 2;
    private static final int LOCAL_ANIMAL_CAP = 12;
    private static final int MIN_SPAWN_DISTANCE = 10;
    private static final int MAX_SPAWN_DISTANCE = 28;
    private static final int POSITION_ATTEMPTS = 8;
    private static final int GROUP_ATTEMPTS = 24;
    private static final SpawnEntry[] SPAWNS = new SpawnEntry[]{
            new SpawnEntry(GCEntityTypes.MOON_COW, 6, 3, 4),
            new SpawnEntry(GCEntityTypes.MOON_SHEEP, 8, 3, 4),
            new SpawnEntry(GCEntityTypes.MOON_CHICKEN, 10, 4, 6)
    };
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

        MoonVillageAnimalSpawnData spawnData = MoonVillageAnimalSpawnData.get(world);
        if (spawnData.hasSeeded(anchor.key())) {
            return 0;
        }

        int nearbyAnimals = this.countNearbyAnimals(world, anchor.position());
        if (nearbyAnimals > 0) {
            spawnData.markSeeded(anchor.key());
            return 0;
        }

        if (nearbyAnimals >= LOCAL_ANIMAL_CAP) {
            return 0;
        }

        SpawnEntry spawn = this.pickSpawn(world.random);
        int targetCount = Math.min(LOCAL_ANIMAL_CAP - nearbyAnimals, spawn.groupSize(world.random));
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        int spawned = 0;

        for (int attempts = 0; attempts < GROUP_ATTEMPTS && spawned < targetCount; ++attempts) {
            if (!this.findSpawnPosition(world, anchor.position(), candidate, world.random)) {
                continue;
            }

            Animal animal = spawn.type().create(world);
            if (animal == null) {
                continue;
            }

            animal.moveTo(candidate.getX() + 0.5D, candidate.getY(), candidate.getZ() + 0.5D, world.random.nextFloat() * 360.0F, 0.0F);
            if (!this.canSpawn(world, candidate, animal, world.random)) {
                continue;
            }

            animal.finalizeSpawn(world, world.getCurrentDifficultyAt(candidate), MobSpawnType.NATURAL, null);
            animal.setPersistenceRequired();
            world.addFreshEntityWithPassengers(animal);
            ++spawned;
        }

        if (spawned > 0) {
            spawnData.markSeeded(anchor.key());
        }

        return spawned;
    }

    private int countNearbyAnimals(ServerLevel world, BlockPos anchor) {
        return world.getEntitiesOfClass(Animal.class, new AABB(anchor).inflate(32.0D), animal -> animal.isAlive() && isMoonVillageAnimal(animal.getType())).size();
    }

    private boolean findSpawnPosition(ServerLevel world, BlockPos anchor, BlockPos.MutableBlockPos candidate, RandomSource random) {
        for (int attempt = 0; attempt < POSITION_ATTEMPTS; ++attempt) {
            int offsetX = Mth.nextInt(random, MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE) * (random.nextBoolean() ? 1 : -1);
            int offsetZ = Mth.nextInt(random, MIN_SPAWN_DISTANCE, MAX_SPAWN_DISTANCE) * (random.nextBoolean() ? 1 : -1);
            candidate.set(anchor.getX() + offsetX, anchor.getY(), anchor.getZ() + offsetZ);

            BlockPos spawnPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
            candidate.set(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            if (!world.hasChunksAt(candidate.getX() - 10, candidate.getY() - 10, candidate.getZ() - 10, candidate.getX() + 10, candidate.getY() + 10, candidate.getZ() + 10)) {
                continue;
            }

            return true;
        }

        return false;
    }

    private boolean canSpawn(ServerLevel world, BlockPos pos, Animal animal, RandomSource random) {
        BlockState blockState = world.getBlockState(pos);
        return NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), animal.getType())
                && SpawnPlacements.checkSpawnRules(animal.getType(), world, MobSpawnType.NATURAL, pos, random)
                && animal.checkSpawnObstruction(world);
    }

    private SpawnEntry pickSpawn(RandomSource random) {
        int totalWeight = 0;
        for (SpawnEntry spawn : SPAWNS) {
            totalWeight += spawn.weight();
        }

        int choice = random.nextInt(totalWeight);
        for (SpawnEntry spawn : SPAWNS) {
            choice -= spawn.weight();
            if (choice < 0) {
                return spawn;
            }
        }

        return SPAWNS[SPAWNS.length - 1];
    }

    private static boolean isMoonVillageAnimal(EntityType<?> entityType) {
        return entityType == GCEntityTypes.MOON_COW || entityType == GCEntityTypes.MOON_SHEEP || entityType == GCEntityTypes.MOON_CHICKEN;
    }

    private record SpawnEntry(EntityType<? extends Animal> type, int weight, int minGroupSize, int maxGroupSize) {
        private int groupSize(RandomSource random) {
            return Mth.nextInt(random, this.minGroupSize, this.maxGroupSize);
        }
    }
}