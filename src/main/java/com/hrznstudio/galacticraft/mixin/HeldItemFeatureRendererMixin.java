/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(HeldItemFeatureRenderer.class)
public class HeldItemFeatureRendererMixin {

    @Inject(method = "method_4192", at = @At("TAIL"))
    private void method_4192(LivingEntity livingEntity_1, ItemStack itemStack_1, ModelTransformation.Type modelTransformation$Type_1, Arm absoluteHand_1, CallbackInfo ci) {
        if (!itemStack_1.isEmpty()) { //&& GalacticraftItems.isRocketItem(itemStack_1.getItem())
            System.out.println("nice that thing rendered");
            GlStateManager.pushMatrix();
            if (livingEntity_1.isInSneakingPose()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            boolean boolean_1 = absoluteHand_1 == Arm.LEFT;
            GlStateManager.translatef((float) (boolean_1 ? -1 : 1) / 16.0F, 50F, -0.625F);
            //MinecraftClient.getInstance().getFirstPersonRenderer().renderItemFromSide(livingEntity_1, itemStack_1, modelTransformation$Type_1, boolean_1);
            GlStateManager.popMatrix();
            return; // leave this, it stops normal item rendering if its a rocket.
        }
    }
}
