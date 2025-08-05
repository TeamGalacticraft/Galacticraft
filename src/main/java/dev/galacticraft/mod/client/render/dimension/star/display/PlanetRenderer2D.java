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

package dev.galacticraft.mod.client.render.dimension.star.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.client.render.dimension.star.GeographicalSolarPosition;
import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBody;
import dev.galacticraft.mod.client.render.dimension.star.data.PlanetData;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.List;

/**
 * Renderer for planets.
 */
public class PlanetRenderer2D implements CelestialBodyRenderer {
    private final GeographicalSolarPosition cameraRenderPosition = GeographicalSolarPosition.getInstance();

    @Override
    public void setupBufferPositions(List<CelestialBody> bodies) {
        // This method is intentionally left empty
        // Each planet is rendered individually in the render method
    }

    @Override
    public void render(CelestialBody body, WorldRenderContext worldRenderContext) {
        if (!(body instanceof PlanetData planet)) {
            return;
        }

        // Get camera position
        Vec3 cameraPos = new Vec3(cameraRenderPosition.getX(), cameraRenderPosition.getY(), cameraRenderPosition.getZ());

        // Calculate planet position relative to camera
        double x = planet.getX() - cameraPos.x;
        double y = planet.getY() - cameraPos.y;
        double z = planet.getZ() - cameraPos.z;
        double planetSize = planet.getSize();

        // Set up rendering
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, planet.getTexture());
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        FogRenderer.setupNoFog();

        // Create buffer for this planet
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // Calculate vectors for billboarding
        Vec3 planetToCamera = new Vec3(x, y, z);
        Vec3 planetToCameraNormalized = planetToCamera.normalize();

        // Calculate right and up vectors for billboarding
        Vec3 right = planetToCameraNormalized.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = right.cross(planetToCameraNormalized).normalize();

        // Apply rotation
        double angle = Math.toRadians(planet.getRotation());
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

        // Define vertices of the quad (billboard) with texture coordinates
        buffer.addVertex(
                (float) (planetToCamera.x - rotatedRight.x * planetSize - rotatedUp.x * planetSize),
                (float) (planetToCamera.y - rotatedRight.y * planetSize - rotatedUp.y * planetSize),
                (float) (planetToCamera.z - rotatedRight.z * planetSize - rotatedUp.z * planetSize)
        ).setUv(0.0F, 0.0F);

        buffer.addVertex(
                (float) (planetToCamera.x + rotatedRight.x * planetSize - rotatedUp.x * planetSize),
                (float) (planetToCamera.y + rotatedRight.y * planetSize - rotatedUp.y * planetSize),
                (float) (planetToCamera.z + rotatedRight.z * planetSize - rotatedUp.z * planetSize)
        ).setUv(1.0F, 0.0F);

        buffer.addVertex(
                (float) (planetToCamera.x + rotatedRight.x * planetSize + rotatedUp.x * planetSize),
                (float) (planetToCamera.y + rotatedRight.y * planetSize + rotatedUp.y * planetSize),
                (float) (planetToCamera.z + rotatedRight.z * planetSize + rotatedUp.z * planetSize)
        ).setUv(1.0F, 1.0F);

        buffer.addVertex(
                (float) (planetToCamera.x - rotatedRight.x * planetSize + rotatedUp.x * planetSize),
                (float) (planetToCamera.y - rotatedRight.y * planetSize + rotatedUp.y * planetSize),
                (float) (planetToCamera.z - rotatedRight.z * planetSize + rotatedUp.z * planetSize)
        ).setUv(0.0F, 1.0F);

        // Draw the planet
        PoseStack matrices = new PoseStack();
        matrices.mulPose(worldRenderContext.positionMatrix());
        
        // Create a temporary VertexBuffer to draw with shader
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        vertexBuffer.bind();
        vertexBuffer.upload(buffer.build());
        
        vertexBuffer.drawWithShader(
                matrices.last().pose(),
                worldRenderContext.projectionMatrix(),
                GameRenderer.getPositionTexShader()
        );
        
        VertexBuffer.unbind();
        RenderSystem.enableCull();
    }

    @Override
    public void renderAll(List<CelestialBody> bodies, WorldRenderContext worldRenderContext) {
        // Render each planet individually with its own texture
        for (CelestialBody body : bodies) {
            if (body instanceof PlanetData) {
                render(body, worldRenderContext);
            }
        }
    }
}