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

package dev.galacticraft.mod.gametest.test;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.decoration.GratingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;

/**
 * Miscellaneous tests.
 *
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void gratingFlowingWaterTest(GameTestHelper context) {
        final var xz = 4;
        final var pos4 = new BlockPos(xz, 4, xz);
        final var pos3 = new BlockPos(xz, 3, xz);
        final var pos2 = new BlockPos(xz, 2, xz);
        final var pos1 = new BlockPos(xz, 1, xz);
        final var mutable = new BlockPos.MutableBlockPos();
        context.setBlock(pos2, GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.GRATING_STATE, GratingBlock.GratingState.LOWER));

        if (!context.getBlockState(pos2).getFluidState().isEmpty()) {
            context.fail(String.format("Expected grating to not be filled with fluid but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos2).getFluidState().getType())), pos2);
        }
        else {
            for (var x = -1; x < 2; x++) {
                for (var z = -1; z < 2; z++) {
                    if (mutable.set(xz + x, 4, xz + z).equals(pos4)) {
                        continue;
                    }
                    context.setBlock(mutable, Blocks.GLASS);
                }
            }
            context.setBlock(pos4, Blocks.WATER);
            context.runAtTickTime(context.getTick() + 40L, () -> {
                if (!context.getBlockState(pos3).getFluidState().is(FluidTags.WATER)) {
                    context.fail(String.format("Expected water to flow downward but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
                }
                else if (!context.getBlockState(pos2).getFluidState().is(FluidTags.WATER)) {
                    context.fail(String.format("Expected grating to be filled with water but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos2).getFluidState().getType())), pos2);
                }
                else if (!context.getBlockState(pos1).getFluidState().is(FluidTags.WATER)) {
                    context.fail(String.format("Expected water to be found below grating but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos1).getFluidState().getType())), pos1);
                }
                else {
                    context.setBlock(pos4, Blocks.AIR);
                    context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
                        if (!context.getBlockState(pos3).getFluidState().isEmpty()) {
                            context.fail(String.format("Expected water to drain itself but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
                        }
                        else if (!context.getBlockState(pos2).getFluidState().isEmpty()) {
                            context.fail(String.format("Expected grating to not be filled with fluid but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos2).getFluidState().getType())), pos2);
                        }
                        else if (!context.getBlockState(pos1).getFluidState().isEmpty()) {
                            context.fail(String.format("Expected no fluid to be found below grating but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos1).getFluidState().getType())), pos1);
                        }
                    }));
                }
            });
        }
    }
}