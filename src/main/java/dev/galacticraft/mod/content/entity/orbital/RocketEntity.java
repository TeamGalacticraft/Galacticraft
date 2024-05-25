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

package dev.galacticraft.mod.content.entity.orbital;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attachments.GCServerPlayer;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.advancements.GCTriggers;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.content.entity.data.GCEntityDataSerializers;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.events.RocketEvents;
import dev.galacticraft.mod.network.packets.RocketSpawnPacket;
import dev.galacticraft.mod.particle.EntityParticleOption;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.util.FluidUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class RocketEntity extends Entity implements Rocket, IgnoreShift, ControllableEntity {
    private static final ResourceLocation NULL_ID = new ResourceLocation("null");
    private static final EntityDataAccessor<LaunchStage> STAGE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.LAUNCH_STAGE);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> TIME_AS_STATE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_TICKS = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_SIDE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DAMAGE_WOBBLE_STRENGTH = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<ResourceLocation> ROCKET_CONE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.IDENTIFIER);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_BODY = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.IDENTIFIER);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_FIN = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.IDENTIFIER);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_BOOSTER = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.IDENTIFIER);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_ENGINE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.IDENTIFIER);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_UPGRADE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.IDENTIFIER);

    public static final EntityDataAccessor<Long> FUEL = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.LONG);
    private final boolean debugMode = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    private BlockPos linkedPad = BlockPos.ZERO;
    private final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets(100), () -> {
        this.entityData.set(FUEL, getTank().getAmount());
    });
    private int timeBeforeLaunch;
    private float timeSinceLaunch;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

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
    public RocketCone<?, ?> getCone() {
        return this.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_CONE).get(this.cone());
    }

    @Override
    public RocketBody<?, ?> getBody() {
        return this.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_BODY).get(this.body());
    }

    @Override
    public RocketFin<?, ?> getFin() {
        return this.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_FIN).get(this.fin());
    }

    @Override
    public RocketBooster<?, ?> getBooster() {
        return this.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_BOOSTER).get(this.booster());
    }

    @Override
    public RocketEngine<?, ?> getEngine() {
        return this.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_ENGINE).get(this.engine());
    }

    @Override
    public RocketUpgrade<?, ?> getUpgrade() {
        return this.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_UPGRADE).get(this.upgrade());
    }

    @Override
    public @NotNull BlockPos getLinkedPad() {
        return linkedPad;
    }

    @Override
    public void setLinkedPad(@NotNull BlockPos blockPos) {
        this.linkedPad = blockPos;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && !this.isRemoved()) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                this.entityData.set(DAMAGE_WOBBLE_SIDE, -this.entityData.get(DAMAGE_WOBBLE_SIDE));
                this.entityData.set(DAMAGE_WOBBLE_TICKS, 10);
                this.entityData.set(DAMAGE_WOBBLE_STRENGTH, this.entityData.get(DAMAGE_WOBBLE_STRENGTH) + amount * 10.0F);
                boolean creative = source.getEntity() instanceof Player && ((Player) source.getEntity()).getAbilities().instabuild;
                if (creative || this.entityData.get(DAMAGE_WOBBLE_STRENGTH) > 40.0F) {
                    this.ejectPassengers();
                    if (creative && !this.hasCustomName()) {
                        this.remove(RemovalReason.DISCARDED);
                    } else {
                        this.dropItems(source, false);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.linkedPad != null && reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED) {
            BlockEntity blockEntity = this.level().getBlockEntity(this.linkedPad);
            if (blockEntity instanceof RocketLaunchPadBlockEntity pad) {
                pad.setLinkedRocket(null);
            }

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
    public void onBaseDestroyed() {

    }

    @Override
    public void dropItems(DamageSource damageSource, boolean b) {

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

    public int getScaledFuelLevel(int scale) {
        if (this.getFuelTankCapacity() <= 0) {
            return 0;
        }

        return (int) (this.getFuel() * scale / this.getFuelTankCapacity());
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
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = 10;
    }

    private void tickLerp() { // Stolen from the boat class to fix the rocket from bugging out
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double e = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double f = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
            double g = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            this.setYRot(this.getYRot() + (float) g / (float) this.lerpSteps);
            this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d, e, f);
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STAGE, LaunchStage.IDLE);
        this.entityData.define(SPEED, 0.0f);

        this.entityData.define(COLOR, -1);

        this.entityData.define(TIME_AS_STATE, 0);
        this.entityData.define(DAMAGE_WOBBLE_TICKS, 0);
        this.entityData.define(DAMAGE_WOBBLE_SIDE, 0);
        this.entityData.define(DAMAGE_WOBBLE_STRENGTH, 0.0F);

        this.entityData.define(ROCKET_CONE, NULL_ID);
        this.entityData.define(ROCKET_BODY, NULL_ID);
        this.entityData.define(ROCKET_FIN, NULL_ID);
        this.entityData.define(ROCKET_BOOSTER, NULL_ID);
        this.entityData.define(ROCKET_ENGINE, NULL_ID);
        this.entityData.define(ROCKET_UPGRADE, NULL_ID);

        this.entityData.define(FUEL, 0L);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
        player.startRiding(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getPassengers().isEmpty()) {
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
    protected Vector3f getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        return new Vector3f(0F, 1.5F, 0F);
    }

    @Override
    public void tick() {
        this.noPhysics = false;
        setTimeAsState(getTimeAsState() + 1);

        super.tick();
        tickLerp();

        int particleChance;

        if (this.timeBeforeLaunch >= 100) {
            particleChance = Math.abs(this.timeBeforeLaunch / 100);
        } else {
            particleChance = 1;
        }
        if ((this.getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal() || this.getLaunchStage() == LaunchStage.IGNITED && this.random.nextInt(particleChance) == 0)) {
            this.spawnParticles();
        }

        if (!level().isClientSide()) {
            if (getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                this.timeSinceLaunch++;
            } else {
                this.timeSinceLaunch = 0;
            }

            if (this.getPassengers().isEmpty()) {
                if (getLaunchStage() != LaunchStage.FAILED) {
                    if (getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                        this.setLaunchStage(LaunchStage.FAILED);
                    } else {
                        this.setLaunchStage(LaunchStage.IDLE);
                    }
                }
            } else if (!(this.getFirstPassenger() instanceof Player) && this.getLaunchStage() != LaunchStage.FAILED) {
                if (getLaunchStage() == LaunchStage.LAUNCHED) {
                    this.setLaunchStage(LaunchStage.FAILED);
                } else {
                    this.setLaunchStage(LaunchStage.IDLE);
                    this.timeBeforeLaunch = getPreLaunchWait();
                }

                this.removePassenger(this.getFirstPassenger());
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
                    if (this.getLinkedPad() != BlockPos.ZERO) {
                        if (passenger instanceof ServerPlayer player) {
                            GCServerPlayer gcPlayer = GCServerPlayer.get(player);
                            gcPlayer.setRocketData(this);
                            gcPlayer.setLaunchpadStack(new ItemStack(GCBlocks.ROCKET_LAUNCH_PAD, 9));
                        }
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (level().getBlockState(getLinkedPad().offset(x, 0, z)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                                        && level().getBlockState(getLinkedPad().offset(x, 0, z)).getValue(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
                                    level().setBlock(getLinkedPad().offset(x, 0, z), Blocks.AIR.defaultBlockState(), Block.UPDATE_NONE);
                                }
                            }
                        }
                    }
                    this.setSpeed(0.0f);
                }
            } else if (getLaunchStage() == LaunchStage.LAUNCHED) {
                if (!debugMode && (isTankEmpty() || !this.getTank().getResource().getFluid().is(GCTags.FUEL)) && FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    this.setLaunchStage(LaunchStage.FAILED);
                } else {
                    try (Transaction t = Transaction.openOuter()) {
                        this.getTank().extract(FluidVariant.of(GCFluids.FUEL), FluidConstants.NUGGET, t); //todo find balanced values
                        t.commit();
                    }
//                    for (int i = 0; i < 4; i++)
//                        ((ServerLevel) level()).sendParticles(ParticleTypes.FLAME, this.getX() + (level().random.nextDouble() - 0.5), this.getY() - 7, this.getZ() + (level().random.nextDouble() - 0.5), 0, (level().random.nextDouble() - 0.5), -1, level().random.nextDouble() - 0.5, 0.12000000596046448D);
//                    for (int i = 0; i < 4; i++)
//                        ((ServerLevel) level()).sendParticles(ParticleTypes.CLOUD, this.getX() + (level().random.nextDouble() - 0.5), this.getY() - 7, this.getZ() + (level().random.nextDouble() - 0.5), 0, (level().random.nextDouble() - 0.5), -1, level().random.nextDouble() - 0.5, 0.12000000596046448D);

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

                    if (this.getYRot() > 90) {
                        this.setYRot(90);
                    }

                    if (this.getXRot() < -90) {
                        this.setXRot(-90);
                    }

                    double velX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                    double velY = Mth.sin((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                    double velZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                    this.setDeltaMovement(calculateVelocity());
                }

                if (this.position().y() >= 1200.0F) {
                    CelestialBody<CelestialBodyConfig, ? extends CelestialBodyType<CelestialBodyConfig>> body = CelestialBody.getByDimension(this.level()).orElse(null);
                    int id;
                    if (body != null) {
                        id = level().registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY).getId(body);
                    } else {
                        id = -1;
                    }
                    for (Entity entity : getPassengers()) {
                        if (entity instanceof ServerPlayer serverPlayer) {
                            GCServerPlayer gcPlayer = GCServerPlayer.get(serverPlayer);
                            gcPlayer.setRocketStacks(NonNullList.withSize(2, ItemStack.EMPTY)); // TODO un-hardcode this
                            RocketData data = RocketData.create(this.color(), this.cone(), this.body(), this.fin(), this.booster(), this.engine(), this.upgrade());
                            gcPlayer.setFuel(this.tank.getAmount());
                            CompoundTag tag = new CompoundTag();
                            data.toNbt(tag);
                            var rocket = new ItemStack(GCItems.ROCKET);
                            rocket.setTag(tag);
                            gcPlayer.setRocketItem(rocket);
                            serverPlayer.galacticraft$openCelestialScreen(data);
                            CompoundTag nbt = new CompoundTag();
                            data.toNbt(nbt);
                            FriendlyByteBuf buf = PacketByteBufs.create().writeNbt(nbt);
                            buf.writeInt(id);
                            ServerPlayNetworking.send(serverPlayer, Constant.Packet.PLANET_MENU_PACKET, buf);
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
        double velY = -Math.min(d, 1) * Math.cos((this.getYRot() - 180) / Constant.RADIANS_TO_DEGREES) * this.getSpeed();
        double velZ = -(50 * Math.sin(this.getYRot() / Mth.RAD_TO_DEG) * Math.sin(this.getXRot() * 0.01 / Constant.RADIANS_TO_DEGREES)) * (this.getSpeed() * 0.632D) * 1.58227848D;
//        double cosPitch = Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
//        double velX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * cosPitch;
//        double velY = Mth.sin((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
//        double velZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * cosPitch;
        return new Vec3(velX, velY, velZ);
    }

    protected void spawnParticles() {
        if (this.isAlive()) {
            double sinPitch = Math.sin(this.getXRot() / Constant.RADIANS_TO_DEGREES);
            double x1 = 2 * Math.cos(this.getYRot() / Constant.RADIANS_TO_DEGREES) * sinPitch;
            double z1 = 2 * Math.sin(this.getYRot() / Constant.RADIANS_TO_DEGREES) * sinPitch;
            double y1 = 2 * Math.cos((this.getXRot() - 180) / Constant.RADIANS_TO_DEGREES);
//            double cosPitch = Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
//            double x1 = 2 * -Math.sin(this.getYRot() / Constant.RADIANS_TO_DEGREES) * cosPitch;
//            double z1 = 2 * Math.cos(this.getYRot() / Constant.RADIANS_TO_DEGREES) * cosPitch;
//            double y1 = 2 * Math.sin((this.getXRot() - 90) / Constant.RADIANS_TO_DEGREES) * this.getSpeed();

            if (this.getLaunchStage() == LaunchStage.FAILED && this.linkedPad != null) {
                double modifier = this.getY() - this.linkedPad.getY();
                modifier = Math.min(Math.max(modifier, 120.0), 300.0);
                x1 *= modifier / 100.0D;
                y1 *= modifier / 100.0D;
                z1 *= modifier / 100.0D;
            }

            double y = this.yo + (this.getY() - this.yo) + y1 - this.getDeltaMovement().y + 1.2D;

            final double x2 = this.getX() + x1 - this.getDeltaMovement().x;
            final double z2 = this.getZ() + z1 - this.getDeltaMovement().z;

            LivingEntity riddenByEntity = !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof LivingEntity ? (LivingEntity) this.getPassengers().get(0) : null;

            if (getLaunchStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
//                Vector3 motionVec = new Vector3((float)x1, (float)y1, (float)z1);
//                Object[] rider = new Object[] { riddenByEntity };
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

    public void setCone(ResourceLocation id) {
        this.getEntityData().set(ROCKET_CONE, id);
    }

    public void setBody(ResourceLocation id) {
        this.getEntityData().set(ROCKET_BODY, id);
    }

    public void setFin(ResourceLocation id) {
        this.getEntityData().set(ROCKET_FIN, id);
    }

    public void setBooster(ResourceLocation id) {
        this.getEntityData().set(ROCKET_BOOSTER, id);
    }

    public void setEngine(ResourceLocation id) {
        this.getEntityData().set(ROCKET_ENGINE, id);
    }

    public void setUpgrade(ResourceLocation id) {
        this.getEntityData().set(ROCKET_UPGRADE, id);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setCone(tag.contains("Cone") ? new ResourceLocation(tag.getString("Cone")) : null);
        this.setBody(tag.contains("Body") ? new ResourceLocation(tag.getString("Body")) : null);
        this.setFin(tag.contains("Fin") ? new ResourceLocation(tag.getString("Fin")) : null);
        this.setBooster(tag.contains("Booster") ? new ResourceLocation(tag.getString("Booster")) : null);
        this.setEngine(tag.contains("Engine") ? new ResourceLocation(tag.getString("Engine")) : null);
        this.setUpgrade(tag.contains("Upgrade") ? new ResourceLocation(tag.getString("Upgrade")) : null);

        if (tag.contains("Color")) {
            this.setColor(tag.getInt("Color"));
        }

        if (tag.contains("Stage")) {
            this.setLaunchStage(LaunchStage.valueOf(tag.getString("Stage")));
        }

        if (tag.contains("Speed")) {
            setSpeed(tag.getFloat("Speed"));
        }

        this.linkedPad = new BlockPos(tag.getInt("lX"), tag.getInt("lY"), tag.getInt("lZ"));
    }

    @Override
    @ApiStatus.Internal
    public void setLevel(Level level) {
        super.setLevel(level);
    }

    public void setColor(int color) {
        this.getEntityData().set(COLOR, color);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.cone() != null) tag.putString("Cone", Objects.requireNonNull(this.cone()).toString());
        if (this.body() != null) tag.putString("Body", Objects.requireNonNull(this.body()).toString());
        if (this.fin() != null) tag.putString("Fin", Objects.requireNonNull(this.fin()).toString());
        if (this.booster() != null) tag.putString("Booster", Objects.requireNonNull(this.booster()).toString());
        if (this.engine() != null) tag.putString("Engine", Objects.requireNonNull(this.engine()).toString());
        if (this.upgrade() != null) tag.putString("Upgrade", Objects.requireNonNull(this.upgrade()).toString());

        tag.putString("Stage", getLaunchStage().name());
        tag.putDouble("Speed", this.getSpeed());
        tag.putInt("Color", this.color());

        tag.putInt("lX", linkedPad.getX());
        tag.putInt("lY", linkedPad.getY());
        tag.putInt("lZ", linkedPad.getZ());
    }

    @Override
    public @NotNull Packet getAddEntityPacket() {
        return ServerPlayNetworking.createS2CPacket(new RocketSpawnPacket(getType(), getId(), this.uuid, getX(), getY(), getZ(), getXRot(), getYRot(), this));
    }

    public int getTimeBeforeLaunch() {
        return timeBeforeLaunch;
    }

    public int getPreLaunchWait() {
        return 400;
    }

    @Override
    public int color() {
        return this.getEntityData().get(COLOR);
    }

    @Override
    public @Nullable ResourceKey<RocketCone<?, ?>> cone() {
        ResourceLocation location = this.getEntityData().get(ROCKET_CONE);
        if (location == null || NULL_ID.equals(location)) {
            return null;
        }
        return ResourceKey.create(RocketRegistries.ROCKET_CONE, location);
    }

    @Override
    public @Nullable ResourceKey<RocketBody<?, ?>> body() {
        ResourceLocation location = this.getEntityData().get(ROCKET_BODY);
        if (location == null || NULL_ID.equals(location)) {
            return null;
        }
        return ResourceKey.create(RocketRegistries.ROCKET_BODY, location);
    }

    @Override
    public @Nullable ResourceKey<RocketFin<?, ?>> fin() {
        ResourceLocation location = this.getEntityData().get(ROCKET_FIN);
        if (location == null || NULL_ID.equals(location)) {
            return null;
        }
        return ResourceKey.create(RocketRegistries.ROCKET_FIN, location);
    }

    @Override
    public @Nullable ResourceKey<RocketBooster<?, ?>> booster() {
        ResourceLocation location = this.getEntityData().get(ROCKET_BOOSTER);
        if (location == null || NULL_ID.equals(location)) {
            return null;
        }
        return ResourceKey.create(RocketRegistries.ROCKET_BOOSTER, location);
    }

    @Override
    public @Nullable ResourceKey<RocketEngine<?, ?>> engine() {
        ResourceLocation location = this.getEntityData().get(ROCKET_ENGINE);
        if (location == null || NULL_ID.equals(location)) {
            return null;
        }
        return ResourceKey.create(RocketRegistries.ROCKET_ENGINE, location);
    }

    @Override
    public @Nullable ResourceKey<RocketUpgrade<?, ?>> upgrade() {
        ResourceLocation location = this.getEntityData().get(ROCKET_UPGRADE);
        if (location == null || NULL_ID.equals(location)) {
            return null;
        }
        return ResourceKey.create(RocketRegistries.ROCKET_UPGRADE, location);
    }

    public void setData(RocketData data) {
        this.setColor(data.color());

        this.setCone(data.cone() != null ? data.cone().location() : null);
        this.setBody(data.body() != null ? data.body().location() : null);
        this.setFin(data.fin() != null ? data.fin().location() : null);
        this.setBooster(data.booster() != null ? data.booster().location() : null);
        this.setEngine(data.engine() != null ? data.engine().location() : null);
        this.setUpgrade(data.upgrade() != null ? data.upgrade().location() : null);
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
}
