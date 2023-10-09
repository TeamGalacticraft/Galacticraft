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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

public class GreyEntity extends PathfinderMob implements InventoryCarrier, Npc {

    // spawn packet stuff
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket);
    }

    private final SimpleContainer inventory = new SimpleContainer(1){
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };

    private static final EntityDataAccessor<ItemStack> DATA_HELD_WANTED_ITEM = SynchedEntityData.defineId(GreyEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_TICKS_HOLDING_WANTED_ITEM = SynchedEntityData.defineId(GreyEntity.class, EntityDataSerializers.INT);

    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(Items.DIAMOND);

    protected static final ImmutableList<SensorType<? extends Sensor<? super GreyEntity>>> SENSOR_TYPES = ImmutableList
            .of(SensorType.NEAREST_ITEMS, SensorType.NEAREST_PLAYERS, GCEntitySensorTypes.NEAREST_ARCH_GREY_SENSOR, GCEntitySensorTypes.TIME_NEAR_PLAYER_SENSOR);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.HURT_BY,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
            MemoryModuleType.IS_PANICKING, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY,
            GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE, GCEntityMemoryModuleTypes.SHOULD_AVOID_PLAYER
    );

    public GreyEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
    }

    static {
        EntityDataSerializers.registerSerializer(DATA_HELD_WANTED_ITEM.getSerializer());
        EntityDataSerializers.registerSerializer(DATA_TICKS_HOLDING_WANTED_ITEM.getSerializer());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_HELD_WANTED_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(DATA_TICKS_HOLDING_WANTED_ITEM, 0);
    }

    public void setHeldItem(ItemStack itemStack) {
        this.getEntityData().set(DATA_HELD_WANTED_ITEM, itemStack);
    }

    public ItemStack getHeldItemRaw() {
        return this.getEntityData().get(DATA_HELD_WANTED_ITEM);
    }

    public void setTicksHoldingWantedItem(int ticks) {
        this.getEntityData().set(DATA_TICKS_HOLDING_WANTED_ITEM, ticks);
    }

    public int getTicksHoldingWantedItem() {
        return this.getEntityData().get(DATA_TICKS_HOLDING_WANTED_ITEM);
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
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readInventoryFromTag(compoundTag);
        ItemStack itemStack = ItemStack.of(compoundTag.getCompound("Item"));
        this.setHeldItem(itemStack);
        int ticksHoldingHeldItem = compoundTag.getInt("HeldItemTicks");
        this.setTicksHoldingWantedItem(ticksHoldingHeldItem);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.writeInventoryToTag(compoundTag);
        ItemStack itemStack = this.getHeldItemRaw();
        if (!itemStack.isEmpty()) {
            compoundTag.put("Item", itemStack.save(new CompoundTag()));
        }
        compoundTag.putInt("HeldItemTicks", this.getTicksHoldingWantedItem());
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (!itemStack.isEmpty() && itemStack.getCount() > 0) {
            ItemStack pickedUpItem = itemStack.copy();
            pickedUpItem.setCount(1); // Pick up only one item
            // Handle the picked up item as needed, for example:
            this.inventory.addItem(pickedUpItem);
            this.take(itemEntity, 1); // animation to take item from ground
            setHeldItem(getInventory().getItem(0));
            itemStack.shrink(1); // Decrease the count of the original item stack
            if (itemStack.isEmpty()) {
                itemEntity.remove(RemovalReason.DISCARDED); // Remove the item entity if the stack is empty
            }
        }

        this.onItemPickup(itemEntity);
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        Item item = itemStack.getItem();
        System.out.println(item);
        return (WANTED_ITEMS.contains(item) && this.getInventory().canAddItem(itemStack) && this.getInventory().isEmpty());
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
    public void tick() {
        super.tick();
        doTick();
    }

    public void doTick() {
        if (!getHeldItemRaw().isEmpty()) {
            setTicksHoldingWantedItem(getTicksHoldingWantedItem() + 1); // increases ticks for how long entity held item
        }

    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }
}