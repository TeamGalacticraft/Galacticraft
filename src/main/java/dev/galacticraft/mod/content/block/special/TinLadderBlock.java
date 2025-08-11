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

package dev.galacticraft.mod.content.block.special;

import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TinLadderBlock extends LadderBlock {
    public TinLadderBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    private ItemInteractionResult checkCanTinLadderBePlaced(Level level, BlockPos checkPos, Player player, ItemStack itemStack, BlockState blockState) {
        if (level.getBlockState(checkPos).canBeReplaced()) {
            var newState = this.defaultBlockState().setValue(FACING, blockState.getValue(FACING));
            level.setBlockAndUpdate(checkPos, newState);
            level.playSound(null, checkPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (blockState.getSoundType().getVolume() + 1.0F) / 2.0F, blockState.getSoundType().getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, checkPos, GameEvent.Context.of(player, newState));

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return ItemInteractionResult.SUCCESS;
        } else if (!level.getBlockState(checkPos).is(this)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(GCBlocks.TIN_LADDER.asItem())) { // checks stack in hand is a tin ladder.
            if (player.getXRot() < 0f) { // if looking above horizontal
                // find a pos directly above the existing ladder (this) but below world height where the ladder can be placed.
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() < level.getMaxBuildHeight(); checkPos = checkPos.offset(0, 1, 0)) {
                    var result = this.checkCanTinLadderBePlaced(level, checkPos, player, stack, state);
                    if (result != null) {
                        return result;
                    }
                }
            } else {
                // find a pos directly below the existing ladder (this) but above world bottom where the ladder can be placed.
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() > level.getMinBuildHeight(); checkPos = checkPos.offset(0, -1, 0)) {
                    var result = this.checkCanTinLadderBePlaced(level, checkPos, player, stack, state);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    // need to override this method because LAddBlock is only interested in the block behind the ladder, which is useful functionality
    // be we want to pay attention to blocks above and below the ladders as well.
    @Override
    protected BlockState updateShape(BlockState state, Direction axisDirection, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if(!isSupported(state, level, pos, axisDirection.getOpposite()))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, axisDirection, neighborState, level, pos, neighborPos);
    }

    // this method never gets called due to the way
    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos)
    {
        return true; // return isSupported(blockState, level, blockPos, blockState.getValue(FACING));
    }


    // this method looks to see if the ladder is supported by a solid block, of if it is attached to a ladder that is supported.
    // Using can occlude to determine if a block is solid as isSolid is deprecated.
    public boolean isSupported(BlockState blockState, LevelReader level, BlockPos blockPos, Direction direction)
    {
        // if the current block we are looking at is solid then ladder is either supported underneath or above -> true
        if(blockState.canOcclude()) return true;

        // if out of bounds, then not supported -> false
        if(!insideBounds(blockPos, level)) return false;

        // if this isn't a tin ladder block and we know it isn't solid, then not supported.
        // change this to instanceof LadderBlock if you want wooden (and other ladders) to be considered support.
        if(!blockState.is(GCBlocks.TIN_LADDER)) return false;

        // get the block behind the ladder - if it can occlude (is solid) then return true;
        BlockState supportingState = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite()));
        if(supportingState.canOcclude()) return true;

        // if we are moving in a particlar vertical direction then continue looking in that direction.
        if(direction.getAxis() == Direction.Axis.Y)
            return isSupported(level.getBlockState(blockPos.relative(direction)), level, blockPos.relative(direction), direction);

        // if was horizontal direction, then need to look up and down to ensure any ladders there
        return isSupported(level.getBlockState(blockPos.relative(Direction.UP)), level, blockPos.above(), Direction.UP)
                || isSupported(level.getBlockState(blockPos.relative(Direction.DOWN)), level, blockPos.below(), Direction.DOWN);
    }

    // returns true if the pos is inside the world/level boundaries.
    public boolean insideBounds(BlockPos pos, LevelReader level)
    {
        WorldBorder border = level.getWorldBorder();
        return pos.getY() >= level.getMinBuildHeight() && pos.getY() <= level.getMaxBuildHeight()
                && pos.getX() >= border.getMinX() && pos.getX() <= border.getMaxX()
                && pos.getZ() >= border.getMinZ() && pos.getZ() <= border.getMaxZ();

    }
}