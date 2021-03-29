/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.model;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.client.util.SpriteUtil;
import com.mojang.datafixers.util.Pair;
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
        builder.add(SpriteUtil.identifier("block/machine"));
        builder.add(SpriteUtil.identifier("block/machine_side"));
        builder.add(SpriteUtil.identifier("block/machine_power_input"));
        builder.add(SpriteUtil.identifier("block/machine_power_output"));
        builder.add(SpriteUtil.identifier("block/machine_oxygen_input"));
        builder.add(SpriteUtil.identifier("block/machine_oxygen_output"));
        builder.add(SpriteUtil.identifier("block/machine_fluid_input"));
        builder.add(SpriteUtil.identifier("block/machine_fluid_output"));
        builder.add(SpriteUtil.identifier("block/machine_item_input"));
        builder.add(SpriteUtil.identifier("block/machine_item_output"));
        return builder.build();
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return GalacticraftMachineBakedModel.INSTANCE;
    }
}
