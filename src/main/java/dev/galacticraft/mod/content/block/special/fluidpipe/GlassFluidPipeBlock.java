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

package dev.galacticraft.mod.content.block.special.fluidpipe;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
import dev.galacticraft.mod.content.item.StandardWrenchItem;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GlassFluidPipeBlock extends FluidPipe {
    public static final VoxelShape NORTH = box(6, 6, 0, 10, 10, 10);
    public static final VoxelShape EAST = box(6, 6, 6, 16, 10, 10);
    public static final VoxelShape SOUTH = box(6, 6, 6, 10, 10, 16);
    public static final VoxelShape WEST = box(0, 6, 6, 10, 10, 10);
    public static final VoxelShape UP = box(6, 6, 6, 10, 16, 10);
    public static final VoxelShape DOWN = box(6, 0, 6, 10, 10, 10);
    public static final VoxelShape NONE = box(6, 6, 6, 10, 10, 10);

    public GlassFluidPipeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);

        if (level.getBlockEntity(blockPos) instanceof GlassFluidPipeBlockEntity glassPipe) {
            for (var interactionHand : InteractionHand.values()) {
                var stack = livingEntity.getItemInHand(interactionHand);

                if (stack.getItem() instanceof DyeItem dye && dye.getDyeColor() != glassPipe.getColor()) {
                    glassPipe.setColor(dye.getDyeColor());
                    var copy = stack.copy();

                    if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
                        copy.shrink(1);
                    }

                    livingEntity.setItemInHand(interactionHand, copy);
                }
            }
            for (var direction : Constant.Misc.DIRECTIONS) {
                var otherBlockEntity = level.getBlockEntity(blockPos.relative(direction));
                glassPipe.getConnections()[direction.ordinal()] = (otherBlockEntity instanceof Pipe pipe && pipe.canConnect(direction.getOpposite())) || FluidUtil.canAccessFluid(level, blockPos.relative(direction), direction);
            }
            level.updateNeighborsAt(blockPos, blockState.getBlock());
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        var itemStack = player.getItemInHand(interactionHand);
        if (level.getBlockEntity(blockPos) instanceof GlassFluidPipeBlockEntity glassPipe) {
            if (itemStack.getItem() instanceof DyeItem dye) {
                var stack = itemStack.copy();
                var color = dye.getDyeColor();
                if (color != glassPipe.getColor()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    player.setItemInHand(interactionHand, stack);
                    glassPipe.setColor(color);
                    level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                else {
                    return InteractionResult.PASS;
                }
            } else if (itemStack.getItem() instanceof StandardWrenchItem) {
                var stack = itemStack.copy();

                if (!player.getAbilities().instabuild) {
                    stack.hurt(1, level.random, player instanceof ServerPlayer ? ((ServerPlayer) player) : null);
                }

                player.setItemInHand(interactionHand, stack);
                glassPipe.setPull(!glassPipe.isPull());
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, fromPos, notify);
        var neighborState = level.getBlockState(fromPos);
        var delta = fromPos.subtract(blockPos);
        var direction = Direction.fromDelta(delta.getX(), delta.getY(), delta.getZ());

        if (direction != null && level.getBlockEntity(blockPos) instanceof GlassFluidPipeBlockEntity glassPipe) {
            var otherBlockEntity = level.getBlockEntity(fromPos);
            glassPipe.getConnections()[direction.ordinal()] = !neighborState.isAir() && ((otherBlockEntity instanceof Pipe pipe && pipe.canConnect(direction.getOpposite())) || FluidUtil.canAccessFluid(level, fromPos, direction));
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof Connected connected) {
            return ConnectingBlockUtil.getVoxelShape(connected, NORTH, SOUTH, EAST, WEST, UP, DOWN, NONE);
        }
        return NONE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    @Nullable
    public PipeBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GlassFluidPipeBlockEntity(blockPos, blockState);
    }
}