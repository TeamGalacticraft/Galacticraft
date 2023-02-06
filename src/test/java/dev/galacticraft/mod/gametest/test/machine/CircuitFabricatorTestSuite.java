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

import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.CircuitFabricatorBlockEntity;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CircuitFabricatorTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void circuitFabricatorPlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.CIRCUIT_FABRICATOR, GCBlockEntityTypes.CIRCUIT_FABRICATOR));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void circuitFabricatorChargeTest(GameTestHelper context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GCMachineTypes.CIRCUIT_FABRICATOR, GCSlotGroupTypes.ENERGY_TO_SELF);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 301)
    public void circuitFabricatorCraftingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var circuitFabricator = this.createBlockEntity(context, pos, GCBlocks.CIRCUIT_FABRICATOR, GCBlockEntityTypes.CIRCUIT_FABRICATOR);
        final var inv = circuitFabricator.itemStorage();
        circuitFabricator.energyStorage().setEnergy(circuitFabricator.energyStorage().getCapacity());
        fillCircuitFabricatorSlots(inv);
        runFinalTaskAt(context, 300 + 1, () -> {
            Item output = inv.getSlot(GCSlotGroupTypes.GENERIC_OUTPUT).getResource();
            if (output != GCItem.BASIC_WAFER) {
                context.fail(String.format("Expected circuit fabricator to have made a wafer but found %s instead!", output), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void circuitFabricatorCraftingFullTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var circuitFabricator = this.createBlockEntity(context, pos, GCBlocks.CIRCUIT_FABRICATOR, GCBlockEntityTypes.CIRCUIT_FABRICATOR);
        final var inv = circuitFabricator.itemStorage();
        circuitFabricator.energyStorage().setEnergy(circuitFabricator.energyStorage().getCapacity());
        inv.getSlot(GCSlotGroupTypes.GENERIC_OUTPUT).set(Items.BARRIER, null, 1);
        fillCircuitFabricatorSlots(inv);
        runFinalTaskNext(context, () -> {
            if (circuitFabricator.getMaxProgress() != 0) {
                context.fail("Expected circuit fabricator to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillCircuitFabricatorSlots(@NotNull MachineItemStorage inv) {
        inv.getSlot(GCSlotGroupTypes.DIAMOND_INPUT).set(Items.DIAMOND, null, 1);
        SlotGroup<Item, ItemStack, ItemResourceSlot> group = inv.getGroup(GCSlotGroupTypes.SILICON_INPUT);
        group.getSlot(0).set(GCItem.RAW_SILICON, null, 1);
        group.getSlot(1).set(GCItem.RAW_SILICON, null, 1);
        inv.getSlot(GCSlotGroupTypes.REDSTONE_INPUT).set(Items.REDSTONE, null, 1);
        inv.getSlot(GCSlotGroupTypes.GENERIC_INPUT).set(Items.REDSTONE_TORCH, null, 1);
    }
}
