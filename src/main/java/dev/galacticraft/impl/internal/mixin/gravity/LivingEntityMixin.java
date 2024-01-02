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

package dev.galacticraft.impl.internal.mixin.gravity;

import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect mobEffect);

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.08))
    private double galacticraft_modifyGravity(double d) {
        AttributeInstance attribute = ((LivingEntity) (Object) this).getAttribute(GcApiEntityAttributes.LOCAL_GRAVITY_LEVEL);
        if (attribute != null && attribute.getValue() > 0) {
            return attribute.getValue() * 0.08d;
        } else {
            return CelestialBody.getByDimension(this.level()).map(celestialBodyType -> celestialBodyType.gravity() * 0.08d).orElse(0.08);
        }
    }

    @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.01))
    private double galacticraft_modifySlowFallingGravity(double d) {
        AttributeInstance attribute = ((LivingEntity) (Object) this).getAttribute(GcApiEntityAttributes.LOCAL_GRAVITY_LEVEL);
        if (attribute != null && attribute.getValue() > 0) {
            return attribute.getValue() * 0.01d;
        } else {
            return CelestialBody.getByDimension(this.level()).map(celestialBodyType -> celestialBodyType.gravity() * 0.01d).orElse(0.01);
        }
    }

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    protected void galacticraft_modifyFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        MobEffectInstance effectInstance = this.getEffect(MobEffects.JUMP);
        AttributeInstance attribute = ((LivingEntity) (Object) this).getAttribute(GcApiEntityAttributes.LOCAL_GRAVITY_LEVEL);
        float ff = effectInstance == null ? 0.0F : (float) (effectInstance.getAmplifier() + 6);
        if (attribute != null && attribute.getValue() > 0) {
            cir.setReturnValue((int) (Mth.ceil((fallDistance * attribute.getValue()) - 3.0F - ff) * damageMultiplier));
        } else {
            CelestialBody.getByDimension(this.level()).ifPresent(celestialBodyType -> cir.setReturnValue((int) (Mth.ceil((fallDistance * celestialBodyType.gravity()) - 3.0F - ff) * damageMultiplier)));
        }
    }
}
