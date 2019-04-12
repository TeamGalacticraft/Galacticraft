package io.github.teamgalacticraft.galacticraft.fluids;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;

public class FuelFluid extends BaseFluid {

    @Override
    public Fluid getFlowing() {
        return GalacticraftFluids.FLOWING_FUEL;
    }

    @Override
    public Fluid getStill() {
        return GalacticraftFluids.STILL_FUEL;
    }

    @Override
    protected BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public Item getBucketItem() {
        return null;
    }

    @Environment(EnvType.CLIENT)
    public ParticleParameters getParticle() {
        return ParticleTypes.DRIPPING_WATER;
    }

    @Override
    protected boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(ViewableWorld viewableWorld) {
        return 0;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == this;
    }

    @Override
    protected boolean method_15737() {
        // Swim
        return true;
    }

    @Override
    protected void method_15730(IWorld iWorld, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    protected int method_15733(ViewableWorld viewableWorld) {
        return 0;
    }

    @Override
    protected int method_15739(ViewableWorld viewableWorld) {
        return 0;
    }

    @Override
    protected boolean hasRandomTicks() {
        return true;
    }

    @Override
    protected float getBlastResistance() {
        return 100.f;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return null;
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

        @Override
        protected void appendProperties(StateFactory.Builder<Fluid, FluidState> stateBuilder) {
            super.appendProperties(stateBuilder);
            stateBuilder.with(LEVEL);
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

    public static class Still extends FuelFluid {

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
