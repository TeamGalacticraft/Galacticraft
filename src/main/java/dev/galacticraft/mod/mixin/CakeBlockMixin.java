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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends Block {
    public CakeBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "eat", at = @At("HEAD"), cancellable = true)
    private static void galacticraft$eatCake(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Player player, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = (Level) levelAccessor;
        Vec3 playerEyePos = player.getEyePosition();

        if (player.galacticraft$hasMask()) {
            if (Galacticraft.CONFIG.cannotEatWithMask()) {
                player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_EAT_WITH_MASK).withStyle(Constant.Text.RED_STYLE), true);
                cir.setReturnValue(InteractionResult.FAIL);
            }
        } else if (Galacticraft.CONFIG.cannotEatInNoAtmosphere()
                && !level.isBreathable(playerEyePos.x, playerEyePos.y, playerEyePos.z)
        ) {
            player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_EAT_IN_NO_ATMOSPHERE).withStyle(Constant.Text.RED_STYLE), true);
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
