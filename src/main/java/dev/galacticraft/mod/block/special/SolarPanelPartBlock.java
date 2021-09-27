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

package dev.galacticraft.mod.block.special;

import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.SolarPanelPartBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SolarPanelPartBlock extends BlockWithEntity {
    private static final VoxelShape POLE_SHAPE = createCuboidShape(8 - 2, 0, 8 - 2, 8 + 2, 16, 8 + 2);
    private static final VoxelShape TOP_POLE_SHAPE = createCuboidShape(8 - 2, 0, 8 - 2, 8 + 2, 8, 8 + 2);
    private static final VoxelShape TOP_SHAPE = createCuboidShape(0, 6, 0, 16, 10, 16);
    private static final VoxelShape TOP_MID_SHAPE = VoxelShapes.union(TOP_POLE_SHAPE, TOP_SHAPE);

    public SolarPanelPartBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView blockView, BlockPos pos, ShapeContext context) {
        Block down = blockView.getBlockState(pos.down()).getBlock();
        if (down instanceof MultiBlockBase) {
            return POLE_SHAPE;
        } else if (blockView.getBlockState(pos.down().down()).getBlock() == GalacticraftBlock.BASIC_SOLAR_PANEL) {
            return TOP_MID_SHAPE;
        }
        return TOP_SHAPE;
    }

    @Override
    public void onBreak(World world, BlockPos partPos, BlockState partState, PlayerEntity player) {
        BlockEntity partBE = world.getBlockEntity(partPos);
        SolarPanelPartBlockEntity be = (SolarPanelPartBlockEntity) partBE;

        if (be == null || be.basePos == BlockPos.ORIGIN) {
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

        super.onBroken(world, partPos, partState);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public ItemStack getPickStack(BlockView blockView, BlockPos pos, BlockState state) {
        return new ItemStack(GalacticraftBlock.BASIC_SOLAR_PANEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelPartBlockEntity(pos, state);
    }

    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView blockView, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView blockView, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canMobSpawnInside() {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        BlockEntity partEntity = world.getBlockEntity(pos);
        if (world.isAir(pos) || !(partEntity instanceof SolarPanelPartBlockEntity)) {
            return ActionResult.SUCCESS;
        }

        if (world.isClient) return ActionResult.SUCCESS;

        BlockPos basePos = ((SolarPanelPartBlockEntity) partEntity).basePos;

        BlockState base = world.getBlockState(basePos);
        return base.getBlock().onUse(base, world, basePos, player, hand, blockHitResult);
    }
}