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

package dev.galacticraft.mod.client.model.vacuum_glass;

import dev.galacticraft.mod.client.model.VacuumGlassBakedModel;
import dev.galacticraft.mod.content.GCBlocks;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class TConnection {
    /**
     * Emits the T-junction geometry for a straight pair and branch.
     *
     * @param emitter quad emitter
     * @param blockView world access used for diagonal checks
     * @param pos block position being rendered
     * @param straightPair opposite directions forming the stem of the T
     * @param branch perpendicular branch direction
     */
    public static void emitTConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            VacuumGlassBakedModel.DirectionPair straightPair,
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
        Direction.Axis thicknessAxis = HelperMethods.remainingAxis(mainAxis, branchAxis);

        Direction negMain = HelperMethods.negativeDirection(mainAxis);
        Direction posMain = HelperMethods.positiveDirection(mainAxis);

        boolean leftFilled = isTDiagonalFilled(blockView, pos, negMain, branch);
        boolean rightFilled = isTDiagonalFilled(blockView, pos, posMain, branch);

        float F = VacuumGlassBakedModel.FRAME_INSET;
        float I = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

        Direction topFace = HelperMethods.positiveDirection(thicknessAxis);
        Direction bottomFace = HelperMethods.negativeDirection(thicknessAxis);
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
            shapeCenter = HelperMethods.averagePoints(A, B, C, D, LBb, LBt, RBb, RBt);
        } else if (leftFilled) {
            shapeCenter = HelperMethods.averagePoints(A, B, C, D, E, H, G, J, Rb, Rt, LBb, LBt);
        } else if (rightFilled) {
            shapeCenter = HelperMethods.averagePoints(A, B, C, D, E, H, G, J, Lb, Lt, RBb, RBt);
        } else {
            shapeCenter = HelperMethods.averagePoints(A, B, C, D, E, H, G, J, Lb, Lt, Rb, Rt);
        }

        // -------------------------
        // Variant 3: both diagonals solid
        // -------------------------
        if (leftFilled && rightFilled) {
            // Top full
            EmitHelper.emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    D[0], D[1], D[2],
                    C[0], C[1], C[2],
                    LBt[0], LBt[1], LBt[2],
                    RBt[0], RBt[1], RBt[2]
            );

            // Bottom full
            EmitHelper.emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    A[0], A[1], A[2],
                    B[0], B[1], B[2],
                    RBb[0], RBb[1], RBb[2],
                    LBb[0], LBb[1], LBb[2]
            );

            // Back side
            EmitHelper.emitCustomQuad(
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
        EmitHelper.emitCustomQuad(
                emitter, topFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                D[0], D[1], D[2],
                C[0], C[1], C[2],
                G[0], G[1], G[2],
                J[0], J[1], J[2]
        );

        // Bottom part 1
        EmitHelper.emitCustomQuad(
                emitter, bottomFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                A[0], A[1], A[2],
                B[0], B[1], B[2],
                H[0], H[1], H[2],
                E[0], E[1], E[2]
        );

        // Back side
        EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    J[0], J[1], J[2],
                    G[0], G[1], G[2],
                    Lt[0], Lt[1], Lt[2],
                    Rt[0], Rt[1], Rt[2]
            );

            // Bottom part 2
            EmitHelper.emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    H[0], H[1], H[2],
                    Rb[0], Rb[1], Rb[2],
                    Lb[0], Lb[1], Lb[2]
            );

            // Left diagonal side
            EmitHelper.emitCustomQuad(
                    emitter, leftFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    Lb[0], Lb[1], Lb[2],
                    Lt[0], Lt[1], Lt[2],
                    G[0], G[1], G[2]
            );

            // Right diagonal side
            EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    J[0], J[1], J[2],
                    G[0], G[1], G[2],
                    Lt[0], Lt[1], Lt[2],
                    RBt[0], RBt[1], RBt[2]
            );

            // Bottom part 2
            EmitHelper.emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    H[0], H[1], H[2],
                    RBb[0], RBb[1], RBb[2],
                    Lb[0], Lb[1], Lb[2]
            );

            // Left diagonal side only
            EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter, topFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    J[0], J[1], J[2],
                    G[0], G[1], G[2],
                    LBt[0], LBt[1], LBt[2],
                    Rt[0], Rt[1], Rt[2]
            );

            // Bottom part 2
            EmitHelper.emitCustomQuad(
                    emitter, bottomFace,
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    E[0], E[1], E[2],
                    H[0], H[1], H[2],
                    Rb[0], Rb[1], Rb[2],
                    LBb[0], LBb[1], LBb[2]
            );

            // Right diagonal side only
            EmitHelper.emitCustomQuad(
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

        HelperMethods.setAxis(xyz, mainAxis, x);
        HelperMethods.setAxis(xyz, thicknessAxis, y);

        float branchCoord = branch.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? z : (1.0F - z);
        HelperMethods.setAxis(xyz, branch.getAxis(), branchCoord);

        return xyz;
    }
}
