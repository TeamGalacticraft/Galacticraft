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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.LunarCheesePressBlock;
import dev.galacticraft.mod.content.entity.MoonVillagerEntity;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.village.GCVillagerProfessions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LunarCheesePressBlockEntity extends BlockEntity {
    private static final String ANIMATION_START_TICK_TAG = "AnimationStartTick";
    private static final String WORK_CYCLE_START_TICK_TAG = "WorkCycleStartTick";
    private static final int ANIMATION_DURATION = 36;
    private static final int PRESS_START_DELAY = 12;
    private static final int WORK_CYCLE_DURATION = PRESS_START_DELAY + ANIMATION_DURATION + 6;
    private static final int ANIMATION_CONTACT_TICK = ANIMATION_DURATION / 2;
    private static final int VILLAGER_WORK_STAND_TICKS = 60;
    private static final int VILLAGER_WORK_ANIMATION_INTERVAL = 180;
    private static final float MAX_PRESS_OFFSET = 7.0F / 16.0F;
    private static final double CONTACT_PARTICLE_RADIUS = 0.2D;
    private static final double CONTACT_PARTICLE_Y = 1.08D;
    private long animationStartTick = Long.MIN_VALUE;
    private long workCycleStartTick = Long.MIN_VALUE;
    private long cheeseMakerReadyAt = Long.MIN_VALUE;
    private long nextVillagerAnimationTick = Long.MIN_VALUE;

    public LunarCheesePressBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.LUNAR_CHEESE_PRESS, pos, state);
    }

    public void startAnimation() {
        Level level = this.getLevel();
        if (level == null) {
            return;
        }

        long gameTime = level.getGameTime();
        this.workCycleStartTick = gameTime;
        this.animationStartTick = gameTime + PRESS_START_DELAY;
        this.setChanged();
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LunarCheesePressBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        blockEntity.tickAnimationEffects(serverLevel);

        long gameTime = serverLevel.getGameTime();
        if (blockEntity.isWorkCycleActive(gameTime)) {
            blockEntity.cheeseMakerReadyAt = Long.MIN_VALUE;
            return;
        }

        if (gameTime < blockEntity.nextVillagerAnimationTick) {
            blockEntity.cheeseMakerReadyAt = Long.MIN_VALUE;
            return;
        }

        if (blockEntity.hasCheeseMakerWorking(serverLevel)) {
            if (blockEntity.cheeseMakerReadyAt == Long.MIN_VALUE) {
                blockEntity.cheeseMakerReadyAt = gameTime;
                return;
            }

            if (gameTime - blockEntity.cheeseMakerReadyAt >= VILLAGER_WORK_STAND_TICKS) {
                blockEntity.startAnimation();
                blockEntity.nextVillagerAnimationTick = gameTime + WORK_CYCLE_DURATION + VILLAGER_WORK_ANIMATION_INTERVAL;
                blockEntity.cheeseMakerReadyAt = Long.MIN_VALUE;
            }
        } else {
            blockEntity.cheeseMakerReadyAt = Long.MIN_VALUE;
        }
    }

    public float getPressOffset(float tickDelta) {
        Level level = this.getLevel();
        if (level == null || this.animationStartTick == Long.MIN_VALUE) {
            return 0.0F;
        }

        float elapsed = (level.getGameTime() - this.animationStartTick) + tickDelta;
        if (elapsed < 0.0F || elapsed >= ANIMATION_DURATION) {
            return 0.0F;
        }

        float progress = elapsed / ANIMATION_DURATION;
        return Mth.sin(progress * Mth.PI) * MAX_PRESS_OFFSET;
    }

    public boolean isCheeseVisible(float tickDelta) {
        Level level = this.getLevel();
        if (level == null || this.workCycleStartTick == Long.MIN_VALUE) {
            return false;
        }

        float elapsed = (level.getGameTime() - this.workCycleStartTick) + tickDelta;
        return elapsed >= 0.0F && elapsed < WORK_CYCLE_DURATION;
    }

    public boolean shouldVillagerApproachForWork() {
        Level level = this.getLevel();
        if (level == null) {
            return false;
        }

        long gameTime = level.getGameTime();
        if (this.isWorkCycleActive(gameTime)) {
            return true;
        }

        if (this.cheeseMakerReadyAt != Long.MIN_VALUE) {
            return true;
        }

        if (this.nextVillagerAnimationTick == Long.MIN_VALUE) {
            return true;
        }

        return gameTime >= this.nextVillagerAnimationTick - VILLAGER_WORK_STAND_TICKS;
    }

    private void tickAnimationEffects(ServerLevel level) {
        if (this.workCycleStartTick == Long.MIN_VALUE) {
            return;
        }

        long cycleElapsed = level.getGameTime() - this.workCycleStartTick;
        if (this.animationStartTick != Long.MIN_VALUE && level.getGameTime() - this.animationStartTick == ANIMATION_CONTACT_TICK) {
            this.spawnPressContactParticles(level);
        }

        if (cycleElapsed >= WORK_CYCLE_DURATION) {
            this.workCycleStartTick = Long.MIN_VALUE;
            this.animationStartTick = Long.MIN_VALUE;
            this.setChanged();
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void spawnPressContactParticles(ServerLevel level) {
        double centerX = this.worldPosition.getX() + 0.5D;
        double centerY = this.worldPosition.getY() + CONTACT_PARTICLE_Y;
        double centerZ = this.worldPosition.getZ() + 0.5D;

        level.sendParticles(GCParticleTypes.DRIPPING_FUEL, centerX + CONTACT_PARTICLE_RADIUS, centerY, centerZ, 2, 0.03D, 0.02D, 0.05D, 0.0D);
        level.sendParticles(GCParticleTypes.DRIPPING_FUEL, centerX - CONTACT_PARTICLE_RADIUS, centerY, centerZ, 2, 0.03D, 0.02D, 0.05D, 0.0D);
        level.sendParticles(GCParticleTypes.DRIPPING_FUEL, centerX, centerY, centerZ + CONTACT_PARTICLE_RADIUS, 2, 0.05D, 0.02D, 0.03D, 0.0D);
        level.sendParticles(GCParticleTypes.DRIPPING_FUEL, centerX, centerY, centerZ - CONTACT_PARTICLE_RADIUS, 2, 0.05D, 0.02D, 0.03D, 0.0D);
    }

    private boolean hasCheeseMakerWorking(ServerLevel level) {
        BlockState state = this.getBlockState();
        if (!state.is(GCBlocks.LUNAR_CHEESE_PRESS)) {
            return false;
        }

        BlockPos frontPos = this.worldPosition.relative(state.getValue(LunarCheesePressBlock.FACING));
        Vec3 frontTarget = Vec3.atBottomCenterOf(frontPos);
        AABB searchBox = new AABB(frontPos).inflate(0.7D, 1.5D, 0.7D);
        for (MoonVillagerEntity villager : level.getEntitiesOfClass(MoonVillagerEntity.class, searchBox)) {
            if (villager.isBaby() || villager.isSleeping() || villager.isTrading() || villager.isPassenger() || villager.isLeashed()) {
                continue;
            }

            if (villager.getVillagerData().getProfession() != GCVillagerProfessions.LUNAR_CHEESE_MAKER) {
                continue;
            }

            Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
            if (jobSite.isEmpty()) {
                continue;
            }

            GlobalPos jobSitePos = jobSite.get();
            if (!jobSitePos.dimension().equals(level.dimension()) || !jobSitePos.pos().equals(this.worldPosition)) {
                continue;
            }

            if (villager.position().distanceToSqr(frontTarget) > 0.85D) {
                continue;
            }

            return true;
        }

        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        if (this.animationStartTick != Long.MIN_VALUE) {
            tag.putLong(ANIMATION_START_TICK_TAG, this.animationStartTick);
        }
        if (this.workCycleStartTick != Long.MIN_VALUE) {
            tag.putLong(WORK_CYCLE_START_TICK_TAG, this.workCycleStartTick);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        if (tag.contains(ANIMATION_START_TICK_TAG)) {
            this.animationStartTick = tag.getLong(ANIMATION_START_TICK_TAG);
        } else {
            this.animationStartTick = Long.MIN_VALUE;
        }

        if (tag.contains(WORK_CYCLE_START_TICK_TAG)) {
            this.workCycleStartTick = tag.getLong(WORK_CYCLE_START_TICK_TAG);
        } else {
            this.workCycleStartTick = Long.MIN_VALUE;
        }
    }

    private boolean isWorkCycleActive(long gameTime) {
        return this.workCycleStartTick != Long.MIN_VALUE && gameTime - this.workCycleStartTick < WORK_CYCLE_DURATION;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        return this.saveWithoutMetadata(lookup);
    }
}