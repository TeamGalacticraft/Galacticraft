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
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;

// ===== Pack 12-bit local coords (x:0..3, y:4..7, z:8..11) + BlockState =====
public record BlockData(short packed, BlockState state) {
    // Create from local coords (0..15) within a section
    public static BlockData ofLocal(int lx, int ly, int lz, BlockState state) {
        return new BlockData(pack(lx, ly, lz), state);
    }

    // Create from world position + its section
    public static BlockData ofWorld(SectionPos section, BlockPos worldPos, BlockState state) {
        int lx = worldPos.getX() & 15;
        int ly = worldPos.getY() & 15;
        int lz = worldPos.getZ() & 15;
        return ofLocal(lx, ly, lz, state);
    }

    public static short pack(int lx, int ly, int lz) {
        return (short) ((lx & 0xF) | ((ly & 0xF) << 4) | ((lz & 0xF) << 8));
    }

    // Unpack helpers
    public int localX() {
        return packed & 0xF;
    }

    public int localY() {
        return (packed >>> 4) & 0xF;
    }

    public int localZ() {
        return (packed >>> 8) & 0xF;
    }

    // Rebuild absolute BlockPos when you have the section (map key)
    public BlockPos toBlockPos(SectionPos section) {
        return new BlockPos(
                section.minBlockX() + localX(),
                section.minBlockY() + localY(),
                section.minBlockZ() + localZ()
        );
    }
}