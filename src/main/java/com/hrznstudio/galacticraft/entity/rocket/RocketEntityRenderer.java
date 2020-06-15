/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.entity.rocket;

import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {

    public RocketEntityRenderer(EntityRenderDispatcher entityRenderDispatcher_1) {
        super(entityRenderDispatcher_1);
    }

    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        MinecraftClient client = MinecraftClient.getInstance();
        matrices.translate(0.0D, 2.1875, 0.0D);
        if (entity.getStage() == LaunchStage.IGNITED) {
            matrices.translate((entity.world.random.nextDouble() - 0.5D) * 0.1D, 0, (entity.world.random.nextDouble() - 0.5D) * 0.1D);
        }
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw - 180.0F, entity.yaw - 180.0F)) * -1.0F));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerpAngleDegrees(tickDelta, entity.prevPitch, entity.pitch)));

        float float_7 = (float) entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_TICKS) - tickDelta;
        float float_8 = entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_STRENGTH) - tickDelta;

        if (float_8 < 0.0F) {
            float_8 = 0.0F;
        }

        if (float_7 > 0.0F) {
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(float_7) * float_7 * float_8 / 10.0F * (float) entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_SIDE)));
        }

        float red = entity.getColor()[0] / 255F;
        float green = entity.getColor()[1] / 255F;
        float blue = entity.getColor()[2] / 255F;
        float alpha = entity.getColor()[3] / 255F;

//        RenderSystem.color4f(red, green, blue, alpha);

//        RenderSystem.pushTextureAttributes();

        client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        matrices.translate(0.0D, -1.75D, 0.0D);

        matrices.push();
        entity.getPartForType(RocketPartType.BOTTOM).preRender(matrices, entity, vertexConsumers, tickDelta);
        client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()), entity.getPartForType(RocketPartType.BOTTOM).getRenderState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.BOTTOM).getRenderState()), red, green, blue, light, OverlayTexture.DEFAULT_UV);
        entity.getPartForType(RocketPartType.BOTTOM).postRender(matrices, entity, vertexConsumers, tickDelta);
        matrices.pop();

        matrices.translate(0.0D, 0.5, 0.0D);

        matrices.push();
        entity.getPartForType(RocketPartType.BOOSTER).preRender(matrices, entity, vertexConsumers, tickDelta);
        client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()), entity.getPartForType(RocketPartType.BOOSTER).getRenderState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.BOOSTER).getRenderState()), red, green, blue, light, OverlayTexture.DEFAULT_UV);
        entity.getPartForType(RocketPartType.BOOSTER).postRender(matrices, entity, vertexConsumers, tickDelta);
        matrices.pop();

        matrices.push();
        entity.getPartForType(RocketPartType.FIN).preRender(matrices, entity, vertexConsumers, tickDelta);
        client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()), entity.getPartForType(RocketPartType.FIN).getRenderState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.FIN).getRenderState()), red, green, blue, light, OverlayTexture.DEFAULT_UV);
        entity.getPartForType(RocketPartType.FIN).postRender(matrices, entity, vertexConsumers, tickDelta);

        matrices.pop();

        matrices.translate(0.0D, 1.0D, 0.0D);

        matrices.push();
        entity.getPartForType(RocketPartType.BODY).preRender(matrices, entity, vertexConsumers, tickDelta);
        client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()), entity.getPartForType(RocketPartType.BODY).getRenderState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.BODY).getRenderState()), red, green, blue, light, OverlayTexture.DEFAULT_UV);
        entity.getPartForType(RocketPartType.BODY).postRender(matrices, entity, vertexConsumers, tickDelta);
        matrices.pop();

        matrices.translate(0.0D, 1.75, 0.0D);

        entity.getPartForType(RocketPartType.CONE).preRender(matrices, entity, vertexConsumers, tickDelta);
        client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()), entity.getPartForType(RocketPartType.CONE).getRenderState(), client.getBlockRenderManager().getModel(entity.getPartForType(RocketPartType.CONE).getRenderState()), red, green, blue, light, OverlayTexture.DEFAULT_UV);
        entity.getPartForType(RocketPartType.CONE).postRender(matrices, entity, vertexConsumers, tickDelta);

        matrices.pop();
    }

    @Override
    public boolean shouldRender(RocketEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true; //maybe fix this later
    }

    @Override
    public Identifier getTexture(RocketEntity var1) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}