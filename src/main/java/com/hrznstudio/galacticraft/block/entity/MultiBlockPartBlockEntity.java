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
 */

package com.hrznstudio.galacticraft.block.entity;

import com.hrznstudio.galacticraft.api.block.MultiBlockBase;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MultiBlockPartBlockEntity extends BlockEntity {
    public BlockPos basePos = BlockPos.ORIGIN;

    public MultiBlockPartBlockEntity() {
        super(GalacticraftBlockEntities.GENERIC_MULTIBLOCK_PART_TYPE);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.basePos != BlockPos.ORIGIN) {
            tag.putInt("baseX", this.basePos.getX());
            tag.putInt("baseY", this.basePos.getY());
            tag.putInt("baseZ", this.basePos.getZ());
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("baseX")) {
            this.basePos = new BlockPos(tag.getInt("baseX"), tag.getInt("baseY"), tag.getInt("baseZ"));
        }
    }

    public void setBasePos(BlockPos basePos) {
        if (this.world.getBlockState(basePos).getBlock() instanceof MultiBlockBase) {
            this.basePos = basePos;
            this.markDirty();
        }
    }
}