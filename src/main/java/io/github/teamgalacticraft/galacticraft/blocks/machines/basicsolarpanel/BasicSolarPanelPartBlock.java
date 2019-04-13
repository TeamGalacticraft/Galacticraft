package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BasicSolarPanelPartBlock extends Block implements BlockEntityProvider {
    public BasicSolarPanelPartBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, VerticalEntityPosition verticalEntityPosition_1) {
        return VoxelShapes.empty();
    }

    @Override
    public void onBreak(World world_1, BlockPos partPos, BlockState partState, PlayerEntity playerEntity_1) {
        BlockEntity partBE = world_1.getBlockEntity(partPos);
        BasicSolarPanelPartBlockEntity be = (BasicSolarPanelPartBlockEntity) partBE;

        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = world_1.getBlockState(basePos);
        BasicSolarPanelBlock block = (BasicSolarPanelBlock) baseState.getBlock();
        block.onPartDestroyed(world_1, partState, partPos, baseState, basePos);

        super.onBroken(world_1, partPos, partState);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState_1) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public ItemStack getPickStack(BlockView blockView_1, BlockPos blockPos_1, BlockState blockState_1) {
        return new ItemStack(GalacticraftBlocks.BASIC_SOLAR_PANEL_BLOCK);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new BasicSolarPanelPartBlockEntity();
    }
}