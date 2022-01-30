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
import dev.galacticraft.mod.block.entity.CompressorBlockEntity;
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
public class CompressorTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void compressorPlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.COMPRESSOR, GalacticraftBlockEntityType.COMPRESSOR));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 2)
    public void compressorFuelingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var compressor = this.createBlockEntity(context, pos, GalacticraftBlock.COMPRESSOR, GalacticraftBlockEntityType.COMPRESSOR);
        final var inv = compressor.itemStorage();
        inv.setStack(CompressorBlockEntity.FUEL_INPUT_SLOT, new ItemStack(Items.COAL));
        runNext(context, () -> {
            ItemStack stack = inv.getStack(CompressorBlockEntity.FUEL_INPUT_SLOT);
            if (stack.isEmpty() || stack.getItem() != Items.COAL || stack.getCount() != 1) {
                context.throwPositionedException(String.format("Expected compressor inventory to be have 1 coal but found %s!", formatItemStack(stack)), pos);
            }
            try (Transaction transaction = Transaction.openOuter()) {
                fillCompressorSlots(inv, transaction);
                transaction.commit();
            }
            runFinalTaskNext(context, () -> {
                if (compressor.fuelLength == 0) {
                    context.throwPositionedException("Expected compressor inventory to be burning fuel!", pos);
                }
            });
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 201)
    public void compressorCraftingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var compressor = this.createBlockEntity(context, pos, GalacticraftBlock.COMPRESSOR, GalacticraftBlockEntityType.COMPRESSOR);
        final var inv = compressor.itemStorage();
        try (Transaction transaction = Transaction.openOuter()) {
            fillCompressorSlots(inv, transaction);
            transaction.commit();
        }
        compressor.fuelTime = compressor.fuelLength = 1000;
        runFinalTaskAt(context, 200 + 1, () -> {
            ItemStack output = inv.getStack(CompressorBlockEntity.OUTPUT_SLOT);
            if (output.getItem() != GalacticraftItem.COMPRESSED_IRON) {
                context.throwPositionedException(String.format("Expected compressor to have made compressed iron but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void compressorCraftingFullTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var compressor = this.createBlockEntity(context, pos, GalacticraftBlock.COMPRESSOR, GalacticraftBlockEntityType.COMPRESSOR);
        final var inv = compressor.itemStorage();
        compressor.fuelTime = compressor.fuelLength = 1000;
        try (Transaction transaction = Transaction.openOuter()) {
            inv.setStack(CompressorBlockEntity.OUTPUT_SLOT, new ItemStack(Items.BARRIER), transaction);
            fillCompressorSlots(inv, transaction);
            transaction.commit();
        }
        runFinalTaskNext(context, () -> {
            if (compressor.maxProgress() != 0) {
                context.throwPositionedException("Expected compressor to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillCompressorSlots(MachineItemStorage inv, TransactionContext transaction) {
        inv.setStack(0, new ItemStack(Items.IRON_INGOT), transaction);
        inv.setStack(1, new ItemStack(Items.IRON_INGOT), transaction);
    }
}
