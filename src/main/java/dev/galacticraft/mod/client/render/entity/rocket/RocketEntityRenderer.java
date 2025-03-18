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

package dev.galacticraft.mod.client.render.entity.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {
    public RocketEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RocketEntity entity, float yaw, float partialTick, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, partialTick, matrices, vertexConsumers, light);
        matrices.pushPose();
        Minecraft client = Minecraft.getInstance();
        float rotationOffset = 1.6F;
        matrices.translate(0, rotationOffset, 0);
        if (entity.getLaunchStage() == LaunchStage.IGNITED) {
            matrices.translate((entity.level().random.nextDouble() - 0.5D) * 0.1D, 0, (entity.level().random.nextDouble() - 0.5D) * 0.1D);
        }
        float pitch = entity.getViewXRot(partialTick);
        float roll = -entity.getViewZRot(partialTick);
        // TODO: Fix roll rotation when the rocket is pointing downwards
        matrices.mulPose(Axis.YN.rotationDegrees(180.0F + entity.getViewYRot(partialTick) - roll));
        matrices.mulPose(Axis.XN.rotationDegrees(pitch * Mth.cos(roll * Mth.DEG_TO_RAD)));
        matrices.mulPose(Axis.ZN.rotationDegrees(pitch * Mth.sin(roll * Mth.DEG_TO_RAD)));
        matrices.translate(0, -rotationOffset, 0);

        float wobbleTicks = (float) entity.getHurtTime() - partialTick;
        float wobbleStrength = entity.getDamage() - partialTick;

        if (wobbleStrength < 0.0F) {
            wobbleStrength = 0.0F;
        }

        if (wobbleTicks > 0.0F) {
            matrices.mulPose(Axis.XP.rotationDegrees(Mth.sin(wobbleTicks) * wobbleTicks * wobbleStrength / 10.0F * (float) entity.getHurtDir()));
        }

//        RenderSystem.setShaderTexture(0, getTextureLocation(entity));
        matrices.translate(0.0F, 0.4375F, 0.0F);

        Holder<? extends RocketPart<?, ?>> part = entity.engine();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.unwrapKey().get()).render(client.level, matrices, entity, vertexConsumers, partialTick, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        matrices.translate(0.0F, 0.5F, 0.0F);

        part = entity.booster();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.unwrapKey().get()).render(client.level, matrices, entity, vertexConsumers, partialTick, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        part = entity.fin();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.unwrapKey().get()).render(client.level, matrices, entity, vertexConsumers, partialTick, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        matrices.translate(0.0F, 1.0F, 0.0F);

        part = entity.body();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.unwrapKey().get()).render(client.level, matrices, entity, vertexConsumers, partialTick, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        matrices.translate(0.0F, 1.75F, 0.0F);

        part = entity.cone();
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part.unwrapKey().get()).render(client.level, matrices, entity, vertexConsumers, partialTick, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }

        matrices.popPose();
    }

    @Override
    public boolean shouldRender(RocketEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true; //maybe fix this later
    }

    @Override
    public ResourceLocation getTextureLocation(RocketEntity var1) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
