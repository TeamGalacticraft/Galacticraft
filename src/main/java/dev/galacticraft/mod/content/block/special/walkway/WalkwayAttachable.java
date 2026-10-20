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

package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.block.entity.WalkwayHolder;
import dev.galacticraft.mod.content.GCBlocks;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A pipe or wire that a walkway can be attached to. The side of the walkway is stored in its block entity, see {@link WalkwayHolder}.
 * <ul>
 *     <li>Using a walkway on it attaches the walkway to the side that was clicked.</li>
 *     <li>Using it on a standalone walkway puts it in the walkway, see {@link WalkwayBlock}.</li>
 *     <li>Breaking it only removes the part that is aimed at: the walkway, or the pipe or wire, which leaves a standalone walkway.</li>
 * </ul>
 */
public interface WalkwayAttachable {
    /**
     * How far the walkway platform reaches from its side of the block.
     */
    double PLATFORM_DEPTH = 3.0 / 16.0;

    static void registerEvents() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (state.getBlock() instanceof WalkwayAttachable && blockEntity instanceof WalkwayHolder holder && holder.hasWalkway()) {
                HitResult hit = player.pick(player.blockInteractionRange(), 1.0f, false);
                // Without a hit on this block, remove the walkway first
                Vec3 location = hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(pos) ? hit.getLocation() : null;
                breakPart(level, player, pos, state, holder, location);
                return false;
            }
            return true;
        });
    }

    /**
     * {@return the shape of a pipe or wire, including its walkway if it has one}
     */
    static VoxelShape getShape(PipeShapedBlock<?> block, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof Connected connected) {
            if (connected instanceof WalkwayHolder holder && holder.getWalkway() != null) {
                return AbstractWalkwayBlock.getShape(connected, holder.getWalkway(), block.getRadius());
            }
            return block.shapes[PipeShapedBlock.generateAABBIndex(connected)];
        }
        return block.shapes[0];
    }

    /**
     * Attaches a walkway to the clicked side of a pipe or wire when the given stack is a walkway.
     * {@return the result of the interaction, or {@code null} if no walkway was attached}
     */
    static @Nullable ItemInteractionResult tryAttachWalkway(ItemStack stack, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!stack.is(GCBlocks.WALKWAY.asItem()) || !(level.getBlockEntity(pos) instanceof WalkwayHolder holder) || holder.hasWalkway()) {
            return null;
        }

        if (!level.isClientSide()) {
            holder.setWalkway(hit.getDirection());
            SoundType sound = GCBlocks.WALKWAY.defaultBlockState().getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0f) / 2.0f, sound.getPitch() * 0.8f);
            stack.consume(1, player);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    /**
     * {@return whether the given position, relative to the block, is on the walkway platform on the given side}
     */
    static boolean isOnPlatform(Direction walkway, Vec3 local) {
        double coordinate = local.get(walkway.getAxis());
        return walkway.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? coordinate >= 1.0 - PLATFORM_DEPTH - 1.0E-4
                : coordinate <= PLATFORM_DEPTH + 1.0E-4;
    }

    /**
     * {@return a standalone walkway on the given side, holding the fluid of the given state}
     */
    static BlockState standaloneWalkway(BlockState state, Direction walkway) {
        return copyFluid(state, GCBlocks.WALKWAY.defaultBlockState().setValue(BlockStateProperties.FACING, walkway));
    }

    /**
     * {@return the target state with the fluid of the source state}
     */
    static BlockState copyFluid(BlockState source, BlockState target) {
        return target.setValue(FluidLoggable.FLUID, source.getValue(FluidLoggable.FLUID))
                .setValue(FlowingFluid.LEVEL, source.getValue(FlowingFluid.LEVEL))
                .setValue(FlowingFluid.FALLING, source.getValue(FlowingFluid.FALLING));
    }

    /**
     * Breaks the part of a pipe or wire with a walkway that the player aims at.
     * The walkway is removed when the given location is on it (or {@code null}), otherwise the pipe or wire is replaced by a standalone walkway.
     */
    static void breakPart(Level level, Player player, BlockPos pos, BlockState state, WalkwayHolder holder, @Nullable Vec3 location) {
        Direction walkway = holder.getWalkway();
        assert walkway != null;
        BlockState walkwayState = standaloneWalkway(state, walkway);

        if (location == null || isOnPlatform(walkway, location.subtract(Vec3.atLowerCornerOf(pos)))) {
            destroyPart(level, player, pos, walkwayState, null);
            holder.setWalkway(null);
        } else {
            destroyPart(level, player, pos, state, (BlockEntity) holder);
            level.setBlockAndUpdate(pos, walkwayState);
            walkwayState.getBlock().setPlacedBy(level, pos, walkwayState, player, ItemStack.EMPTY);
        }
    }

    /**
     * Shows the breaking of the given part and drops it, like the player broke a block of that state.
     */
    private static void destroyPart(Level level, Player player, BlockPos pos, BlockState part, @Nullable BlockEntity blockEntity) {
        level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(part));
        if (player.isCreative()) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        ItemStack toolCopy = tool.copy();
        boolean canHarvest = player.hasCorrectToolForDrops(part);
        tool.mineBlock(level, part, pos, player);
        if (canHarvest) {
            part.getBlock().playerDestroy(level, player, pos, part, blockEntity, toolCopy);
        }
    }
}
