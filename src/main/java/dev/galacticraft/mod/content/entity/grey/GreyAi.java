/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package dev.galacticraft.mod.content.entity.grey;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.allay.AllayAi;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

// TODO: need to reimplement avoiding the player after 3 seconds if too close
public class GreyAi {
    protected static Brain<?> makeBrain(Brain<GreyEntity> brain) {
        GreyAi.initCoreActivity(brain);
        GreyAi.initIdleActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<GreyEntity> brain) {

        brain.addActivity(Activity.CORE, 0,
                ImmutableList.of(new Swim(0.8f), new AnimalPanic(2.5f), new LookAtTargetSink(45, 90),
                        new MoveToTargetSink(),
                        new CountDownCooldownTicks(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)));

    }

    private static void initIdleActivity(Brain<GreyEntity> brain) {

        brain.addActivity(Activity.IDLE,
                ImmutableList.of(
                        Pair.of(1, SetEntityLookTarget.create(EntityType.PLAYER, 10.0f)),
                        Pair.of(1, StayCloseToTarget.create(GreyAi::getNearestPlayerPositionTracker, GreyAi::inRangeOfArchGrey, 3, 0, 0.9f)),
                        // if it gets too far from the nearest arch grey, go to it
                        // FIXME: figure out why it doesn't go fully to the nearest arch grey (intended behavior is ignoring all tasks until 3 blocks away)
                        Pair.of(2, SetEntityLookTarget.create(GCEntityTypes.ARCH_GREY, 60.0f)),
                        Pair.of(2, StayCloseToTarget.create(GreyAi::getNearestArchGreyPositionTracker, Entity::isAlive, 2, 20, 0.9f)),
                        Pair.of(3, SetEntityLookTarget.create(EntityType.ITEM, 10.0f)),
                        Pair.of(3, GoToWantedItem.create(grey -> true, 0.9f, true, 10)),
                        Pair.of(4, new RunOne<GreyEntity>(
                                ImmutableList.of(
                                        Pair.of(new DoNothing(20, 100), 1),
                                        Pair.of(RandomStroll.stroll(0.6f), 1)))
                        )
                )
        );

    }

    private static boolean inRangeOfArchGrey(LivingEntity livingEntity) {
        Optional<ArchGreyEntity> archGreyOptional = livingEntity.getBrain().getMemory(GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY);
        if (archGreyOptional.isPresent()) {
            ArchGreyEntity archGrey = archGreyOptional.get();
            if (archGrey.distanceTo(livingEntity) <= 17) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void updateActivity(GreyEntity grey) {
        grey.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }

    private static Optional<PositionTracker> getNearestArchGreyPositionTracker(LivingEntity livingEntity) {
        return GreyAi.getNearestArchGrey(livingEntity).map(grey -> new EntityTracker((Entity) grey, true));
    }

    public static Optional<Entity> getNearestArchGrey(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Optional<ArchGreyEntity> archGrey = livingEntity.getBrain().getMemory(GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY);
            if (archGrey.isPresent()) {
                Optional<UUID> optional = Optional.of(archGrey.get().getUUID());
                Entity entity = serverLevel.getEntity(optional.get());
                if (entity != null) {
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static Optional<PositionTracker> getNearestPlayerPositionTracker(LivingEntity livingEntity) {
        return GreyAi.getNearestPlayer(livingEntity).map(serverPlayer -> new EntityTracker((Entity)serverPlayer, true));
    }

    public static Optional<ServerPlayer> getNearestPlayer(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Optional<Player> playerOptional = livingEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
            if (playerOptional.isPresent()) {
                Optional<UUID> optional = Optional.of(playerOptional.get().getUUID());
                Entity entity = serverLevel.getEntity(optional.get());
                if (entity instanceof ServerPlayer serverPlayer) {
                    if ((serverPlayer.gameMode.isSurvival() || serverPlayer.gameMode.isCreative()) && serverPlayer.closerThan(livingEntity, 64.0)) {
                        return Optional.of(serverPlayer);
                    }
                }
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

}
