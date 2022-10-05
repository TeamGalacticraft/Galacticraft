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

package dev.galacticraft.mod.block.special;

import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.api.block.MultiBlockPart;
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.block.entity.CryogenicChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class CryogenicChamberBlock extends BaseEntityBlock implements MultiBlockBase {
    protected static final List<BlockPos> PARTS = List.of(new BlockPos(0, 1, 0), new BlockPos(0, 2, 0));
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CryogenicChamberBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Unmodifiable List<BlockPos> getOtherParts(BlockState state) {
        return PARTS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    public void onMultiBlockPlaced(Level world, BlockPos pos, BlockState state) {
        for (BlockPos otherPart : this.getOtherParts(state)) {
            otherPart = otherPart.immutable().offset(pos);
            world.setBlockAndUpdate(otherPart, GCBlocks.CRYOGENIC_CHAMBER_PART.defaultBlockState().setValue(FACING, state.getValue(FACING)));

            BlockEntity part = world.getBlockEntity(otherPart);
            assert part != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPart) part).setBasePos(pos);
            part.setChanged();
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        this.onMultiBlockPlaced(world, pos, state);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(world, pos, state, player);
        for (BlockPos otherPart : this.getOtherParts(state)) {
            otherPart = otherPart.immutable().offset(pos);
            world.setBlock(otherPart, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        for (BlockPos otherPart : this.getOtherParts(state)) {
            otherPart = otherPart.immutable().offset(pos);
            if (!world.getBlockState(otherPart).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return super.canSurvive(state, world, pos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CryogenicChamberBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return onMultiBlockUse(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public InteractionResult onMultiBlockUse(BlockState blockState, Level level, BlockPos basePos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide())
            return InteractionResult.CONSUME;

        ((LivingEntityAccessor) player).startCryogenicSleep(basePos);

        return InteractionResult.PASS;
    }
}
