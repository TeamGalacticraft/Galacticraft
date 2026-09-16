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

import dev.galacticraft.machinelib.api.gametest.RecipeGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.MachineTest;
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.machinelib.api.transfer.ResourceFlow;
import dev.galacticraft.machinelib.api.transfer.ResourceType;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.ElectricCompressorBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@TestSuite("electric_compressor")
public final class ElectricCompressorTestSuite extends RecipeGameTest<CraftingInput, CompressingRecipe, ElectricCompressorBlockEntity> {
    public ElectricCompressorTestSuite() {
        super(GCBlocks.ELECTRIC_COMPRESSOR, List.of(
                machine -> machine.energyStorage().setEnergy(Long.MAX_VALUE / 2),
                machine -> machine.itemStorage().slot(ElectricCompressorBlockEntity.INPUT_SLOTS).set(Items.IRON_INGOT, 1),
                machine -> machine.itemStorage().slot(ElectricCompressorBlockEntity.INPUT_SLOTS + 1).set(Items.IRON_INGOT, 1)
        ), ElectricCompressorBlockEntity.OUTPUT_SLOTS, ElectricCompressorBlockEntity.OUTPUT_LENGTH, 200);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createChargeFromEnergyItemTest(ElectricCompressorBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return tests;
    }

    @MachineTest(workTime = 175)
    public Runnable hopperInsertionTest(ElectricCompressorBlockEntity machine, GameTestHelper context) {
        machine.itemStorage().slot(ElectricCompressorBlockEntity.CHARGE_SLOT).set(GCItems.INFINITE_BATTERY, 1);
        machine.getIOConfig().get(BlockFace.TOP).setOption(ResourceType.ITEM, ResourceFlow.INPUT);

        final BlockPos hopperPos = MACHINE_POS.above();

        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());
        HopperBlockEntity hopper = context.getBlockEntity(hopperPos);

        hopper.setItem(0, new ItemStack(GCItems.COMPRESSED_STEEL, 2));
        hopper.setItem(1, new ItemStack(GCItems.COMPRESSED_ALUMINUM, 2));
        hopper.setItem(2, new ItemStack(GCItems.COMPRESSED_BRONZE, 2));

        return () -> Assertions.assertTrue(
                machine.itemStorage().slot(ElectricCompressorBlockEntity.OUTPUT_SLOTS).contains(GCItems.TIER_1_HEAVY_DUTY_PLATE),
                "Expected hopper to insert ingredients into the correct slots of the electric compressor to craft recipe"
        );
    }
}
