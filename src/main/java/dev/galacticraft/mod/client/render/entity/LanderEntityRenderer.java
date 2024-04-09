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

package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.LanderModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.entity.orbital.lander.LanderEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LanderEntityRenderer extends EntityRenderer<LanderEntity> {
    protected final LanderModel model;

    public LanderEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 2F;
        this.model = new LanderModel(context.bakeLayer(GCEntityModelLayer.LANDER));
    }

    @Override
    public ResourceLocation getTextureLocation(LanderEntity entity) {
        return Constant.id(Constant.EntityTexture.LANDER);
    }

    @Override
    public void render(LanderEntity lander, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5, 0);
        final float pitch = lander.getViewXRot(partialTicks);

        float hurtTime = lander.getHurtTime() - partialTicks;
        float damage = lander.getDamage() - partialTicks;

        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(hurtTime) * 0.2F * hurtTime * damage / 25.0F));
        }

        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - entityYaw));
        poseStack.mulPose(Axis.ZN.rotationDegrees(pitch));
        this.model.renderToBuffer(poseStack, multiBufferSource.getBuffer(this.model.renderType(getTextureLocation(lander))), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
