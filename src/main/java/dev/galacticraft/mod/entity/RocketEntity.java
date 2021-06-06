/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import dev.galacticraft.api.entity.Rocket;
import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.rocket.part.travel.AccessType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.ServerPlayerEntityAccessor;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.tag.GalacticraftTag;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntity extends Entity implements Rocket {
    private static final TrackedData<LaunchStage> STAGE = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<LaunchStage>() {
        @Override
        public void write(PacketByteBuf buf, LaunchStage stage) {
            buf.writeEnumConstant(stage);
        }

        @Override
        public LaunchStage read(PacketByteBuf buf) {
            return buf.readEnumConstant(LaunchStage.class);
        }

        @Override
        public LaunchStage copy(LaunchStage stage) {
            return stage;
        }
    });

    private static final TrackedData<Integer> COLOR = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<Double> SPEED = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<Double>() {
        @Override
        public void write(PacketByteBuf buf, Double speed) {
            buf.writeDouble(speed);
        }

        @Override
        public Double read(PacketByteBuf buf) {
            return buf.readDouble();
        }

        @Override
        public Double copy(Double speed) {
            return speed;
        }
    });

    public static final TrackedData<Identifier[]> PARTS = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<Identifier[]>() {
        @Override
        public void write(PacketByteBuf buf, Identifier[] parts) {
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                buf.writeBoolean(parts[i] != null);
                if (parts[i] != null) {
                    buf.writeIdentifier(parts[i]);
                }
            }
        }

        @Override
        public Identifier[] read(PacketByteBuf buf) {
            Identifier[] array = new Identifier[RocketPartType.values().length];
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                if (buf.readBoolean()) {
                    array[i] = buf.readIdentifier();
                }
            }
            return array;
        }

        @Override
        public Identifier[] copy(Identifier[] buf) {
            Identifier[] parts = new Identifier[RocketPartType.values().length];
            System.arraycopy(buf, 0, parts, 0, buf.length);
            return parts;
        }
    });
    private final boolean debugMode = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    static {
        TrackedDataHandlerRegistry.register(STAGE.getType());
        TrackedDataHandlerRegistry.register(COLOR.getType());
        TrackedDataHandlerRegistry.register(SPEED.getType());
        TrackedDataHandlerRegistry.register(PARTS.getType());
    }

    private BlockPos linkedPad = new BlockPos(0, 0, 0);
    private final SimpleFixedFluidInv tank = new SimpleFixedFluidInv(1, FluidAmount.ofWhole(10));
    private final RocketPart[] parts = new RocketPart[RocketPartType.values().length];

    public RocketEntity(EntityType<RocketEntity> type, World world) {
        super(type, world);
    }

    @Override
    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    private long timeAsState = 0;

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().isEmpty();
    }

    @Override
    public BlockPos getLinkedPad() {
        return linkedPad;
    }

    @Override
    public boolean canTravelTo(CelestialBodyType celestialBodyType) {
        Object2BooleanMap<RocketPart> map = new Object2BooleanArrayMap<>();
        AccessType type = AccessType.PASS;
        for (RocketPart part : this.parts) {
            map.put(part, true);
            type = type.merge(this.travel(part, celestialBodyType, map));
        }
        return type == AccessType.ALLOW;
    }

    private AccessType travel(RocketPart part, CelestialBodyType type, Object2BooleanMap<RocketPart> map) {
        return part.getTravelPredicate().canTravelTo(type, p -> map.computeBooleanIfAbsent((RocketPart) p, p1 -> {
            if (Arrays.stream(this.parts).anyMatch(p2 -> p2.getId() == p1.getId())) {
                map.put((RocketPart) p, false);
                return travel(p1, type, map) != AccessType.BLOCK;
            } else {
                return false;
            }
        }));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && !this.removed) {
            if (this.isInvulnerableTo(source)) {
                return false;
            } else {
                this.dataTracker.set(DAMAGE_WOBBLE_SIDE, -this.dataTracker.get(DAMAGE_WOBBLE_SIDE));
                this.dataTracker.set(DAMAGE_WOBBLE_TICKS, 10);
                this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH) + amount * 10.0F);
                boolean creative = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
                if (creative || this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH) > 40.0F) {
                    this.removeAllPassengers();
                    if (creative && !this.hasCustomName()) {
                        this.remove();
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
    public void remove() {
        super.remove();
        if (this.linkedPad != null) {
            BlockEntity pad = this.world.getBlockEntity(this.linkedPad);
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
    public ActionResult interactAt(PlayerEntity player, Vec3d vec3d, Hand hand) {
        player.startRiding(this);
        return ActionResult.SUCCESS;
    }

    @Override
    public void setLinkedPad(BlockPos linkedPad) {
        this.linkedPad = linkedPad;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getPassengerList().isEmpty()) {
            player.startRiding(this);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
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
    public void readCustomDataFromTag(CompoundTag tag) {
        CompoundTag parts = tag.getCompound("Parts");
        RocketPart[] list = new RocketPart[RocketPartType.values().length];
        for (RocketPartType type : RocketPartType.values()) {
            if (parts.contains(type.asString())) {
                list[type.ordinal()] = RocketPart.getById(this.world.getRegistryManager(), new Identifier(parts.getString(type.asString())));
            }
        }

        setParts(list);

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
    public void writeCustomDataToTag(CompoundTag tag) {
        CompoundTag parts = new CompoundTag();
        CompoundTag color = new CompoundTag();

        for (RocketPart part : this.getParts()) {
            if (part != null) {
                parts.putString(part.getType().asString(), part.getId().toString());
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
    public void setCustomName(@Nullable Text text) {
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Nullable
    @Override
    public Text getCustomName() {
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
    protected void initDataTracker() {
        dataTracker.startTracking(STAGE, LaunchStage.IDLE);
        dataTracker.startTracking(COLOR, -1);
        dataTracker.startTracking(SPEED, 0.0D);

        dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 0);
        dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);

        Identifier[] parts = new Identifier[RocketPartType.values().length];
        dataTracker.startTracking(PARTS, parts);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()));
        buf.writeVarInt(this.getEntityId());
        buf.writeUuid(this.uuid);
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());
        buf.writeByte((int) (pitch / 360F * 256F));
        buf.writeByte((int) (yaw / 360F * 256F));
        buf.writeCompoundTag(new RocketData(this.getColor(), this.getParts()).toTag(this.world.getRegistryManager(), new CompoundTag()));
        return new CustomPayloadS2CPacket(new Identifier(Constant.MOD_ID, "rocket_spawn"), buf);
    }

    @Override
    public void tick() {
        this.noClip = false;
        timeAsState++;

        super.tick();

        if (!world.isClient) {
            if (this.getPassengerList().isEmpty()) {
                if (getStage() != LaunchStage.FAILED) {
                    if (getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal()) {
                        this.setStage(LaunchStage.FAILED);
                    } else {
                        this.setStage(LaunchStage.IDLE);
                    }
                }
            } else if (!(this.getPassengerList().get(0) instanceof PlayerEntity) && this.getStage() != LaunchStage.FAILED) {
                if (getStage() == LaunchStage.LAUNCHED) {
                    this.setStage(LaunchStage.FAILED);
                } else {
                    this.setStage(LaunchStage.IDLE);
                }

                this.removePassenger(this.getPassengerList().get(0));
            }

            if (isOnFire() && !world.isClient) {
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                this.remove();
            }

            if (getStage() == LaunchStage.IGNITED) {
                if (this.getTank().getInvFluid(0).isEmpty() && !debugMode) {
                    this.setStage(LaunchStage.IDLE);
                    if (this.getPassengerList().get(0) instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) this.getPassengerList().get(0)).sendMessage(new TranslatableText("chat.galacticraft.rocket.no_fuel"), false);
                    }
                    return;
                }
                this.getTank().extractFluid(0, key -> GalacticraftTag.FUEL.contains(key.getRawFluid()), FluidVolumeUtil.EMPTY, FluidAmount.of(1, 100), Simulation.ACTION); //todo find balanced values
                if (timeAsState >= 400) {
                    this.setStage(LaunchStage.LAUNCHED);
                    if (!(new BlockPos(0, 0, 0)).equals(this.getLinkedPad())) {
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (world.getBlockState(getLinkedPad().add(x, 0, z)).getBlock() == GalacticraftBlock.ROCKET_LAUNCH_PAD
                                        && world.getBlockState(getLinkedPad().add(x, 0, z)).get(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
                                    world.setBlockState(getLinkedPad().add(x, 0, z), Blocks.AIR.getDefaultState(), 4);
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
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.CLOUD, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.CLOUD, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.CLOUD, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.CLOUD, this.getX() + (world.random.nextDouble() - 0.5), this.getY(), this.getZ() + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);


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

                    double velX = -MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos((pitch + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                    double velY = MathHelper.sin((pitch + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                    double velZ = MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos((pitch + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                    this.setVelocity(velX, velY, velZ);
                }

                if (this.getPos().getY() >= 1200.0F) {
                    for (Entity entity : getPassengerList()) {
                        if (entity instanceof ServerPlayerEntity) {
                            ((ServerPlayerEntityAccessor) entity).setCelestialScreenState(new RocketData(this.getColor(), this.getParts()));
                            ServerPlayNetworking.send(((ServerPlayerEntity) entity), new Identifier(Constant.MOD_ID, "planet_menu_open"), new PacketByteBuf(Unpooled.buffer()).writeCompoundTag(new RocketData(this.getColor(), this.getParts()).toTag(this.world.getRegistryManager(), new CompoundTag())));
                            break;
                        }
                    }
                }
            } else if (!onGround) {
                this.setSpeed(Math.max(-1.5F, this.getSpeed() - 0.05D));

                double velX = -MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos((pitch + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;
                double velY = MathHelper.sin((pitch + 90.0F) / 180.0F * (float) Math.PI) * this.getSpeed();
                double velZ = MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos((pitch + 90.0F) / 180.0F * (float) Math.PI) * (this.getSpeed() * 0.632D) * 1.58227848D;

                this.setVelocity(velX, velY, velZ);
            }

            this.move(MovementType.SELF, this.getVelocity());

            if (getStage() == LaunchStage.FAILED) {
                setRotation((yaw + world.random.nextFloat() - 0.5F * 8.0F) % 360.0F, (pitch + world.random.nextFloat() - 0.5F * 8.0F) % 360.0F);

                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, this.getX() + (world.random.nextDouble() - 0.5) * 0.12F, this.getY() + 2, this.getZ() + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);


                if (this.onGround) {
                    world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                    world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                    world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                    world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                    this.remove();
                }
            }

            ticksSinceJump++;

        }
    }

    public SimpleFixedFluidInv getTank() {
        return this.tank;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.setVelocity(new Vec3d(x, y, z));
    }

    @Override
    public void setVelocity(Vec3d vec3d) {
        super.setVelocity(vec3d);
        this.velocityDirty = true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(d)) {
            d = 1.0D;
        }

        d *= 64.0D * 3;
        return distance < d * d;
    }

    @Override
    public double getSpeed() {
        return dataTracker.get(SPEED);
    }

    @Override
    public void setSpeed(double speed) {
        this.dataTracker.set(SPEED, speed);
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
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengerList()) {
                entity.handleFallDamage(fallDistance, damageMultiplier);
            }
        }
        return true;
    }

    @Override
    protected void setOnFireFromLava() {
        super.setOnFireFromLava();
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        this.remove();
    }

    @Override
    public boolean doesRenderOnFire() {
        return this.isOnFire();
    }

    @Override
    public void setOnFireFor(int seconds) {
        super.setOnFireFor(seconds);
        if (!world.isClient) {
            world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() - 0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() - 0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() - 0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() - 0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        }
        this.remove();
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if (getStage().ordinal() >= LaunchStage.LAUNCHED.ordinal() && timeAsState >= 30 && !state.isAir() && !world.isClient) {
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() - 0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            this.remove();
        }
    }


    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        this.getPassengerList().forEach(this::updatePassengerPosition);
    }

    @Override
    public void move(MovementType type, Vec3d vec3d) {
        if (onGround) vec3d.multiply(1.0D, 0.0D, 1.0D);
        super.move(type, vec3d);
        this.getPassengerList().forEach(this::updatePassengerPosition);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (this.getPassengerList().get(0) == passenger) {
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
        for (RocketPart part : this.getParts()) {
            if (part == null) return;
        }

        if (!this.getPassengerList().isEmpty() && ticksSinceJump > 10) {
            if (this.getPassengerList().get(0) instanceof ServerPlayerEntity) {
                if (getStage().ordinal() < LaunchStage.IGNITED.ordinal()) {
                    if (!this.getTank().getInvFluid(0).isEmpty()) {
                        this.setStage(this.getStage().next());
                        if (getStage() == LaunchStage.WARNING) {
                            ((ServerPlayerEntity) this.getPassengerList().get(0)).sendMessage(new TranslatableText("chat.galacticraft.rocket.warning"), true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public RocketPart[] getParts() {
        for (int i = 0; i < this.dataTracker.get(PARTS).length; i++) {
            this.parts[i] = RocketPart.getById(this.world.getRegistryManager(), this.dataTracker.get(PARTS)[i]);
        }
        return parts;
    }

    @Override
    public RocketPart getPartForType(RocketPartType type) {
        return this.getParts()[type.ordinal()];
    }

    @Override
    public void setPart(RocketPart part) {
        Identifier[] ids = Arrays.copyOf(this.dataTracker.get(PARTS), this.dataTracker.get(PARTS).length);
        ids[part.getType().ordinal()] = RocketPart.getId(this.world.getRegistryManager(), part);
        this.dataTracker.set(PARTS, ids);
    }

    @Override
    public void setParts(RocketPart[] parts) {
        Identifier[] partsi = new Identifier[6];
        for (int i = 0; i < parts.length; i++) {
            partsi[i] = RocketPart.getId(this.world.getRegistryManager(), parts[i]);
        }
        this.dataTracker.set(PARTS, partsi);
    }

    @Override
    public LaunchStage getStage() {
        return this.dataTracker.get(STAGE);
    }

    @Override
    public void setStage(LaunchStage stage) {
        if (dataTracker.get(STAGE) != stage) {
            this.dataTracker.set(STAGE, stage);
            timeAsState = 0;
        }
    }

    @Override
    public void setColor(int color) {
        this.dataTracker.set(COLOR, color);
    }

    @Override
    public void dropItems(DamageSource source, boolean exploded) {

    }

    @Override
    public void onBaseDestroyed() {
        //todo
    }
}