/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.CryogenicChamberPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CryogenicChamberPart extends BaseEntityBlock {
    public static final MapCodec<CryogenicChamberPart> CODEC = simpleCodec(CryogenicChamberPart::new);
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    private static final VoxelShape SHAPE_TOP = Shapes.box(0, -2, 0, 1, 1, 1);
    private static final VoxelShape SHAPE_MID = Shapes.box(0, -1, 0, 1, 2, 1);

    public CryogenicChamberPart(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(TOP, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(GCBlocks.CRYOGENIC_CHAMBER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CryogenicChamberPartBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CryogenicChamberBlock.FACING, TOP);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return state.getValue(TOP) ? SHAPE_TOP : SHAPE_MID;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        var partBE = level.getBlockEntity(blockPos);
        var be = (CryogenicChamberPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) return blockState;

        var basePos = new BlockPos(be.basePos);
        var baseState = level.getBlockState(basePos);

        // The base has been destroyed already.
        if (baseState.isAir()) return baseState;

        var block = (MultiBlockBase) baseState.getBlock();
        block.onPartDestroyed(level, player, baseState, basePos, blockState, blockPos);

        super.destroy(level, blockPos, blockState);

        return baseState;
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        boolean foundValidBlock = false;
        BlockPos validPartPos = null;
        for (BlockPos posTest : BlockPos.betweenClosed(pos.below(), pos.above())) {
            if (level.getBlockEntity(posTest) instanceof CryogenicChamberPartBlockEntity) {
                validPartPos = posTest;
                if (isCorrectBase(level, pos, posTest)) {
                    foundValidBlock = true;
                    break;
                }
            }
        }
        if (foundValidBlock && validPartPos != null) {
            var partBE = level.getBlockEntity(validPartPos);
            var be = (CryogenicChamberPartBlockEntity) partBE;
            if (be == null || be.basePos == BlockPos.ZERO) {
                return;
            }
            var basePos = new BlockPos(be.basePos);
            var baseState = level.getBlockState(basePos);

            if (baseState.isAir()) {
                return;
            }

            var block = (MultiBlockBase) baseState.getBlock();
            block.onPartExploded(level, baseState, basePos);
        }

        super.wasExploded(level, pos, explosion);
    }

    private boolean isCorrectBase(Level level, BlockPos originalPos, BlockPos testPos) {
        var partBE = level.getBlockEntity(testPos);
        var be = (CryogenicChamberPartBlockEntity) partBE;
        if (be == null || be.basePos == BlockPos.ZERO) {
            return false;
        }
        var basePos = new BlockPos(be.basePos);
        var baseState = level.getBlockState(basePos);

        if (baseState.isAir()) {
            return false;
        }

        var block = (MultiBlockBase) baseState.getBlock();
        var blocksPos = new ArrayList<>();
        for (BlockPos otherPart : block.getOtherParts(baseState)) {
            otherPart = otherPart.immutable().offset(basePos);
            blocksPos.add(otherPart);
        }
        return blocksPos.contains(originalPos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        var partBE = level.getBlockEntity(pos);
        var be = (CryogenicChamberPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) return InteractionResult.CONSUME;

        var basePos = new BlockPos(be.basePos);
        var baseState = level.getBlockState(basePos);

        // The base has been destroyed already.
        if (baseState.isAir()) return InteractionResult.PASS;

        var block = (MultiBlockBase) baseState.getBlock();
        return block.multiBlockUseWithoutItem(baseState, level, basePos, player);
    }

    @Override
    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState blockState) {
        return true;
    }
}
