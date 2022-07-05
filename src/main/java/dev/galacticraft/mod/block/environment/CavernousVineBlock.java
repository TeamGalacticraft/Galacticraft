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

package dev.galacticraft.mod.block.environment;

import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CavernousVineBlock extends Block implements SimpleWaterloggedBlock {
    protected static final EnumProperty<VineTypes> VINES = EnumProperty.create("vinetype", VineTypes.class);
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public CavernousVineBlock(Properties settings) {
        super(settings);
        settings.noCollission();
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(VINES, VineTypes.VINE_0));
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos blockPos, BlockState blockState, Player player) {
        super.playerWillDestroy(world, blockPos, blockState, player);

        if (player.getUseItem().getItem() instanceof ShearsItem) {
            Containers.dropItemStack(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(this.asItem(), 1));
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living) || (entity instanceof Player player && player.getAbilities().flying)) {
            return;
        }

        this.onCollided(living);
    }

    public void onCollided(LivingEntity entity) {
        this.dragEntityUp(entity);
    }

    private void dragEntityUp(LivingEntity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().x, 0.1D, entity.getDeltaMovement().z);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(VINES, VineTypes.VINE_0);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor world, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (blockState.getValue(WATERLOGGED)) {
            world.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(blockState, direction, neighborBlockState, world, blockPos, neighborBlockPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED).add(VINES);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader WorldView, BlockPos pos) {
        BlockPos pos2 = pos;
        BlockPos pos3 = pos;
        pos2 = pos2.offset(0, -1, 0);
        pos3 = pos3.offset(0, 1, 0);
        //If it isn't on the ground and it is below a block
        return (!WorldView.getBlockState(pos3).getBlock().equals(Blocks.AIR))
                && (WorldView.getBlockState(pos2).getBlock().equals(Blocks.AIR)
                || WorldView.getBlockState(pos2).getBlock().equals(GalacticraftBlock.CAVERNOUS_VINE)
                || WorldView.getBlockState(pos2).getBlock().equals(GalacticraftBlock.POISONOUS_CAVERNOUS_VINE));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos pos_2, boolean notify) {
        super.neighborChanged(state, world, pos, block, pos_2, notify);
        if (!canSurvive(state, world, pos)) {
            world.destroyBlock(pos, false);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        for (int y2 = pos.getY() - 1; y2 >= pos.getY() - 2; y2--) {

            BlockPos pos1 = new BlockPos(pos.getX(), y2, pos.getZ());
            BlockState blockState = world.getBlockState(pos1);

            if (!blockState.isAir()) {
                return;
            }
        }
        world.setBlock(pos.below(), this.getStateFromMeta(getVineLength(world, pos)), 2);
        world.blockUpdated(pos, state.getBlock());
    }

    private BlockState getStateFromMeta(int meta) {
        return GalacticraftBlock.POISONOUS_CAVERNOUS_VINE.defaultBlockState().setValue(VINES, VineTypes.byMetadata(meta));
    }

    private int getVineLength(Level world, BlockPos pos) {
        int vineCount = 0;
        int y2 = pos.getY();

        while (world.getBlockState(new BlockPos(pos.getX(), y2, pos.getZ())).getBlock() == this) {
            vineCount++;
            y2++;
        }
        return vineCount;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack stack) {
        BlockPos abovePos = new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
        BlockState stateAbove = world.getBlockState(abovePos);

        if (stateAbove.getBlock() == GalacticraftBlock.CAVERNOUS_VINE || stateAbove.getBlock() == GalacticraftBlock.POISONOUS_CAVERNOUS_VINE) {
            switch (stateAbove.getValue(VINES).getMeta()) {
                case 0 -> world.setBlockAndUpdate(blockPos, this.getStateDefinition().any().setValue(WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER).setValue(VINES, VineTypes.VINE_1));
                case 1 -> world.setBlockAndUpdate(blockPos, this.getStateDefinition().any().setValue(WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER).setValue(VINES, VineTypes.VINE_2));
                default -> world.setBlockAndUpdate(blockPos, this.getStateDefinition().any().setValue(WATERLOGGED, world.getBlockState(blockPos).getBlock() == Blocks.WATER).setValue(VINES, VineTypes.VINE_0));
            }
        }
    }

    public enum VineTypes implements StringRepresentable {
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
        public String getSerializedName() {
            return this.name;
        }
    }
}
