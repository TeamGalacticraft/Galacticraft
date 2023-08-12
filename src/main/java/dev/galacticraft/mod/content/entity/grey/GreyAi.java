/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package dev.galacticraft.mod.content.entity.grey;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

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
        brain.addActivityWithConditions(Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, GoToWantedItem.create(allay -> true, 1.75f, true, 32)),
                        Pair.of(3, SetEntityLookTargetSometimes.create(6.0f, UniformInt.of(30, 60))),
                        Pair.of(4,
                                new RunOne(ImmutableList.of(Pair.of(RandomStroll.stroll(1.0f), 2),
                                        Pair.of(SetWalkTargetFromLookTarget.create(1.0f, 3), 2),
                                        Pair.of(new DoNothing(30, 60), 1))))),
                ImmutableSet.of());
    }

    public static void updateActivity(GreyEntity allay) {
        allay.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
    }
}
