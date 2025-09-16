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

package dev.galacticraft.mod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.block.special.CryogenicChamberPart;
import dev.galacticraft.mod.content.block.special.TransportTube;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GCBlockOutlineRenderer {
    // Define the RGBA values of the block outline for translucent blocks
    private static float R = 0.15F;
    private static float G = 0.15F;
    private static float B = 0.15F;
    private static float A = 1.0F; // opaque

    public static boolean renderBlockOutlines(WorldRenderContext worldContext, BlockOutlineContext context) {
        BlockState blockState = context.blockState();
        if (blockState.getBlock() instanceof CryogenicChamberBlock || blockState.getBlock() instanceof CryogenicChamberPart || blockState.getBlock() instanceof TransportTube) {
            BlockPos blockPos = context.blockPos();
            VoxelShape voxelShape = blockState.getShape(worldContext.world(), blockPos, CollisionContext.of(context.entity()));
            double x0 = (double) blockPos.getX() - context.cameraX();
            double y0 = (double) blockPos.getY() - context.cameraY();
            double z0 = (double) blockPos.getZ() - context.cameraZ();
            PoseStack.Pose pose = worldContext.matrixStack().last();
            VertexConsumer vertexConsumer = worldContext.consumers().getBuffer(RenderType.lines());
            voxelShape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
                float x3 = (float) (x2 - x1);
                float y3 = (float) (y2 - y1);
                float z3 = (float) (z2 - z1);
                float L = Mth.sqrt(x3 * x3 + y3 * y3 + z3 * z3);
                vertexConsumer.addVertex(pose, (float) (x0 + x1), (float) (y0 + y1), (float) (z0 + z1)).setColor(R, G, B, A).setNormal(pose, x3 /= L, y3 /= L, z3 /= L);
                vertexConsumer.addVertex(pose, (float) (x0 + x2), (float) (y0 + y2), (float) (z0 + z2)).setColor(R, G, B, A).setNormal(pose, x3, y3, z3);
            });
            return false;
        }
        return true;
    }
}