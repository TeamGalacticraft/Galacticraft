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
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.CompressorBlockEntity;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CompressorTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void compressorPlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.COMPRESSOR, GCBlockEntityTypes.COMPRESSOR));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 2)
    public void compressorFuelingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var compressor = this.createBlockEntity(context, pos, GCBlocks.COMPRESSOR, GCBlockEntityTypes.COMPRESSOR);
        final var inv = compressor.itemStorage();
        ItemResourceSlot slot = inv.getSlot(GCSlotGroupTypes.SOLID_FUEL);
        slot.set(Items.COAL, null, 1);
        runNext(context, () -> {
            if (slot.isEmpty() || slot.getResource() != Items.COAL || slot.getAmount() != 1) {
                context.fail(String.format("Expected compressor inventory to be have 1 coal but found %s!", formatItem(slot.getResource(), slot.getAmount())), pos);
            }
            fillCompressorSlots(inv);
            inv.markModified();
            runFinalTaskNext(context, () -> {
                if (compressor.fuelLength == 0) {
                    context.fail("Expected compressor inventory to be burning fuel!", pos);
                }
            });
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 201)
    public void compressorCraftingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var compressor = this.createBlockEntity(context, pos, GCBlocks.COMPRESSOR, GCBlockEntityTypes.COMPRESSOR);
        final var inv = compressor.itemStorage();
        fillCompressorSlots(inv);
        compressor.fuelTime = compressor.fuelLength = 1000;
        runFinalTaskAt(context, 200 + 1, () -> {
            ItemResourceSlot slot = inv.getSlot(GCSlotGroupTypes.GENERIC_OUTPUT);
            if (slot.getResource() != GCItem.COMPRESSED_IRON) {
                context.fail(String.format("Expected compressor to have made compressed iron but found %s instead!", formatItem(slot.getResource(), slot.getAmount())), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void compressorCraftingFullTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var compressor = this.createBlockEntity(context, pos, GCBlocks.COMPRESSOR, GCBlockEntityTypes.COMPRESSOR);
        final var inv = compressor.itemStorage();
        compressor.fuelTime = compressor.fuelLength = 1000;
        inv.getSlot(GCSlotGroupTypes.GENERIC_OUTPUT).set(Items.BARRIER, null, 1);
        fillCompressorSlots(inv);
        runFinalTaskNext(context, () -> {
            if (compressor.getMaxProgress() != 0) {
                context.fail("Expected compressor to be unable to craft as the output was full!", pos);
            }
        });
    }

    private static void fillCompressorSlots(@NotNull MachineItemStorage inv) {
        SlotGroup<Item, ItemStack, ItemResourceSlot> group = inv.getGroup(GCSlotGroupTypes.GENERIC_INPUT);
        group.getSlot(0).set(Items.IRON_INGOT, null, 1);
        group.getSlot(1).set(Items.IRON_INGOT, null, 1);
    }
}
