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

import dev.galacticraft.mod.content.block.special.ParachestBlock;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ParachestBakedModel implements BakedModel, WrapperBakedModel {
    private final ParachestOverride itemOverride;
    private final BakedModel parent;
    private final Map<DyeColor, BakedModel> bakedChutes;
    private final DyeColor defaultColor;

    public ParachestBakedModel(BakedModel parent, Map<DyeColor, BakedModel> bakedChutes) {
        this(parent, bakedChutes, DyeColor.WHITE);
    }

    public ParachestBakedModel(BakedModel parent, Map<DyeColor, BakedModel> bakedChutes, DyeColor defaultColor) {
        this.parent = parent;
        this.bakedChutes = bakedChutes;
        this.defaultColor = defaultColor;
        this.itemOverride = new ParachestOverride();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        List<BakedQuad> quads = new ArrayList<>(parent.getQuads(blockState, direction, randomSource));
        if (blockState != null)
            quads.addAll(bakedChutes.get(blockState.getValue(ParachestBlock.COLOR)).getQuads(blockState, direction, randomSource));
        else
            quads.addAll(bakedChutes.get(defaultColor).getQuads(blockState, direction, randomSource));
        return quads;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BakedModel.super.emitItemQuads(stack, randomSupplier, context);
        this.bakedChutes.get(stack.getOrDefault(DataComponents.BASE_COLOR, defaultColor)).emitItemQuads(stack, randomSupplier, context);
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
        return itemOverride;
    }

    @Override
    public BakedModel getWrappedModel() {
        return parent;
    }

    public class ParachestOverride extends ItemOverrides {
        public ParachestOverride() {
            super(null, null, Collections.emptyList());
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            DyeColor dyeColor = stack.get(DataComponents.BASE_COLOR);
            if (dyeColor != null) {
                return new ParachestBakedModel(ParachestBakedModel.this.parent, ParachestBakedModel.this.bakedChutes, dyeColor);
            }
            return ParachestBakedModel.this.parent.getOverrides().resolve(model, stack, level, entity, seed);
        }
    }
}
