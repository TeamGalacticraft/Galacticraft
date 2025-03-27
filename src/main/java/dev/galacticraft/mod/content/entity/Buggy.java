/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.FuelDock;
import dev.galacticraft.mod.api.entity.Dockable;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntFunction;

public class Buggy extends GCVehicle implements ContainerListener, ControllableEntity, Dockable, VariantHolder<Buggy.BuggyType> {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Buggy.class, EntityDataSerializers.INT);
    public static final int TANK_CAPACITY = 1;
    private final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets(TANK_CAPACITY), () -> {
    });
    public double speed;
    public float wheelRotationZ;
    public float wheelRotationX;
    float maxSpeed = 0.5F;
    float accel = 0.2F;
    float turnFactor = 3.0F;
    private FuelDock landingPad;
    private int timeClimbing;
    private boolean shouldClimb;

    protected SimpleContainer inventory;

    public Buggy(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID, 0);
        createInventory();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        setVariant(BuggyType.byName(nbt.getString("Type")));
        createInventory();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putString("Type", getVariant().getSerializedName());
    }

    @Override
    public void inputTick(float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown) {
        if (up) { // Accelerate
            this.speed += this.accel / 20D;
            this.shouldClimb = true;
        }
        if (down) { // Deccelerate
            this.speed -= this.accel / 20D;
            this.shouldClimb = true;
        }
        if (left) { // Left
            setYRot(getYRot() - 0.5F * this.turnFactor);
            this.wheelRotationZ = Math.max(-30.0F, Math.min(30.0F, this.wheelRotationZ + 0.5F));
        }
        if (right) { // Right
            setYRot(getYRot() + 0.5F * this.turnFactor);
            this.wheelRotationZ = Math.max(-30.0F, Math.min(30.0F, this.wheelRotationZ - 0.5F));
        }
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void createInventory() {
        SimpleContainer simpleContainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simpleContainer != null) {
            simpleContainer.removeListener(this);
            int size = Math.min(simpleContainer.getContainerSize(), this.inventory.getContainerSize());

            for (int j = 0; j < size; ++j) {
                ItemStack itemStack = simpleContainer.getItem(j);
                if (!itemStack.isEmpty()) {
                    this.inventory.setItem(j, itemStack.copy());
                }
            }
        }

        this.inventory.addListener(this);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.getPassengers().isEmpty()) {
//                player.sendSystemMessage(new TextComponentString(GameSettings.getKeyDisplayString(KeyHandlerClient.leftKey.getKeyCode()) + " / "
//                        + GameSettings.getKeyDisplayString(KeyHandlerClient.rightKey.getKeyCode()) + "  - " + GCCoreUtil.translate("gui.buggy.turn.name")));
//                player.sendSystemMessage(Component.keybind(GameSettings.getKeyDisplayString(KeyHandlerClient.accelerateKey.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.buggy.accel.name")));
//                player.sendSystemMessage(Component.keybind(GameSettings.getKeyDisplayString(KeyHandlerClient.decelerateKey.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.buggy.decel.name")));
//                player.sendSystemMessage(Component.keybind(GameSettings.getKeyDisplayString(KeyHandlerClient.openFuelGui.getKeyCode()) + "       - " + GCCoreUtil.translate("gui.buggy.inv.name")));
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            Vec3 delta = getDeltaMovement();
            this.wheelRotationX += (float) Math.sqrt(delta.x * delta.x + delta.z * delta.z) * 150.0F * (this.speed < 0 ? 1 : -1);
            this.wheelRotationX %= 360;
            this.wheelRotationZ = Math.max(-30.0F, Math.min(30.0F, this.wheelRotationZ * 0.9F));
        }

        if (!this.onGround()) {
            setDeltaMovement(getDeltaMovement().subtract(0, CelestialBody.getGravity(this) * 0.5D, 0));
        }

        if (this.wasTouchingWater && this.speed > 0.2D) {
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_BURN, SoundSource.NEUTRAL, 0.5F,
                    2.6F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.8F);
        }

        this.speed *= 0.98D;

        if (this.speed > this.maxSpeed) {
            this.speed = this.maxSpeed;
        }

        if (this.horizontalCollision && this.shouldClimb) {
            this.speed *= 0.9;
            setDeltaMovement(getDeltaMovement().x, Math.max(-0.15, 0.15D * ((-Math.pow((this.timeClimbing) - 1, 2)) / 250.0F) + 0.15F), getDeltaMovement().z);
            this.shouldClimb = false;
        }

        if ((getDeltaMovement().x == 0 || getDeltaMovement().z == 0) && !onGround()) {
            this.timeClimbing++;
        } else {
            this.timeClimbing = 0;
        }

        if (isControlledByLocalInstance()) {
            if (!this.tank.isResourceBlank() && this.tank.getAmount() > 0) {
                setDeltaMovement(-(this.speed * Math.cos((getYRot() - 90F) / Constant.RADIANS_TO_DEGREES)), getDeltaMovement().y, -(this.speed * Math.sin((getYRot() - 90F) / Constant.RADIANS_TO_DEGREES)));
            }

            move(MoverType.SELF, getDeltaMovement());
        }
    }

    @Override
    public SlotAccess getSlot(int mappedIndex) {
        return SlotAccess.forContainer(this.inventory, mappedIndex);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : super.getControllingPassenger();
    }

    @Override
    public void containerChanged(Container sender) {

    }

    @Override
    public void setVariant(BuggyType type) {
        this.entityData.set(DATA_TYPE_ID, type.getId());
    }

    @Override
    public BuggyType getVariant() {
        return BuggyType.byId(this.entityData.get(DATA_TYPE_ID));
    }

    @Override
    public void setPad(FuelDock pad) {
        this.landingPad = pad;
    }

    @Override
    public FuelDock getLandingPad() {
        return this.landingPad;
    }

    @Override
    public void onPadDestroyed() {
        this.landingPad = null;
    }

    @Override
    public boolean isDockValid(FuelDock dock) {
        return false;
    }

    @Override
    public boolean inFlight() {
        return false;
    }

    @Override
    public Entity asEntity() {
        return this;
    }

    @Override
    public Fluid getFuelTankFluid() {
        return this.tank.getResource().getFluid();
    }

    @Override
    public long getFuelTankAmount() {
        return this.tank.getAmount();
    }

    @Override
    public long getFuelTankCapacity() {
        return this.tank.getCapacity();
    }

    @Override
    public Storage<FluidVariant> getFuelTank() {
        return this.tank;
    }

    public enum BuggyType implements StringRepresentable {
        NORMAL(0, "no_storage"),
        STORAGE_18(1, "storage_18"),
        STORAGE_36(2, "storage_36");

        public static final StringRepresentable.EnumCodec<BuggyType> CODEC = StringRepresentable.fromEnum(BuggyType::values);
        private static final IntFunction<BuggyType> BY_ID = ByIdMap.continuous(BuggyType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

        private final int id;
        private final String name;

        BuggyType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public int getStorage() {
            return this.id * 18;
        }

        public boolean hasStorage() {
            return getStorage() > 0;
        }

        public int getId() {
            return this.id;
        }

        public static BuggyType byName(String name) {
            return CODEC.byName(name, NORMAL);
        }

        public static BuggyType byId(int id) {
            return BY_ID.apply(id);
        }
    }
}
