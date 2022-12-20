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

package dev.galacticraft.impl.client.rocket.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.entity.rocket.render.RocketPartRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public record BakedModelRocketPartRenderer(Supplier<BakedModel> model,
                                           Supplier<RenderType> layer) implements RocketPartRenderer {
    private static final Direction[] DIRECTIONS_AND_NULL = new Direction[]{null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public BakedModelRocketPartRenderer(Supplier<BakedModel> model) {
        this(model, () -> RenderType.entityTranslucent(model.get().getParticleIcon().getName(), true));
    }

    @Override
    public void renderGUI(ClientLevel world, PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        matrices.translate(0, 0, 150);
        matrices.translate(8, 8, 8);
        model.get().getTransforms().getTransform(ItemTransforms.TransformType.GUI).apply(false, matrices);
        matrices.mulPose(Vector3f.XN.rotationDegrees(35));
        matrices.mulPose(Vector3f.YP.rotationDegrees(225));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180));
        matrices.scale(16, 16, 16);

        PoseStack.Pose entry = matrices.last();
        List<BakedQuad> quads = new LinkedList<>();
        for (Direction direction : DIRECTIONS_AND_NULL) {
            quads.addAll(this.model.get().getQuads(null, direction, world.random));
        }
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, this.model.get().getParticleIcon().atlas().getId());
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (model.get().usesBlockLight()) {
            Lighting.setupFor3DItems();
        } else {
            Lighting.setupFor3DItems();
        }

        if (!quads.isEmpty()) {
            MultiBufferSource.BufferSource entityVertexConsumers = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer itemGlintConsumer = entityVertexConsumers.getBuffer(Sheets.cutoutBlockSheet());
            for (BakedQuad quad : quads) {
                itemGlintConsumer.putBulkData(
                        entry,
                        quad,
                        1,
                        1,
                        1,
                        15728880,
                        OverlayTexture.NO_OVERLAY
                );
            }
            entityVertexConsumers.endBatch();
        }

        if (model.get().usesBlockLight()) {
            Lighting.setupFor3DItems();
        }
    }

    @Override
    public void render(ClientLevel world, PoseStack matrices, Rocket rocket, MultiBufferSource vertices, float delta, int light) {
        RenderSystem.setShaderColor((((rocket.getColor() >> 16) & 0xFF) / 255f), (((rocket.getColor() >> 8) & 0xFF) / 255f), ((rocket.getColor() & 0xFF) / 255f), (((rocket.getColor() >> 24) & 0xFF) / 255f));
        matrices.translate(0.5D, 0.5D, 0.5D);
        PoseStack.Pose entry = matrices.last();
        VertexConsumer vertexConsumer = vertices.getBuffer(layer.get());
        for (Direction direction : DIRECTIONS_AND_NULL) {
            for (BakedQuad quad : this.model.get().getQuads(null, direction, world.random)) {
                vertexConsumer.putBulkData(
                        entry,
                        quad,
                        1,
                        1,
                        1,
                        light,
                        OverlayTexture.NO_OVERLAY
                );
            }
        }
    }
}
