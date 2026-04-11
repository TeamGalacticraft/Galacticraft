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

import dev.galacticraft.mod.api.block.PipeShapedBlock;
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

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class PipeBakedModel implements BakedModel {
    private static final float EDGE_FACE_DEPTH = 0.001f;

    private final TextureAtlasSprite sprite;
    private final Map<Direction, Mesh> meshes;
    private final float radius;

    public PipeBakedModel(Function<Material, TextureAtlasSprite> textureGetter, ResourceLocation texture, float radius) {
        this.sprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, texture));
        this.meshes = new EnumMap<>(Direction.class);
        this.radius = radius;

        float min = 0.5f - this.radius;
        float max = 0.5f + this.radius;
        float centerStart = this.centerStart();
        float centerEnd = this.centerEnd();
        float crossSectionWidth = this.crossSectionWidth();

        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            emitter
                    .square(direction, min, 0.0f, max, min, min)
                    .uv(0, 0, centerEnd)
                    .uv(1, 0, 16)
                    .uv(2, crossSectionWidth, 16)
                    .uv(3, crossSectionWidth, centerEnd)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
        }
        this.meshes.put(Direction.DOWN, meshBuilder.build());

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            emitter
                    .square(direction, min, max, max, 1.0f, min)
                    .uv(0, 0, 0)
                    .uv(1, 0, centerStart)
                    .uv(2, crossSectionWidth, centerStart)
                    .uv(3, crossSectionWidth, 0)
                    .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                    .color(-1, -1, -1, -1).emit();
        }
        this.meshes.put(Direction.UP, meshBuilder.build());

        emitter
                .square(Direction.WEST, 0, min, min, max, min)
                .uv(0, 0, 0)
                .uv(1, crossSectionWidth, 0)
                .uv(2, crossSectionWidth, centerStart)
                .uv(3, 0, centerStart)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, min, max, max, 1.0f, min)
                .uv(0, 0, 0)
                .uv(1, 0, centerStart)
                .uv(2, crossSectionWidth, centerStart)
                .uv(3, crossSectionWidth, 0)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.EAST, max, min, 1.0f, max, min)
                .uv(0, 0, centerEnd)
                .uv(1, crossSectionWidth, centerEnd)
                .uv(2, crossSectionWidth, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, min, 0.0f, max, min, min)
                .uv(0, 0, centerEnd)
                .uv(1, 0, 16)
                .uv(2, crossSectionWidth, 16)
                .uv(3, crossSectionWidth, centerEnd)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.NORTH, meshBuilder.build());

        emitter
                .square(Direction.WEST, max, min, 1.0f, max, min)
                .uv(0, 0, centerEnd)
                .uv(1, crossSectionWidth, centerEnd)
                .uv(2, crossSectionWidth, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.EAST, 0, min, min, max, min)
                .uv(0, 0, 0)
                .uv(1, crossSectionWidth, 0)
                .uv(2, crossSectionWidth, centerStart)
                .uv(3, 0, centerStart)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, min, 0.0f, max, min, min)
                .uv(0, 0, centerEnd)
                .uv(1, 0, 16)
                .uv(2, crossSectionWidth, 16)
                .uv(3, crossSectionWidth, centerEnd)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, min, max, max, 1.0f, min)
                .uv(0, 0, 0)
                .uv(1, 0, centerStart)
                .uv(2, crossSectionWidth, centerStart)
                .uv(3, crossSectionWidth, 0)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.SOUTH, meshBuilder.build());

        emitter
                .square(Direction.NORTH, 0, min, min, max, min)
                .uv(0, 0, 0)
                .uv(1, crossSectionWidth, 0)
                .uv(2, crossSectionWidth, centerStart)
                .uv(3, 0, centerStart)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.SOUTH, max, min, 1.0f, max, min)
                .uv(0, 0, centerEnd)
                .uv(1, crossSectionWidth, centerEnd)
                .uv(2, crossSectionWidth, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, max, min, 1.0f, max, min)
                .uv(0, crossSectionWidth, centerEnd)
                .uv(1, 0, centerEnd)
                .uv(2, 0, 16)
                .uv(3, crossSectionWidth, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, max, min, 1.0f, max, min)
                .uv(0, crossSectionWidth, centerEnd)
                .uv(1, 0, centerEnd)
                .uv(2, 0, 16)
                .uv(3, crossSectionWidth, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        this.meshes.put(Direction.EAST, meshBuilder.build());

        emitter
                .square(Direction.NORTH, max, min, 1.0f, max, min)
                .uv(0, 0, centerEnd)
                .uv(1, crossSectionWidth, centerEnd)
                .uv(2, crossSectionWidth, 16)
                .uv(3, 0, 16)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.SOUTH, 0, min, min, max, min)
                .uv(0, 0, 0)
                .uv(1, crossSectionWidth, 0)
                .uv(2, crossSectionWidth, centerStart)
                .uv(3, 0, centerStart)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.UP, 0, min, min, max, min)
                .uv(0, crossSectionWidth, 0)
                .uv(1, 0, 0)
                .uv(2, 0, centerStart)
                .uv(3, crossSectionWidth, centerStart)
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
        emitter
                .square(Direction.DOWN, 0, min, min, max, min)
                .uv(0, crossSectionWidth, 0)
                .uv(1, 0, 0)
                .uv(2, 0, centerStart)
                .uv(3, crossSectionWidth, centerStart)
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
                    this.renderReducerCap(getter, blockPos, direction, emitter);
                } else {
                    this.emitCapQuad(emitter, direction, 0.5f - this.radius, 0.5f - this.radius, 0.5f + this.radius, 0.5f + this.radius, 0.5f - this.radius);
                }
            }
        }
    }

    private void renderReducerCap(BlockAndTintGetter getter, BlockPos blockPos, Direction direction, QuadEmitter emitter) {
        BlockState neighborState = getter.getBlockState(blockPos.relative(direction));
        if (!(neighborState.getBlock() instanceof PipeShapedBlock<?> neighborPipe)) {
            return;
        }

        float neighborRadius = neighborPipe.getRadius();
        if (neighborRadius >= this.radius) {
            return;
        }

        float minOuter = 0.5f - this.radius;
        float maxOuter = 0.5f + this.radius;
        float minInner = 0.5f - neighborRadius;
        float maxInner = 0.5f + neighborRadius;
        this.emitCapQuad(emitter, direction, minInner, minInner, maxInner, maxInner, EDGE_FACE_DEPTH);
        this.emitCapQuad(emitter, direction, minOuter, minOuter, maxOuter, minInner, EDGE_FACE_DEPTH);
        this.emitCapQuad(emitter, direction, minOuter, maxInner, maxOuter, maxOuter, EDGE_FACE_DEPTH);
        this.emitCapQuad(emitter, direction, minOuter, minInner, minInner, maxInner, EDGE_FACE_DEPTH);
        this.emitCapQuad(emitter, direction, maxInner, minInner, maxOuter, maxInner, EDGE_FACE_DEPTH);
    }

    private void emitCapQuad(QuadEmitter emitter, Direction direction, float minX, float minY, float maxX, float maxY, float depth) {
        emitter
                .square(direction, minX, minY, maxX, maxY, depth)
                .uv(0, this.capU(minX), this.capV(minY))
                .uv(1, this.capU(minX), this.capV(maxY))
                .uv(2, this.capU(maxX), this.capV(maxY))
                .uv(3, this.capU(maxX), this.capV(minY))
                .spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV)
                .color(-1, -1, -1, -1).emit();
    }

    private float capU(float value) {
        return (value - (0.5f - this.radius)) * 16.0f;
    }

    private float capV(float value) {
        return value * 16.0f;
    }

    private float centerStart() {
        return (0.5f - this.radius) * 16.0f;
    }

    private float centerEnd() {
        return (0.5f + this.radius) * 16.0f;
    }

    private float crossSectionWidth() {
        return this.centerEnd() - this.centerStart();
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