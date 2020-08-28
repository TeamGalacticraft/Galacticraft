package com.hrznstudio.galacticraft.block.special.fluidpipe;

import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class FluidPipeBlockEntity extends BlockEntity {
    private @NotNull FluidVolume fluid = FluidVolume.EMPTY;
    public FluidPipeBlockEntity() {
        super(GalacticraftBlockEntities.FLUID_PIPE_TYPE);
    }

    public void setFluid(@NotNull FluidVolume fluid) {
        this.fluid = fluid;
    }

    public @NotNull FluidVolume getFluid() {
        return fluid;
    }
}
