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

package dev.galacticraft.mod.client.model.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.javagl.obj.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.*;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UnbakedObjModel implements GCUnbakedModel {
    public static final ObjType TYPE = new ObjType();

    private final ResourceLocation model;
    private final ResourceLocation material;
    private final Optional<ResourceLocation> atlas;

    public UnbakedObjModel(ResourceLocation model, ResourceLocation material, Optional<ResourceLocation> atlas) {
        this.model = model;
        this.material = material;
        this.atlas = atlas;
    }

    @Override
    public GCModelType getType() {
        return TYPE;
    }

    @Override
    public GCModel bake(ResourceManager resourceManager, Function<Material, TextureAtlasSprite> spriteGetter) {
        try {
            Obj obj = ObjReader.read(resourceManager.getResourceOrThrow(model).open());
            List<Mtl> materials = MtlReader.read(resourceManager.getResourceOrThrow(material).open());
            List<BakedMaterial> bakedMaterials = new ArrayList<>();
            for (Mtl material : materials) {
                if (material.getMapKdOptions() != null && material.getMapKdOptions().getFileName() != null)
                    bakedMaterials.add(new BakedMaterial(material, spriteGetter.apply(new Material(atlas.orElse(GCRenderTypes.OBJ_ATLAS), ResourceLocation.parse(material.getMapKdOptions().getFileName())))));
            }

            return new ObjModel(obj, bakedMaterials);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BakedObjModel bakeVanillaModel(ResourceManager resourceManager, Function<Material, TextureAtlasSprite> spriteGetter, TextureAtlasSprite particle) {
        try {
            Obj obj = ObjUtils.convertToRenderable(ObjReader.read(resourceManager.getResourceOrThrow(model).open()));
            List<Mtl> materials = MtlReader.read(resourceManager.getResourceOrThrow(material).open());
            List<BakedMaterial> bakedMaterials = new ArrayList<>();
            for (Mtl material : materials) {
                if (material.getMapKdOptions() != null && material.getMapKdOptions().getFileName() != null)
                    bakedMaterials.add(new BakedMaterial(material, spriteGetter.apply(new Material(atlas.orElse(GCRenderTypes.OBJ_ATLAS), ResourceLocation.parse(material.getMapKdOptions().getFileName())))));
            }

            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            MeshBuilder builder = renderer.meshBuilder();
            QuadEmitter emitter = builder.getEmitter();
            BakedMaterial lastMaterial = null;

            for (int index = 0; index < obj.getNumFaces(); index++) {
                ObjFace face = obj.getFace(index);
                BakedMaterial material = ObjModel.findMaterial(obj.getActivatedMaterialGroupName(face), lastMaterial, bakedMaterials);
                for (int i = 0; i < 4; i++) {
                    int vtx = Math.min(i, face.getNumVertices() - 1);
                    FloatTuple pos = obj.getVertex(face.getVertexIndex(vtx));
                    emitter.pos(i, pos.getX(), pos.getY(), pos.getZ());
                    emitter.color(i, FastColor.ARGB32.color(255, 255, 255));

                    TextureAtlasSprite sprite = material.sprite();

                    emitter.spriteBake(sprite, QuadEmitter.BAKE_ROTATE_NONE);

                    FloatTuple uv = obj.getTexCoord(face.getTexCoordIndex(vtx));

                    emitter.uv(i, sprite.getU(uv.getX()), sprite.getV(uv.getY()));

                    FloatTuple normals = obj.getNormal(face.getNormalIndex(vtx));
                    emitter.normal(i, normals.getX(), normals.getY(), normals.getZ());
                }
                emitter.emit();
                lastMaterial = material;
            }

            Mesh mesh = builder.build();


            // Legacy code for mods that use this...
            List<BakedQuad> quads = new ArrayList<>();
            mesh.forEach(quadView -> {
                quads.add(quadView.toBakedQuad(particle));
            });

            return new BakedObjModel(mesh, quads, particle);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record BakedMaterial(Mtl material, TextureAtlasSprite sprite) {}

    public static class ObjType implements GCModelType {
        public static final ResourceLocation ID = Constant.id("obj");
        public static final MapCodec<UnbakedObjModel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(o -> o.model),
                ResourceLocation.CODEC.fieldOf("mtl").forGetter(o -> o.material),
                ResourceLocation.CODEC.optionalFieldOf("atlas").forGetter(o -> o.atlas)
        ).apply(instance, UnbakedObjModel::new));

        @Override
        public MapCodec<? extends GCUnbakedModel> codec() {
            return CODEC;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
