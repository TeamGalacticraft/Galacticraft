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

package dev.galacticraft.mod.world.gen.feature.features;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeferredBlockPlacement {
    private static final Map<ChunkPos, List<Placement>> QUEUE = new HashMap<>();

    public static void queue(BlockPos pos, BlockState state) {
        ChunkPos chunk = new ChunkPos(pos);
        QUEUE.computeIfAbsent(chunk, c -> new ArrayList<>()).add(new Placement(pos.immutable(), state));
    }

    public static void flush(WorldGenLevel level, ChunkPos chunk) {
        List<Placement> placements = QUEUE.remove(chunk);
        if (placements == null) return;

        for (Placement p : placements) {
            try {
                if (level.getBlockState(p.pos).isAir() || level.getBlockState(p.pos).is(Blocks.GLASS)) {
                    level.setBlock(p.pos, p.state, 2);
                }
            } catch (IllegalStateException ignored) {
                // If still unloaded (somehow), requeue
                queue(p.pos, p.state);
            }
        }
    }

    private record Placement(BlockPos pos, BlockState state) {}
}