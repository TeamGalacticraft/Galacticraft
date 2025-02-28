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

import dev.galacticraft.mod.client.render.dimension.OverworldRenderer;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    @Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", remap = false, ordinal = 1))
    private static void gc$setupColor(Camera camera, float partialTicks, ClientLevel clientLevel, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof RocketEntity && player.getY() > 200) {
            Vec3 vec3 = OverworldRenderer.getFogColor(clientLevel, camera, partialTicks);
            fogRed = (float) vec3.x();
            fogGreen = (float) vec3.y();
            fogBlue = (float) vec3.z();
        }
    }

    @Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;getPriorityFogFunction(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/FogRenderer$MobEffectFogFunction;"))
    private static void gc$setupFluidFog(Camera camera, float partialTicks, ClientLevel clientLevel, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player.isEyeInFluid(GCTags.OIL)) {
            fogRed = 0.0F;
            fogGreen = 0.0F;
            fogBlue = 0.0F;
        } else if (player.isEyeInFluid(GCTags.FUEL)) {
            fogRed = 0.72F;
            fogGreen = 0.58F;
            fogBlue = 0.18F;
        } else if (player.isEyeInFluid(GCTags.SULFURIC_ACID)) {
            fogRed = 0.41F;
            fogGreen = 0.78F;
            fogBlue = 0.25F;
        }
    }

    @ModifyArg(method = "setupFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V", remap = false), index = 0)
    private static float gc$setShaderFogStart(float start, @Local FogType fogType) {
        if (fogType != FogType.NONE) {
            Player player = Minecraft.getInstance().player;
            if (player.isEyeInFluid(GCTags.OIL)) {
                return -8.0F;
            } else if (player.isEyeInFluid(GCTags.FUEL)) {
                return -8.0F;
            } else if (player.isEyeInFluid(GCTags.SULFURIC_ACID)) {
                return -8.0F;
            }
        }
        return start;
    }

    @ModifyArg(method = "setupFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V", remap = false), index = 0)
    private static float gc$setShaderFogEnd(float end, @Local FogType fogType) {
        if (fogType != FogType.NONE) {
            Player player = Minecraft.getInstance().player;
            if (player.isEyeInFluid(GCTags.OIL)) {
                return 8.0F;
            } else if (player.isEyeInFluid(GCTags.FUEL)) {
                return 12.0F;
            } else if (player.isEyeInFluid(GCTags.SULFURIC_ACID)) {
                return 12.0F;
            }
        }
        return end;
    }
}
