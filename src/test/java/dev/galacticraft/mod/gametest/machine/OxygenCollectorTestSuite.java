/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.OxygenCollectorBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.gametest.GalacticraftGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@TestSuite("oxygen_collector")
public final class OxygenCollectorTestSuite extends MachineGameTest<OxygenCollectorBlockEntity> implements GalacticraftGameTest {
    public OxygenCollectorTestSuite() {
        super(GCBlocks.OXYGEN_COLLECTOR);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createChargeFromEnergyItemTest(OxygenCollectorBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return tests;
    }

    @MachineTest(batch = "breathable")
    public Runnable collectOxygenFromAtmosphere(OxygenCollectorBlockEntity machine) {
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
        FluidResourceSlot oxygen = machine.fluidStorage().slot(OxygenCollectorBlockEntity.OXYGEN_TANK);

        return () -> Assertions.assertFalse(oxygen.isEmpty(), "Expected oxygen collector to produce oxygen in a breathable atmosphere!");
    }

    @MachineTest(batch = "nonBreathable")
    public Runnable dontCollectOxygenFromAtmosphere(OxygenCollectorBlockEntity machine) {
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
        FluidResourceSlot oxygen = machine.fluidStorage().slot(OxygenCollectorBlockEntity.OXYGEN_TANK);

        return () -> Assertions.assertTrue(oxygen.isEmpty(), "Expected oxygen collector to not produce any oxygen in a non-breathable atmosphere!");
    }

    @MachineTest(batch = "nonBreathable")
    public Runnable collectOxygenFromLeaves(OxygenCollectorBlockEntity machine, GameTestHelper helper) {
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
        FluidResourceSlot oxygen = machine.fluidStorage().slot(OxygenCollectorBlockEntity.OXYGEN_TANK);

        BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, false);
        for (BlockPos pos : BlockPos.betweenClosed(0, 1, 0, 2, 3, 2)) {
            if (helper.getBlockState(pos).is(Blocks.AIR)) {
                helper.setBlock(pos, leaves);
            }
        }

        return () -> Assertions.assertFalse(oxygen.isEmpty(), "Expected oxygen collector to produce oxygen from nearby leaves!");
    }
}
