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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.api.block.entity.Pullable;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.content.item.StandardWrenchItem;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class FluidPipeBlock extends PipeShapedBlock<PipeBlockEntity> implements EntityBlock {
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
        if (stack.getItem() instanceof DyeItem dye) {
            PipeColor color = PipeColor.fromDye(dye.getDyeColor());
            if (this.color == color) {
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            this.setColorAndBlock(level, pos, state, color);

            ItemStack newStack = stack.copy();
            newStack.consume(1, player);
            player.setItemInHand(hand, newStack);

            return ItemInteractionResult.SUCCESS;
        } else if (stack.is(Items.WET_SPONGE) && this.color != PipeColor.CLEAR) {
            this.setColorAndBlock(level, pos, state, PipeColor.CLEAR);
            return ItemInteractionResult.SUCCESS;
        } else if (stack.getItem() instanceof StandardWrenchItem && level.getBlockEntity(pos) instanceof Pullable pullablePipe) {
            pullablePipe.setPull(!pullablePipe.isPull());
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState) {
        if (level.getBlockState(neighborPos).getBlock() instanceof FluidPipeBlock pipe && !this.color.canConnectTo(pipe.color)) {
            return false;
        } else {
            return FluidUtil.canAccessFluid(level, neighborPos, direction);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (ctx.getPlayer() == null) {
            return state;
        }

        Player player = ctx.getPlayer();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof DyeItem dye && PipeColor.fromDye(dye.getDyeColor()) != this.color) {
                ItemStack newStack = stack.copy();
                newStack.consume(1, player);
                player.setItemInHand(hand, newStack);

                return this.setColor(state, dye.getDyeColor());
            }
        }

        return state;
    }

    protected<T extends FluidPipeBlock> MapCodec<T> simpleCodec(BiFunction<BlockBehaviour.Properties, PipeColor, T> generator) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties),
                PipeColor.CODEC.fieldOf("color").forGetter(FluidPipeBlock::color)
        ).apply(instance, generator));
    }

    @Override
    protected void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos) {
        if (level.getBlockEntity(thisPos) instanceof FluidPipe pipe) {
            pipe.updateConnection(level.getBlockState(thisPos), thisPos, neighborPos, direction);
        }
    }

    // Taken from AE2's Color Applicator implementation
    public BlockState setColor(BlockState state, PipeColor color) {
        BlockState newState = this.getMatchingBlock(color).defaultBlockState();
        for (Property<?> property : newState.getProperties()) {
            newState = copyProperty(state, newState, property);
        }

        return newState;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState oldState, BlockState newState, Property<T> property) {
        return newState.setValue(property, oldState.getValue(property));
    }

    protected BlockState setColor(BlockState state, DyeColor dye) {
        return this.setColor(state, PipeColor.fromDye(dye));
    }

    protected void setColorAndBlock(Level level, BlockPos pos, BlockState state, PipeColor color) {
        BlockState newState = this.setColor(state, color);
        level.setBlockAndUpdate(pos, newState);
        for (Direction direction : Direction.values()) {
            ((FluidPipeBlock)newState.getBlock()).updateConnection(newState, pos, direction, pos.relative(direction), level);
        }
    }

    protected PipeColor color() {
        return this.color;
    }

    protected abstract Block getMatchingBlock(PipeColor color);
}
