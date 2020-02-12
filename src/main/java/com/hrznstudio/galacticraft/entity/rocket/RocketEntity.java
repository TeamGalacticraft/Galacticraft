/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.entity.rocket;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.LaunchStage;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.tag.GalacticraftFluidTags;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
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
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketEntity extends Entity implements FluidInsertable { //pitch+90

    private static final TrackedData<LaunchStage> STAGE = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<LaunchStage>() {
        @Override
        public void write(PacketByteBuf var1, LaunchStage var2) {
            var1.writeEnumConstant(var2);
        }

        @Override
        public LaunchStage read(PacketByteBuf var1) {
            return var1.readEnumConstant(LaunchStage.class);
        }

        @Override
        public LaunchStage copy(LaunchStage var1) {
            return var1;
        }
    });

    private static final TrackedData<SimpleFixedFluidInv> FUEL_INV = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<SimpleFixedFluidInv>() {
        @Override
        public void write(PacketByteBuf var1, SimpleFixedFluidInv var2) {
            var1.writeCompoundTag(var2.toTag());
        }

        @Override
        public SimpleFixedFluidInv read(PacketByteBuf var1) {
            SimpleFixedFluidInv fluidInv = new SimpleFixedFluidInv(1, 10000);
            fluidInv.fromTag(var1.readCompoundTag());
            return fluidInv;
        }

        @Override
        public SimpleFixedFluidInv copy(SimpleFixedFluidInv var1) {
            SimpleFixedFluidInv fluidInv = new SimpleFixedFluidInv(1, 10000);
            fluidInv.fromTag(var1.toTag());
            return fluidInv;
        }
    });
    
    private static final TrackedData<Float[]> COLOR = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<Float[]>() {

    @Override
    public void write(PacketByteBuf var1, Float[] var2) {
        assert var2.length > 3;
        var1.writeFloat(var2[0]);
        var1.writeFloat(var2[1]);
        var1.writeFloat(var2[2]);
        var1.writeFloat(var2[3]);
    }

    @Override
    public Float[] read(PacketByteBuf var1) {
        return new Float[] {var1.readFloat(), var1.readFloat(), var1.readFloat(), var1.readFloat()};
    }

    @Override
    public Float[] copy(Float[] var1) {
        return var1;
    }
});

    public static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(RocketEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final TrackedData<Double> SPEED = DataTracker.registerData(RocketEntity.class, new TrackedDataHandler<Double>() {
        @Override
        public void write(PacketByteBuf var1, Double var2) {
            var1.writeDouble(var2);
        }

        @Override
        public Double read(PacketByteBuf var1) {
            return var1.readDouble();
        }

        @Override
        public Double copy(Double var1) {
            return var1;
        }
    });

    static {
        TrackedDataHandlerRegistry.register(STAGE.getType());
        TrackedDataHandlerRegistry.register(COLOR.getType());
        TrackedDataHandlerRegistry.register(SPEED.getType());
        TrackedDataHandlerRegistry.register(FUEL_INV.getType());
    }

    public final Map<RocketPartType, RocketPart> parts = new HashMap<>();
    
    public RocketEntity(EntityType<RocketEntity> type, World world_1) {
        super(type, world_1);
        for (RocketPartType type1 : RocketPartType.values()) {
            parts.put(type1, Galacticraft.ROCKET_PARTS.get(new Identifier(Constants.MOD_ID, "default_" + type1.asString())));
        }
    }

    public Float[] getColor() {
        return this.dataTracker.get(COLOR);
    }

    @Override
    protected boolean canAddPassenger(Entity entity_1) {
        return this.getPassengerList().isEmpty();
    }

    public boolean damage(DamageSource damageSource_1, float float_1) {
        if (!this.world.isClient && !this.removed) {
            if (this.isInvulnerableTo(damageSource_1)) {
                return false;
            } else {
                this.dataTracker.set(DAMAGE_WOBBLE_SIDE, -this.dataTracker.get(DAMAGE_WOBBLE_SIDE));
                this.dataTracker.set(DAMAGE_WOBBLE_TICKS, 10);
                this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH) + float_1 * 10.0F);
                boolean boolean_1 = damageSource_1.getAttacker() instanceof PlayerEntity && ((PlayerEntity)damageSource_1.getAttacker()).abilities.creativeMode;
                if (boolean_1 || this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH) > 40.0F) {
                    this.removeAllPassengers();
                    if (boolean_1 && !this.hasCustomName()) {
                        this.remove();
                    } else {
                        this.dropItems(damageSource_1, false);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    protected boolean canStartRiding(Entity entity_1) {
        return super.canStartRiding(entity_1) && entity_1 instanceof PlayerEntity;
    }

    @Override
    public void updateTrackedPosition(double double_1, double double_2, double double_3) {
        super.updateTrackedPosition(double_1, double_2, double_3);
    }

    @Override
    public boolean canUsePortals() {
        return true;
    }

    @Override
    public ActionResult interactAt(PlayerEntity playerEntity_1, Vec3d vec3d_1, Hand hand_1) {
        playerEntity_1.startRiding(this);
        return ActionResult.SUCCESS;
    }

    @Override
    public void updatePassengerPosition(Entity entity_1) {
        if (this.hasPassenger(entity_1)) {
            entity_1.setPosition(this.x, this.y + this.getMountedHeightOffset() + entity_1.getHeightOffset() - 2.5, this.z);
        }
    }

    @Override
    public boolean interact(PlayerEntity playerEntity_1, Hand hand_1) {
        playerEntity_1.startRiding(this);
        return true;
    }

    @Override
    public boolean collides() { //Required to interact with the entity
        return true;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.getFuel().fromTag(tag);

        CompoundTag parts = tag.getCompound("Parts");
        HashMap<RocketPartType, RocketPart> temp = new HashMap<>();
        for (RocketPartType type : RocketPartType.values()) {
            if (Galacticraft.ROCKET_PARTS.get(new Identifier(parts.getString(type.asString()))) == null) {
                temp.put(type, Galacticraft.ROCKET_PARTS.get(new Identifier(Constants.MOD_ID, "default_" + type.asString())));
            } else {
                temp.put(type, Galacticraft.ROCKET_PARTS.get(new Identifier(parts.getString(type.asString()))));
            }
        }
        this.parts.clear();
        this.parts.putAll(temp);

        if (tag.containsKey("Color")) {
            CompoundTag color = tag.getCompound("Color");
            this.setColor(color.getFloat("red"), color.getFloat("green"), color.getFloat("blue"), color.getFloat("alpha")); 
        }

        if (tag.containsKey("Stage")) {
            this.dataTracker.set(STAGE, LaunchStage.valueOf(tag.getString("Stage")));
        }

        if (tag.containsKey("Speed")) {
            this.dataTracker.set(SPEED, tag.getDouble("Speed"));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        this.getFuel().toTag(tag);

        CompoundTag parts = new CompoundTag();
        CompoundTag color = new CompoundTag();

        for (Map.Entry<RocketPartType, RocketPart> entry : this.parts.entrySet()) {
            RocketPart rocketPart = entry.getValue();
            rocketPart.toTag(parts);
        }

        color.putFloat("red", this.getColor()[0]);
        color.putFloat("green", this.getColor()[1]);
        color.putFloat("blue", this.getColor()[2]);
        color.putFloat("alpha", this.getColor()[3]);

        tag.putString("Stage", getStage().name());
        tag.putDouble("Speed", this.dataTracker.get(SPEED));

        tag.put("Color", color);
        tag.put("Parts", parts);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        ByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()))
                .writeVarInt(this.getEntityId()).writeUuid(this.uuid).writeDouble(x).writeDouble(y).writeDouble(z).writeByte((int) (pitch / 360F * 256F)))
                .writeByte((int) (yaw / 360F * 256F));
        for (RocketPartType type : RocketPartType.values()) { //in order
            buf.writeInt(Galacticraft.ROCKET_PARTS.getRawId(parts.get(type)));
        }
        buf.writeFloat(this.getColor()[0]);
        buf.writeFloat(this.getColor()[1]);
        buf.writeFloat(this.getColor()[2]);
        buf.writeFloat(this.getColor()[3]);
        return new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "rocket_spawn"),
                new PacketByteBuf(buf));

    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void setCustomName(@Nullable Text text_1) {

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
    public void setCustomNameVisible(boolean boolean_1) {

    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return () -> Collections.singleton(ItemStack.EMPTY).iterator();
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(STAGE, LaunchStage.IDLE);
        dataTracker.startTracking(COLOR, new Float[] {1.0F, 1.0F, 1.0F, 0.0F});
        dataTracker.startTracking(SPEED, 0.0D);
        dataTracker.startTracking(FUEL_INV, new SimpleFixedFluidInv(1, 10000));

        dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 0);
        dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
    }

    private int timeAsState = 0;

    private long ticks = 0;
    @Override
    public void tick() {
        timeAsState++;

        super.tick();

        if (this.getPassengerList().isEmpty()) {
            if (getStage() != LaunchStage.FAILED) {
                if (getStage().level >= LaunchStage.LAUNCHED.level) {
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

        if (isOnFire()) {
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            this.remove();
        }

        //this.getFuel().setInvFluid(0, FluidVolume.create(GalacticraftFluids.FUEL, 10000), Simulation.ACTION); //TODO REMOVE

        if (getStage() == LaunchStage.IGNITED) {
            if (this.getFuel().getTank(0).get().getAmount() == 0) {
                this.setStage(LaunchStage.IDLE);
                super.tick();
                return;
            }
            this.getFuel().extract((FluidKey fluidKey) -> fluidKey.getRawFluid().matches(GalacticraftFluidTags.FUEL), 1);
            if (timeAsState >= 400) {
                this.setStage(LaunchStage.LAUNCHED);
                this.dataTracker.set(SPEED, 0.0D);
            }
        } else if (getStage() == LaunchStage.LAUNCHED) {
            if (this.getFuel().getTank(0).get().getAmount() == 0 || !this.getFuel().getTank(0).get().getRawFluid().matches(GalacticraftFluidTags.FUEL)) {
                this.setStage(LaunchStage.FAILED);
            } else {
                this.getFuel().getTank(0).extract(1);
                if (world instanceof ServerWorld) { //TODO decide how many particles are needed/wanted... This might be very taxing on performance
                    ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.CLOUD, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.CLOUD, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.CLOUD, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                    ((ServerWorld) world).spawnParticles (ParticleTypes.CLOUD, x + (world.random.nextDouble() - 0.5), y, z + (world.random.nextDouble() - 0.5), 0, (world.random.nextDouble() - 0.5), -1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                }


                this.dataTracker.set(SPEED, Math.min(0.5, this.dataTracker.get(SPEED) + 0.1D));

                // Pitch: -45.0
                // Yaw: 0.0
                //
                // X vel: 0.0
                // Y vel: 0.3535533845424652
                // Z vel: 0.223445739030838
                // = 1.58227848
                //
                // I hope this is right

                double velX = -MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos((pitch + 90.0F) / 180.0F * (float)Math.PI) * (this.dataTracker.get(SPEED) * 0.632D) * 1.58227848;
                double velY = MathHelper.sin((pitch + 90.0F) / 180.0F * (float)Math.PI) * this.dataTracker.get(SPEED);
                double velZ = MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos((pitch + 90.0F) / 180.0F * (float)Math.PI) * (this.dataTracker.get(SPEED) * 0.632D) * 1.58227848;

                /*if (ticks++ % 100 == 0) { //TODO remove before committing
                    System.out.println("Thread: " + Thread.currentThread());
                    System.out.println("Yaw: " + yaw);
                    System.out.println("Pitch: " + pitch);
                    System.out.println("X Velocity: " + velX);
                    System.out.println("Y Velocity: " + velY);
                    System.out.println("Z Velocity: " + velZ);
                    System.out.println("----------------");
                }*/


                this.setVelocity(velX, velY, velZ);

                this.velocityDirty = true;
            }
        } else if (!onGround) {
            this.dataTracker.set(SPEED, Math.min(-0.5, this.dataTracker.get(SPEED) - 0.1D));

            this.setVelocity(0, this.dataTracker.get(SPEED), 0);
            this.velocityDirty = true;
        }

        if (getStage() == LaunchStage.FAILED) {
            setRotation((yaw + world.random.nextFloat() - 0.5F * 8.0F) % 360.0F, (pitch + world.random.nextFloat() - 0.5F * 8.0F) % 360.0F);

            if (world instanceof ServerWorld) {
                ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5)* 0.12F, y + 2, z + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5)* 0.12F, y + 2, z + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5)* 0.12F, y + 2, z + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
                ((ServerWorld) world).spawnParticles (ParticleTypes.FLAME, x + (world.random.nextDouble() - 0.5)* 0.12F, y + 2, z + (world.random.nextDouble() - 0.5), 0, world.random.nextDouble() - 0.5, 1, world.random.nextDouble() - 0.5, 0.12000000596046448D);
            }

            if (this.onGround) {
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                world.createExplosion(this, this.getPos().x + (world.random.nextDouble() -0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
                this.remove();
            }
        }

        ticksSinceJump++;

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public void setVelocity(Vec3d vec3d_1) {
        super.setVelocity(vec3d_1);
        if (this.world.isClient && vec3d_1 != getVelocity()) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_velocity_update"), new PacketByteBuf(Unpooled.buffer().writeDouble(getVelocity().x).writeDouble(getVelocity().y).writeDouble(getVelocity().z))));
        }
    }

    @Override
    protected void setRotation(float float_1, float float_2) {
        super.setRotation(float_1, float_2);
        if (world.isClient) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_pitch_update"), new PacketByteBuf(Unpooled.buffer().writeByte((int) (pitch / 360F * 256F)))));
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "rocket_yaw_update"), new PacketByteBuf(Unpooled.buffer().writeByte((int) (yaw / 360F * 256F)))));
        }
    }

    @Override
    public void handleFallDamage(float float_1, float float_2) {
        int int_1 = MathHelper.ceil(float_1 - 1.0F);
        if (int_1 > 0) {
            List<Entity> list_1 = Lists.newArrayList(this.world.getEntities(this, this.getBoundingBox()));
            DamageSource damageSource_1 = DamageSource.FALLING_BLOCK; //TODO

            for (Entity entity_1 : list_1) {
                entity_1.damage(damageSource_1, Math.min(MathHelper.floor((float) int_1 * 9.0F), 19.0F));
            }
        }
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
    public void setOnFireFor(int int_1) {
        super.setOnFireFor(int_1);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        world.createExplosion(this, this.getPos().x + ((world.random.nextDouble() - 0.5) * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + ((world.random.nextDouble() -0.5) * 4), 10.0F, Explosion.DestructionType.DESTROY);
        this.remove();
    }

    @Override
    protected void onBlockCollision(BlockState blockState_1) {
        if (getStage().level >= LaunchStage.LAUNCHED.level && timeAsState >= 30 && !(blockState_1.getBlock() instanceof AirBlock)) {
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            world.createExplosion(this, this.getPos().x + (world.random.nextDouble() - 0.5 * 4), this.getPos().y + (world.random.nextDouble() * 3), this.getPos().z + (world.random.nextDouble() -0.5 * 4), 10.0F, Explosion.DestructionType.DESTROY);
            this.remove();
        }
    }

    @Override
    public void setPosition(double double_1, double double_2, double double_3) {
        super.setPosition(double_1, double_2, double_3);
        getPassengerList().forEach(this::updatePassengerPosition);
    }

    @Override
    public void move(MovementType movementType_1, Vec3d vec3d_1) {
        super.move(movementType_1, vec3d_1);
        getPassengerList().forEach(this::updatePassengerPosition);
    }

    @Override
    protected void removePassenger(Entity entity_1) {
        if (this.getPassengerList().get(0) == entity_1) {
            if (getStage().level > LaunchStage.IGNITED.level) {
                this.setStage(LaunchStage.FAILED);
            } else {
                this.setStage(LaunchStage.IDLE);
            }
        }
        super.removePassenger(entity_1);
    }

    @Override
    public void removeAllPassengers() {
        super.removeAllPassengers();
    }

    public SimpleFixedFluidInv getFuel() {
        return this.dataTracker.get(FUEL_INV);
    }

    @Override
    public FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
        return this.getFuel().attemptInsertion(fluid, simulation);
    }

    private long ticksSinceJump = 0;

    public void jump() {
        if (!this.getPassengerList().isEmpty() && ticksSinceJump > 10) {
            if (this.getPassengerList().get(0) instanceof ServerPlayerEntity) {
                if (getStage().level < LaunchStage.IGNITED.level) {
                    this.setStage(this.getStage().next());
                    if (getStage() == LaunchStage.WARNING) {
                        ((ServerPlayerEntity) this.getPassengerList().get(0)).sendChatMessage(new TranslatableText("chat.galacticraft-rewoven.rocket_warning_1"), MessageType.SYSTEM);
                    }
                }
            }
        }
    }
    
    public LaunchStage getStage() {
        return this.dataTracker.get(STAGE);
    }
    
    public void setStage(LaunchStage stage) {
        if (dataTracker.get(STAGE) != stage) {
            this.dataTracker.set(STAGE, stage);
            timeAsState = 0;
        }
    }
    
    public void setColor(float red, float green, float blue, float alpha) {
        this.dataTracker.set(COLOR, new Float[] {red, green, blue, alpha});
    }

    public void dropItems (DamageSource source, boolean exploded) {

    }
}