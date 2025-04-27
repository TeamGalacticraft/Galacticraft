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

import com.google.common.collect.Maps;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.block.entity.networked.FluidPipeWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.content.block.special.walkway.FluidPipeWalkway;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FluidPipeWalkwayBakedModel implements BakedModel {
    private static final ResourceLocation WALKWAY_TEXTURE = Constant.id("block/walkway");
    private static final Map<PipeColor, Material> COLOR_SPRITE_ID_MAP = Util.make(Maps.newEnumMap(PipeColor.class), map -> {
        for (var color : PipeColor.values()) {
            map.put(color, new Material(InventoryMenu.BLOCK_ATLAS, Constant.id("block/glass_fluid_pipe/" + color.getName())));
        }
    });
    private final PipeBakedModel pipeModel;
    private final Map<PipeColor, BakedModel> coloredWalkway = Maps.newHashMap();
    private final Map<PipeColor, TextureAtlasSprite> colorSpriteMap = Maps.newEnumMap(PipeColor.class);
    private final TextureAtlasSprite walkwaySprite;

    public FluidPipeWalkwayBakedModel(ModelBaker loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer, ResourceLocation pipeTexture) {
        this.walkwaySprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, WALKWAY_TEXTURE));
        for (var color : PipeColor.values()) {
            var pipeWalkway = Constant.id("block/" + color + "_fluid_pipe_walkway");
            this.coloredWalkway.put(color, loader.getModel(pipeWalkway).bake(loader, textureGetter, rotationContainer));
            this.colorSpriteMap.put(color, textureGetter.apply(COLOR_SPRITE_ID_MAP.get(color)));
        }
        var meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();

        this.pipeModel = new PipeBakedModel(textureGetter, pipeTexture);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter getter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var emitter = context.getEmitter();

        if (getter.getBlockEntity(blockPos) instanceof FluidPipeWalkwayBlockEntity pipe && blockState.getBlock() instanceof FluidPipeWalkway block) {
            var x = 0;
            var y = 0;
            var connections = pipe.getConnections();

            switch (pipe.getDirection()) {
                case DOWN -> x = 180;
                case NORTH -> x = 270;
                case SOUTH -> x = 90;
                case EAST -> {
                    x = 90;
                    y = 90;
                }
                case WEST -> {
                    x = 90;
                    y = 270;
                }
            }

            WalkwayBakedModel.Transform.INSTANCE.setQuaternions(Axis.XP.rotationDegrees(x), Axis.YP.rotationDegrees(y));
            context.pushTransform(WalkwayBakedModel.Transform.INSTANCE);
            this.coloredWalkway.get(block.color).emitBlockQuads(getter, blockState, blockPos, randomSupplier, context);
            context.popTransform();

            this.pipeModel.emitBlockQuads(getter, blockState, blockPos, randomSupplier, context);
        }
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<RandomSource> randomSupplier, RenderContext context) {
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.walkwaySprite;
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}