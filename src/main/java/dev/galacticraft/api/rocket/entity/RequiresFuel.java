package dev.galacticraft.api.rocket.entity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface RequiresFuel {

    @Nullable
    Fluid getFuelTankFluid();

    int getFuelTankAmount();

    int getFuelTankCapacity();

    Storage<FluidVariant> getFuelTank();

}
