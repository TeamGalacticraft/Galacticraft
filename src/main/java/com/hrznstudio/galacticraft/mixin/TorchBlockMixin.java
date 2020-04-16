package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends Block {
    public TorchBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (CelestialBodyType.getByDimType(world.dimension.getType()).isPresent() && !CelestialBodyType.getByDimType(world.dimension.getType()).get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
            if (state.getBlock() == Blocks.TORCH) {
                world.setBlockState(pos, GalacticraftBlocks.UNLIT_TORCH.getDefaultState());
            } else if (state.getBlock() == Blocks.WALL_TORCH) {
                world.setBlockState(pos, GalacticraftBlocks.UNLIT_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, state.get(WallTorchBlock.FACING)));
            }
            world.addParticle(ParticleTypes.SMOKE, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }
}
