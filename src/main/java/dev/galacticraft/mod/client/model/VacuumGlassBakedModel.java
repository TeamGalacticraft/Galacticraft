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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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
    private static final float FRAME_INSET = 4.0f / 16.0f;
    private static final float INNER_FRAME_INSET = 5.0f / 16.0f;
    private static final float PANE_INSET = 6.0f / 16.0f;
    private static final float FRAME_THICKNESS = 3.0f / 16.0f;
    private static final float INNER_FRAME_THICKNESS = 2.0f / 16.0f;

    // NON-CONFIGURABLE
    private static final float INVERTED_FRAME_INSET = 1.0f - FRAME_INSET;

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
            emitStub(emitter);
            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // CAP shape
        Direction singleConnectedDirection = getSingleConnectedDirection(north, south, east, west, up, down);
        if (singleConnectedDirection != null) {
            context.pushTransform(aluminumTransform);
            emitCap(emitter, singleConnectedDirection);
            context.popTransform();
            finish(blockView, state, pos, randomSupplier, context);
            return;
        }

        // STRAIGHT or CORNER
        DirectionPair doubleConnectedDirection = getDoubleConnectedDirection(north, south, east, west, up, down);
        if (doubleConnectedDirection != null) {
            context.pushTransform(aluminumTransform);

            if (doubleConnectedDirection.a().getOpposite() == doubleConnectedDirection.b()) {
                emitStraight(emitter, doubleConnectedDirection);
            } else {
                boolean outerCorner = shouldUseOuterCorner(blockView, pos, doubleConnectedDirection);
                emitCorner(emitter, doubleConnectedDirection, !outerCorner);
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

    // ---------------------------------------------------------------------
    // Shape dispatch helpers
    // ---------------------------------------------------------------------

    private void finish(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        context.popTransform();
        BakedModel.super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }

    /** Emits the disconnected stub shape. */
    private static void emitStub(QuadEmitter emitter) {
        emitBox(
                emitter,
                FRAME_INSET, FRAME_INSET, FRAME_INSET,
                INVERTED_FRAME_INSET, INVERTED_FRAME_INSET, INVERTED_FRAME_INSET
        );
    }

    /**
     * Emits the cap shape for a single directional connection.
     *
     * @param emitter quad emitter
     * @param connected connected direction
     */
    private static void emitCap(QuadEmitter emitter, Direction connected) {
        Box box = getCapBox(connected);
        emitBoxWithoutFace(emitter, box, connected);
    }

    /**
     * Emits a straight connection segment for two opposite directions.
     *
     * @param emitter quad emitter
     * @param pair connected direction pair
     */
    private static void emitStraight(QuadEmitter emitter, DirectionPair pair) {
        Box box = getExpandedBox(pair.a(), pair.b());
        emitBoxWithoutFaces(emitter, box, pair);
    }

    /**
     * Emits either an inner or outer corner for a perpendicular connection pair.
     *
     * @param emitter quad emitter
     * @param pair connected direction pair
     * @param outerCorner whether the outer-corner variant should be emitted
     */
    private static void emitCorner(QuadEmitter emitter, DirectionPair pair, boolean outerCorner) {
        Direction a = pair.a();
        Direction b = pair.b();

        if (outerCorner) {
            emitOuterCornerDiagonal(emitter, a, b);
        } else {
            emitInnerCornerDiagonals(emitter, a, b);
        }
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

        DirectionPair oppositePair = findOppositePair(dirs);
        if (oppositePair != null) {
            Direction branch = findRemainingDirection(dirs, oppositePair.a(), oppositePair.b());
            emitTConnection(emitter, blockView, pos, oppositePair, branch);
        } else {
            emitTriCornerConnection(emitter, blockView, pos, triple.a(), triple.b(), triple.c());
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
            emitCrossConnection(emitter, blockView, pos, quad.a(), quad.b(), quad.c(), quad.d());
        } else {
            emitSaddleConnection(emitter, blockView, pos, quad.a(), quad.b(), quad.c(), quad.d());
        }
    }

    // ---------------------------------------------------------------------
    // Connection analysis helpers
    // ---------------------------------------------------------------------

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

    private static @Nullable DirectionPair findOppositePair(Direction[] dirs) {
        for (int i = 0; i < dirs.length; i++) {
            for (int j = i + 1; j < dirs.length; j++) {
                if (dirs[i].getOpposite() == dirs[j]) {
                    return new DirectionPair(dirs[i], dirs[j]);
                }
            }
        }
        return null;
    }

    /**
     * Returns the direction from the array that is not equal to either of the
     * supplied directions.
     *
     * @param dirs source directions
     * @param a first direction to exclude
     * @param b second direction to exclude
     * @return the remaining direction
     */
    private static Direction findRemainingDirection(Direction[] dirs, Direction a, Direction b) {
        for (Direction dir : dirs) {
            if (dir != a && dir != b) return dir;
        }
        throw new IllegalStateException("No remaining direction");
    }

    // ---------------------------------------------------------------------
    // T-connection geometry
    // ---------------------------------------------------------------------

    /**
     * Emits the T-junction geometry for a straight pair and branch.
     *
     * @param emitter quad emitter
     * @param blockView world access used for diagonal checks
     * @param pos block position being rendered
     * @param straightPair opposite directions forming the stem of the T
     * @param branch perpendicular branch direction
     */
    private static void emitTConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            DirectionPair straightPair,
            Direction branch
    ) {
        if (straightPair.a().getAxis() != straightPair.b().getAxis()) {
            throw new IllegalArgumentException("T straight pair must be opposite on same axis");
        }
        if (straightPair.a().getOpposite() != straightPair.b()) {
            throw new IllegalArgumentException("T straight pair must be opposites");
        }
        if (branch.getAxis() == straightPair.a().getAxis()) {
            throw new IllegalArgumentException("T branch must be perpendicular to straight pair");
        }

        Direction.Axis mainAxis = straightPair.a().getAxis();
        Direction.Axis branchAxis = branch.getAxis();
        Direction.Axis thicknessAxis = remainingAxis(mainAxis, branchAxis);

        Direction negMain = negativeDirection(mainAxis);
        Direction posMain = positiveDirection(mainAxis);

        boolean leftFilled = isTDiagonalFilled(blockView, pos, negMain, branch);
        boolean rightFilled = isTDiagonalFilled(blockView, pos, posMain, branch);

        float F = FRAME_INSET;
        float I = INVERTED_FRAME_INSET;

        Direction topFace = positiveDirection(thicknessAxis);
        Direction bottomFace = negativeDirection(thicknessAxis);
        Direction backFace = branch.getOpposite();
        Direction leftFace = negMain;
        Direction rightFace = posMain;

        // Canonical points for T:
        // x = main axis (0 = negative side, 1 = positive side)
        // y = thickness axis
        // z = branch axis (0 = branch outer end, 1 = opposite/back)
        float[] A = tPoint(mainAxis, thicknessAxis, branch, 0.0F, F, I); // 0,F,I
        float[] B = tPoint(mainAxis, thicknessAxis, branch, 1.0F, F, I); // 1,F,I
        float[] C = tPoint(mainAxis, thicknessAxis, branch, 0.0F, I, I); // 0,I,I
        float[] D = tPoint(mainAxis, thicknessAxis, branch, 1.0F, I, I); // 1,I,I

        float[] E = tPoint(mainAxis, thicknessAxis, branch, 0.0F, F, F); // 0,F,F
        float[] H = tPoint(mainAxis, thicknessAxis, branch, 1.0F, F, F); // 1,F,F
        float[] G = tPoint(mainAxis, thicknessAxis, branch, 0.0F, I, F); // 0,I,F
        float[] J = tPoint(mainAxis, thicknessAxis, branch, 1.0F, I, F); // 1,I,F

        float[] Lb = tPoint(mainAxis, thicknessAxis, branch, F, F, 0.0F); // F,F,0
        float[] Lt = tPoint(mainAxis, thicknessAxis, branch, F, I, 0.0F); // F,I,0
        float[] Rb = tPoint(mainAxis, thicknessAxis, branch, I, F, 0.0F); // I,F,0
        float[] Rt = tPoint(mainAxis, thicknessAxis, branch, I, I, 0.0F); // I,I,0

        float[] LBb = tPoint(mainAxis, thicknessAxis, branch, 0.0F, F, 0.0F); // 0,F,0
        float[] LBt = tPoint(mainAxis, thicknessAxis, branch, 0.0F, I, 0.0F); // 0,I,0
        float[] RBb = tPoint(mainAxis, thicknessAxis, branch, 1.0F, F, 0.0F); // 1,F,0
        float[] RBt = tPoint(mainAxis, thicknessAxis, branch, 1.0F, I, 0.0F); // 1,I,0

        float[] shapeCenter;
        if (leftFilled && rightFilled) {
            shapeCenter = averagePoints(A, B, C, D, LBb, LBt, RBb, RBt);
        } else if (leftFilled) {
            shapeCenter = averagePoints(A, B, C, D, E, H, G, J, Rb, Rt, LBb, LBt);
        } else if (rightFilled) {
            shapeCenter = averagePoints(A, B, C, D, E, H, G, J, Lb, Lt, RBb, RBt);
        } else {
            shapeCenter = averagePoints(A, B, C, D, E, H, G, J, Lb, Lt, Rb, Rt);
        }

        // -------------------------
        // Variant 3: both diagonals solid
        // -------------------------
        if (leftFilled && rightFilled) {
            // Top full
            emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    D[0], D[1], D[2],
                    C[0], C[1], C[2],
                    LBt[0], LBt[1], LBt[2],
                    RBt[0], RBt[1], RBt[2]
            );

            // Bottom full
            emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    A[0], A[1], A[2],
                    B[0], B[1], B[2],
                    RBb[0], RBb[1], RBb[2],
                    LBb[0], LBb[1], LBb[2]
            );

            // Back side
            emitCustomQuad(
                    emitter, backFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    B[0], B[1], B[2],
                    A[0], A[1], A[2],
                    C[0], C[1], C[2],
                    D[0], D[1], D[2]
            );

            return;
        }

        // -------------------------
        // Shared faces for variant 1 and 2/left-mirror
        // -------------------------

        // Top part 1
        emitCustomQuad(
                emitter, topFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                D[0], D[1], D[2],
                C[0], C[1], C[2],
                G[0], G[1], G[2],
                J[0], J[1], J[2]
        );

        // Bottom part 1
        emitCustomQuad(
                emitter, bottomFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                A[0], A[1], A[2],
                B[0], B[1], B[2],
                H[0], H[1], H[2],
                E[0], E[1], E[2]
        );

        // Back side
        emitCustomQuad(
                emitter, backFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                B[0], B[1], B[2],
                A[0], A[1], A[2],
                C[0], C[1], C[2],
                D[0], D[1], D[2]
        );

        // -------------------------
        // Variant 2 mirrored left/right or variant 1
        // -------------------------

        if (!leftFilled && !rightFilled) {
            // Top part 2
            emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    J[0], J[1], J[2],
                    G[0], G[1], G[2],
                    Lt[0], Lt[1], Lt[2],
                    Rt[0], Rt[1], Rt[2]
            );

            // Bottom part 2
            emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    H[0], H[1], H[2],
                    Rb[0], Rb[1], Rb[2],
                    Lb[0], Lb[1], Lb[2]
            );

            // Left diagonal side
            emitCustomQuad(
                    emitter, leftFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    Lb[0], Lb[1], Lb[2],
                    Lt[0], Lt[1], Lt[2],
                    G[0], G[1], G[2]
            );

            // Right diagonal side
            emitCustomQuad(
                    emitter, rightFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    Rb[0], Rb[1], Rb[2],
                    H[0], H[1], H[2],
                    J[0], J[1], J[2],
                    Rt[0], Rt[1], Rt[2]
            );

            return;
        }

        if (!leftFilled && rightFilled) {
            // Top part 2
            emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    J[0], J[1], J[2],
                    G[0], G[1], G[2],
                    Lt[0], Lt[1], Lt[2],
                    RBt[0], RBt[1], RBt[2]
            );

            // Bottom part 2
            emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    H[0], H[1], H[2],
                    RBb[0], RBb[1], RBb[2],
                    Lb[0], Lb[1], Lb[2]
            );

            // Left diagonal side only
            emitCustomQuad(
                    emitter, leftFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    Lb[0], Lb[1], Lb[2],
                    Lt[0], Lt[1], Lt[2],
                    G[0], G[1], G[2]
            );

            return;
        }

        if (leftFilled && !rightFilled) {
            // Mirror of shape 2

            // Top part 2
            emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    J[0], J[1], J[2],
                    G[0], G[1], G[2],
                    LBt[0], LBt[1], LBt[2],
                    Rt[0], Rt[1], Rt[2]
            );

            // Bottom part 2
            emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    H[0], H[1], H[2],
                    Rb[0], Rb[1], Rb[2],
                    LBb[0], LBb[1], LBb[2]
            );

            // Right diagonal side only
            emitCustomQuad(
                    emitter, rightFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    Rb[0], Rb[1], Rb[2],
                    H[0], H[1], H[2],
                    J[0], J[1], J[2],
                    Rt[0], Rt[1], Rt[2]
            );
        }
    }

    /**
     * Checks whether the T-junction diagonal position should be considered filled.
     *
     * @param blockView world access
     * @param pos block position being rendered
     * @param mainSide side along the main axis
     * @param branch branch direction
     * @return {@code true} if the diagonal is solid or vacuum glass
     */
    private static boolean isTDiagonalFilled(
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction mainSide,
            Direction branch
    ) {
        BlockPos diagonal = pos.relative(mainSide).relative(branch);
        BlockState diagonalState = blockView.getBlockState(diagonal);
        return diagonalState.isSolidRender(blockView, diagonal) || diagonalState.is(GCBlocks.VACUUM_GLASS);
    }

    /**
     * Builds a point in the canonical T-junction coordinate space and maps it to
     * world coordinates.
     *
     * @param mainAxis main axis for the T stem
     * @param thicknessAxis thickness axis of the emitted geometry
     * @param branch branch direction
     * @param x canonical main-axis coordinate
     * @param y canonical thickness coordinate
     * @param z canonical branch coordinate
     * @return the mapped point as an XYZ float array
     */
    private static float[] tPoint(
            Direction.Axis mainAxis,
            Direction.Axis thicknessAxis,
            Direction branch,
            float x, float y, float z
    ) {
        float[] xyz = new float[3];

        setAxis(xyz, mainAxis, x);
        setAxis(xyz, thicknessAxis, y);

        float branchCoord = branch.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? z : (1.0F - z);
        setAxis(xyz, branch.getAxis(), branchCoord);

        return xyz;
    }

    // ---------------------------------------------------------------------
    // Tri-corner geometry
    // ---------------------------------------------------------------------

    /** Emits a tri-corner connection shape. */
    private static void emitTriCornerConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction a,
            Direction b,
            Direction c
    ) {
        emitTriCornerNoDiagonals(emitter, blockView, pos, a, b, c);
    }

    /**
     * Emits the tri-corner variant that does not include diagonal fill geometry.
     *
     * @param emitter quad emitter
     * @param blockView world access used for neighbor checks
     * @param pos block position being rendered
     * @param a first direction
     * @param b second direction
     * @param c third direction
     */
    private static void emitTriCornerNoDiagonals(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction a,
            Direction b,
            Direction c
    ) {
        Direction xConn = null;
        Direction yConn = null;
        Direction zConn = null;

        for (Direction dir : new Direction[]{a, b, c}) {
            switch (dir.getAxis()) {
                case X -> xConn = dir;
                case Y -> yConn = dir;
                case Z -> zConn = dir;
            }
        }

        if (xConn == null || yConn == null || zConn == null) {
            throw new IllegalArgumentException("Tri-corner requires one connection on each axis: " + a + ", " + b + ", " + c);
        }

        float F = FRAME_INSET;
        float I = INVERTED_FRAME_INSET;

        float[] vertex0  = triCornerPoint(F, F, 0.0F, xConn, yConn, zConn);
        float[] vertex1  = triCornerPoint(0.0F, F, F, xConn, yConn, zConn);
        float[] vertex2  = triCornerPoint(I, F, 0.0F, xConn, yConn, zConn);
        float[] vertex3  = triCornerPoint(0.0F, F, I, xConn, yConn, zConn);
        float[] vertex4  = triCornerPoint(0.0F, I, I, xConn, yConn, zConn);
        float[] vertex5  = triCornerPoint(F, 1.0F, I, xConn, yConn, zConn);
        float[] vertex6 = triCornerPoint(I, 1.0F, I, xConn, yConn, zConn);
        float[] vertex7 = triCornerPoint(I, 1.0F, F, xConn, yConn, zConn);
        float[] vertex8 = triCornerPoint(I, I, 0.0F, xConn, yConn, zConn);
        float[] vertex9 = triCornerPoint(0.0F, I, F, xConn, yConn, zConn);
        float[] vertex10 = triCornerPoint(F, I, 0.0F, xConn, yConn, zConn);
        float[] vertex11 = triCornerPoint(F, 1.0F, F, xConn, yConn, zConn);
        float[] vertex12 = triCornerPoint(0.0F, I, 0.0F, xConn, yConn, zConn);
        float[] vertex13 = triCornerPoint(F, 1.0F, 0.0F, xConn, yConn, zConn);
        float[] vertex14 = triCornerPoint(0.0F, 1.0F, F, xConn, yConn, zConn);
        float[] vertex15 = triCornerPoint(0.0F, F, 0.0F, xConn, yConn, zConn);
        float[] vertex16 = triCornerPoint(0.0F, 1.0F, I, xConn, yConn, zConn);
        float[] vertex17 = triCornerPoint(I, 1.0F, 0.0F, xConn, yConn, zConn);

        float[] shapeCenter = averagePoints(
                vertex0, vertex1, vertex2, vertex3,
                vertex4, vertex5, vertex6, vertex7,
                vertex8, vertex9, vertex10, vertex11,
                vertex12, vertex13, vertex14, vertex15,
                vertex16, vertex17
        );

        BlockPos relativeA = pos.relative(a);
        BlockState stateA = blockView.getBlockState(relativeA);
        BlockPos relativeB = pos.relative(b);
        BlockState stateB = blockView.getBlockState(relativeB);
        BlockPos relativeC = pos.relative(c);
        BlockState stateC = blockView.getBlockState(relativeC);
        BlockPos relativeAB = relativeA.relative(b);
        BlockState stateAB = blockView.getBlockState(relativeAB);
        BlockPos relativeAC = relativeA.relative(c);
        BlockState stateAC = blockView.getBlockState(relativeAC);
        BlockPos relativeBC = relativeB.relative(c);
        BlockState stateBC = blockView.getBlockState(relativeBC);
        BlockPos relativeABC = relativeAB.relative(c);
        BlockState stateABC = blockView.getBlockState(relativeABC);

        // outer tri   canonical hint = (-1, +1, -1)
        emitCustomTri(
                emitter,
                nominalFaceFromHint(-1.0F, 1.0F, -1.0F, xConn, yConn, zConn),
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                vertex3[0], vertex3[1], vertex3[2],
                vertex2[0], vertex2[1], vertex2[2],
                vertex6[0], vertex6[1], vertex6[2]
        );

        // inner tri   canonical hint = (+1, -1, +1)
        if (
                ((stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) && (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS))) ||
                        ((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS))) ||
                        ((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS)))
        ) {
            if ((stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) && (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS))) {
                if (((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS))) &&
                        ((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS)))) {
                    if ((!stateABC.isSolidRender(blockView, relativeABC) && !stateABC.is(GCBlocks.VACUUM_GLASS))) {
                        emitCustomTri(
                                emitter,
                                nominalFaceFromHint(1.0F, -1.0F, 1.0F, xConn, yConn, zConn),
                                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                                vertex12[0], vertex12[1], vertex12[2],
                                vertex13[0], vertex13[1], vertex13[2],
                                vertex14[0], vertex14[1], vertex14[2]
                        );
                    }
                } else {
                    emitCustomQuad(
                            emitter,
                            nominalFaceFromHint(1.0F, 0.0F, 1.0F, xConn, yConn, zConn),
                            shapeCenter[0], shapeCenter[1], shapeCenter[2],
                            vertex9[0], vertex9[1], vertex9[2],
                            vertex14[0], vertex14[1], vertex14[2],
                            vertex13[0], vertex13[1], vertex13[2],
                            vertex10[0], vertex10[1], vertex10[2]
                    );
                }
            } else if ((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS))) {
                emitCustomQuad(
                        emitter,
                        nominalFaceFromHint(1.0F, 0.0F, 1.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex9[0], vertex9[1], vertex9[2],
                        vertex11[0], vertex11[1], vertex11[2],
                        vertex13[0], vertex13[1], vertex13[2],
                        vertex12[0], vertex12[1], vertex12[2]
                );
            } else if ((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS))) {
                emitCustomQuad(
                        emitter,
                        nominalFaceFromHint(1.0F, 0.0F, 1.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex10[0], vertex10[1], vertex10[2],
                        vertex11[0], vertex11[1], vertex11[2],
                        vertex14[0], vertex14[1], vertex14[2],
                        vertex12[0], vertex12[1], vertex12[2]
                );
            }
        } else {
            emitCustomTri(
                    emitter,
                    nominalFaceFromHint(1.0F, -1.0F, 1.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex10[0], vertex10[1], vertex10[2],
                    vertex9[0], vertex9[1], vertex9[2],
                    vertex11[0], vertex11[1], vertex11[2]
            );
        }

        // bottom quad canonical hint = (0, +1, 0)
        emitCustomQuad(
                emitter,
                nominalFaceFromHint(0.0F, 1.0F, 0.0F, xConn, yConn, zConn),
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                vertex0[0], vertex0[1], vertex0[2],
                vertex2[0], vertex2[1], vertex2[2],
                vertex3[0], vertex3[1], vertex3[2],
                vertex1[0], vertex1[1], vertex1[2]
        );

        // +X quad canonical hint = (-1, 0, 0)
        emitCustomQuad(
                emitter,
                nominalFaceFromHint(-1.0F, 0.0F, 0.0F, xConn, yConn, zConn),
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                vertex3[0], vertex3[1], vertex3[2],
                vertex6[0], vertex6[1], vertex6[2],
                vertex5[0], vertex5[1], vertex5[2],
                vertex4[0], vertex4[1], vertex4[2]
        );

        // +Z quad canonical hint = (0, 0, -1)
        emitCustomQuad(
                emitter,
                nominalFaceFromHint(0.0F, 0.0F, -1.0F, xConn, yConn, zConn),
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                vertex2[0], vertex2[1], vertex2[2],
                vertex8[0], vertex8[1], vertex8[2],
                vertex7[0], vertex7[1], vertex7[2],
                vertex6[0], vertex6[1], vertex6[2]
        );

        // inner corner quad canonical hint = (+1, 0, +1)
        if ((stateA.isSolidRender(blockView, relativeA) && stateB.isSolidRender(blockView, relativeB)) || stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) {
            // inner
            if ((stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) || (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS))) {

            } else {
                emitCustomTri(
                        emitter,
                        nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex10[0], vertex10[1], vertex10[2],
                        vertex9[0], vertex9[1], vertex9[2],
                        vertex12[0], vertex12[1], vertex12[2]
                );
            }

            // outer
            emitCustomTri(
                    emitter,
                    nominalFaceFromHint(0.0F, 1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex0[0], vertex0[1], vertex0[2],
                    vertex15[0], vertex15[1], vertex15[2],
                    vertex1[0], vertex1[1], vertex1[2]
            );
        } else {
            emitCustomQuad(
                    emitter,
                    nominalFaceFromHint(1.0F, 0.0F, 1.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex0[0], vertex0[1], vertex0[2],
                    vertex1[0], vertex1[1], vertex1[2],
                    vertex9[0], vertex9[1], vertex9[2],
                    vertex10[0], vertex10[1], vertex10[2]
            );
        }

        // -X direction quad canonical hint = (+1, -1, 0)
        if ((stateB.isSolidRender(blockView, relativeB) && stateC.isSolidRender(blockView, relativeC)) || stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS)) {
            // inner
            if ((stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) || (stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS))) {

            } else {
                emitCustomTri(
                        emitter,
                        nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex9[0], vertex9[1], vertex9[2],
                        vertex14[0], vertex14[1], vertex14[2],
                        vertex11[0], vertex11[1], vertex11[2]
                );
            }
            // outer
            emitCustomTri(
                    emitter,
                    nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex4[0], vertex4[1], vertex4[2],
                    vertex5[0], vertex5[1], vertex5[2],
                    vertex16[0], vertex16[1], vertex16[2]
            );
        } else {
            emitCustomQuad(
                    emitter,
                    nominalFaceFromHint(1.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex9[0], vertex9[1], vertex9[2],
                    vertex4[0], vertex4[1], vertex4[2],
                    vertex5[0], vertex5[1], vertex5[2],
                    vertex11[0], vertex11[1], vertex11[2]
            );
        }

        // -Z direction quad canonical hint = (0, -1, +1)
        if ((stateA.isSolidRender(blockView, relativeA) && stateC.isSolidRender(blockView, relativeC)) || stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) {
            // inner
            if ((stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS)) || (stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS))) {

            } else {
                emitCustomTri(
                        emitter,
                        nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex10[0], vertex10[1], vertex10[2],
                        vertex11[0], vertex11[1], vertex11[2],
                        vertex13[0], vertex13[1], vertex13[2]
                );
            }

            // outer
            emitCustomTri(
                    emitter,
                    nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex8[0], vertex8[1], vertex8[2],
                    vertex17[0], vertex17[1], vertex17[2],
                    vertex7[0], vertex7[1], vertex7[2]
            );
        } else {
            emitCustomQuad(
                    emitter,
                    nominalFaceFromHint(0.0F, -1.0F, 1.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex8[0], vertex8[1], vertex8[2],
                    vertex10[0], vertex10[1], vertex10[2],
                    vertex11[0], vertex11[1], vertex11[2],
                    vertex7[0], vertex7[1], vertex7[2]
            );
        }
    }

    // ---------------------------------------------------------------------
    // Cross and saddle geometry
    // ---------------------------------------------------------------------

    /**
     * Emits the saddle variant for a four-direction connection.
     *
     * @param emitter quad emitter
     * @param blockView world access used for neighbor checks
     * @param pos block position being rendered
     * @param a first direction
     * @param b second direction
     * @param c third direction
     * @param d fourth direction
     */
    private static void emitSaddleConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction a, Direction b, Direction c, Direction d
    ) {
        Direction[] dirs = new Direction[]{a, b, c, d};

        DirectionPair oppositePair = findOppositePair(dirs);
        if (oppositePair == null) {
            throw new IllegalArgumentException(
                    "Saddle requires exactly one opposite pair: " +
                            a + ", " + b + ", " + c + ", " + d
            );
        }

        Direction[] remaining = findRemainingDirections(dirs, oppositePair.a(), oppositePair.b());
        if (remaining.length != 2) {
            throw new IllegalStateException(
                    "Saddle expected two remaining directions but got " + remaining.length
            );
        }

        SaddleBasis basis = getSaddleBasis(oppositePair, remaining[0], remaining[1]);

        float F = FRAME_INSET;
        float I = INVERTED_FRAME_INSET;

        // Local axis meaning:
        //   X = opposite pair axis
        //   Y = first non-opposite direction
        //   Z = second non-opposite direction

        float[] vertex0 = saddlePoint(0.0F, I, F, basis);
        float[] vertex1 = saddlePoint(0.0F, I, I, basis);
        float[] vertex2 = saddlePoint(F, I, 1.0F, basis);
        float[] vertex3 = saddlePoint(I, I, 1.0F, basis);
        float[] vertex4 = saddlePoint(1.0F, I, I, basis);
        float[] vertex5 = saddlePoint(1.0F, I, F, basis);
        float[] vertex6 = saddlePoint(F, 1.0F, F, basis);
        float[] vertex7 = saddlePoint(F, 1.0F, I, basis);
        float[] vertex8 = saddlePoint(I, 1.0F, I, basis);
        float[] vertex9 = saddlePoint(I, 1.0F, F, basis);
        float[] vertex10 = saddlePoint(0.0F, F, F, basis);
        float[] vertex11 = saddlePoint(0.0F, F, I, basis);
        float[] vertex12 = saddlePoint(F, F, 1.0F, basis);
        float[] vertex13 = saddlePoint(I, F, 1.0F, basis);
        float[] vertex14 = saddlePoint(1.0F, F, I, basis);
        float[] vertex15 = saddlePoint(1.0F, F, F, basis);
        float[] vertex16 = saddlePoint(1.0F, I, 1.0F, basis);
        float[] vertex17 = saddlePoint(0.0F, I, 1.0F, basis);
        float[] vertex24 = saddlePoint(1.0F, 1.0F, I, basis);
        float[] vertex21 = saddlePoint(0.0F, 1.0F, I, basis);

        float[] shapeCenter = averagePoints(
                vertex0, vertex1, vertex2, vertex3,
                vertex4, vertex5, vertex6, vertex7,
                vertex8, vertex9, vertex10, vertex11,
                vertex14, vertex15, vertex16, vertex17,
                vertex24, vertex21
        );

        // Canonical 2-step diagonals
        BlockPos relativeXPosYPos = pos.relative(basis.xPos()).relative(basis.yPos());
        BlockPos relativeXNegYPos = pos.relative(basis.xNeg()).relative(basis.yPos());
        BlockPos relativeXPosZPos = pos.relative(basis.xPos()).relative(basis.zPos());
        BlockPos relativeXNegZPos = pos.relative(basis.xNeg()).relative(basis.zPos());
        BlockPos relativeYPosZPos = pos.relative(basis.yPos()).relative(basis.zPos());

        BlockState stateXPosYPos = blockView.getBlockState(relativeXPosYPos);
        BlockState stateXNegYPos = blockView.getBlockState(relativeXNegYPos);
        BlockState stateXPosZPos = blockView.getBlockState(relativeXPosZPos);
        BlockState stateXNegZPos = blockView.getBlockState(relativeXNegZPos);
        BlockState stateYPosZPos = blockView.getBlockState(relativeYPosZPos);

        // +Z,+Y slant
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex2,
                vertex3,
                vertex8,
                vertex7
        );

        // -X,+Y slant
        if (stateXNegYPos.isSolidRender(blockView, relativeXNegYPos) || stateXNegYPos.is(GCBlocks.VACUUM_GLASS)) {
            emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex7,
                    vertex21
            );
        } else {
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex0,
                    vertex1,
                    vertex7,
                    vertex6
            );
        }


        if (stateXPosYPos.isSolidRender(blockView, relativeXPosYPos) || stateXPosYPos.is(GCBlocks.VACUUM_GLASS)) {
            emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex24,
                    vertex8
            );
        } else {
            // +X,+Y slant
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex5,
                    vertex9,
                    vertex8
            );
        }

        if ((stateXNegYPos.isSolidRender(blockView, relativeXNegYPos) || stateXNegYPos.is(GCBlocks.VACUUM_GLASS)) &&
                (stateXNegZPos.isSolidRender(blockView, relativeXNegZPos) || stateXNegZPos.is(GCBlocks.VACUUM_GLASS))) {
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex17,
                    vertex2,
                    vertex7,
                    vertex21
            );
        } else {
            // -X,+Z,+Y triangle
            emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex2,
                    vertex7
            );
        }

        if ((stateXPosYPos.isSolidRender(blockView, relativeXPosYPos) || stateXPosYPos.is(GCBlocks.VACUUM_GLASS)) &&
                (stateXPosZPos.isSolidRender(blockView, relativeXPosZPos) || stateXPosZPos.is(GCBlocks.VACUUM_GLASS))) {
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex3,
                    vertex16,
                    vertex24,
                    vertex8
            );
        } else {
            // +X,+Z,+Y triangle
            emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex3,
                    vertex4,
                    vertex8
            );
        }

        if (stateXNegZPos.isSolidRender(blockView, relativeXNegZPos) || stateXNegZPos.is(GCBlocks.VACUUM_GLASS)) {
            emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex17,
                    vertex2,
                    vertex1
            );
        } else {
            // -X,+Z edge
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex11,
                    vertex12,
                    vertex2,
                    vertex1
            );
        }

        if (stateXPosZPos.isSolidRender(blockView, relativeXPosZPos) || stateXPosZPos.is(GCBlocks.VACUUM_GLASS)) {
            emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex3,
                    vertex16,
                    vertex4
            );
        } else {
            // +X,+Z edge
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex13,
                    vertex14,
                    vertex4,
                    vertex3
            );
        }

        // -Z face
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex5,
                vertex0,
                vertex6,
                vertex9
        );

        // -Z second face
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex15,
                vertex10,
                vertex0,
                vertex5
        );

        // -Y face
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex14,
                vertex13,
                vertex12,
                vertex11
        );

        // -Y second face
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex14,
                vertex11,
                vertex10,
                vertex15
        );
    }

    /**
     * Local basis used to transform canonical saddle coordinates into world space.
     *
     * xPos / xNeg = the opposite pair axis
     * yPos        = first remaining saddle direction
     * zPos        = second remaining saddle direction
     */
    private record SaddleBasis(Vec3 origin, Direction xPos, Direction xNeg, Direction yPos, Direction zPos) {}

    /**
     * Builds a canonical local basis for a saddle connection.
     *
     * The opposite pair is mapped to local X.
     * The two remaining directions are mapped to local Y and Z.
     */
    private static SaddleBasis getSaddleBasis(DirectionPair oppositePair, Direction remainingA, Direction remainingB) {
        Direction xPos = positiveDirectionOfPair(oppositePair.a(), oppositePair.b());
        Direction xNeg = xPos.getOpposite();

        Direction yPos;
        Direction zPos;

        // Prefer vertical as Y when available, because it tends to make the
        // canonical saddle easier to reason about when writing the base shape.
        if (remainingA.getAxis().isVertical() && !remainingB.getAxis().isVertical()) {
            yPos = remainingA;
            zPos = remainingB;
        } else if (remainingB.getAxis().isVertical() && !remainingA.getAxis().isVertical()) {
            yPos = remainingB;
            zPos = remainingA;
        } else {
            yPos = remainingA;
            zPos = remainingB;
        }

        Vec3 origin = new Vec3(
                Math.max(0, -xPos.getStepX()) + Math.max(0, -yPos.getStepX()) + Math.max(0, -zPos.getStepX()),
                Math.max(0, -xPos.getStepY()) + Math.max(0, -yPos.getStepY()) + Math.max(0, -zPos.getStepY()),
                Math.max(0, -xPos.getStepZ()) + Math.max(0, -yPos.getStepZ()) + Math.max(0, -zPos.getStepZ())
        );

        return new SaddleBasis(origin, xPos, xNeg, yPos, zPos);
    }

    /**
     * Maps a canonical saddle-local point into world coordinates.
     *
     * @param x local X coordinate along the opposite-pair axis
     * @param y local Y coordinate along the first remaining direction
     * @param z local Z coordinate along the second remaining direction
     * @param basis saddle basis used for mapping
     * @return mapped point as an XYZ float array
     */
    private static float[] saddlePoint(float x, float y, float z, SaddleBasis basis) {
        Vec3 p = basis.origin()
                .add(dirVec(basis.xPos()).scale(x))
                .add(dirVec(basis.yPos()).scale(y))
                .add(dirVec(basis.zPos()).scale(z));

        return new float[]{(float) p.x, (float) p.y, (float) p.z};
    }

    /**
     * Chooses the positive-facing direction from an opposite pair so the
     * canonical saddle basis is stable.
     */
    private static Direction positiveDirectionOfPair(Direction a, Direction b) {
        if (a.getAxis() != b.getAxis() || a.getOpposite() != b) {
            throw new IllegalArgumentException("Directions are not an opposite pair: " + a + ", " + b);
        }

        return switch (a.getAxis()) {
            case X -> a == Direction.EAST  ? a : b;
            case Y -> a == Direction.UP    ? a : b;
            case Z -> a == Direction.SOUTH ? a : b;
        };
    }

    /**
     * Returns the directions from the set that are not the supplied excluded pair.
     */
    private static Direction[] findRemainingDirections(Direction[] dirs, Direction excludedA, Direction excludedB) {
        Direction[] result = new Direction[2];
        int index = 0;

        for (Direction dir : dirs) {
            if (dir != excludedA && dir != excludedB) {
                if (index >= 2) {
                    throw new IllegalStateException("More than two remaining directions found.");
                }
                result[index++] = dir;
            }
        }

        if (index != 2) {
            throw new IllegalStateException("Expected two remaining directions, found " + index);
        }

        return result;
    }

    /**
     * Emits the cross-connection geometry for four directions on a plane.
     *
     * @param emitter quad emitter
     * @param blockView world access used for diagonal checks
     * @param pos block position being rendered
     * @param a first direction
     * @param b second direction
     * @param c third direction
     * @param d fourth direction
     */
    private static void emitCrossConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction a, Direction b, Direction c, Direction d
    ) {
        Direction normal = findCrossNormal(a, b, c, d);
        FaceBasis basis = getFaceBasis(normal);

        float F = FRAME_INSET;
        float I = INVERTED_FRAME_INSET;

        float[] vertex0  = facePoint(0.0F, I, 0.0F, basis);
        float[] vertex1  = facePoint(0.0F, I, F, basis);
        float[] vertex2  = facePoint(0.0F, I, I, basis);
        float[] vertex3  = facePoint(0.0F, I, 1.0F, basis);
        float[] vertex4  = facePoint(F, I, 1.0F, basis);
        float[] vertex5  = facePoint(I, I, 1.0F, basis);
        float[] vertex6  = facePoint(1.0F, I, 1.0F, basis);
        float[] vertex7  = facePoint(1.0F, I, I, basis);
        float[] vertex8  = facePoint(1.0F, I, F, basis);
        float[] vertex9  = facePoint(1.0F, I, 0.0F, basis);
        float[] vertex10  = facePoint(I, I, 0.0F, basis);
        float[] vertex11  = facePoint(F, I, 0.0F, basis);
        float[] vertex12  = facePoint(F, I, F, basis);
        float[] vertex13  = facePoint(F, I, I, basis);
        float[] vertex14  = facePoint(I, I, I, basis);
        float[] vertex15  = facePoint(I, I, F, basis);
        float[] vertex16  = facePoint(0.0F, F, 0.0F, basis);
        float[] vertex17  = facePoint(0.0F, F, F, basis);
        float[] vertex18  = facePoint(0.0F, F, I, basis);
        float[] vertex19  = facePoint(0.0F, F, 1.0F, basis);
        float[] vertex20  = facePoint(F, F, 1.0F, basis);
        float[] vertex21  = facePoint(I, F, 1.0F, basis);
        float[] vertex22  = facePoint(1.0F, F, 1.0F, basis);
        float[] vertex23  = facePoint(1.0F, F, I, basis);
        float[] vertex24  = facePoint(1.0F, F, F, basis);
        float[] vertex25  = facePoint(1.0F, F, 0.0F, basis);
        float[] vertex26  = facePoint(I, F, 0.0F, basis);
        float[] vertex27  = facePoint(F, F, 0.0F, basis);
        float[] vertex28  = facePoint(F, F, F, basis);
        float[] vertex29  = facePoint(F, F, I, basis);
        float[] vertex30  = facePoint(I, F, I, basis);
        float[] vertex31  = facePoint(I, F, F, basis);

        float[] shapeCenter = averagePoints(
                vertex0, vertex1, vertex2, vertex3,
                vertex4, vertex5, vertex6, vertex7,
                vertex8, vertex9, vertex10, vertex11,
                vertex12, vertex13, vertex14, vertex15,
                vertex16, vertex17, vertex18, vertex19,
                vertex20, vertex21, vertex22, vertex23,
                vertex24, vertex25, vertex26, vertex27,
                vertex28, vertex29, vertex30, vertex31
        );

        BlockPos relativeAC = pos.relative(a).relative(c);
        BlockState stateAC = blockView.getBlockState(relativeAC);
        BlockPos relativeCB = pos.relative(c).relative(b);
        BlockState stateCB = blockView.getBlockState(relativeCB);
        BlockPos relativeBD = pos.relative(b).relative(d);
        BlockState stateBD = blockView.getBlockState(relativeBD);
        BlockPos relativeDA = pos.relative(d).relative(a);
        BlockState stateDA = blockView.getBlockState(relativeDA);

        // emit top center quad
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex12,
                vertex13,
                vertex14,
                vertex15
        );
        // emit bottom center quad
        emitCustomQuad(
                emitter,
                shapeCenter,
                vertex31,
                vertex30,
                vertex29,
                vertex28
        );

        if (stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) {
            // emit +X side with corner top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex10,
                    vertex14,
                    vertex7,
                    vertex9
            );
            // emit +X side with corner bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex25,
                    vertex23,
                    vertex30,
                    vertex26
            );
        } else {
            // emit +X,-Z side
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex26,
                    vertex10,
                    vertex8,
                    vertex24
            );

            // emit +X side with corner missing top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex10,
                    vertex14,
                    vertex7,
                    vertex8
            );
            // emit +X side with corner missing bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex24,
                    vertex23,
                    vertex30,
                    vertex26
            );
        }

        if (stateCB.isSolidRender(blockView, relativeCB) || stateCB.is(GCBlocks.VACUUM_GLASS)) {
            // emit +Z side with corner top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex7,
                    vertex13,
                    vertex4,
                    vertex6
            );
            // emit +Z side with corner bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex22,
                    vertex20,
                    vertex29,
                    vertex23
            );
        } else {
            // emit +X,+Z side
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex23,
                    vertex7,
                    vertex5,
                    vertex21
            );

            // emit +Z side with corner missing top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex7,
                    vertex13,
                    vertex4,
                    vertex5
            );
            // emit +Z side with corner missing bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex21,
                    vertex20,
                    vertex29,
                    vertex23
            );
        }

        if (stateBD.isSolidRender(blockView, relativeBD) || stateBD.is(GCBlocks.VACUUM_GLASS)) {
            // emit -X side with corner top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex12,
                    vertex1,
                    vertex3
            );
            // emit -X side with corner bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex19,
                    vertex17,
                    vertex28,
                    vertex20
            );
        } else {
            // emit -x,+z side
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex20,
                    vertex4,
                    vertex2,
                    vertex18
            );

            // emit -X side with corner missing top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex12,
                    vertex1,
                    vertex2
            );
            // emit -X side with corner missing bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex18,
                    vertex17,
                    vertex28,
                    vertex20
            );
        }

        if (stateDA.isSolidRender(blockView, relativeDA) || stateDA.is(GCBlocks.VACUUM_GLASS)) {
            // emit -Z side with corner top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex15,
                    vertex10,
                    vertex0
            );
            // emit -Z side with corner bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex16,
                    vertex26,
                    vertex31,
                    vertex17
            );
        } else {
            // emit -x,-z side
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex17,
                    vertex1,
                    vertex11,
                    vertex27
            );

            // emit -Z side with corner missing top
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex15,
                    vertex10,
                    vertex11
            );
            // emit -Z side with corner missing bottom
            emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex27,
                    vertex26,
                    vertex31,
                    vertex17
            );
        }

    }

    // ---------------------------------------------------------------------
    // Face basis and orientation helpers
    // ---------------------------------------------------------------------

    /**
     * Computes the nearest cardinal face for a quad from its vertex winding.
     *
     * @param a first vertex
     * @param b second vertex
     * @param c third vertex
     * @param d fourth vertex
     * @return the nearest nominal face direction
     */
    private static Direction nominalFaceFromQuad(Vec3 a, Vec3 b, Vec3 c, Vec3 d) {
        Vec3 ab = b.subtract(a);
        Vec3 ac = c.subtract(a);
        Vec3 normal = ab.cross(ac).normalize();
        return Direction.getNearest((float) normal.x, (float) normal.y, (float) normal.z);
    }

    /**
     * Local basis used to transform face-relative coordinates into world space.
     */
    private record FaceBasis(Vec3 origin, Direction right, Direction up, Direction normal, boolean rotateCCW) {}

    private static FaceBasis makeFaceBasis(Direction right, Direction up, Direction normal, boolean rotateCCW) {
        Vec3 origin = new Vec3(
                Math.max(0, -right.getStepX()) + Math.max(0, -up.getStepX()) + Math.max(0, -normal.getStepX()),
                Math.max(0, -right.getStepY()) + Math.max(0, -up.getStepY()) + Math.max(0, -normal.getStepY()),
                Math.max(0, -right.getStepZ()) + Math.max(0, -up.getStepZ()) + Math.max(0, -normal.getStepZ())
        );

        return new FaceBasis(origin, right, up, normal, rotateCCW);
    }

    private static FaceBasis getFaceBasis(Direction normal) {
        return switch (normal) {
            case UP -> makeFaceBasis(Direction.EAST, Direction.SOUTH, Direction.UP, false);
            case DOWN -> makeFaceBasis(Direction.EAST, Direction.NORTH, Direction.DOWN, false);

            case NORTH -> makeFaceBasis(Direction.EAST, Direction.DOWN, Direction.NORTH, true);
            case SOUTH -> makeFaceBasis(Direction.WEST, Direction.DOWN, Direction.SOUTH, true);

            case EAST -> makeFaceBasis(Direction.SOUTH, Direction.DOWN, Direction.EAST, true);
            case WEST -> makeFaceBasis(Direction.NORTH, Direction.DOWN, Direction.WEST, true);
        };
    }

    /** Returns the direction step as a {@link Vec3}. */
    private static Vec3 dirVec(Direction dir) {
        return new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
    }

    /**
     * Maps a face-local point into world coordinates using the supplied basis.
     *
     * @param x local right coordinate
     * @param y local normal coordinate
     * @param z local up coordinate
     * @param basis basis used for mapping
     * @return mapped point as an XYZ float array
     */
    private static float[] facePoint(float x, float y, float z, FaceBasis basis) {
        if (basis.rotateCCW()) {
            float oldX = x;
            x = z;
            z = 1.0F - oldX;
        }

        Vec3 p = basis.origin()
                .add(dirVec(basis.right()).scale(x))
                .add(dirVec(basis.up()).scale(z))
                .add(dirVec(basis.normal()).scale(y));

        return new float[]{(float) p.x, (float) p.y, (float) p.z};
    }

    /**
     * Determines the plane normal for a cross connection.
     *
     * @param a first direction
     * @param b second direction
     * @param c third direction
     * @param d fourth direction
     * @return the normal of the plane containing the cross
     */
    private static Direction findCrossNormal(Direction a, Direction b, Direction c, Direction d) {
        boolean east = false, west = false;
        boolean up = false, down = false;
        boolean north = false, south = false;

        for (Direction dir : new Direction[]{a, b, c, d}) {
            switch (dir) {
                case EAST -> east = true;
                case WEST -> west = true;
                case UP -> up = true;
                case DOWN -> down = true;
                case NORTH -> north = true;
                case SOUTH -> south = true;
            }
        }

        if (east && west && north && south) return Direction.UP;     // XZ plane
        if (east && west && up && down)       return Direction.SOUTH; // XY plane
        if (north && south && up && down)     return Direction.EAST;  // YZ plane

        throw new IllegalArgumentException(
                "Cross requires two opposite pairs on one plane: " +
                        a + ", " + b + ", " + c + ", " + d
        );
    }

    /**
     * Builds a point for tri-corner geometry in canonical local space.
     *
     * @param localX local X coordinate
     * @param localY local Y coordinate
     * @param localZ local Z coordinate
     * @param xConn X-axis connection
     * @param yConn Y-axis connection
     * @param zConn Z-axis connection
     * @return mapped point as an XYZ float array
     */
    private static float[] triCornerPoint(
            float localX, float localY, float localZ,
            Direction xConn, Direction yConn, Direction zConn
    ) {
        float[] xyz = new float[3];

        applyMappedPositiveAxis(xyz, xConn.getOpposite(), localX);

        applyMappedPositiveAxis(xyz, yConn, localY);

        applyMappedPositiveAxis(xyz, zConn.getOpposite(), localZ);

        return xyz;
    }

    /** Applies a mapped positive-axis coordinate to the destination XYZ array. */
    private static void applyMappedPositiveAxis(float[] xyz, Direction positiveDir, float value) {
        float mapped = switch (positiveDir) {
            case EAST, UP, SOUTH -> value;
            case WEST, DOWN, NORTH -> 1.0F - value;
        };

        switch (positiveDir.getAxis()) {
            case X -> xyz[0] = mapped;
            case Y -> xyz[1] = mapped;
            case Z -> xyz[2] = mapped;
        }
    }

    /** Determines the dominant nominal face from a local directional hint. */
    private static Direction nominalFaceFromHint(
            float localHx, float localHy, float localHz,
            Direction xConn, Direction yConn, Direction zConn
    ) {
        float[] world = transformHint(localHx, localHy, localHz, xConn, yConn, zConn);
        return dominantDirection(world[0], world[1], world[2]);
    }

    /** Transforms a local directional hint into world-space coordinates. */
    private static float[] transformHint(
            float localHx, float localHy, float localHz,
            Direction xConn, Direction yConn, Direction zConn
    ) {
        float wx = 0.0F;
        float wy = 0.0F;
        float wz = 0.0F;

        float[] bx = unitVector(xConn.getOpposite());
        wx += bx[0] * localHx;
        wy += bx[1] * localHy * 0.0F;
        wz += bx[2] * localHx;

        float[] by = unitVector(yConn);
        wx += by[0] * localHy;
        wy += by[1] * localHy;
        wz += by[2] * localHy;

        float[] bz = unitVector(zConn.getOpposite());
        wx += bz[0] * localHz;
        wy += bz[1] * localHz;
        wz += bz[2] * localHz;

        return new float[]{wx, wy, wz};
    }

    /** Returns the unit step vector for a cardinal direction. */
    private static float[] unitVector(Direction dir) {
        return new float[]{dir.getStepX(), dir.getStepY(), dir.getStepZ()};
    }

    /** Returns the dominant cardinal direction for the supplied vector. */
    private static Direction dominantDirection(float x, float y, float z) {
        float ax = Math.abs(x);
        float ay = Math.abs(y);
        float az = Math.abs(z);

        if (ax >= ay && ax >= az) {
            return x >= 0.0F ? Direction.EAST : Direction.WEST;
        }
        if (ay >= ax && ay >= az) {
            return y >= 0.0F ? Direction.UP : Direction.DOWN;
        }
        return z >= 0.0F ? Direction.SOUTH : Direction.NORTH;
    }

    // ---------------------------------------------------------------------
    // Box and fallback helpers
    // ---------------------------------------------------------------------

    /** Emits the fallback extension box for a single direction. */
    private static void emitExtensionBoxFallback(QuadEmitter emitter, Direction direction) {
        Box box = getExtensionBoxFallback(direction);
        emitBoxWithoutFace(emitter, box, direction);
    }

    /** Returns the fallback extension box for a single direction. */
    private static Box getExtensionBoxFallback(Direction direction) {
        float minX = FRAME_INSET;
        float minY = FRAME_INSET;
        float minZ = FRAME_INSET;
        float maxX = INVERTED_FRAME_INSET;
        float maxY = INVERTED_FRAME_INSET;
        float maxZ = INVERTED_FRAME_INSET;

        switch (direction) {
            case NORTH -> minZ = 0.0F;
            case SOUTH -> maxZ = 1.0F;
            case WEST -> minX = 0.0F;
            case EAST -> maxX = 1.0F;
            case DOWN -> minY = 0.0F;
            case UP -> maxY = 1.0F;
        }

        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Lightweight integer vector helper used for direction cross products.
     */
    private record Vec3iDir(int x, int y, int z) {
        static Vec3iDir from(Direction dir) {
            return new Vec3iDir(dir.getStepX(), dir.getStepY(), dir.getStepZ());
        }

        Vec3iDir cross(Vec3iDir other) {
            return new Vec3iDir(
                    this.y * other.z - this.z * other.y,
                    this.z * other.x - this.x * other.z,
                    this.x * other.y - this.y * other.x
            );
        }

        Direction toDirection() {
            for (Direction dir : Direction.values()) {
                if (dir.getStepX() == x && dir.getStepY() == y && dir.getStepZ() == z) {
                    return dir;
                }
            }
            throw new IllegalStateException("No cardinal direction for vector: " + this);
        }
    }

    private static float innerCoord(Direction dir) {
        return switch (dir) {
            case WEST, DOWN, NORTH -> FRAME_INSET;
            case EAST, UP, SOUTH -> INVERTED_FRAME_INSET;
        };
    }

    private static float outerCoord(Direction dir) {
        return switch (dir) {
            case WEST, DOWN, NORTH -> 0.0F;
            case EAST, UP, SOUTH -> 1.0F;
        };
    }

    /** Sets the value for the supplied axis on an XYZ coordinate array. */
    private static void setAxis(float[] xyz, Direction axisDir, float value) {
        switch (axisDir.getAxis()) {
            case X -> xyz[0] = value;
            case Y -> xyz[1] = value;
            case Z -> xyz[2] = value;
        }
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

    /**
     * Builds a box expanded toward the supplied directions.
     *
     * @param directions directions to expand toward
     * @return expanded box definition
     */
    private static Box getExpandedBox(Direction... directions) {
        float minX = FRAME_INSET;
        float minY = FRAME_INSET;
        float minZ = FRAME_INSET;
        float maxX = INVERTED_FRAME_INSET;
        float maxY = INVERTED_FRAME_INSET;
        float maxZ = INVERTED_FRAME_INSET;

        for (Direction direction : directions) {
            switch (direction) {
                case NORTH -> minZ = 0.0F;
                case SOUTH -> maxZ = 1.0F;
                case WEST -> minX = 0.0F;
                case EAST -> maxX = 1.0F;
                case DOWN -> minY = 0.0F;
                case UP -> maxY = 1.0F;
            }
        }

        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /** Returns the cap box for a single connected direction. */
    private static Box getCapBox(Direction direction) {
        return getExpandedBox(direction);
    }

    /** Emits all faces of a box except one. */
    private static void emitBoxWithoutFace(QuadEmitter emitter, Box box, Direction skipFace) {
        for (Direction face : Direction.values()) {
            if (face != skipFace) {
                emitBoxFace(emitter, face, box);
            }
        }
    }

    /** Emits all faces of a box except those in the supplied pair. */
    private static void emitBoxWithoutFaces(QuadEmitter emitter, Box box, DirectionPair skipFaces) {
        for (Direction face : Direction.values()) {
            if (!skipFaces.contains(face)) {
                emitBoxFace(emitter, face, box);
            }
        }
    }

    /** Emits all faces of the supplied box. */
    private static void emitBox(QuadEmitter emitter, Box box) {
        for (Direction face : Direction.values()) {
            emitBoxFace(emitter, face, box);
        }
    }

    private static void emitBox(
            QuadEmitter emitter,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ
    ) {
        emitBox(emitter, new Box(minX, minY, minZ, maxX, maxY, maxZ));
    }

    // ---------------------------------------------------------------------
    // Corner geometry
    // ---------------------------------------------------------------------

    /**
     * Emits the diagonal outer-corner geometry for a perpendicular direction pair.
     *
     * @param emitter quad emitter
     * @param a first direction
     * @param b second direction
     */
    private static void emitOuterCornerDiagonal(QuadEmitter emitter, Direction a, Direction b) {
        if (a.getAxis() == b.getAxis()) {
            throw new IllegalArgumentException("Directions must be perpendicular: " + a + ", " + b);
        }

        Direction.Axis axisA = a.getAxis();
        Direction.Axis axisB = b.getAxis();
        Direction.Axis axisC = remainingAxis(axisA, axisB);

        Direction frontNominalFace = Vec3iDir.from(a).cross(Vec3iDir.from(b)).toDirection();
        Direction backNominalFace = frontNominalFace.getOpposite();
        Direction topFace = positiveDirection(axisC);
        Direction bottomFace = negativeDirection(axisC);

        float F = FRAME_INSET;
        float I = INVERTED_FRAME_INSET;

        float outerA = outerCoord(a);
        float outerB = outerCoord(b);

        float deepA = deepInset(a);
        float deepB = deepInset(b);
        float shallowA = shallowInset(a);
        float shallowB = shallowInset(b);

        float[] p0f = point(axisA, deepA,    axisB, outerB,   axisC, F);
        float[] p1f = point(axisA, outerA,   axisB, deepB,    axisC, F);
        float[] p1t = point(axisA, outerA,   axisB, deepB,    axisC, I);
        float[] p0t = point(axisA, deepA,    axisB, outerB,   axisC, I);

        float[] p2f = point(axisA, outerA,   axisB, shallowB, axisC, F);
        float[] p3f = point(axisA, shallowA, axisB, outerB,   axisC, F);
        float[] p3t = point(axisA, shallowA, axisB, outerB,   axisC, I);
        float[] p2t = point(axisA, outerA,   axisB, shallowB, axisC, I);

        float[] shapeCenter = averagePoints(
                p0f, p1f, p2f, p3f,
                p0t, p1t, p2t, p3t
        );

        // Front face
        emitCustomQuad(
                emitter,
                frontNominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0f[0], p0f[1], p0f[2],
                p1f[0], p1f[1], p1f[2],
                p1t[0], p1t[1], p1t[2],
                p0t[0], p0t[1], p0t[2]
        );

        // Back face
        emitCustomQuad(
                emitter,
                backNominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p2f[0], p2f[1], p2f[2],
                p3f[0], p3f[1], p3f[2],
                p3t[0], p3t[1], p3t[2],
                p2t[0], p2t[1], p2t[2]
        );

        // Top face
        emitCustomQuad(
                emitter,
                topFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0t[0], p0t[1], p0t[2],
                p1t[0], p1t[1], p1t[2],
                p2t[0], p2t[1], p2t[2],
                p3t[0], p3t[1], p3t[2]
        );

        // Bottom face
        emitCustomQuad(
                emitter,
                bottomFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p1f[0], p1f[1], p1f[2],
                p0f[0], p0f[1], p0f[2],
                p3f[0], p3f[1], p3f[2],
                p2f[0], p2f[1], p2f[2]
        );
    }

    /**
     * Emits the diagonal inner-corner geometry for a perpendicular direction pair.
     *
     * @param emitter quad emitter
     * @param a first direction
     * @param b second direction
     */
    private static void emitInnerCornerDiagonals(QuadEmitter emitter, Direction a, Direction b) {
        if (a.getAxis() == b.getAxis()) {
            throw new IllegalArgumentException("Directions must be perpendicular: " + a + ", " + b);
        }

        Direction.Axis axisA = a.getAxis();
        Direction.Axis axisB = b.getAxis();
        Direction.Axis axisC = remainingAxis(axisA, axisB);

        Direction frontNominalFace = Vec3iDir.from(a).cross(Vec3iDir.from(b)).toDirection();
        Direction topFace = positiveDirection(axisC);
        Direction bottomFace = negativeDirection(axisC);

        float F = FRAME_INSET;
        float I = INVERTED_FRAME_INSET;

        float outerA = outerCoord(a);
        float outerB = outerCoord(b);

        float deepA = deepInset(a);
        float deepB = deepInset(b);

        float[] p0f = point(axisA, deepA,  axisB, outerB, axisC, F);
        float[] p1f = point(axisA, outerA, axisB, deepB,  axisC, F);
        float[] p1t = point(axisA, outerA, axisB, deepB,  axisC, I);
        float[] p0t = point(axisA, deepA,  axisB, outerB, axisC, I);

        // Corner apex points for the triangular top/bottom faces
        float[] topCorner = point(axisA, outerA, axisB, outerB, axisC, I);
        float[] bottomCorner = point(axisA, outerA, axisB, outerB, axisC, F);

        float[] shapeCenter = averagePoints(
                p0f, p1f, p1t, p0t,
                topCorner, bottomCorner
        );

        // Front face
        emitCustomQuad(
                emitter,
                frontNominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0f[0], p0f[1], p0f[2],
                p1f[0], p1f[1], p1f[2],
                p1t[0], p1t[1], p1t[2],
                p0t[0], p0t[1], p0t[2]
        );

        // Top triangle
        emitCustomTri(
                emitter,
                topFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0t[0], p0t[1], p0t[2],
                p1t[0], p1t[1], p1t[2],
                topCorner[0], topCorner[1], topCorner[2]
        );

        // Bottom triangle
        emitCustomTri(
                emitter,
                bottomFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p1f[0], p1f[1], p1f[2],
                p0f[0], p0f[1], p0f[2],
                bottomCorner[0], bottomCorner[1], bottomCorner[2]
        );
    }

    /** Returns the axis not present in the supplied axis pair. */
    private static Direction.Axis remainingAxis(Direction.Axis a, Direction.Axis b) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != a && axis != b) {
                return axis;
            }
        }
        throw new IllegalStateException("No remaining axis for " + a + " and " + b);
    }

    /** Returns the positive direction for an axis. */
    private static Direction positiveDirection(Direction.Axis axis) {
        return Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
    }

    /** Returns the negative direction for an axis. */
    private static Direction negativeDirection(Direction.Axis axis) {
        return Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
    }

    private static float deepInset(Direction dir) {
        // WEST/NORTH/DOWN -> I
        // EAST/SOUTH/UP   -> F
        return outerCoord(dir) - dirStep(dir) * INVERTED_FRAME_INSET;
    }

    private static float shallowInset(Direction dir) {
        // WEST/NORTH/DOWN -> F
        // EAST/SOUTH/UP   -> I
        return outerCoord(dir) - dirStep(dir) * FRAME_INSET;
    }

    /** Returns the signed step for a direction as {@code -1} or {@code 1}. */
    private static int dirStep(Direction dir) {
        return switch (dir) {
            case WEST, DOWN, NORTH -> -1;
            case EAST, UP, SOUTH -> 1;
        };
    }

    /** Builds an XYZ float array from three axis/value pairs. */
    private static float[] point(
            Direction.Axis axis1, float value1,
            Direction.Axis axis2, float value2,
            Direction.Axis axis3, float value3
    ) {
        float[] xyz = new float[3];
        setAxis(xyz, axis1, value1);
        setAxis(xyz, axis2, value2);
        setAxis(xyz, axis3, value3);
        return xyz;
    }

    private static void setAxis(float[] xyz, Direction.Axis axis, float value) {
        switch (axis) {
            case X -> xyz[0] = value;
            case Y -> xyz[1] = value;
            case Z -> xyz[2] = value;
        }
    }

    // ---------------------------------------------------------------------
    // Low-level quad and triangle emission
    // ---------------------------------------------------------------------

    /** Emits a custom triangle, inferring the nominal face from its vertices. */
    private static void emitCustomTri(
            QuadEmitter emitter,
            float[] shapeCenter,
            float[] v0,
            float[] v1,
            float[] v2
    ) {
        Vec3 vec30 = new Vec3(v0[0], v0[1], v0[2]);
        Vec3 vec31 = new Vec3(v1[0], v1[1], v1[2]);
        Vec3 vec32 = new Vec3(v2[0], v2[1], v2[2]);
        Direction nominalFace = nominalFaceFromTri(vec30, vec31, vec32);
        emitCustomTri(
                emitter,
                nominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                v0[0], v0[1], v0[2],
                v1[0], v1[1], v1[2],
                v2[0], v2[1], v2[2]
        );
    }

    /** Computes the nearest cardinal face for a triangle from its vertex winding. */
    private static Direction nominalFaceFromTri(Vec3 v0, Vec3 v1, Vec3 v2) {
        Vec3 normal = v1.subtract(v0).cross(v2.subtract(v0)).normalize();
        return Direction.getNearest((float) normal.x, (float) normal.y, (float) normal.z);
    }

    private static void emitCustomTri(
            QuadEmitter emitter,
            Direction nominalFace,
            float shapeCx, float shapeCy, float shapeCz,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2
    ) {
        float ax = x1 - x0;
        float ay = y1 - y0;
        float az = z1 - z0;

        float bx = x2 - x0;
        float by = y2 - y0;
        float bz = z2 - z0;

        float nx = ay * bz - az * by;
        float ny = az * bx - ax * bz;
        float nz = ax * by - ay * bx;

        float faceCx = (x0 + x1 + x2) / 3.0f;
        float faceCy = (y0 + y1 + y2) / 3.0f;
        float faceCz = (z0 + z1 + z2) / 3.0f;

        float ox = faceCx - shapeCx;
        float oy = faceCy - shapeCy;
        float oz = faceCz - shapeCz;

        float dot = nx * ox + ny * oy + nz * oz;

        emitter.nominalFace(nominalFace);

        if (dot < 0.0F) {
            emitter.pos(0, x0, y0, z0);
            emitter.pos(1, x2, y2, z2);
            emitter.pos(2, x1, y1, z1);
            emitter.pos(3, x1, y1, z1);

            emitter.uv(0, 0.0F, 0.0F);
            emitter.uv(1, 1.0F, 1.0F);
            emitter.uv(2, 1.0F, 0.0F);
            emitter.uv(3, 1.0F, 0.0F);
        } else {
            emitter.pos(0, x0, y0, z0);
            emitter.pos(1, x1, y1, z1);
            emitter.pos(2, x2, y2, z2);
            emitter.pos(3, x2, y2, z2);

            emitter.uv(0, 0.0F, 0.0F);
            emitter.uv(1, 1.0F, 0.0F);
            emitter.uv(2, 1.0F, 1.0F);
            emitter.uv(3, 1.0F, 1.0F);
        }

        emitter.emit();
    }

    /** Returns the arithmetic center of a collection of XYZ float points. */
    private static float[] averagePoints(float[]... points) {
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;

        for (float[] p : points) {
            x += p[0];
            y += p[1];
            z += p[2];
        }

        float inv = 1.0F / points.length;
        return new float[] { x * inv, y * inv, z * inv };
    }

    /** Emits a custom quad. */
    private static void emitCustomQuad(
            QuadEmitter emitter,
            float[] shapeCenter,
            float[] v0,
            float[] v1,
            float[] v2,
            float[] v3
    ) {
        Vec3 vec30 = new Vec3(v0[0], v0[1], v0[2]);
        Vec3 vec31 = new Vec3(v1[0], v1[1], v1[2]);
        Vec3 vec32 = new Vec3(v2[0], v2[1], v2[2]);
        Vec3 vec33 = new Vec3(v3[0], v3[1], v3[2]);
        Direction nominalFace = nominalFaceFromQuad(vec30, vec31, vec32, vec33);
        emitCustomQuad(
                emitter,
                nominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                v0[0], v0[1], v0[2],
                v1[0], v1[1], v1[2],
                v2[0], v2[1], v2[2],
                v3[0], v3[1], v3[2]
        );
    }

    private static void emitCustomQuad(
            QuadEmitter emitter,
            Direction nominalFace,
            float shapeCx, float shapeCy, float shapeCz,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3
    ) {
        float ax = x1 - x0;
        float ay = y1 - y0;
        float az = z1 - z0;

        float bx = x2 - x0;
        float by = y2 - y0;
        float bz = z2 - z0;

        float nx = ay * bz - az * by;
        float ny = az * bx - ax * bz;
        float nz = ax * by - ay * bx;

        float faceCx = (x0 + x1 + x2 + x3) * 0.25f;
        float faceCy = (y0 + y1 + y2 + y3) * 0.25f;
        float faceCz = (z0 + z1 + z2 + z3) * 0.25f;

        float ox = faceCx - shapeCx;
        float oy = faceCy - shapeCy;
        float oz = faceCz - shapeCz;

        float dot = nx * ox + ny * oy + nz * oz;

        emitter.nominalFace(nominalFace);

        if (dot < 0.0F) {
            emitter.pos(0, x0, y0, z0);
            emitter.pos(1, x3, y3, z3);
            emitter.pos(2, x2, y2, z2);
            emitter.pos(3, x1, y1, z1);
        } else {
            emitter.pos(0, x0, y0, z0);
            emitter.pos(1, x1, y1, z1);
            emitter.pos(2, x2, y2, z2);
            emitter.pos(3, x3, y3, z3);
        }

        emitter.uv(0, 0.0F, 0.0F);
        emitter.uv(1, 0.0F, 1.0F);
        emitter.uv(2, 1.0F, 1.0F);
        emitter.uv(3, 1.0F, 0.0F);

        emitter.emit();
    }

    /** Emits a single face of the supplied box. */
    private static void emitBoxFace(QuadEmitter emitter, Direction face, Box box) {
        float minX = box.minX();
        float minY = box.minY();
        float minZ = box.minZ();
        float maxX = box.maxX();
        float maxY = box.maxY();
        float maxZ = box.maxZ();

        switch (face) {
            case UP -> emitter.square(
                    Direction.UP,
                    minX, 1.0F - maxZ,
                    maxX, 1.0F - minZ,
                    1.0F - maxY
            ).emit();

            case DOWN -> emitter.square(
                    Direction.DOWN,
                    minX, minZ,
                    maxX, maxZ,
                    minY
            ).emit();

            case NORTH -> emitter.square(
                    Direction.NORTH,
                    1.0F - maxX, minY,
                    1.0F - minX, maxY,
                    minZ
            ).emit();

            case SOUTH -> emitter.square(
                    Direction.SOUTH,
                    minX, minY,
                    maxX, maxY,
                    1.0F - maxZ
            ).emit();

            case EAST -> emitter.square(
                    Direction.EAST,
                    1.0F - maxZ, minY,
                    1.0F - minZ, maxY,
                    1.0F - maxX
            ).emit();

            case WEST -> emitter.square(
                    Direction.WEST,
                    minZ, minY,
                    maxZ, maxY,
                    minX
            ).emit();
        }
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

    private record Box(
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ
    ) {}

    // ---------------------------------------------------------------------
    // Item rendering and debug helpers
    // ---------------------------------------------------------------------

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

    // DEBUG METHODS
    /** ----------------------------------------------------------------**/

    /** Emits a small debug marker box at the supplied vertex position. */
    private static void emitDebugVertexMarker(QuadEmitter emitter, float[] vertex, float v, int i) {
        emitDebugVertexMarker(emitter, vertex[0], vertex[1], vertex[2], v, i);
    }

    private static void emitDebugVertexMarker(
            QuadEmitter emitter,
            float x, float y, float z,
            float size,
            int argb
    ) {
        float h = size * 0.5F;

        // Small cube centered at the vertex
        emitColoredBox(
                emitter,
                x - h, y - h, z - h,
                x + h, y + h, z + h,
                argb
        );
    }

    /** Emits a colored box for debugging purposes. */
    private static void emitColoredBox(
            QuadEmitter emitter,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ,
            int argb
    ) {
        emitColoredQuad(emitter, Direction.UP, argb,
                minX, maxY, minZ,
                minX, maxY, maxZ,
                maxX, maxY, maxZ,
                maxX, maxY, minZ
        );

        emitColoredQuad(emitter, Direction.DOWN, argb,
                minX, minY, maxZ,
                minX, minY, minZ,
                maxX, minY, minZ,
                maxX, minY, maxZ
        );

        emitColoredQuad(emitter, Direction.NORTH, argb,
                maxX, minY, minZ,
                minX, minY, minZ,
                minX, maxY, minZ,
                maxX, maxY, minZ
        );

        emitColoredQuad(emitter, Direction.SOUTH, argb,
                minX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, maxY, maxZ,
                minX, maxY, maxZ
        );

        emitColoredQuad(emitter, Direction.WEST, argb,
                minX, minY, minZ,
                minX, minY, maxZ,
                minX, maxY, maxZ,
                minX, maxY, minZ
        );

        emitColoredQuad(emitter, Direction.EAST, argb,
                maxX, minY, maxZ,
                maxX, minY, minZ,
                maxX, maxY, minZ,
                maxX, maxY, maxZ
        );
    }

    /** Emits a colored quad for debugging purposes. */
    private static void emitColoredQuad(
            QuadEmitter emitter,
            Direction nominalFace,
            int argb,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3
    ) {
        emitter.nominalFace(nominalFace);

        emitter.pos(0, x0, y0, z0);
        emitter.pos(1, x1, y1, z1);
        emitter.pos(2, x2, y2, z2);
        emitter.pos(3, x3, y3, z3);

        emitter.color(0, argb);
        emitter.color(1, argb);
        emitter.color(2, argb);
        emitter.color(3, argb);

        emitter.uv(0, 0.0F, 0.0F);
        emitter.uv(1, 0.0F, 1.0F);
        emitter.uv(2, 1.0F, 1.0F);
        emitter.uv(3, 1.0F, 0.0F);

        emitter.emit();
    }
}