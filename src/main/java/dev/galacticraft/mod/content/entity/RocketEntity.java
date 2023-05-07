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

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.data.GCEntityDataSerializers;
import dev.galacticraft.mod.events.RocketEvents;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.util.FluidUtil;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class RocketEntity extends Entity implements Rocket {
    private static final EntityDataAccessor<LaunchStage> STAGE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.LAUNCH_STAGE);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> TIME_AS_STATE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_TICKS = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_SIDE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DAMAGE_WOBBLE_STRENGTH = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<ResourceLocation> ROCKET_CONE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_PART);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_BODY = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_PART);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_FIN = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_PART);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_BOOSTER = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_PART);
    public static final EntityDataAccessor<ResourceLocation> ROCKET_BOTTOM = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_PART);
    public static final EntityDataAccessor<ResourceLocation[]> ROCKET_UPGRADES = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_UPGRADES);
    private final boolean debugMode = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    private BlockPos linkedPad = BlockPos.ZERO;
    private final SingleFluidStorage tank = SingleFluidStorage.withFixedCapacity(FluidUtil.bucketsToDroplets(100), () -> {});
    private int timeBeforeLaunch;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        try (Transaction t = Transaction.openOuter()) { // TODO: remove when fuel loader is fully implemented
            getTank().insert(FluidVariant.of(GCFluids.FUEL), getTank().getCapacity(), t);
            t.commit();
        }
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
        return this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_CONE).get(this.cone());
    }

    @Override
    public RocketBody<?, ?> getBody() {
        return this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_BODY).get(this.body());
    }

    @Override
    public RocketFin<?, ?> getFin() {
        return this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_FIN).get(this.fin());
    }

    @Override
    public RocketBooster<?, ?> getBooster() {
        return this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_BOOSTER).get(this.booster());
    }

    @Override
    public RocketBottom<?, ?> getBottom() {
        return this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_BOTTOM).get(this.bottom());
    }

    @Override
    public RocketUpgrade<?, ?>[] getUpgrades() {
        ResourceLocation[] keys = this.upgrades();
        Registry<RocketUpgrade<?, ?>> registry = this.level.registryAccess().registryOrThrow(RocketRegistries.ROCKET_UPGRADE);
        RocketUpgrade[] upgrades = new RocketUpgrade[keys.length];
        for (int i = 0; i < keys.length; i++) {
            upgrades[i] = registry.get(keys[i]);
        }
        return upgrades;
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
        if (!this.level.isClientSide && !this.isRemoved()) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                this.entityData.set(DAMAGE_WOBBLE_SIDE, -this.entityData.get(DAMAGE_WOBBLE_SIDE));
                this.entityData.set(DAMAGE_WOBBLE_TICKS, 10);
                this.entityData.set(DAMAGE_WOBBLE_STRENGTH, this.entityData.get(DAMAGE_WOBBLE_STRENGTH) + amount * 10.0F);
                boolean creative = source.getEntity() instanceof Player && ((Player)source.getEntity()).getAbilities().instabuild;
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
        if (this.linkedPad != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.linkedPad);
            if (blockEntity instanceof RocketLaunchPadBlockEntity pad){
                pad.setRocketEntityId(Integer.MIN_VALUE);
                pad.setRocketEntityUUID(null);
            }

        }
    }

    @Override
    protected boolean canRide(Entity ridable) {
        return false;
    }

    @Override
    public Vector3d getVelocity() {
        return null;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.blockPosition();
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
                    if (!isTankEmpty()) {
                        this.timeBeforeLaunch = 400;
                        this.setLaunchStage(this.getLaunchStage().next());
                        if (getLaunchStage() == LaunchStage.WARNING) {
                            ((ServerPlayer) this.getFirstPassenger()).sendSystemMessage(Component.translatable("chat.galacticraft.rocket.warning"), true);
                        }
                    }
                }
            }
        }
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
        return this.tank.amount;
    }

    @Override
    public long getFuelTankCapacity() {
        return this.tank.getCapacity();
    }

    @Override
    public Entity getEntity() {
        return null;
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
        if (onGround) vec3d.multiply(1.0D, 0.0D, 1.0D);
        super.move(type, vec3d);
        this.getPassengers().forEach(this::positionRider);
    }

    @Override
    protected void reapplyPosition() {
        super.reapplyPosition();
        this.getPassengers().forEach(this::positionRider);
    }

    @Override
    public void lerpTo(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.lerpX = d;
        this.lerpY = e;
        this.lerpZ = f;
        this.lerpYRot = g;
        this.lerpXRot = h;
        this.lerpSteps = 10;
    }

    private void tickLerp() { // Stolen from the boat class to fix the rocket from bugging out
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double e = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double f = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double g = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            this.setYRot(this.getYRot() + (float)g / (float)this.lerpSteps);
            this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
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

        this.entityData.define(ROCKET_CONE, BuiltinObjects.INVALID_ROCKET_CONE.location());
        this.entityData.define(ROCKET_BODY, BuiltinObjects.INVALID_ROCKET_BODY.location());
        this.entityData.define(ROCKET_FIN, BuiltinObjects.INVALID_ROCKET_FIN.location());
        this.entityData.define(ROCKET_BOOSTER, BuiltinObjects.INVALID_ROCKET_BOOSTER.location());
        this.entityData.define(ROCKET_BOTTOM, BuiltinObjects.INVALID_ROCKET_BOTTOM.location());
        this.entityData.define(ROCKET_UPGRADES, new ResourceLocation[0]);
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
    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            passenger.setPos(this.getX(), this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset() - 2.5, this.getZ());
        }
    }

    @Override
    public void tick() {
        this.noPhysics = false;
        setTimeAsState(getTimeAsState() + 1);

        super.tick();
        tickLerp();

        if (!level.isClientSide()) {
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
                    this.timeBeforeLaunch = 400;
                }

                this.removePassenger(this.getFirstPassenger());
            }

            if (isOnFire() && !level.isClientSide) {
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                this.remove(RemovalReason.KILLED);
            }

            if (getLaunchStage() == LaunchStage.IGNITED) {
                timeBeforeLaunch--;
                if (isTankEmpty() && !debugMode) {
                    this.setLaunchStage(LaunchStage.IDLE);
                    if (this.getPassengers().get(0) instanceof ServerPlayer) {
                        ((ServerPlayer) this.getPassengers().get(0)).sendSystemMessage(Component.translatable("chat.galacticraft.rocket.no_fuel"), false);
                    }
                    return;
                }
                try (Transaction t = Transaction.openOuter()) {
                    this.getTank().extract(FluidVariant.of(GCFluids.FUEL), FluidConstants.NUGGET, t); //todo find balanced values
                    t.commit();
                }
                if (getTimeAsState() >= 400) {
                    this.setLaunchStage(LaunchStage.LAUNCHED);
                    if (this.getLinkedPad() != BlockPos.ZERO) {
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (level.getBlockState(getLinkedPad().offset(x, 0, z)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                                        && level.getBlockState(getLinkedPad().offset(x, 0, z)).getValue(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
                                    level.setBlock(getLinkedPad().offset(x, 0, z), Blocks.AIR.defaultBlockState(), 4);
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
                    for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, this.getX() + (level.random.nextDouble() - 0.5), this.getY() - 7, this.getZ() + (level.random.nextDouble() - 0.5), 0, (level.random.nextDouble() - 0.5), -1, level.random.nextDouble() - 0.5, 0.12000000596046448D);
                    for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, this.getX() + (level.random.nextDouble() - 0.5), this.getY() - 7, this.getZ() + (level.random.nextDouble() - 0.5), 0, (level.random.nextDouble() - 0.5), -1, level.random.nextDouble() - 0.5, 0.12000000596046448D);

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

                    double velX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                    double velY = Mth.sin((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                    double velZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                    this.setDeltaMovement(velX, velY, velZ);
                }

                if (this.position().y() >= 1200.0F) {
                    CelestialBody<CelestialBodyConfig, ? extends CelestialBodyType<CelestialBodyConfig>> body = CelestialBody.getByDimension(this.getLevel()).orElse(null);
                    int id;
                    if (body != null) {
                        id = level.registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY).getId(body);
                    } else {
                        id = -1;
                    }
                    for (Entity entity : getPassengers()) {
                        if (entity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.setCelestialScreenState(RocketData.create(this.color(), this.cone(), this.body(), this.fin(), this.booster(), this.bottom(), this.upgrades()));
                            FriendlyByteBuf buf = PacketByteBufs.create().writeNbt(RocketData.create(this.color(), this.cone(), this.body(), this.fin(), this.booster(), this.bottom(), this.upgrades()).toNbt(new CompoundTag()));
                            buf.writeInt(id);
                            ServerPlayNetworking.send(serverPlayer, new ResourceLocation(Constant.MOD_ID, "planet_menu_open"), buf);
                            remove(RemovalReason.UNLOADED_WITH_PLAYER);
                            break;
                        }
                    }
                }
            } else if (!onGround) {
                this.setSpeed(Math.max(-1.5f, this.getSpeed() - 0.05f));

                double velX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                double velY = Mth.sin((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                double velZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                this.setDeltaMovement(velX, velY, velZ);
            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (getLaunchStage() == LaunchStage.FAILED) {
                setRot((this.getYRot() + level.random.nextFloat() - 0.5F * 8.0F) % 360.0F, (this.getXRot() + level.random.nextFloat() - 0.5F * 8.0F) % 360.0F);

                for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, this.getX() + (level.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (level.random.nextDouble() - 0.5), 0, level.random.nextDouble() - 0.5, 1, level.random.nextDouble() - 0.5, 0.12000000596046448D);

                if (this.onGround) {
                    for (int i = 0; i < 4; i++) level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Level.ExplosionInteraction.TNT);
                    this.remove(RemovalReason.KILLED);
                }
            }

            ticksSinceJump++;

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

    public void setBottom(ResourceLocation id) {
        this.getEntityData().set(ROCKET_BOTTOM, id);
    }

    public void setUpgrades(ResourceLocation[] ids) {
        this.getEntityData().set(ROCKET_UPGRADES, ids);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setCone(new ResourceLocation(tag.getString("Cone")));
        this.setBody(new ResourceLocation(tag.getString("Body")));
        this.setFin(new ResourceLocation(tag.getString("Fin")));
        this.setBooster(new ResourceLocation(tag.getString("Booster")));
        this.setBottom(new ResourceLocation(tag.getString("Bottom")));
        ListTag list = tag.getList("Upgrades", Tag.TAG_STRING);
        ResourceLocation[] upgrades = new ResourceLocation[list.size()];
        for (int i = 0; i < list.size(); i++) {
            upgrades[i] = new ResourceLocation(list.getString(i));
        }
        this.setUpgrades(upgrades);

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

    public void setColor(int color) {
        this.getEntityData().set(COLOR, color);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Cone", this.cone().toString());
        tag.putString("Body", this.body().toString());
        tag.putString("Fin", this.fin().toString());
        tag.putString("Booster", this.booster().toString());
        tag.putString("Bottom", this.bottom().toString());
        ListTag list = new ListTag();
        for (ResourceLocation upgrade : this.upgrades()) {
            list.add(StringTag.valueOf(upgrade.toString()));
        }
        tag.put("Upgrades", list);

        tag.putString("Stage", getLaunchStage().name());
        tag.putDouble("Speed", this.getSpeed());
        tag.putInt("Color", this.color());

        tag.putInt("lX", linkedPad.getX());
        tag.putInt("lY", linkedPad.getY());
        tag.putInt("lZ", linkedPad.getZ());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(BuiltInRegistries.ENTITY_TYPE.getId(this.getType()));
        buf.writeVarInt(this.getId());
        buf.writeUUID(this.uuid);
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());
        buf.writeByte((int) (this.getXRot() / 360F * 256F));
        buf.writeByte((int) (this.getYRot() / 360F * 256F));
        CompoundTag nbt = RocketData.create(this.color(), this.cone(), this.body(), this.fin(), this.booster(), this.bottom(), this.upgrades()).toNbt(new CompoundTag());
        buf.writeNbt(nbt);
        return ServerPlayNetworking.createS2CPacket(Constant.id("rocket_spawn"), buf);
    }

    public int getTimeBeforeLaunch() {
        return timeBeforeLaunch;
    }

    @Override
    public SoundSource getSoundSource() {
        return super.getSoundSource();
    }

    @Override
    public CompoundTag toNbt(CompoundTag nbt) {
        return null;
    }

    @Override
    public int color() {
        return this.getEntityData().get(COLOR);
    }

    @Override
    public ResourceLocation cone() {
        return this.getEntityData().get(ROCKET_CONE);
    }

    @Override
    public ResourceLocation body() {
        return this.getEntityData().get(ROCKET_BODY);
    }

    @Override
    public ResourceLocation fin() {
        return this.getEntityData().get(ROCKET_FIN);
    }

    @Override
    public ResourceLocation booster() {
        return this.getEntityData().get(ROCKET_BOOSTER);
    }

    @Override
    public ResourceLocation bottom() {
        return this.getEntityData().get(ROCKET_BOTTOM);
    }

    @Override
    public ResourceLocation[] upgrades() {
        return this.getEntityData().get(ROCKET_UPGRADES);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean canTravel(RegistryAccess manager, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
        Object2BooleanMap<ResourceLocation> map = new Object2BooleanArrayMap<>();
        TravelPredicateType.Result type = TravelPredicateType.Result.PASS;
        RocketCone<?, ?> cone = this.getCone();
        RocketBody<?, ?> body = this.getBody();
        RocketFin<?, ?> fin = this.getFin();
        RocketBooster<?, ?> booster = this.getBooster();
        RocketBottom<?, ?> bottom = this.getBottom();
        RocketUpgrade<?, ?>[] upgrades = this.getUpgrades();
        TravelPredicateType.Result result = TravelPredicateType.Result.PASS;
        result = result.merge(cone.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        result = result.merge(body.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        result = result.merge(fin.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        result = result.merge(booster.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        result = result.merge(bottom.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        int i = 0;
        while (result != TravelPredicateType.Result.BLOCK && i < upgrades.length) {
            result = result.merge(upgrades[i].travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
            i++;
        }
        return result == TravelPredicateType.Result.ALLOW;
    }

    public void setData(RocketData data) {
        this.setColor(data.color());

        this.setCone(data.cone());
        this.setBody(data.body());
        this.setFin(data.fin());
        this.setBooster(data.booster());
        this.setBottom(data.bottom());
        this.setUpgrades(data.upgrades());
    }
}
