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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.BakedObjModel;
import dev.galacticraft.mod.client.model.GCBakedModel;
import dev.galacticraft.mod.client.model.GCModel;
import dev.galacticraft.mod.client.model.GCSheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ObjModel implements GCModel {
    public static final ObjType TYPE = new ObjType();

    private final ResourceLocation model;
    private final ResourceLocation material;
    private final Optional<ResourceLocation> atlas;

    public ObjModel(ResourceLocation model, ResourceLocation material, Optional<ResourceLocation> atlas) {
        this.model = model;
        this.material = material;
        this.atlas = atlas;
    }

    @Override
    public GCModelType getType() {
        return TYPE;
    }

    @Override
    public GCBakedModel bake(ResourceManager resourceManager, Function<Material, TextureAtlasSprite> spriteGetter) {
        try {
            Obj obj = ObjReader.read(resourceManager.getResourceOrThrow(model).open());
            List<Mtl> materials = MtlReader.read(resourceManager.getResourceOrThrow(material).open());
            List<BakedMaterial> bakedMaterials = new ArrayList<>();
            for (Mtl material : materials) {
                if (material.getMapKdOptions() != null && material.getMapKdOptions().getFileName() != null)
                    bakedMaterials.add(new BakedMaterial(material, spriteGetter.apply(new Material(atlas.orElse(GCSheets.OBJ_ATLAS), new ResourceLocation(material.getMapKdOptions().getFileName())))));
            }

            return new BakedObjModel(obj, bakedMaterials);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record BakedMaterial(Mtl material, TextureAtlasSprite sprite) {}

    public static class ObjType implements GCModelType {
        public static final ResourceLocation ID = Constant.id("obj");
        public static final Codec<ObjModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(o -> o.model),
                ResourceLocation.CODEC.fieldOf("mtl").forGetter(o -> o.material),
                ResourceLocation.CODEC.optionalFieldOf("atlas").forGetter(o -> o.atlas)
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
