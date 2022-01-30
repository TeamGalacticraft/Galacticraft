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

import dev.galacticraft.mod.lookup.storage.MachineItemStorage;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.CircuitFabricatorBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CircuitFabricatorTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void circuitFabricatorPlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.CIRCUIT_FABRICATOR, GalacticraftBlockEntityType.CIRCUIT_FABRICATOR));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void circuitFabricatorChargeTest(TestContext context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GalacticraftBlock.CIRCUIT_FABRICATOR, GalacticraftBlockEntityType.CIRCUIT_FABRICATOR, CircuitFabricatorBlockEntity.CHARGE_SLOT);
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 301)
    public void circuitFabricatorCraftingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var circuitFabricator = this.createBlockEntity(context, pos, GalacticraftBlock.CIRCUIT_FABRICATOR, GalacticraftBlockEntityType.CIRCUIT_FABRICATOR);
        final var inv = circuitFabricator.itemStorage();
        circuitFabricator.capacitor().setEnergy(circuitFabricator.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            fillCircuitFabricatorSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskAt(context, 300 + 1, () -> {
            ItemStack output = inv.getStack(CircuitFabricatorBlockEntity.OUTPUT_SLOT);
            if (output.getItem() != GalacticraftItem.BASIC_WAFER) {
                context.throwPositionedException(String.format("Expected circuit fabricator to have made a wafer but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void circuitFabricatorCraftingFullTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var circuitFabricator = this.createBlockEntity(context, pos, GalacticraftBlock.CIRCUIT_FABRICATOR, GalacticraftBlockEntityType.CIRCUIT_FABRICATOR);
        final var inv = circuitFabricator.itemStorage();
        circuitFabricator.capacitor().setEnergy(circuitFabricator.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            inv.setStack(CircuitFabricatorBlockEntity.OUTPUT_SLOT, new ItemStack(Items.BARRIER), transaction);
            fillCircuitFabricatorSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskNext(context, () -> {
            if (circuitFabricator.maxProgress() != 0) {
                context.throwPositionedException("Expected circuit fabricator to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillCircuitFabricatorSlots(MachineItemStorage inv, TransactionContext transaction) {
        inv.setStack(CircuitFabricatorBlockEntity.INPUT_SLOT_DIAMOND, new ItemStack(Items.DIAMOND), transaction);
        inv.setStack(CircuitFabricatorBlockEntity.INPUT_SLOT_SILICON, new ItemStack(GalacticraftItem.RAW_SILICON), transaction);
        inv.setStack(CircuitFabricatorBlockEntity.INPUT_SLOT_SILICON_2, new ItemStack(GalacticraftItem.RAW_SILICON), transaction);
        inv.setStack(CircuitFabricatorBlockEntity.INPUT_SLOT_REDSTONE, new ItemStack(Items.REDSTONE), transaction);
        inv.setStack(CircuitFabricatorBlockEntity.INPUT_SLOT, new ItemStack(Items.REDSTONE_TORCH), transaction);
    }
}
