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

package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.SolarPanelPartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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

public class SolarPanelPartBlock extends BaseEntityBlock {
    public static final MapCodec<SolarPanelPartBlock> CODEC = simpleCodec(SolarPanelPartBlock::new);
    private static final VoxelShape POLE_SHAPE = box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape TOP_POLE_SHAPE = box(6, 0, 6, 10, 7, 10);
    private static final VoxelShape TOP_SHAPE = box(0, 7, 0, 16, 10, 16);
    private static final VoxelShape TOP_MID_SHAPE = Shapes.or(TOP_POLE_SHAPE, TOP_SHAPE);

    public SolarPanelPartBlock(Properties settings) {
        super(settings.pushReaction(PushReaction.BLOCK));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockView, BlockPos pos, CollisionContext context) {
        var down = blockView.getBlockState(pos.below()).getBlock();
        var centerTopBlock = blockView.getBlockState(pos.below(2)).getBlock();
        if (down instanceof MultiBlockBase) {
            return POLE_SHAPE;
        } else if (centerTopBlock == GCBlocks.BASIC_SOLAR_PANEL || centerTopBlock == GCBlocks.ADVANCED_SOLAR_PANEL) {
            return TOP_MID_SHAPE;
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
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos pos, BlockState state) {
        return new ItemStack(GCBlocks.BASIC_SOLAR_PANEL);
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
    public InteractionResult use(BlockState state, @NotNull Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockEntity partEntity = world.getBlockEntity(pos);
        if (world.isClientSide || world.isEmptyBlock(pos) || !(partEntity instanceof SolarPanelPartBlockEntity)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos basePos = ((SolarPanelPartBlockEntity) partEntity).basePos;
        BlockState base = world.getBlockState(basePos);
        return base.getBlock().use(base, world, basePos, player, hand, blockHitResult);
    }
}