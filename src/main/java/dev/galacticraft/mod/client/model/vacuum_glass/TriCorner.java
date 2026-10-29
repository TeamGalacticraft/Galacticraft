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

public class TriCorner {
    /**
     * Emits the tri-corner variant.
     *
     * @param emitter quad emitter
     * @param blockView world access used for neighbor checks
     * @param pos block position being rendered
     * @param a first direction
     * @param b second direction
     * @param c third direction
     */
    public static void emitTriCornerConnection(
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

        float F = VacuumGlassBakedModel.FRAME_INSET;
        float I = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

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

        float[] shapeCenter = HelperMethods.averagePoints(
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
        EmitHelper.emitCustomTri(
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
                        EmitHelper.emitCustomTri(
                                emitter,
                                nominalFaceFromHint(1.0F, -1.0F, 1.0F, xConn, yConn, zConn),
                                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                                vertex12[0], vertex12[1], vertex12[2],
                                vertex13[0], vertex13[1], vertex13[2],
                                vertex14[0], vertex14[1], vertex14[2]
                        );
                    }
                } else {
                    EmitHelper.emitCustomQuad(
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
                EmitHelper.emitCustomQuad(
                        emitter,
                        nominalFaceFromHint(1.0F, 0.0F, 1.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex9[0], vertex9[1], vertex9[2],
                        vertex11[0], vertex11[1], vertex11[2],
                        vertex13[0], vertex13[1], vertex13[2],
                        vertex12[0], vertex12[1], vertex12[2]
                );
            } else if ((stateAB.isSolidRender(blockView, relativeAB) || stateAB.is(GCBlocks.VACUUM_GLASS)) && (stateBC.isSolidRender(blockView, relativeBC) || stateBC.is(GCBlocks.VACUUM_GLASS))) {
                EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomTri(
                    emitter,
                    nominalFaceFromHint(1.0F, -1.0F, 1.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex10[0], vertex10[1], vertex10[2],
                    vertex9[0], vertex9[1], vertex9[2],
                    vertex11[0], vertex11[1], vertex11[2]
            );
        }

        // bottom quad canonical hint = (0, +1, 0)
        EmitHelper.emitCustomQuad(
                emitter,
                nominalFaceFromHint(0.0F, 1.0F, 0.0F, xConn, yConn, zConn),
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                vertex0[0], vertex0[1], vertex0[2],
                vertex2[0], vertex2[1], vertex2[2],
                vertex3[0], vertex3[1], vertex3[2],
                vertex1[0], vertex1[1], vertex1[2]
        );

        // +X quad canonical hint = (-1, 0, 0)
        EmitHelper.emitCustomQuad(
                emitter,
                nominalFaceFromHint(-1.0F, 0.0F, 0.0F, xConn, yConn, zConn),
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                vertex3[0], vertex3[1], vertex3[2],
                vertex6[0], vertex6[1], vertex6[2],
                vertex5[0], vertex5[1], vertex5[2],
                vertex4[0], vertex4[1], vertex4[2]
        );

        // +Z quad canonical hint = (0, 0, -1)
        EmitHelper.emitCustomQuad(
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
                EmitHelper.emitCustomTri(
                        emitter,
                        nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex10[0], vertex10[1], vertex10[2],
                        vertex9[0], vertex9[1], vertex9[2],
                        vertex12[0], vertex12[1], vertex12[2]
                );
            }

            // outer
            EmitHelper.emitCustomTri(
                    emitter,
                    nominalFaceFromHint(0.0F, 1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex0[0], vertex0[1], vertex0[2],
                    vertex15[0], vertex15[1], vertex15[2],
                    vertex1[0], vertex1[1], vertex1[2]
            );
        } else {
            EmitHelper.emitCustomQuad(
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
                EmitHelper.emitCustomTri(
                        emitter,
                        nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex9[0], vertex9[1], vertex9[2],
                        vertex14[0], vertex14[1], vertex14[2],
                        vertex11[0], vertex11[1], vertex11[2]
                );
            }
            // outer
            EmitHelper.emitCustomTri(
                    emitter,
                    nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex4[0], vertex4[1], vertex4[2],
                    vertex5[0], vertex5[1], vertex5[2],
                    vertex16[0], vertex16[1], vertex16[2]
            );
        } else {
            EmitHelper.emitCustomQuad(
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
                EmitHelper.emitCustomTri(
                        emitter,
                        nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                        shapeCenter[0], shapeCenter[1], shapeCenter[2],
                        vertex10[0], vertex10[1], vertex10[2],
                        vertex11[0], vertex11[1], vertex11[2],
                        vertex13[0], vertex13[1], vertex13[2]
                );
            }

            // outer
            EmitHelper.emitCustomTri(
                    emitter,
                    nominalFaceFromHint(0.0F, -1.0F, 0.0F, xConn, yConn, zConn),
                    shapeCenter[0], shapeCenter[1], shapeCenter[2],
                    vertex8[0], vertex8[1], vertex8[2],
                    vertex17[0], vertex17[1], vertex17[2],
                    vertex7[0], vertex7[1], vertex7[2]
            );
        } else {
            EmitHelper.emitCustomQuad(
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
}
