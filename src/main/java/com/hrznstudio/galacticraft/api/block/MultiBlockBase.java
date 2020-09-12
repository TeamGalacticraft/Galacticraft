/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.MultiBlockPartBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface MultiBlockBase {
    @NotNull
    static List<BlockPos> genPartList(BlockPos pos) {
        List<BlockPos> parts = new LinkedList<>();
        BlockPos rod = pos.up();
        BlockPos mid = rod.up();
        BlockPos front = mid.north();
        BlockPos back = mid.south();

        BlockPos right = mid.east();
        BlockPos left = mid.west();

        BlockPos frontLeft = front.east();
        BlockPos frontRight = front.west();
        BlockPos backLeft = back.east();
        BlockPos backRight = back.west();

        parts.add(rod);
        parts.add(mid);
        parts.add(front);
        parts.add(back);

        parts.add(right);
        parts.add(left);

        parts.add(frontLeft);
        parts.add(frontRight);
        parts.add(backLeft);
        parts.add(backRight);

        return parts;
    }

    void onPartDestroyed(World world, PlayerEntity player, BlockState state, BlockPos pos, BlockState partState, BlockPos partPos);

    List<BlockPos> getOtherParts(BlockState state, BlockPos pos);

    default void onMultiblockPlaced(World world, BlockPos basePos, BlockState state) {
        BlockState defaultState = GalacticraftBlocks.SOLAR_PANEL_PART.getDefaultState();
        for (BlockPos otherPart : this.getOtherParts(state, basePos)) {
            world.setBlockState(otherPart, defaultState);

            BlockEntity partEntity = world.getBlockEntity(otherPart);
            assert partEntity != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPartBlockEntity) partEntity).setBasePos(basePos);
            partEntity.markDirty();
        }
    }
}
