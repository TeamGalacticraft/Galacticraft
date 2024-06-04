package dev.galacticraft.mod.content.entity.vehicle;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.api.rocket.entity.RequiresFuel;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Should contain code common to vehicles that the player can ride
 */
public abstract class GCPlayerRideableVehicleEntity extends GCVehicleEntity implements IgnoreShift, ControllableEntity, RequiresFuel {

    // **************************************** FIELDS ****************************************

    protected static final EntityDataAccessor<Long> FUEL = SynchedEntityData.defineId(GCVehicleEntity.class, EntityDataSerializers.LONG);

    protected final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets(this.getFuelTankCapacity()), () -> {
        this.entityData.set(FUEL, this.getFuelTank().getAmount());
    });

    // **************************************** CONSTRUCTOR ****************************************

    public GCPlayerRideableVehicleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // **************************************** DATA ****************************************

    @Override
    public boolean isPickable() { //Required to interact with the entity
        return true;
    }

    // **************************************** FUEL ****************************************

    public SingleFluidStorage getFuelTank() {
        return this.tank;
    }

    public int getFuelTankAmount() {
        return this.entityData.get(FUEL).intValue();
    }

    @Override
    public int getScaledFuelLevel(int scale) {
        if (this.getFuelTankCapacity() <= 0) {
            return 0;
        }
        return (int) (this.entityData.get(FUEL) * scale / this.getFuelTankCapacity());
    }

    // **************************************** INTERACTION ****************************************

    @Override
    public abstract InteractionResult interact(Player player, InteractionHand hand);

    @Override
    public abstract Vec3 getDismountLocationForPassenger(LivingEntity passenger);

}
