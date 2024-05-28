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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.entity.EvolvedCreeperEntity;
import dev.galacticraft.mod.content.entity.EvolvedSkeletonEntity;
import dev.galacticraft.mod.content.entity.EvolvedSpiderEntity;
import dev.galacticraft.mod.content.entity.EvolvedZombieEntity;
import dev.galacticraft.mod.content.entity.boss.AbstractBossEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class DungeonSpawnerBlockEntity extends BlockEntity implements Spawner {
    public EntityType<? extends AbstractBossEntity> bossType;
    public AbstractBossEntity boss;
    public boolean spawned;
    public boolean isBossDefeated;
    public boolean playerInRange;
    public boolean lastPlayerInRange;
    private Vec3i roomCoords;
    private Vec3i roomSize;
    public long lastKillTime;
    private BlockPos chestPos;
    private AABB range15 = null;
    private AABB rangeBounds = null;
    private AABB rangeBoundsPlus3 = null;
    private AABB rangeBoundsPlus11 = null;

    public DungeonSpawnerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.DUNGEON_BOSS_SPAWNER, blockPos, blockState);
    }

    @Override
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.bossType = (EntityType<? extends AbstractBossEntity>) type;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DungeonSpawnerBlockEntity blockEntity) {
        blockEntity.tick((ServerLevel) level, pos, state);
    }

    public void tick(ServerLevel level, BlockPos pos, BlockState state) {

        if (this.roomCoords == null) {
            return;
        }

        if (this.range15 == null) {
            final Vec3 thisVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            this.range15 = new AABB(thisVec.x - 15, thisVec.y - 15, thisVec.z - 15, thisVec.x + 15, thisVec.y + 15, thisVec.z + 15);
            this.rangeBounds = new AABB(this.roomCoords.getX(), this.roomCoords.getY(), this.roomCoords.getZ(), this.roomCoords.getX() + this.roomSize.getX(),
                    this.roomCoords.getY() + this.roomSize.getY(), this.roomCoords.getZ() + this.roomSize.getZ());
            this.rangeBoundsPlus3 = this.rangeBounds.inflate(3, 3, 3);
        }

        if (this.lastKillTime > 0 && Util.getMillis() - lastKillTime > 900000) // 15
        // minutes
        {
            this.lastKillTime = 0;
            this.isBossDefeated = false;
            // After 15 minutes a new boss is able to be spawned
        }

        final List<? extends Entity> l = level.getEntities(bossType, this.range15, Entity::isAlive);

        for (final Entity e : l) {
            this.boss = (AbstractBossEntity) e;
            this.spawned = true;
            this.isBossDefeated = false;
            this.boss.onBossSpawned(this);
        }

        List<Monster> entitiesWithin = level.getEntitiesOfClass(Monster.class, this.rangeBoundsPlus3);

        for (Entity mob : entitiesWithin) {
            if (this.getDisabledCreatures().contains(mob.getClass())) {
                mob.remove(Entity.RemovalReason.KILLED);
            }
        }

        List<Player> playersWithin = level.getEntitiesOfClass(Player.class, this.rangeBounds);

        this.playerInRange = !playersWithin.isEmpty();

        if (this.playerInRange) {
            if (!this.lastPlayerInRange && !this.spawned) {
                // Try to create a boss entity
                if (this.boss == null && !this.isBossDefeated) {
                    this.boss = this.bossType.create(level);
                    this.boss.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                }

                // Now spawn the boss
                if (this.boss != null) {
                    this.boss.finalizeSpawn(level, level.getCurrentDifficultyAt(this.boss.blockPosition()), MobSpawnType.SPAWNER, null, null);
                    level.addFreshEntity(this.boss);
                    this.playSpawnSound(this.boss);
                    this.spawned = true;
                }
            }
        }

        this.lastPlayerInRange = this.playerInRange;
    }

    public void playSpawnSound(Entity entity) {

    }

    public List<Class<? extends Mob>> getDisabledCreatures() {
        List<Class<? extends Mob>> list = new ArrayList<>();
        list.add(EvolvedSkeletonEntity.class);
        list.add(EvolvedCreeperEntity.class);
        list.add(EvolvedZombieEntity.class);
        list.add(EvolvedSpiderEntity.class);
        return list;
    }

    public void setRoom(Vec3i coords, Vec3i size) {
        this.roomCoords = coords;
        this.roomSize = size;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.playerInRange = this.lastPlayerInRange = tag.getBoolean("playerInRange");
        this.isBossDefeated = tag.getBoolean("defeated");

        try {
            this.bossType = (EntityType<? extends AbstractBossEntity>) BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(tag.getString("bossType")));
        } catch (Exception e) {
            // This exception will be thrown when read is called from
            // TileEntity.handleUpdateTag
            // but we only care if an exception is thrown on server side read
            if (!this.level.isClientSide) {
                e.printStackTrace();
            }
        }

        this.roomCoords = new Vec3i(tag.getInt("roomCoordsX"), tag.getInt("roomCoordsY"), tag.getInt("roomCoordsZ"));
        this.roomSize = new Vec3i(tag.getInt("roomSizeX"), tag.getInt("roomSizeY"), tag.getInt("roomSizeZ"));

        if (tag.contains("lastKillTime")) {
            this.lastKillTime = tag.getLong("lastKillTime");
        } else if (tag.contains("lastKillTimeNew")) {
            long savedTime = tag.getLong("lastKillTimeNew");
            this.lastKillTime = savedTime == 0 ? 0 : savedTime + Util.getMillis();
        }

        if (tag.contains("chestPosNull") && !tag.getBoolean("chestPosNull")) {
            this.chestPos = new BlockPos(tag.getInt("chestX"), tag.getInt("chestY"), tag.getInt("chestZ"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putBoolean("playerInRange", this.playerInRange);
        tag.putBoolean("defeated", this.isBossDefeated);
        tag.putString("bossType", BuiltInRegistries.ENTITY_TYPE.getKey(this.bossType).toString());

        if (this.roomCoords != null) {
            tag.putInt("roomCoordsX", this.roomCoords.getX());
            tag.putInt("roomCoordsY", this.roomCoords.getY());
            tag.putInt("roomCoordsZ", this.roomCoords.getZ());
            tag.putInt("roomSizeX", this.roomSize.getX());
            tag.putInt("roomSizeY", this.roomSize.getY());
            tag.putInt("roomSizeZ", this.roomSize.getZ());
        }

        tag.putLong("lastKillTimeNew", this.lastKillTime == 0 ? 0 : this.lastKillTime - Util.getMillis());

        tag.putBoolean("chestPosNull", this.chestPos == null);
        if (this.chestPos != null) {
            tag.putInt("chestX", this.chestPos.getX());
            tag.putInt("chestY", this.chestPos.getY());
            tag.putInt("chestZ", this.chestPos.getZ());
        }
    }

    public BlockPos getChestPos() {
        return chestPos;
    }

    public void setChestPos(BlockPos chestPos) {
        this.chestPos = chestPos;
    }

    public AABB getRangeBounds() {
        if (this.rangeBounds == null)
            this.rangeBounds = new AABB(this.roomCoords.getX(), this.roomCoords.getY(), this.roomCoords.getZ(), this.roomCoords.getX() + this.roomSize.getX(),
                    this.roomCoords.getY() + this.roomSize.getY(), this.roomCoords.getZ() + this.roomSize.getZ());

        return this.rangeBounds;
    }

    public AABB getRangeBoundsPlus11() {
        if (this.rangeBoundsPlus11 == null)
            this.rangeBoundsPlus11 = this.getRangeBounds().inflate(11, 11, 11);

        return this.rangeBoundsPlus11;
    }
}
