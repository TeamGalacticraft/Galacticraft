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

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorTestSuite implements MachineGameTest {
    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 1)
    public void coalGeneratorPlacementTest(TestContext context) {
        context.addInstantFinalTask(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GalacticraftBlock.COAL_GENERATOR, GalacticraftBlockEntityType.COAL_GENERATOR));
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 322)
    public void coalGeneratorFuelingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GalacticraftBlock.COAL_GENERATOR, GalacticraftBlockEntityType.COAL_GENERATOR);
        final var fuelSlot = coalGenerator.itemInv().getSlot(CoalGeneratorBlockEntity.FUEL_SLOT);
        fuelSlot.set(new ItemStack(Items.COAL, 2));
        runNext(context, () -> {
            ItemStack stack = fuelSlot.get();
            if (stack.isEmpty() || stack.getItem() != Items.COAL || stack.getCount() != 1) {
                context.throwPositionedException(String.format("Expected coal generator inventory to be have 1 coal but found %s!", formatItemStack(stack)), pos);
            }
            if (coalGenerator.fuelLength == 0) {
                context.throwPositionedException("Expected coal generator inventory to be burning fuel!", pos);
            }
            runFinalTaskAt(context, 320 + 1, () -> {
                ItemStack stack1 = fuelSlot.get();

                if (!stack1.isEmpty()) {
                    context.throwPositionedException(String.format("Expected coal generator inventory to be empty but found %s!", formatItemStack(stack1)), pos);
                }
            });
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 371)
    public void coalGeneratorGenerationTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GalacticraftBlock.COAL_GENERATOR, GalacticraftBlockEntityType.COAL_GENERATOR);
        coalGenerator.fuelLength = CoalGeneratorBlockEntity.FUEL_MAP.getInt(Items.COAL);
        runFinalTaskAt(context, 370 + 1, () -> {
            if (coalGenerator.capacitorView().getEnergy() != 26371) {
                context.throwPositionedException(String.format("Expected coal generator to have 26371 energy! Found: %s", coalGenerator.capacitorView().getEnergy()), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 250)
    public void coalGeneratorHeatTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GalacticraftBlock.COAL_GENERATOR, GalacticraftBlockEntityType.COAL_GENERATOR);
        coalGenerator.fuelLength = 250;
        runFinalTaskAt(context, 250, () -> {
            if (coalGenerator.getHeat() != 1) {
                context.throwPositionedException(String.format("Expected coal generator to be 100 percent heated but it only was %s percent!", (int)(coalGenerator.getHeat() * 100)), pos);
            }
        });
    }

    @GameTest(structureName = GalacticraftGameTest.SINGLE_BLOCK, tickLimit = 50)
    public void coalGeneratorCoolingTest(TestContext context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GalacticraftBlock.COAL_GENERATOR, GalacticraftBlockEntityType.COAL_GENERATOR);
        coalGenerator.setHeat(1.0);
        runFinalTaskAt(context, 50, () -> {
            if (coalGenerator.getHeat() != 0) {
                context.throwPositionedException(String.format("Expected coal generator to be 0 percent heated but it was %s percent!", (int)(coalGenerator.getHeat() * 100)), pos);
            }
        });
    }
}
