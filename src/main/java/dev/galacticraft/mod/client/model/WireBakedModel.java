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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WireBakedModel implements BakedModel {
    private static WireBakedModel instance = null;

    private static final ResourceLocation ALUMINUM_WIRE = Constant.id("block/aluminum_wire");
    private final Mesh down;
    private final Mesh up;
    private final Mesh north;
    private final Mesh south;
    private final Mesh west;
    private final Mesh east;
    private final TextureAtlasSprite sprite;

    protected WireBakedModel(Function<Material, TextureAtlasSprite> textureGetter) {
        this.sprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, ALUMINUM_WIRE));
        var meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        var emitter = meshBuilder.getEmitter();
        emitter.square(Direction.WEST, 0.6f, 0.4f, 0.4f, 0.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 0, 0).uv(2, 4, 0).uv(3, 4, 8).uv(0, 0, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0.6f, 0.4f, 0.4f, 0.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 0).uv(2, 8, 0).uv(3, 8, 8).uv(0, 4, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 0).uv(2, 12, 0).uv(3, 12, 8).uv(0, 8, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 0).uv(2, 8, 0).uv(3, 8, 8).uv(0, 4, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.down = meshBuilder.build();
        emitter.square(Direction.EAST, 0.6f, 1.0f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(1, 0, 8).uv(2, 4, 8).uv(3, 4, 16).uv(0, 0, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0.6f, 1.0f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 8).uv(2, 8, 8).uv(3, 8, 16).uv(0, 4, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 8).uv(2, 12, 8).uv(3, 12, 16).uv(0, 8, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 8).uv(2, 8, 8).uv(3, 8, 16).uv(0, 4, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.up = meshBuilder.build();
        emitter.square(Direction.WEST, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 0).uv(1, 4, 0).uv(2, 4, 8).uv(3, 0, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 0).uv(1, 8, 0).uv(2, 8, 8).uv(3, 4, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 0).uv(2, 12, 0).uv(3, 12, 8).uv(0, 8, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 0).uv(2, 8, 0).uv(3, 8, 8).uv(0, 4, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.north = meshBuilder.build();
        emitter.square(Direction.EAST, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 8).uv(1, 4, 8).uv(2, 4, 16).uv(3, 0, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 8).uv(1, 8, 8).uv(2, 8, 16).uv(3, 4, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).color(-1, -1, -1, -1).uv(1, 8, 8).uv(2, 12, 8).uv(3, 12, 16).uv(0, 8, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).color(-1, -1, -1, -1).uv(1, 4, 8).uv(2, 8, 8).uv(3, 8, 16).uv(0, 4, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.south = meshBuilder.build();
        emitter.square(Direction.NORTH, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 0).uv(1, 4, 0).uv(2, 4, 8).uv(3, 0, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 0).uv(1, 8, 0).uv(2, 8, 8).uv(3, 4, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 8, 0).uv(1, 12, 0).uv(2, 12, 8).uv(3, 8, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 0).uv(1, 8, 0).uv(2, 8, 8).uv(3, 4, 8).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.west = meshBuilder.build();
        emitter.square(Direction.SOUTH, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 0, 8).uv(1, 4, 8).uv(2, 4, 16).uv(3, 0, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 8).uv(1, 8, 8).uv(2, 8, 16).uv(3, 4, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 8, 8).uv(1, 12, 8).uv(2, 12, 16).uv(3, 8, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 4, 8).uv(1, 8, 8).uv(2, 8, 16).uv(3, 4, 16).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.east = meshBuilder.build();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter getter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var emitter = context.getEmitter();

        if (getter.getBlockEntity(blockPos) instanceof WireBlockEntity wire) {
            this.emitBlockQuadsDirection(emitter, wire.getConnections(), this.down, Direction.DOWN);
            this.emitBlockQuadsDirection(emitter, wire.getConnections(), this.up, Direction.UP);
            this.emitBlockQuadsDirection(emitter, wire.getConnections(), this.north, Direction.NORTH);
            this.emitBlockQuadsDirection(emitter, wire.getConnections(), this.south, Direction.SOUTH);
            this.emitBlockQuadsDirection(emitter, wire.getConnections(), this.west, Direction.WEST);
            this.emitBlockQuadsDirection(emitter, wire.getConnections(), this.east, Direction.EAST);
        }
    }

    private void emitBlockQuadsDirection(QuadEmitter emitter, boolean[] connections, Mesh mesh, Direction direction) {
        if (connections[direction.get3DDataValue()]) {
            mesh.outputTo(emitter);
        }
        else {
            emitter.square(direction, 0.4f, 0.4f, 0.6f, 0.6f, 0.4f).color(-1, -1, -1, -1).uv(0, 12, 0).uv(1, 16, 0).uv(2, 16, 4).uv(3, 12, 4).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
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
        return this.sprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    public static WireBakedModel getInstance(Function<Material, TextureAtlasSprite> spriteFunction) {
        if (instance == null) {
            return instance = new WireBakedModel(spriteFunction);
        }
        return instance;
    }

    public static void invalidate() {
        instance = null;
    }
}