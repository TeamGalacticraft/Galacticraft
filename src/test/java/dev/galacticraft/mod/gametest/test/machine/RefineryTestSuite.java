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

import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.RefineryBlockEntity;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class RefineryTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void refineryPlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void refineryChargingTest(GameTestHelper context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GCMachineTypes.REFINERY, GCSlotGroupTypes.ENERGY_TO_SELF);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void refineryOilInputTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY);
        final var inv = refinery.itemStorage();
        ItemResourceSlot slot = inv.getSlot(GCSlotGroupTypes.OIL_FILL);
        slot.set(GCItem.CRUDE_OIL_BUCKET, null, 1);
        refinery.energyStorage().setEnergy(refinery.energyStorage().getCapacity());
        runFinalTaskNext(context, () -> {
            if (slot.getResource() != Items.BUCKET || slot.getAmount() != 1) {
                context.fail(String.format("Expected refinery to return a bucket from fluid transaction but found %s instead!", formatItem(slot.getResource(), slot.getAmount())), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 201)
    public void refineryCraftingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY);
        refinery.energyStorage().setEnergy(refinery.energyStorage().getCapacity());
        FluidResourceSlot oilInput = refinery.fluidStorage().getSlot(GCSlotGroupTypes.OIL_INPUT);
        FluidResourceSlot fuelOutput = refinery.fluidStorage().getSlot(GCSlotGroupTypes.FUEL_OUTPUT);
        oilInput.set(GCFluids.CRUDE_OIL, null, FluidConstants.BUCKET);
        runFinalTaskAt(context, 200 + 1, () -> {
            long oil = oilInput.getAmount();
            long fuel = fuelOutput.getAmount();
            if (oil != 0) {
                context.fail(String.format("Expected refinery to refine all of the oil but found %s remaining!", oil), pos);
            }
            if (fuel < FluidConstants.BUCKET) {
                context.fail(String.format("Expected refinery to refine all of the oil into fuel but it only refined %s!", fuel), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void refineryRefiningFullTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY);
        refinery.energyStorage().setEnergy(refinery.energyStorage().getCapacity());
        FluidResourceSlot oilInput = refinery.fluidStorage().getSlot(GCSlotGroupTypes.OIL_INPUT);
        FluidResourceSlot fuelOutput = refinery.fluidStorage().getSlot(GCSlotGroupTypes.FUEL_OUTPUT);
        oilInput.set(GCFluids.CRUDE_OIL, null, FluidConstants.BUCKET);
        fuelOutput.set(GCFluids.FUEL, null, RefineryBlockEntity.MAX_CAPACITY);
        runFinalTaskNext(context, () -> {
            if (oilInput.getAmount() != FluidConstants.BUCKET) {
                context.fail("Expected refinery to be unable to refine oil as the fuel tank was full!", pos);
            }
        });
    }
}
