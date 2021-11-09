/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.EnergyStorageModuleBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EnergyStorageModuleTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void energyStorageModulePlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.ENERGY_STORAGE_MODULE, GalacticraftBlockEntityType.ENERGY_STORAGE_MODULE));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void energyStorageModuleChargingTest(TestContext context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GalacticraftBlock.ENERGY_STORAGE_MODULE, GalacticraftBlockEntityType.ENERGY_STORAGE_MODULE, EnergyStorageModuleBlockEntity.DRAIN_FROM_BATTERY_SLOT);
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void energyStorageModuleDrainingTest(TestContext context) {
        this.testItemDraining(context, new BlockPos(0, 0, 0), GalacticraftBlock.ENERGY_STORAGE_MODULE, GalacticraftBlockEntityType.ENERGY_STORAGE_MODULE, EnergyStorageModuleBlockEntity.CHARGE_TO_BATTERY_SLOT);
    }
}
