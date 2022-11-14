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

package dev.galacticraft.mod.content.block.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class TinLadderBlock extends Block {
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;

    public TinLadderBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Nullable
    private InteractionResult checkCanTinLadderBePlaced(Level world, BlockPos checkPos, BlockState state) {
        if (world.getBlockState(checkPos).isAir()) {
            var newState = this.defaultBlockState().setValue(FACING, state.getValue(FACING));
            world.setBlockAndUpdate(checkPos, newState);
            var blockSoundGroup = newState.getSoundType();
            world.playSound(null, checkPos, blockSoundGroup.getPlaceSound(), SoundSource.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
            return InteractionResult.SUCCESS;
        } else if (world.getBlockState(checkPos).getBlock() != this) {
            return InteractionResult.PASS;
        }
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == this.asItem()) {
            if (player.getXRot() < 0f) {
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); world.isOutsideBuildHeight(checkPos); checkPos = checkPos.offset(0, 1, 0)) {
                    var result = this.checkCanTinLadderBePlaced(world, checkPos, state);
                    if (result != null) {
                        if (!player.isCreative() && result == InteractionResult.SUCCESS) {
                            itemStack.shrink(1);
                        }
                        return result;
                    }
                }
            } else {
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); world.isOutsideBuildHeight(checkPos); checkPos = checkPos.offset(0, -1, 0)) {
                    var result = this.checkCanTinLadderBePlaced(world, checkPos, state);
                    if (result != null) {
                        if (!player.isCreative() && result == InteractionResult.SUCCESS) {
                            itemStack.shrink(1);
                        }
                        return result;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClientSide) {
            world.scheduleTick(pos, this, 1);
        } else {
            super.onPlace(state, world, pos, oldState, notify);
        }
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> EAST_SHAPE;
        };
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, direction, newState, world, pos, posFrom);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState2;
        if (!ctx.replacingClickedOnBlock()) {
            blockState2 = ctx.getLevel().getBlockState(ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite()));
            if (blockState2.is(this) && blockState2.getValue(FACING) == ctx.getClickedFace()) {
                return null;
            }
        }
        blockState2 = this.defaultBlockState();
        var worldView = ctx.getLevel();
        var blockPos = ctx.getClickedPos();
        var fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        var placementDirs = ctx.getNearestLookingDirections();
        for (var direction : placementDirs) {
            if (direction.getAxis().isHorizontal()) {
                blockState2 = blockState2.setValue(FACING, direction.getOpposite());
                if (blockState2.canSurvive(worldView, blockPos)) {
                    return blockState2.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
                }
            }
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        EAST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
        WEST_SHAPE = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        SOUTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
        NORTH_SHAPE = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    }
}
