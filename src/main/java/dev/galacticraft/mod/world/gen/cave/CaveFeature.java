package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;

public interface CaveFeature {
    void decorate(CaveFeatureContext context, BlockPos pos, CaveSampleType type, int hash);
}