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

import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.rocket.part.GalacticraftRocketParts;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.data.GCEntityDataSerializers;
import dev.galacticraft.mod.events.RocketEvents;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.data.GCTags;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RocketEntity extends Entity implements Rocket {
    private static final EntityDataAccessor<LaunchStage> STAGE = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.LAUNCH_STAGE);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> TIME_AS_STATE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_TICKS = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_SIDE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DAMAGE_WOBBLE_STRENGTH = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Double> SPEED = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.DOUBLE);

    public static final EntityDataAccessor<ResourceLocation[]> PARTS = SynchedEntityData.defineId(RocketEntity.class, GCEntityDataSerializers.ROCKET_PART_IDS);
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
    public int getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }


    @Override
    public LaunchStage getStage() {
        return this.entityData.get(STAGE);
    }

    @Override
    public void setStage(LaunchStage launchStage) {
        LaunchStage oldStage = getStage();
        if (oldStage != launchStage) {
            this.entityData.set(STAGE, launchStage);
            setTimeAsState(0);
            RocketEvents.STAGE_CHANGED.invoker().onStageChanged(this, oldStage);
        }
    }

    @Override
    public ResourceLocation[] getPartIds() {
        return this.entityData.get(PARTS);
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
    public boolean canTravelTo(CelestialBody<?, ?> celestialBody) {
        Object2BooleanMap<ResourceLocation> map = new Object2BooleanArrayMap<>();
        TravelPredicateType.AccessType type = TravelPredicateType.AccessType.PASS;
        for (ResourceLocation part : this.getPartIds()) {
            map.put(part, true);
            type = type.merge(this.travel(this.level.registryAccess(), part, celestialBody, map));
        }
        return type == TravelPredicateType.AccessType.ALLOW;
    }

    private TravelPredicateType.AccessType travel(RegistryAccess manager, ResourceLocation part, CelestialBody<?, ?> type, Object2BooleanMap<ResourceLocation> map) {
        RocketPart part1 = RocketPart.getById(this.level.registryAccess(), part);
        return part1.travelPredicate().canTravelTo(type, p -> map.computeBooleanIfAbsent((ResourceLocation) p, (p1) -> {
            if (Arrays.asList(RocketEntity.this.getPartIds()).contains(p1)) {
                map.put(part, false);
                return RocketEntity.this.travel(manager, p1, type, map) != TravelPredicateType.AccessType.BLOCK;
            } else {
                return false;
            }
        }));
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
    public double getSpeed() {
        return this.entityData.get(SPEED);
    }

    @Override
    public void setSpeed(double speed) {
        this.entityData.set(SPEED, speed);
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
                if (getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    if (!isTankEmpty()) {
                        this.timeBeforeLaunch = 400;
                        this.setStage(this.getStage().next());
                        if (getStage() == LaunchStage.WARNING) {
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
    public void setPart(ResourceLocation resourceLocation, RocketPartType rocketPartType) {
        ResourceLocation[] ids = Arrays.copyOf(this.entityData.get(PARTS), this.entityData.get(PARTS).length);
        ids[rocketPartType.ordinal()] = resourceLocation;
        this.setParts(ids);
    }

    @Override
    public void setParts(ResourceLocation[] parts) {
        this.entityData.set(PARTS, parts);
    }

    @Override
    public ResourceLocation getPartForType(RocketPartType rocketType) {
        return this.getPartIds()[rocketType.ordinal()];
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STAGE, LaunchStage.IDLE);
        this.entityData.define(COLOR, -1);
        this.entityData.define(SPEED, 0.0D);

        this.entityData.define(TIME_AS_STATE, 0);
        this.entityData.define(DAMAGE_WOBBLE_TICKS, 0);
        this.entityData.define(DAMAGE_WOBBLE_SIDE, 0);
        this.entityData.define(DAMAGE_WOBBLE_STRENGTH, 0.0F);

        ResourceLocation[] parts = new ResourceLocation[RocketPartType.values().length];
        for (RocketPartType value : RocketPartType.values()) {
            parts[value.ordinal()] = GalacticraftRocketParts.getDefaultPartIdForType(value);
        }
        this.entityData.define(PARTS, parts);
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
                if (getStage() != LaunchStage.FAILED) {
                    if (getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                        this.setStage(LaunchStage.FAILED);
                    } else {
                        this.setStage(LaunchStage.IDLE);
                    }
                }
            } else if (!(this.getFirstPassenger() instanceof Player) && this.getStage() != LaunchStage.FAILED) {
                if (getStage() == LaunchStage.LAUNCHED) {
                    this.setStage(LaunchStage.FAILED);
                } else {
                    this.setStage(LaunchStage.IDLE);
                    this.timeBeforeLaunch = 400;
                }

                this.removePassenger(this.getFirstPassenger());
            }

            if (isOnFire() && !level.isClientSide) {
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.BlockInteraction.DESTROY);
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.BlockInteraction.DESTROY);
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.BlockInteraction.DESTROY);
                level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.BlockInteraction.DESTROY);
                this.remove(RemovalReason.KILLED);
            }

            if (getStage() == LaunchStage.IGNITED) {
                timeBeforeLaunch--;
                if (isTankEmpty() && !debugMode) {
                    this.setStage(LaunchStage.IDLE);
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
                    this.setStage(LaunchStage.LAUNCHED);
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
                    this.setSpeed(0.0D);
                }
            } else if (getStage() == LaunchStage.LAUNCHED) {
                if (!debugMode && (isTankEmpty() || !this.getTank().getResource().getFluid().is(GCTags.FUEL)) && FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    this.setStage(LaunchStage.FAILED);
                } else {
                    try (Transaction t = Transaction.openOuter()) {
                        this.getTank().extract(FluidVariant.of(GCFluids.FUEL), FluidConstants.NUGGET, t); //todo find balanced values
                        t.commit();
                    }
                    for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, this.getX() + (level.random.nextDouble() - 0.5), this.getY() - 7, this.getZ() + (level.random.nextDouble() - 0.5), 0, (level.random.nextDouble() - 0.5), -1, level.random.nextDouble() - 0.5, 0.12000000596046448D);
                    for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, this.getX() + (level.random.nextDouble() - 0.5), this.getY() - 7, this.getZ() + (level.random.nextDouble() - 0.5), 0, (level.random.nextDouble() - 0.5), -1, level.random.nextDouble() - 0.5, 0.12000000596046448D);

                    this.setSpeed(Math.min(0.75, this.getSpeed() + 0.05D));

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
                    for (Entity entity : getPassengers()) {
                        if (entity instanceof ServerPlayer serverPlayer) {
                            ResourceLocation[] partIds = this.getPartIds();
                            serverPlayer.setCelestialScreenState(RocketData.create(this.getColor(), partIds[0], partIds[1], partIds[2], partIds[3], partIds[4], partIds[5]));
                            ServerPlayNetworking.send(serverPlayer, new ResourceLocation(Constant.MOD_ID, "planet_menu_open"), PacketByteBufs.create().writeNbt(RocketData.create(this.getColor(), partIds[0], partIds[1], partIds[2], partIds[3], partIds[4], partIds[5]).toNbt(new CompoundTag())));
                            remove(RemovalReason.UNLOADED_WITH_PLAYER);
                            break;
                        }
                    }
                }
            } else if (!onGround) {
                this.setSpeed(Math.max(-1.5F, this.getSpeed() - 0.05D));

                double velX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                double velY = Mth.sin((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                double velZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * Mth.cos((this.getXRot() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                this.setDeltaMovement(velX, velY, velZ);
            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (getStage() == LaunchStage.FAILED) {
                setRot((this.getYRot() + level.random.nextFloat() - 0.5F * 8.0F) % 360.0F, (this.getXRot() + level.random.nextFloat() - 0.5F * 8.0F) % 360.0F);

                for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, this.getX() + (level.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (level.random.nextDouble() - 0.5), 0, level.random.nextDouble() - 0.5, 1, level.random.nextDouble() - 0.5, 0.12000000596046448D);

                if (this.onGround) {
                    for (int i = 0; i < 4; i++) level.explode(this, this.position().x + (level.random.nextDouble() - 0.5 * 4), this.position().y + (level.random.nextDouble() * 3), this.position().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.BlockInteraction.DESTROY);
                    this.remove(RemovalReason.KILLED);
                }
            }

            ticksSinceJump++;

        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        CompoundTag parts = tag.getCompound("Parts");
        ResourceLocation[] array = new ResourceLocation[RocketPartType.values().length];
        for (RocketPartType type : RocketPartType.values()) {
            if (parts.contains(type.getSerializedName())) {
                array[type.ordinal()] = new ResourceLocation(parts.getString(type.getSerializedName()));
            } else {
                array[type.ordinal()] = GalacticraftRocketParts.getDefaultPartIdForType(type);
            }
        }

        setParts(array);

        if (tag.contains("Color")) {
            this.setColor(tag.getInt("Color"));
        }

        if (tag.contains("Stage")) {
            this.setStage(LaunchStage.valueOf(tag.getString("Stage")));
        }

        if (tag.contains("Speed")) {
            setSpeed(tag.getDouble("Speed"));
        }

        this.linkedPad = new BlockPos(tag.getInt("lX"), tag.getInt("lY"), tag.getInt("lZ"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        CompoundTag parts = new CompoundTag();

        ResourceLocation[] partIds = this.getPartIds();
        for (int i = 0; i < partIds.length; i++) {
            ResourceLocation part = partIds[i];
            if (part != null) {
                parts.putString(RocketPartType.values()[i].getSerializedName(), part.toString());
            }
        }

        tag.putString("Stage", getStage().name());
        tag.putDouble("Speed", this.getSpeed());
        tag.putInt("Color", this.getColor());

        tag.put("Parts", parts);

        tag.putInt("lX", linkedPad.getX());
        tag.putInt("lY", linkedPad.getY());
        tag.putInt("lZ", linkedPad.getZ());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(Registry.ENTITY_TYPE.getId(this.getType()));
        buf.writeVarInt(this.getId());
        buf.writeUUID(this.uuid);
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());
        buf.writeByte((int) (this.getXRot() / 360F * 256F));
        buf.writeByte((int) (this.getYRot() / 360F * 256F));
        ResourceLocation[] parts = this.getPartIds();
        CompoundTag nbt = RocketData.create(this.getColor(), parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]).toNbt(new CompoundTag());
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
}
