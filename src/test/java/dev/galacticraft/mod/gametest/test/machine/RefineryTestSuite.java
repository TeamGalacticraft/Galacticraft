/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.machinelib.api.gametest.MachineGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.InstantTest;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.RefineryBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class RefineryTestSuite extends MachineGameTest<RefineryBlockEntity> {
    public RefineryTestSuite() {
        super(GCMachineTypes.REFINERY);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> generateTests() {
        List<TestFunction> functions = super.generateTests();
        functions.add(this.createChargeFromEnergyItemTest(RefineryBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return functions;
    }

    @InstantTest
    public Runnable oilItemExtraction(RefineryBlockEntity machine) {
        ItemResourceSlot slot = machine.itemStorage().getSlot(RefineryBlockEntity.OIL_INPUT_SLOT);
        slot.set(GCItems.CRUDE_OIL_BUCKET, 1);
        return () -> {
            if (machine.fluidStorage().getSlot(RefineryBlockEntity.OIL_TANK).getAmount() != FluidConstants.BUCKET) {
                throw new GameTestAssertException("Expected refinery to take 1 bucket of oil from the input slot.");
            }
        };
    }

    @InstantTest
    public Runnable oilItemRemainder(RefineryBlockEntity machine) {
        ItemResourceSlot slot = machine.itemStorage().getSlot(RefineryBlockEntity.OIL_INPUT_SLOT);
        slot.set(GCItems.CRUDE_OIL_BUCKET, 1);
        return () -> {
            if (slot.getResource() != Items.BUCKET) {
                throw new GameTestAssertException("Expected refinery to leave 1 empty bucket in the input slot.");
            }
        };
    }

    @InstantTest
    public Runnable fuelItemInsertion(RefineryBlockEntity machine) {
        FluidResourceSlot fluidSlot = machine.fluidStorage().getSlot(RefineryBlockEntity.FUEL_TANK);
        fluidSlot.set(GCFluids.FUEL, FluidConstants.BUCKET);
        ItemResourceSlot slot = machine.itemStorage().getSlot(RefineryBlockEntity.FUEL_OUTPUT_SLOT);
        slot.set(Items.BUCKET, 1);
        return () -> {
            if (fluidSlot.getAmount() != 0) {
                throw new GameTestAssertException("Expected refinery to extract 1 bucket of fuel from the tank!");
            }
        };
    }

    @InstantTest
    public Runnable fuelItemOutput(RefineryBlockEntity machine) {
        FluidResourceSlot fluidSlot = machine.fluidStorage().getSlot(RefineryBlockEntity.FUEL_TANK);
        fluidSlot.set(GCFluids.FUEL, FluidConstants.BUCKET);
        ItemResourceSlot slot = machine.itemStorage().getSlot(RefineryBlockEntity.FUEL_OUTPUT_SLOT);
        slot.set(Items.BUCKET, 1);
        return () -> {
            if (slot.getResource() != GCItems.FUEL_BUCKET) {
                throw new GameTestAssertException("Expected refinery to create a fuel bucket in the output slot.");
            }
        };
    }

    @InstantTest
    public Runnable crafting(RefineryBlockEntity machine) {
        FluidResourceSlot fuel = machine.fluidStorage().getSlot(RefineryBlockEntity.FUEL_TANK);
        FluidResourceSlot oil = machine.fluidStorage().getSlot(RefineryBlockEntity.OIL_TANK);
        oil.set(GCFluids.CRUDE_OIL, FluidConstants.BUCKET);
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
        return () -> {
            if (fuel.isEmpty() || oil.getAmount() >= FluidConstants.BUCKET) {
                throw new GameTestAssertException("Expected refinery to refine oil!");
            }
        };
    }

    @InstantTest
    public Runnable craftingFailFull(RefineryBlockEntity machine) {
        FluidResourceSlot fuel = machine.fluidStorage().getSlot(RefineryBlockEntity.FUEL_TANK);
        FluidResourceSlot oil = machine.fluidStorage().getSlot(RefineryBlockEntity.OIL_TANK);
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
        fuel.set(GCFluids.FUEL, fuel.getCapacity());
        oil.set(GCFluids.CRUDE_OIL, FluidConstants.BUCKET);
        return () -> {
            if (oil.getAmount() < FluidConstants.BUCKET) {
                throw new GameTestAssertException("Expected refinery to not refine oil!");
            }
        };
    }
}
