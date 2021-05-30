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
import dev.galacticraft.mod.block.entity.AdvancedSolarPanelBlockEntity;
import dev.galacticraft.mod.client.render.entity.model.GalacticraftEntityModelLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
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
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class AdvancedSolarPanelBlockEntityRenderer implements BlockEntityRenderer<AdvancedSolarPanelBlockEntity> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, "textures/model/solar_panel.png");
    private final ModelPart panel;
    private final ModelPart pole;

    public AdvancedSolarPanelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        ModelPart root = context.getLayerModelPart(GalacticraftEntityModelLayer.SOLAR_PANEL);
        this.panel = root.getChild(Constant.ModelPartName.SOLAR_PANEL_PANEL);
        this.pole = root.getChild(Constant.ModelPartName.SOLAR_PANEL_POLE);
    }

    @Override
    public void render(AdvancedSolarPanelBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        light = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().offset(Direction.UP, 3));

        matrices.push();
        matrices.translate(0.5F, 1.0F, 0.5F);
        RenderSystem.setShaderTexture(0, AdvancedSolarPanelBlockEntityRenderer.TEXTURE);
        this.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, overlay, blockEntity.getWorld());
        matrices.pop();
    }

    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, World world) {
        this.pole.render(matrices, vertexConsumer, light, overlay);
        matrices.translate(0.0F, 1.5F, 0.0F);

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngleRadians(1.0F)));

        this.panel.render(matrices, vertexConsumer, light, overlay);
    }
}
