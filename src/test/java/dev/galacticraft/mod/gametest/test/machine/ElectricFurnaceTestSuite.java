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

import dev.galacticraft.machinelib.api.gametest.RecipeGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.container.DefaultedMetadata;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.ElectricFurnaceBlockEntity;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@DefaultedMetadata(structure = GalacticraftGameTest.SINGLE_BLOCK)
public final class ElectricFurnaceTestSuite extends RecipeGameTest<Container, SmeltingRecipe, ElectricFurnaceBlockEntity> {
    public ElectricFurnaceTestSuite() {
        super(GCMachineTypes.ELECTRIC_FURNACE, ElectricFurnaceBlockEntity.INPUT_SLOT, ElectricFurnaceBlockEntity.OUTPUT_SLOT);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> generateTests() {
        List<TestFunction> functions = super.generateTests();
        functions.add(this.createChargeFromEnergyItemTest(ElectricFurnaceBlockEntity.CHARGE_SLOT, GCItems.INFINITE_BATTERY));
        return functions;
    }

    @Override
    protected void fulfillRunRequirements(@NotNull ElectricFurnaceBlockEntity machine) {
        machine.energyStorage().setEnergy(Long.MAX_VALUE / 2);
    }

    @Override
    protected int getRecipeRuntime() {
        return 200;
    }

    @Override
    protected void createValidRecipe(@NotNull MachineItemStorage storage) {
        storage.getSlot(ElectricFurnaceBlockEntity.INPUT_SLOT).set(Items.PORKCHOP, 1);
    }
}
