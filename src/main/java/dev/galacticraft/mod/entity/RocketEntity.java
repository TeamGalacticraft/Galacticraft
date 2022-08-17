/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.entity;

import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.ServerPlayerAccessor;
import dev.galacticraft.mod.api.rocket.part.GalacticraftRocketParts;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.tag.GalacticraftTag;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntity extends Entity implements Rocket {
    private static final EntityDataAccessor<LaunchStage> STAGE = SynchedEntityData.defineId(RocketEntity.class, GalacticraftEntityDataAccessorHandler.LAUNCH_STAGE);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(RocketEntity.class, EntityDataAccessorHandlerRegistry.INTEGER);

    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_TICKS = SynchedEntityData.defineId(RocketEntity.class, EntityDataAccessorHandlerRegistry.INTEGER);
    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_SIDE = SynchedEntityData.defineId(RocketEntity.class, EntityDataAccessorHandlerRegistry.INTEGER);
    public static final EntityDataAccessor<Float> DAMAGE_WOBBLE_STRENGTH = SynchedEntityData.defineId(RocketEntity.class, EntityDataAccessorHandlerRegistry.FLOAT);

    public static final EntityDataAccessor<Double> SPEED = SynchedEntityData.defineId(RocketEntity.class, GalacticraftEntityDataAccessorHandler.DOUBLE);

    public static final EntityDataAccessor<ResourceLocation[]> PARTS = SynchedEntityData.defineId(RocketEntity.class, GalacticraftEntityDataAccessorHandler.ROCKET_PART_IDS);
    private final boolean debugMode = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    private BlockPos linkedPad = BlockPos.ZERO;
//    private final SimpleFixedFluidInv tank = new SimpleFixedFluidInv(1, FluidAmount.ofWhole(10));

    public RocketEntity(EntityType<RocketEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public int getColor() {
        return this.entityData.get(COLOR);
    }

    private long timeAsState = 0;

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    public BlockPos getLinkedPad() {
        return linkedPad;
    }

    @Override
    public boolean canTravelTo(CelestialBody<?, ?> body) {
        Object2BooleanMap<ResourceLocation> map = new Object2BooleanArrayMap<>();
        TravelPredicateType.AccessType type = TravelPredicateType.AccessType.PASS;
        for (ResourceLocation part : this.getPartIds()) {
            map.put(part, true);
            type = type.merge(this.travel(this.level.registryAccess(), part, body, map));
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
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.isRemoved()) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                this.entityData.set(DAMAGE_WOBBLE_SIDE, -this.entityData.get(DAMAGE_WOBBLE_SIDE));
                this.entityData.set(DAMAGE_WOBBLE_TICKS, 10);
                this.entityData.set(DAMAGE_WOBBLE_STRENGTH, this.entityData.get(DAMAGE_WOBBLE_STRENGTH) + amount * 10.0F);
                boolean creative = source.getAttacker() instanceof Player && ((Player)source.getAttacker()).getAbilities().creativeMode;
                if (creative || this.SynchedEntityData.get(DAMAGE_WOBBLE_STRENGTH) > 40.0F) {
                    this.removeAllPassengers();
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
            BlockEntity pad = this.level.getBlockEntity(this.linkedPad);
            if (pad instanceof RocketLaunchPadBlockEntity){
                ((RocketLaunchPadBlockEntity) pad).setRocketEntityId(Integer.MIN_VALUE);
                ((RocketLaunchPadBlockEntity) pad).setRocketEntityUUID(null);
            }

        }
    }

    @Override
    protected boolean canStartRiding(Entity ridable) {
        return false;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
        player.startRiding(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setLinkedPad(BlockPos linkedPad) {
        this.linkedPad = linkedPad;
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
    public boolean collides() { //Required to interact with the entity
        return true;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            passenger.updatePosition(this.getX(), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() - 2.5, this.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
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
    public void addAdditionalSaveData(CompoundTag tag) {
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
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void setCustomName(@Nullable Component text) {
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return null;
    }

    @Override
    public void setCustomNameVisible(boolean visible) {

    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return () -> Collections.singleton(ItemStack.EMPTY).iterator();
    }

    @Override
    protected void initSynchedEntityData() {
        SynchedEntityData.startTracking(STAGE, LaunchStage.IDLE);
        SynchedEntityData.startTracking(COLOR, -1);
        SynchedEntityData.startTracking(SPEED, 0.0D);

        SynchedEntityData.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        SynchedEntityData.startTracking(DAMAGE_WOBBLE_SIDE, 0);
        SynchedEntityData.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);

        ResourceLocation[] parts = new ResourceLocation[RocketPartType.values().length];
        for (RocketPartType value : RocketPartType.values()) {
            parts[value.ordinal()] = GalacticraftRocketParts.getDefaultPartIdForType(value);
        }
        SynchedEntityData.startTracking(PARTS, parts);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(Registry.ENTITY_TYPE.getId(this.getType()));
        buf.writeVarInt(this.getId());
        buf.writeUUID(this.uuid);
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());
        buf.writeByte((int) (this.getPitch() / 360F * 256F));
        buf.writeByte((int) (this.getYaw() / 360F * 256F));
        ResourceLocation[] parts = this.getPartIds();
        buf.writeNbt(RocketData.create(this.getColor(), parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]).toNbt(new CompoundTag()));
        return new ClientboundCustomPayloadPacket(new ResourceLocation(Constant.MOD_ID, "rocket_spawn"), buf);
    }

    @Override
    public void tick() {
        this.noClip = false;
        timeAsState++;

        super.tick();

        if (!level.isClientSide()) {
            if (this.getPassengers().isEmpty()) {
                if (getStage() != LaunchStage.FAILED) {
                    if (getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                        this.setStage(LaunchStage.FAILED);
                    } else {
                        this.setStage(LaunchStage.IDLE);
                    }
                }
            } else if (!(this.getPassengers().get(0) instanceof Player) && this.getStage() != LaunchStage.FAILED) {
                if (getStage() == LaunchStage.LAUNCHED) {
                    this.setStage(LaunchStage.FAILED);
                } else {
                    this.setStage(LaunchStage.IDLE);
                }

                this.removePassenger(this.getPassengerList().get(0));
            }

            if (isOnFire() && !world.isClient) {
                level.explode(this, this.getPos().x + (level.random.nextDouble() - 0.5 * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                level.explode(this, this.getPos().x + (level.random.nextDouble() - 0.5 * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                level.explode(this, this.getPos().x + (level.random.nextDouble() - 0.5 * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                level.explode(this, this.getPos().x + (level.random.nextDouble() - 0.5 * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                this.remove(RemovalReason.KILLED);
            }

            if (getStage() == LaunchStage.IGNITED) {
                if (this.getTank().getInvFluid(0).isEmpty() && !debugMode) {
                    this.setStage(LaunchStage.IDLE);
                    if (this.getPassengerList().get(0) instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) this.getPassengerList().get(0)).sendMessage(Component.translatable("chat.galacticraft.rocket.no_fuel"), false);
                    }
                    return;
                }
                this.getTank().extractFluid(0, key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidVolumeUtil.EMPTY, FluidAmount.of(1, 100), Simulation.ACTION); //todo find balanced values
                if (timeAsState >= 400) {
                    this.setStage(LaunchStage.LAUNCHED);
                    if (this.getLinkedPad() != BlockPos.ORIGIN) {
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (level.getBlockState(getLinkedPad().add(x, 0, z)).getBlock() == GalacticraftBlock.ROCKET_LAUNCH_PAD
                                        && level.getBlockState(getLinkedPad().add(x, 0, z)).get(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
                                    level.setBlockState(getLinkedPad().add(x, 0, z), Blocks.AIR.getDefaultState(), 4);
                                }
                            }
                        }
                    }
                    this.setSpeed(0.0D);
                }
            } else if (getStage() == LaunchStage.LAUNCHED) {
                if (!debugMode && (this.getTank().getInvFluid(0).isEmpty() || !GalacticraftTag.FUEL.contains(this.getTank().getInvFluid(0).getRawFluid())) && FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    this.setStage(LaunchStage.FAILED);
                } else {
                    this.getTank().extractFluid(0, key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidVolumeUtil.EMPTY, FluidAmount.of(1, 100), Simulation.ACTION); //todo find balanced values
                    for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, this.getX() + (level.random.nextDouble() - 0.5), this.getY(), this.getZ() + (level.random.nextDouble() - 0.5), 0, (level.random.nextDouble() - 0.5), -1, level.random.nextDouble() - 0.5, 0.12000000596046448D);
                    for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.CLOUD, this.getX() + (level.random.nextDouble() - 0.5), this.getY(), this.getZ() + (level.random.nextDouble() - 0.5), 0, (level.random.nextDouble() - 0.5), -1, level.random.nextDouble() - 0.5, 0.12000000596046448D);

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

                    double velX = -Mth.sin(this.getYaw() / 180.0F * (float) Math.PI) * Mth.cos((this.getPitch() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                    double velY = Mth.sin((this.getPitch() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                    double velZ = Mth.cos(this.getYaw() / 180.0F * (float) Math.PI) * Mth.cos((this.getPitch() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                    this.setVelocity(velX, velY, velZ);
                }

                if (this.getPos().getY() >= 1200.0F) {
                    for (Entity entity : getPassengerList()) {
                        if (entity instanceof ServerPlayer serverPlayer) {
                            ResourceLocation[] partIds = this.getPartIds();
                            serverPlayer.setCelestialScreenState(RocketData.create(this.getColor(), partIds[0], partIds[1], partIds[2], partIds[3], partIds[4], partIds[5]));
                            ServerPlayNetworking.send(serverPlayer, new ResourceLocation(Constant.MOD_ID, "planet_menu_open"), PacketByteBufs.create().writeNbt(RocketData.create(this.getColor(), partIds[0], partIds[1], partIds[2], partIds[3], partIds[4], partIds[5]).toNbt(new NbtCompound())));
                            break;
                        }
                    }
                }
            } else if (!onGround) {
                this.setSpeed(Math.max(-1.5F, this.getSpeed() - 0.05D));

                double velX = -Mth.sin(this.getYaw() / 180.0F * (float) Math.PI) * Mth.cos((this.getPitch() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                double velY = Mth.sin((this.getPitch() + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                double velZ = Mth.cos(this.getYaw() / 180.0F * (float) Math.PI) * Mth.cos((this.getPitch() + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                this.setVelocity(velX, velY, velZ);
            }

            this.move(MoverType.SELF, this.getVelocity());

            if (getStage() == LaunchStage.FAILED) {
                setRotation((this.getYaw() + level.random.nextFloat() - 0.5F * 8.0F) % 360.0F, (this.getPitch() + level.random.nextFloat() - 0.5F * 8.0F) % 360.0F);

                for (int i = 0; i < 4; i++) ((ServerLevel) level).sendParticles(ParticleTypes.FLAME, this.getX() + (level.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (level.random.nextDouble() - 0.5), 0, level.random.nextDouble() - 0.5, 1, level.random.nextDouble() - 0.5, 0.12000000596046448D);

                if (this.onGround) {
                    for (int i = 0; i < 4; i++) world.createExplosion(this, this.getPos().x + (level.random.nextDouble() - 0.5 * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                    this.remove(RemovalReason.KILLED);
                }
            }

            ticksSinceJump++;

        }
    }

//    public SimpleFixedFluidInv getTank() {
//        return this.tank;
//    }

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
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = this.getBoundingBox().getSize();
        if (Double.isNaN(d)) {
            d = 1.0D;
        }

        d *= 64.0D * 3;
        return distance < d * d;
    }

    @Override
    public double getSpeed() {
        return SynchedEntityData.get(SPEED);
    }

    @Override
    public void setSpeed(double speed) {
        this.entityData.set(SPEED, speed);
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
        this.getPassengerList().forEach(this::updatePassengerPosition);
    }

    @Override
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        super.refreshPositionAndAngles(x, y, z, yaw, pitch);
    }

    @Override
    protected void refreshPosition() {
        super.refreshPosition();
        this.getPassengerList().forEach(this::updatePassengerPosition);
    }

    @Override
    public void setOnFireFromLava() {
        super.setOnFireFromLava();
        for (int i = 0; i < 4; i++) world.createExplosion(this, this.getPos().x + ((level.random.nextDouble() - 0.5) * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + ((level.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public boolean doesRenderOnFire() {
        return this.isOnFire();
    }

    @Override
    public void setOnFireFor(int seconds) {
        super.setOnFireFor(seconds);
        if (!level.isClientSide()) {
            for (int i = 0; i < 4; i++) world.createExplosion(this, this.getPos().x + ((level.random.nextDouble() - 0.5) * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + ((level.random.nextDouble() - 0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        }
        this.remove(RemovalReason.KILLED);
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal() && timeAsState >= 30 && !state.isAir() && !world.isClient) {
            for (int i = 0; i < 4; i++) world.createExplosion(this, this.getPos().x + (level.random.nextDouble() - 0.5 * 4), this.getPos().y + (level.random.nextDouble() * 3), this.getPos().z + (level.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void move(MoverType type, Vec3 vec3d) {
        if (onGround) vec3d.multiply(1.0D, 0.0D, 1.0D);
        super.move(type, vec3d);
        this.getPassengers().forEach(this::updatePassengerPosition);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (this.getPassengers().get(0) == passenger) {
            if (getStage().ordinal() > LaunchStage.IGNITED.ordinal()) {
                this.setStage(LaunchStage.FAILED);
            } else {
                this.setStage(LaunchStage.IDLE);
            }
        }
        super.removePassenger(passenger);
    }

    private long ticksSinceJump = 0;

    @Override
    public void onJump() {
        if (!this.getPassengerList().isEmpty() && ticksSinceJump > 10) {
            if (this.getPassengerList().get(0) instanceof ServerPlayerEntity) {
                if (getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    if (!this.getTank().getInvFluid(0).isEmpty()) {
                        this.setStage(this.getStage().next());
                        if (getStage() == LaunchStage.WARNING) {
                            ((ServerPlayer) this.getPassengers().get(0)).sendSystemMessage(Component.translatable("chat.galacticraft.rocket.warning"), true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public ResourceLocation[] getPartIds() {
        return this.entityData.get(PARTS);
    }

    @Override
    public ResourceLocation getPartForType(RocketPartType type) {
        return this.getPartIds()[type.ordinal()];
    }

    @Override
    public void setPart(ResourceLocation identifier, RocketPartType rocketPartType) {
        ResourceLocation[] ids = Arrays.copyOf(this.entityData.get(PARTS), this.entityData.get(PARTS).length);
        ids[rocketPartType.ordinal()] = identifier;
        this.setParts(ids);
    }

    @Override
    public void setParts(ResourceLocation[] parts) {
        this.entityData.set(PARTS, parts);
    }

    @Override
    public LaunchStage getStage() {
        return this.entityData.get(STAGE);
    }

    @Override
    public void setStage(LaunchStage stage) {
        if (SynchedEntityData.get(STAGE) != stage) {
            this.entityData.set(STAGE, stage);
            timeAsState = 0;
        }
    }

    @Override
    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    @Override
    public void dropItems(DamageSource source, boolean exploded) {

    }

    @Override
    public void onBaseDestroyed() {
        //todo
    }
}