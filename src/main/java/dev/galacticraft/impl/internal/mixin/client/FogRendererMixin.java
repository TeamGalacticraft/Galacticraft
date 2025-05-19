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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.dimension.GalacticDimensionEffects;
import dev.galacticraft.mod.client.render.dimension.OverworldRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @WrapOperation(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 setDimensionColor(Vec3 cameraPos, CubicSampler.Vec3Fetcher fetcher, Operation<Vec3> original, Camera activeRenderInfo, float partialTicks, ClientLevel level) {
        if (level.effects() instanceof GalacticDimensionEffects gcEffects) {
            return gcEffects.getFogColor(level, partialTicks, cameraPos, fetcher);
        } else if (4.0F * cameraPos.y() > Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
            return OverworldRenderer.getFogColor(level, partialTicks, cameraPos.scale(4.0).add(2.0, 2.0, 2.0));
        }
        return original.call(cameraPos, fetcher);
    }
}
