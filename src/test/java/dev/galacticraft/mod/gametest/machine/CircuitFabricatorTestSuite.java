/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.content.block.entity.machine.CircuitFabricatorBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@TestSuite("circuit_fabricator")
public final class CircuitFabricatorTestSuite extends RecipeGameTest<RecipeInput, FabricationRecipe, CircuitFabricatorBlockEntity> {
    public CircuitFabricatorTestSuite() {
        super(GCBlocks.CIRCUIT_FABRICATOR, List.of(
                machine -> machine.energyStorage().setEnergy(Long.MAX_VALUE / 2),
                machine -> machine.itemStorage().slot(CircuitFabricatorBlockEntity.DIAMOND_SLOT).set(Items.DIAMOND, 1),
                machine -> machine.itemStorage().slot(CircuitFabricatorBlockEntity.REDSTONE_SLOT).set(Items.REDSTONE, 1),
                machine -> machine.itemStorage().slot(CircuitFabricatorBlockEntity.SILICON_SLOT_1).set(GCItems.SILICON, 1),
                machine -> machine.itemStorage().slot(CircuitFabricatorBlockEntity.SILICON_SLOT_2).set(GCItems.SILICON, 1),
                machine -> machine.itemStorage().slot(CircuitFabricatorBlockEntity.INPUT_SLOT).set(Items.REDSTONE_TORCH, 1)
        ), CircuitFabricatorBlockEntity.OUTPUT_SLOT, 300);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();
        tests.add(this.createChargeFromEnergyItemTest(CircuitFabricatorBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return tests;
    }
}
