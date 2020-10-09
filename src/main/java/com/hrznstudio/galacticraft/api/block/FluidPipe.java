package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.pipe.PipeNetwork;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidPipe extends Block implements BlockEntityProvider {
    public FluidPipe(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && Galacticraft.configManager.get().isDebugLogEnabled()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof FluidPipeBlockEntity) {
                Galacticraft.logger.info(((FluidPipeBlockEntity) entity).getNetwork());
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient()) {
            ((FluidPipeBlockEntity) world.getBlockEntity(pos)).getNetwork().removePipe(pos);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos thePosOThisPipe, Block block, BlockPos updatedBlockPos, boolean notify) {
        super.neighborUpdate(state, world, thePosOThisPipe, block, updatedBlockPos, notify);
        if (!world.isClient()) {
            ((FluidPipeBlockEntity) world.getBlockEntity(thePosOThisPipe)).getNetwork().updateConnections(updatedBlockPos, thePosOThisPipe);
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public @Nullable FluidPipeBlockEntity createBlockEntity(BlockView world) {
        return new FluidPipeBlockEntity();
    }
}
