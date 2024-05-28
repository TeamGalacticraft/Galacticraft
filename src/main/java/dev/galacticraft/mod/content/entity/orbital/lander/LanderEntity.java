/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.entity.orbital.lander;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attachments.GCServerPlayer;
import dev.galacticraft.mod.network.packets.ResetThirdPersonPacket;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.content.entity.ScalableFuelLevel;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.screen.ParachestMenu;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class LanderEntity extends AbstractLanderEntity implements Container, ScalableFuelLevel, ControllableEntity, HasCustomInventoryScreen, IgnoreShift, ExtendedScreenHandlerFactory {
    public static final float NO_PARTICLES = 0.0000001F;
    protected NonNullList<ItemStack> inventory;
    protected InventoryStorage storage;
    private final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets(100), () -> {
    });
    private final float turnFactor = 2.0F;
    private final float angle = 45;
    protected long ticks = 0;
    private double lastDeltaY;
    protected boolean lastOnGround;

    public LanderEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public LanderEntity(ServerPlayer player) {
        this(GCEntityTypes.LANDER, player.level());

        GCServerPlayer gcPlayer = GCServerPlayer.get(player);
        this.inventory = NonNullList.withSize(gcPlayer.getRocketStacks().size() + 1, ItemStack.EMPTY);
        this.storage = InventoryStorage.of(this, null);
        this.tank.variant = FluidVariant.of(GCFluids.FUEL);
        this.tank.amount = gcPlayer.getFuel();

        for (int i = 0; i < gcPlayer.getRocketStacks().size(); i++) {
            if (!gcPlayer.getRocketStacks().get(i).isEmpty()) {
                this.inventory.set(i, gcPlayer.getRocketStacks().get(i).copy());
            } else {
                this.inventory.get(i).setCount(0);
            }
        }

        moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tank.readNbt(tag);
        this.inventory = NonNullList.withSize(tag.getInt("size"), ItemStack.EMPTY);
        this.storage = InventoryStorage.of(this, null);
        ContainerHelper.loadAllItems(tag, this.inventory);

        this.lastDeltaY = this.getDeltaMovement().y;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tank.writeNbt(tag);

        tag.putInt("size", this.inventory.size());
        ContainerHelper.saveAllItems(tag, this.inventory);
    }

    public Pair<Vec3, Vec3> getParticlePosition() {
        double sinPitch = Math.sin(this.getXRot() / Constant.RADIANS_TO_DEGREES);
        final double x1 = 4 * Math.cos(this.getYRot() / Constant.RADIANS_TO_DEGREES) * sinPitch;
        final double z1 = 4 * Math.sin(this.getYRot() / Constant.RADIANS_TO_DEGREES) * sinPitch;
        final double y1 = -4 * Math.abs(Math.cos(this.getXRot() / Constant.RADIANS_TO_DEGREES));

        double motionY = getDeltaMovement().y();
        return new Pair<>(new Vec3(this.getX(), this.getY() + 1D + motionY / 2, this.getZ()), new Vec3(x1, y1 + motionY / 2, z1));
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        if (onGround()) {
            tickOnGround();
        } else {
            tickInAir();
        }

        Level level = level();

        setDeltaMovement(getLanderDeltaMovement());

        if (shouldSpawnParticles()) {
            if (level.isClientSide) {
                var particlePos = getParticlePosition();
                final Vec3 posVec = particlePos.getFirst();
                final Vec3 motionVec = particlePos.getSecond();
                level.addParticle(GCParticleTypes.LANDER_FLAME_PARTICLE, posVec.x(), posVec.y(), posVec.z(), motionVec.x(), motionVec.y(), motionVec.z());
            }
        }

        move(MoverType.SELF, getDeltaMovement());

        if (onGround() && !this.lastOnGround) {
            onGroundHit();
        }

        if (this.ticks < 40 && this.getY() > 150) {
            if (this.getPassengers().isEmpty()) {
                Player player = level.getNearestPlayer(this, 5);

                if (player != null && player.getVehicle() == null) {
                    player.startRiding(this);
                }
            }
        }

        if (!level.isClientSide) {
            ContainerItemContext context = ContainerItemContext.ofSingleSlot(InventoryStorage.of(this, null).getSlot(this.inventory.size() - 1));
            Storage<FluidVariant> fluidStorage = context.find(FluidStorage.ITEM);
            if (fluidStorage != null && !tank.isResourceBlank() && tank.getAmount() > 0) {
                try (Transaction tx = Transaction.openOuter()) {
                    tank.extract(tank.getResource(), fluidStorage.insert(tank.getResource(), tank.getAmount(), tx), tx);
                    tx.commit();
                }
            }
        }

        pushEntities();

        this.xo = getX();
        this.yo = getY();
        this.zo = getZ();
        this.lastOnGround = onGround();
        this.lastDeltaY = getDeltaMovement().y();
    }

    protected void pushEntities() {
        AABB box = getBoundingBox().inflate(0.2D, 0.4D, 0.2D);

        final List<Entity> entities = this.level().getEntities(this, box);

        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                if (!this.getPassengers().contains(entity)) {
                    push(entity);
                }
            }
        }
    }

    public void push(Entity entity) {
        if (!this.getPassengers().contains(entity) && this.getVehicle() != entity) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d = entity.getX() - this.getX();
                double e = entity.getZ() - this.getZ();
                double f = Mth.absMax(d, e);
                if (f >= 0.01F) {
                    f = Math.sqrt(f);
                    d /= f;
                    e /= f;
                    double g = 1.0 / f;
                    if (g > 1.0) {
                        g = 1.0;
                    }

                    d *= g;
                    e *= g;
                    d *= 0.05F;
                    e *= 0.05F;
                    this.push(-d, 0.0, -e);
                }
            }
        }
    }

    public void onGroundHit() {
        if (!level().isClientSide) {
            if (Math.abs(this.lastDeltaY) > 2.0D) {
                for (Entity entity : this.getPassengers()) {
                    entity.removeVehicle();
                    if (entity instanceof ServerPlayer player) {
                        ServerPlayNetworking.send(player, new ResetThirdPersonPacket());
                    }
                    entity.setDeltaMovement(Vec3.ZERO);
                    entity.setPos(entity.getX(), this.getY() + 2.25, entity.getZ());
                }
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 12, true, Level.ExplosionInteraction.MOB);

                discard();
            }
        }
    }

    public void tickOnGround() {
        setXRot(NO_PARTICLES);
    }

    public void tickInAir() {
        if (!this.onGround()) {
            this.addDeltaMovement(new Vec3(0, CelestialBody.getByDimension(level()).map(CelestialBody::gravity).orElse(1F) * -0.008D, 0));
        }

        double motY = -1 * Math.sin(getXRot() / Constant.RADIANS_TO_DEGREES);
        double motX = Math.cos(getYRot() / Constant.RADIANS_TO_DEGREES) * motY;
        double motZ = Math.sin(getYRot() / Constant.RADIANS_TO_DEGREES) * motY;

        setDeltaMovement(new Vec3(motX / 2.0F, getDeltaMovement().y(), motZ / 2.0F));
    }

    public Vec3 getLanderDeltaMovement() {
        if (this.onGround()) {
            return Vec3.ZERO;
        }

        if (this.ticks >= 40 && this.ticks < 45) {
            setDeltaMovement(0, -2.5D, 0);
        }

        Vec3 delta = getDeltaMovement();
        return new Vec3(delta.x(), this.ticks < 40 ? 0 : delta.y(), delta.z());
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            if (!this.onGround()) {
                return InteractionResult.FAIL;
            }

            if (!this.getPassengers().isEmpty()) {
                this.ejectPassengers();
            }

            return InteractionResult.SUCCESS;
        }

        if (this.getPassengers().isEmpty()) {
            openCustomInventoryScreen(player);
            return InteractionResult.SUCCESS;
        } else if (player instanceof ServerPlayer) {
            if (!this.onGround()) {
                return InteractionResult.FAIL;
            }

            this.ejectPassengers();
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public int getScaledFuelLevel(int scale) {
        final double fuelLevel = this.tank.getResource().isBlank() ? 0 : this.tank.getAmount();

        return (int) (fuelLevel * scale / tank.getCapacity());
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return this.position().closerThan(player.position(), 8.0);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBoolean(false);
        buf.writeVarInt(this.inventory.size());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ParachestMenu(containerId, inventory, this);
    }

    @Override
    public float getDamageMultiplier() {
        return 5;
    }

    @Override
    public float getMaxDamage() {
        return 100;
    }

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        if (shouldMove())
            super.move(movementType, movement);
    }

    @Override
    public boolean shouldMove() {
        if (this.ticks < 40) {
            return false;
        }

        return !this.onGround();
    }

    @Override
    public boolean shouldSpawnParticles() {
        return this.ticks > 40 && getXRot() != NO_PARTICLES;
    }

    @Override
    protected Vector3f getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0, 1.5F, 0);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(getX(), getY(), getZ());
    }

    @Override
    public void inputTick(float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown) {
        if (!onGround()) {
            if (up)
                setXRot(Math.min(Math.max(getXRot() - 0.5F * turnFactor, -angle), angle));
            if (down)
                setXRot(Math.min(Math.max(getXRot() + 0.5F * turnFactor, -angle), angle));
            if (left)
                setYRot(getYRot() - 0.5F * turnFactor);
            if (right)
                setYRot(getYRot() + 0.5F * turnFactor);

            if (jumping) {
                var deltaM = getDeltaMovement();;
                setDeltaMovement(deltaM.x(), Math.min(deltaM.y() + 0.03F, getY() < level().getHeight(Heightmap.Types.WORLD_SURFACE, getBlockX(), getBlockZ()) + 35 ? -0.15 : -1.0), deltaM.z());
            }

            if (shiftKeyDown) {
                var deltaM = getDeltaMovement();
                setDeltaMovement(deltaM.x(), Math.min(deltaM.y() - 0.022F, -1.0), deltaM.z());
            }
        }
    }

    @Override
    public boolean shouldIgnoreShiftExit() {
        return !onGround();
    }
}
