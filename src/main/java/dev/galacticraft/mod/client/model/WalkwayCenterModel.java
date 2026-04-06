/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.Connected;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WalkwayCenterModel implements UnbakedModel {
    private final ResourceLocation texture;
        private final float radius;
        private final float yOffset;

    public WalkwayCenterModel(ResourceLocation texture) {
                this(texture, 0.125f);
        }

        public WalkwayCenterModel(ResourceLocation texture, float radius) {
                this(texture, radius, 0.0f);
        }

        public WalkwayCenterModel(ResourceLocation texture, float radius, float yOffset) {
        this.texture = texture;
                this.radius = radius;
                this.yOffset = yOffset;
    }

    @Override
    public @NotNull Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelLoader) {

    }

    @Override
    public @Nullable BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer) {
                return new Baked(textureGetter, this.texture, this.radius, this.yOffset);
    }

    public static class Baked implements BakedModel {
        private final TextureAtlasSprite sprite;
        private final Map<Direction, Mesh> meshes;
                private final float yOffset;

                public Baked(Function<Material, TextureAtlasSprite> textureGetter, ResourceLocation texture, float radius, float yOffset) {
            this.sprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, texture));
            this.meshes = new EnumMap<>(Direction.class);
                        this.yOffset = yOffset;

                        float min = 0.5f - radius;
                        float max = 0.5f + radius;
                        float edgeMin = 0.0625f;
                        float edgeMax = 0.9375f;

            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            MeshBuilder meshBuilder = renderer.meshBuilder();
            QuadEmitter emitter = meshBuilder.getEmitter();

            for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                emitter
                                                .square(direction, min, edgeMin, max, min, min)
                        .uv(0, 0, 10)
                        .uv(1, 0, 15)
                        .uv(2, 4, 15)
                        .uv(3, 4, 10)
                        .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                        .color(-1, -1, -1, -1).emit();
            }
            emitter.square(Direction.DOWN, min, min, max, max, edgeMin)
                    .uv(0, 0, 6)
                    .uv(1, 0, 10)
                    .uv(2, 4, 10)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            this.meshes.put(Direction.DOWN, meshBuilder.build());

            for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                emitter
                                                .square(direction, min, max, max, edgeMax, min)
                        .uv(0, 0, 1)
                        .uv(1, 0, 6)
                        .uv(2, 4, 6)
                        .uv(3, 4, 1)
                        .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                        .color(-1, -1, -1, -1).emit();
            }
            emitter.square(Direction.UP, min, min, max, max, edgeMin)
                    .uv(0, 0, 6)
                    .uv(1, 0, 10)
                    .uv(2, 4, 10)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            this.meshes.put(Direction.UP, meshBuilder.build());

            emitter
                    .square(Direction.WEST, edgeMin, min, min, max, min)
                    .uv(0, 0, 1)
                    .uv(1, 4, 1)
                    .uv(2, 4, 6)
                    .uv(3, 0, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.UP, min, max, max, edgeMax, min)
                    .uv(0, 0, 1)
                    .uv(1, 0, 6)
                    .uv(2, 4, 6)
                    .uv(3, 4, 1)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.EAST, max, min, edgeMax, max, min)
                    .uv(0, 0, 10)
                    .uv(1, 4, 10)
                    .uv(2, 4, 15)
                    .uv(3, 0, 15)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.DOWN, min, edgeMin, max, min, min)
                    .uv(0, 0, 10)
                    .uv(1, 0, 15)
                    .uv(2, 4, 15)
                    .uv(3, 4, 10)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter.square(Direction.NORTH, min, min, max, max, edgeMin)
                    .uv(0, 0, 6)
                    .uv(1, 0, 10)
                    .uv(2, 4, 10)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            this.meshes.put(Direction.NORTH, meshBuilder.build());

            emitter
                    .square(Direction.WEST, max, min, edgeMax, max, min)
                    .uv(0, 0, 10)
                    .uv(1, 4, 10)
                    .uv(2, 4, 15)
                    .uv(3, 0, 15)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.EAST, edgeMin, min, min, max, min)
                    .uv(0, 0, 1)
                    .uv(1, 4, 1)
                    .uv(2, 4, 6)
                    .uv(3, 0, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.UP, min, edgeMin, max, min, min)
                    .uv(0, 0, 10)
                    .uv(1, 0, 15)
                    .uv(2, 4, 15)
                    .uv(3, 4, 10)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.DOWN, min, max, max, edgeMax, min)
                    .uv(0, 0, 1)
                    .uv(1, 0, 6)
                    .uv(2, 4, 6)
                    .uv(3, 4, 1)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter.square(Direction.SOUTH, min, min, max, max, edgeMin)
                    .uv(0, 0, 6)
                    .uv(1, 0, 10)
                    .uv(2, 4, 10)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            this.meshes.put(Direction.SOUTH, meshBuilder.build());

            emitter
                    .square(Direction.NORTH, edgeMin, min, min, max, min)
                    .uv(0, 0, 1)
                    .uv(1, 4, 1)
                    .uv(2, 4, 6)
                    .uv(3, 0, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.SOUTH, 0.625f, 0.375f,0.9375f, 0.625f, 0.375f)
                    .uv(0, 0, 10)
                    .uv(1, 4, 10)
                    .uv(2, 4, 15)
                    .uv(3, 0, 15)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.UP, 0.625f, 0.375f,0.9375f, 0.625f, 0.375f)
                    .uv(0, 4, 10)
                    .uv(1, 0, 10)
                    .uv(2, 0, 15)
                    .uv(3, 4, 15)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.DOWN, 0.625f, 0.375f,0.9375f, 0.625f, 0.375f)
                    .uv(0, 4, 10)
                    .uv(1, 0, 10)
                    .uv(2, 0, 15)
                    .uv(3, 4, 15)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter.square(Direction.EAST, 0.375f, 0.375f, 0.625f, 0.625f, 0.0625f)
                    .uv(0, 0, 6)
                    .uv(1, 0, 10)
                    .uv(2, 4, 10)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            this.meshes.put(Direction.EAST, meshBuilder.build());

            emitter
                    .square(Direction.NORTH, 0.625f, 0.375f,0.9375f, 0.625f, 0.375f)
                    .uv(0, 0, 10)
                    .uv(1, 4, 10)
                    .uv(2, 4, 15)
                    .uv(3, 0, 15)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.SOUTH, 0.0625f, 0.375f, 0.375f, 0.625f, 0.375f)
                    .uv(0, 0, 1)
                    .uv(1, 4, 1)
                    .uv(2, 4, 6)
                    .uv(3, 0, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.UP, 0.0625f, 0.375f, 0.375f, 0.625f, 0.375f)
                    .uv(0, 4, 1)
                    .uv(1, 0, 1)
                    .uv(2, 0, 6)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter
                    .square(Direction.DOWN, 0.0625f, 0.375f, 0.375f, 0.625f, 0.375f)
                    .uv(0, 4, 1)
                    .uv(1, 0, 1)
                    .uv(2, 0, 6)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            emitter.square(Direction.WEST, 0.375f, 0.375f, 0.625f, 0.625f, 0.0625f)
                    .uv(0, 0, 6)
                    .uv(1, 0, 10)
                    .uv(2, 4, 10)
                    .uv(3, 4, 6)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
            this.meshes.put(Direction.WEST, meshBuilder.build());
        }

        @Override
        public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
                        if (this.yOffset != 0.0f) {
                                context.pushTransform(quad -> {
                                        for (int vertex = 0; vertex < 4; vertex++) {
                                                quad.pos(vertex, quad.x(vertex), quad.y(vertex) + this.yOffset, quad.z(vertex));
                                        }
                                        return true;
                                });
                        }

            if (blockView.getBlockEntity(pos) instanceof Connected connected) {
                Direction facing = state.getValue(BlockStateProperties.FACING);
                if (!connected.isConnected(facing)) {
                    this.meshes.get(facing).outputTo(context.getEmitter());
                }
            } else {
                Constant.LOGGER.warn("Walkway center model loaded for block that's not a Connected entity");
            }

                        if (this.yOffset != 0.0f) {
                                context.popTransform();
                        }
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource randomSource) {
            return List.of();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return null;
        }

        @Override
        public ItemTransforms getTransforms() {
            return null;
        }

        @Override
        public ItemOverrides getOverrides() {
            return null;
        }
    }
}
