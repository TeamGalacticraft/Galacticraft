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

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.content.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.gametest.test.GalacticraftGameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorTestSuite implements MachineGameTest {
    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 1)
    public void coalGeneratorPlacementTest(GameTestHelper context) {
        context.succeedWhen(() -> this.createBlockEntity(context, new BlockPos(0, 0, 0), GCBlocks.COAL_GENERATOR, GCBlockEntityTypes.COAL_GENERATOR));
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 322)
    public void coalGeneratorFuelingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GCBlocks.COAL_GENERATOR, GCBlockEntityTypes.COAL_GENERATOR);
        coalGenerator.itemStorage().setSlot(CoalGeneratorBlockEntity.FUEL_SLOT, ItemVariant.of(Items.COAL), 2);
        runNext(context, () -> {
            ItemStack stack = coalGenerator.itemStorage().getStack(CoalGeneratorBlockEntity.FUEL_SLOT);
            if (stack.isEmpty() || stack.getItem() != Items.COAL || stack.getCount() != 1) {
                context.fail(String.format("Expected coal generator inventory to be have 1 coal but found %s!", formatItemStack(stack)), pos);
            }
            if (coalGenerator.getFuelLength() == 0) {
                context.fail("Expected coal generator inventory to be burning fuel!", pos);
            }
            runFinalTaskAt(context, 320 + 1, () -> {
                ItemStack stack1 = coalGenerator.itemStorage().getStack(CoalGeneratorBlockEntity.FUEL_SLOT);

                if (!stack1.isEmpty()) {
                    context.fail(String.format("Expected coal generator inventory to be empty but found %s!", formatItemStack(stack1)), pos);
                }
            });
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 372)
    public void coalGeneratorGenerationTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GCBlocks.COAL_GENERATOR, GCBlockEntityTypes.COAL_GENERATOR);
        coalGenerator.setFuelLength(CoalGeneratorBlockEntity.FUEL_MAP.getInt(Items.COAL));
        runFinalTaskAt(context, 370 + 1, () -> {
            if (coalGenerator.energyStorage().getAmount() != 26251) {
                context.fail(String.format("Expected coal generator to have 26251 energy! Found: %s", coalGenerator.energyStorage().getAmount()), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 251)
    public void coalGeneratorHeatTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GCBlocks.COAL_GENERATOR, GCBlockEntityTypes.COAL_GENERATOR);
        coalGenerator.setFuelLength(250);
        runFinalTaskAt(context, 250, () -> {
            if (coalGenerator.getHeat() != 1) {
                context.fail(String.format("Expected coal generator to be 100 percent heated but it only was %s percent!", (int)(coalGenerator.getHeat() * 100)), pos);
            }
        });
    }

    @GameTest(template = GalacticraftGameTest.SINGLE_BLOCK, timeoutTicks = 50)
    public void coalGeneratorCoolingTest(GameTestHelper context) {
        final var pos = new BlockPos(0, 0, 0);
        final var coalGenerator = this.createBlockEntity(context, pos, GCBlocks.COAL_GENERATOR, GCBlockEntityTypes.COAL_GENERATOR);
        coalGenerator.setHeat(1.0);
        runFinalTaskAt(context, 50, () -> {
            if (coalGenerator.getHeat() != 0) {
                context.fail(String.format("Expected coal generator to be 0 percent heated but it was %s percent!", (int)(coalGenerator.getHeat() * 100)), pos);
            }
        });
    }
}
