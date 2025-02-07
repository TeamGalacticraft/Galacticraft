/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionTransition.class)
public abstract class DimensionTransitionMixin {
    @Inject(method = "missingRespawnBlock", at = @At(value = "HEAD"), cancellable = true)
    private static void findRespawnAndUseCryoChamber(ServerLevel overworld, Entity entity, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        if (entity instanceof ServerPlayer serverPlayer) {
            BlockPos blockPos = serverPlayer.getRespawnPosition();
            float yaw = serverPlayer.getRespawnAngle();
            ServerLevel serverLevel = serverPlayer.server.getLevel(serverPlayer.getRespawnDimension());
            if (serverLevel != null && blockPos != null) {
                if (gc$canRespawn(serverLevel, blockPos)) {
                    cir.setReturnValue(new DimensionTransition(serverLevel, blockPos.getBottomCenter(), Vec3.ZERO, yaw, 0.0f, postDimensionTransition));
                }
            }
        }
    }

    @Unique
    private static boolean gc$canRespawn(Level level, BlockPos blockPos) {
        BlockState baseState = level.getBlockState(blockPos);
        if (baseState.getBlock() instanceof CryogenicChamberBlock) {
            Direction direction = baseState.getValue(CryogenicChamberBlock.FACING);
            return gc$freeAt(level, blockPos.relative(direction)) && gc$freeAt(level, blockPos.above().relative(direction));
        }
        return false;
    }

    @Unique
    private static boolean gc$freeAt(Level level, BlockPos blockPos) {
        return !level.getBlockState(blockPos).isSuffocating(level, blockPos);
    }
}