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

<<<<<<< HEAD
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
=======
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
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
<<<<<<< HEAD
            var newState = this.getDefaultState().with(FACING, state.get(FACING));
            world.setBlockState(checkPos, newState);
            var blockSoundGroup = newState.getSoundGroup();
            world.playSound(null, checkPos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
            return ActionResult.SUCCESS;
        } else if (world.getBlockState(checkPos).getBlock() != this) {
            return ActionResult.PASS;
=======
            BlockState newState = this.defaultBlockState().setValue(FACING, state.getValue(FACING));
            world.setBlockAndUpdate(checkPos, newState);
            return InteractionResult.SUCCESS;
        } else if (!(world.getBlockState(checkPos).getBlock() instanceof TinLadderBlock)) {
            return InteractionResult.PASS;
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
        }
        return null;
    }

    @Override
<<<<<<< HEAD
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == this.asItem()) {
            if (player.getPitch() < 0f) {
                for (BlockPos checkPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ()); world.isInBuildLimit(checkPos); checkPos = checkPos.add(0, 1, 0)) {
                    var result = this.checkCanTinLadderBePlaced(world, checkPos, state);
                    if (result != null) {
                        if (!player.isCreative() && result == ActionResult.SUCCESS) {
                            itemStack.decrement(1);
                        }
=======
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack item = player.getInventory().getItem(player.getInventory().selected);
        if (Block.byItem(item.getItem()) instanceof TinLadderBlock) {
            if (!player.isCreative())
                item.shrink(1);
            if (player.getXRot() < 0f) {
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() < world.getHeight(); checkPos = checkPos.offset(0, 1, 0)) {
                    InteractionResult result = this.checkCanTinLadderBePlaced(world, checkPos, state);
                    if (result != null)
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
                        return result;
                    }
                }
            } else {
<<<<<<< HEAD
                for (BlockPos checkPos = new BlockPos.Mutable(pos.getX(), pos.getY(), pos.getZ()); world.isInBuildLimit(checkPos); checkPos = checkPos.add(0, -1, 0)) {
                    var result = this.checkCanTinLadderBePlaced(world, checkPos, state);
                    if (result != null) {
                        if (!player.isCreative() && result == ActionResult.SUCCESS) {
                            itemStack.decrement(1);
                        }
=======
                for (BlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()); checkPos.getY() > 0; checkPos = checkPos.offset(0, -1, 0)) {
                    InteractionResult result = this.checkCanTinLadderBePlaced(world, checkPos, state);
                    if (result != null)
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
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

<<<<<<< HEAD
    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
=======
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return true;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
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
<<<<<<< HEAD
        blockState2 = this.getDefaultState();
        var worldView = ctx.getWorld();
        var blockPos = ctx.getBlockPos();
        var fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        var placementDirs = ctx.getPlacementDirections();
        for (var direction : placementDirs) {
=======
        blockState2 = this.defaultBlockState();
        LevelReader worldView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        Direction[] var6 = ctx.getNearestLookingDirections();
        for (Direction direction : var6) {
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
            if (direction.getAxis().isHorizontal()) {
                blockState2 = blockState2.setValue(FACING, direction.getOpposite());
                if (blockState2.canSurvive(worldView, blockPos)) {
                    return blockState2.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
                }
            }
        }
        return null;
    }

<<<<<<< HEAD
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
=======
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
>>>>>>> 6593609d1587824a7c3451231d3600f8bfc1240e
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
