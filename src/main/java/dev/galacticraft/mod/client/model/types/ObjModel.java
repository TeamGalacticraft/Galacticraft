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

package dev.galacticraft.mod.client.model.types;

import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.GCBakedModel;
import dev.galacticraft.mod.client.model.GCModel;
import dev.galacticraft.mod.client.model.VertexBufferModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ObjModel implements GCModel {
    public static final ObjType TYPE = new ObjType();

    private final ResourceLocation model;

    public ObjModel(ResourceLocation model) {
        this.model = model;
    }

    @Override
    public GCModelType getType() {
        return TYPE;
    }

    @Override
    public GCBakedModel bake(ResourceManager resourceManager) {
        try {
//            VertexBuffer vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
//            Tesselator tesselator = Tesselator.getInstance();
//            BufferBuilder buffer = tesselator.getBuilder();

            Obj obj = ObjReader.read(resourceManager.getResourceOrThrow(model).open());
//            int[] indices = ObjData.getFaceVertexIndicesArray(obj);
//            float[] vertices = ObjData.getVerticesArray(obj);
//            float[] texCoords = ObjData.getTexCoordsArray(obj, 2);
//            float[] normals = ObjData.getNormalsArray(obj);
//
//            buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
//            for (int vtx = 0; vtx < vertices.length; vtx += 3) {
//                buffer.vertex(vertices[vtx], vertices[vtx + 1], vertices[vtx + 2]).uv(texCoords[vtx / 3], texCoords[vtx / 3 + 1]).color(1.0F, 1.0F, 1.0F, 1.0F)/*.normal(normals[vtx * 3], normals[vtx * 3 + 1], normals[vtx * 3 + 2])*/.endVertex();
//            }
//            vbo.upload(buffer.end());
//
//            BufferWriter in = mesh.getIndicesBuffer();
//
//            for (int i = 0; i < indices.length; i += 3) {
//                in.int3(indices[i], indices[i + 1], indices[i + 2]);
//            }
            return new VertexBufferModel(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ObjType implements GCModelType {
        public static final ResourceLocation ID = Constant.id("obj");
        public static final Codec<ObjModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(o -> o.model)
        ).apply(instance, ObjModel::new));

        @Override
        public Codec<ObjModel> codec() {
            return CODEC;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
