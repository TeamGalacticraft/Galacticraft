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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.mod.content.block.decoration.DisplayCaseBlock;
import dev.galacticraft.mod.content.block.entity.decoration.DisplayCaseBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class DisplayCaseBlockEntityRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity> {
    private static final double ITEM_DISTANCE_FROM_BASE = 2.3D / 16.0D;

    private final ItemRenderer itemRenderer;
    private final Font font;

    public DisplayCaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.font = context.getFont();
    }

    @Override
    public void render(
            DisplayCaseBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        ItemStack stack = blockEntity.getItem();

        if (stack.isEmpty()) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(DisplayCaseBlock.FACING);

        renderItem(
                blockEntity,
                stack,
                facing,
                poseStack,
                buffer,
                packedLight
        );

        renderName(
                stack,
                poseStack,
                buffer,
                packedLight
        );
    }

    private void renderItem(
            DisplayCaseBlockEntity blockEntity,
            ItemStack stack,
            Direction facing,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        poseStack.pushPose();

        translateItem(poseStack, facing);
        rotateItem(poseStack, facing);

        poseStack.scale(0.5F, 0.5F, 0.5F);

        this.itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                blockEntity.getLevel(),
                0
        );

        poseStack.popPose();
    }

    private static void translateItem(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case UP -> poseStack.translate(
                    0.5D,
                    ITEM_DISTANCE_FROM_BASE,
                    0.5D
            );

            case DOWN -> poseStack.translate(
                    0.5D,
                    1.0D - ITEM_DISTANCE_FROM_BASE,
                    0.5D
            );

            case NORTH -> poseStack.translate(
                    0.5D,
                    0.5D,
                    1.0D - ITEM_DISTANCE_FROM_BASE
            );

            case SOUTH -> poseStack.translate(
                    0.5D,
                    0.5D,
                    ITEM_DISTANCE_FROM_BASE
            );

            case EAST -> poseStack.translate(
                    ITEM_DISTANCE_FROM_BASE,
                    0.5D,
                    0.5D
            );

            case WEST -> poseStack.translate(
                    1.0D - ITEM_DISTANCE_FROM_BASE,
                    0.5D,
                    0.5D
            );
        }
    }

    private static void rotateItem(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case SOUTH -> {
            }

            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }
    }

    private void renderName(
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (!stack.has(DataComponents.CUSTOM_NAME)) {
            return;
        }

        Component name = stack.getHoverName();

        poseStack.pushPose();

        poseStack.translate(0.5D, 1.15D, 0.5D);

        poseStack.mulPose(
                Minecraft.getInstance()
                        .getEntityRenderDispatcher()
                        .cameraOrientation()
        );

        poseStack.scale(0.025F, -0.025F, 0.025F);

        Matrix4f matrix = poseStack.last().pose();

        float backgroundOpacity = Minecraft.getInstance()
                .options
                .getBackgroundOpacity(0.25F);

        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

        float x = -this.font.width(name) / 2.0F;

        this.font.drawInBatch(
                name,
                x,
                0.0F,
                0x20FFFFFF,
                false,
                matrix,
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                backgroundColor,
                packedLight
        );

        this.font.drawInBatch(
                name,
                x,
                0.0F,
                0xFFFFFFFF,
                false,
                matrix,
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                packedLight
        );

        poseStack.popPose();
    }
}