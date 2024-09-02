/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.ElectricArcFurnaceBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@TestSuite("electric_arc_furnace")
public final class ElectricArcFurnaceTestSuite extends RecipeGameTest<SingleRecipeInput, BlastingRecipe, ElectricArcFurnaceBlockEntity> {
    public ElectricArcFurnaceTestSuite() {
        super(GCBlocks.ELECTRIC_ARC_FURNACE, List.of(
                machine -> machine.energyStorage().setEnergy(Long.MAX_VALUE / 2),
                machine -> machine.itemStorage().slot(ElectricArcFurnaceBlockEntity.INPUT_SLOT).set(Items.RAW_IRON, 1)
        ), ElectricArcFurnaceBlockEntity.OUTPUT_SLOTS, ElectricArcFurnaceBlockEntity.OUTPUT_LENGTH, 100);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createChargeFromEnergyItemTest(ElectricArcFurnaceBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return tests;
    }
}
