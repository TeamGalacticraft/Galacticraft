package dev.galacticraft.api.rocket.entity;

import dev.galacticraft.mod.content.entity.ScalableFuelLevel;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface RequiresFuel extends ScalableFuelLevel {

    @Nullable
    Fluid getFuelTankFluid();

    long getFuelTankAmount();

    long getFuelTankCapacity();

    boolean isTankEmpty();

    Storage<FluidVariant> getFuelTank();

}
