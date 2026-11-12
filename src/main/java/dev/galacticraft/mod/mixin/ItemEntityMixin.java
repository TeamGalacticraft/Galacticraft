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
import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.mod.accessor.EntityFluidAccessor;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract void setItem(ItemStack stack);

    @Inject(at = @At("RETURN"), method = "tick")
    void coolMeteorChunk(CallbackInfo ci) {
        ItemStack stack = this.getItem();
        if (stack.is(GCItems.HOT_THROWABLE_METEOR_CHUNK)) {
            int ttc = stack.getComponents().getOrDefault(GCDataComponents.TICKS_UNTIL_COOL, 0);
            if (ttc == 0) {
                this.setItem(new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount()));
            } else {
                stack.set(GCDataComponents.TICKS_UNTIL_COOL, ttc - 1);
            }

            if (this.isInWater()) {
                this.setItem(new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount()));
                this.playEntityOnFireExtinguishedSound();
            }
        }
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;isInWater()Z"))
    private boolean gc$checkThinFluid(boolean inWater) {
        return gc$getInThinFluid(inWater);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D", ordinal = 0))
    private double gc$useThinFluidHeight(double waterHeight) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        if (fluidType != null) {
            return this.getFluidHeight(fluidType);
        }
        return waterHeight;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;isInLava()Z"))
    private boolean gc$checkThickFluid(boolean inLava) {
        return gc$getInThickFluid(inLava);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D", ordinal = 1))
    private double gc$useThickFluidHeight(double lavaHeight) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        if (fluidType != null) {
            return this.getFluidHeight(fluidType);
        }
        return lavaHeight;
    }

    @Unique
    private TagKey<Fluid> gc$getInFluidType() {
        return ((EntityFluidAccessor)  this).gc$getInFluidType();
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
