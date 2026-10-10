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
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.gametest.GalacticraftGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@TestSuite("oxygen_sealer")
public final class OxygenSealerTestSuite extends MachineGameTest<OxygenSealerBlockEntity> implements GalacticraftGameTest {
    public OxygenSealerTestSuite() {
        super(GCBlocks.OXYGEN_SEALER);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createChargeFromEnergyItemTest(OxygenSealerBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        tests.add(this.createTakeFromFluidItemTest(OxygenSealerBlockEntity.OXYGEN_INPUT_SLOT, GCItems.INFINITE_OXYGEN_TANK, OxygenSealerBlockEntity.OXYGEN_TANK));
        return tests;
    }

    @GameTest(batch = "nonBreathable", template = EMPTY_ROOM)
    public void notExtinguishedInSealedRoomTest(GameTestHelper context) {
        final var sealerPos = new BlockPos(1, 2, 1);
        final var torchPos = new BlockPos(3, 2, 3);

        context.setBlock(sealerPos, GCBlocks.OXYGEN_SEALER.defaultBlockState());
        OxygenSealerBlockEntity blockEntity = context.getBlockEntity(sealerPos);
        blockEntity.itemStorage().slot(OxygenSealerBlockEntity.CHARGE_SLOT).set(GCItems.INFINITE_BATTERY, 1);
        blockEntity.itemStorage().slot(OxygenSealerBlockEntity.OXYGEN_INPUT_SLOT).set(GCItems.INFINITE_OXYGEN_TANK, 1);

        // The room should be sealed by the time this runs
        context.runAfterDelay(OxygenSealerBlockEntity.SEAL_CHECK_TIME, () -> context.setBlock(torchPos, Blocks.TORCH.defaultBlockState()));

        // Another oxygen sealer check should have been performed since the torch was placed
        context.runAfterDelay(OxygenSealerBlockEntity.SEAL_CHECK_TIME * 2, () -> context.succeedWhenBlockPresent(Blocks.TORCH, torchPos));
    }
}
