package dev.galacticraft.mod.machine;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class Region {
    public static final Region EMPTY = new Region(new HashSet<>());
    private Set<BlockPos> blockRegionArea;

    public Region(Set<BlockPos> originalRegionArea)
    {
        blockRegionArea = originalRegionArea;
    }

    public boolean contains(BlockPos pos)
    {
        return blockRegionArea.contains(pos);
    }

    public int getRegionSize()
    {
        return blockRegionArea.size();
    }

    public Set<BlockPos> getPositions() {
        return blockRegionArea;
    }

    public void setPositions(Set<BlockPos> region) {
        this.blockRegionArea = region;
    }

    public void remove(BlockPos changedPos) {
        this.blockRegionArea.remove(changedPos);
    }

    public void add(BlockPos changedPos) {
        this.blockRegionArea.add(changedPos);
    }
}
