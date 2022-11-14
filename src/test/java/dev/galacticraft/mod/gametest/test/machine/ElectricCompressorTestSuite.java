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

import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.ElectricCompressorBlockEntity;
import dev.galacticraft.mod.content.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.content.item.GCItem;
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
public class ElectricCompressorTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricCompressorPlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.ELECTRIC_COMPRESSOR, GCBlockEntityTypes.ELECTRIC_COMPRESSOR));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricCompressorChargingTest(GameTestHelper context) {
        this.testItemCharging(context, new BlockPos(0, 0, 0), GCBlocks.ELECTRIC_COMPRESSOR, GCBlockEntityTypes.ELECTRIC_COMPRESSOR, ElectricCompressorBlockEntity.CHARGE_SLOT);
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 201)
    public void electricCompressorCraftingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricCompressor = this.createBlockEntity(context, pos, GCBlocks.ELECTRIC_COMPRESSOR, GCBlockEntityTypes.ELECTRIC_COMPRESSOR);
        final var inv = electricCompressor.itemStorage();
        fillElectricCompressorSlots(inv);
        electricCompressor.energyStorage().setEnergyUnsafe(electricCompressor.getEnergyCapacity());
        runFinalTaskAt(context, 200 + 1, () -> {
            ItemStack output = inv.getStack(ElectricCompressorBlockEntity.OUTPUT_SLOT);
            if (output.getItem() != GCItem.COMPRESSED_IRON) {
                context.fail(String.format("Expected electric compressor to have made compressed iron but found %s instead!", formatItemStack(output)), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void electricCompressorCraftingFullTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var electricCompressor = this.createBlockEntity(context, pos, GCBlocks.ELECTRIC_COMPRESSOR, GCBlockEntityTypes.ELECTRIC_COMPRESSOR);
        final var inv = electricCompressor.itemStorage();
        electricCompressor.energyStorage().setEnergyUnsafe(electricCompressor.getEnergyCapacity());
        inv.setSlot(ElectricCompressorBlockEntity.OUTPUT_SLOT, ItemVariant.of(Items.BARRIER), 1);
        inv.setSlot(ElectricCompressorBlockEntity.SECOND_OUTPUT_SLOT, ItemVariant.of(Items.BARRIER), 1);
        fillElectricCompressorSlots(inv);
        runFinalTaskNext(context, () -> {
            if (electricCompressor.getMaxProgress() != 0) {
                context.fail("Expected electric compressor to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillElectricCompressorSlots(@NotNull MachineItemStorage inv) {
        inv.setSlot(0, ItemVariant.of(Items.IRON_INGOT), 1);
        inv.setSlot(1, ItemVariant.of(Items.IRON_INGOT), 1);
    }
}
