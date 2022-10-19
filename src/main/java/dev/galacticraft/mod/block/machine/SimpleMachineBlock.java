/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.machinelib.api.block.MachineBlock;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SimpleMachineBlock<T extends MachineBlockEntity> extends MachineBlock<T> {
    public static final Properties MACHINE_DEFAULT_SETTINGS = FabricBlockSettings.of(Material.METAL)
            .strength(3.0F, 5.0F).sound(SoundType.METAL);

    private final BlockEntityFactory<T> factory;
    private Component information = null;

    @Contract("_ -> new")
    public static <T extends MachineBlockEntity> @NotNull SimpleMachineBlock<T> create(BlockEntityFactory<T> factory) {
        return new SimpleMachineBlock<>(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS), factory);
    }

    protected SimpleMachineBlock(Properties settings, BlockEntityFactory<T> factory) {
        super(settings);
        this.factory = factory;
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

    @FunctionalInterface
    public interface BlockEntityFactory<T extends MachineBlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
