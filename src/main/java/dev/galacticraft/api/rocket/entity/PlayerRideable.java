package dev.galacticraft.api.rocket.entity;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface PlayerRideable extends IgnoreShift, ControllableEntity {

    default boolean isPickable() { //Required to interact with the entity
        return true;
    }

    InteractionResult interact(Player player, InteractionHand hand);

    Vec3 getDismountLocationForPassenger(LivingEntity passenger);

}
