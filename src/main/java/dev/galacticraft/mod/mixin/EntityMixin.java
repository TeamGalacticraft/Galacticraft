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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.accessor.EntityAccessor;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {
    private @Unique double distanceSinceLastStep;
    private @Unique int lastStep;
    private @Unique int lastAcidSoundPlayTick = -20;

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    private float yRot;

    @Shadow
    private float xRot;

    @Shadow
    private Level level;

    @Shadow
    public int tickCount;

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
    public abstract boolean isInvulnerable();

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource source);

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

    @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing", at = @At("TAIL"))
    private void checkWaterStateGC(CallbackInfo ci) {
        Player player = level.getPlayerByUUID(uuid);
        boolean isCreative = (player != null) && (player.isCreative() || player.isSpectator());
        if (this.updateFluidHeightAndDoFluidPushing(GCTags.OIL, 0.0028d) || this.updateFluidHeightAndDoFluidPushing(GCTags.FUEL, 0.0028d)) {
            if (this.isOnFire()) {
                level.explode(level.getEntity(id), position.x, position.y, position.z, 0f, Level.ExplosionInteraction.NONE);
                if (!isCreative) {
                    this.hurt(new DamageSource(this.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(GCDamageTypes.OIL_BOOM)), 20.0f);
                }
            }
        } else if (this.updateFluidHeightAndDoFluidPushing(GCTags.SULFURIC_ACID, 0.0028d)) {
            // The entity enters an acid fluid, this entity needs to take damage
            if (!isCreative) {
                this.hurt(new DamageSource(this.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(GCDamageTypes.SULFURIC_ACID)), 2.0f);

                if (this.shouldPlaySulfuricAcidSound()) {
                    this.playSulfuricAcidSound();
                    this.addSulfuricAcidParticles();
                }
            }
        }
    }

    @Unique
    private boolean shouldPlaySulfuricAcidSound() {
        return this.tickCount >= this.lastAcidSoundPlayTick + 5;
    }

    @Unique
    private void playSulfuricAcidSound() {
        this.playSound(SoundEvents.LAVA_EXTINGUISH, 0.7F, 1.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.4F);
        this.lastAcidSoundPlayTick = this.tickCount;
    }

    @Unique
    private void addSulfuricAcidParticles() {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.WHITE_SMOKE, true, this.getX() + level.random.nextDouble() - 0.5, Mth.ceil(this.getY()), this.getZ() + level.random.nextDouble() - 0.5, 0.0D, 0.0D, 0.0D);
        }
    }

    // GC 4 ticks footprints on the client and server, however we will just do it on the server
    @Inject(method = "move", at = @At("HEAD"))
    private void tickFootprints(MoverType type, Vec3 motion, CallbackInfo ci) {
        if (!getType().is(GCTags.HAS_FOOTPRINTS))
            return;
        double motionSqrd = Mth.lengthSquared(motion.x, motion.z);

        // If the player is on the moon, not airbourne and not riding anything
        boolean isFlying = false;
        if ((Object) this instanceof Player player)
            isFlying = player.getAbilities().flying;
        if (motionSqrd > 0.001 && this.level.dimensionTypeRegistration().is(GCTags.FOOTPRINTS_DIMENSIONS) && getVehicle() == null && !isFlying) {
            int iPosX = Mth.floor(getX());
            int iPosY = Mth.floor(getY() - 0.05);
            int iPosZ = Mth.floor(getZ());
            BlockPos pos1 = new BlockPos(iPosX, iPosY, iPosZ);
            BlockState state = this.level.getBlockState(pos1);

            // If the block below is the moon block
            if (state.is(GCTags.FOOTPRINTS)) {
                // If it has been long enough since the last step
                if (galacticraft$getDistanceSinceLastStep() > 0.35) {
                    Vector3d pos = new Vector3d(getX(), getY(), getZ());
                    // Set the footprint position to the block below and add
                    // random number to stop z-fighting
                    pos.y = Mth.floor(getY()) + random.nextFloat() / 100.0F;

                    // Adjust footprint to left or right depending on step
                    // count
                    switch (galacticraft$getLastStep()) {
                        case 0:
                            pos.add(new Vector3d(Math.sin(Math.toRadians(-getYRot() + 90)) * 0.25, 0, Math.cos(Math.toRadians(-getYRot() + 90)) * 0.25));
                            break;
                        case 1:
                            pos.add(new Vector3d(Math.sin(Math.toRadians(-getYRot() - 90)) * 0.25, 0, Math.cos(Math.toRadians(-getYRot() - 90)) * 0.25));
                            break;
                    }

                    pos = Footprint.getFootprintPosition(level, getYRot() - 180, pos, position());

                    long chunkKey = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z));
                    level.galacticraft$getFootprintManager().addFootprint(chunkKey, new Footprint(level.dimensionTypeRegistration().unwrapKey().get().location(), pos, getYRot(), getUUID()));

                    // Increment and cap step counter at 1
                    galacticraft$setLastStep((galacticraft$getLastStep() + 1) % 2);
                    galacticraft$setDistanceSinceLastStep(0);
                } else {
                    galacticraft$setDistanceSinceLastStep(galacticraft$getDistanceSinceLastStep() + motionSqrd);
                }
            }
        }
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
    public void galacticraft$setLastStep(int lastStep) {
        this.lastStep = lastStep;
    }
}
