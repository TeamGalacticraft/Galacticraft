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

package dev.galacticraft.mod.content.block.machine;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.MultiBlockMachineBlock;
import dev.galacticraft.mod.api.block.MultiBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class SimpleMultiBlockMachineBlock<T extends MachineBlockEntity, P extends BaseEntityBlock> extends MultiBlockMachineBlock<T> {
    private final List<BlockPos> parts;
    private final BlockState partState;

    /**
     * Note: BlockEntity of the partBlock must implement {@link MultiBlockPart}
     */
    public static <T extends MachineBlockEntity, P extends BaseEntityBlock> SimpleMultiBlockMachineBlock<T, P> create(Properties properties, ResourceLocation type, List<BlockPos> parts, P partBlock) {
        return new SimpleMultiBlockMachineBlock<>(properties, type, parts, partBlock);
    }

    protected SimpleMultiBlockMachineBlock(Properties properties, ResourceLocation factory, List<BlockPos> parts, P partBlock) {
        super(properties, factory);
        this.parts = parts;
        this.partState = partBlock.defaultBlockState();
    }

    @Override
    public void onMultiBlockPlaced(Level level, BlockPos blockPos, BlockState blockState) {
        for (BlockPos otherPart : this.getOtherParts(blockState)) {
            otherPart = otherPart.immutable().offset(blockPos);
            level.setBlockAndUpdate(otherPart, this.partState);

            BlockEntity part = level.getBlockEntity(otherPart);
            assert part != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPart) part).setBasePos(blockPos);
            part.setChanged();
        }
    }

    @Override
    public @Unmodifiable List<BlockPos> getOtherParts(BlockState blockState) {
        return this.parts;
    }
}
