/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.special.fluidpipe;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.FluidPipe;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.StandardWrenchItem;
import com.hrznstudio.galacticraft.util.ConnectingBlockUtils;
import com.hrznstudio.galacticraft.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GlassFluidPipeBlock extends FluidPipe {

    private static final VoxelShape NORTH = box(8 - 2, 8 - 2, 0, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape EAST = box(8 - 2, 8 - 2, 8 - 2, 16, 8 + 2, 8 + 2);
    private static final VoxelShape SOUTH = box(8 - 2, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 16);
    private static final VoxelShape WEST = box(0, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape UP = box(8 - 2, 8 - 2, 8 - 2, 8 + 2, 16, 8 + 2);
    private static final VoxelShape DOWN = box(8 - 2, 0, 8 - 2, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape NONE = box(8 - 2, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 8 + 2);

    private static final BooleanProperty PULL = BooleanProperty.create("pull"); //todo pull state (what would that mean for conf. sides that are different?)
    private static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public GlassFluidPipeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PULL, false).setValue(COLOR, DyeColor.WHITE).setValue(ConnectingBlockUtils.ATTACHED_NORTH, false).setValue(ConnectingBlockUtils.ATTACHED_EAST, false).setValue(ConnectingBlockUtils.ATTACHED_SOUTH, false).setValue(ConnectingBlockUtils.ATTACHED_WEST, false).setValue(ConnectingBlockUtils.ATTACHED_UP, false).setValue(ConnectingBlockUtils.ATTACHED_DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        BlockPos pos = context.getClickedPos().immutable();
        for (Direction direction : Constants.Misc.DIRECTIONS) {
            Block block = context.getLevel().getBlockState(pos.relative(direction)).getBlock();
            if (block instanceof FluidPipe || FluidUtils.isExtractableOrInsertable(context.getLevel(), pos.relative(direction), direction)) state = state.setValue(propFromDirection(direction), true);
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty()) {
            if (player.getItemInHand(hand).getItem() instanceof DyeItem) {
                ItemStack stack = player.getItemInHand(hand).copy();
                DyeColor color = ((DyeItem) stack.getItem()).getDyeColor();
                if (color != state.getValue(COLOR)) {
                    stack.shrink(1);
                    player.setItemInHand(hand, stack);
                    world.setBlockAndUpdate(pos, state.setValue(COLOR, color));
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
            if (player.getItemInHand(hand).getItem() instanceof StandardWrenchItem) {
                ItemStack stack = player.getItemInHand(hand).copy();
                stack.hurt(1, world.random, player instanceof ServerPlayer ? ((ServerPlayer) player) : null);
                player.setItemInHand(hand, stack);
                world.setBlockAndUpdate(pos, state.setValue(PULL, !state.getValue(PULL)));
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos updatedPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, updatedPos, notify);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        return ConnectingBlockUtils.getVoxelShape(blockState, NORTH, SOUTH, EAST, WEST, UP, DOWN, NONE);
    }

    private BooleanProperty getPropForDirection(Direction dir) {
        return ConnectingBlockUtils.getBooleanProperty(dir);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState other, LevelAccessor world, BlockPos thisWire, BlockPos otherConnectable) {
        return state.setValue(getPropForDirection(direction), (
                !other.isAir()
                        && ((other.getBlock() instanceof FluidPipe && other.getValue(COLOR) == state.getValue(COLOR))
                        || FluidUtils.isExtractableOrInsertable(world.getBlockEntity(thisWire).getLevel(), otherConnectable, direction.getOpposite())
                )
        ));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    private BooleanProperty propFromDirection(Direction direction) {
        return ConnectingBlockUtils.getBooleanProperty(direction);
    }

    @Override
    public @Nullable FluidPipeBlockEntity newBlockEntity(BlockGetter world) {
        return new FluidPipeBlockEntity(GalacticraftBlockEntities.GLASS_FLUID_PIPE_TYPE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PULL, COLOR, ConnectingBlockUtils.ATTACHED_NORTH, ConnectingBlockUtils.ATTACHED_EAST, ConnectingBlockUtils.ATTACHED_SOUTH, ConnectingBlockUtils.ATTACHED_WEST, ConnectingBlockUtils.ATTACHED_UP, ConnectingBlockUtils.ATTACHED_DOWN);
    }
}
