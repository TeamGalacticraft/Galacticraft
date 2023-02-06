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

import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.ElectricFurnaceBlockEntity;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
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
public class ElectricFurnaceTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricFurnacePlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.ELECTRIC_FURNACE, GCBlockEntityTypes.ELECTRIC_FURNACE));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricFurnaceChargeTest(GameTestHelper context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GCBlocks.ELECTRIC_FURNACE, GCBlockEntityTypes.ELECTRIC_FURNACE, ElectricFurnaceBlockEntity.CHARGE_SLOT);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 201)
    public void electricFurnaceCraftingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricFurnace = this.createBlockEntity(context, pos, GCBlocks.ELECTRIC_FURNACE, GCBlockEntityTypes.ELECTRIC_FURNACE);
        final var inv = electricFurnace.itemStorage();
        electricFurnace.energyStorage().setEnergy(electricFurnace.energyStorage().getCapacity());
        fillElectricFurnaceSlots(inv);
        runFinalTaskAt(context, 200 + 1, () -> {
            ItemStack output = inv.getStack(ElectricFurnaceBlockEntity.OUTPUT_SLOT);
            if (output.getItem() != Items.COOKED_PORKCHOP) {
                context.fail(String.format("Expected electric furnace to have made a cooked porkchop but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricFurnaceCraftingFullTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricFurnace = this.createBlockEntity(context, pos, GCBlocks.ELECTRIC_FURNACE, GCBlockEntityTypes.ELECTRIC_FURNACE);
        final var inv = electricFurnace.itemStorage();
        electricFurnace.energyStorage().setEnergy(electricFurnace.energyStorage().getCapacity());
        fillElectricFurnaceSlots(inv);
        inv.setSlot(ElectricFurnaceBlockEntity.OUTPUT_SLOT, ItemVariant.of(Items.BARRIER), 1);

        runFinalTaskNext(context, () -> {
            if (electricFurnace.getMaxProgress() != 0) {
                context.fail("Expected electric furnace to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillElectricFurnaceSlots(@NotNull MachineItemStorage inv) {
        inv.setSlot(ElectricFurnaceBlockEntity.INPUT_SLOT, ItemVariant.of(Items.PORKCHOP), 1);
    }
}
