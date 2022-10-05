package dev.galacticraft.mod.block.special;

import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.block.entity.CryogenicChamberPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CryogenicChamberPart extends BaseEntityBlock {
    public CryogenicChamberPart(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CryogenicChamberPartBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CryogenicChamberBlock.FACING);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos partPos, BlockState partState, Player player) {
        BlockEntity partBE = world.getBlockEntity(partPos);
        CryogenicChamberPartBlockEntity be = (CryogenicChamberPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) {
            return;
        }
        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = world.getBlockState(basePos);

        if (baseState.isAir()) {
            // The base has been destroyed already.
            return;
        }

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
        block.onPartDestroyed(world, player, baseState, basePos, partState, partPos);

        super.destroy(world, partPos, partState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity partBE = level.getBlockEntity(blockPos);
        CryogenicChamberPartBlockEntity be = (CryogenicChamberPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) {
            return InteractionResult.PASS;
        }
        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = level.getBlockState(basePos);

        if (baseState.isAir()) {
            // The base has been destroyed already.
            return InteractionResult.PASS;
        }

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
        return block.onMultiBlockUse(blockState, level, basePos, player, interactionHand, blockHitResult);
    }
}
