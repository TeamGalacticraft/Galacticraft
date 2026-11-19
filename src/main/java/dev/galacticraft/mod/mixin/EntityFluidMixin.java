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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.galacticraft.mod.accessor.EntityAccessor;
import dev.galacticraft.mod.accessor.EntityFluidAccessor;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.events.GCEventHandlers;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.tag.GCEntityTypeTags;
import dev.galacticraft.mod.tag.GCFluidTags;
import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityFluidMixin implements EntityAccessor, EntityFluidAccessor {
    private @Unique int timeInAcid = 0;
    private @Unique boolean wasTouchingFluid;
    private @Unique @Nullable TagKey<Fluid> inFluidType;


    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    private Level level;

    @Shadow
    public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double d);

    @Shadow
    public boolean wasOnFire;

    @Shadow
    private Vec3 position;

    @Shadow
    protected UUID uuid;

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

    @Shadow
    public abstract void resetFallDistance();

    @Shadow
    public abstract void clearFire();

    @Shadow
    protected boolean firstTick;

    @Shadow
    abstract public LivingEntity getControllingPassenger();

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    protected abstract SoundEvent getSwimSplashSound();

    @Shadow
    protected abstract SoundEvent getSwimHighSpeedSplashSound();

    @Shadow
    public abstract void gameEvent(Holder<GameEvent> event);

    @Override
    @Nullable
    public TagKey<Fluid> gc$getInFluidType() {
        return inFluidType;
    }

    @Override
    public void gc$setInFluidType(@Nullable TagKey<Fluid> fluidType) {
        inFluidType = fluidType;
    }

    @Override
    public boolean gc$getWasTouchingGCFluid() {
        return wasTouchingFluid;
    }

    @Override
    public void gc$setWasTouchingGCFluid(boolean value) {
        wasTouchingFluid = value;
    }

    @ModifyReturnValue(method = "isInLiquid", at = @At(value = "RETURN"))
    private boolean gc$isInAnyFluid(boolean original) {
        return original || this.gc$getWasTouchingGCFluid();
    }

    @ModifyReturnValue(method = "updateInWaterStateAndDoFluidPushing", at = @At("RETURN"))
    private boolean gc$checkFluidState(boolean original) {
        Player player = level.getPlayerByUUID(uuid);
        boolean invulnerable = player != null && player.getAbilities().invulnerable;
        boolean bl = false;

        if (Objects.requireNonNull(this.gc$getInFluidState()).is(GCFluidTags.EXPLOSIVE)) {
            doExplosion(invulnerable);
        }
        if (this.updateFluidHeightAndDoFluidPushing(GCFluidTags.OIL, 0.0028)) {
            doFluidEffects(0.34F, GCParticleTypes.OIL_BUBBLE, GCParticleTypes.OIL_SPLASH);
            gc$setInFluidType(GCFluidTags.OIL);
            bl = true;
        } else if (this.updateFluidHeightAndDoFluidPushing(GCFluidTags.FUEL, 0.007)) {
            doFluidEffects(0.54F, GCParticleTypes.FUEL_BUBBLE, GCParticleTypes.FUEL_SPLASH);
            gc$setInFluidType(GCFluidTags.FUEL);
            bl = true;
        } else if (this.updateFluidHeightAndDoFluidPushing(GCFluidTags.SULFURIC_ACID, 0.014)) {
            doFluidEffects(1.0F, GCParticleTypes.ACID_BUBBLE, GCParticleTypes.ACID_SPLASH);
            sulfuricAcidEffects(invulnerable);
            gc$setInFluidType(GCFluidTags.SULFURIC_ACID);
            bl = true;
        } else {
            gc$setWasTouchingGCFluid(false);
            gc$setInFluidType(null);
            this.timeInAcid = 0;
        }
        return original || bl;
    }

    @Unique
    private void doFluidEffects(float pitch, ParticleOptions bubble, ParticleOptions splash) {
        if (!this.gc$getWasTouchingGCFluid() && !this.firstTick) {
            this.doGCSplashEffect(pitch, bubble, splash);
        }
        this.resetFallDistance();
        gc$setWasTouchingGCFluid(true);
    }

    @Unique
    protected void doGCSplashEffect(float pitch, ParticleOptions bubble, ParticleOptions splash) {
        Entity entity = (Entity) Objects.requireNonNullElse(this.getControllingPassenger(), this);
        float f = entity == (Object) this ? 0.2F : 0.9F;
        Vec3 vec3 = entity.getDeltaMovement();
        float g = Math.min(1.0F, (float)Math.sqrt(vec3.x * vec3.x * 0.2D + vec3.y * vec3.y + vec3.z * vec3.z * 0.2D) * f);
        if (g < 0.25F) {
            this.playSound(this.getSwimSplashSound(), g, pitch + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        } else {
            this.playSound(this.getSwimHighSpeedSplashSound(), g, pitch + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        float h = (float) Mth.floor(this.getY());

        for(int i = 0; (float)i < 1.0F + this.dimensions.width() * 20.0F; ++i) {
            double d = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width();
            double e = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width();
            this.level().addParticle(bubble, this.getX() + d, h + 1.0D, this.getZ() + e, vec3.x, vec3.y - this.random.nextDouble() * 0.2D, vec3.z);
        }
        for(int i = 0; (float)i < 1.0F + this.dimensions.width() * 20.0F; ++i) {
            double d = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width();
            double e = (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.dimensions.width();
            this.level().addParticle(splash, this.getX() + d, h + 1.0D, this.getZ() + e, vec3.x, vec3.y, vec3.z);
        }
        this.gameEvent(GameEvent.SPLASH);
    }

    // If a flaming entity enters an explosive substance, this entity should explode
    @Unique
    private void doExplosion(boolean invulnerable) {
        if (this.wasOnFire) {
            level.explode(level.getEntity(id), position.x, position.y, position.z, 0F, Level.ExplosionInteraction.NONE);
            if (!invulnerable) {
                this.hurt(this.damageSources().source(GCDamageTypes.OIL_BOOM), 20.0F);
            }
        }
    }

    // The entity enters an acid fluid, this entity needs to take damage
    @Unique
    private void sulfuricAcidEffects(boolean invulnerable) {
        this.clearFire();
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
                this.playSound(SoundEvents.LAVA_EXTINGUISH, 0.7F, 1.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.4F);
                for (int i = 0; i < 4; i++) {
                    level.addParticle(ParticleTypes.WHITE_SMOKE, true, this.getX() + level.random.nextDouble() - 0.5, Mth.ceil(this.getY()), this.getZ() + level.random.nextDouble() - 0.5, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        ++this.timeInAcid;
    }
}
