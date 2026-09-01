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

package dev.galacticraft.mod.client.render.dimension.star.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.client.render.dimension.star.GeographicalSolarPosition;
import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBody;
import dev.galacticraft.mod.client.render.dimension.star.data.StarData;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.List;

/**
 * Renderer for stars.
 */
public class StarRenderer implements CelestialBodyRenderer {

    private VertexBuffer starBuffer;
    private final GeographicalSolarPosition cameraRenderPosition = GeographicalSolarPosition.getInstance();

    @Override
    public void setupBufferPositions(List<CelestialBody> bodies) {
        if (this.starBuffer == null) {
            this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        }

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();

        RenderSystem.disableCull();

        // Calculate the direction from the star to the camera
        Vec3 cameraPos = new Vec3(cameraRenderPosition.getX(), cameraRenderPosition.getY(), cameraRenderPosition.getZ());


        for (CelestialBody body : bodies) {
            if (body instanceof StarData star) {

                double starSize = star.getSize();
                double starBrightness = star.getBrightness();
                double x = star.getX() - cameraPos.x;
                double y = star.getY() - cameraPos.y;
                double z = star.getZ() - cameraPos.z;

                Vec3 starToCamera = new Vec3(x, y, z); // Star's position relative to camera
                Vec3 starToCameraNormalized = starToCamera.normalize();

                Vec3 starPos = new Vec3(star.getX(), star.getY(), star.getZ());

                double distance = starPos.distanceTo(cameraPos);

                starSize = this.getModifiedStarSize(distance, starSize);

                starSize *= 0.5; // half size star

                // TODO: we should make the star sized modified by the distance from the camera
                if (starSize == 0) {
                    continue;
                }

                Vec3 right = starToCameraNormalized.cross(new Vec3(0, 1, 0)).normalize();
                Vec3 up = right.cross(starToCameraNormalized).normalize();

                double angle = Math.toRadians(star.getRotation());

                double cos = Math.cos(angle);
                double sin = Math.sin(angle);

                // Compute rotated right and up vectors
                Vector3d rotatedRight = new Vector3d(
                        cos * right.x - sin * up.x,
                        cos * right.y - sin * up.y,
                        cos * right.z - sin * up.z
                ).normalize();

                Vector3d rotatedUp = new Vector3d(
                        sin * right.x + cos * up.x,
                        sin * right.y + cos * up.y,
                        sin * right.z + cos * up.z
                ).normalize();

                // Define vertices of the quad (billboard) around the star with rotation
                float r = 1.0f;
                float g = 1.0f;
                float b = 1.0f;
                float a = 1.0f;

                buffer.addVertex(
                        (float) (starToCamera.x - rotatedRight.x * starSize - rotatedUp.x * starSize),
                        (float) (starToCamera.y - rotatedRight.y * starSize - rotatedUp.y * starSize),
                        (float) (starToCamera.z - rotatedRight.z * starSize - rotatedUp.z * starSize)
                ).setColor(r, g, b, a);

                buffer.addVertex(
                        (float) (starToCamera.x + rotatedRight.x * starSize - rotatedUp.x * starSize),
                        (float) (starToCamera.y + rotatedRight.y * starSize - rotatedUp.y * starSize),
                        (float) (starToCamera.z + rotatedRight.z * starSize - rotatedUp.z * starSize)
                ).setColor(r, g, b, a);

                buffer.addVertex(
                        (float) (starToCamera.x + rotatedRight.x * starSize + rotatedUp.x * starSize),
                        (float) (starToCamera.y + rotatedRight.y * starSize + rotatedUp.y * starSize),
                        (float) (starToCamera.z + rotatedRight.z * starSize + rotatedUp.z * starSize)
                ).setColor(r, g, b, a);

                buffer.addVertex(
                        (float) (starToCamera.x - rotatedRight.x * starSize + rotatedUp.x * starSize),
                        (float) (starToCamera.y - rotatedRight.y * starSize + rotatedUp.y * starSize),
                        (float) (starToCamera.z - rotatedRight.z * starSize + rotatedUp.z * starSize)
                ).setColor(r, g, b, a);

            }
        }

        this.starBuffer.bind();
        this.starBuffer.upload(buffer.build());
        VertexBuffer.unbind();

        RenderSystem.enableCull();


    }

    // modify star size to scale differently based on distance from camera
    private double getModifiedStarSize(double distance, double starSize) {
        if (distance < 60) {
            // Reduce star size linearly as distance decreases from 100 to 0
            return starSize * (distance / 60);
        } else if (distance > 600) {
            // Increase star size linearly as distance increases from 200 to 1000
            return 0;
        } else if (distance > 320) {
            return starSize * (1 + (distance / 300));
        }
        return starSize;
    }

    @Override
    public void render(CelestialBody body, WorldRenderContext worldRenderContext) {
        // Individual star rendering is not used
        // Stars are rendered all at once in renderAll
    }

    @Override
    public void renderAll(List<CelestialBody> bodies, WorldRenderContext worldRenderContext) {
        // Setup buffer positions once for all bodies
        this.setupBufferPositions(bodies);


        // Render all stars at once
        if (starBuffer != null) {
            RenderSystem.setShaderColor(1.0F, 0.95F, 0.9F, 1);
            FogRenderer.setupNoFog();

            this.starBuffer.bind();


            PoseStack matrices = new PoseStack();

            matrices.mulPose(worldRenderContext.positionMatrix());

            this.starBuffer.drawWithShader(
                    matrices.last().pose(),
                    worldRenderContext.projectionMatrix(),
                    GameRenderer.getPositionColorShader()
            );
            VertexBuffer.unbind();
        }
    }
}
