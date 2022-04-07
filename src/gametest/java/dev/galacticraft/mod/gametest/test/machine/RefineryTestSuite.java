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
import dev.galacticraft.impl.fluid.FluidStack;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.block.entity.RefineryBlockEntity;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
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
public class RefineryTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void refineryPlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.REFINERY, GalacticraftBlockEntityType.REFINERY));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void refineryChargingTest(TestContext context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GalacticraftBlock.REFINERY, GalacticraftBlockEntityType.REFINERY, RefineryBlockEntity.CHARGE_SLOT);
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void refineryOilInputTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GalacticraftBlock.REFINERY, GalacticraftBlockEntityType.REFINERY);
        final var inv = refinery.itemStorage();
        try (Transaction transaction = Transaction.openOuter()) {
            inv.setStack(RefineryBlockEntity.FLUID_INPUT_SLOT, new ItemStack(GalacticraftItem.CRUDE_OIL_BUCKET), transaction);
            transaction.commit();
        }
        refinery.energyStorage().setEnergyUnsafe(refinery.getEnergyCapacity());
        runFinalTaskNext(context, () -> {
            ItemStack inputStack = inv.getStack(RefineryBlockEntity.FLUID_INPUT_SLOT);
            if (inputStack.getItem() != Items.BUCKET) {
                context.throwPositionedException(String.format("Expected refinery to return a bucket from fluid transaction but found %s instead!", formatItemStack(inputStack)), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 201)
    public void refineryCraftingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GalacticraftBlock.REFINERY, GalacticraftBlockEntityType.REFINERY);
        final var inv = refinery.itemStorage();
        try (Transaction transaction = Transaction.openOuter()) {
            fillRefinerySlots(inv, transaction);
            transaction.commit();
        }
        refinery.energyStorage().setEnergyUnsafe(refinery.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            refinery.fluidStorage().setStack(RefineryBlockEntity.OIL_TANK, new FluidStack(FluidVariant.of(GalacticraftFluid.CRUDE_OIL), FluidConstants.BUCKET), transaction);
            transaction.commit();
        }
        runFinalTaskAt(context, 200 + 1, () -> {
            long oil = refinery.fluidStorage().getAmount(RefineryBlockEntity.OIL_TANK);
            long fuel = refinery.fluidStorage().getAmount(RefineryBlockEntity.FUEL_TANK);
            if (oil != 0) {
                context.throwPositionedException(String.format("Expected refinery to refine all of the oil but found %s remaining!", oil), pos);
            }
            if (fuel < FluidConstants.BUCKET) {
                context.throwPositionedException(String.format("Expected refinery to refine all of the oil into fuel but it only refined %s!", fuel), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void refineryRefiningFullTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GalacticraftBlock.REFINERY, GalacticraftBlockEntityType.REFINERY);
        final var inv = refinery.itemStorage();
        try (Transaction transaction = Transaction.openOuter()) {
            fillRefinerySlots(inv, transaction);
            transaction.commit();
        }
        refinery.energyStorage().setEnergyUnsafe(refinery.getEnergyCapacity());
        try (Transaction transaction = Transaction.openOuter()) {
            refinery.fluidStorage().setStack(RefineryBlockEntity.OIL_TANK, new FluidStack(FluidVariant.of(GalacticraftFluid.CRUDE_OIL), FluidConstants.BUCKET), transaction);
            refinery.fluidStorage().setStack(RefineryBlockEntity.FUEL_TANK, new FluidStack(FluidVariant.of(GalacticraftFluid.FUEL), FluidConstants.BUCKET), transaction);
            transaction.commit();
        }
        runFinalTaskNext(context, () -> {
            if (refinery.fluidStorage().getAmount(RefineryBlockEntity.OIL_TANK) != FluidConstants.BUCKET) {
                context.throwPositionedException(String.format("Expected refinery to be unable to refine oil as the fuel tank was full!"), pos);
            }
        });
    }

    private static void fillRefinerySlots(MachineItemStorage inv, TransactionContext transaction) {
        inv.setStack(0, new ItemStack(Items.IRON_INGOT), transaction);
        inv.setStack(1, new ItemStack(Items.IRON_INGOT), transaction);
    }
}
