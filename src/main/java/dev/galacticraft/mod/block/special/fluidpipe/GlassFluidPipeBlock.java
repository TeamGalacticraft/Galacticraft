/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block.special.fluidpipe;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.entity.GalacticraftBlockEntities;
import dev.galacticraft.mod.items.StandardWrenchItem;
import dev.galacticraft.mod.util.ConnectingBlockUtils;
import dev.galacticraft.mod.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GlassFluidPipeBlock extends FluidPipe {

    private static final VoxelShape NORTH = createCuboidShape(8 - 2, 8 - 2, 0, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape EAST = createCuboidShape(8 - 2, 8 - 2, 8 - 2, 16, 8 + 2, 8 + 2);
    private static final VoxelShape SOUTH = createCuboidShape(8 - 2, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 16);
    private static final VoxelShape WEST = createCuboidShape(0, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape UP = createCuboidShape(8 - 2, 8 - 2, 8 - 2, 8 + 2, 16, 8 + 2);
    private static final VoxelShape DOWN = createCuboidShape(8 - 2, 0, 8 - 2, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape NONE = createCuboidShape(8 - 2, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 8 + 2);

    private static final BooleanProperty PULL = BooleanProperty.of("pull"); //todo pull state (what would that mean for conf. sides that are different?)
    private static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

    public GlassFluidPipeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(PULL, false).with(COLOR, DyeColor.WHITE).with(ConnectingBlockUtils.ATTACHED_NORTH, false).with(ConnectingBlockUtils.ATTACHED_EAST, false).with(ConnectingBlockUtils.ATTACHED_SOUTH, false).with(ConnectingBlockUtils.ATTACHED_WEST, false).with(ConnectingBlockUtils.ATTACHED_UP, false).with(ConnectingBlockUtils.ATTACHED_DOWN, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        BlockPos pos = context.getBlockPos().toImmutable();
        for (Direction direction : Constants.Misc.DIRECTIONS) {
            Block block = context.getWorld().getBlockState(pos.offset(direction)).getBlock();
            if (block instanceof FluidPipe || FluidUtils.isExtractableOrInsertable(context.getWorld(), pos.offset(direction), direction)) state = state.with(propFromDirection(direction), true);
        }
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).isEmpty()) {
            if (player.getStackInHand(hand).getItem() instanceof DyeItem) {
                ItemStack stack = player.getStackInHand(hand).copy();
                DyeColor color = ((DyeItem) stack.getItem()).getColor();
                if (color != state.get(COLOR)) {
                    stack.decrement(1);
                    player.setStackInHand(hand, stack);
                    world.setBlockState(pos, state.with(COLOR, color));
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
            if (player.getStackInHand(hand).getItem() instanceof StandardWrenchItem) {
                ItemStack stack = player.getStackInHand(hand).copy();
                stack.damage(1, world.random, player instanceof ServerPlayerEntity ? ((ServerPlayerEntity) player) : null);
                player.setStackInHand(hand, stack);
                world.setBlockState(pos, state.with(PULL, !state.get(PULL)));
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos updatedPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, updatedPos, notify);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        return ConnectingBlockUtils.getVoxelShape(blockState, NORTH, SOUTH, EAST, WEST, UP, DOWN, NONE);
    }

    private BooleanProperty getPropForDirection(Direction dir) {
        return ConnectingBlockUtils.getBooleanProperty(dir);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState other, WorldAccess world, BlockPos thisWire, BlockPos otherConnectable) {
        return state.with(getPropForDirection(direction), (
                !other.isAir()
                        && ((other.getBlock() instanceof FluidPipe && other.get(COLOR) == state.get(COLOR))
                        || FluidUtils.isExtractableOrInsertable(world.getBlockEntity(thisWire).getWorld(), otherConnectable, direction.getOpposite())
                )
        ));
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return 1.0F;
    }

    private BooleanProperty propFromDirection(Direction direction) {
        return ConnectingBlockUtils.getBooleanProperty(direction);
    }

    @Override
    public @Nullable FluidPipeBlockEntity createBlockEntity(BlockView world) {
        return new FluidPipeBlockEntity(GalacticraftBlockEntities.GLASS_FLUID_PIPE_TYPE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PULL, COLOR, ConnectingBlockUtils.ATTACHED_NORTH, ConnectingBlockUtils.ATTACHED_EAST, ConnectingBlockUtils.ATTACHED_SOUTH, ConnectingBlockUtils.ATTACHED_WEST, ConnectingBlockUtils.ATTACHED_UP, ConnectingBlockUtils.ATTACHED_DOWN);
    }
}
