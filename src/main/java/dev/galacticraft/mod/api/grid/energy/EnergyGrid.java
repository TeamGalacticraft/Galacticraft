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

package dev.galacticraft.mod.api.grid.energy;

import dev.galacticraft.mod.api.grid.Grid;
import dev.galacticraft.mod.api.grid.GridHelper;
import dev.galacticraft.mod.api.grid.GridTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.UUID;

/**
 * A grid of wires sharing one energy buffer.
 * Machines insert energy through any wire, and nodes next to blocks that accept energy push it out, round-robin.
 * Ported from ThermalDynamics' {@code EnergyGrid} (by covers1624).
 */
public class EnergyGrid extends Grid<EnergyGrid, EnergyGridNode> {
    public static final long MIN_CAPACITY = 10_000;
    public static final long DEFAULT_NODE_CAPACITY = 400;
    public static final long DEFAULT_TRANSFER_RATE = 128;

    protected final EnergyGridStorage storage = new EnergyGridStorage(MIN_CAPACITY);

    protected EnergyGridNode[] distArray = new EnergyGridNode[0];
    protected int distIndex = 0;

    public EnergyGrid(UUID id, ServerLevel level) {
        super(GridTypes.ENERGY, id, level);
    }

    @Override
    public EnergyGridNode newNode() {
        return new EnergyGridNode(this);
    }

    @Override
    protected void onNodeAdded(EnergyGridNode node) {
        node.readDuct();
    }

    @Override
    public void tick() {
        this.storage.tick();

        if (this.distArray.length != this.nodes.size()) {
            this.distArray = this.nodes.values().toArray(new EnergyGridNode[0]);
        }
        int curIndex = this.distIndex;
        if (this.distIndex >= this.distArray.length) {
            this.distIndex = 0;
        }

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
        if (this.storage.getEnergy() <= 0) {
            // Start with the next node next time, so every node gets its turn
            this.distIndex = i + 1;
            if (curIndex == this.distIndex) {
                --this.distIndex;
            }
            return true;
        }
        return false;
    }

    /**
     * {@return the base buffer size: the buffer contributions of all nodes, but at least {@link #MIN_CAPACITY}}
     */
    public long computeBaseCapacity() {
        long capacity = 0;
        for (EnergyGridNode node : this.nodes.values()) {
            capacity += node.getNodeCapacity();
        }
        return Math.max(MIN_CAPACITY, capacity);
    }

    @Override
    public void onModified() {
        this.distArray = new EnergyGridNode[0];
        this.storage.setBaseCapacity(this.computeBaseCapacity());
        super.onModified();
    }

    @Override
    public void onMerge(EnergyGrid from) {
        this.storage.setBaseCapacity(this.computeBaseCapacity());
        this.storage.setCapacity(this.storage.getCapacity() + from.storage.getCapacity());
        this.storage.setEnergy(this.storage.getEnergy() + from.storage.getEnergy());
    }

    @Override
    public void onSplit(List<EnergyGrid> others) {
        int totalNodes = 0;
        for (EnergyGrid grid : others) {
            totalNodes += grid.nodes.size();
            grid.storage.setBaseCapacity(grid.computeBaseCapacity());
            grid.storage.setCapacity(this.storage.getCapacity());
        }
        long energy = this.storage.getEnergy();
        if (energy <= 0 || totalNodes == 0) {
            return;
        }

        long energyPerNode = energy / totalNodes;
        long remainder = energy % totalNodes;
        for (EnergyGrid grid : others) {
            grid.storage.setEnergy(energyPerNode * grid.nodes.size());
        }
        // The first grid gets the rest. Why? Because there's always a first grid.
        others.getFirst().storage.setEnergy(others.getFirst().storage.getEnergy() + remainder);
    }

    @Override
    public boolean canConnectOnSide(BlockPos pos, Direction side) {
        if (!this.level.isLoaded(pos) || GridHelper.getDuct(this.level, pos) != null) {
            return false; // Ducts are connected through the grid, not externally
        }
        return EnergyStorage.SIDED.find(this.level, pos, side) != null;
    }

    public EnergyGridStorage getStorage() {
        return this.storage;
    }

    @Override
    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = super.save(registries);
        this.storage.save(tag);
        return tag;
    }

    @Override
    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        super.load(tag, registries);
        this.storage.setBaseCapacity(this.computeBaseCapacity());
        this.storage.load(tag);
    }

    @Override
    public String toString() {
        return super.toString() + "[energy=" + this.storage.getEnergy() + "/" + this.storage.getCapacity() + "]";
    }
}
