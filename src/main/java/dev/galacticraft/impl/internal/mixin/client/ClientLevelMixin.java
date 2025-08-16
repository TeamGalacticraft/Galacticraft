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

package dev.galacticraft.impl.internal.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.galacticraft.mod.api.dimension.GalacticDimensionEffects;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {
    @Shadow
    @Final
    private DimensionSpecialEffects effects;

    @Shadow
    @Final
    private Minecraft minecraft;

    ClientLevelMixin() {
        super(null, null, null, null, null, false, false, 0, 0);
    }

    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private Vec3 gc$getDimensionSkyColor(Vec3 original, Vec3 pos, float partialTick) {
        if (effects instanceof GalacticDimensionEffects gcEffects) {
            return gcEffects.getSkyColor((ClientLevel) (Object) this, partialTick);
        } else if (pos.y() > Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT && this.dimension() == Level.OVERWORLD) {
            float heightOffset = ((float) (pos.y()) - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 200.0F;
            heightOffset = Math.max(1.0F - 0.75F * Mth.sqrt(heightOffset), 0.0F);
            return original.scale(heightOffset);
        }
        return original;
    }

    @ModifyReturnValue(method = "getSkyFlashTime", at = @At("RETURN"))
    private int gc$hideLightningFlash(int original) {
        Player player = this.minecraft.player;
        if (player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > Constant.CLOUD_LIMIT) {
            return 0;
        }
        return original;
    }
}
