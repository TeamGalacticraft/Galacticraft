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

package dev.galacticraft.mod.accessor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public interface EntityFluidAccessor {
    boolean gc$getWasTouchingGCFluid();
    void gc$setWasTouchingGCFluid(boolean value);

    @Nullable
    TagKey<Fluid> gc$getInFluidType();

    void gc$setInFluidType(@Nullable TagKey<Fluid> fluidType);

    default boolean gc$isInThinFluid(boolean inWater) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        return inWater || (fluidType != null && GCFluidTags.THIN.contains(fluidType));
    }

    default boolean gc$isInThickFluid(boolean inLava) {
        LivingEntity entity = (LivingEntity) (Object) this;
        TagKey<Fluid> fluidType = gc$getInFluidType();
        return inLava || (fluidType != null && GCFluidTags.THICK.contains(fluidType));
    }

    default double gc$getFluidHeight(double originalHeight) {
        TagKey<Fluid> fluidType = gc$getInFluidType();
        Entity entity = (Entity) this;
        if (fluidType != null) {
            return entity.getFluidHeight(fluidType);
        }
        return originalHeight;
    }
}
