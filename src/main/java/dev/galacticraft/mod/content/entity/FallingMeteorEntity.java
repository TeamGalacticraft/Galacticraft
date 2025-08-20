package dev.galacticraft.mod.content.entity;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.environment.FallenMeteorBlock;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
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
import net.minecraft.world.phys.Vec3;

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
                this.getSize() / 3 * 2,
                true,
                Level.ExplosionInteraction.BLOCK);

        BlockPos meteorPos = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.containing(position));
        if (this.level().isEmptyBlock(meteorPos)) {
            this.level().setBlock(
                    meteorPos,
                    GCBlocks.FALLEN_METEOR.defaultBlockState().setValue(FallenMeteorBlock.HEAT, 5),
                    Block.UPDATE_ALL);
        }

        this.discard();
    }

    private void onImpact(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            entity.hurt(this.damageSources().source(GCDamageTypes.METEOR_STRIKE), 20.0f);
        }

        this.fallTo(hitResult.getLocation());
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

    private void spawnParticles() {
        if (this.level().isClientSide && this.isAlive()) {
            this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY() + 1.0 + this.random.nextDouble(), this.getZ(),
                    0.0, 0.0, 0.0);
            this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX() + this.random.nextDouble() * 0.5, this.getY() + 1.0 + this.random.nextDouble() * 0.5, this.getZ(),
                    0.0, 0.0, 0.0);
            this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY() + 1.0 + this.random.nextDouble(), this.getZ() + this.random.nextDouble(),
                    0.0, 0.0, 0.0);
            this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX() - this.random.nextDouble() * 0.5, this.getY() + 1.0 + this.random.nextDouble(), this.getZ(),
                    0.0, 0.0, 0.0);
            this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getX(), this.getY() + 1.0 + this.random.nextDouble(), this.getZ() - this.random.nextDouble(),
                    0.0, 0.0, 0.0);
        }
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
