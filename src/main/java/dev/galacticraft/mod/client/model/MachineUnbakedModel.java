/*
 * Copyright (c) 2020 HRZN LTD
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
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.client.util.SpriteUtil;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class MachineUnbakedModel implements UnbakedModel {
    public static final MachineUnbakedModel INSTANCE = new MachineUnbakedModel();

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        ImmutableList.Builder<SpriteIdentifier> builder = ImmutableList.builder();
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_SIDE));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_POWER_IN));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_POWER_OUT));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_FLUID_IN));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_FLUID_OUT));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_ITEM_IN));
        builder.add(SpriteUtil.identifier(MachineBakedModel.MACHINE_ITEM_OUT));
        builder.addAll(SpriteUtil.identifiers(MachineBakedModel.TEXTURE_DEPENDENCIES));
        return builder.build();
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return MachineBakedModel.INSTANCE;
    }
}
