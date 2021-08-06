/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.api.client.rocket.render.RocketPartRendererRegistry;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.entity.RocketEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntityRenderer extends EntityRenderer<RocketEntity> {
    public RocketEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(RocketEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        MinecraftClient client = MinecraftClient.getInstance();
        matrices.translate(0.0D, 1.75, 0.0D);
        if (entity.getStage() == LaunchStage.IGNITED) {
            matrices.translate((entity.world.random.nextDouble() - 0.5D) * 0.1D, 0, (entity.world.random.nextDouble() - 0.5D) * 0.1D);
        }
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw(tickDelta)));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(entity.getPitch(tickDelta)));

        float wobbleTicks = (float) entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_TICKS) - tickDelta;
        float wobbleStrength = entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_STRENGTH) - tickDelta;

        if (wobbleStrength < 0.0F) {
            wobbleStrength = 0.0F;
        }

        if (wobbleTicks > 0.0F) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(wobbleTicks) * wobbleTicks * wobbleStrength / 10.0F * (float) entity.getDataTracker().get(RocketEntity.DAMAGE_WOBBLE_SIDE)));
        }

        client.getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        matrices.translate(0.0D, -1.75D, 0.0D);

        Identifier part = entity.getPartForType(RocketPartType.BOTTOM);
        if (part != null) {
            matrices.push();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.world, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.pop();
        }

        matrices.translate(0.0D, 0.5, 0.0D);

        part = entity.getPartForType(RocketPartType.BOOSTER);
        if (part != null) {
            matrices.push();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.world, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.pop();
        }

        part = entity.getPartForType(RocketPartType.FIN);
        if (part != null) {
            matrices.push();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.world, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.pop();
        }

        matrices.translate(0.0D, 1.0D, 0.0D);

        part = entity.getPartForType(RocketPartType.BODY);
        if (part != null) {
            matrices.push();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.world, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.pop();
        }

        matrices.translate(0.0D, 1.75, 0.0D);

        part = entity.getPartForType(RocketPartType.CONE);
        if (part != null) {
            matrices.push();
            RocketPartRendererRegistry.INSTANCE.getRenderer(part).render(client.world, matrices, entity, vertexConsumers, tickDelta, light);
            matrices.pop();
        }

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