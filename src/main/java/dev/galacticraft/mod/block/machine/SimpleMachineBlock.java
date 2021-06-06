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
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SimpleMachineBlock<T extends MachineBlockEntity> extends MachineBlock<T> {
    public static final Settings MACHINE_DEFAULT_SETTINGS = FabricBlockSettings.of(Material.METAL)
            .strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL);

    private final BlockEntityFactory<T> factory;
    private final Text information;

    public static <T extends MachineBlockEntity> SimpleMachineBlock<T> create(BlockEntityFactory<T> factory, String key) {
        return new SimpleMachineBlock<>(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS), factory, new TranslatableText(key).setStyle(Constant.Text.DARK_GRAY_STYLE));
    }

    protected SimpleMachineBlock(Settings settings, BlockEntityFactory<T> factory, Text information) {
        super(settings);
        this.factory = factory;
        this.information = information;
    }

    @Override
    public T createBlockEntity(BlockPos pos, BlockState state) {
        return this.factory.create(pos, state);
    }

    @Override
    public Text machineInfo(ItemStack stack, BlockView view, boolean advanced) {
        return this.information;
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends MachineBlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
