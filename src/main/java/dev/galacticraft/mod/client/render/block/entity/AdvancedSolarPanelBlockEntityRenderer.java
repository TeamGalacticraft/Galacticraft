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

package dev.galacticraft.mod.client.render.block.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.block.entity.machine.AdvancedSolarPanelBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class AdvancedSolarPanelBlockEntityRenderer implements BlockEntityRenderer<AdvancedSolarPanelBlockEntity> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/model/solar_panel.png");
    private final ModelPart panel;
    private final ModelPart pole;

    public AdvancedSolarPanelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(GCEntityModelLayer.SOLAR_PANEL);
        this.panel = root.getChild(Constant.ModelPartName.SOLAR_PANEL_PANEL);
        this.pole = root.getChild(Constant.ModelPartName.SOLAR_PANEL_POLE);
    }

    @Override
    public void render(AdvancedSolarPanelBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        light = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().relative(Direction.UP, 3));

        matrices.pushPose();
        matrices.translate(0.5F, 1.0F, 0.5F);
        RenderSystem.setShaderTexture(0, AdvancedSolarPanelBlockEntityRenderer.TEXTURE);
        this.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(TEXTURE)), light, overlay, blockEntity.getLevel(), tickDelta);
        matrices.popPose();
    }

    public void render(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, Level world, float tickDelta) {
        this.pole.render(matrices, vertexConsumer, light, overlay);
        matrices.translate(0.0F, 1.5F, 0.0F);

        matrices.mulPose(Axis.ZP.rotationDegrees(180.0F));
        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Axis.XP.rotation(world.getSunAngle(tickDelta)));

        this.panel.render(matrices, vertexConsumer, light, overlay);
    }
}
