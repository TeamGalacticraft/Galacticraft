/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.machinelib.api.gametest.TestUtils;
import dev.galacticraft.machinelib.api.gametest.annotation.TestInfo;
import dev.galacticraft.machinelib.api.gametest.annotation.timing.Oneshot;
import dev.galacticraft.machinelib.api.gametest.annotation.type.Machine;
import dev.galacticraft.machinelib.api.gametest.context.MachineTestContext;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity.INPUT_SLOT;

@TestInfo(group = "coal_generator")
public final class CoalGeneratorTestSuite extends MachineGameTest<CoalGeneratorBlockEntity> {
    public CoalGeneratorTestSuite() {
        super(GCBlocks.COAL_GENERATOR);
    }

    @Machine
    @Oneshot
    public Runnable fuelBurning(MachineTestContext<CoalGeneratorBlockEntity> ctx) {
        ctx.setItem(INPUT_SLOT, Items.COAL, 1);
        return () -> ctx.assertSlotEmpty(INPUT_SLOT);
    }

    @Machine
    @Oneshot
    public Runnable fuelConsumption(MachineTestContext<CoalGeneratorBlockEntity> ctx) {
        ctx.setItem(INPUT_SLOT, Items.COAL, 1);
        return () -> ctx.assertNotEquals(0, ctx.be.getFuelLength(), "Expected fuel length >0 from burned fuel");
    }

    @Machine
    @Oneshot(time = 320)
    public Runnable multipleFuelBurning(MachineTestContext<CoalGeneratorBlockEntity> ctx) {
        ctx.setItem(INPUT_SLOT, Items.COAL, 2);
        return () -> ctx.assertSlotEmpty(INPUT_SLOT);
    }

    @Machine
    @Oneshot(time = 250)
    public Runnable heat(MachineTestContext<CoalGeneratorBlockEntity> ctx) {
        ctx.be.setFuelLength(CoalGeneratorBlockEntity.FUEL_MAP.getInt(Items.COAL));
        return () -> ctx.assertEquals(1.0, ctx.be.getHeat(), "Coal generator did not heat up!");
    }

    @Machine
    @Oneshot(time = 50)
    public Runnable cool(MachineTestContext<CoalGeneratorBlockEntity> ctx) {
        ctx.be.setHeat(1.0);
        return () -> ctx.assertEquals(0.0, ctx.be.getHeat(), "Coal generator did not cool down!");
    }

    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = TestUtils.generateTests(this);
        tests.add(this.createDrainToEnergyItemTest(CoalGeneratorBlockEntity.CHARGE_SLOT, GCItems.BATTERY));
        return tests;
    }
}
