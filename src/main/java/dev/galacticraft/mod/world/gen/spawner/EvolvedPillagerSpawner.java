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

package dev.galacticraft.mod.world.gen.spawner;

import dev.galacticraft.mod.content.GCEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class EvolvedPillagerSpawner implements CustomSpawner {
   private int ticksUntilNextSpawn;

   public int tick(ServerLevel world, boolean spawnMonsters, boolean spawnAnimals) {
      if (!spawnMonsters) {
         return 0;
      } else if (!world.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
         return 0;
      } else {
         RandomSource random = world.random;
         --this.ticksUntilNextSpawn;
         if (this.ticksUntilNextSpawn > 0) {
            return 0;
         } else {
            this.ticksUntilNextSpawn += 10000 + random.nextInt(1000);
            long l = world.getDayTime() / 24000L;
            if (l >= 5L && world.isDay()) {
               if (random.nextInt(5) != 0) {
                  return 0;
               } else {
                  int i = world.players().size();
                  if (i < 1) {
                     return 0;
                  } else {
                     Player player = world.players().get(random.nextInt(i));
                     if (player.isSpectator()) {
                        return 0;
                     } else if (world.isCloseToVillage(player.blockPosition(), 2)) {
                        return 0;
                     } else {
                        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos mutable = player.blockPosition().mutable().move(j, 0, k);
                        if (!world.hasChunksAt(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)) {
                           return 0;
                        } else {
                           Holder<Biome> biome = world.getBiome(mutable);
                           if (biome.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                              return 0;
                           } else {
                              int m = 0;
                              int n = (int)Math.ceil(world.getCurrentDifficultyAt(mutable).getEffectiveDifficulty()) + 1;

                              for(int o = 0; o < n; ++o) {
                                 ++m;
                                 mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
                                 if (o == 0) {
                                    if (!this.spawnPillager(world, mutable, random, true)) {
                                       break;
                                    }
                                 } else {
                                    this.spawnPillager(world, mutable, random, false);
                                 }

                                 mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                 mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                              }

                              return m;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   /**
    * @param captain whether the pillager is the captain of a patrol
    */
   private boolean spawnPillager(ServerLevel world, BlockPos pos, RandomSource random, boolean captain) {
      BlockState blockState = world.getBlockState(pos);
      if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), GCEntityTypes.EVOLVED_PILLAGER)) {
         return false;
      } else if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(GCEntityTypes.EVOLVED_PILLAGER, world, MobSpawnType.PATROL, pos, random)) {
         return false;
      } else {
         PatrollingMonster patrolEntity = GCEntityTypes.EVOLVED_PILLAGER.create(world);
         if (patrolEntity != null) {
            if (captain) {
               patrolEntity.setPatrolLeader(true);
               patrolEntity.findPatrolTarget();
            }

            patrolEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            patrolEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null, null);
            world.addFreshEntityWithPassengers(patrolEntity);
            return true;
         } else {
            return false;
         }
      }
   }
}
