package io.github.teamgalacticraft.galacticraft.world.dimension;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collections;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDimensions {
    public static final DimensionType MOON = Registry.register(Registry.DIMENSION, 30, "galacticraft-rewoven:moon", new GalacticraftDimensionType(30, "galacticraft-rewoven:moon", "DIM30", MoonDimension::new, true));

    public static void init() {
    }

    public static void teleport(ServerCommandSource serverCommandSource_1, Entity entity_1, ServerWorld serverWorld_1) {
        BlockPos spawnPos = serverWorld_1.getSpawnPos();
        double x = spawnPos.getX();
        double y = spawnPos.getY();
        double z = spawnPos.getZ();

        if (entity_1 instanceof ServerPlayerEntity) {
            entity_1.stopRiding();
            if (((ServerPlayerEntity) entity_1).isSleeping()) {
                ((ServerPlayerEntity) entity_1).wakeUp(true, true, false);
            }

            if (serverWorld_1 == entity_1.world) {
                ((ServerPlayerEntity) entity_1).networkHandler.teleportRequest(x, y, z, 0, 0, Collections.emptySet());
            } else {
                ((ServerPlayerEntity) entity_1).teleport(serverWorld_1, x, y, z, 0, 0);
            }
        } else {
            if (serverWorld_1 == entity_1.world) {
                entity_1.setPosition(x, y, z);
            } else {
                entity_1.detach();
                entity_1.dimension = serverWorld_1.dimension.getType();
                Entity entity_2 = entity_1;
                entity_1 = entity_1.getType().create(serverWorld_1);
                if (entity_1 == null) {
                    return;
                }

                entity_1.method_5878(entity_2);
                entity_1.setPosition(x, y, z);
                serverWorld_1.method_18769(entity_1);
                entity_2.removed = true;
            }
        }

//        if (teleportCommand$class_3144_1 != null) {
//            GalacticraftDimensions.method_13772(serverCommandSource_1, entity_1);
//        }

        if (!(entity_1 instanceof LivingEntity) || !((LivingEntity) entity_1).isFallFlying()) {
            entity_1.setVelocity(entity_1.getVelocity().multiply(1.0D, 0.0D, 1.0D));
            entity_1.onGround = true;
        }
    }
}