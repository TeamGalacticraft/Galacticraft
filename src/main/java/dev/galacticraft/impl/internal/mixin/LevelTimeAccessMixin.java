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

package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelTimeAccess.class)
public interface LevelTimeAccessMixin extends LevelTimeAccess {
    @Shadow
    long dayTime();

    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true)
    private void getGalacticTimeOfDay(float partialTicks, CallbackInfoReturnable<Float> cir) {
        // Might be worth it to make our own dimension type system?
        var dimensionTypeRegistry = registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        if (this instanceof Level level) {
            var holder = CelestialBody.getByDimension(level);
            if (holder.isPresent() && dimensionTypeRegistry.getHolder(dimensionTypeRegistry.getId(dimensionType())).map(reference -> reference.is(GCTags.SPACE)).orElse(false)) {
                long worldTime = this.dayTime();
                long dayLength = holder.get().dayLength();
                int j = (int) (worldTime % dayLength);
                float f1 = (j + partialTicks) / dayLength - 0.25F;

                if (f1 < 0.0F) {
                    ++f1;
                }

                if (f1 > 1.0F) {
                    --f1;
                }

                float f2 = f1;
                f1 = 0.5F - (float) Math.cos(f1 * Math.PI) / 2.0F;
                cir.setReturnValue(f2 + (f1 - f2) / 3.0F);
            }
        }
    }
}
