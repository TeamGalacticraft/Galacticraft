/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.render.block.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.entity.BasicSolarPanelBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class BasicSolarPanelBlockEntityRenderer extends BlockEntityRenderer<BasicSolarPanelBlockEntity> {

    private static final ResourceLocation solarPanelTexture = new ResourceLocation(Constants.MOD_ID, "textures/model/solar_panel_basic.png");

    private final ModelPart panelMain;
    private final ModelPart sideHorizontal0;
    private final ModelPart sideVertical0;
    private final ModelPart sideVertical2;
    private final ModelPart sideVertical1;
    private final ModelPart sideHorizontal1;
    private final ModelPart sideHorizontal3;
    private final ModelPart sideHorizontal2;
    private final ModelPart pole;

    public BasicSolarPanelBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.panelMain = new ModelPart(256, 128, 0, 0);
        this.panelMain.addBox(-23F, -0.5F, -23F, 46, 1, 46);
        this.panelMain.setPos(0F, 0F, 0F);
        this.panelMain.setTexSize(256, 128);
        this.panelMain.mirror = true;
        this.setRotation(this.panelMain);
        this.sideHorizontal0 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal0.addBox(-24F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal0.setPos(0F, 0F, 0F);
        this.sideHorizontal0.setTexSize(256, 128);
        this.sideHorizontal0.mirror = true;
        this.setRotation(this.sideHorizontal0);
        this.sideVertical0 = new ModelPart(256, 128, 94, 48);
        this.sideVertical0.addBox(-24F, -1.1F, 23F, 48, 1, 1);
        this.sideVertical0.setPos(0F, 0F, 0F);
        this.sideVertical0.setTexSize(256, 128);
        this.sideVertical0.mirror = true;
        this.setRotation(this.sideVertical0);
        this.sideVertical2 = new ModelPart(256, 128, 94, 48);
        this.sideVertical2.addBox(-24F, -1.1F, -24F, 48, 1, 1);
        this.sideVertical2.setPos(0F, 0F, 0F);
        this.sideVertical2.setTexSize(256, 128);
        this.sideVertical2.mirror = true;
        this.setRotation(this.sideVertical2);
        this.sideVertical1 = new ModelPart(256, 128, 94, 48);
        this.sideVertical1.addBox(-24F, -1.1F, -0.5F, 48, 1, 1);
        this.sideVertical1.setPos(0F, 0F, 0F);
        this.sideVertical1.setTexSize(256, 128);
        this.sideVertical1.mirror = true;
        this.setRotation(this.sideVertical1);
        this.sideHorizontal1 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal1.addBox(-9F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal1.setPos(0F, 0F, 0F);
        this.sideHorizontal1.setTexSize(256, 128);
        this.sideHorizontal1.mirror = true;
        this.setRotation(this.sideHorizontal1);
        this.sideHorizontal3 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal3.addBox(23F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal3.setPos(0F, 0F, 0F);
        this.sideHorizontal3.setTexSize(256, 128);
        this.sideHorizontal3.mirror = true;
        this.setRotation(this.sideHorizontal3);
        this.sideHorizontal2 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal2.addBox(8F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal2.setPos(0F, 0F, 0F);
        this.sideHorizontal2.setTexSize(256, 128);
        this.sideHorizontal2.mirror = true;
        this.setRotation(this.sideHorizontal2);
        this.pole = new ModelPart(256, 128, 94, 50);
        this.pole.addBox(-1.5F, 0.0F, -1.5F, 3, 24, 3);
        this.pole.setPos(0F, 0F, 0F);
        this.pole.setTexSize(256, 128);
        this.pole.mirror = true;
        this.setRotation(this.pole);
    }

    @Override
    public void render(BasicSolarPanelBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        light = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().relative(Direction.UP, 3));

        matrices.pushPose();
        matrices.translate(0.5F, 1.0F, 0.5F);
        Minecraft.getInstance().getTextureManager().bind(BasicSolarPanelBlockEntityRenderer.solarPanelTexture);
        this.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(solarPanelTexture)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.popPose();
    }

    public void renderPanel(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.panelMain.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideHorizontal0.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideVertical0.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideVertical2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideVertical1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideHorizontal1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideHorizontal3.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        this.sideHorizontal2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    private void setRotation(ModelPart model) {
        model.setPos((float) 0.0, (float) 0.0, (float) 0.0);
    }

    public void render(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.pole.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.translate(0.0F, 1.5F, 0.0F);

        matrices.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrices.mulPose(Vector3f.YP.rotationDegrees(-90.0F));

        this.renderPanel(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
