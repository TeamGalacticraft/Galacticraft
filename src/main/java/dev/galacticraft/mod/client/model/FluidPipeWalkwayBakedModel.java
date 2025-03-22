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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FluidPipeWalkwayBakedModel implements BakedModel {
    private static FluidPipeWalkwayBakedModel instance = null;
    private static final ResourceLocation WALKWAY_TEXTURE = Constant.id("block/walkway");
    private static final Map<PipeColor, Material> COLOR_SPRITE_ID_MAP = Util.make(Maps.newEnumMap(PipeColor.class), map -> {
        for (var color : PipeColor.values()) {
            map.put(color, new Material(InventoryMenu.BLOCK_ATLAS, Constant.id("block/glass_fluid_pipe/" + color.getName())));
        }
    });
    private final Map<PipeColor, BakedModel> coloredWalkway = Maps.newHashMap();
    private final Map<PipeColor, TextureAtlasSprite> colorSpriteMap = Maps.newEnumMap(PipeColor.class);
    private final Mesh down;
    private final Mesh up;
    private final Mesh north;
    private final Mesh south;
    private final Mesh west;
    private final Mesh east;
    private final TextureAtlasSprite walkwaySprite;

    protected FluidPipeWalkwayBakedModel(ModelBaker loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer) {
        this.walkwaySprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, WALKWAY_TEXTURE));
        for (var color : PipeColor.values()) {
            var pipeWalkway = Constant.id("block/" + color + "_fluid_pipe_walkway");
            this.coloredWalkway.put(color, loader.getModel(pipeWalkway).bake(loader, textureGetter, rotationContainer));
            this.colorSpriteMap.put(color, textureGetter.apply(COLOR_SPRITE_ID_MAP.get(color)));
        }
        var meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        var emitter = meshBuilder.getEmitter();
        emitter.square(Direction.DOWN, 0.4f, 0.4f, 0.6f, 0.6f, 0.0f).color(-1, -1, -1, -1).uv(1, 12, 0).uv(2, 16, 0).uv(3, 16, 4).uv(0, 12, 4).cullFace(Direction.DOWN).emit();
        emitter.square(Direction.WEST, 0.6f, 0.4f, 0.4f, 0.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 0, 0).uv(2, 4, 0).uv(3, 4, 8).uv(0, 0, 8).emit();
        emitter.square(Direction.EAST, 0.6f, 0.4f, 0.4f, 0.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 0).uv(2, 8, 0).uv(3, 8, 8).uv(0, 4, 8).emit();
        emitter.square(Direction.SOUTH, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 0).uv(2, 12, 0).uv(3, 12, 8).uv(0, 8, 8).emit();
        emitter.square(Direction.NORTH, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 0).uv(2, 8, 0).uv(3, 8, 8).uv(0, 4, 8).emit();
        this.down = meshBuilder.build();
        emitter.square(Direction.UP, 0.4f, 0.4f, 0.6f, 0.6f, 0.0f).color(-1, -1, -1, -1).uv(1, 12, 0).uv(2, 16, 0).uv(3, 16, 4).uv(0, 12, 4).cullFace(Direction.UP).emit();
        emitter.square(Direction.EAST, 0.6f, 1.0f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(1, 0, 8).uv(2, 4, 8).uv(3, 4, 16).uv(0, 0, 16).emit();
        emitter.square(Direction.WEST, 0.6f, 1.0f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 8).uv(2, 8, 8).uv(3, 8, 16).uv(0, 4, 16).emit();
        emitter.square(Direction.NORTH, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 8).uv(2, 12, 8).uv(3, 12, 16).uv(0, 8, 16).emit();
        emitter.square(Direction.SOUTH, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 8).uv(2, 8, 8).uv(3, 8, 16).uv(0, 4, 16).emit();
        this.up = meshBuilder.build();
        emitter.square(Direction.NORTH, 0.4f, 0.4f, 0.6f, 0.6f, 0.0f).color(-1, -1, -1, -1).uv(1, 12, 0).uv(2, 16, 0).uv(3, 16, 4).uv(0, 12, 4).cullFace(Direction.NORTH).emit();
        emitter.square(Direction.WEST, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 0).uv(1, 4, 0).uv(2, 4, 8).uv(3, 0, 8).emit();
        emitter.square(Direction.EAST, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 0).uv(1, 8, 0).uv(2, 8, 8).uv(3, 4, 8).emit();
        emitter.square(Direction.DOWN, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 0).uv(2, 12, 0).uv(3, 12, 8).uv(0, 8, 8).emit();
        emitter.square(Direction.UP, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 0).uv(2, 8, 0).uv(3, 8, 8).uv(0, 4, 8).emit();
        this.north = meshBuilder.build();
        emitter.square(Direction.SOUTH, 0.4f, 0.4f, 0.6f, 0.6f, 0.0f).color(-1, -1, -1, -1).uv(1, 12, 0).uv(2, 16, 0).uv(3, 16, 4).uv(0, 12, 4).cullFace(Direction.SOUTH).emit();
        emitter.square(Direction.EAST, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 8).uv(1, 4, 8).uv(2, 4, 16).uv(3, 0, 16).emit();
        emitter.square(Direction.WEST, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 8).uv(1, 8, 8).uv(2, 8, 16).uv(3, 4, 16).emit();
        emitter.square(Direction.UP, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 8).uv(2, 12, 8).uv(3, 12, 16).uv(0, 8, 16).emit();
        emitter.square(Direction.DOWN, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 8).uv(2, 8, 8).uv(3, 8, 16).uv(0, 4, 16).emit();
        this.south = meshBuilder.build();
        emitter.square(Direction.WEST, 0.4f, 0.4f, 0.6f, 0.6f, 0.0f).color(-1, -1, -1, -1).uv(0, 12, 0).uv(1, 16, 0).uv(2, 16, 4).uv(3, 12, 4).cullFace(Direction.WEST).emit();
        emitter.square(Direction.NORTH, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 0).uv(1, 4, 0).uv(2, 4, 8).uv(3, 0, 8).emit();
        emitter.square(Direction.SOUTH, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 0).uv(1, 8, 0).uv(2, 8, 8).uv(3, 4, 8).emit();
        emitter.square(Direction.UP, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 8, 0).uv(1, 12, 0).uv(2, 12, 8).uv(3, 8, 8).emit();
        emitter.square(Direction.DOWN, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 0).uv(1, 8, 0).uv(2, 8, 8).uv(3, 4, 8).emit();
        this.west = meshBuilder.build();
        emitter.square(Direction.EAST, 0.4f, 0.4f, 0.6f, 0.6f, 0.0f).color(-1, -1, -1, -1).uv(0, 12, 0).uv(1, 16, 0).uv(2, 16, 4).uv(3, 12, 4).cullFace(Direction.EAST).emit();
        emitter.square(Direction.SOUTH, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 8).uv(1, 4, 8).uv(2, 4, 16).uv(3, 0, 16).emit();
        emitter.square(Direction.NORTH, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 8).uv(1, 8, 8).uv(2, 8, 16).uv(3, 4, 16).emit();
        emitter.square(Direction.DOWN, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 8, 8).uv(1, 12, 8).uv(2, 12, 16).uv(3, 8, 16).emit();
        emitter.square(Direction.UP, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 8).uv(1, 8, 8).uv(2, 8, 16).uv(3, 4, 16).emit();
        this.east = meshBuilder.build();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter getter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var emitter = context.getEmitter();

        if (getter.getBlockEntity(blockPos) instanceof FluidPipeWalkwayBlockEntity pipe) {
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
            this.coloredWalkway.get(pipe.getColor()).emitBlockQuads(getter, blockState, blockPos, randomSupplier, context);
            context.popTransform();

            PipeBakedModel.ColorTransform.INSTANCE.setSprite(this.colorSpriteMap.get(pipe.getColor()));
            context.pushTransform(PipeBakedModel.ColorTransform.INSTANCE);
            this.emitBlockQuadsDirection(emitter, connections, this.down, Direction.DOWN);
            this.emitBlockQuadsDirection(emitter, connections, this.up, Direction.UP);
            this.emitBlockQuadsDirection(emitter, connections, this.north, Direction.NORTH);
            this.emitBlockQuadsDirection(emitter, connections, this.south, Direction.SOUTH);
            this.emitBlockQuadsDirection(emitter, connections, this.west, Direction.WEST);
            this.emitBlockQuadsDirection(emitter, connections, this.east, Direction.EAST);
            context.popTransform();
        }
    }

    private void emitBlockQuadsDirection(QuadEmitter emitter, boolean[] connections, Mesh mesh, Direction direction) {
        if (connections[direction.get3DDataValue()]) {
            mesh.outputTo(emitter);
        }
        else {
            emitter.square(direction, 0.4f, 0.4f, 0.6f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 12, 0).uv(1, 16, 0).uv(2, 16, 4).uv(3, 12, 4).emit();
        }
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<RandomSource> randomSupplier, RenderContext context) {
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource random) {
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
    public TextureAtlasSprite getParticleIcon() {
        return this.walkwaySprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    public static FluidPipeWalkwayBakedModel getInstance(ModelBaker loader, Function<Material, TextureAtlasSprite> spriteFunction, ModelState rotationContainer) {
        if (instance == null) {
            return instance = new FluidPipeWalkwayBakedModel(loader, spriteFunction, rotationContainer);
        }
        return instance;
    }

    public static void invalidate() {
        instance = null;
    }
}