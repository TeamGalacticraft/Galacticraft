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

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.ParaChestBlock;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record ParaChestBakedModel(BakedModel parent, Map<DyeColor, BakedModel> bakedChutes) implements BakedModel, WrapperBakedModel {
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        List<BakedQuad> quads = new ArrayList<>(parent.getQuads(blockState, direction, randomSource));
        if (blockState != null)
            quads.addAll(bakedChutes.get(blockState.getValue(ParaChestBlock.COLOR)).getQuads(blockState, direction, randomSource));
        return quads;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BakedModel.super.emitItemQuads(stack, randomSupplier, context);
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null) {
            CompoundTag blockStateTag = compoundTag.getCompound("BlockStateTag");
            StateDefinition<Block, BlockState> stateDefinition = GCBlocks.PARACHEST.getStateDefinition();


                Property<?> property = stateDefinition.getProperty("color");
                if (property != null) {
                    property.getValue(blockStateTag.getString("color")).ifPresent(color -> {
                        bakedChutes.get(color).emitItemQuads(stack, randomSupplier, context);
                    });
                }
        } else {
            bakedChutes.get(DyeColor.WHITE).emitItemQuads(stack, randomSupplier, context);
        }
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return parent.usesBlockLight();
    }

    @Override
    public boolean isGui3d() {
        return parent.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return parent.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return parent.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return parent.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return parent.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return parent.getOverrides();
    }

    @Override
    public BakedModel getWrappedModel() {
        return parent;
    }

    public class ParaChestOverride extends ItemOverrides {

        public ParaChestOverride(ModelBaker modelBaker, BlockModel blockModel, List<ItemOverride> list) {
            super(modelBaker, blockModel, list);
        }
    }
}
