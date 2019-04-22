package com.hrznstudio.galacticraft.blocks.environment;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class MoonBerryBushBlock extends PlantBlock {

    public static final IntegerProperty AGE = Properties.AGE_3;
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public MoonBerryBushBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, 0));
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView blockView_1, BlockPos blockPos_1, BlockState blockState_1) {
        return new ItemStack(GalacticraftItems.MOON_BERRIES);
    }

    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
        if (blockState.get(AGE) == 0) {
            return SMALL_SHAPE;
        } else {
            return blockState.get(AGE) < 3 ? LARGE_SHAPE : super.getOutlineShape(blockState, blockView, blockPos, verticalEntityPosition);
        }
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        super.onScheduledTick(blockState, world, blockPos, random);
        int age = blockState.get(AGE);
        if (age < 3 && random.nextInt(20) == 0) {
            world.setBlockState(blockPos, blockState.with(AGE, age + 1), 2);
        }
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        entity.slowMovement(blockState, new Vec3d(0.800000011920929D, 0.75D, 0.800000011920929D));
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        int age = blockState.get(AGE);
        boolean mature = age == 3;

        if (mature) {
            int amount = 1 + world.random.nextInt(3);
            dropStack(world, blockPos, new ItemStack(GalacticraftItems.MOON_BERRIES, amount));
            world.playSound(null, blockPos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlockState(blockPos, blockState.with(AGE, 1), 2);
            return true;
        } else {
            return super.activate(blockState, world, blockPos, playerEntity, hand, blockHitResult);
        }
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.with(AGE);
    }

    @Override
    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.getBlock() == GalacticraftBlocks.MOON_DIRT_BLOCK;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (blockState.get(AGE) == 3) {

            double x = blockPos.getX() + 0.5D + (random.nextFloat() - random.nextFloat());
            double y = blockPos.getY() + random.nextFloat();
            double z = blockPos.getZ() + 0.5D + (random.nextFloat() - random.nextFloat());
            int times = random.nextInt(4);

            for (int i = 0; i < times; i++) {
                world.addParticle(new DustParticleParameters(0.5f, 0.5f, 1.0f, 0.6f), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
