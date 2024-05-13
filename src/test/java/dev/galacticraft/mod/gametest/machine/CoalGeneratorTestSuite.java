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

package dev.galacticraft.mod.gametest.machine;

import dev.galacticraft.machinelib.api.gametest.MachineGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.MachineTest;
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@TestSuite("coal_generator")
public final class CoalGeneratorTestSuite extends MachineGameTest<CoalGeneratorBlockEntity> {
    public CoalGeneratorTestSuite() {
        super(GCMachineTypes.COAL_GENERATOR);
    }

    @MachineTest
    public Runnable fuelBurning(CoalGeneratorBlockEntity machine) {
        ItemResourceSlot slot = machine.itemStorage().getSlot(CoalGeneratorBlockEntity.INPUT_SLOT);
        slot.set(Items.COAL, 1);
        return () -> Assertions.assertTrue(slot.isEmpty(), "Failed to consume fuel");
    }

    @MachineTest
    public Runnable fuelConsumption(CoalGeneratorBlockEntity machine) {
        ItemResourceSlot slot = machine.itemStorage().getSlot(CoalGeneratorBlockEntity.INPUT_SLOT);

        slot.set(Items.COAL, 1);
        return () -> Assertions.assertNotEquals(0, machine.getFuelLength(), "Failed to burn fuel");
    }

    @MachineTest(workTime = 320)
    public Runnable multipleFuelBurning(CoalGeneratorBlockEntity machine) {
        ItemResourceSlot slot = machine.itemStorage().getSlot(CoalGeneratorBlockEntity.INPUT_SLOT);

        slot.set(Items.COAL, 2);
        return () -> Assertions.assertTrue(slot.isEmpty(), "Failed to consume two coals!");
    }

    @MachineTest(workTime = 250)
    public Runnable heat(CoalGeneratorBlockEntity machine) {
        machine.setFuelLength(CoalGeneratorBlockEntity.FUEL_MAP.getInt(Items.COAL));
        return () -> Assertions.assertEquals(1, machine.getHeat(), "Coal generator did not heat up!");
    }

    @MachineTest(workTime = 50)
    public Runnable cool(CoalGeneratorBlockEntity machine) {
        machine.setHeat(1.0);
        return () -> Assertions.assertEquals(0.0, machine.getHeat(), "Coal generator did not cool down!");
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createDrainToEnergyItemTest(CoalGeneratorBlockEntity.CHARGE_SLOT, GCItems.BATTERY));
        return tests;
    }
}
