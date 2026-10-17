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
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

/**
 * Renders the fluid flowing through a glass fluid pipe as a core inside the glass.
 */
public class FluidPipeBlockEntityRenderer implements BlockEntityRenderer<GlassFluidPipeBlockEntity> {
    /**
     * How far the fluid stays inside the glass, so the two don't z-fight.
     */
    private static final float INSET = 0.01F;

    public FluidPipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(GlassFluidPipeBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        FluidVariant fluid = entity.getDisplayedFluid();
        if (fluid.isBlank() || !(entity.getBlockState().getBlock() instanceof PipeShapedBlock<?> block)) return;

        TextureAtlasSprite sprite = FluidVariantRendering.getSprite(fluid);
        if (sprite == null) return;
        // Fluid colors don't always include alpha, the transparency comes from the texture
        int color = 0xFF000000 | FluidVariantRendering.getColor(fluid, entity.getLevel(), entity.getBlockPos());

        float min = 0.5F - block.getRadius() + INSET;
        float max = 0.5F + block.getRadius() - INSET;
        VertexConsumer consumer = vertexConsumers.getBuffer(Sheets.translucentCullBlockSheet());
        PoseStack.Pose pose = matrices.last();

        // The center, with an opening on every connected side
        for (Direction face : Direction.values()) {
            if (!entity.isConnected(face)) {
                face(consumer, pose, face, min, min, min, max, max, max, sprite, color, light, overlay);
            }
        }

        // An arm from the center to the edge of the block for every connection, without its ends
        for (Direction direction : Direction.values()) {
            if (!entity.isConnected(direction)) continue;
            float x0 = direction == Direction.WEST ? 0 : direction == Direction.EAST ? max : min;
            float x1 = direction == Direction.WEST ? min : direction == Direction.EAST ? 1 : max;
            float y0 = direction == Direction.DOWN ? 0 : direction == Direction.UP ? max : min;
            float y1 = direction == Direction.DOWN ? min : direction == Direction.UP ? 1 : max;
            float z0 = direction == Direction.NORTH ? 0 : direction == Direction.SOUTH ? max : min;
            float z1 = direction == Direction.NORTH ? min : direction == Direction.SOUTH ? 1 : max;
            for (Direction face : Direction.values()) {
                if (face.getAxis() != direction.getAxis()) {
                    face(consumer, pose, face, x0, y0, z0, x1, y1, z1, sprite, color, light, overlay);
                }
            }
        }
    }

    private static void face(VertexConsumer consumer, PoseStack.Pose pose, Direction face, float x0, float y0, float z0, float x1, float y1, float z1, TextureAtlasSprite sprite, int color, int light, int overlay) {
        // The texture is mapped by position, so it lines up between the center and the arms
        float u0, u1, v0, v1;
        switch (face.getAxis()) {
            case Y -> { u0 = x0; u1 = x1; v0 = z0; v1 = z1; }
            case Z -> { u0 = x0; u1 = x1; v0 = 1 - y1; v1 = 1 - y0; }
            default -> { u0 = z0; u1 = z1; v0 = 1 - y1; v1 = 1 - y0; }
        }
        u0 = sprite.getU(u0);
        u1 = sprite.getU(u1);
        v0 = sprite.getV(v0);
        v1 = sprite.getV(v1);

        switch (face) {
            case DOWN -> {
                vertex(consumer, pose, face, x0, y0, z1, u0, v1, color, light, overlay);
                vertex(consumer, pose, face, x0, y0, z0, u0, v0, color, light, overlay);
                vertex(consumer, pose, face, x1, y0, z0, u1, v0, color, light, overlay);
                vertex(consumer, pose, face, x1, y0, z1, u1, v1, color, light, overlay);
            }
            case UP -> {
                vertex(consumer, pose, face, x0, y1, z0, u0, v0, color, light, overlay);
                vertex(consumer, pose, face, x0, y1, z1, u0, v1, color, light, overlay);
                vertex(consumer, pose, face, x1, y1, z1, u1, v1, color, light, overlay);
                vertex(consumer, pose, face, x1, y1, z0, u1, v0, color, light, overlay);
            }
            case NORTH -> {
                vertex(consumer, pose, face, x1, y1, z0, u1, v0, color, light, overlay);
                vertex(consumer, pose, face, x1, y0, z0, u1, v1, color, light, overlay);
                vertex(consumer, pose, face, x0, y0, z0, u0, v1, color, light, overlay);
                vertex(consumer, pose, face, x0, y1, z0, u0, v0, color, light, overlay);
            }
            case SOUTH -> {
                vertex(consumer, pose, face, x0, y1, z1, u0, v0, color, light, overlay);
                vertex(consumer, pose, face, x0, y0, z1, u0, v1, color, light, overlay);
                vertex(consumer, pose, face, x1, y0, z1, u1, v1, color, light, overlay);
                vertex(consumer, pose, face, x1, y1, z1, u1, v0, color, light, overlay);
            }
            case WEST -> {
                vertex(consumer, pose, face, x0, y1, z0, u0, v0, color, light, overlay);
                vertex(consumer, pose, face, x0, y0, z0, u0, v1, color, light, overlay);
                vertex(consumer, pose, face, x0, y0, z1, u1, v1, color, light, overlay);
                vertex(consumer, pose, face, x0, y1, z1, u1, v0, color, light, overlay);
            }
            case EAST -> {
                vertex(consumer, pose, face, x1, y1, z1, u1, v0, color, light, overlay);
                vertex(consumer, pose, face, x1, y0, z1, u1, v1, color, light, overlay);
                vertex(consumer, pose, face, x1, y0, z0, u0, v1, color, light, overlay);
                vertex(consumer, pose, face, x1, y1, z0, u0, v0, color, light, overlay);
            }
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Direction face, float x, float y, float z, float u, float v, int color, int light, int overlay) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, face.getStepX(), face.getStepY(), face.getStepZ());
    }
}
