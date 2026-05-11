/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.LunarCheesePressBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class LunarCheesePressBlockEntityRenderer implements BlockEntityRenderer<LunarCheesePressBlockEntity> {
    private static final ResourceLocation TEXTURE = Constant.id("textures/block/lunar_cheese_press_head.png");
    private static final BlockState CHEESE_WHEEL_STATE = GCBlocks.MOON_CHEESE_WHEEL.defaultBlockState();
    private static final float CHEESE_SCALE = 0.4375F;
    private static final float CHEESE_Y = 1.01F;
    private static final float CHEESE_MODEL_HEIGHT = 8.0F / 16.0F;
    private static final float CHEESE_VOLUME_SCALE = CHEESE_SCALE * CHEESE_SCALE * CHEESE_SCALE;
    private static final float CHEESE_SURFACE_CLEARANCE = 1.0F / 256.0F;
    private static final float CHEESE_MIN_HEIGHT_FACTOR = 0.72F;
    private static final float CHEESE_MAX_SPREAD_FACTOR = 1.15F;
    private static final float HEAD_REST_Y = 2.25F;
    private static final float HEAD_TOUCH_SQUASH = 0.015F;
    private static final float HEAD_PLATE_BOTTOM_OFFSET = 10.0F / 16.0F;
    private final ModelPart head;
    private final BlockRenderDispatcher blockRenderer;

    public LunarCheesePressBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(GCEntityModelLayer.LUNAR_CHEESE_PRESS);
        this.head = root.getChild(Constant.ModelPartName.LUNAR_CHEESE_PRESS_HEAD);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild(
                Constant.ModelPartName.LUNAR_CHEESE_PRESS_HEAD,
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.0F, -10.0F, -1.0F, 2.0F, 10.0F, 2.0F, CubeDeformation.NONE)
                        .texOffs(0, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 2.0F, 8.0F, CubeDeformation.NONE),
                PartPose.ZERO
        );
        return LayerDefinition.create(meshDefinition, 16, 16);
    }

    @Override
    public void render(LunarCheesePressBlockEntity blockEntity, float tickDelta, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        int wheelLight = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above());
        int headLight = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above(2));
        float pressOffset = blockEntity.getPressOffset(tickDelta);

        if (blockEntity.isCheeseVisible(tickDelta)) {
            float cheeseBaseHeight = CHEESE_SCALE * CHEESE_MODEL_HEIGHT;
            float cheeseMinHeight = cheeseBaseHeight * CHEESE_MIN_HEIGHT_FACTOR;
            float pressBottomY = HEAD_REST_Y - pressOffset - HEAD_TOUCH_SQUASH - HEAD_PLATE_BOTTOM_OFFSET;
            float cheeseHeight = Mth.clamp(pressBottomY - CHEESE_Y - CHEESE_SURFACE_CLEARANCE, cheeseMinHeight, cheeseBaseHeight);
            float cheeseScaleY = cheeseHeight / CHEESE_MODEL_HEIGHT;
            float cheeseScaleXZ = Mth.clamp(Mth.sqrt(CHEESE_VOLUME_SCALE / cheeseScaleY), CHEESE_SCALE, CHEESE_SCALE * CHEESE_MAX_SPREAD_FACTOR);
            float cheeseXZOffset = (1.0F - cheeseScaleXZ) * 0.5F;

            poseStack.pushPose();
            poseStack.translate(cheeseXZOffset, CHEESE_Y, cheeseXZOffset);
            poseStack.scale(cheeseScaleXZ, cheeseScaleY, cheeseScaleXZ);
            this.blockRenderer.renderSingleBlock(CHEESE_WHEEL_STATE, poseStack, bufferSource, wheelLight, overlay);
            poseStack.popPose();
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, HEAD_REST_Y - pressOffset - HEAD_TOUCH_SQUASH, 0.5F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.head.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), headLight, overlay);
        poseStack.popPose();
    }
}