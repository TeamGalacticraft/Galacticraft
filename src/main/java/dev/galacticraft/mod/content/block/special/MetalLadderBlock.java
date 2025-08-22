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

import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MetalLadderBlock extends LadderBlock {
    public MetalLadderBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Nullable
    private ItemInteractionResult checkCanMetalLadderBePlaced(Level level, BlockPos checkPos, Player player, ItemStack itemStack, BlockState blockState, Block ladderBlock) {
        BlockState checkState = level.getBlockState(checkPos);

        // Added check to ensure ladders are all facing the same way.
        if (checkState.getBlock() instanceof MetalLadderBlock && blockState.getBlock() instanceof MetalLadderBlock && checkState.getValue(FACING) != blockState.getValue(FACING)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        // Can only place into a fluid if its water.
        if (checkState.canBeReplaced()) {
e            boolean waterLogged = checkState.is(Blocks.WATER)
                    && (checkState.getFluidState().getAmount() >= 8 || checkState.getFluidState().isSource())
                    || (checkState.hasProperty(WATERLOGGED) && checkState.getValue(WATERLOGGED));
            var newState = ladderBlock.defaultBlockState().setValue(FACING, blockState.getValue(FACING)).setValue(WATERLOGGED, waterLogged);
            level.setBlockAndUpdate(checkPos, newState);
            level.playSound(null, checkPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (blockState.getSoundType().getVolume() + 1.0F) / 2.0F, blockState.getSoundType().getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, checkPos, GameEvent.Context.of(player, newState));

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return ItemInteractionResult.SUCCESS;
        } else if (!checkState.is(this)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(GCItemTags.METAL_LADDER_BLOCKS)) { // Checks stack in hand is a metal ladder.
            Block ladderBlock = ((BlockItem)(stack.getItem())).getBlock();
            if (player.getXRot() < 0f) { // If looking above horizontal plane.
                // Find a pos directly above the existing ladder (this) but below world height where the ladder can be placed.
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() < level.getMaxBuildHeight(); checkPos = checkPos.offset(0, 1, 0)) {
                    var result = this.checkCanMetalLadderBePlaced(level, checkPos, player, stack, state, ladderBlock);
                    if (result != null) {
                        return result;
                    }
                }
            } else {
                // Find a pos directly below the existing ladder (this) but above world bottom where the ladder can be placed.
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() > level.getMinBuildHeight(); checkPos = checkPos.offset(0, -1, 0)) {
                    var result = this.checkCanMetalLadderBePlaced(level, checkPos, player, stack, state, ladderBlock);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    /// Need to override this method because Ladder Block is only interested in the block behind the ladder, which is useful
    /// functionality, however, we want to pay attention to blocks above and below the ladders as well. This is handled
    /// in the isSupported method.
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!isSupported(state, level, pos, direction.getOpposite(), state.getValue(FACING))) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /// Checks to see if a ladder could survive in the suggested location. Called when a nearby block is broken or
    /// and sometimes when a new ladder is being placed.
    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        Direction facingDirection = blockState.getValue(FACING);

        // Look at the block behind, above and below and check the appropriate face is sturdy or a ladder
        for (Direction direction : Arrays.asList(facingDirection.getOpposite(), Direction.UP, Direction.DOWN)) {
            BlockPos checkPos = blockPos.relative(direction);
            BlockState checkState = level.getBlockState(checkPos);

            // If we are looking at the blocks above or below
            if (direction == Direction.UP || direction == Direction.DOWN) {
                // If the face is sturdy
                if (checkState.isFaceSturdy(level, checkPos, direction.getOpposite())) return true;
                // If it's a metal ladder in facing the same way
                if (checkState.getBlock() instanceof MetalLadderBlock && checkState.getValue(FACING) == facingDirection) return true;
            } else { // It's on the same level - not above or below.
                // If the face is sturdy and it isn't a metal ladder. This prevents the situation where a metal ladder can be placed
                // back to back with another metal ladder and they can end up supporting themselves floating in the air.
                if (checkState.isFaceSturdy(level, checkPos, direction.getOpposite()) && !(checkState.getBlock() instanceof MetalLadderBlock)) return true;
            }
        }
        return false;
    }

    /// This method looks to see if the ladder is supported by a solid block, or if it's attached to a ladder that is supported.
    /// Using isFaceSturdy to determine if a block is solid as isSolid is deprecated and canOcclude doesn't give the desired result.
    /// All ladders are not considered sturdy.
    /// It is recursive, so we have various termination scenarios before any recursion.
    public boolean isSupported(BlockState blockState, LevelReader level, BlockPos blockPos, Direction direction, Direction facingDirection) {
        // If the current block we are looking has the appropriate sturdy face, then the ladder is either supported underneath or above -> true
        if ((blockState.isFaceSturdy(level, blockPos, Direction.UP) && (direction == Direction.DOWN))
                || (blockState.isFaceSturdy(level, blockPos, Direction.DOWN) && (direction == Direction.UP))) return true;

        // If this isn't a metal ladder block, and we know it isn't solid, then not supported.
        // Change this to instanceof LadderBlock if you want wooden (and other ladders) to be considered support.
        if (!(blockState.getBlock() instanceof MetalLadderBlock)) return false;

        // Check to ensure that the ladder is facing in the same direction.
        if (blockState.getValue(FACING) != facingDirection) return false;

        // Get the block behind the ladder - if the appropriate face is sturdy and it's not a ladder then return true;
        BlockState supportingState = level.getBlockState(blockPos.relative(blockState.getValue(FACING).getOpposite()));
        if (supportingState.isFaceSturdy(level, blockPos, facingDirection) && !(supportingState.getBlock() instanceof LadderBlock)) return true;

        // If we are moving in a particular vertical direction, then continue looking in that direction.
        if (direction.getAxis() == Direction.Axis.Y) {
            return isSupported(level.getBlockState(blockPos.relative(direction)), level, blockPos.relative(direction), direction, facingDirection);
        }

        // If was horizontal direction, then need to look up and down to find supported ladders there.
        return isSupported(level.getBlockState(blockPos.relative(Direction.UP)), level, blockPos.above(), Direction.UP, facingDirection)
                || isSupported(level.getBlockState(blockPos.relative(Direction.DOWN)), level, blockPos.below(), Direction.DOWN, facingDirection);
    }
}