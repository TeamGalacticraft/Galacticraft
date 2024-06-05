package dev.galacticraft.mod.content.entity.vehicle;

import dev.galacticraft.api.rocket.entity.RequiresFuel;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public abstract class GCFueledVehicleEntity extends GCVehicleEntity implements RequiresFuel {

    // **************************************** FIELDS ****************************************

    protected static final EntityDataAccessor<Long> FUEL = SynchedEntityData.defineId(GCVehicleEntity.class, EntityDataSerializers.LONG);

    protected final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets((int) this.getFuelTankCapacity()), () -> {
        this.entityData.set(FUEL, this.getFuelTank().getAmount());
    });

    // **************************************** CONSTRUCTOR ****************************************

    public GCFueledVehicleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // **************************************** FUEL ****************************************

    @Override
    public SingleFluidStorage getFuelTank() {
        return this.tank;
    }

    @Override
    public long getFuelTankAmount() {
        return this.entityData.get(FUEL);
    }

    public boolean isTankEmpty() {
        return this.getFuelTank().getAmount() <= 0 || this.getFuelTank().getResource().isBlank();
    }

    @Override
    public @Nullable Fluid getFuelTankFluid() {
        return this.tank.isResourceBlank() ? null : this.tank.variant.getFluid();
    }

    @Override
    public int getScaledFuelLevel(int scale) {
        if (this.getFuelTankCapacity() <= 0) {
            return 0;
        }
        return (int) (this.entityData.get(FUEL) * scale / this.getFuelTankCapacity());
    }

}
