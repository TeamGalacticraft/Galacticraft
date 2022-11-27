/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.content.block.special;

import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.CryogenicChamberPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CryogenicChamberPart extends BaseEntityBlock {
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public CryogenicChamberPart(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(GCBlocks.CRYOGENIC_CHAMBER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CryogenicChamberPartBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CryogenicChamberBlock.FACING, TOP);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos partPos, BlockState partState, Player player) {
        BlockEntity partBE = world.getBlockEntity(partPos);
        CryogenicChamberPartBlockEntity be = (CryogenicChamberPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) return;

        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = world.getBlockState(basePos);

        // The base has been destroyed already.
        if (baseState.isAir()) return;

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
        block.onPartDestroyed(world, player, baseState, basePos, partState, partPos);

        super.destroy(world, partPos, partState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity partBE = level.getBlockEntity(blockPos);
        CryogenicChamberPartBlockEntity be = (CryogenicChamberPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) return InteractionResult.CONSUME;

        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = level.getBlockState(basePos);

        // The base has been destroyed already.
        if (baseState.isAir()) return InteractionResult.PASS;

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();

        return block.onMultiBlockUse(blockState, level, basePos, player, interactionHand, blockHitResult);
    }
}
