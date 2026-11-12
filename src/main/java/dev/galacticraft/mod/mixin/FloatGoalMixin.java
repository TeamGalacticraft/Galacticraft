package dev.galacticraft.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.galacticraft.mod.accessor.EntityFluidAccessor;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
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
