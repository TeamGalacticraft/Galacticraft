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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.mod.content.block.machine.ResourceStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Range;

public abstract class ResourceStorageBlockEntity extends MachineBlockEntity {
    private int amount = 0;

    protected ResourceStorageBlockEntity(BlockEntityType<? extends MachineBlockEntity> type, BlockPos pos, BlockState state, StorageSpec spec) {
        super(type, pos, state, spec);
    }

    @Range(from = 0, to = 8)
    protected abstract int calculateAmount();

    @Override
    public void setChanged() {
        super.setChanged();

        int newAmount = this.calculateAmount();
        if (this.amount != newAmount && this.level != null && !this.level.isClientSide) {
            this.amount = newAmount;
            this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ResourceStorageBlock.AMOUNT, this.amount), 2);
        }
    }
}
