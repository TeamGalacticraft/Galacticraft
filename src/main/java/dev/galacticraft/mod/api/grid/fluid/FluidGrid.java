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

package dev.galacticraft.mod.api.grid.fluid;

import dev.galacticraft.mod.api.grid.Grid;
import dev.galacticraft.mod.api.grid.GridContainer;
import dev.galacticraft.mod.api.grid.GridHelper;
import dev.galacticraft.mod.api.grid.GridTypes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * A grid of fluid pipes sharing one tank, which holds one fluid at a time.
 * Grids holding different fluids don't connect until one of them is empty.
 * Ported from ThermalDynamics' {@code FluidGrid} (by King Lemming).
 */
public class FluidGrid extends Grid<FluidGrid, FluidGridNode> {
    public static final long MIN_CAPACITY = 8 * FluidConstants.BUCKET;
    public static final long NODE_CAPACITY = FluidConstants.BUCKET / 10;
    public static final long DEFAULT_TRANSFER_RATE = FluidConstants.BUCKET / 50;
    /**
     * How long the pipes keep showing their fluid after the grid ran empty, so they don't flicker.
     */
    private static final int EMPTY_DISPLAY_DELAY = 40;

    protected final FluidGridStorage storage = new FluidGridStorage(MIN_CAPACITY);

    protected FluidGridNode[] distArray = new FluidGridNode[0];
    protected int distIndex = 0;
    private FluidVariant displayedFluid = FluidVariant.blank();
    private int emptyTicks;
    private boolean needsUpdate;
    private boolean retriedWhileEmpty;

    public FluidGrid(UUID id, ServerLevel level) {
        super(GridTypes.FLUID, id, level);
    }

    /**
     * {@return whether the two grids could be one grid: when they hold the same fluid or one of them is empty}
     */
    public static boolean canHoldTogether(@Nullable FluidGrid a, @Nullable FluidGrid b) {
        if (a == null || b == null || a == b) {
            return true;
        }
        return a.storage.isEmpty() || b.storage.isEmpty() || a.storage.getVariant().equals(b.storage.getVariant());
    }

    @Override
    public FluidGridNode newNode() {
        return new FluidGridNode(this);
    }

    @Override
    protected void onNodeAdded(FluidGridNode node) {
        node.readDuct();
    }

    @Override
    public void tick() {
        this.storage.tick();

        if (this.distArray.length != this.nodes.size()) {
            this.distArray = this.nodes.values().toArray(new FluidGridNode[0]);
        }
        int curIndex = this.distIndex;
        if (this.distIndex >= this.distArray.length) {
            this.distIndex = 0;
        }

        this.renderUpdate();
        this.retryWhenEmpty();

        for (int i = this.distIndex; i < this.distArray.length; ++i) {
            if (this.rrNodeTick(curIndex, i)) {
                this.storage.postTick();
                return;
            }
        }
        for (int i = 0; i < this.distIndex; ++i) {
            if (this.rrNodeTick(curIndex, i)) {
                this.storage.postTick();
                return;
            }
        }
        ++this.distIndex;
        this.storage.postTick();
    }

    private boolean rrNodeTick(int curIndex, int i) {
        if (!this.distArray[i].isLoaded()) {
            return false;
        }
        this.distArray[i].distributionTick();
        if (this.storage.isEmpty()) {
            this.distIndex = i + 1;
            if (curIndex == this.distIndex) {
                --this.distIndex;
            }
            return true;
        }
        return false;
    }

    /**
     * Tells the pipes which fluid to show. They keep showing a fluid for a while after the grid ran empty.
     */
    private void renderUpdate() {
        FluidVariant current = this.storage.getVariant();
        if (!current.isBlank()) {
            this.emptyTicks = 0;
            if (!current.equals(this.displayedFluid)) {
                this.displayedFluid = current;
                this.needsUpdate = true;
            }
        } else if (!this.displayedFluid.isBlank() && ++this.emptyTicks > EMPTY_DISPLAY_DELAY) {
            this.displayedFluid = FluidVariant.blank();
            this.needsUpdate = true;
        }

        if (this.needsUpdate) {
            this.needsUpdate = false;
            this.updateHosts();
        }
    }

    /**
     * Once this grid is empty, it can connect to pipes that it was kept apart from because of their fluid.
     */
    private void retryWhenEmpty() {
        if (!this.storage.isEmpty()) {
            this.retriedWhileEmpty = false;
        } else if (!this.retriedWhileEmpty) {
            this.retriedWhileEmpty = true;
            GridContainer.get(this.level).requestRetryBlocked(this);
        }
    }

    @Override
    public void onModified() {
        this.distArray = new FluidGridNode[0];
        this.storage.setBaseCapacity(this.computeBaseCapacity());
        this.needsUpdate = true;
        super.onModified();
    }

    private long computeBaseCapacity() {
        return Math.max(MIN_CAPACITY, this.nodes.size() * NODE_CAPACITY);
    }

    @Override
    public void onMerge(FluidGrid from) {
        assert canHoldTogether(this, from);
        FluidVariant variant = this.storage.isEmpty() ? from.storage.getVariant() : this.storage.getVariant();
        this.storage.setBaseCapacity(this.computeBaseCapacity());
        this.storage.setCapacity(this.storage.getCapacity() + from.storage.getCapacity());
        this.storage.setFluid(variant, this.storage.getAmount() + from.storage.getAmount());
        if (this.displayedFluid.isBlank()) {
            this.displayedFluid = from.displayedFluid;
        }
        this.needsUpdate = true;
    }

    @Override
    public void onSplit(List<FluidGrid> others) {
        int totalNodes = 0;
        for (FluidGrid grid : others) {
            totalNodes += grid.nodes.size();
            grid.storage.setBaseCapacity(grid.computeBaseCapacity());
            grid.storage.setCapacity(this.storage.getCapacity());
            grid.displayedFluid = this.displayedFluid;
            grid.needsUpdate = true;
        }
        if (this.storage.isEmpty() || totalNodes == 0) {
            return;
        }

        FluidVariant variant = this.storage.getVariant();
        long amountPerNode = this.storage.getAmount() / totalNodes;
        long remainder = this.storage.getAmount() % totalNodes;
        for (FluidGrid grid : others) {
            grid.storage.setFluid(variant, amountPerNode * grid.nodes.size());
        }
        // The first grid gets the rest. Why? Because there's always a first grid.
        FluidGrid first = others.getFirst();
        first.storage.setFluid(variant, first.storage.getAmount() + remainder);
    }

    @Override
    public boolean canConnectOnSide(BlockPos pos, Direction side) {
        if (!this.level.isLoaded(pos) || GridHelper.getDuct(this.level, pos) != null) {
            return false; // Ducts are connected through the grid, not externally
        }
        return FluidStorage.SIDED.find(this.level, pos, side) != null;
    }

    public FluidGridStorage getStorage() {
        return this.storage;
    }

    /**
     * {@return the fluid the pipes of this grid should show}
     */
    public FluidVariant getDisplayedFluid() {
        return this.displayedFluid;
    }

    @Override
    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = super.save(registries);
        this.storage.save(tag, registries);
        return tag;
    }

    @Override
    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        super.load(tag, registries);
        this.storage.setBaseCapacity(this.computeBaseCapacity());
        this.storage.load(tag, registries);
        this.displayedFluid = this.storage.getVariant();
    }

    @Override
    public String toString() {
        return super.toString() + "[fluid=" + this.storage.getVariant() + " " + this.storage.getAmount() + "/" + this.storage.getCapacity() + "]";
    }
}
