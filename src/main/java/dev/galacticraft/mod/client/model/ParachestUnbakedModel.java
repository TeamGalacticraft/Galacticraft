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

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.*;
import java.util.function.Function;

public record ParachestUnbakedModel(UnbakedModel parentModel, Map<DyeColor, UnbakedModel> chutes) implements UnbakedModel {

    @Override
    public Collection<ResourceLocation> getDependencies() {
        List<ResourceLocation> depends = new ArrayList<>(parentModel.getDependencies());
        chutes.forEach((color, unbakedModel) -> {
            depends.addAll(unbakedModel.getDependencies());
        });
        return depends;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
        parentModel.resolveParents(function);
        chutes.forEach((color, unbakedModel) -> {
            unbakedModel.resolveParents(function);
        });
    }

    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState state) {
        Map<DyeColor, BakedModel> bakedChutes = new HashMap<>();
        chutes.forEach((color, unbakedModel) -> {
            bakedChutes.put(color, unbakedModel.bake(baker, textureGetter, state));
        });
        return new ParachestBakedModel(parentModel.bake(baker, textureGetter, state), bakedChutes);
    }
}
