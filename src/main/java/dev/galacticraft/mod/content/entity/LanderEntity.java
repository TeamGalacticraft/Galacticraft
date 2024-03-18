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

package dev.galacticraft.mod.content.entity;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.particle.GCParticleTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LanderEntity extends Entity {

    protected long ticks = 0;
    private double lastDeltaY;
    protected boolean lastOnGround;

    public LanderEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

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
        this.move(MoverType.SELF, getDeltaMovement());

        if (onGround()) {
            tickOnGround();
        } else {
            tickInAir();
        }

        this.xo = getX();
        this.yo = getY();
        this.zo = getZ();

        if (this.level().isClientSide) {
            var particlePos = getParticlePosition();
            final Vec3 posVec = particlePos.getFirst();
            final Vec3 motionVec = particlePos.getSecond();
            level().addParticle(GCParticleTypes.LANDER_FLAME_PARTICLE, posVec.x(), posVec.y(), posVec.z(), motionVec.x(), motionVec.y(), motionVec.z());
        }

        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(getId());
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());
        buf.writeFloat(getYRot());
        buf.writeFloat(getXRot());
        Vec3 deltaMovement = getDeltaMovement();
        buf.writeDouble(deltaMovement.x());
        buf.writeDouble(deltaMovement.y());
        buf.writeDouble(deltaMovement.z());
        ClientPlayNetworking.send(Constant.Packet.ENTITY_UPDATE, buf);

        if (onGround() && !this.lastOnGround) {
            this.onGroundHit();
        }

        this.lastOnGround = onGround();
        this.lastDeltaY = getDeltaMovement().y();
    }

    public void onGroundHit() {
        if (!level().isClientSide) {
            if (Math.abs(this.lastDeltaY) > 2.0D) {
                for (Entity entity : this.getPassengers()) {
                    entity.removeVehicle();
                    if (entity instanceof ServerPlayer) {
//                        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_RESET_THIRD_PERSON, GCCoreUtil.getDimensionID(this.world), new Object[]
//                                {}), (EntityPlayerMP) entity);
                    }
                    entity.setDeltaMovement(Vec3.ZERO);
                    entity.setPos(entity.getX(), this.getY() + 2.25, entity.getZ());
//                    this.world.updateEntityWithOptionalForce(entity, false);
                }
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 12, true, Level.ExplosionInteraction.MOB);

                discard();
            }
        }
    }

    public void tickOnGround() {
        setXRot(0.0000001F);
    }

    public void tickInAir() {
        if (this.level().isClientSide()) {
            if (!this.onGround()) {
                this.addDeltaMovement(new Vec3(0, CelestialBody.getByDimension(level()).map(CelestialBody::gravity).orElse(1F) * -0.008D, 0));
            }

            double motY = -1 * Math.sin(getXRot() / Constant.RADIANS_TO_DEGREES);
            double motX = Math.cos(getYRot() / Constant.RADIANS_TO_DEGREES) * motY;
            double motZ = Math.sin(getYRot() / Constant.RADIANS_TO_DEGREES) * motY;

            setDeltaMovement(new Vec3(motX / 2.0F, getDeltaMovement().y(), motZ / 2.0F));
        }
    }

    @Override
    public Vec3 getDeltaMovement() {
        if (this.level().isClientSide()) {
            if (this.onGround()) {
                return Vec3.ZERO;
            }

            if (this.ticks >= 40 && this.ticks < 45) {
                setDeltaMovement(0, -2.5D, 0);
            }

            Vec3 oldMotion = super.getDeltaMovement();
            return new Vec3(oldMotion.x(), this.ticks < 40 ? 0 : oldMotion.y(), oldMotion.z());
        }
        return super.getDeltaMovement();
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
}
