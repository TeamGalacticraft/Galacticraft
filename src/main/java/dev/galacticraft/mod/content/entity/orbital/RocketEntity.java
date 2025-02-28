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

package dev.galacticraft.mod.content.entity.orbital;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.FuelDock;
import dev.galacticraft.mod.attachments.GCServerPlayer;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.advancements.GCTriggers;
import dev.galacticraft.mod.content.block.special.launchpad.AbstractLaunchPad;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.content.entity.data.GCEntityDataSerializers;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.events.RocketEvents;
import dev.galacticraft.mod.network.s2c.OpenCelestialScreenPayload;
import dev.galacticraft.mod.network.s2c.RocketSpawnPacket;
import dev.galacticraft.mod.particle.EntityParticleOption;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.util.FluidUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class RocketEntity extends AdvancedVehicle implements Rocket, IgnoreShift, ControllableEntity {
    private static final ResourceLocation NULL_ID = ResourceLocation.withDefaultNamespace("null");
    private static final EntityDataAccessor<LaunchStage> STAGE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.LAUNCH_STAGE);

    private static final EntityDataAccessor<Integer> TIME_AS_STATE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<RocketData> ROCKET_DATA = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_DATA);

    public static final EntityDataAccessor<Long> FUEL = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.LONG);
    private final boolean debugMode = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    private FuelDock linkedPad = null;
    private final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets(100), () -> {
        this.entityData.set(FUEL, getTank().getAmount());
    });
    private int timeBeforeLaunch;
    private float timeSinceLaunch;

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public int getTimeAsState() {
        return this.entityData.get(TIME_AS_STATE);
    }

    public void setTimeAsState(int time) {
        this.entityData.set(TIME_AS_STATE, time);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public LivingEntity getControllingPassenger() {
        // If the controlling passenger is not null, then the rocket won't move
        return null;
    }

    @Override
    public LaunchStage getLaunchStage() {
        return this.entityData.get(STAGE);
    }

    @Override
    public void setLaunchStage(LaunchStage launchStage) {
        LaunchStage oldStage = getLaunchStage();
        if (oldStage != launchStage) {
            this.entityData.set(STAGE, launchStage);
            setTimeAsState(0);
            RocketEvents.STAGE_CHANGED.invoker().onStageChanged(this, oldStage);
        }
    }

    @Override
    public @NotNull RocketData getRocketData() {
        return this.entityData.get(ROCKET_DATA);
    }

    @Override
    public @NotNull BlockPos getLinkedPad() {
        return linkedPad.getDockPos();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.linkedPad != null && (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED)) {
            this.linkedPad.setDockedEntity(null);
        }
    }

    @Override
    protected boolean canRide(Entity ridable) {
        return false;
    }

    private long ticksSinceJump = 0;

    public SingleFluidStorage getTank() {
        return this.tank;
    }

    public boolean isTankEmpty() {
        return this.getTank().getAmount() <= 0 || this.getTank().getResource().isBlank();
    }

    @Override
    public void onJump() {
        if (!this.getPassengers().isEmpty() && ticksSinceJump > 10) {
            if (this.getFirstPassenger() instanceof ServerPlayer) {
                if (getLaunchStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    if (!isTankEmpty() || debugMode) {
                        this.timeBeforeLaunch = getPreLaunchWait();
                        this.setLaunchStage(this.getLaunchStage().next());
                        if (getLaunchStage() == LaunchStage.WARNING) {
                            ((ServerPlayer) this.getFirstPassenger()).sendSystemMessage(Component.translatable(Translations.Chat.ROCKET_WARNING), true);
                        }
                    }
                }
            }
        }
    }

    public void setFuel(long fuel) {
        try (Transaction tx = Transaction.openOuter()) {
            StorageUtil.extractAny(this.tank, Long.MAX_VALUE, tx);
            this.tank.insert(FluidVariant.of(GCFluids.FUEL), fuel, tx);
            tx.commit();
        }
    }

    public void setCreative(boolean creative) {

    }

    public long getFuel() {
        return this.entityData.get(FUEL);
    }

    @Override
    public void setPad(FuelDock pad) {
        this.linkedPad = pad;
    }

    @Override
    public FuelDock getLandingPad() {
        return this.linkedPad;
    }

    @Override
    public void onPadDestroyed() {
        var rocket = new ItemStack(GCItems.ROCKET);
        rocket.applyComponents(this.getRocketData().asPatch());
        this.spawnAtLocation(rocket);
        this.remove(RemovalReason.DISCARDED);
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
    public void dropItems(DamageSource damageSource, boolean exploded) {
        if (!exploded) {
            var rocket = new ItemStack(GCItems.ROCKET);
            rocket.applyComponents(this.getRocketData().asPatch());
            this.spawnAtLocation(rocket);
        }
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public @Nullable Fluid getFuelTankFluid() {
        return this.tank.isResourceBlank() ? null : this.tank.variant.getFluid();
    }

    @Override
    public long getFuelTankAmount() {
        return this.tank.getAmount();
    }

    @Override
    public long getFuelTankCapacity() {
        return this.tank.getCapacity();
    }

    public float getScaledFuelLevel(float scale) {
        if (this.getFuelTankCapacity() <= 0) {
            return 0;
        }

        return this.getFuel() * scale / this.getFuelTankCapacity();
    }

    @Override
    public Storage<FluidVariant> getFuelTank() {
        return this.tank;
    }

    @Override
    public Entity asEntity() {
        return this;
    }

    @Override
    public void setDeltaMovement(double x, double y, double z) {
        this.setDeltaMovement(new Vec3(x, y, z));
    }

    @Override
    public void setDeltaMovement(Vec3 vec3d) {
        super.setDeltaMovement(vec3d);
        this.hasImpulse = true;
    }

    @Override
    public void move(MoverType type, Vec3 vec3d) {
        if (onGround()) vec3d.multiply(1.0D, 0.0D, 1.0D);
        super.move(type, vec3d);
        this.getPassengers().forEach(this::positionRider);
    }

    @Override
    protected void reapplyPosition() {
        super.reapplyPosition();
        this.getPassengers().forEach(this::positionRider);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STAGE, LaunchStage.IDLE);
        builder.define(SPEED, 0.0f);

        builder.define(TIME_AS_STATE, 0);

        builder.define(ROCKET_DATA, RocketPrefabs.TIER_1);

        builder.define(FUEL, 0L);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
        return interact(player, hand);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getPassengers().isEmpty()) {
            player.absRotateTo(this.getYRot(), this.getXRot());
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isPickable() { //Required to interact with the entity
        return true;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vec3(0F, 1.5F, 0F);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(getX(), getY(), getZ() + 1f);
    }

    @Override
    public void tick() {
        this.noPhysics = false;
        setTimeAsState(getTimeAsState() + 1);

        super.tick();

        int particleChance;

        if (this.timeBeforeLaunch >= 100) {
            particleChance = Math.abs(this.timeBeforeLaunch / 100);
        } else {
            particleChance = 1;
        }
        if ((this.getLaunchStage() == LaunchStage.LAUNCHED || this.getLaunchStage() == LaunchStage.IGNITED) && this.random.nextInt(particleChance) == 0) {
            this.spawnParticles();
        }

        if (!level().isClientSide()) {
            if (getLaunchStage() == LaunchStage.LAUNCHED) {
                this.timeSinceLaunch++;
            } else {
                this.timeSinceLaunch = 0;
            }

            if (isOnFire() && !level().isClientSide) {
                level().explode(this, this.position().x + (level().random.nextDouble() - 0.5 * 4), this.position().y + (level().random.nextDouble() * 3), this.position().z + (level().random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                level().explode(this, this.position().x + (level().random.nextDouble() - 0.5 * 4), this.position().y + (level().random.nextDouble() * 3), this.position().z + (level().random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                level().explode(this, this.position().x + (level().random.nextDouble() - 0.5 * 4), this.position().y + (level().random.nextDouble() * 3), this.position().z + (level().random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                level().explode(this, this.position().x + (level().random.nextDouble() - 0.5 * 4), this.position().y + (level().random.nextDouble() * 3), this.position().z + (level().random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                this.remove(RemovalReason.KILLED);
            }
            Entity passenger = getFirstPassenger();
            if (getLaunchStage() == LaunchStage.IGNITED) {
                timeBeforeLaunch--;
                if (isTankEmpty() && !debugMode) {
                    this.setLaunchStage(LaunchStage.IDLE);

                    if (passenger instanceof ServerPlayer player) {
                        player.sendSystemMessage(Component.translatable(Translations.Ui.ROCKET_NO_FUEL), false);
                    }
                    return;
                }
                try (Transaction t = Transaction.openOuter()) {
                    this.getTank().extract(FluidVariant.of(GCFluids.FUEL), FluidConstants.NUGGET, t); //todo find balanced values
                    t.commit();
                }
                if (getTimeAsState() >= getPreLaunchWait()) {
                    this.setLaunchStage(LaunchStage.LAUNCHED);
                    this.setSpeed(0.0f);
                    BlockPos dockPos = this.getLinkedPad();
                    if (dockPos != BlockPos.ZERO) {
                        if (passenger instanceof ServerPlayer player) {
                            GCServerPlayer gcPlayer = GCServerPlayer.get(player);
                            gcPlayer.setRocketData(this.getRocketData());
                            gcPlayer.setLaunchpadStack(new ItemStack(GCBlocks.ROCKET_LAUNCH_PAD, 9));
                        }
                        this.linkedPad.setDockedEntity(null);
                        if (level().getBlockState(dockPos).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                                && level().getBlockState(dockPos).getValue(AbstractLaunchPad.PART) != AbstractLaunchPad.Part.NONE) {
                            level().destroyBlock(dockPos, false);
                        }
                    }
                }
            } else if (getLaunchStage() == LaunchStage.LAUNCHED) {
                if (!debugMode && (isTankEmpty() || !this.getTank().getResource().getFluid().is(GCTags.FUEL))) {
                    this.setLaunchStage(LaunchStage.FAILED);
                } else {
                    try (Transaction t = Transaction.openOuter()) {
                        this.getTank().extract(FluidVariant.of(GCFluids.FUEL), FluidConstants.NUGGET, t); //todo find balanced values
                        t.commit();
                    }

                    this.setSpeed(Math.min(0.75f, this.getSpeed() + 0.05f));

                    // Pitch: -45.0
                    // Yaw: 0.0
                    //
                    // X vel: 0.0
                    // Y vel: 0.3535533845424652
                    // Z vel: 0.223445739030838
                    // = 1.58227848
                    //
                    // I hope this is right

                    if (this.getXRot() > 90) {
                        this.setXRot(90);
                    } else if (this.getXRot() < -90) {
                        this.setXRot(-90);
                    }

                    this.setDeltaMovement(calculateVelocity());
                }

                if (this.position().y() >= 1200.0F) {
                    // will need to change is for rockets that are launched via launch controllers
                    if (this.getPassengers().isEmpty()) {
                        this.remove(RemovalReason.DISCARDED);
                    }

                    for (Entity entity : getPassengers()) {
                        if (entity instanceof ServerPlayer serverPlayer) {
                            GCServerPlayer gcPlayer = GCServerPlayer.get(serverPlayer);
                            gcPlayer.setRocketStacks(NonNullList.withSize(2, ItemStack.EMPTY)); // TODO un-hardcode this
                            gcPlayer.setFuel(this.tank.getAmount());
                            var rocket = new ItemStack(GCItems.ROCKET);
                            RocketData data = this.getRocketData();
                            rocket.applyComponents(data.asPatch());
                            gcPlayer.setRocketItem(rocket);
                            serverPlayer.galacticraft$openCelestialScreen(data);
                            ServerPlayNetworking.send(serverPlayer, new OpenCelestialScreenPayload(this.getRocketData(), this.level().galacticraft$getCelestialBody()));
                            remove(RemovalReason.UNLOADED_WITH_PLAYER);
                            break;
                        }
                    }
                }
            } else if (!onGround()) {
                this.setSpeed(Math.max(-1.5f, this.getSpeed() - 0.05f));

                this.setDeltaMovement(calculateVelocity());
            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (getLaunchStage() == LaunchStage.FAILED) {
                setRot((this.getYRot() + level().random.nextFloat() - 0.5F * 8.0F) % 360.0F, (this.getXRot() + level().random.nextFloat() - 0.5F * 8.0F) % 360.0F);

                for (int i = 0; i < 4; i++)
                    ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME, this.getX() + (level().random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (level().random.nextDouble() - 0.5), 0, level().random.nextDouble() - 0.5, 1, level().random.nextDouble() - 0.5, 0.12000000596046448D);

                if (this.onGround()) {
                    for (int i = 0; i < 4; i++)
                        level().explode(this, this.position().x + (level().random.nextDouble() - 0.5 * 4), this.position().y + (level().random.nextDouble() * 3), this.position().z + (level().random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                    this.remove(RemovalReason.KILLED);
                }
            }

            ticksSinceJump++;

        }

        if (getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
            if (getPassengers().size() >= 1) { // When the screen changes to the
                // map, the player is not riding
                // the rocket anymore.
                if (getPassengers().get(0) instanceof ServerPlayer player) {
                    GCTriggers.LAUNCH_ROCKET.trigger(player);
                }
            }
        }
    }

    public Vec3 calculateVelocity() {
        double d = this.timeSinceLaunch / 150;
        double velX = -(50 * Math.cos(this.getYRot() / Mth.RAD_TO_DEG) * Math.sin(this.getXRot() * 0.01 / Constant.RADIANS_TO_DEGREES)) * (this.getSpeed() * 0.632D) * 1.58227848D;
        double velY = -Math.min(d, 1) * Math.cos((this.getXRot() - 180) / Constant.RADIANS_TO_DEGREES) * this.getSpeed();
        double velZ = -(50 * Math.sin(this.getYRot() / Mth.RAD_TO_DEG) * Math.sin(this.getXRot() * 0.01 / Constant.RADIANS_TO_DEGREES)) * (this.getSpeed() * 0.632D) * 1.58227848D;
        return new Vec3(velX, velY, velZ);
    }

    protected void spawnParticles() {
        if (this.isAlive()) {
            double sinPitch = Math.sin(this.getXRot() / Constant.RADIANS_TO_DEGREES);
            double x1 = 2 * Math.cos(this.getYRot() / Constant.RADIANS_TO_DEGREES) * sinPitch;
            double y1 = 2 * Math.cos((this.getXRot() - 180) / Constant.RADIANS_TO_DEGREES);
            double z1 = 2 * Math.sin(this.getYRot() / Constant.RADIANS_TO_DEGREES) * sinPitch;

            if (this.getLaunchStage() == LaunchStage.FAILED && this.linkedPad != null) {
                double modifier = this.getY() - this.linkedPad.getDockPos().getY();
                modifier = Math.min(Math.max(modifier, 120.0), 300.0);
                x1 *= modifier / 100.0D;
                y1 *= modifier / 100.0D;
                z1 *= modifier / 100.0D;
            }

            double y = this.yo + (this.getY() - this.yo) + y1 - this.getDeltaMovement().y + 1.2D;

            final double x2 = this.getX() + x1 - this.getDeltaMovement().x;
            final double z2 = this.getZ() + z1 - this.getDeltaMovement().z;

            LivingEntity riddenByEntity = !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof LivingEntity ? (LivingEntity) this.getPassengers().get(0) : null;

            if (getLaunchStage() == LaunchStage.LAUNCHED) {
                EntityParticleOption particleData = new EntityParticleOption(GCParticleTypes.LAUNCH_FLAME_LAUNCHED, riddenByEntity == null ? null : riddenByEntity.getUUID());
                this.level().addParticle(particleData, x2 + 0.4 - this.random.nextDouble() / 10D, y, z2 + 0.4 - this.random.nextDouble() / 10D, x1, y1, z1);
                this.level().addParticle(particleData, x2 - 0.4 + this.random.nextDouble() / 10D, y, z2 + 0.4 - this.random.nextDouble() / 10D, x1, y1, z1);
                this.level().addParticle(particleData, x2 - 0.4 + this.random.nextDouble() / 10D, y, z2 - 0.4 + this.random.nextDouble() / 10D, x1, y1, z1);
                this.level().addParticle(particleData, x2 + 0.4 - this.random.nextDouble() / 10D, y, z2 - 0.4 + this.random.nextDouble() / 10D, x1, y1, z1);
                this.level().addParticle(particleData, x2, y, z2, x1, y1, z1);
                this.level().addParticle(particleData, x2 + 0.4, y, z2, x1, y1, z1);
                this.level().addParticle(particleData, x2, y, z2 + 0.4D, x1, y1, z1);
                this.level().addParticle(particleData, x2, y, z2 - 0.4D, x1, y1, z1);

            } else if (this.tickCount % 2 == 0) {
                y += 0.6D;
                EntityParticleOption particleData = new EntityParticleOption(GCParticleTypes.LAUNCH_FLAME_LAUNCHED, riddenByEntity == null ? null : riddenByEntity.getUUID());
                this.level().addParticle(particleData, x2 + 0.4 - this.random.nextDouble() / 10D, y, z2 + 0.4 - this.random.nextDouble() / 10D, this.random.nextDouble() / 2.0 - 0.25, 0.0, this.random.nextDouble() / 2.0 - 0.25);
                this.level().addParticle(particleData, x2 - 0.4 + this.random.nextDouble() / 10D, y, z2 + 0.4 - this.random.nextDouble() / 10D, this.random.nextDouble() / 2.0 - 0.25, 0.0, this.random.nextDouble() / 2.0 - 0.25);
                this.level().addParticle(particleData, x2 - 0.4 + this.random.nextDouble() / 10D, y, z2 - 0.4 + this.random.nextDouble() / 10D, this.random.nextDouble() / 2.0 - 0.25, 0.0, this.random.nextDouble() / 2.0 - 0.25);
                this.level().addParticle(particleData, x2 + 0.4 - this.random.nextDouble() / 10D, y, z2 - 0.4 + this.random.nextDouble() / 10D, this.random.nextDouble() / 2.0 - 0.25, 0.0, this.random.nextDouble() / 2.0 - 0.25);
            }
        }
    }

    public float getSpeed() {
        return this.getEntityData().get(SPEED);
    }

    public void setSpeed(float speed) {
        this.getEntityData().set(SPEED, speed);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setData(RocketData.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("data")).mapOrElse(Pair::getFirst, e -> RocketPrefabs.TIER_1));

        if (tag.contains("Stage")) {
            this.setLaunchStage(LaunchStage.valueOf(tag.getString("Stage")));
        }

        if (tag.contains("Speed")) {
            setSpeed(tag.getFloat("Speed"));
        }

        BlockEntity be = this.level().getBlockEntity(BlockPos.of(tag.getLong("Linked")));
        if (be instanceof FuelDock pad)
            this.linkedPad = pad;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        DataResult<Tag> result = RocketData.CODEC.encodeStart(NbtOps.INSTANCE, getRocketData());
        tag.put("data", result.getPartialOrThrow());

        tag.putString("Stage", getLaunchStage().name());
        tag.putDouble("Speed", this.getSpeed());

        if (this.linkedPad != null) tag.putLong("Linked", this.linkedPad.getDockPos().asLong());
    }

    @Override
    public Packet getAddEntityPacket(ServerEntity serverEntity) {
        return ServerPlayNetworking.createS2CPacket(new RocketSpawnPacket(getType(), getId(), this.uuid, getX(), getY(), getZ(), getXRot(), getYRot(), getRocketData()));
    }

    public int getTimeBeforeLaunch() {
        return timeBeforeLaunch;
    }

    public int getPreLaunchWait() {
        return 400;
    }

    @Override
    public @Nullable Holder<RocketCone<?, ?>> cone() {
        return maybeGet(getRocketData().cone());
    }

    @Override
    public @Nullable Holder<RocketBody<?, ?>> body() {
        return maybeGet(getRocketData().body());
    }

    @Override
    public @Nullable Holder<RocketFin<?, ?>> fin() {
        return maybeGet(getRocketData().fin());
    }

    @Override
    public @Nullable Holder<RocketBooster<?, ?>> booster() {
        return maybeGet(getRocketData().booster());
    }

    @Override
    public @Nullable Holder<RocketEngine<?, ?>> engine() {
        return maybeGet(getRocketData().engine());
    }

    @Override
    public @Nullable Holder<RocketUpgrade<?, ?>> upgrade() {
        return maybeGet(getRocketData().upgrade());
    }

    private <T> @Nullable Holder<T> maybeGet(Optional<EitherHolder<T>> holder) {
        return holder.flatMap(tEitherHolder -> tEitherHolder.unwrap(this.registryAccess())).orElse(null);
    }

    public void setData(RocketData data) {
        this.entityData.set(ROCKET_DATA, data);
    }

    @Override
    public void inputTick(float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left, boolean right, boolean jumping, boolean shiftKeyDown) {
        float turnFactor = 2.0F;
        float angle = 45;

        LaunchStage stage = getLaunchStage();

        if (jumping && stage.ordinal() < LaunchStage.IGNITED.ordinal())
            onJump();

        if (stage.ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
            if (up) {
                setXRot(Math.min(Math.max(getXRot() - 0.5F * turnFactor, -angle), angle));
            } else if (down) {
                setXRot((getXRot() + 2.0F) % 360.0f);
            }

            if (left) {
                setYRot((getYRot() - 2.0F) % 360.0f);
            } else if (right) {
                setYRot((getYRot() + 2.0F) % 360.0f);
            }
        }
    }

    @Override
    public boolean shouldIgnoreShiftExit() {
        return getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal();
    }

    @Override
    public void setLevel(Level level) { // public for render
        super.setLevel(level);
    }
}
