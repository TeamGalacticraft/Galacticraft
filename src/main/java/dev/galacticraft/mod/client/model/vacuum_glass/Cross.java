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

public class Cross {
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
    public static void emitCrossConnection(
            QuadEmitter emitter,
            BlockAndTintGetter blockView,
            BlockPos pos,
            Direction a, Direction b, Direction c, Direction d
    ) {
        Direction normal = findCrossNormal(a, b, c, d);
        FaceBasis basis = getFaceBasis(normal);

        float F = VacuumGlassBakedModel.FRAME_INSET;
        float I = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

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

        float[] shapeCenter = HelperMethods.averagePoints(
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
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex12,
                vertex13,
                vertex14,
                vertex15
        );
        // emit bottom center quad
        EmitHelper.emitCustomQuad(
                emitter,
                shapeCenter,
                vertex31,
                vertex30,
                vertex29,
                vertex28
        );

        if (stateAC.isSolidRender(blockView, relativeAC) || stateAC.is(GCBlocks.VACUUM_GLASS)) {
            // emit +X side with corner top
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex10,
                    vertex14,
                    vertex7,
                    vertex9
            );
            // emit +X side with corner bottom
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex25,
                    vertex23,
                    vertex30,
                    vertex26
            );
        } else {
            // emit +X,-Z side
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex26,
                    vertex10,
                    vertex8,
                    vertex24
            );

            // emit +X side with corner missing top
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex10,
                    vertex14,
                    vertex7,
                    vertex8
            );
            // emit +X side with corner missing bottom
            EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex7,
                    vertex13,
                    vertex4,
                    vertex6
            );
            // emit +Z side with corner bottom
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex22,
                    vertex20,
                    vertex29,
                    vertex23
            );
        } else {
            // emit +X,+Z side
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex23,
                    vertex7,
                    vertex5,
                    vertex21
            );

            // emit +Z side with corner missing top
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex7,
                    vertex13,
                    vertex4,
                    vertex5
            );
            // emit +Z side with corner missing bottom
            EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex12,
                    vertex1,
                    vertex3
            );
            // emit -X side with corner bottom
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex19,
                    vertex17,
                    vertex28,
                    vertex20
            );
        } else {
            // emit -x,+z side
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex20,
                    vertex4,
                    vertex2,
                    vertex18
            );

            // emit -X side with corner missing top
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex4,
                    vertex12,
                    vertex1,
                    vertex2
            );
            // emit -X side with corner missing bottom
            EmitHelper.emitCustomQuad(
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
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex15,
                    vertex10,
                    vertex0
            );
            // emit -Z side with corner bottom
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex16,
                    vertex26,
                    vertex31,
                    vertex17
            );
        } else {
            // emit -x,-z side
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex17,
                    vertex1,
                    vertex11,
                    vertex27
            );

            // emit -Z side with corner missing top
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex1,
                    vertex15,
                    vertex10,
                    vertex11
            );
            // emit -Z side with corner missing bottom
            EmitHelper.emitCustomQuad(
                    emitter,
                    shapeCenter,
                    vertex27,
                    vertex26,
                    vertex31,
                    vertex17
            );
        }
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

    private static FaceBasis makeFaceBasis(Direction right, Direction up, Direction normal, boolean rotateCCW) {
        Vec3 origin = new Vec3(
                Math.max(0, -right.getStepX()) + Math.max(0, -up.getStepX()) + Math.max(0, -normal.getStepX()),
                Math.max(0, -right.getStepY()) + Math.max(0, -up.getStepY()) + Math.max(0, -normal.getStepY()),
                Math.max(0, -right.getStepZ()) + Math.max(0, -up.getStepZ()) + Math.max(0, -normal.getStepZ())
        );

        return new FaceBasis(origin, right, up, normal, rotateCCW);
    }

    /**
     * Local basis used to transform face-relative coordinates into world space.
     */
    private record FaceBasis(Vec3 origin, Direction right, Direction up, Direction normal, boolean rotateCCW) {}

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
                .add(HelperMethods.dirVec(basis.right()).scale(x))
                .add(HelperMethods.dirVec(basis.up()).scale(z))
                .add(HelperMethods.dirVec(basis.normal()).scale(y));

        return new float[]{(float) p.x, (float) p.y, (float) p.z};
    }
}
