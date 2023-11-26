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
import dev.galacticraft.machinelib.api.gametest.annotation.InstantTest;
import dev.galacticraft.machinelib.api.gametest.annotation.container.DefaultedMetadata;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.util.BlockFace;import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.CompressorBlockEntity;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@DefaultedMetadata(structure = GalacticraftGameTest.SINGLE_BLOCK)
public final class CompressorTestSuite extends RecipeGameTest<Container, CompressingRecipe, CompressorBlockEntity> {
    public CompressorTestSuite() {
        super(GCMachineTypes.COMPRESSOR, CompressorBlockEntity.INPUT_SLOTS, CompressorBlockEntity.INPUT_LENGTH, CompressorBlockEntity.OUTPUT_SLOT);
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> generateTests() {
        return super.generateTests();
    }

    @InstantTest
    public Runnable fuelConsumption(CompressorBlockEntity machine) {
        ItemResourceSlot slot = machine.itemStorage().getSlot(CompressorBlockEntity.FUEL_SLOT);
        slot.set(Items.COAL, 1);
        this.createValidRecipe(machine.itemStorage());
        return () -> {
            if (!slot.isEmpty()) {
                throw new GameTestAssertException("Failed to consume fuel");
            }
            if (machine.getFuelLength() == 0) {
                throw new GameTestAssertException("Failed to burn fuel");
            }
        };
    }

    @Override
    protected void fulfillRunRequirements(@NotNull CompressorBlockEntity machine) {
        machine.fuelLength = machine.fuelTime = 5000;
    }

    @Override
    protected int getRecipeRuntime() {
        return 200;
    }

    @Override
    protected void createValidRecipe(@NotNull MachineItemStorage storage) {
        storage.getSlot(CompressorBlockEntity.INPUT_SLOTS).set(Items.IRON_INGOT, 1);
        storage.getSlot(CompressorBlockEntity.INPUT_SLOTS + 1).set(Items.IRON_INGOT, 1);
    }
}
