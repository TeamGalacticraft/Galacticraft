/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.decoration.CannedFoodBlock;
import dev.galacticraft.mod.content.block.entity.decoration.CannedFoodBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CannedFoodBlockEntityRenderer implements BlockEntityRenderer<CannedFoodBlockEntity> {
    private static final Minecraft client = Minecraft.getInstance();
    private static final BakedModel canModel = client.getBlockRenderer().getBlockModelShaper().getModelManager().getModel(GCModelLoader.CANNED_FOOD_MODEL);

    public CannedFoodBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CannedFoodBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource buffer, int light, int overlay) {
        int canCount = entity.getCanCount();
        List<ItemStack> contents = entity.getCanContents();

        if (contents.size() < canCount) {
            // Prevent crash and wait for data sync
            // to avoid rendering bugs where it stops rendering while waiting for client sync
            // if the block had previous data keep rendering previous data until client sync happens
            if (!contents.isEmpty()) {
                canCount -= 1;
            } else {
                return;
            }
        }

        // Define relative positions for each can layout
        float[][][] positions = {
                {{8, 0, 8}},                        // 1 can
                {{4, 0, 8}, {12, 0, 8}},            // 2 cans
                {{4, 0, 4}, {12, 0, 6}, {6, 0, 12}},// 3 cans
                {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}}, // 4 cans
                {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {8, 8, 8}}, // 5 cans (layer 2 starts)
                {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 8}, {12, 8, 8}}, // 6 cans
                {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 4}, {12, 8, 6}, {6, 8, 12}}, // 7 cans
                {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 4}, {12, 8, 4}, {4, 8, 12}, {12, 8, 12}} // 8 cans (full)
        };

        for (int i = 0; i < canCount; i++) {
            float[] position = positions[canCount - 1][i];
            float x = (position[0] - 8) / 16.0f; // Convert pixel coords to block space
            float y = position[1] / 16.0f;
            float z = (position[2] - 8) / 16.0f;

            int canColor = 0;

            ItemStack stack = contents.get(i);
            canColor = stack.getOrDefault(GCDataComponents.COLOR, 0);

            // Extract RGB components from the integer color
            float red = ((canColor >> 16) & 0xFF) / 255.0f;
            float green = ((canColor >> 8) & 0xFF) / 255.0f;
            float blue = (canColor & 0xFF) / 255.0f;

            matrices.pushPose();
            matrices.translate(0.5f, 0, 0.5f);

            Direction facing = entity.getBlockState().getValue(CannedFoodBlock.FACING);
            float rotationAngle = getRotationAngle(facing);

            matrices.mulPose(Axis.YP.rotationDegrees(rotationAngle));

            matrices.translate(-0.5f, 0, -0.5f);

            matrices.translate(x, y, z);


            for (BakedQuad quad : canModel.getQuads(GCBlocks.CANNED_FOOD.defaultBlockState(), null, entity.getLevel().random)) {
                RenderType renderType = RenderType.cutout();

                if (quad.getSprite().contents().name().toString().contains("canned_food_label_texture")) {
                    buffer.getBuffer(renderType).putBulkData(
                            matrices.last(),
                            quad,
                            red, green, blue, 1.0f,
                            light,
                            overlay
                    );
                } else {
                    // Render other parts normally
                    buffer.getBuffer(renderType).putBulkData(
                            matrices.last(),
                            quad,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            light,
                            overlay
                    );
                }
            }

            matrices.popPose();
        }
    }

    private float getRotationAngle(Direction direction) {
        return switch (direction) {
            case NORTH -> 0f;    // Default, no rotation
            case EAST -> 90f;     // Rotated 90 degrees clockwise
            case SOUTH -> 180f;   // Rotated 180 degrees
            case WEST -> 270f;    // Rotated 270 degrees
            default -> 0f;        // Fallback (shouldn't happen)
        };
    }
}
