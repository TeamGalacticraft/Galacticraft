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
import dev.galacticraft.mod.client.render.dimension.star.data.Planet3DData;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Renderer for 3D planets as cubes with texture and opacity support.
 */
public class PlanetRenderer3D implements CelestialBodyRenderer {
    private final GeographicalSolarPosition cameraRenderPosition = GeographicalSolarPosition.getInstance();

    @Override
    public void setupBufferPositions(List<CelestialBody> bodies) {
        // This method is intentionally left empty
        // Each planet is rendered individually in the render method
    }
    @Override
    public void render(CelestialBody body, WorldRenderContext worldRenderContext) {
        if (!(body instanceof Planet3DData planet)) {
            return;
        }

        // Get camera position
        Vec3 cameraPos = cameraRenderPosition.getVec3();

        // Calculate planet position relative to camera
        double x = planet.getX() - cameraPos.x;
        double y = planet.getY() - cameraPos.y;
        double z = planet.getZ() - cameraPos.z;
        double planetSize = planet.getSize();
        float opacity = planet.getOpacity();

        // Set up rendering
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc(); // Use default blend function for transparency
        FogRenderer.setupNoFog();

        // Create matrix for rotation
        PoseStack matrices = new PoseStack();
        matrices.mulPose(worldRenderContext.positionMatrix());

        // Translate to planet position
        matrices.pushPose();
        matrices.translate(x, y, z);

        // Apply planet rotation
        double rotationDegrees = planet.getRotation();
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees((float) rotationDegrees));

        // Half size for vertex calculations (from center to edge)
        float size = (float) planetSize / 2.0f;

        // Render each face of the cube
        renderCubeFace(matrices, planet, Planet3DData.Face.TOP, size, opacity, worldRenderContext);
        renderCubeFace(matrices, planet, Planet3DData.Face.BOTTOM, size, opacity, worldRenderContext);
        renderCubeFace(matrices, planet, Planet3DData.Face.NORTH, size, opacity, worldRenderContext);
        renderCubeFace(matrices, planet, Planet3DData.Face.SOUTH, size, opacity, worldRenderContext);
        renderCubeFace(matrices, planet, Planet3DData.Face.EAST, size, opacity, worldRenderContext);
        renderCubeFace(matrices, planet, Planet3DData.Face.WEST, size, opacity, worldRenderContext);

        matrices.popPose();

        // Reset render state
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    /**
     * Renders a single face of the cube.
     */
    private void renderCubeFace(PoseStack matrices, Planet3DData planet, Planet3DData.Face face, float size, float opacity, WorldRenderContext worldRenderContext) {
        // Set the texture for this face
        ResourceLocation texture = planet.getTexture(face);
        RenderSystem.setShaderTexture(0, texture);

        // Create buffer for this face
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        // Define vertices based on which face we're rendering
        // Each vertex has position, texture coordinates, and color (with alpha for opacity)
        float r = 1.0f;
        float g = 1.0f;
        float b = 1.0f;
        float a = opacity;

        switch (face) {
            case TOP -> {
                // Top face (Y+)
                buffer.addVertex(-size, size, -size).setUv(0, 0).setColor(r, g, b, a);
                buffer.addVertex(-size, size, size).setUv(0, 1).setColor(r, g, b, a);
                buffer.addVertex(size, size, size).setUv(1, 1).setColor(r, g, b, a);
                buffer.addVertex(size, size, -size).setUv(1, 0).setColor(r, g, b, a);
            }
            case BOTTOM -> {
                // Bottom face (Y-)
                buffer.addVertex(-size, -size, -size).setUv(0, 0).setColor(r, g, b, a);
                buffer.addVertex(size, -size, -size).setUv(1, 0).setColor(r, g, b, a);
                buffer.addVertex(size, -size, size).setUv(1, 1).setColor(r, g, b, a);
                buffer.addVertex(-size, -size, size).setUv(0, 1).setColor(r, g, b, a);
            }
            case NORTH -> {
                // North face (Z-)
                buffer.addVertex(-size, -size, -size).setUv(0, 1).setColor(r, g, b, a);
                buffer.addVertex(-size, size, -size).setUv(0, 0).setColor(r, g, b, a);
                buffer.addVertex(size, size, -size).setUv(1, 0).setColor(r, g, b, a);
                buffer.addVertex(size, -size, -size).setUv(1, 1).setColor(r, g, b, a);
            }
            case SOUTH -> {
                // South face (Z+)
                buffer.addVertex(-size, -size, size).setUv(1, 1).setColor(r, g, b, a);
                buffer.addVertex(size, -size, size).setUv(0, 1).setColor(r, g, b, a);
                buffer.addVertex(size, size, size).setUv(0, 0).setColor(r, g, b, a);
                buffer.addVertex(-size, size, size).setUv(1, 0).setColor(r, g, b, a);
            }
            case EAST -> {
                // East face (X+)
                buffer.addVertex(size, -size, -size).setUv(0, 1).setColor(r, g, b, a);
                buffer.addVertex(size, size, -size).setUv(0, 0).setColor(r, g, b, a);
                buffer.addVertex(size, size, size).setUv(1, 0).setColor(r, g, b, a);
                buffer.addVertex(size, -size, size).setUv(1, 1).setColor(r, g, b, a);
            }
            case WEST -> {
                // West face (X-)
                buffer.addVertex(-size, -size, -size).setUv(1, 1).setColor(r, g, b, a);
                buffer.addVertex(-size, -size, size).setUv(0, 1).setColor(r, g, b, a);
                buffer.addVertex(-size, size, size).setUv(0, 0).setColor(r, g, b, a);
                buffer.addVertex(-size, size, -size).setUv(1, 0).setColor(r, g, b, a);
            }
        }

        // Draw the face
        Matrix4f matrix = matrices.last().pose();

        drawVertexBuffer(worldRenderContext, buffer, matrix);
    }

    VertexBuffer planetBuffer;

    private void drawVertexBuffer(WorldRenderContext worldRenderContext, BufferBuilder buffer, Matrix4f matrix) {
        // Create a temporary VertexBuffer to draw with shader
        if (this.planetBuffer == null) {
            this.planetBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        }

        planetBuffer.bind();
        planetBuffer.upload(buffer.build());

        planetBuffer.drawWithShader(
                matrix,
                worldRenderContext.projectionMatrix(),
                GameRenderer.getPositionTexColorShader()
        );

        VertexBuffer.unbind();
    }

    @Override
    public void renderAll(List<CelestialBody> bodies, WorldRenderContext worldRenderContext) {
        // Render each planet individually
        for (CelestialBody body : bodies) {
            if (body instanceof Planet3DData) {
                render(body, worldRenderContext);
            }
        }
    }
}
