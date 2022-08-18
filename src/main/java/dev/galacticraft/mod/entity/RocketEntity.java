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
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.entity.data.GalacticraftTrackedDataHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RocketEntity extends Entity implements Rocket {
    private static final EntityDataAccessor<LaunchStage> STAGE = SynchedEntityData.defineId(RocketEntity.class, GalacticraftTrackedDataHandler.LAUNCH_STAGE);

    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);

    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_TICKS = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> DAMAGE_WOBBLE_SIDE = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> DAMAGE_WOBBLE_STRENGTH = SynchedEntityData.defineId(RocketEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<Double> SPEED = SynchedEntityData.defineId(RocketEntity.class, GalacticraftTrackedDataHandler.DOUBLE);

    public static final EntityDataAccessor<ResourceLocation[]> PARTS = SynchedEntityData.defineId(RocketEntity.class, GalacticraftTrackedDataHandler.ROCKET_PART_IDS);
    private final boolean debugMode = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    private BlockPos linkedPad = BlockPos.ZERO;

    public RocketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public void setColor(int i) {

    }

    @Override
    public LaunchStage getStage() {
        return null;
    }

    @Override
    public void setStage(LaunchStage launchStage) {

    }

    @Override
    public ResourceLocation[] getPartIds() {
        return new ResourceLocation[0];
    }

    @Override
    public @NotNull BlockPos getLinkedPad() {
        return null;
    }

    @Override
    public void setLinkedPad(@NotNull BlockPos blockPos) {

    }

    @Override
    public boolean canTravelTo(CelestialBody<?, ?> celestialBody) {
        return false;
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(double v) {

    }

    @Override
    public void onJump() {

    }

    @Override
    public void onBaseDestroyed() {

    }

    @Override
    public void dropItems(DamageSource damageSource, boolean b) {

    }

    @Override
    public void setPart(ResourceLocation resourceLocation, RocketPartType rocketPartType) {

    }

    @Override
    public void setParts(ResourceLocation[] resourceLocations) {

    }

    @Override
    public ResourceLocation getPartForType(RocketPartType rocketPartType) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STAGE, LaunchStage.IDLE);
        this.entityData.define(COLOR, -1);
        this.entityData.define(SPEED, 0.0D);

        this.entityData.define(DAMAGE_WOBBLE_TICKS, 0);
        this.entityData.define(DAMAGE_WOBBLE_SIDE, 0);
        this.entityData.define(DAMAGE_WOBBLE_STRENGTH, 0.0F);

        ResourceLocation[] parts = new ResourceLocation[RocketPartType.values().length];
        for (RocketPartType value : RocketPartType.values()) {
//            parts[value.ordinal()] = GalacticraftRocketParts.getDefaultPartIdForType(value);
        }
        this.entityData.define(PARTS, parts);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        CompoundTag parts = tag.getCompound("Parts");
        ResourceLocation[] array = new ResourceLocation[RocketPartType.values().length];
        for (RocketPartType type : RocketPartType.values()) {
            if (parts.contains(type.getSerializedName())) {
                array[type.ordinal()] = new ResourceLocation(parts.getString(type.getSerializedName()));
            } else {
//                array[type.ordinal()] = GalacticraftRocketParts.getDefaultPartIdForType(type);
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
        buf.writeNbt(RocketData.create(this.getColor(), parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]).toNbt(new CompoundTag()));
        return ServerPlayNetworking.createS2CPacket(Constant.id("rocket_spawn"), buf);
    }
}
