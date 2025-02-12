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

package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.client.render.entity.model.ParachestModel;
import dev.galacticraft.mod.content.entity.ParachestEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ParachestRenderer extends EntityRenderer<ParachestEntity> {

    public static final ResourceLocation TEXTURE = Constant.id("textures/block/parachest.png");

    public ParachestModel parachute;
    private ModelPart chest;

    public ParachestRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1F;
        this.chest = context.bakeLayer(ModelLayers.CHEST);
        this.parachute = new ParachestModel(context.bakeLayer(GCEntityModelLayer.PARACHEST));
    }

    @Override
    public void render(ParachestEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.parachute.setupAnim(entity, 0F, 0F, 0F, 0F, 0F);
        poseStack.translate(-0.5F, 0F, -0.5F);
        this.chest.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.translate(-0.0625F, 0F, 0.375F);
        this.parachute.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public ResourceLocation getTextureLocation(ParachestEntity entity) {
        return Constant.id("textures/model/parachute/" + entity.getColor().getName() + ".png");
    }
}
