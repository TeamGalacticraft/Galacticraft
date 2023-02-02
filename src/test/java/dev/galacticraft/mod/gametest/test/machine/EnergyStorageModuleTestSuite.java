/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.EnergyStorageModuleBlockEntity;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EnergyStorageModuleTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void energyStorageModulePlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.ENERGY_STORAGE_MODULE, GCBlockEntityTypes.ENERGY_STORAGE_MODULE));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void energyStorageModuleChargingTest(GameTestHelper context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GCBlocks.ENERGY_STORAGE_MODULE, GCBlockEntityTypes.ENERGY_STORAGE_MODULE, EnergyStorageModuleBlockEntity.DRAIN_FROM_BATTERY_SLOT);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void energyStorageModuleDrainingTest(GameTestHelper context) {
        this.testItemDraining(context, new BlockPos(0, 0, 0), GCBlocks.ENERGY_STORAGE_MODULE, GCBlockEntityTypes.ENERGY_STORAGE_MODULE, EnergyStorageModuleBlockEntity.CHARGE_TO_BATTERY_SLOT);
    }
}
