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

import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BubbleEntity extends Entity {
    public BubbleEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void handleNetherPortal() {
    }

    @Override
    public void tick() {
        this.baseTick();
    }

    @Override
    public void baseTick() {
        this.clearFire();
        if (this.isPassenger()) {
            this.stopRiding();
        }
        this.setYRot(0);
        this.setXRot(0);
        this.xRotO = 0;
        this.yRotO = 0;

        if (this.getY() < this.level().dimensionType().minY()) {
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (damageSources().fellOutOfWorld() == source) {
            this.remove(RemovalReason.DISCARDED);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void processPortalCooldown() {
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean dismountsUnderwater() {
        return true;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void lavaHurt() {
    }

    @Override
    public void setSecondsOnFire(int seconds) {
    }

    @Override
    public int getRemainingFireTicks() {
        return -1;
    }

    @Override
    public void setRemainingFireTicks(int ticks) {
    }

    @Override
    protected float getBlockJumpFactor() {
        return 0;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return 0;
    }

    @Override
    protected void onInsideBlock(BlockState state) {
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public void setSilent(boolean silent) {
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void setNoGravity(boolean noGravity) {
    }

    @Override
    protected int getFireImmuneTicks() {
        return 0;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        BlockEntity entity = level().getBlockEntity(blockPosition());
        if (entity instanceof OxygenBubbleDistributorBlockEntity machine) {
            double d = Math.abs(machine.getSize() * 2D + 1D);
            if (Double.isNaN(d)) {
                d = 1.0D;
            }

            d *= 64.0D * getViewScale();
            return distance < d * d;
        }
        return false;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
