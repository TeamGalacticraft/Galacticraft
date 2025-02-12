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

package dev.galacticraft.mod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.javagl.obj.*;
import dev.galacticraft.mod.client.model.types.UnbakedObjModel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A Model rendered via a VBO.
 */
public class ObjModel implements GCModel {
    private final Obj obj;
    private final List<UnbakedObjModel.BakedMaterial> materials;

    public ObjModel(Obj obj, List<UnbakedObjModel.BakedMaterial> materials) {
        this.obj = ObjUtils.convertToRenderable(obj);
        this.materials = materials;
    }

    @Override
    public void render(PoseStack modelStack, @Nullable GCModelState state, VertexConsumer consumer, int light, int overlay, int color) {
        UnbakedObjModel.BakedMaterial lastMaterial = null;
        if (state == null) {
            for (int index = 0; index < obj.getNumFaces(); index++) {
                ObjFace face = obj.getFace(index);
                lastMaterial = renderFace(lastMaterial, face, consumer, modelStack, light, overlay, color);
            }
        } else {
            ObjGroup group = obj.getGroup(state.getName());
            for (int index = 0; index < group.getNumFaces(); index++) {
                ObjFace face = group.getFace(index);
                lastMaterial = renderFace(lastMaterial, face, consumer, modelStack, light, overlay, color);
            }
        }
    }

    protected UnbakedObjModel.BakedMaterial renderFace(UnbakedObjModel.BakedMaterial lastMaterial, ObjFace face, VertexConsumer consumer, PoseStack matrices, int light, int overlay, int color) {
        UnbakedObjModel.BakedMaterial material = findMaterial(this.obj.getActivatedMaterialGroupName(face), lastMaterial, materials);
        consumer = (material != null ? material.sprite() : GCModelLoader.INSTANCE.getDefaultSprite()).wrap(consumer);
        PoseStack.Pose last = matrices.last();
        for (int vtx = 0; vtx < face.getNumVertices(); vtx++) {
            FloatTuple pos = obj.getVertex(face.getVertexIndex(vtx));
            consumer.addVertex(last.pose(), pos.getX(), pos.getY(), pos.getZ());
            consumer.setColor(color);
            FloatTuple uv = obj.getTexCoord(face.getTexCoordIndex(vtx));
            consumer.setUv(uv.getX(), 1 - uv.getY());

            consumer.setOverlay(overlay);
            consumer.setLight(light);
            FloatTuple normals = obj.getNormal(face.getNormalIndex(vtx));
            consumer.setNormal(last, normals.getX(), normals.getY(), normals.getZ());
        }

        return material;
    }

    public static UnbakedObjModel.BakedMaterial findMaterial(String name, UnbakedObjModel.BakedMaterial lastMaterial, List<UnbakedObjModel.BakedMaterial> materials) {
        for (UnbakedObjModel.BakedMaterial material : materials)
            if (material.material().getName().equals(name))
                return material;
        return lastMaterial;
    }

    @Override
    public void close() {}
}
