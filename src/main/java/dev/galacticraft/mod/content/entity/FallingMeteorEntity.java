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

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.mod.content.block.environment.FallenMeteorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import static dev.galacticraft.mod.content.GCBlocks.FALLEN_METEOR;
import static dev.galacticraft.mod.content.entity.damage.GCDamageTypes.METEOR_STRIKE;

public class FallingMeteorEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(FallingMeteorEntity.class, EntityDataSerializers.INT);

    public FallingMeteorEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public int getSize() {
        return this.entityData.get(DATA_SIZE);
    }

    public void setSize(int size) {
        this.entityData.set(DATA_SIZE, size);
    }

    private void fallTo(Position position) {
        this.level().explode(
                this,
                position.x(), position.y(), position.z(),
                this.getSize() * 2 / 3,
                true,
                Level.ExplosionInteraction.BLOCK);

        BlockPos meteorPos = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(position));
        if (this.level().isEmptyBlock(meteorPos)) {
            this.level().setBlock(
                    meteorPos,
                    FALLEN_METEOR.defaultBlockState().setValue(FallenMeteorBlock.HEAT, 5),
                    Block.UPDATE_ALL);
        }

        this.discard();
    }

    private void onImpact(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult) {
            entityHitResult.getEntity().hurt(this.damageSources().source(METEOR_STRIKE), 20.0f);
        }

        this.fallTo(hitResult.getLocation());
    }

    private void spawnSmokeParticle(double offsetX, double offsetY, double offsetZ) {
        this.level().addParticle(
                ParticleTypes.SMOKE,
                this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                0.0, 0.0, 0.0);
    }

    private void spawnParticles() {
        if (this.level().isClientSide && this.isAlive()) {
            this.spawnSmokeParticle(0.0, 1.0 + this.random.nextDouble(), 0.0);
            this.spawnSmokeParticle(this.random.nextDouble() * 0.5, 1.0 + this.random.nextDouble() * 0.5, 0.0);
            this.spawnSmokeParticle(0.0, 1.0 + this.random.nextDouble(), this.random.nextDouble());
            this.spawnSmokeParticle(-this.random.nextDouble() * 0.5, 1.0 + this.random.nextDouble(), 0.0);
            this.spawnSmokeParticle(0.0, 1.0 + this.random.nextDouble(), -this.random.nextDouble());
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, Entity::canBeHitByProjectile);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onImpact(hitResult);
            } else if (this.onGround()) {
                this.fallTo(this.position());
            }
        }

        this.setRot(this.getXRot() + 2.0f, this.getYRot() + 2.0f);

        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.handlePortal();

        this.spawnParticles();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder compositeStateBuilder) {
        compositeStateBuilder.define(DATA_SIZE, 6);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }
}
