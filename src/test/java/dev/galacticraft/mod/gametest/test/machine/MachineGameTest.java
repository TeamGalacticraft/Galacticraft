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

import dev.galacticraft.api.block.MachineBlock;
import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface MachineGameTest extends GalacticraftGameTest {
    default <T extends MachineBlockEntity, B extends MachineBlock<T>> @NotNull T createBlockEntity(@NotNull GameTestHelper context, BlockPos pos, B block, BlockEntityType<T> type) {
        context.setBlock(pos, block);
        final var blockEntity = context.getBlockEntity(pos);
        if (blockEntity == null) {
            context.fail(String.format("Expected a '%s' block entity, but found 'null'!", Registry.BLOCK_ENTITY_TYPE.getKey(type)), pos);
        } else if (blockEntity.getType() != type) {
            context.fail(String.format("Expected a '%s' block entity, but found '%s'!", Registry.BLOCK_ENTITY_TYPE.getKey(type), Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType())), pos);
        }
        return (T) blockEntity;
    }

    default <T extends MachineBlockEntity, B extends MachineBlock<T>> void testItemCharging(GameTestHelper context, BlockPos pos, B block, BlockEntityType<T> type, int slot) {
        T machine = this.createBlockEntity(context, pos, block, type);
        machine.itemStorage().setSlotUnsafe(slot, ItemVariant.of(GalacticraftItem.INFINITE_BATTERY), 1, true);
        runFinalTaskNext(context, () -> {
            if (machine.energyStorage().getAmount() <= 0) {
                context.fail(String.format("Expected %s to charge from an item, but found %s energy!", Registry.BLOCK_ENTITY_TYPE.getKey(type), machine.energyStorage().getAmount()), pos);
            }
        });
    }

    default <T extends MachineBlockEntity, B extends MachineBlock<T>> void testItemDraining(GameTestHelper context, BlockPos pos, B block, BlockEntityType<T> type, int slot) {
        T machine = this.createBlockEntity(context, pos, block, type);
        machine.energyStorage().setEnergyUnsafe(machine.energyStorage().getCapacity());
        machine.itemStorage().setSlotUnsafe(slot, ItemVariant.of(GalacticraftItem.BATTERY), 1, true);
        runFinalTaskNext(context, () -> {
            if (machine.energyStorage().getAmount() >= machine.energyStorage().getCapacity()) {
                context.fail(String.format("Expected %s to drain power to an item, but it was still at max energy!", Registry.BLOCK_ENTITY_TYPE.getKey(type)), pos);
            }
        });
    }
}
