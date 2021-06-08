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
import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonBerryBushBlock extends PlantBlock {

    public static final IntProperty AGE = Properties.AGE_3;
    private static final VoxelShape SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public MoonBerryBushBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(AGE, 0));
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView view, BlockPos pos, BlockState state) {
        return new ItemStack(GalacticraftItem.MOON_BERRIES);
    }

    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        if (blockState.get(AGE) == 0) {
            return SMALL_SHAPE;
        } else {
            return blockState.get(AGE) < 3 ? LARGE_SHAPE : super.getOutlineShape(blockState, blockView, blockPos, context);
        }
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld world, BlockPos blockPos, Random random) {
        super.scheduledTick(blockState, world, blockPos, random);
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
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        int age = blockState.get(AGE);
        boolean mature = age == 3;

        if (mature) {
            int amount = 1 + world.random.nextInt(3);
            dropStack(world, blockPos, new ItemStack(GalacticraftItem.MOON_BERRIES, amount));
            world.playSound(null, blockPos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlockState(blockPos, blockState.with(AGE, 1), 2);
            return ActionResult.SUCCESS;
        } else {
            return super.onUse(blockState, world, blockPos, player, hand, blockHitResult);
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AGE);
    }

    @Override
    protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.getBlock() == GalacticraftBlock.MOON_DIRT;
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
                world.addParticle(new DustParticleEffect(new Vec3f(0.5f, 0.5f, 1.0f), 0.6f), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
