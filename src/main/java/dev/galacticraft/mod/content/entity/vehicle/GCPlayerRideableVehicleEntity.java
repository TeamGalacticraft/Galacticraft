package dev.galacticraft.mod.content.entity.vehicle;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Should contain code common to vehicles that the player can ride
 */
public abstract class GCPlayerRideableVehicleEntity extends GCVehicleEntity implements IgnoreShift, ControllableEntity {

    public GCPlayerRideableVehicleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isPickable() { //Required to interact with the entity
        return true;
    }

}
