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

package dev.galacticraft.mod.gametest.test.machine;

import alexiil.mc.lib.attributes.Simulation;
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface MachineGameTest extends GalacticraftGameTest {
    default <T extends MachineBlockEntity, B extends MachineBlock<T>> @NotNull T createBlockEntity(@NotNull TestContext context, BlockPos pos, B block, BlockEntityType<T> type) {
        context.setBlockState(pos, block);
        final var blockEntity = context.getBlockEntity(pos);
        if (blockEntity == null) {
            context.throwPositionedException(String.format("Expected a '%s' block entity, but found 'null'!", Registry.BLOCK_ENTITY_TYPE.getId(type)), pos);
        } else if (blockEntity.getType() != type) {
            context.throwPositionedException(String.format("Expected a '%s' block entity, but found '%s'!", Registry.BLOCK_ENTITY_TYPE.getId(type), Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType())), pos);
        }
        return (T) blockEntity;
    }

    default <T extends MachineBlockEntity, B extends MachineBlock<T>> void testItemCharging(TestContext context, BlockPos pos, B block, BlockEntityType<T> type, int slot) {
        T machine = this.createBlockEntity(context, pos, block, type);
        machine.itemInv().setInvStack(slot, new ItemStack(GalacticraftItem.INFINITE_BATTERY), Simulation.ACTION);
        runFinalTaskNext(context, () -> {
            if (machine.capacitor().getEnergy() <= 0) {
                context.throwPositionedException(String.format("Expected %s to charge from an item, but found %s energy!", Registry.BLOCK_ENTITY_TYPE.getId(type), machine.capacitor().getEnergy()), pos);
            }
        });
    }

    default <T extends MachineBlockEntity, B extends MachineBlock<T>> void testItemDraining(TestContext context, BlockPos pos, B block, BlockEntityType<T> type, int slot) {
        T machine = this.createBlockEntity(context, pos, block, type);
        machine.capacitor().setEnergy(machine.capacitor().getMaxCapacity());
        machine.itemInv().setInvStack(slot, new ItemStack(GalacticraftItem.BATTERY), Simulation.ACTION);
        runFinalTaskNext(context, () -> {
            if (machine.capacitor().getEnergy() >= machine.capacitor().getMaxCapacity()) {
                context.throwPositionedException(String.format("Expected %s to drain power to an item, but it was still at max energy!", Registry.BLOCK_ENTITY_TYPE.getId(type)), pos);
            }
        });
    }
}
