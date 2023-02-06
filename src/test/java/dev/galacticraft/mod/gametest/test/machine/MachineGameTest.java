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

package dev.galacticraft.mod.gametest.test.machine;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineType;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroupType;
import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface MachineGameTest extends GalacticraftGameTest {
    default <T extends MachineBlockEntity> @NotNull T createBlockEntity(@NotNull GameTestHelper context, BlockPos pos, Block block, BlockEntityType<T> type) {
        context.setBlock(pos, block);
        final var blockEntity = context.getBlockEntity(pos);
        if (blockEntity == null) {
            context.fail(String.format("Expected a '%s' block entity, but found 'null'!", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)), pos);
        } else if (blockEntity.getType() != type) {
            context.fail(String.format("Expected a '%s' block entity, but found '%s'!", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type), BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType())), pos);
        }
        assert blockEntity != null;
        return (T) blockEntity;
    }

    default <T extends MachineBlockEntity> void testItemCharging(GameTestHelper context, BlockPos pos, MachineType<T, ? extends MachineMenu<T>> type, SlotGroupType slotType) {
        T machine = this.createBlockEntity(context, pos, type.getBlock(), type.getBlockEntityType());
        machine.itemStorage().getSlot(slotType).set(GCItem.INFINITE_BATTERY, null, 1);
        runFinalTaskNext(context, () -> {
            if (machine.energyStorage().getAmount() <= 0) {
                context.fail(String.format("Expected %s to charge from an item, but found %s energy!", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type.getBlockEntityType()), machine.energyStorage().getAmount()), pos);
            }
        });
    }

    default <T extends MachineBlockEntity> void testItemDraining(GameTestHelper context, BlockPos pos, MachineType<T, ? extends MachineMenu<T>> type, SlotGroupType slotType) {
        T machine = this.createBlockEntity(context, pos, type.getBlock(), type.getBlockEntityType());
        machine.energyStorage().setEnergy(machine.energyStorage().getCapacity());
        machine.itemStorage().getSlot(slotType).set(GCItem.BATTERY, null, 1);
        runFinalTaskNext(context, () -> {
            if (machine.energyStorage().getAmount() >= machine.energyStorage().getCapacity()) {
                context.fail(String.format("Expected %s to drain power to an item, but it was still at max energy (%s)!", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type.getBlockEntityType()), machine.energyStorage().getAmount()), pos);
            }
        });
    }
}
