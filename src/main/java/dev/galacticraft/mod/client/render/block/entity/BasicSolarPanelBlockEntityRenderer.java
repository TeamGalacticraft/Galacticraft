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

package dev.galacticraft.mod.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.BasicSolarPanelBlockEntity;
import dev.galacticraft.mod.client.render.entity.model.GalacticraftEntityModelLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class BasicSolarPanelBlockEntityRenderer implements BlockEntityRenderer<BasicSolarPanelBlockEntity> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, "textures/model/solar_panel.png");
    private final ModelPart panel;
    private final ModelPart pole;

    public BasicSolarPanelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        ModelPart root = context.getLayerModelPart(GalacticraftEntityModelLayer.SOLAR_PANEL);
        this.panel = root.getChild(Constant.ModelPartName.SOLAR_PANEL_PANEL);
        this.pole = root.getChild(Constant.ModelPartName.SOLAR_PANEL_POLE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(Constant.ModelPartName.SOLAR_PANEL_POLE, ModelPartBuilder.create().uv(94, 50).cuboid(-1.5F, 0.0F, -1.5F, 3, 24, 3, Dilation.NONE), ModelTransform.NONE);
        ModelPartData panel = modelPartData.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL, ModelPartBuilder.create().uv(0, 0).cuboid(-23F, -0.5F, -23F, 46, 1, 46, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_HORIZONTAL_1, ModelPartBuilder.create().uv(0, 48).cuboid(-24F, -1.111F, -23F, 1, 1, 46, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_HORIZONTAL_2, ModelPartBuilder.create().uv(0, 48).cuboid(-9F, -1.111F, -23F, 1, 1, 46, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_HORIZONTAL_3, ModelPartBuilder.create().uv(0, 48).cuboid(8F, -1.111F, -23F, 1, 1, 46, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_HORIZONTAL_4, ModelPartBuilder.create().uv(0, 48).cuboid(23F, -1.111F, -23F, 1, 1, 46, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_VERTICAL_1, ModelPartBuilder.create().uv(94, 48).cuboid(-24F, -1.1F, 23F, 48, 1, 1, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_VERTICAL_2, ModelPartBuilder.create().uv(94, 48).cuboid(-24F, -1.1F, -24F, 48, 1, 1, Dilation.NONE), ModelTransform.NONE);
        panel.addChild(Constant.ModelPartName.SOLAR_PANEL_PANEL_VERTICAL_3, ModelPartBuilder.create().uv(94, 48).cuboid(-24F, -1.1F, -0.5F, 48, 1, 1, Dilation.NONE), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 256, 128);
    }

    @Override
    public void render(BasicSolarPanelBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        light = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().offset(Direction.UP, 3));

        matrices.push();
        matrices.translate(0.5F, 1.0F, 0.5F);
        RenderSystem.setShaderTexture(0, BasicSolarPanelBlockEntityRenderer.TEXTURE);
        this.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.pole.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.translate(0.0F, 1.5F, 0.0F);

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));

        this.panel.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
