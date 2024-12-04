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

package dev.galacticraft.mod.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class SolidCheck implements BlockCheck {
    @Override
    public boolean checkSolid(BlockPos block, ServerLevel world) {
        if (Block.isShapeFullBlock(world.getBlockState(block).getShape(world, block))) {
            return true;
        }
        return false;
    }

    @Override
    public SealerGroupings checkCalculated(BlockPos block, ServerLevel world) {
        if (SealerManager.INSTANCE.getInsideSealerGroupings(block, world.dimensionType()) != null)
        {
            return SealerManager.INSTANCE.getInsideSealerGroupings(block, world.dimensionType());
        } else if (SealerManager.INSTANCE.getOutsideSealerGroupings(block, world.dimensionType()) != null) {
            return SealerManager.INSTANCE.getOutsideSealerGroupings(block, world.dimensionType());
        }
        return null;
    }
}
