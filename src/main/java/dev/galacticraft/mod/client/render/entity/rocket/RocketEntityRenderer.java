/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.galacticraft.api.entity.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.content.entity.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {
    public RocketEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pushPose();
        Minecraft client = Minecraft.getInstance();
        matrices.translate(-0.5D, 1.6, -0.5D);
        if (entity.getStage() == LaunchStage.IGNITED) {
            matrices.translate((entity.level.random.nextDouble() - 0.5D) * 0.1D, 0, (entity.level.random.nextDouble() - 0.5D) * 0.1D);
        }
        matrices.translate(0.5D, 0, 0.5D);
        matrices.mulPose(Vector3f.YP.rotationDegrees(entity.getViewYRot(tickDelta)));
        matrices.mulPose(Vector3f.XP.rotationDegrees(entity.getViewXRot(tickDelta)));
        matrices.translate(-0.5D, 0, -0.5D);

        float wobbleTicks = (float) entity.getEntityData().get(RocketEntity.DAMAGE_WOBBLE_TICKS) - tickDelta;
        float wobbleStrength = entity.getEntityData().get(RocketEntity.DAMAGE_WOBBLE_STRENGTH) - tickDelta;

        if (wobbleStrength < 0.0F) {
            wobbleStrength = 0.0F;
        }

        if (wobbleTicks > 0.0F) {
            matrices.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(wobbleTicks) * wobbleTicks * wobbleStrength / 10.0F * (float) entity.getEntityData().get(RocketEntity.DAMAGE_WOBBLE_SIDE)));
        }

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        matrices.translate(0.0D, -1.75D, 0.0D);

        ResourceLocation part = entity.getPartForType(RocketPartType.BOTTOM);
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.level, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.popPose();
        }

        matrices.translate(0.0D, 0.5, 0.0D);

        part = entity.getPartForType(RocketPartType.BOOSTER);
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.level, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.popPose();
        }

        part = entity.getPartForType(RocketPartType.FIN);
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.level, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.popPose();
        }

        matrices.translate(0.0D, 1.0D, 0.0D);

        part = entity.getPartForType(RocketPartType.BODY);
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.level, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.popPose();
        }

        matrices.translate(0.0D, 1.75, 0.0D);

        part = entity.getPartForType(RocketPartType.CONE);
        if (part != null) {
            matrices.pushPose();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.level, matrices, entity, vertexConsumers, tickDelta, light);
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
