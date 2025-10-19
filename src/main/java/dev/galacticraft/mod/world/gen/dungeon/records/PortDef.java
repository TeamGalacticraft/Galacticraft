/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.dungeon.records;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Port defined in LOCAL template coordinates (relative to template min origin).
 * The port is a rectangular plane on a template face, spanning [min..max] (inclusive).
 */
public record PortDef(
        String name,            // optional label, e.g. "north_exit_1"
        boolean entrance,       // true = entrance port
        boolean exit,           // true = exit port
        Direction facing,       // face normal in LOCAL template frame
        BlockPos min,           // local min corner (inclusive)
        BlockPos max            // local max corner (inclusive)
) {
    /**
     * Integer center in local coords (block center).
     */
    public BlockPos localCenterBlock() {
        return new BlockPos(
                (min.getX() + max.getX()) >> 1,
                (min.getY() + max.getY()) >> 1,
                (min.getZ() + max.getZ()) >> 1
        );
    }

    /**
     * Sub-block center in local coords (cube center of the plane area).
     */
    public net.minecraft.world.phys.Vec3 localCenter() {
        return new net.minecraft.world.phys.Vec3(
                (min.getX() + max.getX()) * 0.5 + 0.5,
                (min.getY() + max.getY()) * 0.5 + 0.5,
                (min.getZ() + max.getZ()) * 0.5 + 0.5
        );
    }

    /**
     * Width × height of the port rectangle (in blocks).
     */
    public int area() {
        int dx = (max.getX() - min.getX()) + 1;
        int dy = (max.getY() - min.getY()) + 1;
        int dz = (max.getZ() - min.getZ()) + 1;
        // On a face, one of dx/dy/dz must be 1; area = product of the other two.
        return switch (facing.getAxis()) {
            case X -> dy * dz;
            case Y -> dx * dz;
            case Z -> dx * dy;
        };
    }
}