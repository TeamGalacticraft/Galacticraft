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

import org.jetbrains.annotations.Nullable;

import dev.galacticraft.mod.block.entity.FallenMeteorBlockEntity;
import dev.galacticraft.mod.util.ColorUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FallenMeteorBlock extends FallingBlock implements Waterloggable, BlockEntityProvider {
    private static final VoxelShape SHAPE = createCuboidShape(3, 1, 3, 13, 11, 13);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public FallenMeteorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FallenMeteorBlockEntity();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FallenMeteorBlockEntity) {
            ((FallenMeteorBlockEntity)blockEntity).setHeatLevel(0);
        }
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockView, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entityIn) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof FallenMeteorBlockEntity) {
            FallenMeteorBlockEntity meteor = (FallenMeteorBlockEntity) blockEntity;

            if (meteor.getHeatLevel() <= 0) {
                return;
            }

            if (entityIn instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entityIn;

                world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.NEUTRAL, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                for (int i = 0; i < 8; ++i) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + 0.2D + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                if (!livingEntity.isOnFire()) {
                    livingEntity.setFireTicks(2);
                }

                double knockX = pos.getX() + 0.5F - livingEntity.getX();
                double knockZ;

                for (knockZ = livingEntity.getZ() - pos.getZ(); knockX * knockX + knockZ * knockZ < 1.0E-4D; knockZ = (Math.random() - Math.random()) * 0.01D) {
                    knockX = (Math.random() - Math.random()) * 0.01D;
                }

                livingEntity.takeKnockback(1, knockX, knockZ);
            }
        }
    }

    @Override
    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
        entity.blockEntityData = entity.world.getBlockEntity(entity.getBlockPos()).toTag(new CompoundTag());
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        ((FallenMeteorBlockEntity)world.getBlockEntity(pos)).setHeatLevel(fallingBlockEntity.blockEntityData.getInt("HeatLevel"));
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            int i = MathHelper.nextInt(world.random, 3, 7);
            if (i > 0) {
                this.dropExperience(world, pos, i);
            }
        }
    }

    //TODO Fix falling block entity don't render block color
    public static int colorMultiplier(BlockRenderView blockView, BlockPos pos) {
        if (blockView != null && pos != null) {
            BlockEntity blockEntity = blockView.getBlockEntity(pos);

            if (blockEntity instanceof FallenMeteorBlockEntity) {
                FallenMeteorBlockEntity meteor = (FallenMeteorBlockEntity) blockEntity;
                float scale = 200 - meteor.getScaledHeatLevel() * 200;
                return ColorUtil.rgb(Math.min(255, 198 + (int)scale), Math.min(255, 108 + (int)scale), Math.min(255, 58 + (int)scale));
            }
        }
        return 16777215;
    }
}