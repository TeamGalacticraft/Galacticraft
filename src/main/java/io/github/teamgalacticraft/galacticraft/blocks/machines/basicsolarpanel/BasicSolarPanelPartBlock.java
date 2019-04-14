package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class BasicSolarPanelPartBlock extends Block implements BlockEntityProvider {
    // createCuboidShape(minX, minY, minZ, maxX, maxY, maxZ)
    private static final VoxelShape POLE_SHAPE = createCuboidShape(8 - 2, 0, 8 - 2, 8 + 2, 16, 8 + 2);
    private static final VoxelShape TOP_POLE_SHAPE = createCuboidShape(8 - 2, 0, 8 - 2, 8 + 2, 8, 8 + 2);
    private static final VoxelShape TOP_SHAPE = createCuboidShape(0, 6, 0, 16, 10, 16);
    private static final VoxelShape TOP_MID_SHAPE = VoxelShapes.union(TOP_POLE_SHAPE, TOP_SHAPE);

    public BasicSolarPanelPartBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos pos, VerticalEntityPosition verticalEntityPosition_1) {
        Block down = blockView_1.getBlockState(pos.down()).getBlock();
        if (down == GalacticraftBlocks.BASIC_SOLAR_PANEL_BLOCK) {
            return POLE_SHAPE;
        } else if (blockView_1.getBlockState(pos.down().down()).getBlock() == GalacticraftBlocks.BASIC_SOLAR_PANEL_BLOCK) {
            return TOP_MID_SHAPE;
        }
        return TOP_SHAPE;
    }

    @Override
    public void onBreak(World world_1, BlockPos partPos, BlockState partState, PlayerEntity playerEntity_1) {
        BlockEntity partBE = world_1.getBlockEntity(partPos);
        BasicSolarPanelPartBlockEntity be = (BasicSolarPanelPartBlockEntity) partBE;
        if (be == null) {
            // Probably already been destroyed by the code in the base.
            return;
        }

        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = world_1.getBlockState(basePos);
        if (baseState.isAir()) {
            // The base has been destroyed already.
            return;
        }

        BasicSolarPanelBlock block = (BasicSolarPanelBlock) baseState.getBlock();
        block.onPartDestroyed(world_1, partState, partPos, baseState, basePos, !playerEntity_1.isCreative());

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

    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return true;
    }

    @Override
    public boolean isSimpleFullBlock(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1) {
        return false;
    }

    @Override
    public boolean allowsSpawning(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityType<?> entityType_1) {
        return false;
    }

    @Override
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        BlockEntity partEntity = world_1.getBlockEntity(blockPos_1);
        if (world_1.isAir(blockPos_1) || !(partEntity instanceof BasicSolarPanelPartBlockEntity)) {
            return false;
        }

        BlockPos basePos = ((BasicSolarPanelPartBlockEntity) partEntity).basePos;
        System.out.println("base pos: " + basePos);

        BlockState base = world_1.getBlockState(basePos);
        BasicSolarPanelBlock baseBlock = (BasicSolarPanelBlock) base.getBlock();
        return baseBlock.activate(base, world_1, basePos, playerEntity_1, hand_1, blockHitResult_1);
    }
}