/*
 * Copyright (c) 2019-2023 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.mod.content.entity.goals.FollowPlayerGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.StartAdmiringItemIfSeen;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Set;
// FIXME: figure out why the grey occasionally follows random outside of the arch grey such as cows, villagers, and other mobs ):
public class GreyEntity extends PathfinderMob implements InventoryCarrier, Npc {

    @Nullable
    private FollowPlayerGoal followPlayerGoal;
    @Nullable
    private GreyAvoidEntityGoal greyAvoidEntityGoal;

    boolean isNearbyPlayer;
    boolean allowedToRemoveFollowingGoal = true;
    int tickAgeNearOrAwayFromPlayer = 0;

    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.DIAMOND);

    private static final ImmutableList<MemoryModuleType<?>> GREY_MEMORY_TYPES =
            ImmutableList.of(
                    MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
                    MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.LOOK_TARGET
            );

    private static final ImmutableList<SensorType<? extends Sensor<? super GreyEntity>>> GREY_SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_ITEMS);

    public GreyEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }

    protected Brain.Provider<GreyEntity> brainProvider() {
        return Brain.provider(GREY_MEMORY_TYPES, GREY_SENSOR_TYPES);
    }

    @Override
    public Brain<?> getBrain() {
        return super.getBrain();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<GreyEntity> brain = this.brainProvider().makeBrain(dynamic);
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(StartAdmiringItemIfSeen.create(120)));
        return brain;
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        InventoryCarrier.pickUpItem(this, this, itemEntity);
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        Item item = itemStack.getItem();
        System.out.println(item);
        return (WANTED_ITEMS.contains(item) && this.getInventory().canAddItem(itemStack));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        RandomSource randomSource = serverLevelAccessor.getRandom();
        populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    // 20% pickaxe, 20% sword, 60% nothing
    // TODO: change sword and pickaxe to a custom moon tool@Override
    protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
        super.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
        int randomNumber = randomSource.nextInt(100);
        if (randomNumber < 20) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_PICKAXE)); // 20% chance of a pickaxe
        } else if (randomNumber < 40) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD)); // 20% chance of a sword
        } else {
            // 60% chance of no item, so do nothing
        }
    }

    @Override
    protected void registerGoals() {
        // this.goalSelector.addGoal(0, new RandomLookAroundGoal(this));
        this.followPlayerGoal = new FollowPlayerGoal(this, 10.0F, 0.8, false);
        this.greyAvoidEntityGoal = new GreyAvoidEntityGoal<>(this, Player.class, 3.0f, 0.8, 1);

        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 15F));

        this.goalSelector.addGoal(1, this.followPlayerGoal);

        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.7, new Random().nextInt(25 - 20) + 20, true));


    }

    @Override
    public void tick() {
        super.tick();
        doTick();
    }

    @Override
    protected void customServerAiStep() {
        ServerLevel serverLevel = (ServerLevel) this.level();
        this.level().getProfiler().push("greyBrain");
        super.customServerAiStep();
    }

    @Nullable
    private FollowMobGoal followMobGoal;
    public void doTick() {
        if (!level().isClientSide) {
            handleFollowingGoal();
            handleAvoidEntityGoal();
            handleArchGreyZoneLeaveFollow();
        }
    }

    // go to arch grey if grey leaves zone
    private void handleArchGreyZoneLeaveFollow() {
        Mob archGreyEntity = this.level().getNearestEntity(ArchGreyEntity.class, TargetingConditions.forNonCombat().range(50), this, getX(), getY(), getZ(), this.getBoundingBox().inflate(50.0, 2.0, 50.0));
        if (archGreyEntity != null) { // An ArchGreyEntity is nearby
            boolean hasStartedFollowing = followMobGoal != null;
            if (this.distanceTo(archGreyEntity) > 20) {
                if (!hasStartedFollowing) { // Execute only once when starting to follow
                    this.followMobGoal = new FollowMobGoal(this, 1, 3, 50);
                    this.goalSelector.addGoal(0, this.followMobGoal);
                }
            } else if (this.distanceTo(archGreyEntity) < 4) {
                if (hasStartedFollowing) { // Execute only once when stopping following
                    this.goalSelector.removeGoal(this.followMobGoal);
                    this.followMobGoal = null;
                }
            }
        }
    }

    private void handleFollowingGoal() {
        if (allowedToRemoveFollowingGoal) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 2.5D);
            isNearbyPlayer = nearestPlayer != null;
            tickAgeNearOrAwayFromPlayer = isNearbyPlayer ? tickAgeNearOrAwayFromPlayer + 1 : 0;
            if (tickAgeNearOrAwayFromPlayer >= 50) {
                goalSelector.removeGoal(followPlayerGoal);
                goalSelector.addGoal(1, greyAvoidEntityGoal);
                allowedToRemoveFollowingGoal = false;
                tickAgeNearOrAwayFromPlayer = 0;
            }
        }
    }

    private void handleAvoidEntityGoal() {
        if (!allowedToRemoveFollowingGoal) {
            tickAgeNearOrAwayFromPlayer++;
            if (tickAgeNearOrAwayFromPlayer >= 80) {
                goalSelector.removeGoal(greyAvoidEntityGoal);
                goalSelector.addGoal(1, followPlayerGoal);
                allowedToRemoveFollowingGoal = true;
                tickAgeNearOrAwayFromPlayer = 0;
            }
        }
    }

    @Override
    public SimpleContainer getInventory() {
        return new SimpleContainer(1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0)
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D)
                .add(GcApiEntityAttributes.LOCAL_GRAVITY_LEVEL, 0.9F);
    }

    static class GreyAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final GreyEntity greyEntity;

        public GreyAvoidEntityGoal(GreyEntity greyEntity, Class<T> class_, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
            super(greyEntity, class_, maxDist, walkSpeedModifier, sprintSpeedModifier, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
            this.greyEntity = greyEntity;
        }

    }

}