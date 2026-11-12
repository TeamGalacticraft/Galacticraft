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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.galacticraft.mod.accessor.EntityFluidAccessor;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FloatGoal.class)
abstract class FloatGoalMixin {
    @Shadow
    @Final
    private Mob mob;

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;isInWater()Z"))
    private boolean gc$checkThinFluid(boolean inWater) {
        return gc$getInThinFluid(inWater);
    }

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getFluidHeight(Lnet/minecraft/tags/TagKey;)D"))
    private double gc$useCustomFluidHeight(double waterHeight) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        if (fluidType != null) {
            return mob.getFluidHeight(fluidType);
        }
        return waterHeight;
    }

    @ModifyExpressionValue(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;isInLava()Z"))
    private boolean gc$checkThickFluid(boolean inLava) {
        return gc$getInThickFluid(inLava);
    }

    @Unique
    private TagKey<Fluid> gc$getInFluidType() {
        return ((EntityFluidAccessor)  mob).gc$getInFluidType();
    }

    @Unique
    private boolean gc$getInThinFluid(boolean inWater) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        if (fluidType != null) {
            return inWater || GCFluidTags.THIN.contains(gc$getInFluidType());
        }
        return inWater;
    }

    @Unique
    private boolean gc$getInThickFluid(boolean inLava) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        if (fluidType != null) {
            return inLava || GCFluidTags.THICK.contains(gc$getInFluidType());
        }
        return inLava;
    }
}
