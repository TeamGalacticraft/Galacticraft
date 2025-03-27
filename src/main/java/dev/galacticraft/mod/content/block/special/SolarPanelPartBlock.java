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
import dev.galacticraft.mod.content.block.entity.SolarPanelPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SolarPanelPartBlock extends BaseEntityBlock {
    public static final MapCodec<SolarPanelPartBlock> CODEC = simpleCodec(SolarPanelPartBlock::new);
    private static final VoxelShape POLE_SHAPE = box(6.5, 0, 6.5, 9.5, 16, 9.5);
    private static final VoxelShape TOP_POLE_SHAPE = box(6.5, 0, 6.5, 9.5, 8, 9.5);
    private static final VoxelShape TOP_SHAPE = box(0, 8, 0, 16, 9, 16);
    private static final VoxelShape[] TOP_SHAPES = {
            box(0, 8, 1, 15, 9, 16),
            box(0, 8, 0, 15, 9, 16),
            box(0, 8, 0, 15, 9, 15),
            box(0, 8, 1, 16, 9, 16),
            Shapes.or(TOP_POLE_SHAPE, TOP_SHAPE),
            box(0, 8, 0, 16, 9, 15),
            box(1, 8, 1, 16, 9, 16),
            box(1, 8, 0, 16, 9, 16),
            box(1, 8, 0, 16, 9, 15)
    };

    public SolarPanelPartBlock(Properties settings) {
        super(settings.pushReaction(PushReaction.BLOCK));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockView, BlockPos pos, CollisionContext context) {
        var block = blockView.getBlockState(pos.below()).getBlock();
        if (block == GCBlocks.BASIC_SOLAR_PANEL || block == GCBlocks.ADVANCED_SOLAR_PANEL) {
            return POLE_SHAPE;
        }
        for (int i = 0; i < 9; i++) {
            block = blockView.getBlockState(pos.below(2).east((i / 3) - 1).north((i % 3) - 1)).getBlock();
            if (block == GCBlocks.BASIC_SOLAR_PANEL || block == GCBlocks.ADVANCED_SOLAR_PANEL) {
                return TOP_SHAPES[i];
            }
        }
        return TOP_SHAPE;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos partPos, BlockState partState, Player player) {
        BlockEntity partBE = world.getBlockEntity(partPos);
        SolarPanelPartBlockEntity be = (SolarPanelPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ZERO) {
            return partState;
        }
        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = world.getBlockState(basePos);

        if (baseState.isAir()) {
            // The base has been destroyed already.
            return partState;
        }

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
        block.onPartDestroyed(world, player, baseState, basePos, partState, partPos);

        super.destroy(world, partPos, partState);
        return partState;
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
        boolean foundValidBlock = false;
        BlockPos validPartPos = null;
        for (BlockPos posTest : BlockPos.betweenClosed(pos.east().north(), pos.west().south())) {
            if (world.getBlockEntity(posTest) instanceof SolarPanelPartBlockEntity) {
                validPartPos = posTest;
                if (isCorrectBase(world, pos, posTest)) {
                    foundValidBlock = true;
                    break;
                }
            }
        }

        if (foundValidBlock && validPartPos != null) {
            BlockEntity partBE = world.getBlockEntity(validPartPos);
            SolarPanelPartBlockEntity be = (SolarPanelPartBlockEntity) partBE;
            if (be == null || be.basePos == BlockPos.ZERO) {
                return;
            }
            BlockPos basePos = new BlockPos(be.basePos);
            BlockState baseState = world.getBlockState(basePos);

            if (baseState.isAir()) {
                return;
            }

            MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
            block.onPartExploded(world, baseState, basePos);
        }

        super.wasExploded(world, pos, explosion);
    }


    private boolean isCorrectBase(Level world, BlockPos originalPos, BlockPos testPos) {
        BlockEntity partBE = world.getBlockEntity(testPos);
        SolarPanelPartBlockEntity be = (SolarPanelPartBlockEntity) partBE;
        if (be == null || be.basePos == BlockPos.ZERO) {
            return false;
        }
        BlockPos basePos = new BlockPos(be.basePos);
        BlockState baseState = world.getBlockState(basePos);

        if (baseState.isAir()) {
            return false;
        }

        MultiBlockBase block = (MultiBlockBase) baseState.getBlock();
        List<BlockPos> blocksPos = new ArrayList<>();
        for (BlockPos otherPart : block.getOtherParts(baseState)) {
            otherPart = otherPart.immutable().offset(basePos);
            blocksPos.add(otherPart);
        }
        return blocksPos.contains(originalPos);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos pos, BlockState state) {
        BlockEntity partBE = levelReader.getChunk(pos).getBlockEntity(pos);
        SolarPanelPartBlockEntity be = (SolarPanelPartBlockEntity) partBE;
        if (be == null || be.basePos == BlockPos.ZERO) {
            return new ItemStack(GCBlocks.BASIC_SOLAR_PANEL);
        }

        Level world = be.getLevel();
        return new ItemStack(world.getBlockState(be.basePos).getBlock());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelPartBlockEntity(pos, state);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter blockView, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter blockView, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState blockState) {
        return false;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof SolarPanelPartBlockEntity part) {
            BlockPos basePos = part.basePos;
            BlockState base = level.getBlockState(basePos);
            return ((MultiBlockBase) base.getBlock()).multiBlockUseWithoutItem(base, level, basePos, player);
        }
        return InteractionResult.PASS;
    }
}