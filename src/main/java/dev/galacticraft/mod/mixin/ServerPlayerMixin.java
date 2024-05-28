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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.accessor.ServerPlayerAccessor;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntityMixin implements ServerPlayerAccessor {

    private @Unique @Nullable RocketData rocketData = null;
    private @Unique boolean celestialActive = false;
    private @Unique boolean isRideTick = false;

    @Override
    public boolean galacticraft$isCelestialScreenActive() {
        return this.celestialActive;
    }

    public ServerPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void galacticraft$closeCelestialScreen() {
        this.celestialActive = false;
        this.rocketData = null;
    }

    @Override
    public @Nullable RocketData galacticraft$getCelestialScreenState() {
        return this.rocketData;
    }

    @Override
    public void galacticraft$openCelestialScreen(@Nullable RocketData data) {
        this.celestialActive = true;
        this.rocketData = data;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeCelestialData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("CelestialActive", this.celestialActive);
        if (this.rocketData != null) {
            CompoundTag nbt1 = new CompoundTag();
            this.rocketData.toNbt(nbt1);
            nbt.put("CelestialState", nbt1);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readCelestialData(CompoundTag nbt, CallbackInfo ci) {
        this.celestialActive = nbt.getBoolean("CelestialActive");
        if (nbt.contains("CelestialState")) {
            this.rocketData = RocketData.fromNbt(nbt.getCompound("CelestialState"));
        }
    }

    @Inject(method = "rideTick", at = @At("HEAD"))
    private void rideTickStart(CallbackInfo ci) {
        this.isRideTick = true;
    }

    @Inject(method = "rideTick", at = @At("TAIL"))
    private void rideTickEnd(CallbackInfo ci) {
        this.isRideTick = false;
    }

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    private void canStopRiding(CallbackInfo ci) {
        Entity vehicle = getVehicle();
        if (isRideTick && vehicle instanceof IgnoreShift ignoreShift && ignoreShift.shouldIgnoreShiftExit())
            ci.cancel();
    }

    @Inject(method = "bedBlocked", at = @At(value = "HEAD"), cancellable = true)
    private void checkIfCryoBedBlocked(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir){
        if(this.level().getBlockState(blockPos).getBlock() instanceof CryogenicChamberBlock){
            BlockPos blockPos2 = blockPos.relative(direction);

            cir.setReturnValue(!this.gc$freeAt(blockPos2) || !this.gc$freeAt(blockPos2.above()));
        }
    }

    @Unique
    private boolean gc$freeAt(BlockPos blockPos){
        return !this.level().getBlockState(blockPos).isSuffocating(this.level(), blockPos);
    }
}
