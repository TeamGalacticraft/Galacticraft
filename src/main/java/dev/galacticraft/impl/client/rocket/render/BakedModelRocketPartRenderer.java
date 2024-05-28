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

package dev.galacticraft.impl.client.rocket.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.galacticraft.api.entity.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.client.model.GCBakedModel;
import dev.galacticraft.mod.client.model.GCSheets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlas;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public record BakedModelRocketPartRenderer(Supplier<GCBakedModel> model,
                                           Supplier<RenderType> layer) implements RocketPartRenderer {

    public BakedModelRocketPartRenderer(Supplier<GCBakedModel> model) {
        this(model, () -> /*RenderType.entityCutoutNoCull(model.get().getParticleIcon().contents().name(), true)*/null);
    }

    @Override
    public void renderGUI(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y-5, 10); //todo: add GUI model transforms to models
        pose.translate(8, 16, 8);
//        model.get().getTransforms().getTransform(ItemDisplayContext.GUI).apply(false, pose);
        pose.mulPose(Axis.XN.rotationDegrees(35));
        pose.mulPose(Axis.YP.rotationDegrees(225));
        pose.mulPose(Axis.ZP.rotationDegrees(180));
        pose.scale(10, 10, 10);

        PoseStack.Pose entry = pose.last();
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//        RenderSystem.setShaderTexture(0, this.model.get().getParticleIcon().atlasLocation());
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        if (model.get().usesBlockLight()) {
            Lighting.setupFor3DItems();
//        } else {
//            Lighting.setupFor3DItems();
//        }

//        MultiBufferSource.BufferSource entityVertexConsumers = Minecraft.getInstance().renderBuffers().bufferSource();
//        VertexConsumer itemGlintConsumer = entityVertexConsumers.getBuffer(Sheets.cutoutBlockSheet());
//        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
//        this.model.get().render(pose);
//        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(entry, itemGlintConsumer, null, this.model.get(), 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
//        entityVertexConsumers.endBatch();

//        if (model.get().usesBlockLight()) {
            Lighting.setupFor3DItems();
//        }
        pose.popPose();
    }

    @Override
    public void render(ClientLevel world, PoseStack matrices, Rocket rocket, MultiBufferSource vertices, float partialTick, int light, int overlay) {
        RenderSystem.setShaderColor(rocket.red() / 255.0f, rocket.green() / 255.0f, rocket.blue() / 255.0f, rocket.alpha() / 255.0f);
//        matrices.translate(0.5D, 0.5D, 0.5D);
//        PoseStack.Pose entry = matrices.last();
//        VertexConsumer vertexConsumer = vertices.getBuffer(layer.get());
//        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);

        VertexConsumer consumer = vertices.getBuffer(GCSheets.obj(GCSheets.OBJ_ATLAS));
        this.model.get().render(matrices, null, consumer, light, overlay);
//        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(entry, vertexConsumer, null, this.model.get(), 1, 1, 1, light, overlay);
    }
}
