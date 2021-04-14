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

package dev.galacticraft.mod.client.render.block.entity;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.block.entity.BasicSolarPanelBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class BasicSolarPanelBlockEntityRenderer extends BlockEntityRenderer<BasicSolarPanelBlockEntity> {
    private static final Identifier solarPanelTexture = new Identifier(Constants.MOD_ID, "textures/model/solar_panel_basic.png");

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
        this.panelMain.addCuboid(-23F, -0.5F, -23F, 46, 1, 46);
        this.panelMain.setPivot(0F, 0F, 0F);
        this.panelMain.setTextureSize(256, 128);
        this.panelMain.mirror = true;
        this.setRotation(this.panelMain);
        this.sideHorizontal0 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal0.addCuboid(-24F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal0.setPivot(0F, 0F, 0F);
        this.sideHorizontal0.setTextureSize(256, 128);
        this.sideHorizontal0.mirror = true;
        this.setRotation(this.sideHorizontal0);
        this.sideVertical0 = new ModelPart(256, 128, 94, 48);
        this.sideVertical0.addCuboid(-24F, -1.1F, 23F, 48, 1, 1);
        this.sideVertical0.setPivot(0F, 0F, 0F);
        this.sideVertical0.setTextureSize(256, 128);
        this.sideVertical0.mirror = true;
        this.setRotation(this.sideVertical0);
        this.sideVertical2 = new ModelPart(256, 128, 94, 48);
        this.sideVertical2.addCuboid(-24F, -1.1F, -24F, 48, 1, 1);
        this.sideVertical2.setPivot(0F, 0F, 0F);
        this.sideVertical2.setTextureSize(256, 128);
        this.sideVertical2.mirror = true;
        this.setRotation(this.sideVertical2);
        this.sideVertical1 = new ModelPart(256, 128, 94, 48);
        this.sideVertical1.addCuboid(-24F, -1.1F, -0.5F, 48, 1, 1);
        this.sideVertical1.setPivot(0F, 0F, 0F);
        this.sideVertical1.setTextureSize(256, 128);
        this.sideVertical1.mirror = true;
        this.setRotation(this.sideVertical1);
        this.sideHorizontal1 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal1.addCuboid(-9F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal1.setPivot(0F, 0F, 0F);
        this.sideHorizontal1.setTextureSize(256, 128);
        this.sideHorizontal1.mirror = true;
        this.setRotation(this.sideHorizontal1);
        this.sideHorizontal3 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal3.addCuboid(23F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal3.setPivot(0F, 0F, 0F);
        this.sideHorizontal3.setTextureSize(256, 128);
        this.sideHorizontal3.mirror = true;
        this.setRotation(this.sideHorizontal3);
        this.sideHorizontal2 = new ModelPart(256, 128, 0, 48);
        this.sideHorizontal2.addCuboid(8F, -1.111F, -23F, 1, 1, 46);
        this.sideHorizontal2.setPivot(0F, 0F, 0F);
        this.sideHorizontal2.setTextureSize(256, 128);
        this.sideHorizontal2.mirror = true;
        this.setRotation(this.sideHorizontal2);
        this.pole = new ModelPart(256, 128, 94, 50);
        this.pole.addCuboid(-1.5F, 0.0F, -1.5F, 3, 24, 3);
        this.pole.setPivot(0F, 0F, 0F);
        this.pole.setTextureSize(256, 128);
        this.pole.mirror = true;
        this.setRotation(this.pole);
    }

    @Override
    public void render(BasicSolarPanelBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        light = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().offset(Direction.UP, 3));

        matrices.push();
        matrices.translate(0.5F, 1.0F, 0.5F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(BasicSolarPanelBlockEntityRenderer.solarPanelTexture);
        this.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(solarPanelTexture)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    public void renderPanel(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
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
        model.setPivot((float) 0.0, (float) 0.0, (float) 0.0);
    }

    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.pole.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.translate(0.0F, 1.5F, 0.0F);

        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));

        this.renderPanel(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
