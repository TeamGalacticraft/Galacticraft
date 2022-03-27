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

import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.ElectricArcFurnaceBlockEntity;
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
public class ElectricArcFurnaceTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void electricArcFurnacePlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void electricArcFurnaceChargeTest(TestContext context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE, ElectricArcFurnaceBlockEntity.CHARGE_SLOT);
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 81)
    public void electricArcFurnaceBlastingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricArcFurnace = this.createBlockEntity(context, pos, GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE);
        final var inv = electricArcFurnace.itemStorage();
        electricArcFurnace.capacitor().setEnergy(electricArcFurnace.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            fillElectricArcFurnaceSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskAt(context, 80 + 1, () -> {
            ItemStack output = inv.getStack(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1);
            if (output.getItem() != Items.IRON_INGOT && output.getCount() != 2) {
                context.throwPositionedException(String.format("Expected electric arc furnace to have made two iron ingots but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void electricArcFurnaceCraftingFullTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricArcFurnace = this.createBlockEntity(context, pos, GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE);
        final var inv = electricArcFurnace.itemStorage();
        electricArcFurnace.capacitor().setEnergy(electricArcFurnace.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            inv.setStack(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1, new ItemStack(Items.BARRIER), transaction);
            inv.setStack(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_2, new ItemStack(Items.BARRIER), transaction);
            fillElectricArcFurnaceSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskNext(context, () -> {
            if (electricArcFurnace.maxProgress() != 0) {
                context.throwPositionedException("Expected electric arc furnace to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillElectricArcFurnaceSlots(MachineItemStorage inv, TransactionContext transaction) {
        inv.setStack(ElectricArcFurnaceBlockEntity.INPUT_SLOT, new ItemStack(Items.RAW_IRON), transaction);
    }
}
