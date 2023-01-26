/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MultiBlockMachineBlock;
import dev.galacticraft.mod.api.block.MultiBlockPart;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SimpleMultiBlockMachineBlock<T extends MachineBlockEntity, P extends BaseEntityBlock> extends MultiBlockMachineBlock<T> {
    private final List<BlockPos> parts;
    private final SimpleMachineBlock.BlockEntityFactory<T> factory;
    private Component information = null;
    private final BlockState partState;

    /**
     * Note: BlockEntity of the partBlock must implement {@link MultiBlockPart}
     */
    public static <T extends MachineBlockEntity, P extends BaseEntityBlock> SimpleMultiBlockMachineBlock<T, P> create(SimpleMachineBlock.BlockEntityFactory<T> type, List<BlockPos> parts, P partBlock) {
        return new SimpleMultiBlockMachineBlock<>(FabricBlockSettings.copyOf(SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS), parts, type, partBlock);
    }

    protected SimpleMultiBlockMachineBlock(Properties settings, List<BlockPos> parts, SimpleMachineBlock.BlockEntityFactory<T> factory, P partBlock) {
        super(settings);
        this.parts = parts;
        this.factory = factory;
        this.partState = partBlock.defaultBlockState();
    }

    @Override
    public T newBlockEntity(BlockPos pos, BlockState state) {
        return this.factory.create(pos, state);
    }

    @Override
    public Component machineDescription(ItemStack stack, BlockGetter view, boolean advanced) {
        if (this.information == null) {
            this.information = Component.translatable(this.getDescriptionId() + ".description").setStyle(Constant.Text.Color.DARK_GRAY_STYLE);
        }
        return this.information;
    }

    @Override
    public void onMultiBlockPlaced(Level world, BlockPos pos, BlockState state) {
        for (BlockPos otherPart : this.getOtherParts(state)) {
            otherPart = otherPart.immutable().offset(pos);
            world.setBlockAndUpdate(otherPart, this.partState);

            BlockEntity part = world.getBlockEntity(otherPart);
            assert part != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPart) part).setBasePos(pos);
            part.setChanged();
        }
    }

    @Override
    public @Unmodifiable List<BlockPos> getOtherParts(BlockState state) {
        return this.parts;
    }
}
