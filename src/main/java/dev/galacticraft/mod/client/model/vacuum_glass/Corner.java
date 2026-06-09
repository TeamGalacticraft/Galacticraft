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
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.core.Direction;

public class Corner {
    /**
     * Emits either an inner or outer corner for a perpendicular connection pair.
     *
     * @param emitter quad emitter
     * @param pair connected direction pair
     * @param outerCorner whether the outer-corner variant should be emitted
     */
    public static void emitCorner(QuadEmitter emitter, VacuumGlassBakedModel.DirectionPair pair, boolean outerCorner) {
        Direction a = pair.a();
        Direction b = pair.b();

        if (outerCorner) {
            emitOuterCornerDiagonal(emitter, a, b);
        } else {
            emitInnerCornerDiagonals(emitter, a, b);
        }
    }

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
        Direction.Axis axisC = HelperMethods.remainingAxis(axisA, axisB);

        Direction frontNominalFace = HelperMethods.Vec3iDir.from(a).cross(HelperMethods.Vec3iDir.from(b)).toDirection();
        Direction backNominalFace = frontNominalFace.getOpposite();
        Direction topFace = HelperMethods.positiveDirection(axisC);
        Direction bottomFace = HelperMethods.negativeDirection(axisC);

        float F = VacuumGlassBakedModel.FRAME_INSET;
        float I = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

        float outerA = HelperMethods.outerCoord(a);
        float outerB = HelperMethods.outerCoord(b);

        float deepA = HelperMethods.deepInset(a);
        float deepB = HelperMethods.deepInset(b);
        float shallowA = HelperMethods.shallowInset(a);
        float shallowB = HelperMethods.shallowInset(b);

        float[] p0f = point(axisA, deepA,    axisB, outerB,   axisC, F);
        float[] p1f = point(axisA, outerA,   axisB, deepB,    axisC, F);
        float[] p1t = point(axisA, outerA,   axisB, deepB,    axisC, I);
        float[] p0t = point(axisA, deepA,    axisB, outerB,   axisC, I);

        float[] p2f = point(axisA, outerA,   axisB, shallowB, axisC, F);
        float[] p3f = point(axisA, shallowA, axisB, outerB,   axisC, F);
        float[] p3t = point(axisA, shallowA, axisB, outerB,   axisC, I);
        float[] p2t = point(axisA, outerA,   axisB, shallowB, axisC, I);

        float[] shapeCenter = HelperMethods.averagePoints(
                p0f, p1f, p2f, p3f,
                p0t, p1t, p2t, p3t
        );

        // Front face
        EmitHelper.emitCustomQuad(
                emitter,
                frontNominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0f[0], p0f[1], p0f[2],
                p1f[0], p1f[1], p1f[2],
                p1t[0], p1t[1], p1t[2],
                p0t[0], p0t[1], p0t[2]
        );

        // Back face
        EmitHelper.emitCustomQuad(
                emitter,
                backNominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p2f[0], p2f[1], p2f[2],
                p3f[0], p3f[1], p3f[2],
                p3t[0], p3t[1], p3t[2],
                p2t[0], p2t[1], p2t[2]
        );

        // Top face
        EmitHelper.emitCustomQuad(
                emitter,
                topFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0t[0], p0t[1], p0t[2],
                p1t[0], p1t[1], p1t[2],
                p2t[0], p2t[1], p2t[2],
                p3t[0], p3t[1], p3t[2]
        );

        // Bottom face
        EmitHelper.emitCustomQuad(
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
        Direction.Axis axisC = HelperMethods.remainingAxis(axisA, axisB);

        Direction frontNominalFace = HelperMethods.Vec3iDir.from(a).cross(HelperMethods.Vec3iDir.from(b)).toDirection();
        Direction topFace = HelperMethods.positiveDirection(axisC);
        Direction bottomFace = HelperMethods.negativeDirection(axisC);

        float F = VacuumGlassBakedModel.FRAME_INSET;
        float I = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

        float outerA = HelperMethods.outerCoord(a);
        float outerB = HelperMethods.outerCoord(b);

        float deepA = HelperMethods.deepInset(a);
        float deepB = HelperMethods.deepInset(b);

        float[] p0f = point(axisA, deepA,  axisB, outerB, axisC, F);
        float[] p1f = point(axisA, outerA, axisB, deepB,  axisC, F);
        float[] p1t = point(axisA, outerA, axisB, deepB,  axisC, I);
        float[] p0t = point(axisA, deepA,  axisB, outerB, axisC, I);

        // Corner apex points for the triangular top/bottom faces
        float[] topCorner = point(axisA, outerA, axisB, outerB, axisC, I);
        float[] bottomCorner = point(axisA, outerA, axisB, outerB, axisC, F);

        float[] shapeCenter = HelperMethods.averagePoints(
                p0f, p1f, p1t, p0t,
                topCorner, bottomCorner
        );

        // Front face
        EmitHelper.emitCustomQuad(
                emitter,
                frontNominalFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0f[0], p0f[1], p0f[2],
                p1f[0], p1f[1], p1f[2],
                p1t[0], p1t[1], p1t[2],
                p0t[0], p0t[1], p0t[2]
        );

        // Top triangle
        EmitHelper.emitCustomTri(
                emitter,
                topFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p0t[0], p0t[1], p0t[2],
                p1t[0], p1t[1], p1t[2],
                topCorner[0], topCorner[1], topCorner[2]
        );

        // Bottom triangle
        EmitHelper.emitCustomTri(
                emitter,
                bottomFace,
                shapeCenter[0], shapeCenter[1], shapeCenter[2],
                p1f[0], p1f[1], p1f[2],
                p0f[0], p0f[1], p0f[2],
                bottomCorner[0], bottomCorner[1], bottomCorner[2]
        );
    }

    /** Builds an XYZ float array from three axis/value pairs. */
    private static float[] point(
            Direction.Axis axis1, float value1,
            Direction.Axis axis2, float value2,
            Direction.Axis axis3, float value3
    ) {
        float[] xyz = new float[3];
        HelperMethods.setAxis(xyz, axis1, value1);
        HelperMethods.setAxis(xyz, axis2, value2);
        HelperMethods.setAxis(xyz, axis3, value3);
        return xyz;
    }
}
