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
import dev.galacticraft.mod.client.model.vacuum_glass.*;
import dev.galacticraft.mod.content.GCBlocks;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Baked model implementation for the vacuum glass block.
 * <p>
 * This model emits custom frame geometry based on the six directional connection
 * properties present on the block state. Depending on the active connections,
 * the model selects and emits one of several shapes such as stub, cap, straight,
 * corner, T-junction, tri-corner, cross, or saddle geometry.
 * <p>
 * The implementation intentionally performs all geometry generation at render time
 * and relies on helper methods throughout this class to build quads for the
 * different connection combinations.
 */
public class VacuumGlassBakedModel implements BakedModel {
    // CONFIGURABLE
    public static final float FRAME_INSET = 4.0f / 16.0f;
    public static final float INNER_FRAME_INSET = 5.0f / 16.0f;
    public static final float PANE_INSET = 6.0f / 16.0f;
    public static final float FRAME_THICKNESS = 3.0f / 16.0f;
    public static final float INNER_FRAME_THICKNESS = 2.0f / 16.0f;

    // NON-CONFIGURABLE
    public static final float INVERTED_FRAME_INSET = 1.0f - FRAME_INSET;

    private final TextureAtlasSprite glass;
    private final TextureAtlasSprite frame;

    // ---------------------------------------------------------------------
    // Construction
    // ---------------------------------------------------------------------

