package dev.galacticraft.mod.machine;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Region {

    boolean sealed;
    Set<BlockPos> insideBlocks;
    Set<BlockPos> outsideBlocks;
    SealerGroupings sealers;

    public Region(Set<BlockPos> insideBlocks, Queue<BlockPos> outsideBlocks, boolean sealed, SealerGroupings sealers) {
        this.insideBlocks = insideBlocks;
        this.outsideBlocks = new HashSet<>(outsideBlocks);
        this.sealed = sealed;
        this.sealers = sealers;
    }

    public Set<BlockPos> getInsideBlocks() {
        return this.insideBlocks;
    }

    public Set<BlockPos> getOutsideBlocks() {
        return this.outsideBlocks;
    }

    public boolean isSealed() {
        return sealed;
    }

    public SealerGroupings getSealerGroups() {
        return this.sealers;
    }
}
