/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.environment;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class MoonBerryBushBlock extends BushBlock {
    public static final MapCodec<MoonBerryBushBlock> CODEC = simpleCodec(MoonBerryBushBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SMALL_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape LARGE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public MoonBerryBushBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos pos, BlockState state) {
        return new ItemStack(GCItems.MOON_BERRIES);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        if (blockState.getValue(AGE) == 0) {
            return SMALL_SHAPE;
        } else {
            return blockState.getValue(AGE) < 3 ? LARGE_SHAPE : super.getShape(blockState, blockView, blockPos, context);
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel world, BlockPos blockPos, RandomSource random) {
        super.tick(blockState, world, blockPos, random);
        int age = blockState.getValue(AGE);
        if (age < 3 && random.nextInt(20) == 0) {
            world.setBlock(blockPos, blockState.setValue(AGE, age + 1), 2);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos blockPos, Entity entity) {
        entity.makeStuckInBlock(blockState, new Vec3(0.800000011920929D, 0.75D, 0.800000011920929D));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        int age = blockState.getValue(AGE);
        boolean mature = age == 3;

        if (mature) {
            int amount = 1 + world.random.nextInt(3);
            popResource(world, blockPos, new ItemStack(GCItems.MOON_BERRIES, amount));
            world.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(blockPos, blockState.setValue(AGE, 1), 2);
            return InteractionResult.SUCCESS;
        } else {
            return super.use(blockState, world, blockPos, player, hand, blockHitResult);
        }
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AGE);
    }

    @Override
    public boolean mayPlaceOn(BlockState blockState, BlockGetter blockView, BlockPos blockPos) {
        return blockState.getBlock() == GCBlocks.MOON_DIRT;
    }

    @Override
    public void animateTick(BlockState blockState, Level world, BlockPos blockPos, RandomSource random) {
        if (blockState.getValue(AGE) == 3) {

            double x = blockPos.getX() + 0.5D + (random.nextFloat() - random.nextFloat());
            double y = blockPos.getY() + random.nextFloat();
            double z = blockPos.getZ() + 0.5D + (random.nextFloat() - random.nextFloat());
            int times = random.nextInt(4);

            for (int i = 0; i < times; i++) {
                world.addParticle(new DustParticleOptions(new Vector3f(0.5f, 0.5f, 1.0f), 0.6f), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
