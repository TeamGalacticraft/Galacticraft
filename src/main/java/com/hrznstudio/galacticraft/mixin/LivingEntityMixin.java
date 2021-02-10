/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.api.entity.attribute.GalacticraftEntityAttributes;
import com.hrznstudio.galacticraft.component.GalacticraftComponents;
import com.hrznstudio.galacticraft.entity.damage.GalacticraftDamageSource;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow
    public abstract boolean canBreatheInWater();

    @Shadow
    public abstract boolean isAlive();

    @Shadow
    protected abstract int getNextAirUnderwater(int air);

    @Shadow public abstract void travel(Vec3d movementInput);

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getNextAirOnLand(I)I"))
    private int skipAirCheck_1gc(LivingEntity livingEntity, int air) {
        return air;
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getNextAirUnderwater(I)I"))
    private int skipAirCheck_2gc(LivingEntity livingEntity, int air) {
        return air;
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getAir()I", ordinal = 0))
    private int skipAirCheck_3gc(LivingEntity livingEntity) {
        return 0; //not -20
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tickStatusEffects()V", shift = At.Shift.BEFORE))
    private void doOxygenChecks(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        //noinspection ConstantConditions
        if (this.isAlive() && !(entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.invulnerable)) {
            if (CelestialBodyType.getByDimType(world.getRegistryKey()).isPresent() && !CelestialBodyType.getByDimType(world.getRegistryKey()).get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
                updateAir(entity);
            } else {
                if (this.isSubmergedIn(FluidTags.WATER) && this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).getBlock() != Blocks.BUBBLE_COLUMN) {
                    if (!this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing((LivingEntity) (Object) this) && !isInvulnerableTo(DamageSource.DROWN)) {
                        this.setAir(this.getNextAirUnderwater(this.getAir()));
                    }
                }
            }

            if (this.getAir() == -20) {
                this.setAir(0);

                if (this.isSubmergedIn(FluidTags.WATER) && this.world.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).getBlock() != Blocks.BUBBLE_COLUMN) {
                    if (!this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing((LivingEntity) (Object) this) && !isInvulnerableTo(DamageSource.DROWN)) {
                        Vec3d vec3d = this.getVelocity();

                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float g = this.random.nextFloat() - this.random.nextFloat();
                            float h = this.random.nextFloat() - this.random.nextFloat();
                            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + (double) f, this.getY() + (double) g, this.getZ() + (double) h, vec3d.x, vec3d.y, vec3d.z);
                        }
                    }
                }

                this.damage(GalacticraftDamageSource.SUFFOCATION, 2.0F);
            }
        }
    }

    private void updateAir(LivingEntity entity) {
        //todo check for sealed space
        if (entity instanceof PlayerEntity) {
            InventoryComponent gearInventory = GalacticraftComponents.GEAR_INVENTORY_COMPONENT.get(entity);
            if (gearInventory.getStack(6).getItem() instanceof OxygenTankItem && ((gearInventory.getStack(6).getMaxDamage() - gearInventory.getStack(6).getDamage()) > 0)) {
                gearInventory.getStack(6).setDamage(gearInventory.getStack(6).getDamage() + 1);
                return;
            } else if (gearInventory.getStack(7).getItem() instanceof OxygenTankItem && ((gearInventory.getStack(7).getMaxDamage() - gearInventory.getStack(7).getDamage()) > 0)) {
                gearInventory.getStack(7).setDamage(gearInventory.getStack(7).getDamage() + 1);
                return;
            }
        } else {
            EntityAttributeInstance attribute = entity.getAttributeInstance(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE);
            if (attribute != null && attribute.getValue() >= 0.99D) {
                return;
            }
        }
        entity.setAir(entity.getAir() - 1);
    }
}
