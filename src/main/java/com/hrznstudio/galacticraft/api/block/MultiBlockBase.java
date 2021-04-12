/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.SolarPanelPartBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface MultiBlockBase {
    default void onPartDestroyed(Level world, Player player, BlockState state, BlockPos pos, BlockState partState, BlockPos partPos) {
        world.destroyBlock(pos, !player.isCreative());

        for (BlockPos otherPart : getOtherParts(state, pos)) {
            if (!world.getBlockState(otherPart).isAir()) {
                world.setBlock(otherPart, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    List<BlockPos> getOtherParts(BlockState state, BlockPos pos);

    default void onMultiblockPlaced(Level world, BlockPos basePos, BlockState state) {
        BlockState defaultState = GalacticraftBlocks.SOLAR_PANEL_PART.defaultBlockState();
        for (BlockPos otherPart : this.getOtherParts(state, basePos)) {
            world.setBlockAndUpdate(otherPart, defaultState);

            BlockEntity partEntity = world.getBlockEntity(otherPart);
            assert partEntity != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((SolarPanelPartBlockEntity) partEntity).setBasePos(basePos);
            partEntity.setChanged();
        }
    }
}
