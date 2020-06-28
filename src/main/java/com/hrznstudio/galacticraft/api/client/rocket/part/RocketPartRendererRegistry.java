package com.hrznstudio.galacticraft.api.client.rocket.part;

import com.hrznstudio.galacticraft.api.rocket.part.RocketPart;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class RocketPartRendererRegistry {
    private static final Map<RocketPart, Renderer> RENDERERS = new HashMap<>();
    public static final Renderer DEFAULT = (stack, part, entity, vertexConsumers, light, tickDelta) -> MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer().render(stack.peek(), vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout()), part.getRenderState(), MinecraftClient.getInstance().getBlockRenderManager().getModel(part.getRenderState()), entity.getColor()[0], entity.getColor()[1], entity.getColor()[2], light, OverlayTexture.DEFAULT_UV);

    public static void register(RocketPart part, Renderer renderer) {
        RENDERERS.put(part, renderer);
    }

    public static void render(MatrixStack stack, RocketPart part, RocketEntity entity, VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
        RENDERERS.getOrDefault(part, DEFAULT).render(stack, part, entity, vertexConsumers, light, tickDelta);
    }

    @FunctionalInterface
    public interface Renderer {
        void render(MatrixStack stack, RocketPart part, RocketEntity entity, VertexConsumerProvider vertexConsumers, int light, float tickDelta);
    }
}
