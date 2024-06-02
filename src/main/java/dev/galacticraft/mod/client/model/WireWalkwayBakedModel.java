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

import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.networked.WireWalkwayBlockEntity;
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
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WireWalkwayBakedModel implements BakedModel {
    private static WireWalkwayBakedModel instance = null;

    private static final ResourceLocation WALKWAY_TEXTURE = Constant.id("block/walkway");
    public static final ResourceLocation WALKWAY_PLATFORM = Constant.id("block/wire_walkway");
    private static final ResourceLocation ALUMINUM_WIRE = Constant.id("block/aluminum_wire");
    private final BakedModel walkway;
    private final Mesh down;
    private final Mesh up;
    private final Mesh north;
    private final Mesh south;
    private final Mesh west;
    private final Mesh east;
    private final Mesh smallDown;
    private final Mesh smallUp;
    private final Mesh smallNorth;
    private final Mesh smallSouth;
    private final Mesh smallWest;
    private final Mesh smallEast;
    private final TextureAtlasSprite sprite;
    private final TextureAtlasSprite walkwaySprite;

    protected WireWalkwayBakedModel(ModelBaker loader, Function<Material, TextureAtlasSprite> textureGetter, ModelState rotationContainer) {
        this.walkway = loader.getModel(WALKWAY_PLATFORM).bake(loader, textureGetter, rotationContainer, WALKWAY_PLATFORM);
        this.sprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, ALUMINUM_WIRE));
        this.walkwaySprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, WALKWAY_TEXTURE));
        var meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        var emitter = meshBuilder.getEmitter();

        emitter.square(Direction.WEST, 0.6f, 0.4f, 0.4f, 0.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 0, 0).sprite(2, 0, 4, 0).sprite(3, 0, 4, 8).sprite(0, 0, 0, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0.6f, 0.4f, 0.4f, 0.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 0).sprite(2, 0, 8, 0).sprite(3, 0, 8, 8).sprite(0, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 0).sprite(2, 0, 12, 0).sprite(3, 0, 12, 8).sprite(0, 0, 8, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 0).sprite(2, 0, 8, 0).sprite(3, 0, 8, 8).sprite(0, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.down = meshBuilder.build();

        emitter.square(Direction.EAST, 0.6f, 1.0f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 0, 8).sprite(2, 0, 4, 8).sprite(3, 0, 4, 16).sprite(0, 0, 0, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0.6f, 1.0f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 8).sprite(2, 0, 8, 8).sprite(3, 0, 8, 16).sprite(0, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 8).sprite(2, 0, 12, 8).sprite(3, 0, 12, 16).sprite(0, 0, 8, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 8).sprite(2, 0, 8, 8).sprite(3, 0, 8, 16).sprite(0, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.up = meshBuilder.build();

        emitter.square(Direction.WEST, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 0).sprite(1, 0, 4, 0).sprite(2, 0, 4, 8).sprite(3, 0, 0, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 0).sprite(1, 0, 8, 0).sprite(2, 0, 8, 8).sprite(3, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 0).sprite(2, 0, 12, 0).sprite(3, 0, 12, 8).sprite(0, 0, 8, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 0).sprite(2, 0, 8, 0).sprite(3, 0, 8, 8).sprite(0, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.north = meshBuilder.build();

        emitter.square(Direction.EAST, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 8).sprite(1, 0, 4, 8).sprite(2, 0, 4, 16).sprite(3, 0, 0, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 8).sprite(1, 0, 8, 8).sprite(2, 0, 8, 16).sprite(3, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.4f, 0.0f, 0.6f, 0.4f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 8).sprite(2, 0, 12, 8).sprite(3, 0, 12, 16).sprite(0, 0, 8, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.4f, 0.6f, 0.6f, 1.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 8).sprite(2, 0, 8, 8).sprite(3, 0, 8, 16).sprite(0, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.south = meshBuilder.build();

        emitter.square(Direction.NORTH, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 0).sprite(1, 0, 4, 0).sprite(2, 0, 4, 8).sprite(3, 0, 0, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 0).sprite(1, 0, 8, 0).sprite(2, 0, 8, 8).sprite(3, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 8, 0).sprite(1, 0, 12, 0).sprite(2, 0, 12, 8).sprite(3, 0, 8, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 0).sprite(1, 0, 8, 0).sprite(2, 0, 8, 8).sprite(3, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.west = meshBuilder.build();

        emitter.square(Direction.SOUTH, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 8).sprite(1, 0, 4, 8).sprite(2, 0, 4, 16).sprite(3, 0, 0, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.0f, 0.4f, 0.4f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 8).sprite(1, 0, 8, 8).sprite(2, 0, 8, 16).sprite(3, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 8, 8).sprite(1, 0, 12, 8).sprite(2, 0, 12, 16).sprite(3, 0, 8, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.6f, 0.4f, 1.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 8).sprite(1, 0, 8, 8).sprite(2, 0, 8, 16).sprite(3, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.east = meshBuilder.build();

        emitter.square(Direction.WEST, 0.6f, 0.0f, 0.4f, -0.2f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 0, 0).sprite(2, 0, 4, 0).sprite(3, 0, 4, 8).sprite(0, 0, 0, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0.6f, 0.0f, 0.4f, -0.2f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 0).sprite(2, 0, 8, 0).sprite(3, 0, 8, 8).sprite(0, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.4f, -0.2f, 0.6f, 0.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 0).sprite(2, 0, 12, 0).sprite(3, 0, 12, 8).sprite(0, 0, 8, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.4f, -0.2f, 0.6f, 0.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 0).sprite(2, 0, 8, 0).sprite(3, 0, 8, 8).sprite(0, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.smallDown = meshBuilder.build();

        emitter.square(Direction.EAST, 0.6f, 1.2f, 0.4f, 1.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 0, 8).sprite(2, 0, 4, 8).sprite(3, 0, 4, 16).sprite(0, 0, 0, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0.6f, 1.2f, 0.4f, 1.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 8).sprite(2, 0, 8, 8).sprite(3, 0, 8, 16).sprite(0, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0.4f, 1.0f, 0.6f, 1.2f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 8).sprite(2, 0, 12, 8).sprite(3, 0, 12, 16).sprite(0, 0, 8, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, 0.4f, 1.0f, 0.6f, 1.2f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 8).sprite(2, 0, 8, 8).sprite(3, 0, 8, 16).sprite(0, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.smallUp = meshBuilder.build();

        emitter.square(Direction.WEST, -0.2f, 0.4f, 0.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 0).sprite(1, 0, 4, 0).sprite(2, 0, 4, 8).sprite(3, 0, 0, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 1.0f, 0.4f, 1.2f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 0).sprite(1, 0, 8, 0).sprite(2, 0, 8, 8).sprite(3, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.4f, -0.2f, 0.6f, 0.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 0).sprite(2, 0, 12, 0).sprite(3, 0, 12, 8).sprite(0, 0, 8, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.4f, 1.0f, 0.6f, 1.2f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 0).sprite(2, 0, 8, 0).sprite(3, 0, 8, 8).sprite(0, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.smallNorth = meshBuilder.build();

        emitter.square(Direction.EAST, -0.2f, 0.4f, 0.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 8).sprite(1, 0, 4, 8).sprite(2, 0, 4, 16).sprite(3, 0, 0, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 1.0f, 0.4f, 1.2f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 8).sprite(1, 0, 8, 8).sprite(2, 0, 8, 16).sprite(3, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0.4f, -0.2f, 0.6f, 0.0f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 8, 8).sprite(2, 0, 12, 8).sprite(3, 0, 12, 16).sprite(0, 0, 8, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0.4f, 1.0f, 0.6f, 1.2f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(1, 0, 4, 8).sprite(2, 0, 8, 8).sprite(3, 0, 8, 16).sprite(0, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.smallSouth = meshBuilder.build();

        emitter.square(Direction.NORTH, 1.0f, 0.4f, 1.2f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 0).sprite(1, 0, 4, 0).sprite(2, 0, 4, 8).sprite(3, 0, 0, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.SOUTH, -0.2f, 0.4f, 0.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 0).sprite(1, 0, 8, 0).sprite(2, 0, 8, 8).sprite(3, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, -0.2f, 0.4f, 0.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 8, 0).sprite(1, 0, 12, 0).sprite(2, 0, 12, 8).sprite(3, 0, 8, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, -0.2f, 0.4f, 0.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 0).sprite(1, 0, 8, 0).sprite(2, 0, 8, 8).sprite(3, 0, 4, 8).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.smallWest = meshBuilder.build();

        emitter.square(Direction.SOUTH, 1.0f, 0.4f, 1.2f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 0, 8).sprite(1, 0, 4, 8).sprite(2, 0, 4, 16).sprite(3, 0, 0, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, -0.2f, 0.4f, 0.0f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 8).sprite(1, 0, 8, 8).sprite(2, 0, 8, 16).sprite(3, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 1.0f, 0.4f, 1.2f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 8, 8).sprite(1, 0, 12, 8).sprite(2, 0, 12, 16).sprite(3, 0, 8, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 1.0f, 0.4f, 1.2f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 4, 8).sprite(1, 0, 8, 8).sprite(2, 0, 8, 16).sprite(3, 0, 4, 16).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
        this.smallEast = meshBuilder.build();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter getter, BlockState blockState, BlockPos blockPos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var emitter = context.getEmitter();

        if (getter.getBlockEntity(blockPos) instanceof WireWalkwayBlockEntity wire) {
            var x = 0;
            var y = 0;
            var connections = wire.getConnections();

            switch (wire.getDirection()) {
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
            this.walkway.emitBlockQuads(getter, blockState, blockPos, randomSupplier, context);
            context.popTransform();

            this.emitBlockQuadsDirection(emitter, connections, this.down, this.smallDown, Direction.DOWN);
            this.emitBlockQuadsDirection(emitter, connections, this.up, this.smallUp, Direction.UP);
            this.emitBlockQuadsDirection(emitter, connections, this.north, this.smallNorth, Direction.NORTH);
            this.emitBlockQuadsDirection(emitter, connections, this.south, this.smallSouth, Direction.SOUTH);
            this.emitBlockQuadsDirection(emitter, connections, this.west, this.smallWest, Direction.WEST);
            this.emitBlockQuadsDirection(emitter, connections, this.east, this.smallEast, Direction.EAST);
        }
    }

    private void emitBlockQuadsDirection(QuadEmitter emitter, boolean[] connections, Mesh mesh, Mesh smallMesh, Direction direction) {
        if (connections[direction.get3DDataValue()]) {
            mesh.outputTo(emitter);
            smallMesh.outputTo(emitter);
        }
        else {
            emitter.square(direction, 0.4f, 0.4f, 0.6f, 0.6f, 0.4f).spriteColor(0, -1, -1, -1, -1).sprite(0, 0, 12, 0).sprite(1, 0, 16, 0).sprite(2, 0, 16, 4).sprite(3, 0, 12, 4).spriteBake(0, this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
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

    public static WireWalkwayBakedModel getInstance(ModelBaker loader, Function<Material, TextureAtlasSprite> spriteFunction, ModelState rotationContainer) {
        if (instance == null) {
            return instance = new WireWalkwayBakedModel(loader, spriteFunction, rotationContainer);
        }
        return instance;
    }

    public static void invalidate() {
        instance = null;
    }
}