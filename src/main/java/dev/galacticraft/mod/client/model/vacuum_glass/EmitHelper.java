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
import net.minecraft.world.phys.Vec3;

public class EmitHelper {

    public record Box(
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ
    ) {}

    /** Emits all faces of the supplied box. */
    public static void emitBox(QuadEmitter emitter, Box box) {
        for (Direction face : Direction.values()) {
            emitBoxFace(emitter, face, box);
        }
    }

    public static void emitBox(
            QuadEmitter emitter,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ
    ) {
        emitBox(emitter, new Box(minX, minY, minZ, maxX, maxY, maxZ));
    }

    /** Emits a single face of the supplied box. */
    public static void emitBoxFace(QuadEmitter emitter, Direction face, Box box) {
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

    /** Emits all faces of a box except one. */
    public static void emitBoxWithoutFace(QuadEmitter emitter, Box box, Direction skipFace) {
        for (Direction face : Direction.values()) {
            if (face != skipFace) {
                emitBoxFace(emitter, face, box);
            }
        }
    }

    /** Emits all faces of a box except those in the supplied pair. */
    public static void emitBoxWithoutFaces(QuadEmitter emitter, Box box, VacuumGlassBakedModel.DirectionPair skipFaces) {
        for (Direction face : Direction.values()) {
            if (!skipFaces.contains(face)) {
                emitBoxFace(emitter, face, box);
            }
        }
    }

    /** Emits a custom quad. */
    public static void emitCustomQuad(
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

    public static void emitCustomQuad(
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

    /** Emits a custom triangle, inferring the nominal face from its vertices. */
    public static void emitCustomTri(
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

    public static void emitCustomTri(
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
