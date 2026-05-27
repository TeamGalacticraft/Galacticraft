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

package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.LunarCheesePressPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LunarCheesePressPartBlock extends BaseEntityBlock {
    public static final MapCodec<LunarCheesePressPartBlock> CODEC = simpleCodec(LunarCheesePressPartBlock::new);
    private static final VoxelShape TOP_SLAB = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape BACK_WALL_SOUTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 14.0D, 16.0D);
    private static final VoxelShape BACK_WALL_NORTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 2.0D);
    private static final VoxelShape BACK_WALL_WEST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 14.0D, 16.0D);
    private static final VoxelShape BACK_WALL_EAST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);
    private static final VoxelShape SHAPE_NORTH = Shapes.or(TOP_SLAB, BACK_WALL_SOUTH);
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(TOP_SLAB, BACK_WALL_NORTH);
    private static final VoxelShape SHAPE_EAST = Shapes.or(TOP_SLAB, BACK_WALL_WEST);
    private static final VoxelShape SHAPE_WEST = Shapes.or(TOP_SLAB, BACK_WALL_EAST);

    public LunarCheesePressPartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LunarCheesePressBlock.FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LunarCheesePressBlock.FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(LunarCheesePressBlock.FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(GCBlocks.LUNAR_CHEESE_PRESS);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !neighborState.is(GCBlocks.LUNAR_CHEESE_PRESS)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof LunarCheesePressPartBlockEntity part && part.basePos != BlockPos.ZERO) {
            BlockPos basePos = new BlockPos(part.basePos);
            BlockState baseState = level.getBlockState(basePos);
            if (!baseState.isAir()) {
                MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
                block.onPartDestroyed(level, player, baseState, basePos, state, pos);
            }
        }
        super.destroy(level, pos, state);
        return state;
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (level.getBlockEntity(pos) instanceof LunarCheesePressPartBlockEntity part && part.basePos != BlockPos.ZERO) {
            BlockPos basePos = new BlockPos(part.basePos);
            BlockState baseState = level.getBlockState(basePos);
            if (!baseState.isAir()) {
                MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
                block.onPartExploded(level, baseState, basePos);
            }
        }
        super.wasExploded(level, pos, explosion);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof LunarCheesePressPartBlockEntity part) || part.basePos == BlockPos.ZERO) {
            return InteractionResult.PASS;
        }

        BlockPos basePos = new BlockPos(part.basePos);
        BlockState baseState = level.getBlockState(basePos);
        if (baseState.isAir()) {
            return InteractionResult.PASS;
        }

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
        return block.multiBlockUseWithoutItem(baseState, level, basePos, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LunarCheesePressPartBlockEntity(pos, state);
    }
}