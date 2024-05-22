/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.client.model;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.javagl.obj.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gl.MeshBuffer;
import dev.galacticraft.mod.client.render.entity.rocket.RocketEntityRenderer;
import dev.galacticraft.mod.client.resources.RocketTextureManager;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;

/**
 * A Model rendered via a VBO.
 */
public class VertexBufferModel implements GCBakedModel {
    private final Obj obj;
    private boolean compiled = false;
    private MeshBuffer buffer;

    public VertexBufferModel(Obj obj) {
        this.obj = ObjUtils.convertToRenderable(obj);
    }

    public void compile(PoseStack modelStack, VertexConsumer consumer, int light, int overlay) {
//        MeshBuffer buffer = new MeshBuffer(DefaultVertexFormat.NEW_ENTITY);
//        Tesselator tes = Tesselator.getInstance();
//        BufferBuilder buffer = tes.getBuilder();
//        VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
//        int[] indices = ObjData.getFaceVertexIndicesArray(obj);
//        float[] vertices = ObjData.getVerticesArray(obj);
//        float[] texCoords = ObjData.getTexCoordsArray(obj, 2);
//        float[] normals = ObjData.getNormalsArray(obj);

        Matrix4f last = modelStack.last().pose();
        for (int index = 0; index < obj.getNumFaces(); index++) {
            ObjFace face = obj.getFace(index);
            for (int vtx = 0; vtx < face.getNumVertices(); vtx++) {
                FloatTuple pos = obj.getVertex(face.getVertexIndex(vtx));
                consumer.vertex(last, pos.getX(), pos.getY(), pos.getZ());
                consumer.color(255, 255, 255, 255);
                FloatTuple uv = obj.getTexCoord(face.getTexCoordIndex(vtx));
                consumer.uv(uv.getX(), 1 - uv.getY());

                consumer.overlayCoords(overlay);
                consumer.uv2(light);
                FloatTuple normals = obj.getNormal(face.getNormalIndex(vtx));
                consumer.normal(normals.getX(), normals.getY(), normals.getZ());
                consumer.endVertex();
            }
        }
//        int[] indices = ObjData.getFaceVertexIndicesArray(obj);
//        float[] vertices = ObjData.getVerticesArray(obj);
//        float[] texCoords = ObjData.getTexCoordsArray(obj, 2, true);
//        float[] normals = ObjData.getNormalsArray(obj);
//        buffer.buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.NEW_ENTITY);
//        for (int vtx = 0; vtx < vertices.length / 3; vtx++) {
//            buffer.vertex(vertices[vtx * 3], vertices[vtx * 3 + 1], vertices[vtx * 3 + 2]);
//            buffer.color(1, 1, 1, 1);
//            buffer.uv(texCoords[vtx * 2], texCoords[vtx * 2 + 1]);
//
//            buffer.overlayCoords(OverlayTexture.NO_OVERLAY);
//            buffer.uv2(LightTexture.FULL_BRIGHT);
//
//            buffer.normal(normals[vtx * 3], normals[vtx * 3 + 1], normals[vtx * 3 + 2]);
//            buffer.endVertex();
//        }

//        buffer.buffer.defaultColor(1, 1, 1, 1);
//        Matrix4f last = modelStack.last().pose();
//        for (int vtx = 0; vtx < vertices.length / 3; vtx++) {
//            buffer.vertex(last, vertices[vtx * 3], vertices[vtx * 3 + 1], vertices[vtx * 3 + 2]);
//            buffer.color(1, 1, 1, 1);
//            buffer.uv(texCoords[vtx * 2], texCoords[vtx * 2 + 1]);
//
//            buffer.overlayCoords(OverlayTexture.NO_OVERLAY);
//            buffer.uv2(LightTexture.FULL_BRIGHT);
//            buffer.normal(normals[vtx * 3], normals[vtx * 3 + 1], normals[vtx * 3 + 2]);
//            buffer.endVertex();
//        }

//        var indexBuffer = buffer.indicesBuffer;
//        for (int idx = 0; idx < indices.length / 3; idx++) {
//            indexBuffer.int3(
//                    indices[idx * 3],
//                    indices[idx * 3 + 1],
//                    indices[idx * 3 + 2]
//            );
//        }
//        buffer.upload(false);
//        this.buffer = buffer;
//        VertexBuffer.unbind();
//        this.buffer = buffer;
        this.compiled = true;
    }

    @Override
    public void render(PoseStack modelStack, VertexConsumer vertexConsumer, int light, int overlay) {
//        if (!compiled)
            compile(modelStack, vertexConsumer, light, overlay);
//        RenderSystem.enableDepthTest();
//        this.buffer.draw(modelStack, GameRenderer.getRendertypeSolidShader());
//        this.buffer.bind();
//        this.buffer.drawWithShader(modelStack.last().pose(), RenderSystem.getProjectionMatrix(), GameRenderer.getRendertypeEntitySolidShader());
//        VertexBuffer.unbind();
//        RenderSystem.disableDepthTest();
//        this.buffer.drawWithShader(modelStack.last().pose(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionTexColorNormalShader());
//        VertexBuffer.unbind();
    }

    @Override
    public void close() {
        if (buffer != null)
            buffer.delete();
    }
}
