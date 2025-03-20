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

package dev.galacticraft.mod.api.block;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.api.block.entity.Pullable;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.content.item.StandardWrenchItem;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FluidPipeBlock extends PipeBlock implements EntityBlock {
//    public static final MapCodec<FluidPipe> CODEC = RecordCodecBuilder.mapCodec(
//            instance -> instance.group(BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties)).apply(instance, ))

    public final PipeColor color;

    public FluidPipeBlock(Properties settings, PipeColor color) {
        super(0.125f, settings.pushReaction(PushReaction.BLOCK));
        this.color = color;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && Galacticraft.CONFIG.isDebugLogEnabled() && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof FluidPipe pipe) {
                Constant.LOGGER.info("Network: {}", pipe.getNetwork());
            }
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof PipeBlockEntity pipeEntity) {
            if (stack.getItem() instanceof DyeItem dye) {
                var stack2 = stack.copy();
                var color = PipeColor.fromDye(dye.getDyeColor());
                if (this.color == color) {
                    return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                }

                if (!player.getAbilities().instabuild) {
                    stack2.shrink(1);
                }
                player.setItemInHand(hand, stack2);
                this.setColor(state, level, pos, color);

                return ItemInteractionResult.SUCCESS;
            } else if ((stack.is(Items.WATER_BUCKET) || stack.is(Items.WET_SPONGE)) && this.color != PipeColor.CLEAR) {
                if (stack.is(Items.WATER_BUCKET) && !player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
                this.setColor(state, level, pos, PipeColor.CLEAR);

                return ItemInteractionResult.SUCCESS;
            } else if (stack.getItem() instanceof StandardWrenchItem && pipeEntity instanceof Pullable pullablePipe) {
                var stack2 = stack.copy();

                stack2.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

                player.setItemInHand(hand, stack2);
                pullablePipe.setPull(!pullablePipe.isPull());
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState();

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = ctx.getClickedPos().relative(direction);
            Block neighbor = ctx.getLevel().getBlockState(neighborPos).getBlock();

            if (neighbor instanceof FluidPipeBlock pipe && !this.color.canConnectTo(pipe.color)) {
                continue;
            }

            if (FluidUtil.canAccessFluid(ctx.getLevel(), neighborPos, direction)) {
                state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }

        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);

        if (level.getBlockEntity(pos) instanceof GlassFluidPipeBlockEntity glassPipe && placer instanceof Player player) {
            var changed = false;
            for (var interactionHand : InteractionHand.values()) {
                var stack = player.getItemInHand(interactionHand);

                if (stack.getItem() instanceof DyeItem dye && PipeColor.fromDye(dye.getDyeColor()) != this.color) {
                    this.setColor(state, level, pos, dye.getDyeColor());
                    var copy = stack.copy();
                    copy.consume(1, player);

                    player.setItemInHand(interactionHand, copy);
                    changed = true;
                }
            }

            // Regular Stuff
            for (var direction : Constant.Misc.DIRECTIONS) {
                changed |= glassPipe.getConnections()[direction.ordinal()] = glassPipe.canConnect(direction) && FluidUtil.canAccessFluid(level, pos.relative(direction), direction);
            }
            if (changed) {
                glassPipe.setChanged();
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
            }
        }
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return null; // idk man
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (levelAccessor instanceof Level level) {
            BooleanProperty directionProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
            Block neighbor = neighborState.getBlock();

            if (neighbor instanceof FluidPipeBlock pipe && !this.color.canConnectTo(pipe.color)) {
                return state.setValue(directionProperty, false);
            } else if (FluidUtil.canAccessFluid(level, neighborPos, direction)) {
                return state.setValue(directionProperty, true);
            } else {
                return state.setValue(directionProperty, false);
            }
        }

        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, neighborPos, notify);

        Direction direction = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        if (direction == null)
            return;

        if (!level.isClientSide) {
            var pipe = (FluidPipe) level.getBlockEntity(pos);
            assert pipe != null;

            if (pipe.canConnect(direction)) {
                pipe.updateConnection(state, pos, neighborPos, direction);
            }
        }
    }

    // Taken from AE2's Color Applicator implementation
    public boolean setColor(BlockState state, Level level, BlockPos pos, PipeColor color) {
        if (color == this.color)
            return false;

        BlockState newState = this.getMatchingBlock(color).defaultBlockState();
        for (Property<?> property : newState.getProperties()) {
            newState = copyProperty(state, newState, property);
        }
        level.setBlockAndUpdate(pos, newState);

        return true;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState oldState, BlockState newState, Property<T> property) {
        return newState.setValue(property, oldState.getValue(property));
    }

    public boolean setColor(BlockState state, Level level, BlockPos pos, DyeColor dye) {
        return this.setColor(state, level, pos, PipeColor.fromDye(dye));
    }

    @Nullable
    @Override
    public abstract PipeBlockEntity newBlockEntity(BlockPos pos, BlockState state);

    protected abstract Block getMatchingBlock(PipeColor color);

    protected static <O, S extends StateHolder<O,S>> S applyDefaultState(S state) {
        return state
                .setValue(PipeBlock.NORTH, Boolean.FALSE)
                .setValue(PipeBlock.EAST, Boolean.FALSE)
                .setValue(PipeBlock.SOUTH, Boolean.FALSE)
                .setValue(PipeBlock.WEST, Boolean.FALSE)
                .setValue(PipeBlock.UP, Boolean.FALSE)
                .setValue(PipeBlock.DOWN, Boolean.FALSE);
    }

    protected static <O, S extends StateHolder<O,S>> void addStateDefinitions(StateDefinition.Builder<O, S> builder) {
        builder.add(PipeBlock.NORTH, PipeBlock.EAST, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.UP, PipeBlock.DOWN);
    }
}
