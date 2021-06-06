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

package dev.galacticraft.mod.block.environment;

import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CavernousVineBlock extends Block implements Waterloggable {
    protected static final EnumProperty<VineTypes> VINES = EnumProperty.of("vinetype", VineTypes.class);
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CavernousVineBlock(Settings settings) {
        super(settings);
        settings.noCollision();
        this.setDefaultState(this.getStateManager().getDefaultState().with(WATERLOGGED, false).with(VINES, VineTypes.VINE_0));
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player) {
        super.onBreak(world, blockPos, blockState, player);

        if (player.getActiveItem().getItem() instanceof ShearsItem) {
            ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(this.asItem(), 1));
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living) || (entity instanceof PlayerEntity player && player.getAbilities().flying)) {
            return;
        }

        this.onCollided(living);
    }

    public void onCollided(LivingEntity entity) {
        this.dragEntityUp(entity);
    }

    private void dragEntityUp(LivingEntity entity) {
        entity.setVelocity(entity.getVelocity().x, 0.1D, entity.getVelocity().z);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return super.getPlacementState(context).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(VINES, VineTypes.VINE_0);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState neighborBlockState, WorldAccess world, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (blockState.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(blockState, direction, neighborBlockState, world, blockPos, neighborBlockPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED).add(VINES);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView WorldView, BlockPos pos) {
        BlockPos pos2 = pos;
        BlockPos pos3 = pos;
        pos2 = pos2.add(0, -1, 0);
        pos3 = pos3.add(0, 1, 0);
        //If it isn't on the ground and it is below a block
        return (!WorldView.getBlockState(pos3).getBlock().equals(Blocks.AIR))
                && (WorldView.getBlockState(pos2).getBlock().equals(Blocks.AIR)
                || WorldView.getBlockState(pos2).getBlock().equals(GalacticraftBlock.CAVERNOUS_VINE)
                || WorldView.getBlockState(pos2).getBlock().equals(GalacticraftBlock.POISONOUS_CAVERNOUS_VINE));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos pos_2, boolean notify) {
        super.neighborUpdate(state, world, pos, block, pos_2, notify);
        if (!canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, false);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        for (int y2 = pos.getY() - 1; y2 >= pos.getY() - 2; y2--) {

            BlockPos pos1 = new BlockPos(pos.getX(), y2, pos.getZ());
            BlockState blockState = world.getBlockState(pos1);

            if (!blockState.isAir()) {
                return;
            }
        }
        world.setBlockState(pos.down(), this.getStateFromMeta(getVineLength(world, pos)), 2);
        world.updateNeighbors(pos, state.getBlock());
    }

    private BlockState getStateFromMeta(int meta) {
        return GalacticraftBlock.POISONOUS_CAVERNOUS_VINE.getDefaultState().with(VINES, VineTypes.byMetadata(meta));
    }

    private int getVineLength(World world, BlockPos pos) {
        int vineCount = 0;
        int y2 = pos.getY();

        while (world.getBlockState(new BlockPos(pos.getX(), y2, pos.getZ())).getBlock() == this) {
            vineCount++;
            y2++;
        }
        return vineCount;
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack stack) {
        BlockPos abovePos = new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
        BlockState stateAbove = world.getBlockState(abovePos);

        if (stateAbove.getBlock() == GalacticraftBlock.CAVERNOUS_VINE || stateAbove.getBlock() == GalacticraftBlock.POISONOUS_CAVERNOUS_VINE) {
            switch (stateAbove.get(VINES).getMeta()) {
                case 0 -> world.setBlockState(blockPos, this.getStateManager().getDefaultState().with(WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER).with(VINES, VineTypes.VINE_1));
                case 1 -> world.setBlockState(blockPos, this.getStateManager().getDefaultState().with(WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER).with(VINES, VineTypes.VINE_2));
                default -> world.setBlockState(blockPos, this.getStateManager().getDefaultState().with(WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER).with(VINES, VineTypes.VINE_0));
            }
        }
    }

    public enum VineTypes implements StringIdentifiable {
        VINE_0("vine_0", 0),
        VINE_1("vine_1", 1),
        VINE_2("vine_2", 2);

        private final static VineTypes[] values = values();
        private final String name;
        private final int meta;

        VineTypes(String name, int meta) {
            this.name = name;
            this.meta = meta;
        }

        public static VineTypes byMetadata(int meta) {
            return values[meta % values.length];
        }

        public int getMeta() {
            return this.meta;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
