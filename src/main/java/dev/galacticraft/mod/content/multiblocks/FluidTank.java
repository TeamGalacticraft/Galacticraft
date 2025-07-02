package dev.galacticraft.mod.content.multiblocks;

import dev.galacticraft.multiblocklib.api.CuboidMultiblock;
import dev.galacticraft.multiblocklib.api.CuboidMultiblockConfig;

public class FluidTank extends CuboidMultiblock implements CuboidMultiblockConfig {
    @Override
    public void tick() {
        //empty for now
    }

    @Override
    public int getWidth() {
        return 5;
    }

    @Override
    public int getHeight() {
        return 5;
    }

    @Override
    public int getDepth() {
        return 5;
    }
}
