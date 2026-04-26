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

package dev.galacticraft.mod.content.block.decoration;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MeteoricIronDoorTopBlock extends Block {
    public static final MapCodec<MeteoricIronDoorTopBlock> CODEC = simpleCodec(properties -> new MeteoricIronDoorTopBlock(properties, Constant.id(Constant.Block.METEORIC_IRON_DOOR)));
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;

    private static final float AABB_DOOR_THICKNESS = 3.0F;
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, AABB_DOOR_THICKNESS);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 16.0D - AABB_DOOR_THICKNESS, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(16.0D - AABB_DOOR_THICKNESS, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, AABB_DOOR_THICKNESS, 16.0D, 16.0D);

    private final ResourceLocation baseDoorId;

    public MeteoricIronDoorTopBlock(BlockBehaviour.Properties properties, ResourceLocation baseDoorId) {
        super(properties);
        this.baseDoorId = baseDoorId;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HINGE, DoorHingeSide.LEFT));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HINGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        boolean open = state.getValue(OPEN);
        boolean rightHinge = state.getValue(HINGE) == DoorHingeSide.RIGHT;
        return switch (direction) {
            case EAST -> open ? (rightHinge ? SOUTH_AABB : NORTH_AABB) : EAST_AABB;
            case SOUTH -> open ? (rightHinge ? WEST_AABB : EAST_AABB) : SOUTH_AABB;
            case WEST -> open ? (rightHinge ? NORTH_AABB : SOUTH_AABB) : WEST_AABB;
            default -> open ? (rightHinge ? EAST_AABB : WEST_AABB) : NORTH_AABB;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return this.isBaseDoorUpper(neighborState)
                    ? state.setValue(FACING, neighborState.getValue(DoorBlock.FACING))
                            .setValue(OPEN, neighborState.getValue(DoorBlock.OPEN))
                            .setValue(HINGE, neighborState.getValue(DoorBlock.HINGE))
                    : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.isBaseDoorUpper(level.getBlockState(pos.below()));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos upperDoorPos = pos.below();
        BlockState upperDoorState = level.getBlockState(upperDoorPos);
        if (upperDoorState.getBlock() instanceof MeteoricIronDoorBlock doorBlock) {
            return doorBlock.useTopPart(upperDoorState, level, upperDoorPos, player, hitResult);
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.getBaseDoor());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos lowerDoorPos = pos.below(2);
            if (level.getBlockState(lowerDoorPos).is(this.getBaseDoor())) {
                level.destroyBlock(lowerDoorPos, !player.isCreative());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return switch (type) {
            case LAND, AIR -> state.getValue(OPEN);
            default -> false;
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
    }

    private boolean isBaseDoorUpper(BlockState state) {
        return state.is(this.getBaseDoor()) && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
    }

    private Block getBaseDoor() {
        return BuiltInRegistries.BLOCK.get(this.baseDoorId);
    }
}