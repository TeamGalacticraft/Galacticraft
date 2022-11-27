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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import dev.galacticraft.mod.accessor.ServerPlayerAccessor;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntityMixin implements ServerPlayerAccessor, LivingEntityAccessor {

    private @Unique @Nullable RocketData rocketData = null;

    public ServerPlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public RocketData getCelestialScreenState() {
        return this.rocketData;
    }

    @Override
    public void setCelestialScreenState(RocketData data) {
        this.rocketData = data;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void writeCustomDataToNbt_gc(CompoundTag nbt, CallbackInfo ci) {
        if (this.rocketData != null) nbt.put("CelestialState", this.rocketData.toNbt(new CompoundTag()));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readCustomDataFromNbt_gc(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("CelestialState")) {
            this.rocketData = RocketData.fromNbt(nbt.getCompound("CelestialState"));
        }
    }

    @Inject(method = "bedBlocked", at = @At(value = "HEAD"), cancellable = true)
    private void checkIfCryoBedBlocked(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir){
        if(this.level.getBlockState(blockPos).getBlock() instanceof CryogenicChamberBlock){
            BlockPos blockPos2 = blockPos.relative(direction);

            cir.setReturnValue(!this.gc$freeAt(blockPos2) || !this.gc$freeAt(blockPos2.above()));
        }
    }

    @Unique
    private boolean gc$freeAt(BlockPos blockPos){
        return !this.level.getBlockState(blockPos).isSuffocating(this.level, blockPos);
    }
}
