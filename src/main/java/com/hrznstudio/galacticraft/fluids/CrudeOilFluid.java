package com.hrznstudio.galacticraft.fluids;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CrudeOilFluid extends BaseFluid {


    @Override
    public Fluid getFlowing() {
        return GalacticraftFluids.FLOWING_CRUDE_OIL;
    }

    @Override
    public Fluid getStill() {
        return GalacticraftFluids.STILL_CRUDE_OIL;
    }

    @Override
    protected boolean isInfinite() {
        return true;
    }

    @Override
    protected BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public Item getBucketItem() {
        return GalacticraftItems.CRUDE_OIL_BUCKET;
    }

    @Environment(EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.matches(FluidTags.WATER);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
        if (random.nextInt(10) == 0) {
            world.addParticle(new DustParticleEffect(0.0f, 0.0f, 0.0f, 0.5f),
                    (double) blockPos.getX() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    (double) blockPos.getY() + 1.1F,
                    (double) blockPos.getZ() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    0.0D, 0.0D, 0.0D);
        }
    }


    @Override
    public int getTickRate(ViewableWorld viewableWorld) {
        return 7;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    public void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? iWorld.getBlockEntity(blockPos) : null;
        Block.dropStacks(blockState, iWorld.getWorld(), blockPos, blockEntity);
    }

    @Override
    public int method_15733(ViewableWorld viewableWorld) {
        return 4;
    }

    @Override
    public int getLevelDecreasePerBlock(ViewableWorld viewableWorld) {
        return 1;
    }

    @Override
    public boolean hasRandomTicks() {
        return true;
    }

    @Override
    public float getBlastResistance() {
        return 100.f;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return GalacticraftBlocks.CRUDE_OIL.getDefaultState().with(FluidBlock.LEVEL, method_15741(fluidState));
    }

    @Override
    public boolean isStill(FluidState fluidState) {
        return false;
    }

    @Override
    public int getLevel(FluidState fluidState) {
        return 0;
    }

    public static class Flowing extends CrudeOilFluid {

        public Flowing() {

        }

        @Override
        protected void appendProperties(StateFactory.Builder<Fluid, FluidState> stateBuilder) {
            super.appendProperties(stateBuilder);
            stateBuilder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends CrudeOilFluid {

        public Still() {

        }

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
