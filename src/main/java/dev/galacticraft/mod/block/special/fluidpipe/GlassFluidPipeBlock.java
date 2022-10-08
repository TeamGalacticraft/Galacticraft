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

package dev.galacticraft.mod.block.special.fluidpipe;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.block.entity.GlassFluidPipeBlockEntity;
import dev.galacticraft.mod.item.StandardWrenchItem;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GlassFluidPipeBlock extends FluidPipe {
    private static final VoxelShape NORTH = box(8 - 2, 8 - 2, 0, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape EAST = box(8 - 2, 8 - 2, 8 - 2, 16, 8 + 2, 8 + 2);
    private static final VoxelShape SOUTH = box(8 - 2, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 16);
    private static final VoxelShape WEST = box(0, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape UP = box(8 - 2, 8 - 2, 8 - 2, 8 + 2, 16, 8 + 2);
    private static final VoxelShape DOWN = box(8 - 2, 0, 8 - 2, 8 + 2, 8 + 2, 8 + 2);
    private static final VoxelShape NONE = box(8 - 2, 8 - 2, 8 - 2, 8 + 2, 8 + 2, 8 + 2);

    public GlassFluidPipeBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        final GlassFluidPipeBlockEntity blockEntity = (GlassFluidPipeBlockEntity) world.getBlockEntity(pos);
        assert blockEntity != null;
        for (InteractionHand hand : InteractionHand.values()) {
            final ItemStack stack = placer.getItemInHand(hand);
            if (stack.getItem() instanceof DyeItem dye && dye.getDyeColor() != blockEntity.getColor()) {
                blockEntity.setColor(dye.getDyeColor());
                final ItemStack copy = stack.copy();
                copy.shrink(1);
                placer.setItemInHand(hand, copy);
            }
        }
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            final BlockEntity otherBlockEntity = world.getBlockEntity(pos.relative(direction));
            blockEntity.getConnections()[direction.ordinal()] = (otherBlockEntity instanceof Pipe pipe && pipe.canConnect(direction.getOpposite()))
                    || FluidUtil.canAccessFluid(world, pos.relative(direction), direction);
        }
        world.updateNeighborsAt(pos, state.getBlock());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var stack = player.getItemInHand(hand);
        if (!stack.isEmpty()) {
            final GlassFluidPipeBlockEntity blockEntity = (GlassFluidPipeBlockEntity) world.getBlockEntity(pos);
            assert blockEntity != null;
            if (stack.getItem() instanceof DyeItem dye) {
                DyeColor color = dye.getDyeColor();
                if (color != blockEntity.getColor()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    blockEntity.setColor(color);
                    return InteractionResult.SUCCESS;
                }
            }
            if (stack.getItem() instanceof StandardWrenchItem) {
                stack.hurt(1, world.random, player instanceof ServerPlayer ? ((ServerPlayer) player) : null);
                blockEntity.setPull(!blockEntity.isPull());
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);
        BlockState neighbor = world.getBlockState(fromPos);
        Direction dir = Direction.fromNormal(fromPos.subtract(pos));
        assert dir != null;
        var blockEntity = (GlassFluidPipeBlockEntity) world.getBlockEntity(pos);
        var otherBlockEntity = world.getBlockEntity(fromPos);
        assert blockEntity != null;
        blockEntity.getConnections()[dir.ordinal()] = !neighbor.isAir() && ((otherBlockEntity instanceof Pipe pipe && pipe.canConnect(dir.getOpposite()))
                || FluidUtil.canAccessFluid(world, fromPos, dir));
        if (!world.isClientSide) ((ServerLevel) world).getChunkSource().blockChanged(pos);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        var blockEntity = blockView.getBlockEntity(blockPos);
        if (blockEntity instanceof Connected connected) {
            return ConnectingBlockUtil.getVoxelShape(connected, NORTH, SOUTH, EAST, WEST, UP, DOWN, NONE);
        }
        return NONE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @Nullable PipeBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlassFluidPipeBlockEntity(pos, state);
    }
}