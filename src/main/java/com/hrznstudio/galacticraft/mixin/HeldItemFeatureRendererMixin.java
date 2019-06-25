package com.hrznstudio.galacticraft.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AbsoluteHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Joe van der Zwet (https://joezwet.me)
 */
@Mixin(HeldItemFeatureRenderer.class)
public  class HeldItemFeatureRendererMixin {

    @Inject(method = "method_4192", at = @At("TAIL"))
    private void method_4192(LivingEntity livingEntity_1, ItemStack itemStack_1, ModelTransformation.Type modelTransformation$Type_1, AbsoluteHand absoluteHand_1, CallbackInfo ci) {
        if (!itemStack_1.isEmpty()) { //&& GalacticraftItems.isRocketItem(itemStack_1.getItem())
            System.out.println("nice that thing rendered");
            GlStateManager.pushMatrix();
            if (livingEntity_1.isInSneakingPose()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            boolean boolean_1 = absoluteHand_1 == AbsoluteHand.LEFT;
            GlStateManager.translatef((float)(boolean_1 ? -1 : 1) / 16.0F, 50F, -0.625F);
            //MinecraftClient.getInstance().getFirstPersonRenderer().renderItemFromSide(livingEntity_1, itemStack_1, modelTransformation$Type_1, boolean_1);
            GlStateManager.popMatrix();
            return; // leave this, it stops normal item rendering if its a rocket.
        }
    }
}
