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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BubbleEntity extends Entity {
    public BubbleEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
    }

    @Override
    protected void tickNetherPortal() {
    }

    @Override
    public void tick() {
        this.baseTick();
    }

    @Override
    public void baseTick() {
        this.extinguish();
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.setYaw(0);
        this.setPitch(0);
        this.prevPitch = 0;
        this.prevYaw = 0;

        if (this.getY() < -64.0D) {
            this.tickInVoid();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == DamageSource.OUT_OF_WORLD) {
            this.remove(RemovalReason.DISCARDED);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void tickNetherPortalCooldown() {
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    public boolean canAvoidTraps() {
        return true;
    }

    @Override
    public boolean canBeRiddenInWater() {
        return false;
    }

    @Override
    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        return false;
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void setOnFireFromLava() {
    }

    @Override
    public void setOnFireFor(int seconds) {
    }

    @Override
    public int getFireTicks() {
        return -1;
    }

    @Override
    public void setFireTicks(int ticks) {
    }

    @Override
    protected float getJumpVelocityMultiplier() {
        return 0;
    }

    @Override
    protected float getVelocityMultiplier() {
        return 0;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public void setSilent(boolean silent) {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void setNoGravity(boolean noGravity) {
    }

    @Override
    protected int getBurningDuration() {
        return 0;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean hasPassengers() {
        return false;
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        BlockEntity entity = world.getBlockEntity(getBlockPos());
        if (entity instanceof BubbleDistributorBlockEntity machine) {
            double d = Math.abs(machine.getSize() * 2D + 1D);
            if (Double.isNaN(d)) {
                d = 1.0D;
            }

            d *= 64.0D * getRenderDistanceMultiplier();
            return distance < d * d;
        }
        return false;
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(this.getId());
        buf.writeUuid(this.getUuid());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.getType()));
        buf.writeDouble(this.getX());
        buf.writeDouble(this.getY());
        buf.writeDouble(this.getZ());
        buf.writeByte((byte) ((int) (this.getYaw() * 256.0F / 360.0F)));
        buf.writeByte((byte) ((int) (this.getPitch() * 256.0F / 360.0F)));
        Vec3d vec3d = this.getVelocity();
        double e = MathHelper.clamp(vec3d.x, -3.9D, 3.9D);
        double f = MathHelper.clamp(vec3d.y, -3.9D, 3.9D);
        double g = MathHelper.clamp(vec3d.z, -3.9D, 3.9D);
        buf.writeShort((int) (e * 8000.0D));
        buf.writeShort((int) (f * 8000.0D));
        buf.writeShort((int) (g * 8000.0D));
        return new CustomPayloadS2CPacket(new Identifier(Constant.MOD_ID, "entity_spawn"), buf);
    }
}
