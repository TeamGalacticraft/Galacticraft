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
import net.minecraft.world.phys.Vec3;

public class Saddle {
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
    public static void emitSaddleConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction a, Direction b, Direction c, Direction d
    ) {
        Direction[] dirs = new Direction[]{a, b, c, d};

        VacuumGlassBakedModel.DirectionPair oppositePair = HelperMethods.findOppositePair(dirs);
        if (oppositePair == null) {
            throw new IllegalArgumentException(
                    "Saddle requires exactly one opposite pair: " +
                            a + ", " + b + ", " + c + ", " + d
            );
        }

        Direction[] remaining = HelperMethods.findRemainingDirections(dirs, oppositePair.a(), oppositePair.b());
        if (remaining.length != 2) {
            throw new IllegalStateException(
                    "Saddle expected two remaining directions but got " + remaining.length
            );
        }

        SaddleBasis basis = getSaddleBasis(oppositePair, remaining[0], remaining[1]);

        float F = VacuumGlassBakedModel.FRAME_INSET;
        float I = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

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

        float[] shapeCenter = HelperMethods.averagePoints(
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
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex2,
                vertex3,
                vertex8,
                vertex7
        );

        // -X,+Y slant
        if (stateXNegYPos.isSolidRender(blockView, relativeXNegYPos) || stateXNegYPos.is(GCBlocks.VACUUM_GLASS)) {
            EmitHelper.emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex7,
                    vertex21
            );
        } else {
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex0,
                    vertex1,
                    vertex7,
                    vertex6
            );
        }


        if (stateXPosYPos.isSolidRender(blockView, relativeXPosYPos) || stateXPosYPos.is(GCBlocks.VACUUM_GLASS)) {
            EmitHelper.emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex24,
                    vertex8
            );
        } else {
            // +X,+Y slant
            EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex17,
                    vertex2,
                    vertex7,
                    vertex21
            );
        } else {
            // -X,+Z,+Y triangle
            EmitHelper.emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex2,
                    vertex7
            );
        }

        if ((stateXPosYPos.isSolidRender(blockView, relativeXPosYPos) || stateXPosYPos.is(GCBlocks.VACUUM_GLASS)) &&
                (stateXPosZPos.isSolidRender(blockView, relativeXPosZPos) || stateXPosZPos.is(GCBlocks.VACUUM_GLASS))) {
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex3,
                    vertex16,
                    vertex24,
                    vertex8
            );
        } else {
            // +X,+Z,+Y triangle
            EmitHelper.emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex3,
                    vertex4,
                    vertex8
            );
        }

        if (stateXNegZPos.isSolidRender(blockView, relativeXNegZPos) || stateXNegZPos.is(GCBlocks.VACUUM_GLASS)) {
            EmitHelper.emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex17,
                    vertex2,
                    vertex1
            );
        } else {
            // -X,+Z edge
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex11,
                    vertex12,
                    vertex2,
                    vertex1
            );
        }

        if (stateXPosZPos.isSolidRender(blockView, relativeXPosZPos) || stateXPosZPos.is(GCBlocks.VACUUM_GLASS)) {
            EmitHelper.emitCustomTri(
                    emitter,
                    shapeCenter,
                    vertex3,
                    vertex16,
                    vertex4
            );
        } else {
            // +X,+Z edge
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex13,
                    vertex14,
                    vertex4,
                    vertex3
            );
        }

        // -Z face
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex5,
                vertex0,
                vertex6,
                vertex9
        );

        // -Z second face
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex15,
                vertex10,
                vertex0,
                vertex5
        );

        // -Y face
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex14,
                vertex13,
                vertex12,
                vertex11
        );

        // -Y second face
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex14,
                vertex11,
                vertex10,
                vertex15
        );
    }

    /**
     * Builds a canonical local basis for a saddle connection.
     *
     * The opposite pair is mapped to local X.
     * The two remaining directions are mapped to local Y and Z.
     */
    private static SaddleBasis getSaddleBasis(VacuumGlassBakedModel.DirectionPair oppositePair, Direction remainingA, Direction remainingB) {
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
     * Local basis used to transform canonical saddle coordinates into world space.
     *
     * xPos / xNeg = the opposite pair axis
     * yPos        = first remaining saddle direction
     * zPos        = second remaining saddle direction
     */
    private record SaddleBasis(Vec3 origin, Direction xPos, Direction xNeg, Direction yPos, Direction zPos) {}

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
                .add(HelperMethods.dirVec(basis.xPos()).scale(x))
                .add(HelperMethods.dirVec(basis.yPos()).scale(y))
                .add(HelperMethods.dirVec(basis.zPos()).scale(z));

        return new float[]{(float) p.x, (float) p.y, (float) p.z};
    }
}
