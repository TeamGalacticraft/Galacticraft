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

import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.block.entity.RefineryBlockEntity;
import dev.galacticraft.mod.fluid.GCFluid;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.item.GCItem;
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
        this.testItemCharging(context, new BlockPos(0, 0, 0), GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY, RefineryBlockEntity.CHARGE_SLOT);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void refineryOilInputTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY);
        final var inv = refinery.itemStorage();
        inv.setSlotUnsafe(RefineryBlockEntity.FLUID_INPUT_SLOT, ItemVariant.of(GCItem.CRUDE_OIL_BUCKET), 1, true);
        refinery.energyStorage().setEnergyUnsafe(refinery.getEnergyCapacity());
        runFinalTaskNext(context, () -> {
            ItemStack inputStack = inv.getStack(RefineryBlockEntity.FLUID_INPUT_SLOT);
            if (inputStack.getItem() != Items.BUCKET) {
                context.fail(String.format("Expected refinery to return a bucket from fluid transaction but found %s instead!", formatItemStack(inputStack)), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 201)
    public void refineryCraftingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var refinery = this.createBlockEntity(context, pos, GCBlocks.REFINERY, GCBlockEntityTypes.REFINERY);
        refinery.energyStorage().setEnergyUnsafe(refinery.getEnergyCapacity());
        refinery.fluidStorage().setSlotUnsafe(RefineryBlockEntity.OIL_TANK, FluidVariant.of(GCFluid.CRUDE_OIL), FluidConstants.BUCKET, true);
        runFinalTaskAt(context, 200 + 1, () -> {
            long oil = refinery.fluidStorage().getAmount(RefineryBlockEntity.OIL_TANK);
            long fuel = refinery.fluidStorage().getAmount(RefineryBlockEntity.FUEL_TANK);
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
        refinery.energyStorage().setEnergyUnsafe(refinery.getEnergyCapacity());
        refinery.fluidStorage().setSlotUnsafe(RefineryBlockEntity.OIL_TANK, FluidVariant.of(GCFluid.CRUDE_OIL), FluidConstants.BUCKET, true);
        refinery.fluidStorage().setSlotUnsafe(RefineryBlockEntity.FUEL_TANK, FluidVariant.of(GCFluid.FUEL), RefineryBlockEntity.MAX_CAPACITY, true);
        runFinalTaskNext(context, () -> {
            if (refinery.fluidStorage().getAmount(RefineryBlockEntity.OIL_TANK) != FluidConstants.BUCKET) {
                context.fail("Expected refinery to be unable to refine oil as the fuel tank was full!", pos);
            }
        });
    }
}
