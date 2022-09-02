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

import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.ElectricArcFurnaceBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricArcFurnaceTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricArcFurnacePlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricArcFurnaceChargeTest(GameTestHelper context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE, ElectricArcFurnaceBlockEntity.CHARGE_SLOT);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 90 + 1)
    public void electricArcFurnaceBlastingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricArcFurnace = this.createBlockEntity(context, pos, GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE);
        final var inv = electricArcFurnace.itemStorage();
        electricArcFurnace.energyStorage().setEnergyUnsafe(electricArcFurnace.getEnergyCapacity());
        fillElectricArcFurnaceSlots(inv);
        runFinalTaskAt(context, 90 + 1, () -> {
            ItemStack output = inv.getStack(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1);
            if (output.getItem() != Items.IRON_INGOT && output.getCount() != 2) {
                context.fail(String.format("Expected electric arc furnace to have made two iron ingots but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricArcFurnaceCraftingFullTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricArcFurnace = this.createBlockEntity(context, pos, GalacticraftBlock.ELECTRIC_ARC_FURNACE, GalacticraftBlockEntityType.ELECTRIC_ARC_FURNACE);
        final var inv = electricArcFurnace.itemStorage();
        electricArcFurnace.energyStorage().setEnergyUnsafe(electricArcFurnace.getEnergyCapacity());
        inv.setSlotUnsafe(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1, ItemVariant.of(Items.BARRIER), 1);
        inv.setSlotUnsafe(ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_2, ItemVariant.of(Items.BARRIER), 1);
        fillElectricArcFurnaceSlots(inv);
        runFinalTaskNext(context, () -> {
            if (electricArcFurnace.getMaxProgress() != 0) {
                context.fail("Expected electric arc furnace to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillElectricArcFurnaceSlots(@NotNull MachineItemStorage inv) {
        inv.setSlotUnsafe(ElectricArcFurnaceBlockEntity.INPUT_SLOT, ItemVariant.of(Items.RAW_IRON), 1, true);
    }
}
