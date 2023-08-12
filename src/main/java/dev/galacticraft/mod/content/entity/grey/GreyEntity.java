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

package dev.galacticraft.mod.content.entity.grey;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import dev.galacticraft.mod.content.GCEntitySensorTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// FIXME: figure out why the grey occasionally follows random outside of the arch grey such as cows, villagers, and other mobs ):
public class GreyEntity extends PathfinderMob implements InventoryCarrier, Npc {
    private final SimpleContainer inventory = new SimpleContainer(1);

    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.DIAMOND);

    protected static final ImmutableList<SensorType<? extends Sensor<? super GreyEntity>>> SENSOR_TYPES = ImmutableList
            .of(SensorType.NEAREST_ITEMS, SensorType.NEAREST_PLAYERS, GCEntitySensorTypes.NEAREST_ARCH_GREY_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
            MemoryModuleType.IS_PANICKING, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY
    );

    public GreyEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }

    protected Brain.Provider<GreyEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return GreyAi.makeBrain(this.brainProvider().makeBrain(dynamic));
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

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation flyingPathNavigation = new GroundPathNavigation(this, level);
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
            MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
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
    protected void customServerAiStep() {
        this.level().getProfiler().push("greyBrain");
        ((Brain<GreyEntity>) this.getBrain()).tick((ServerLevel) this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("greyActivityUpdate");
        GreyAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }
}