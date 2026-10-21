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

package dev.galacticraft.mod.api.grid;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

/**
 * Ticks the grids of every level and tells them about chunks loading and unloading.
 */
public final class GridEvents {
    private GridEvents() {
    }

    public static void register() {
        GridTypes.init();
        // Load the grids before the spawn chunks are loaded, so those chunk events reach them
        ServerWorldEvents.LOAD.register((server, level) -> GridContainer.get(level));
        // At the end of the tick, after all machines have inserted energy and fluid
        ServerTickEvents.END_WORLD_TICK.register(level -> GridContainer.get(level).tick());
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk) -> GridContainer.get(level).onChunkLoad(chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> GridContainer.get(level).onChunkUnload(chunk));
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, level) -> {
            if (blockEntity instanceof Duct<?, ?> duct) {
                GridContainer.get(level).onDuctLoaded(duct);
            }
        });
    }
}
