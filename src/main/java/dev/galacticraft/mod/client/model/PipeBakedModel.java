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

import dev.galacticraft.mod.api.block.entity.Connected;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
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

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PipeBakedModel implements BakedModel {
    private final TextureAtlasSprite sprite;
    private final Map<Direction, Mesh> meshes;
    private final float radius;

    public PipeBakedModel(Function<Material, TextureAtlasSprite> textureGetter, ResourceLocation texture, float radius) {
        this.sprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, texture));
        this.meshes = new EnumMap<>(Direction.class);
        this.radius = radius;

        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();

        for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            emitter
                    .square(direction, 0.5f-radius, 0.0f, 0.5f+radius, 0.5f-radius, 0.5f-radius)
                    .uv(0, 0, 10)
                    .uv(1, 0, 16)
                    .uv(2, 4, 16)
                    .uv(3, 4, 10)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
        }
        this.meshes.put(Direction.DOWN, meshBuilder.build());

        for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            emitter
                    .square(direction, 0.5f-radius, 0.5f+radius, 0.5f+radius, 1.0f, 0.5f-radius)
                    .uv(0, 0, 0)
                    .uv(1, 0, 6)
                    .uv(2, 4, 6)
                    .uv(3, 4, 0)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
        }
        this.meshes.put(Direction.UP, meshBuilder.build());

        emitter
                .square(Direction.WEST, 0, 0.5f-radius, 0.5f-radius, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 0)
                .uv(1, 4, 0)
                .uv(2, 4, 6)
                .uv(3, 0, 6)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, 0.5f-radius, 0.5f+radius, 0.5f+radius, 1, 0.5f-radius)
                .uv(0, 0, 0)
                .uv(1, 0, 6)
                .uv(2, 4, 6)
                .uv(3, 4, 0)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.EAST, 0.5f+radius, 0.5f-radius, 1, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 10)
                .uv(1, 4, 10)
                .uv(2, 4, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, 0.5f-radius, 0, 0.5f+radius, 0.5f-radius, 0.5f-radius)
                .uv(0, 0, 10)
                .uv(1, 0, 16)
                .uv(2, 4, 16)
                .uv(3, 4, 10)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.NORTH, meshBuilder.build());

        emitter
                .square(Direction.WEST, 0.5f+radius, 0.5f-radius, 1, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 10)
                .uv(1, 4, 10)
                .uv(2, 4, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.EAST, 0, 0.5f-radius, 0.5f-radius, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 0)
                .uv(1, 4, 0)
                .uv(2, 4, 6)
                .uv(3, 0, 6)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, 0.5f-radius, 0, 0.5f+radius, 0.5f-radius, 0.5f-radius)
                .uv(0, 0, 10)
                .uv(1, 0, 16)
                .uv(2, 4, 16)
                .uv(3, 4, 10)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, 0.5f-radius, 0.5f+radius, 0.5f+radius, 1, 0.5f-radius)
                .uv(0, 0, 0)
                .uv(1, 0, 6)
                .uv(2, 4, 6)
                .uv(3, 4, 0)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.SOUTH, meshBuilder.build());

        emitter
                .square(Direction.NORTH, 0, 0.5f-radius, 0.5f-radius, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 0)
                .uv(1, 4, 0)
                .uv(2, 4, 6)
                .uv(3, 0, 6)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.SOUTH, 0.5f+radius, 0.5f-radius, 1, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 10)
                .uv(1, 4, 10)
                .uv(2, 4, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, 0.5f+radius, 0.5f-radius, 1, 0.5f+radius, 0.5f-radius)
                .uv(0, 4, 10)
                .uv(1, 0, 10)
                .uv(2, 0, 16)
                .uv(3, 4, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, 0.5f+radius, 0.5f-radius, 1, 0.5f+radius, 0.5f-radius)
                .uv(0, 4, 10)
                .uv(1, 0, 10)
                .uv(2, 0, 16)
                .uv(3, 4, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.EAST, meshBuilder.build());

        emitter
                .square(Direction.NORTH, 0.5f+radius, 0.5f-radius, 1, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 10)
                .uv(1, 4, 10)
                .uv(2, 4, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.SOUTH, 0, 0.5f-radius, 0.5f-radius, 0.5f+radius, 0.5f-radius)
                .uv(0, 0, 0)
                .uv(1, 4, 0)
                .uv(2, 4, 6)
                .uv(3, 0, 6)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, 0, 0.5f-radius, 0.5f-radius, 0.5f+radius, 0.5f-radius)
                .uv(0, 4, 0)
                .uv(1, 0, 0)
                .uv(2, 0, 6)
                .uv(3, 4, 6)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, 0, 0.5f-radius, 0.5f-radius, 0.5f+radius, 0.5f-radius)
                .uv(0, 4, 0)
                .uv(1, 0, 0)
                .uv(2, 0, 6)
                .uv(3, 4, 6)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.WEST, meshBuilder.build());
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter getter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var emitter = context.getEmitter();

        if (getter.getBlockEntity(blockPos) instanceof Connected pipe) {
            for (Direction direction : Direction.values()) {
                if (pipe.isConnected(direction)) {
                    this.meshes.get(direction).outputTo(emitter);
                } else {
                    emitter
                        .square(direction, 0.5f-this.radius, 0.5f-this.radius, 0.5f+this.radius, 0.5f+this.radius, 0.5f-this.radius)
                        .uv(0, 0, 6)
                        .uv(1, 0, 10)
                        .uv(2, 4, 10)
                        .uv(3, 4, 6)
                        .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                        .color(-1, -1, -1, -1).emit();
                }
            }
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
        return this.sprite;
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    public enum ColorTransform implements RenderContext.QuadTransform {
        INSTANCE;
        private TextureAtlasSprite sprite = null;

        public void setSprite(TextureAtlasSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            quad.spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV);
            return true;
        }
    }
}