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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A block entity that is part of a {@link Grid}, like a wire or a fluid pipe.
 * The grid only exists on the server.
 * Ported from ThermalDynamics' {@code IDuct}.
 */
public interface Duct<G extends Grid<G, N>, N extends GridNode<G>> {
    Level getHostLevel();

    BlockPos getHostPos();

    GridType<G> getGridType();

    /**
     * {@return the grid this duct is part of, or {@code null} on the client or before it was added}
     */
    @Nullable G getGrid();

    void setGrid(@Nullable G grid);

    default boolean hasGrid() {
        return this.getGrid() != null;
    }

    /**
     * {@return the node at this duct, or {@code null} if it lies on an edge}
     */
    default @Nullable N getNode() {
        G grid = this.getGrid();
        return grid == null ? null : grid.getNodes().get(this.getHostPos());
    }

    /**
     * {@return whether this duct can join the grid of the given duct on the given side}
     * The grid only connects two ducts when both of them allow it.
     */
    default boolean canConnectTo(Duct<?, ?> other, Direction direction) {
        return this.getGridType() == other.getGridType();
    }

    /**
     * {@return whether this duct could join the grid of the given duct if their grids held the same contents}
     * Ducts that can only connect later, like pipes with different fluids, are connected once one of their grids is empty.
     */
    default boolean canEverConnectTo(Duct<?, ?> other, Direction direction) {
        return this.canConnectTo(other, direction);
    }

    /**
     * Called when the grid connected this duct to the duct on the given side later, so it can show the connection.
     */
    default void onGridConnectionChanged(Direction direction) {
    }
}
