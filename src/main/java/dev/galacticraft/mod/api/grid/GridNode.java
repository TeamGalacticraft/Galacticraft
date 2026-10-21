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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * A node of a {@link Grid}: a duct at an end, a corner, a junction or next to a block it connects to.
 * Ported from ThermalDynamics' {@code GridNode}.
 */
public abstract class GridNode<G extends Grid<G, ?>> {
    protected final EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);
    protected G grid;
    protected BlockPos pos = BlockPos.ZERO;
    protected boolean loaded;
    protected boolean cached;

    protected GridNode(G grid) {
        this.grid = grid;
    }

    public CompoundTag save() {
        return new CompoundTag();
    }

    public void load(CompoundTag tag) {
    }

    /**
     * Called when this node was moved from the given grid to its current one.
     */
    public void onGridChange(G oldGrid) {
    }

    /**
     * Forgets the cached external connections, so they are looked up again.
     */
    public final void clearConnections() {
        this.connections.clear();
        this.cached = false;
    }

    public final G getGrid() {
        return this.grid;
    }

    public final BlockPos getPos() {
        return this.pos;
    }

    public final boolean isLoaded() {
        return this.loaded;
    }

    /**
     * {@return the sides this node connects to blocks outside the grid}
     */
    public final EnumSet<Direction> getConnections() {
        return this.connections;
    }

    protected @Nullable Duct<?, ?> getDuct() {
        return GridHelper.getDuct(this.getLevel(), this.pos);
    }

    public final ServerLevel getLevel() {
        return this.grid.getLevel();
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public void setGrid(G grid) {
        this.grid = grid;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public String toString() {
        return "Node" + this.pos.toShortString();
    }
}
