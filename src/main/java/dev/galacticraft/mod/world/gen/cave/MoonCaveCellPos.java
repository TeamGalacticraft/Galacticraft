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

package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;

public record MoonCaveCellPos(int x, int z) {
    public static MoonCaveCellPos fromChunk(ChunkPos chunk) {
        return new MoonCaveCellPos(
                Math.floorDiv(chunk.x, MoonCavePlanner.CELL_SIZE_CHUNKS),
                Math.floorDiv(chunk.z, MoonCavePlanner.CELL_SIZE_CHUNKS)
        );
    }

    public int minBlockX() {
        return this.x * MoonCavePlanner.CELL_SIZE_BLOCKS;
    }

    public int minBlockZ() {
        return this.z * MoonCavePlanner.CELL_SIZE_BLOCKS;
    }

    public int maxBlockX() {
        return this.minBlockX() + MoonCavePlanner.CELL_SIZE_BLOCKS - 1;
    }

    public int maxBlockZ() {
        return this.minBlockZ() + MoonCavePlanner.CELL_SIZE_BLOCKS - 1;
    }

    public int centerBlockX() {
        return this.minBlockX() + MoonCavePlanner.CELL_SIZE_BLOCKS / 2;
    }

    public int centerBlockZ() {
        return this.minBlockZ() + MoonCavePlanner.CELL_SIZE_BLOCKS / 2;
    }
}