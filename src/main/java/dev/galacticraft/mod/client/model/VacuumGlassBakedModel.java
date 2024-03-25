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

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class VacuumGlassBakedModel implements BakedModel {
    public static final ResourceLocation VACUUM_GLASS_MODEL = Constant.id("vacuum_glass");

    private static final float PANE_INSET = 6.0f / 16.0f;
    private static final float INNER_FRAME_INSET = 5.0f / 16.0f;
    private static final float INNER_FRAME_THICKNESS = 3.0f / 16.0f;
    private static final float FRAME_INSET = 4.0f / 16.0f;
    private static final float FRAME_THICKNESS = 2.0f / 16.0f;

    private final TextureAtlasSprite glass;
    private final TextureAtlasSprite frame;

    public VacuumGlassBakedModel(Function<Material, TextureAtlasSprite> textureGetter) {
        this.glass = textureGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, Constant.id("block/vacuum_glass_vanilla")));
        this.frame = textureGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, Constant.id("block/aluminum_decoration")));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
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
        return this.glass;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        QuadEmitter emitter = context.getEmitter();
        boolean up = state.getValue(BlockStateProperties.UP);
        boolean down = state.getValue(BlockStateProperties.DOWN);
        boolean north = state.getValue(BlockStateProperties.NORTH);
        boolean east = state.getValue(BlockStateProperties.EAST);
        boolean south = state.getValue(BlockStateProperties.SOUTH);
        boolean west = state.getValue(BlockStateProperties.WEST);
        int horizontal = (north ? 1 : 0) + (east ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0);
        RenderContext.QuadTransform glassTransform = quad -> {
            quad.spriteBake(this.glass, MutableQuadView.BAKE_LOCK_UV); //todo glass UVs
            return true;
        };RenderContext.QuadTransform aluminumTransform = quad -> {
            quad.spriteBake(this.frame, MutableQuadView.BAKE_LOCK_UV);
            return true;
        };
        context.pushTransform(quad -> {
            quad.color(-1, -1, -1, -1);
            return true;
        });
        switch (horizontal) {
            case 0 -> {
                Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
                switch (axis) {
                    case X -> {
                        context.pushTransform(glassTransform);
                        emitPane(emitter, Direction.NORTH, false, false, down, up);
                        emitPane(emitter, Direction.SOUTH, false, false, down, up);
                        context.popTransform();
                        context.pushTransform(aluminumTransform);
                        emitBasePlate(emitter, Direction.NORTH, down, up);
                        emitSides(emitter, Direction.NORTH, false, false);
                        context.popTransform();
                    }
                    case Z -> {
                        context.pushTransform(glassTransform);
                        emitPane(emitter, Direction.EAST, false, false, down, up);
                        emitPane(emitter, Direction.WEST, false, false, down, up);
                        context.popTransform();
                        context.pushTransform(aluminumTransform);
                        emitBasePlate(emitter, Direction.EAST, down, up);
                        emitSides(emitter, Direction.EAST, false, false);
                        context.popTransform();
                    }
                }
            }
            case 1 -> {
                if (east || west) {
                    context.pushTransform(glassTransform);
                    emitPane(emitter, Direction.NORTH, east, west, down, up);
                    emitPane(emitter, Direction.SOUTH, west, east, down, up);
                    context.popTransform();
                    context.pushTransform(aluminumTransform);
                    emitBasePlate(emitter, Direction.NORTH, down, up);
                    emitSides(emitter, Direction.NORTH, east, west);
                    context.popTransform();
                } else {
                    context.pushTransform(glassTransform);
                    emitPane(emitter, Direction.EAST, south, north, down, up);
                    emitPane(emitter, Direction.WEST, north, south, down, up);
                    context.popTransform();
                    context.pushTransform(aluminumTransform);
                    emitBasePlate(emitter, Direction.EAST, down, up);
                    emitSides(emitter, Direction.EAST, south, north);
                    context.popTransform();
                }
            }
            case 2 -> {
                if (east && west) {
                    context.pushTransform(glassTransform);
                    emitPane(emitter, Direction.NORTH, true, true, down, up);
                    emitPane(emitter, Direction.SOUTH, true, true, down, up);
                    context.popTransform();
                    context.pushTransform(aluminumTransform);
                    emitBasePlate(emitter, Direction.NORTH, down, up);
                    context.popTransform();
                } else if (north && south) {
                    context.pushTransform(glassTransform);
                    emitPane(emitter, Direction.EAST, true, true, down, up);
                    emitPane(emitter, Direction.WEST, true, true, down, up);
                    context.popTransform();
                    context.pushTransform(aluminumTransform);
                    emitBasePlate(emitter, Direction.EAST, down, up);
                    context.popTransform();
                } else {
                    context.pushTransform(quad -> {
                        quad.spriteBake(this.glass, MutableQuadView.BAKE_ROTATE_NONE);
                        return true;
                    });
                    emitCornerPane(emitter, east, down, north, up);
                    context.popTransform();
                    context.pushTransform(quad -> {
                        quad.spriteBake(this.frame, MutableQuadView.BAKE_LOCK_UV);
                        return true;
                    });
                    emitCornerBasePlate(emitter, east, north, down, up);
                    context.popTransform();
                }
            }
            case 3 -> {
                if (east && west) {
                    if (north) {
                        context.pushTransform(glassTransform);
                        emitBrokenPane(emitter, Direction.NORTH, down, up);
                        emitPane(emitter, Direction.SOUTH, true, true, down, up);
                        emitCenterPane(emitter, Direction.NORTH, down, up);
                        context.popTransform();
                        context.pushTransform(aluminumTransform);
                        emitBasePlate(emitter, Direction.NORTH, down, up);
                        emitCenterBasePlate(emitter, Direction.NORTH, down, up);
                        context.popTransform();
                    } else {
                        context.pushTransform(glassTransform);
                        emitBrokenPane(emitter, Direction.SOUTH, down, up);
                        emitPane(emitter, Direction.NORTH, true, true, down, up);
                        emitCenterPane(emitter, Direction.SOUTH, down, up);
                        context.popTransform();
                        context.pushTransform(aluminumTransform);
                        emitBasePlate(emitter, Direction.SOUTH, down, up);
                        emitCenterBasePlate(emitter, Direction.SOUTH, down, up);
                        context.popTransform();
                    }
                } else {
                    if (east) {
                        context.pushTransform(glassTransform);
                        emitBrokenPane(emitter, Direction.EAST, down, up);
                        emitPane(emitter, Direction.WEST, true, true, down, up);
                        emitCenterPane(emitter, Direction.EAST, down, up);
                        context.popTransform();
                        context.pushTransform(aluminumTransform);
                        emitBasePlate(emitter, Direction.EAST, down, up);
                        emitCenterBasePlate(emitter, Direction.EAST, down, up);
                        context.popTransform();
                    } else {
                        context.pushTransform(glassTransform);
                        emitBrokenPane(emitter, Direction.WEST, down, up);
                        emitPane(emitter, Direction.EAST, true, true, down, up);
                        emitCenterPane(emitter, Direction.WEST, down, up);
                        context.popTransform();
                        context.pushTransform(aluminumTransform);
                        emitBasePlate(emitter, Direction.WEST, down, up);
                        emitCenterBasePlate(emitter, Direction.WEST, down, up);
                        context.popTransform();
                    }
                }
            }
            case 4 -> {
                context.pushTransform(glassTransform);
                emitCenterPane(emitter, Direction.NORTH, down, up);
                emitCenterPane(emitter, Direction.EAST, down, up);
                emitCenterPane(emitter, Direction.SOUTH, down, up);
                emitCenterPane(emitter, Direction.WEST, down, up);
                context.popTransform();
                context.pushTransform(aluminumTransform);

                emitCenterBasePlate(emitter, Direction.NORTH, down, up);
                emitCenterBasePlate(emitter, Direction.EAST, down, up);
                emitCenterBasePlate(emitter, Direction.SOUTH, down, up);
                emitCenterBasePlate(emitter, Direction.WEST, down, up);

                if (!down) {
                    emitter.square(Direction.DOWN, FRAME_INSET, FRAME_INSET, 1.0f - FRAME_INSET, 1.0f - FRAME_INSET, 0.0f).emit();
                }
                if (!up) {
                    emitter.square(Direction.UP, FRAME_INSET, FRAME_INSET, 1.0f - FRAME_INSET, 1.0f - FRAME_INSET, 0.0f).emit();
                }
                context.popTransform();
            }
        }
        context.popTransform();

        BakedModel.super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    private static void emitSides(QuadEmitter emitter, Direction direction, boolean left, boolean right) {
        if (!left) {
            // OUTER FRAME
            emitter.square(direction.getClockWise(), FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, 1.0f, 0.0f).emit();

            // INNER FRAME PANE
            emitter.square(direction, 0.0f, 0.0f, FRAME_THICKNESS, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, FRAME_THICKNESS, 0.0f, INNER_FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR
            emitter.square(direction.getCounterClockWise(), FRAME_INSET, 0.0f, INNER_FRAME_INSET, 1.0f, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME PANE (BACK)
            emitter.square(direction.getOpposite(), 1.0f - FRAME_THICKNESS, 0.0f, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 1.0f - INNER_FRAME_THICKNESS, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR (BACK)
            emitter.square(direction.getCounterClockWise(), 1.0f - INNER_FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, 1.0f, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME
            emitter.square(direction.getCounterClockWise(), INNER_FRAME_INSET, 0.0f, 1.0f - INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_THICKNESS).emit();
        }
        if (!right) {
            // OUTER FRAME
            emitter.square(direction.getCounterClockWise(), FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, 1.0f, 0.0f).emit();

            // INNER FRAME PANE
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, FRAME_THICKNESS, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), FRAME_THICKNESS, 0.0f, INNER_FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR
            emitter.square(direction.getClockWise(), FRAME_INSET, 0.0f, INNER_FRAME_INSET, 1.0f, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME PANE (BACK)
            emitter.square(direction, 1.0f - FRAME_THICKNESS, 0.0f, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, 1.0f - INNER_FRAME_THICKNESS, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, INNER_FRAME_INSET).emit();

            // INNER FRAME PANE CONNECTOR (BACK)
            emitter.square(direction.getClockWise(), 1.0f - INNER_FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, 1.0f, 1.0f - FRAME_THICKNESS).emit();

            // INNER FRAME
            emitter.square(direction.getClockWise(), INNER_FRAME_INSET, 0.0f, 1.0f - INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_THICKNESS).emit();
        }
    }

    private static void emitCornerPane(QuadEmitter emitter, boolean east, boolean down, boolean north, boolean up) {
        // A D <- quad order
        // B C

        emitter
                .pos(0, east ? PANE_INSET : 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(1, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(2, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(3, east ? PANE_INSET : 1.0f - PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .uv(0, 0, down ? 0 : 3)
                .uv(1, 13, down ? 0 : 3)
                .uv(2, 13, 13 + (up ? 3 : 0))
                .uv(3, 0, 13 + (up ? 3 : 0))
                .emit();
        emitter
                .pos(0, east ? 1.0f - PANE_INSET : PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(1, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(2, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(3, east ? 1.0f - PANE_INSET : PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .uv(0, 0, down ? 0 : 3)
                .uv(1, 8, down ? 0 : 3)
                .uv(2, 8, 13 + (up ? 3 : 0))
                .uv(3, 0, 13 + (up ? 3 : 0))
                .emit();

        emitter
                .pos(3, east ? PANE_INSET : 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(2, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(1, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 1.0f - PANE_INSET : PANE_INSET)
                .pos(0, east ? PANE_INSET : 1.0f - PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .uv(0, 0, down ? 0 : 3)
                .uv(1, 13, down ? 0 : 3)
                .uv(2, 13, 13 + (up ? 3 : 0))
                .uv(3, 0, 13 + (up ? 3 : 0))
                .emit();
        emitter
                .pos(3, east ? 1.0f - PANE_INSET : PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .pos(2, east ? 1.0f : 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(1, east ? 1.0f : 0.0f, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? PANE_INSET : 1.0f - PANE_INSET)
                .pos(0, east ? 1.0f - PANE_INSET : PANE_INSET, up ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                .uv(0, 0, down ? 0 : 3)
                .uv(1, 8, down ? 0 : 3)
                .uv(2, 8, 13 + (up ? 3 : 0))
                .uv(3, 0, 13 + (up ? 3 : 0))
                .emit();
    }

    private static void emitCornerBasePlate(QuadEmitter emitter, boolean east, boolean north, boolean down, boolean up) {
        if (!up) {
            // OUTER FRAME
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(2, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .pos(3, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .cullFace(Direction.UP)
                    .emit();

            // OUTER FRAME SIDE
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(1, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(2, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .pos(3, east ? 1.0f : 0.0f, 1.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .uv(0, 4, 14)
                    .uv(1, 9, 14)
                    .uv(2, 9, 16)
                    .uv(3, 4, 16)
                    .emit();

            // OUTER FRAME IN
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? 1.0f - FRAME_INSET : FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .nominalFace(Direction.DOWN)
                    .emit();

            // INNER FRAME CONNECTOR
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .uv(0, 4, 13)
                    .uv(1, 4 + 5.65f, 14) //sqrt(32)
                    .uv(2, 4 + 5.65f, 14)
                    .uv(3, 4, 13)
                    .emit();

            // INNER FRAME
            emitter
                    .pos(0, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .uv(0, 7, 13)
                    .uv(1, 7 + 7.07f, 14) //sqrt(50)
                    .uv(2, 7 + 7.07f, 14)
                    .uv(3, 7, 13)
                    .nominalFace(Direction.DOWN)
                    .emit();

            // OUTER FRAME SIDE (REV)
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(2, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(1, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f, north ? 0.0f : 1.0f)
                    .pos(0, east ? 1.0f : 0.0f, 1.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .uv(0, 4, 16)
                    .uv(1, 9, 16)
                    .uv(2, 9, 14)
                    .uv(3, 4, 14)
                    .emit();

            // OUTER FRAME IN (REV)
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? FRAME_INSET : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .nominalFace(Direction.DOWN)
                    .emit();

            // INNER FRAME CONNECTOR (REV)
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 1.0f - FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, 1.0f - INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, 1.0f - FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .uv(0, 4, 13)
                    .uv(1, 4 + 5.65f, 14) //sqrt(32)
                    .uv(2, 4 + 5.65f, 14)
                    .uv(3, 4, 13)
                    .emit();
        }
        if (!down) {
            // OUTER FRAME
            emitter
                    .pos(3, east ? 1.0f : 0.0f, 0.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, 0.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(1, north ? 1.0f - FRAME_INSET : FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .pos(0, north ? FRAME_INSET : 1.0f - FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .cullFace(Direction.DOWN)
                    .emit();

            // OUTER FRAME SIDE
            emitter
                    .pos(3, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(2, north ? 1.0f - FRAME_INSET : FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(1, north ? 1.0f - FRAME_INSET : FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .pos(0, east ? 1.0f : 0.0f, 0.0f, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .uv(0, 4, 0)
                    .uv(1, 9, 0)
                    .uv(2, 9, 2)
                    .uv(3, 4, 2)
                    .emit();

            // OUTER FRAME IN
            emitter
                    .pos(3, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? FRAME_INSET : 1.0f - FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? 1.0f - FRAME_INSET : FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .nominalFace(Direction.UP)
                    .emit();

            // INNER FRAME CONNECTOR
            emitter
                    .pos(3, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(1, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .uv(0, 4, 2)
                    .uv(1, 10, 2)
                    .uv(2, 10, 3)
                    .uv(3, 4, 3)
                    .emit();

            // INNER FRAME
            emitter
                    .pos(3, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET)
                    .pos(2, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(0, north ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .nominalFace(Direction.UP)
                    .emit();

            // OUTER FRAME SIDE (REV)
            emitter
                    .pos(0, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(1, north ? FRAME_INSET : 1.0f - FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(2, north ? FRAME_INSET : 1.0f - FRAME_INSET, 0.0f, north ? 0.0f : 1.0f)
                    .pos(3, east ? 1.0f : 0.0f, 0.0f, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .uv(0, 4, 2)
                    .uv(1, 9, 2)
                    .uv(2, 9, 0)
                    .uv(3, 4, 0)
                    .emit();

            // OUTER FRAME IN (REV)
            emitter
                    .pos(0, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - FRAME_INSET : FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? FRAME_INSET : 1.0f - FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .nominalFace(Direction.UP)
                    .emit();

            // INNER FRAME CONNECTOR (REV)
            emitter
                    .pos(0, east ? 1.0f : 0.0f, FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(1, east ? 1.0f : 0.0f, INNER_FRAME_THICKNESS, east ? 1.0f - INNER_FRAME_INSET : INNER_FRAME_INSET)
                    .pos(2, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, INNER_FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .pos(3, north ? INNER_FRAME_INSET : 1.0f - INNER_FRAME_INSET, FRAME_THICKNESS, north ? 0.0f : 1.0f)
                    .uv(0, 4, 2)
                    .uv(1, 10, 2)
                    .uv(2, 10, 3)
                    .uv(3, 4, 3)
                    .emit();
        }
    }

    private static void emitBasePlate(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        boolean side = direction.getAxis() == Direction.Axis.X;
        if (!down) {
            emitter.square(direction, 0.0f, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction, 0.0f, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();

            emitter.square(Direction.UP, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
            emitter.square(Direction.UP, side ? INNER_FRAME_INSET : 0.0f, side ? 0.0f : INNER_FRAME_INSET, side ? 1.0f - INNER_FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
            emitter.square(Direction.DOWN, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 0.0f).emit();
        }
        if (!up) {
            emitter.square(direction, 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction, 0.0f, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getOpposite(), 0.0f, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();

            emitter.square(Direction.DOWN, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
            emitter.square(Direction.DOWN, side ? INNER_FRAME_INSET : 0.0f, side ? 0.0f : INNER_FRAME_INSET, side ? 1.0f - INNER_FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
            emitter.square(Direction.UP, side ? FRAME_INSET : 0.0f, side ? 0.0f : FRAME_INSET, side ? 1.0f - FRAME_INSET : 1.0f, side ? 1.0f : 1.0f - FRAME_INSET, 0.0f).emit();
        }
    }

    private static void emitCenterBasePlate(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        if (!down) {
            emitter.square(direction.getClockWise(), 1.0f - FRAME_INSET, 0.0f, 1.0f, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction.getClockWise(), 1.0f - INNER_FRAME_INSET, 0.0f, 1.0f, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();
            emitter.square(direction.getCounterClockWise(), 0.0f, 0.0f, FRAME_INSET, FRAME_THICKNESS, FRAME_INSET).emit();
            emitter.square(direction.getCounterClockWise(), 0.0f, 0.0f, INNER_FRAME_INSET, INNER_FRAME_THICKNESS, INNER_FRAME_INSET).emit();

            switch (direction) {
                case NORTH -> {
                    emitter.square(Direction.UP, FRAME_INSET, 1.0f - PANE_INSET, 1.0f - FRAME_INSET, 1.0f, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, INNER_FRAME_INSET, 1.0f - PANE_INSET, 1.0f - INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, FRAME_INSET, 1.0f - FRAME_INSET, 1.0f - FRAME_INSET, 1.0f, 0.0f).emit();
                }
                case SOUTH -> {
                    emitter.square(Direction.UP, FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, PANE_INSET, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, INNER_FRAME_INSET, 0.0f, 1.0f - INNER_FRAME_INSET, PANE_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, FRAME_INSET, 0.0f).emit();
                }
                case WEST -> {
                    emitter.square(Direction.UP, 0.0f, FRAME_INSET, PANE_INSET, 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, 0.0f, INNER_FRAME_INSET, PANE_INSET, 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, 0.0f, FRAME_INSET, FRAME_INSET, 1.0f - FRAME_INSET, 0.0f).emit();
                }
                case EAST -> {
                    emitter.square(Direction.UP, 1.0f - PANE_INSET, FRAME_INSET, 1.0f, 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, 1.0f - PANE_INSET, INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, 1.0f - FRAME_INSET, FRAME_INSET, 1.0f, 1.0f - FRAME_INSET, 0.0f).emit();
                }
            }
        }

        if (!up) {
            emitter.square(direction.getClockWise(), 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS, 1.0f, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getClockWise(), 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS, 1.0f, 1.0f, INNER_FRAME_INSET).emit();
            emitter.square(direction.getCounterClockWise(), 0.0f, 1.0f - FRAME_THICKNESS, FRAME_INSET, 1.0f, FRAME_INSET).emit();
            emitter.square(direction.getCounterClockWise(), 0.0f, 1.0f - INNER_FRAME_THICKNESS, INNER_FRAME_INSET, 1.0f, INNER_FRAME_INSET).emit();

            switch (direction) {
                case SOUTH -> {
                    emitter.square(Direction.DOWN, FRAME_INSET, 1.0f - PANE_INSET, 1.0f - FRAME_INSET, 1.0f, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, INNER_FRAME_INSET, 1.0f - PANE_INSET, 1.0f - INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, FRAME_INSET, 0.0f).emit();
                }
                case NORTH -> {
                    emitter.square(Direction.DOWN, FRAME_INSET, 0.0f, 1.0f - FRAME_INSET, PANE_INSET, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, INNER_FRAME_INSET, 0.0f, 1.0f - INNER_FRAME_INSET, PANE_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, FRAME_INSET, 1.0f - FRAME_INSET, 1.0f - FRAME_INSET, 1.0f, 0.0f).emit();
                }
                case WEST -> {
                    emitter.square(Direction.DOWN, 0.0f, FRAME_INSET, PANE_INSET, 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, 0.0f, INNER_FRAME_INSET, PANE_INSET, 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, 0.0f, FRAME_INSET, FRAME_INSET, 1.0f - FRAME_INSET, 0.0f).emit();
                }
                case EAST -> {
                    emitter.square(Direction.DOWN, 1.0f - PANE_INSET, FRAME_INSET, 1.0f, 1.0f - FRAME_INSET, 1.0f - FRAME_THICKNESS).emit();
                    emitter.square(Direction.DOWN, 1.0f - PANE_INSET, INNER_FRAME_INSET, 1.0f, 1.0f - INNER_FRAME_INSET, 1.0f - INNER_FRAME_THICKNESS).emit();
                    emitter.square(Direction.UP, 1.0f - FRAME_INSET, FRAME_INSET, 1.0f, 1.0f - FRAME_INSET, 0.0f).emit();
                }
            }
        }
    }

    private static void emitCenterPane(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        emitter.square(direction.getClockWise(), 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();
        emitter.square(direction.getCounterClockWise(), 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();

        emitter.square(direction.getCounterClockWise(), 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
        emitter.square(direction.getClockWise(), 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
    }

    private static void emitPane(QuadEmitter emitter, Direction direction, boolean left, boolean right, boolean down, boolean up) {
        emitter.square(direction, left ? 0.0f : INNER_FRAME_THICKNESS, down ? 0.0f : INNER_FRAME_THICKNESS, right ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();
        emitter.square(direction.getOpposite(), right ? 0.0f : INNER_FRAME_THICKNESS, down ? 0.0f : INNER_FRAME_THICKNESS, left ? 1.0f : 1.0f - INNER_FRAME_THICKNESS, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - (PANE_INSET)).emit();
    }

    private static void emitBrokenPane(QuadEmitter emitter, Direction direction, boolean down, boolean up) {
        emitter.square(direction, 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();
        emitter.square(direction, 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), PANE_INSET).emit();

        emitter.square(direction.getOpposite(), 0.0f, down ? 0.0f : INNER_FRAME_THICKNESS, PANE_INSET, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
        emitter.square(direction.getOpposite(), 1.0f - PANE_INSET, down ? 0.0f : INNER_FRAME_THICKNESS, 1.0f, up ? 1.0f : (1.0f - INNER_FRAME_THICKNESS), 1.0f - PANE_INSET).emit();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BakedModel.super.emitItemQuads(stack, randomSupplier, context);
    }
}
