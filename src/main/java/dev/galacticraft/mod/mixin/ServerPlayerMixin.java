/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends LivingEntity implements ServerPlayerAccessor {
    @SuppressWarnings("ConstantConditions")
    private ServerPlayerMixin() {
        super(null, null);
    }

    @Unique
    private @Nullable RocketData galacticraft$rocketData = null;

    @Unique
    private boolean galacticraft$celestialActive = false;

    @Unique
    private boolean galacticraft$isRideTick = false;

    /**
     * Dimension found in the player's NBT when the player was loaded.
     *
     * <p>We keep this temporarily so the JOIN event can determine whether the
     * world no longer exists without trying to teleport while NBT is still being
     * deserialized.</p>
     */
    @Unique
    private @Nullable ResourceLocation galacticraft$savedLoginDimension = null;

    @Override
    public boolean galacticraft$isCelestialScreenActive() {
        return this.galacticraft$celestialActive;
    }

    @Override
    public void galacticraft$closeCelestialScreen() {
        this.galacticraft$celestialActive = false;
        this.galacticraft$rocketData = null;
    }

    @Override
    public @Nullable RocketData galacticraft$getCelestialScreenState() {
        return this.galacticraft$rocketData;
    }

    @Override
    public void galacticraft$openCelestialScreen(@Nullable RocketData data) {
        this.galacticraft$celestialActive = true;
        this.galacticraft$rocketData = data;
    }

    @Override
    public @Nullable ResourceLocation galacticraft$getSavedLoginDimension() {
        return this.galacticraft$savedLoginDimension;
    }

    @Override
    public void galacticraft$clearSavedLoginDimension() {
        this.galacticraft$savedLoginDimension = null;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeCelestialData(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("CelestialActive", this.galacticraft$celestialActive);

        if (this.galacticraft$rocketData != null) {
            CompoundTag nbt1 = new CompoundTag();
            RocketData.CODEC.encode(this.galacticraft$rocketData, NbtOps.INSTANCE, nbt1);
            nbt.put("CelestialState", nbt1);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readCelestialData(CompoundTag nbt, CallbackInfo ci) {
        this.galacticraft$celestialActive = nbt.getBoolean("CelestialActive");

        if (nbt.contains("CelestialState")) {
            this.galacticraft$rocketData = RocketData.CODEC
                    .decode(
                            NbtOps.INSTANCE,
                            nbt.getCompound("CelestialState")
                    )
                    .getOrThrow()
                    .getFirst();
        }

        /*
         * Vanilla's Entity data contains the dimension as a resource location.
         *
         * Do not teleport from inside the NBT loading process. Merely record
         * the value and let the server JOIN event perform recovery once the
         * connection/player is fully established.
         */
        if (nbt.contains("Dimension")) {
            String dimension = nbt.getString("Dimension");

            if (!dimension.isBlank()) {
                try {
                    this.galacticraft$savedLoginDimension =
                            ResourceLocation.parse(dimension);
                } catch (Exception ignored) {
                    this.galacticraft$savedLoginDimension = null;
                }
            }
        }
    }

    @Inject(method = "rideTick", at = @At("HEAD"))
    private void rideTickStart(CallbackInfo ci) {
        this.galacticraft$isRideTick = true;
    }

    @Inject(method = "rideTick", at = @At("TAIL"))
    private void rideTickEnd(CallbackInfo ci) {
        this.galacticraft$isRideTick = false;
    }

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    private void canStopRiding(CallbackInfo ci) {
        Entity vehicle = getVehicle();

        if (this.galacticraft$isRideTick
                && vehicle instanceof IgnoreShift ignoreShift
                && ignoreShift.shouldIgnoreShiftExit()) {
            ci.cancel();
        }
    }

    @Inject(method = "bedBlocked", at = @At("HEAD"), cancellable = true)
    private void checkIfCryoBedBlocked(
            BlockPos sleepingPos,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockPos basePos = sleepingPos.below();

        if (this.level()
                .getBlockState(basePos)
                .getBlock() instanceof CryogenicChamberBlock) {
            cir.setReturnValue(
                    !this.galacticraft$freeAt(basePos.relative(direction))
                            || !this.galacticraft$freeAt(sleepingPos.relative(direction))
            );
        }
    }

    @Unique
    private boolean galacticraft$freeAt(BlockPos blockPos) {
        return !this.level()
                .getBlockState(blockPos)
                .isSuffocating(this.level(), blockPos);
    }
}