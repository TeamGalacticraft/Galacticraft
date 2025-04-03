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
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.TransportTubeBlockEntity;
import dev.galacticraft.mod.content.block.special.TransportTube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

// TODO: A block entity renderer isn't very efficient this should be replaced with a more efficient rendering system later
public class TransportTubeBlockEntityRenderer implements BlockEntityRenderer<TransportTubeBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    public TransportTubeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public class Transform {
        private Vector3f position;
        private Vector3f scale;
        private Vector3f direction;

        public Transform(Vector3f position, Vector3f scale, Vector3f rotation) {
            this.position = position;
            this.scale = scale;
            this.direction = rotation;
        }

        public Vector3f transformPoint(Vector3f localPoint) {
//            Matrix4f transformMatrix = new Matrix4f();
//
//            Vector3f worldPoint = new Vector3f();
//
//            Matrix4f rotationMatrix = new Matrix4f();
//
//            Quaternionf rotation = new Quaternionf();
//            rotationMatrix.lookAlong(new Vector3f(direction).negate(), new Vector3f(0, 1, 0)).invertAffine();
//
//            transformMatrix
//                .translate(position)
//                .scale(scale)
//                .rotation(rotationMatrix.getNormalizedRotation(rotation));
//
//            transformMatrix.transformPosition(localPoint, worldPoint);
//            return new Vector3f(position).add(worldPoint); // We shouldn't need to do this wtf

            Vector3f scaledPoint = new Vector3f(localPoint).mul(scale);

            // Step 2: Rotate the scaled point using the quaternion
            Vector3f rotatedPoint = lookRotation(direction).transform(new Vector3f(scaledPoint));

            // Step 3: Translate the rotated point
            Vector3f worldPoint = rotatedPoint.add(position);

            return worldPoint;
        }
    }

    void drawPoint(PoseStack matrices, Vector3f point, MultiBufferSource bufferSource, int light, int overlay, Transform transform) {
        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.translate(point.x, point.y, point.z);
        matrices.scale(0.5F, 0.5F, 0.5F);
        matrices.translate(-0.5, -0.5, -0.5);
//        matrices.rotateAround(transform.rotation, 0, 0, 0);
//        matrices.last().pose().rotateTowards(transform.position, new Vector3f(0, 1, 0));

        this.blockRenderer.renderSingleBlock(Blocks.STONE.defaultBlockState(), matrices, bufferSource, light, overlay);
        matrices.popPose();
    }
    public static Quaternionf lookRotation(Vector3f forward) {
        Vector3f f = new Vector3f(forward).normalize();
        Vector3f up = new Vector3f(0, 1, 0);

        // Handle the degenerate case when forward is too close to up
        if (Math.abs(f.dot(up)) > 0.9999f) {
            // If looking straight up or down, choose a different arbitrary right vector
            up = new Vector3f(1, 0, 0);
        }

        // Compute the right vector
        Vector3f right = new Vector3f();
        up.cross(f, right).normalize();

        // Compute the corrected up vector
        Vector3f correctedUp = new Vector3f();
        f.cross(right, correctedUp).normalize();

        // Create rotation matrix
        Matrix3f rotationMatrix = new Matrix3f().set(
                right.x, correctedUp.x, -f.x,
                right.y, correctedUp.y, -f.y,
                right.z, correctedUp.z, -f.z
        );

        // Convert to quaternion
        Quaternionf rotation = new Quaternionf();
        rotation.setFromNormalized(rotationMatrix);

        return rotation;
    }


    public Vector3f bezier(float t, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
//        Vector3f a = p0.lerp(p1, t, new Vector3f());
//        Vector3f b = p1.lerp(p2, t, new Vector3f());
//        Vector3f c = p2.lerp(p3, t, new Vector3f());
//
//        Vector3f d = a.lerp(b, t, new Vector3f());
//        Vector3f e = b.lerp(c, t, new Vector3f());
//
//        return d.lerp(e, t, new Vector3f());

        float u = 1 - t;
        return new Vector3f(u * u * u * p0.x + 3 * t * u * u * p1.x + 3 * t * t * u * p2.x + t * t * t * p3.x,
                u * u * u * p0.y + 3 * t * u * u * p1.y + 3 * t * t * u * p2.y + t * t * t * p3.y,
                u * u * u * p0.z + 3 * t * u * u * p1.z + 3 * t * t * u * p2.z + t * t * t * p3.z);
    }

    @Override
    public void render(TransportTubeBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.LINES);
        BlockPos connectionPos = entity.getConnectionPos();


        if (connectionPos != null && entity.getLevel().getBlockState(connectionPos).is(GCBlocks.PLAYER_TRANSPORT_TUBE) && entity.isOwner()) {
            BlockPos otherPos = entity.getBlockPos();
            BlockState state = entity.getBlockState();
            BlockState otherState = entity.getLevel().getBlockState(connectionPos);

            connectionPos = connectionPos.subtract(otherPos);

            Vector3f p1 = new Vector3f();
            Vector3f p2 = new Vector3f(connectionPos.getX(), connectionPos.getY(), connectionPos.getZ());

            var p1Nomral = state.getValue(TransportTube.FACING).getNormal();
            Vector3f p1Dir = new Vector3f(p1Nomral.getX(), p1Nomral.getY(), p1Nomral.getZ());

            var p2Nomral = otherState.getValue(TransportTube.FACING).getNormal();
            Vector3f p2Dir = new Vector3f(p2Nomral.getX(), p2Nomral.getY(), p2Nomral.getZ());

            Transform start = new Transform(p1, new Vector3f(1, 1, 1), p1Dir);
            Transform end = new Transform(p2, new Vector3f(1, 1, 1), p2Dir);

            Vector3f controlP1 = start.transformPoint(new Vector3f(0, 0, 1));
            Vector3f controlP2 = end.transformPoint(new Vector3f(0, 0, 1));

            drawPoint(matrices, p1, vertexConsumers, light, overlay, start);
            drawPoint(matrices, controlP1, vertexConsumers, light, overlay, start);
            drawPoint(matrices, controlP2, vertexConsumers, light, overlay, end);
            drawPoint(matrices, p2, vertexConsumers, light, overlay, end);

//            Vec3 p3 = getPerpendicular(start, p2, 0.5);
            PoseStack.Pose pose = matrices.last();

            Vector3f lastPoint = null;
//
            for (float t = 0; t < 1; t += 0.05F) {
//                float x1 = (float) Mth.lerp(delta, start.x, p2.x);
//                float y1 = (float) Mth.lerp(delta, start.y, p2.y);
//                float z1 = (float) Mth.lerp(delta, start.z, p2.z);
//
//                float x2 = (float) Mth.lerp(delta, p2.x, p3.x);
//                float y2 = (float) Mth.lerp(delta, p2.y, p3.y);
//                float z2 = (float) Mth.lerp(delta, p2.z, p3.z);
//
//                float x = Mth.lerp(delta, x1, x2);
//                float y = Mth.lerp(delta, y1, y2);
//                float z = Mth.lerp(delta, z1, z2);


                Vector3f point = bezier(t, p1, controlP1, controlP2, p2);
                if (lastPoint == null)
                    lastPoint = point;

                consumer.addVertex(pose, point).setNormal(pose, 0, 1, 0).setColor(255, 255, 255, 255);
                consumer.addVertex(pose, lastPoint).setNormal(pose, 0, 1, 0).setColor(255, 255, 255, 255);
                lastPoint = point;
            }

//            PoseStack.Pose pose = matrices.last();
//            consumer.addVertex(pose, 0, 0, 0).setNormal(pose, 0, 1, 0).setColor(255, 255, 255, 255);
//            var otherPos = p2.subtract(entity.getBlockPos());
//            consumer.addVertex(pose, otherPos.getX(), otherPos.getY(), otherPos.getZ()).setNormal(pose, 0, 1, 0).setColor(255, 255, 255, 255);
//            matrices.popPose();
        }

    }

    @Override
    public boolean shouldRenderOffScreen(TransportTubeBlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean shouldRender(TransportTubeBlockEntity blockEntity, Vec3 pos) {
        return true;
    }
}
