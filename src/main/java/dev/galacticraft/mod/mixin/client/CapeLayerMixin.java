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

package dev.galacticraft.mod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.content.entity.orbital.AdvancedVehicle;
import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {
    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 2))
    private void galacticraft$restrictCapeWithOxygenTanks(Args args, @Local AbstractClientPlayer player) {
        Container inv = player.galacticraft$getOxygenTanks();
        boolean tank1 = inv.getItem(0).is(GCItemTags.OXYGEN_TANKS);
        boolean tank2 = inv.getItem(1).is(GCItemTags.OXYGEN_TANKS);
        if (tank1 && tank2) {
            args.set(0, 0.0F);
        } else if (tank2 && !tank1) {
            args.set(1, 0.0F);
        } else if (tank1 && !tank2) {
            args.set(2, 0.0F);
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;sin(F)F", ordinal = 1))
    private float galacticraft$reduceFlappingFrequency(float angle, @Local AbstractClientPlayer player) {
        Holder<CelestialBody<?, ?>> holder = player.level().galacticraft$getCelestialBody();
        if (holder != null && holder.value().gravity() < 0.8) {
            Container inv = player.galacticraft$getOxygenTanks();
            if (inv.getItem(0).is(GCItemTags.OXYGEN_TANKS) || inv.getItem(1).is(GCItemTags.OXYGEN_TANKS)) {
                angle /= 6.0F;
            }
        }
        return angle;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 0))
    private float galacticraft$limitBillowing(float angle, @Local AbstractClientPlayer player) {
        if (player.getVehicle() instanceof AdvancedVehicle) {
            return 0.0F;
        }
        Container inv = player.galacticraft$getOxygenTanks();
        if (inv.getItem(0).is(GCItemTags.OXYGEN_TANKS) || inv.getItem(1).is(GCItemTags.OXYGEN_TANKS)) {
            angle = Mth.clamp(0.5F * (angle - 4.0F), 0.0F, 18.0F);
        }
        return angle;
    }
}
