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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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

    public GlassFluidPipeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        var blockEntity = (GlassFluidPipeBlockEntity) world.getBlockEntity(pos);
        assert blockEntity != null;
        for (var direction : Constant.Misc.DIRECTIONS) {
            var otherBlockEntity = world.getBlockEntity(pos.offset(direction));
            blockEntity.getConnections()[direction.ordinal()] = (otherBlockEntity instanceof Pipe pipe && pipe.canConnect(direction.getOpposite()))
                    || FluidUtil.canAccessFluid(world, pos.offset(direction), direction);
        }
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var stack = player.getStackInHand(hand);
        if (!stack.isEmpty()) {
            var blockEntity = (GlassFluidPipeBlockEntity) world.getBlockEntity(pos);
            assert blockEntity != null;
            if (stack.getItem() instanceof DyeItem dye) {
                var color = dye.getColor();
                if (color != blockEntity.getColor()) {
                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                    blockEntity.setColor(color);
                    if (!world.isClient) {
                        blockEntity.sync();
                    }
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
            if (stack.getItem() instanceof StandardWrenchItem) {
                stack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
                blockEntity.setPull(!blockEntity.isPull());
                if (!world.isClient) {
                    blockEntity.sync();
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        var neighbor = world.getBlockState(fromPos);
        var dir = Direction.fromVector(fromPos.subtract(pos));
        assert dir != null;
        var blockEntity = (GlassFluidPipeBlockEntity) world.getBlockEntity(pos);
        var otherBlockEntity = world.getBlockEntity(fromPos);
        assert blockEntity != null;
        blockEntity.getConnections()[dir.ordinal()] = !neighbor.isAir() && ((otherBlockEntity instanceof Pipe pipe && pipe.canConnect(dir.getOpposite()))
                || FluidUtil.canAccessFluid(world, fromPos, dir));
        if (!world.isClient) blockEntity.sync();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        var blockEntity = blockView.getBlockEntity(blockPos);
        if (blockEntity instanceof Connected connected) {
            return ConnectingBlockUtil.getVoxelShape(connected, NORTH, SOUTH, EAST, WEST, UP, DOWN, NONE);
        }
        return NONE;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @Nullable PipeBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GlassFluidPipeBlockEntity(pos, state);
    }
}