    /**
     * Creates a new baked model instance and resolves the sprites used for the
     * glass pane and aluminum frame surfaces.
     *
     * @param textureGetter function used to resolve atlas materials into sprites
     */
    public VacuumGlassBakedModel(Function<Material, TextureAtlasSprite> textureGetter) {
        this.glass = textureGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, Constant.id("block/vacuum_glass_vanilla")));
        this.frame = textureGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, Constant.id("block/aluminum_decoration")));
    }

    // ---------------------------------------------------------------------
    // BakedModel implementation
    // ---------------------------------------------------------------------

    /**
     * Returns vanilla baked quads for this model.
     * <p>
     * This implementation returns an empty list because block geometry is emitted
     * through Fabric Renderer API callbacks instead.
     *
     * @param state block state being rendered
     * @param face optional cull face
     * @param random random source supplied by the renderer
     * @return an empty quad list
     */
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
        return Collections.emptyList();
    }

    /**
     * Indicates that this model should participate in ambient occlusion.
     *
     * @return {@code true}
     */
    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    /**
     * Indicates that this model should render as a 3D model in GUIs.
     *
     * @return {@code true}
     */
    @Override
    public boolean isGui3d() {
        return true;
    }

    /**
     * Indicates that this model uses block lighting.
     *
     * @return {@code true}
     */
    @Override
    public boolean usesBlockLight() {
        return true;
    }

    /**
     * Indicates that this model does not use a special item renderer.
     *
     * @return {@code false}
     */
    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    /**
     * Returns the particle texture used for this model.
     *
     * @return the frame sprite used as the particle icon
     */
    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.frame;
    }

    /**
     * Returns the item transform set for this model.
     *
     * @return the default no-transform configuration
     */
    @Override
    public @NotNull ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    /**
     * Returns the item override table for this model.
     *
     * @return the empty override table
     */
    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    /**
     * Indicates that this model is not a vanilla adapter model.
     *
     * @return {@code false}
     */
    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    /**
     * Emits the block quads for the vacuum glass model.
     * <p>
     * The method inspects all six directional connection properties and dispatches
     * to the appropriate shape emitter for the current configuration.
     *
     * @param blockView world access used for adjacent block checks
     * @param state current block state
     * @param pos block position being rendered
     * @param randomSupplier random source supplier
     * @param context render emission context
     */
    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        QuadEmitter emitter = context.getEmitter();

        boolean up = state.getValue(BlockStateProperties.UP);
        boolean down = state.getValue(BlockStateProperties.DOWN);
        boolean north = state.getValue(BlockStateProperties.NORTH);
        boolean east = state.getValue(BlockStateProperties.EAST);
        boolean south = state.getValue(BlockStateProperties.SOUTH);
        boolean west = state.getValue(BlockStateProperties.WEST);

        RenderContext.QuadTransform glassTransform = quad -> {
            quad.spriteBake(this.glass, MutableQuadView.BAKE_LOCK_UV); // TODO glass UVs
            return true;
        };

        RenderContext.QuadTransform aluminumTransform = quad -> {
            quad.spriteBake(this.frame, MutableQuadView.BAKE_LOCK_UV);
            return true;
        };

        // Force white tint
        context.pushTransform(quad -> {
            quad.color(-1, -1, -1, -1);
            return true;
        });

        // FULL shape: render nothing
        if (up && down && north && east && south && west) {
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // STUB shape
        if (!up && !down && !north && !east && !south && !west) {
            context.pushTransform(aluminumTransform);
            Stub.emitStub(emitter);
            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // CAP shape
        Direction singleConnectedDirection = getSingleConnectedDirection(north, south, east, west, up, down);
        if (singleConnectedDirection != null) {
            context.pushTransform(aluminumTransform);
            Cap.emitCap(emitter, singleConnectedDirection);
            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // STRAIGHT or CORNER
        DirectionPair doubleConnectedDirection = getDoubleConnectedDirection(north, south, east, west, up, down);
        if (doubleConnectedDirection != null) {
            context.pushTransform(aluminumTransform);

            if (doubleConnectedDirection.a().getOpposite() == doubleConnectedDirection.b()) {
                Straight.emitStraight(emitter, doubleConnectedDirection);
            } else {
                boolean outerCorner = shouldUseOuterCorner(blockView, pos, doubleConnectedDirection);
                Corner.emitCorner(emitter, doubleConnectedDirection, !outerCorner);
            }

            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // T or TRI_CORNER shape
        DirectionTriple tripleConnectedDirection = getTripleConnectedDirection(north, south, east, west, up, down);
        if (tripleConnectedDirection != null) {
            context.pushTransform(aluminumTransform);
            emitTripleConnection(emitter, blockView, pos, tripleConnectedDirection);
            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // CROSS or SADDLE shape
        DirectionQuad quadConnectedDirection = getQuadConnectedDirection(north, south, east, west, up, down);
        if (quadConnectedDirection != null) {
            context.pushTransform(aluminumTransform);
            emitQuadConnection(emitter, blockView, pos, quadConnectedDirection);
            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // MISSING_ONE shape

        finish(blockView, state, pos, randomSupplier, context);
    }

    private void finish(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.popTransform();
        BakedModel.super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    /**
     * Emits the shape for a three-direction connection.
     *
     * @param emitter quad emitter
     * @param blockView world access used for diagonal checks
     * @param pos block position being rendered
     * @param triple triple of connected directions
     */
    private static void emitTripleConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            DirectionTriple triple
    ) {
        Direction[] dirs = triple.asArray();

        DirectionPair oppositePair = HelperMethods.findOppositePair(dirs);
        if (oppositePair != null) {
            Direction branch = HelperMethods.findRemainingDirection(dirs, oppositePair.a(), oppositePair.b());
            TConnection.emitTConnection(emitter, blockView, pos, oppositePair, branch);
        } else {
            TriCorner.emitTriCornerConnection(emitter, blockView, pos, triple.a(), triple.b(), triple.c());
        }
    }

    /**
     * Emits the shape for a four-direction connection.
     *
     * @param emitter quad emitter
     * @param blockView world access used for diagonal checks
     * @param pos block position being rendered
     * @param quad set of four connected directions
     */
    private static void emitQuadConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            DirectionQuad quad
    ) {
        Direction[] dirs = quad.asArray();

        boolean hasX = containsAxisPair(dirs, Direction.WEST, Direction.EAST);
        boolean hasY = containsAxisPair(dirs, Direction.DOWN, Direction.UP);
        boolean hasZ = containsAxisPair(dirs, Direction.NORTH, Direction.SOUTH);

        int oppositeAxes = 0;
        if (hasX) oppositeAxes++;
        if (hasY) oppositeAxes++;
        if (hasZ) oppositeAxes++;

        if (oppositeAxes == 2) {
            Cross.emitCrossConnection(emitter, blockView, pos, quad.a(), quad.b(), quad.c(), quad.d());
        } else {
            Saddle.emitSaddleConnection(emitter, blockView, pos, quad.a(), quad.b(), quad.c(), quad.d());
        }
    }

    /**
     * Checks whether a direction array contains both directions for a given axis.
     *
     * @param dirs directions to inspect
     * @param a first direction to look for
     * @param b second direction to look for
     * @return {@code true} if both directions are present
     */
    private static boolean containsAxisPair(Direction[] dirs, Direction a, Direction b) {
        boolean foundA = false;
        boolean foundB = false;

        for (Direction dir : dirs) {
            if (dir == a) foundA = true;
            else if (dir == b) foundB = true;
        }

        return foundA && foundB;
    }

    /**
     * Determines whether a perpendicular connection pair should render as an outer
     * corner based on adjacent occupancy.
     *
     * @param blockView world access
     * @param pos block position being rendered
     * @param pair connection pair to inspect
     * @return {@code true} if the outer-corner variant should be used
     */
    private static boolean shouldUseOuterCorner(BlockAndTintGetter blockView, BlockPos pos, DirectionPair pair) {
        BlockState stateA = blockView.getBlockState(pos.relative(pair.a()));
        BlockState stateB = blockView.getBlockState(pos.relative(pair.b()));
        if (stateA.isSolidRender(blockView, pos.relative(pair.a())) && stateB.isSolidRender(blockView, pos.relative(pair.b()))) return true;
        BlockPos diagonal = pos.relative(pair.a()).relative(pair.b());
        BlockState diagonalState = blockView.getBlockState(diagonal);

        return diagonalState.isSolidRender(blockView, diagonal) || diagonalState.is(GCBlocks.VACUUM_GLASS);
    }

    public static @Nullable Direction getSingleConnectedDirection(
            boolean north, boolean south,
            boolean east, boolean west,
            boolean up, boolean down
    ) {
        Direction found = null;

        if (north) {
            found = Direction.NORTH;
        }
        if (south) {
            if (found != null) return null;
            found = Direction.SOUTH;
        }
        if (east) {
            if (found != null) return null;
            found = Direction.EAST;
        }
        if (west) {
            if (found != null) return null;
            found = Direction.WEST;
        }
        if (up) {
            if (found != null) return null;
            found = Direction.UP;
        }
        if (down) {
            if (found != null) return null;
            found = Direction.DOWN;
        }

        return found;
    }

    public record DirectionPair(Direction a, Direction b) {
        public boolean contains(Direction face) {
            return this.a == face || this.b == face;
        }
    }

    public record DirectionTriple(Direction a, Direction b, Direction c) {
        public boolean contains(Direction face) {
            return this.a == face || this.b == face || this.c == face;
        }

        public Direction[] asArray() {
            return new Direction[] { a, b, c };
        }
    }

    public record DirectionQuad(Direction a, Direction b, Direction c, Direction d) {
        public boolean contains(Direction face) {
            return this.a == face || this.b == face || this.c == face || this.d == face;
        }

        public Direction[] asArray() {
            return new Direction[] { a, b, c, d };
        }
    }

    public static @Nullable DirectionPair getDoubleConnectedDirection(
            boolean north, boolean south,
            boolean east, boolean west,
            boolean up, boolean down
    ) {
        Direction firstFound = null;
        Direction secondFound = null;

        if (north) {
            firstFound = Direction.NORTH;
        }
        if (south) {
            if (firstFound == null) {
                firstFound = Direction.SOUTH;
            } else {
                secondFound = Direction.SOUTH;
            }
        }
        if (east) {
            if (secondFound != null) return null;
            if (firstFound == null) {
                firstFound = Direction.EAST;
            } else {
                secondFound = Direction.EAST;
            }
        }
        if (west) {
            if (secondFound != null) return null;
            if (firstFound == null) {
                firstFound = Direction.WEST;
            } else {
                secondFound = Direction.WEST;
            }
        }
        if (up) {
            if (secondFound != null) return null;
            if (firstFound == null) {
                firstFound = Direction.UP;
            } else {
                secondFound = Direction.UP;
            }
        }
        if (down) {
            if (secondFound != null) return null;
            if (firstFound == null) {
                firstFound = Direction.DOWN;
            } else {
                secondFound = Direction.DOWN;
            }
        }

        if (secondFound == null) {
            return null;
        }

        return new DirectionPair(firstFound, secondFound);
    }


    public static @Nullable DirectionTriple getTripleConnectedDirection(
            boolean north, boolean south,
            boolean east, boolean west,
            boolean up, boolean down
    ) {
        Direction[] found = new Direction[3];
        int count = 0;

        if (north) found[count++] = Direction.NORTH;
        if (south) {
            if (count >= 3) return null;
            found[count++] = Direction.SOUTH;
        }
        if (east) {
            if (count >= 3) return null;
            found[count++] = Direction.EAST;
        }
        if (west) {
            if (count >= 3) return null;
            found[count++] = Direction.WEST;
        }
        if (up) {
            if (count >= 3) return null;
            found[count++] = Direction.UP;
        }
        if (down) {
            if (count >= 3) return null;
            found[count++] = Direction.DOWN;
        }

        if (count != 3) return null;
        return new DirectionTriple(found[0], found[1], found[2]);
    }

    public static @Nullable DirectionQuad getQuadConnectedDirection(
            boolean north, boolean south,
            boolean east, boolean west,
            boolean up, boolean down
    ) {
        Direction[] found = new Direction[4];
        int count = 0;

        if (north) found[count++] = Direction.NORTH;
        if (south) {
            found[count++] = Direction.SOUTH;
        }
        if (east) {
            found[count++] = Direction.EAST;
        }
        if (west) {
            found[count++] = Direction.WEST;
        }
        if (up) {
            if (count >= 4) return null;
            found[count++] = Direction.UP;
        }
        if (down) {
            if (count >= 4) return null;
            found[count++] = Direction.DOWN;
        }

        if (count != 4) return null;
        return new DirectionQuad(found[0], found[1], found[2], found[3]);
    }

    /**
     * Emits item quads for the inventory representation of the model.
     *
     * @param stack item stack being rendered
     * @param randomSupplier random source supplier
     * @param context render context
     */
    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        BakedModel.super.emitItemQuads(stack, randomSupplier, context);
    }
}