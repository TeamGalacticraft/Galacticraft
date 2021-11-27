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

package dev.galacticraft.mod.gametest.test.machine;

import dev.galacticraft.mod.lookup.storage.MachineItemStorage;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.ElectricFurnaceBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
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
public class ElectricFurnaceTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void electricFurnacePlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.ELECTRIC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_FURNACE));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void electricFurnaceChargeTest(TestContext context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GalacticraftBlock.ELECTRIC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_FURNACE, ElectricFurnaceBlockEntity.CHARGE_SLOT);
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 201)
    public void electricFurnaceCraftingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricFurnace = this.createBlockEntity(context, pos, GalacticraftBlock.ELECTRIC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_FURNACE);
        final var inv = electricFurnace.itemStorage();
        electricFurnace.capacitor().setEnergy(electricFurnace.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            fillElectricFurnaceSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskAt(context, 200 + 1, () -> {
            ItemStack output = inv.getStack(ElectricFurnaceBlockEntity.OUTPUT_SLOT);
            if (output.getItem() != Items.COOKED_PORKCHOP) {
                context.throwPositionedException(String.format("Expected electric furnace to have made a cooked porkchop but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void electricFurnaceCraftingFullTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricFurnace = this.createBlockEntity(context, pos, GalacticraftBlock.ELECTRIC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_FURNACE);
        final var inv = electricFurnace.itemStorage();
        electricFurnace.capacitor().setEnergy(electricFurnace.getEnergyCapacity());

        try (Transaction transaction = Transaction.openOuter()) {
            inv.setStack(ElectricFurnaceBlockEntity.OUTPUT_SLOT, new ItemStack(Items.BARRIER), transaction);
            fillElectricFurnaceSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskNext(context, () -> {
            if (electricFurnace.maxProgress() != 0) {
                context.throwPositionedException("Expected electric furnace to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillElectricFurnaceSlots(MachineItemStorage inv, TransactionContext transaction) {
        inv.setStack(ElectricFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.PORKCHOP), transaction);
    }
}
