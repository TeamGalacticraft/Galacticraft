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

package dev.galacticraft.mod.content.block.special.fluidpipe;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
import dev.galacticraft.mod.content.item.StandardWrenchItem;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
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
            var changed = false;
            for (var interactionHand : InteractionHand.values()) {
                var stack = livingEntity.getItemInHand(interactionHand);

                if (stack.getItem() instanceof DyeItem dye && glassPipe.dyeCanBeApplied(dye.getDyeColor())) {
                    glassPipe.setColor(dye.getDyeColor());
                    var copy = stack.copy();
                    copy.consume(1, livingEntity);

                    livingEntity.setItemInHand(interactionHand, copy);
                    changed = true;
                }
            }

            // Regular Stuff
            for (var direction : Constant.Misc.DIRECTIONS) {
                changed |= glassPipe.getConnections()[direction.ordinal()] = glassPipe.canConnect(direction) && FluidUtil.canAccessFluid(level, blockPos.relative(direction), direction);
            }
            if (changed) {
                glassPipe.setChanged();
                level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
            }
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof GlassFluidPipeBlockEntity glassPipe) {
            if (stack.getItem() instanceof DyeItem dye) {
                var stack2 = stack.copy();
                var color = dye.getDyeColor();
                if (glassPipe.dyeCanBeApplied(color)) {
                    if (!player.getAbilities().instabuild) {
                        stack2.shrink(1);
                    }
                    player.setItemInHand(hand, stack2);
                    glassPipe.setColor(color);
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
                    return ItemInteractionResult.SUCCESS;
                } else {
                    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                }
            } else if ((stack.is(Items.WATER_BUCKET) || stack.is(Items.WET_SPONGE)) && glassPipe.getColor() != PipeColor.CLEAR) {
                if (stack.is(Items.WATER_BUCKET) && !player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
                glassPipe.setColor(PipeColor.CLEAR);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
                return ItemInteractionResult.SUCCESS;
            } else if (stack.getItem() instanceof StandardWrenchItem) {
                var stack2 = stack.copy();

                stack2.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                player.setItemInHand(hand, stack2);
                glassPipe.setPull(!glassPipe.isPull());
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, fromPos, notify);

        if (level.getBlockEntity(blockPos) instanceof PipeBlockEntity glassPipe) {
            var direction = DirectionUtil.fromNormal(fromPos.getX() - blockPos.getX(), fromPos.getY() - blockPos.getY(), fromPos.getZ() - blockPos.getZ());

            if (direction != null) {
                if (!level.isClientSide
                        && glassPipe.getConnections()[direction.ordinal()]
                        != (glassPipe.getConnections()[direction.ordinal()]
                        = glassPipe.canConnect(direction) && FluidUtil.canAccessFluid(level, fromPos, direction))
                ) {
                    level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                    glassPipe.setChanged();
                }
            }
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