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

package dev.galacticraft.mod.content.block.special;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TinLadderBlock extends LadderBlock {
    public TinLadderBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    private InteractionResult checkCanTinLadderBePlaced(Level level, BlockPos checkPos, Player player, ItemStack itemStack, BlockState blockState) {
        if (level.getBlockState(checkPos).canBeReplaced()) {
            var newState = this.defaultBlockState().setValue(FACING, blockState.getValue(FACING));
            level.setBlockAndUpdate(checkPos, newState);
            level.playSound(null, checkPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (blockState.getSoundType().getVolume() + 1.0F) / 2.0F, blockState.getSoundType().getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, checkPos, GameEvent.Context.of(player, newState));

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (!level.getBlockState(checkPos).is(this)) {
            return InteractionResult.PASS;
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        var itemStack = player.getItemInHand(interactionHand);
        if (itemStack.is(this.asItem())) {
            if (player.getXRot() < 0f) {
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()); checkPos.getY() < level.getMaxBuildHeight(); checkPos = checkPos.offset(0, 1, 0))
                {
                    var result = this.checkCanTinLadderBePlaced(level, checkPos, player, itemStack, blockState);
                    if (result != null) {
                        return result;
                    }
                }
            }
            else {
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()); checkPos.getY() > level.getMinBuildHeight(); checkPos = checkPos.offset(0, -1, 0))
                {
                    var result = this.checkCanTinLadderBePlaced(level, checkPos, player, itemStack, blockState);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        return true;
    }
}