/*
 * Copyright (c) 2019 HRZN LTD
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

import com.hrznstudio.galacticraft.block.machines.BasicSolarPanelBlock;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelPartBlockEntity extends BlockEntity {
    public BlockPos basePos;

    public BasicSolarPanelPartBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_PART_TYPE);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        CompoundTag baseTag = new CompoundTag();
        baseTag.putInt("X", this.basePos.getX());
        baseTag.putInt("Y", this.basePos.getY());
        baseTag.putInt("Z", this.basePos.getZ());

        tag.put("Base", baseTag);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        CompoundTag base = tag.getCompound("Base");
        this.basePos = new BlockPos(base.getInt("X"), base.getInt("Y"), base.getInt("Z"));
    }

    public boolean setBasePos(BlockPos basePos) {
        if (this.world.getBlockState(basePos).getBlock() instanceof BasicSolarPanelBlock) {
            this.basePos = basePos;
            this.markDirty();
            return true;
        } else {
            return false;
        }
    }
}