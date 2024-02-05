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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dev.galacticraft.mod.Constant;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class FluidPipeWalkwayUnbakedModel implements UnbakedModel {
    public static final FluidPipeWalkwayUnbakedModel INSTANCE = new FluidPipeWalkwayUnbakedModel();
    private static final List<ResourceLocation> COLORED_FLUID_PIPE_WALKWAY = Util.make(Lists.newArrayList(), list -> list.addAll(Arrays.stream(DyeColor.values()).map(color -> Constant.id("block/" + color + "_fluid_pipe_walkway")).collect(ImmutableList.toImmutableList())));

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return COLORED_FLUID_PIPE_WALKWAY;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
    }

    @Override
    public BakedModel bake(ModelBaker loader, Function<Material, TextureAtlasSprite> spriteFunction, ModelState rotationContainer, ResourceLocation modelId) {
        return FluidPipeWalkwayBakedModel.getInstance(loader, spriteFunction, rotationContainer);
    }
}