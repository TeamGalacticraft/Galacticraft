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

package dev.galacticraft.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.mod.accessor.EntityAccessor;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.events.GCEventHandlers;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.misc.footprint.FootprintType;
import dev.galacticraft.mod.network.s2c.FootprintExactRemovedPacket;
import dev.galacticraft.mod.tag.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {
    private @Unique double distanceSinceLastStep;
    private @Unique int lastStep = -1;
    private @Unique int timeInAcid = 0;
    private @Unique boolean petWasWalking;
    private @Unique boolean petHasRecentStopPose;
    private @Unique double petLastStopX;
    private @Unique double petLastStopY;
    private @Unique double petLastStopZ;
    private @Unique float petLastMoveRotation = Float.NaN;

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    private float yRot;

    @Shadow
    private float xRot;

    @Shadow
    private Level level;

//    @Inject(method = "findDimensionEntryPoint", at = @At("HEAD"), cancellable = true)
//    private void getTeleportTargetGC(ServerLevel destination, CallbackInfoReturnable<PortalInfo> cir) {
//        if (destination.dimension().equals(GCDimensions.MOON) || this.level.dimension().equals(GCDimensions.MOON)) { //TODO lander/parachute stuff
//            BlockPos pos = destination.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, destination.getSharedSpawnPos());
//            cir.setReturnValue(new PortalInfo(new Vec3((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D), this.getDeltaMovement(), this.yRot, this.xRot));
//        }
//    }

    @Shadow
    public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double d);

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    private Vec3 position;

    @Shadow
    protected UUID uuid;

    @Shadow
    public abstract boolean isAlwaysTicking();

    @Shadow
    private int id;

    @Shadow
    public abstract DamageSources damageSources();

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract float getBbWidth();

    @Shadow
    public abstract @Nullable Entity getVehicle();

    @Shadow
    public abstract float getYRot();

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    @Final
    protected RandomSource random;

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    @Final
    protected abstract void discard();

    @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing", at = @At("TAIL"))
    private void checkWaterStateGC(CallbackInfo ci) {
        Player player = level.getPlayerByUUID(uuid);
        boolean invulnerable = player != null && player.getAbilities().invulnerable;
        if (this.updateFluidHeightAndDoFluidPushing(GCFluidTags.OIL, 0.0028d) || this.updateFluidHeightAndDoFluidPushing(GCFluidTags.FUEL, 0.0028d)) {
            if (this.isOnFire()) {
                level.explode(level.getEntity(id), position.x, position.y, position.z, 0f, Level.ExplosionInteraction.NONE);
                if (!invulnerable) {
                    this.hurt(this.damageSources().source(GCDamageTypes.OIL_BOOM), 20.0F);
                }
            }
        }

        if (this.updateFluidHeightAndDoFluidPushing(GCFluidTags.SULFURIC_ACID, 0.0028d)) {
            // The entity enters an acid fluid, this entity needs to take damage
            if (!invulnerable && !this.getType().is(GCEntityTypeTags.IMMUNE_TO_ACID)) {
                boolean damage = this.timeInAcid >= 30;
                boolean playSound = true;
                if (damage && ((Object) this instanceof Projectile || this.getType().is(GCEntityTypeTags.SENSITIVE_TO_ACID))) {
                    this.discard();
                    return;
                }

                if ((Object) this instanceof ItemEntity itemEntity) {
                    ItemStack itemStack = itemEntity.getItem();
                    if (itemStack.is(GCItemTags.ACID_RESISTANT)) {
                        damage = false;
                        playSound = false;
                    } else if (damage) {
                        damage = !GCEventHandlers.sulfuricAcidTransformItem(itemEntity, itemStack);
                    }
                } else {
                    damage = true;
                }

                if (damage) {
                    this.hurt(this.damageSources().source(GCDamageTypes.SULFURIC_ACID), 2.0F);
                }
                if (playSound && this.timeInAcid % 5 == 0) {
                    this.sulfuricAcidEffects();
                }
            }
            ++this.timeInAcid;
        } else {
            this.timeInAcid = 0;
        }
    }

    @Unique
    private void sulfuricAcidEffects() {
        this.playSound(SoundEvents.LAVA_EXTINGUISH, 0.7F, 1.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.4F);
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.WHITE_SMOKE, true, this.getX() + level.random.nextDouble() - 0.5, Mth.ceil(this.getY()), this.getZ() + level.random.nextDouble() - 0.5, 0.0D, 0.0D, 0.0D);
        }
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void galacticraft$tickPetStopFootprints(CallbackInfo ci) {
        FootprintType footprintType = this.galacticraft$getFootprintType();
        if (!footprintType.isPet() || this.level.isClientSide() || this.getVehicle() != null) {
            return;
        }

        Holder<DimensionType> dimensionType = this.level.dimensionTypeRegistration();
        if (!dimensionType.is(GCDimensionTypeTags.FOOTPRINTS_DIMENSIONS)) {
            this.petWasWalking = false;
            this.petHasRecentStopPose = false;
            return;
        }

        double speed = this.getDeltaMovement().horizontalDistance();
        boolean navigationWalking = (Object) this instanceof Mob mob && !mob.getNavigation().isDone();
        boolean movingNow = speed > footprintType.stopSpeedThreshold() || navigationWalking;

        if (movingNow) {
            this.petWasWalking = true;
            if (speed > footprintType.stopSpeedThreshold()) {
                this.petLastMoveRotation = this.getYRot() * Mth.DEG_TO_RAD;
            }
            return;
        }

        if (!this.petWasWalking) {
            return;
        }

        float rotation = Float.isNaN(this.petLastMoveRotation) ? this.getYRot() * Mth.DEG_TO_RAD : this.petLastMoveRotation;
        Vector3d stopCenter = new Vector3d(this.getX(), Math.floor(this.getY()), this.getZ());
        this.galacticraft$removeNearbyPetFootprints(stopCenter, footprintType);
        this.galacticraft$placePetStopPose(dimensionType, footprintType, rotation);

        this.petLastStopX = stopCenter.x;
        this.petLastStopY = stopCenter.y;
        this.petLastStopZ = stopCenter.z;
        this.petHasRecentStopPose = true;
        this.petWasWalking = false;
        this.galacticraft$setDistanceSinceLastStep(0);
    }

    // GC 4 ticks footprints on the client and server, however we will just do it on the server
    @Inject(method = "move", at = @At("HEAD"))
    private void tickFootprints(MoverType type, Vec3 motion, CallbackInfo ci) {
        if (!this.getType().is(GCEntityTypeTags.HAS_FOOTPRINTS)) {
            return;
        } else if ((Object) this instanceof Player player && player.getAbilities().flying) {
            return;
        } else if (this.getVehicle() != null) {
            return;
        }
        // The entity has footprints, is not flying and is not riding anything

        Holder<DimensionType> dimensionType = this.level.dimensionTypeRegistration();
        FootprintType footprintType = this.galacticraft$getFootprintType();
        if (footprintType.isPet() && this.level.isClientSide()) {
            return;
        }

        if (!dimensionType.is(GCDimensionTypeTags.FOOTPRINTS_DIMENSIONS)) {
            return;
        }

        if (!footprintType.isPet()) {
            double motionSqrd = motion.horizontalDistanceSqr();
            if (motionSqrd <= 0.001D) {
                return;
            }

            if (this.galacticraft$getDistanceSinceLastStep() > 0.35D) {
                float rotation = this.getYRot() * Mth.DEG_TO_RAD;
                this.galacticraft$placeFootprint(dimensionType, footprintType, rotation, this.galacticraft$getLastStep() * 0.25D, 0.0D);
                this.galacticraft$swapLastStep();
                this.galacticraft$setDistanceSinceLastStep(0);
            } else {
                this.galacticraft$setDistanceSinceLastStep(this.galacticraft$getDistanceSinceLastStep() + motionSqrd);
            }
            return;
        }

        double motionAmount = motion.horizontalDistance();
        if (motionAmount <= 0.001D) {
            return;
        }

        this.petLastMoveRotation = this.getYRot() * Mth.DEG_TO_RAD;
        double nextStepDistance = this.galacticraft$getDistanceSinceLastStep() + motionAmount;
        if (this.petHasRecentStopPose && this.galacticraft$isNearRecentStopPose(footprintType)) {
            this.galacticraft$setDistanceSinceLastStep(nextStepDistance);
            return;
        }
        this.petHasRecentStopPose = false;

        if (nextStepDistance > footprintType.stepDistance()) {
            float rotation = this.petLastMoveRotation;
            int side = this.galacticraft$getLastStep();
            this.galacticraft$placeFootprint(
                    dimensionType,
                    footprintType,
                    rotation,
                    side * footprintType.movingSideOffset(),
                    footprintType.movingForwardOffset(side)
            );
            this.galacticraft$swapLastStep();
            this.galacticraft$setDistanceSinceLastStep(0);
        } else {
            this.galacticraft$setDistanceSinceLastStep(nextStepDistance);
        }
    }

    @Unique
    private FootprintType galacticraft$getFootprintType() {
        EntityType<?> entityType = this.getType();
        if (entityType == EntityType.WOLF) {
            return FootprintType.DOG;
        } else if (entityType == EntityType.CAT) {
            return FootprintType.CAT;
        }
        return FootprintType.HUMAN;
    }

    @Unique
    private boolean galacticraft$isNearRecentStopPose(FootprintType footprintType) {
        double dx = this.getX() - this.petLastStopX;
        double dy = Math.floor(this.getY()) - this.petLastStopY;
        double dz = this.getZ() - this.petLastStopZ;
        return dx * dx + dy * dy + dz * dz < footprintType.restartClearanceSq();
    }

    @Unique
    private void galacticraft$placePetStopPose(Holder<DimensionType> dimensionType, FootprintType footprintType, float rotation) {
        this.galacticraft$placeFootprint(dimensionType, footprintType, rotation, footprintType.stopFrontRightSideOffset(), footprintType.stopFrontRightForwardOffset());
        this.galacticraft$placeFootprint(dimensionType, footprintType, rotation, footprintType.stopFrontLeftSideOffset(), footprintType.stopFrontLeftForwardOffset());
        this.galacticraft$placeFootprint(dimensionType, footprintType, rotation, footprintType.stopBackRightSideOffset(), footprintType.stopBackRightForwardOffset());
        this.galacticraft$placeFootprint(dimensionType, footprintType, rotation, footprintType.stopBackLeftSideOffset(), footprintType.stopBackLeftForwardOffset());
    }

    @Unique
    private void galacticraft$removeNearbyPetFootprints(Vector3d center, FootprintType footprintType) {
        List<Long> removedChunks = new java.util.ArrayList<>();
        List<Footprint> removedFootprints = new java.util.ArrayList<>();
        Long2ObjectMap<List<Footprint>> footprintMap = this.level.galacticraft$getFootprintManager().getFootprints();
        var chunkIterator = footprintMap.long2ObjectEntrySet().iterator();

        while (chunkIterator.hasNext()) {
            Long2ObjectMap.Entry<List<Footprint>> entry = chunkIterator.next();
            List<Footprint> footprints = entry.getValue();
            var footprintIterator = footprints.iterator();
            while (footprintIterator.hasNext()) {
                Footprint footprint = footprintIterator.next();
                if (footprint.type != footprintType || !footprint.owner.equals(this.getUUID())) {
                    continue;
                }

                double dx = footprint.position.x - center.x;
                double dy = footprint.position.y - center.y;
                double dz = footprint.position.z - center.z;
                if (dx * dx + dy * dy + dz * dz >= footprintType.cleanupRadiusSq()) {
                    continue;
                }

                removedChunks.add(entry.getLongKey());
                removedFootprints.add(footprint);
                footprintIterator.remove();
            }

            if (footprints.isEmpty()) {
                chunkIterator.remove();
            }
        }

        if (!(this.level instanceof ServerLevel serverLevel) || removedFootprints.isEmpty()) {
            return;
        }

        for (int i = 0; i < removedFootprints.size(); i++) {
            long chunk = removedChunks.get(i);
            Footprint footprint = removedFootprints.get(i);
            PlayerLookup.tracking(serverLevel, new ChunkPos(chunk)).forEach(player ->
                    ServerPlayNetworking.send(player, new FootprintExactRemovedPacket(chunk, footprint))
            );
        }
    }

    @Unique
    private void galacticraft$placeFootprint(Holder<DimensionType> dimensionType, FootprintType footprintType, float rotation, double sideOffset, double forwardOffset) {
        Vector3d pos = new Vector3d(
                this.getX() + sideOffset * Mth.cos(rotation) - forwardOffset * Mth.sin(rotation),
                Math.floor(this.getY()),
                this.getZ() + sideOffset * Mth.sin(rotation) + forwardOffset * Mth.cos(rotation)
        );
        pos = Footprint.getFootprintPosition(footprintType, rotation - Mth.PI, pos);

        BlockPos blockPos = new BlockPos(Mth.floor(pos.x), Mth.floor(pos.y - 0.05D), Mth.floor(pos.z));
        BlockState state = this.level.getBlockState(blockPos);
        if (!state.is(GCBlockTags.FOOTPRINTS)) {
            return;
        }

        long chunkKey = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z));
        short age = (short) (this.level.getGameTime() % 20);
        this.level.galacticraft$getFootprintManager().addFootprint(this.level, chunkKey, new Footprint(dimensionType.unwrapKey().get().location(), pos, rotation, age, this.getUUID(), footprintType));
    }

    @Override
    public double galacticraft$getDistanceSinceLastStep() {
        return this.distanceSinceLastStep;
    }

    @Override
    public void galacticraft$setDistanceSinceLastStep(double distanceSinceLastStep) {
        this.distanceSinceLastStep = distanceSinceLastStep;
    }

    @Override
    public int galacticraft$getLastStep() {
        return this.lastStep;
    }

    @Override
    public void galacticraft$swapLastStep() {
        this.lastStep = -this.lastStep;
    }

    @WrapOperation(method = "checkBelowWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onBelowWorld()V"))
    private void galacticraft$onBelowWorld(Entity entity, Operation<Void> original) {
        if (!entity.getType().is(GCEntityTypeTags.CAN_REENTER_ATMOSPHERE)) {
            original.call(entity);
            return;
        }

        Holder<CelestialBody<?, ?>> holder = entity.level().galacticraft$getCelestialBody();
        CelestialBody fromBody = holder != null ? holder.value() : null;
        if (fromBody != null && fromBody.isSatellite() && fromBody.parent().isPresent()) {
            Registry<CelestialBody<?, ?>> celestialBodies = entity.level().registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY);
            CelestialBody body = fromBody.parentValue(celestialBodies);
            if (body.type() instanceof Landable landable) {
                if (entity.level() instanceof ServerLevel level) {
                    ((CelestialTeleporter) landable.teleporter(body.config()).value()).onEnterAtmosphere(level.getServer().getLevel(landable.world(body.config())), entity, body, fromBody);
                }
                return;
            }
        }
        original.call(entity);
    }
}
