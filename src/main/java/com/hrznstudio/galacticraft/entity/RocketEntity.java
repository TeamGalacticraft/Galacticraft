/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.entity;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPartType;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import com.hrznstudio.galacticraft.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import com.hrznstudio.galacticraft.client.gui.screen.ingame.PlanetSelectScreen;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
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

import java.util.Collections;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntity extends Entity implements com.hrznstudio.galacticraft.api.entity.RocketEntity {
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

    private static final TrackedData<float[]> COLOR = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<float[]>() {
        @Override
        public void write(PacketByteBuf buf, float[] colour) {
            assert colour.length > 3;
            buf.writeFloat(colour[0]);
            buf.writeFloat(colour[1]);
            buf.writeFloat(colour[2]);
            buf.writeFloat(colour[3]);
        }

        @Override
        public float[] read(PacketByteBuf buf) {
            return new float[] {buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()};
        }

        @Override
        public float[] copy(float[] colour) {
            float[] copy = new float[4];
            System.arraycopy(colour, 0, copy, 0, 4);
            return copy;
        }
    });

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

    public static final TrackedData<RocketPart[]> PARTS = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<RocketPart[]>() {
        @Override
        public void write(PacketByteBuf buf, RocketPart[] speed) {
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                buf.writeBoolean(speed[i] != null);
                if (speed[i] != null) {
                    buf.writeIdentifier(AddonRegistry.ROCKET_PARTS.getId(speed[i]));
                }
            }
        }

        @Override
        public RocketPart[] read(PacketByteBuf buf) {
            RocketPart[] array = new RocketPart[RocketPartType.values().length];
            for (byte i = 0; i < RocketPartType.values().length; i++) {
                if (buf.readBoolean()) {
                    array[i] = AddonRegistry.ROCKET_PARTS.get(buf.readIdentifier());
                }
            }
            return array;
        }

        @Override
        public RocketPart[] copy(RocketPart[] buf) {
            RocketPart[] parts = new RocketPart[RocketPartType.values().length];
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

    public RocketEntity(EntityType<RocketEntity> type, World world) {
        super(type, world);
    }

    @Override
    public float[] getColor() {
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
    public int getTier() {
        int tier = 0;
        for (RocketPart part : this.getParts()) {
            if (part != null) tier = Math.min(part.getTier(), tier);
        }
        return tier;
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
                list[type.ordinal()] = AddonRegistry.ROCKET_PARTS.get(new Identifier(parts.getString(type.asString())));
            }
        }

        setParts(list);

        if (tag.contains("Color")) {
            CompoundTag color = tag.getCompound("Color");
            this.setColor(color.getFloat("red"), color.getFloat("green"), color.getFloat("blue"), color.getFloat("alpha"));
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

        color.putFloat("red", this.getColor()[0]);
        color.putFloat("green", this.getColor()[1]);
        color.putFloat("blue", this.getColor()[2]);
        color.putFloat("alpha", this.getColor()[3]);

        tag.putString("Stage", getStage().name());
        tag.putDouble("Speed", this.getSpeed());

        tag.put("Color", color);
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
        dataTracker.startTracking(COLOR, new float[]{255.0F, 255.0F, 255.0F, 255.0F});
        dataTracker.startTracking(SPEED, 0.0D);

        dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 0);
        dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);

        RocketPart[] parts = new RocketPart[RocketPartType.values().length];
        dataTracker.startTracking(PARTS, parts);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType())).writeVarInt(this.getEntityId())
                .writeUuid(this.uuid).writeDouble(getX()).writeDouble(getY()).writeDouble(getZ()).writeByte((int) (pitch / 360F * 256F)).writeByte((int) (yaw / 360F * 256F));

        CompoundTag tag = new CompoundTag();
        tag.putFloat("tier", getTier());
        tag.putFloat("red", getColor()[0]);
        tag.putFloat("green", getColor()[1]);
        tag.putFloat("blue", getColor()[2]);
        tag.putFloat("alpha", getColor()[3]);
        RocketPart part = this.getPartForType(RocketPartType.CONE);
        if (part != null) tag.putString("cone", AddonRegistry.ROCKET_PARTS.getId(part).toString());

        part = this.getPartForType(RocketPartType.BODY);
        if (part != null) tag.putString("body", AddonRegistry.ROCKET_PARTS.getId(part).toString());

        part = this.getPartForType(RocketPartType.FIN);
        if (part != null) tag.putString("fin", AddonRegistry.ROCKET_PARTS.getId(part).toString());

        part = this.getPartForType(RocketPartType.BOOSTER);
        if (part != null) tag.putString("booster", AddonRegistry.ROCKET_PARTS.getId(part).toString());

        part = this.getPartForType(RocketPartType.BOTTOM);
        if (part != null) tag.putString("bottom", AddonRegistry.ROCKET_PARTS.getId(part).toString());

        part = this.getPartForType(RocketPartType.UPGRADE);
        if (part != null) tag.putString("upgrade", AddonRegistry.ROCKET_PARTS.getId(part).toString());

        buf.writeCompoundTag(tag);

        return new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "rocket_spawn"),
                new PacketByteBuf(buf));

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
                if (this.getTank().getContents(0).isEmpty() && !debugMode) {
                    this.setStage(LaunchStage.IDLE);
                    if (this.getPassengerList().get(0) instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) this.getPassengerList().get(0)).sendMessage(new TranslatableText("chat.galacticraft-rewoven.rocket.no_fuel"), false);
                    }
                    return;
                }
                this.getTank().takeFluid(0, Fraction.of(1, 100), ActionType.PERFORM); //todo find balanced values
                if (timeAsState >= 400) {
                    this.setStage(LaunchStage.LAUNCHED);
                    if (!(new BlockPos(0, 0, 0)).equals(this.getLinkedPad())) {
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (world.getBlockState(getLinkedPad().add(x, 0, z)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                                        && world.getBlockState(getLinkedPad().add(x, 0, z)).get(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
                                    world.setBlockState(getLinkedPad().add(x, 0, z), Blocks.AIR.getDefaultState(), 4);
                                }
                            }
                        }
                    }
                    this.setSpeed(0.0D);
                }
            } else if (getStage() == LaunchStage.LAUNCHED) {
                if (!debugMode && (this.getTank().isEmpty() || !this.getTank().getContents(0).getFluid().isIn(GalacticraftTags.FUEL))) {
                    this.setStage(LaunchStage.FAILED);
                } else {
                    this.getTank().takeFluid(0, Fraction.of(1, 100), ActionType.PERFORM); //todo find balanced values
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
                            MinecraftClient.getInstance().openScreen(new PlanetSelectScreen(false, this.getTier(), true));
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

    public TankComponent getTank() {
        return UniversalComponents.TANK_COMPONENT.get(this);
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
                    if (!this.getTank().getContents(0).isEmpty()) {
                        this.setStage(this.getStage().next());
                        if (getStage() == LaunchStage.WARNING) {
                            ((ServerPlayerEntity) this.getPassengerList().get(0)).sendMessage(new TranslatableText("chat.galacticraft-rewoven.rocket.warning"), true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public RocketPart[] getParts() {
        return this.dataTracker.get(PARTS);
    }

    @Override
    public RocketPart getPartForType(RocketPartType type) {
        for (RocketPart part : this.dataTracker.get(PARTS)) {
            if (part != null) {
                if (part.getType() == type) {
                    return part;
                }
            }
        }
        return null;
    }

    @Override
    public void setPart(RocketPart part) {
        this.dataTracker.get(PARTS)[part.getType().ordinal()] = part;
    }

    @Override
    public void setParts(RocketPart[] parts) {
        this.dataTracker.set(PARTS, parts);
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
    public void setColor(float red, float green, float blue, float alpha) {
        this.dataTracker.set(COLOR, new float[] {red, green, blue, alpha});
    }

    @Override
    public void dropItems(DamageSource source, boolean exploded) {

    }

    @Override
    public void onBaseDestroyed() {
        //todo
    }
}