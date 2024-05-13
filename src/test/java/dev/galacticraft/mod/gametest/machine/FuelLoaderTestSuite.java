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
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.FuelLoaderBlockEntity;
import dev.galacticraft.mod.content.block.entity.machine.RefineryBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@TestSuite("fuel_loader")
public final class FuelLoaderTestSuite extends MachineGameTest<FuelLoaderBlockEntity> {
    public FuelLoaderTestSuite() {
        super(GCMachineTypes.FUEL_LOADER);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createChargeFromEnergyItemTest(RefineryBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        tests.add(this.createTakeFromFluidItemTest(FuelLoaderBlockEntity.FUEL_INPUT_SLOT, GCItems.FUEL_BUCKET, FuelLoaderBlockEntity.FUEL_TANK));
        return tests;
    }
}
