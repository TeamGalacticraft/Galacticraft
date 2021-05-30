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

package dev.galacticraft.mod.block.machine;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MultiBlockMachineBlock;
import dev.galacticraft.mod.api.block.MultiBlockPart;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SimpleMultiBlockMachineBlock<T extends MachineBlockEntity, P extends BlockWithEntity> extends MultiBlockMachineBlock<T> {
    private final List<BlockPos> parts;
    private final SimpleMachineBlock.BlockEntityFactory<T> factory;
    private final Text information;
    private final BlockState partState;

    /**
     * Note: BlockEntity of the partBlock must implement {@link MultiBlockPart}
     */
    public static <T extends MachineBlockEntity, P extends BlockWithEntity> SimpleMultiBlockMachineBlock<T, P> create(SimpleMachineBlock.BlockEntityFactory<T> type, List<BlockPos> parts, P partBlock, String key) {
        return new SimpleMultiBlockMachineBlock<>(FabricBlockSettings.copyOf(SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS), parts, type, partBlock, new TranslatableText(key).setStyle(Constant.Text.DARK_GRAY_STYLE));
    }

    protected SimpleMultiBlockMachineBlock(Settings settings, List<BlockPos> parts, SimpleMachineBlock.BlockEntityFactory<T> factory, P partBlock, Text information) {
        super(settings);
        this.parts = parts;
        this.factory = factory;
        this.information = information;
        this.partState = partBlock.getDefaultState();
    }

    @Override
    public T createBlockEntity(BlockPos pos, BlockState state) {
        return this.factory.create(pos, state);
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, boolean advanced) {
        return this.information;
    }

    @Override
    public void onMultiBlockPlaced(World world, BlockPos pos, BlockState state) {
        for (BlockPos otherPart : this.getOtherParts(state)) {
            otherPart = otherPart.toImmutable().add(pos);
            world.setBlockState(otherPart, this.partState);

            BlockEntity part = world.getBlockEntity(otherPart);
            assert part != null; // This will never be null because world.setBlockState will put a blockentity there.
            ((MultiBlockPart) part).setBasePos(pos);
            part.markDirty();
        }
    }

    @Override
    public @Unmodifiable List<BlockPos> getOtherParts(BlockState state) {
        return this.parts;
    }
}
