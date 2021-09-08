/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.decoration.GratingBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class GalacticraftTestSuite implements FabricGameTest {
	@GameTest(structureName = EMPTY_STRUCTURE)
	public void wireConnectionTest(TestContext context) {
        final var pos0 = new BlockPos(0, 0, 0);
        final var pos1 = new BlockPos(0, 1, 0);
        final var pos2 = new BlockPos(0, 2, 0);
        context.setBlockState(pos0, GalacticraftBlock.ALUMINUM_WIRE);
        context.setBlockState(pos1, GalacticraftBlock.ALUMINUM_WIRE);
        context.setBlockState(pos2, GalacticraftBlock.ALUMINUM_WIRE);
        final var be0 = context.getBlockEntity(pos0);
        final var be1 = context.getBlockEntity(pos1);
        final var be2 = context.getBlockEntity(pos2);
        if (!(be0 instanceof Wire wire0)) {
            context.throwPositionedException(String.format("Expected a wire but found %s!", be0), pos0);
        } else if (!(be1 instanceof Wire wire1)) {
            context.throwPositionedException(String.format("Expected a wire but found %s!", be1), pos1);
        } else if (!(be2 instanceof Wire wire2)) {
            context.throwPositionedException(String.format("Expected a wire but found %s!", be2), pos2);
        } else {
            if (wire0.getNetwork() == null) {
                context.throwPositionedException("Expected a wire network but got null!", pos0);
            } else if (wire1.getNetwork() == null) {
                context.throwPositionedException("Expected a wire network but got null!", pos1);
            } else if (wire2.getNetwork() == null) {
                context.throwPositionedException("Expected a wire network but got null!", pos2);
            } else {
                if (wire0.getNetwork() != wire1.getNetwork()) {
                    context.throwGameTestException(String.format("Expected wire networks at %s and %s to be the same!", pos0, pos1));
                } else if (wire1.getNetwork() != wire2.getNetwork()) {
                    context.throwGameTestException(String.format("Expected wire networks at %s and %s to be the same!", pos1, pos2));
                } else {
                    if (wire0.getNetwork().getAllWires().size() != 3) {
                        context.throwGameTestException("Not all wires are registered in the network!");
                    } else {
                        context.addInstantFinalTask(() -> {
                            context.removeBlock(pos1);
                            if (wire0.getNetwork().getAllWires().size() != 1) {
                                context.throwPositionedException(String.format("Expected wire network with 1 wire but found %s wires!", wire0.getNetwork().getAllWires().size()), pos0);
                            } else if (wire2.getNetwork().getAllWires().size() != 1) {
                                context.throwPositionedException(String.format("Expected wire network with 1 wire but found %s wires!", wire2.getNetwork().getAllWires().size()), pos2);
                            } else if (!wire1.getNetwork().markedForRemoval()) {
                                if (!be1.isRemoved()) {
                                    context.throwPositionedException("Expected wire to be removed!", pos1);
                                } else {
                                    context.throwPositionedException("Expected removed wire network to be marked for removal!", pos1);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @GameTest(structureName = EMPTY_STRUCTURE)
    public void pipeConnectionTest(TestContext context) {
        final var pos0 = new BlockPos(0, 0, 0);
        final var pos1 = new BlockPos(0, 1, 0);
        final var pos2 = new BlockPos(0, 2, 0);
        context.setBlockState(pos0, GalacticraftBlock.GLASS_FLUID_PIPE);
        context.setBlockState(pos1, GalacticraftBlock.GLASS_FLUID_PIPE);
        context.setBlockState(pos2, GalacticraftBlock.GLASS_FLUID_PIPE);
        final var be0 = context.getBlockEntity(pos0);
        final var be1 = context.getBlockEntity(pos1);
        final var be2 = context.getBlockEntity(pos2);
        if (!(be0 instanceof Pipe pipe0)) {
            context.throwPositionedException(String.format("Expected a pipe but found %s!", be0), pos0);
        } else if (!(be1 instanceof Pipe pipe1)) {
            context.throwPositionedException(String.format("Expected a pipe but found %s!", be1), pos1);
        } else if (!(be2 instanceof Pipe pipe2)) {
            context.throwPositionedException(String.format("Expected a pipe but found %s!", be2), pos2);
        } else {
            if (pipe0.getNetwork() == null) {
                context.throwPositionedException("Expected a pipe network but got null!", pos0);
            } else if (pipe1.getNetwork() == null) {
                context.throwPositionedException("Expected a pipe network but got null!", pos1);
            } else if (pipe2.getNetwork() == null) {
                context.throwPositionedException("Expected a pipe network but got null!", pos2);
            } else {
                if (pipe0.getNetwork() != pipe1.getNetwork()) {
                    context.throwGameTestException(String.format("Expected pipe networks at %s and %s to be the same!", pos0, pos1));
                } else if (pipe1.getNetwork() != pipe2.getNetwork()) {
                    context.throwGameTestException(String.format("Expected pipe networks at %s and %s to be the same!", pos1, pos2));
                } else {
                    if (pipe0.getNetwork().getAllPipes().size() != 3) {
                        context.throwGameTestException("Not all pipes are registered in the network!");
                    } else {
                        context.addInstantFinalTask(() -> {
                            context.removeBlock(pos1);
                            if (pipe0.getNetwork().getAllPipes().size() != 1) {
                                context.throwPositionedException(String.format("Expected pipe network with 1 pipe but found %s pipes!", pipe0.getNetwork().getAllPipes().size()), pos0);
                            } else if (pipe2.getNetwork().getAllPipes().size() != 1) {
                                context.throwPositionedException(String.format("Expected pipe network with 1 pipe but found %s pipes!", pipe2.getNetwork().getAllPipes().size()), pos2);
                            } else if (!pipe1.getNetwork().markedForRemoval()) {
                                if (!be1.isRemoved()) {
                                    context.throwPositionedException("Expected pipe to be removed!", pos1);
                                } else {
                                    context.throwPositionedException("Expected removed pipe network to be marked for removal!", pos1);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

	@GameTest(structureName = EMPTY_STRUCTURE)
	public void gratingWaterFlowTest(TestContext context) {
        final var pos4 = new BlockPos(0, 4, 0);
        final var pos3 = new BlockPos(0, 3, 0);
        final var pos2 = new BlockPos(0, 2, 0);
        final var pos1 = new BlockPos(0, 1, 0);
        final var mutable = new BlockPos.Mutable();
        context.setBlockState(pos2, GalacticraftBlock.GRATING.getDefaultState().with(GratingBlock.GRATING_STATE, GratingBlock.GratingState.LOWER));
        if (!context.getBlockState(pos2).getFluidState().isEmpty()) {
            context.throwPositionedException(String.format("Expected grating to not be filled with fluid but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos2).getFluidState().getFluid())), pos2);
        } else {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    if (mutable.set(x, 4, z).equals(pos4)) {
                        continue;
                    }
                    context.setBlockState(mutable, Blocks.GLASS);
                }
            }
            context.setBlockState(pos4, Blocks.WATER);
            context.runAtTick(context.getTick() + 40L, () -> {
                if (!context.getBlockState(pos3).getFluidState().isIn(FluidTags.WATER)) {
                    context.throwPositionedException(String.format("Expected water to flow downward but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos3).getFluidState().getFluid())), pos3);
                } else if (!context.getBlockState(pos2).getFluidState().isIn(FluidTags.WATER)) {
                    context.throwPositionedException(String.format("Expected grating to be filled with water but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos2).getFluidState().getFluid())), pos2);
                } else if (!context.getBlockState(pos1).getFluidState().isIn(FluidTags.WATER)) {
                    context.throwPositionedException(String.format("Expected water to be found below grating but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos1).getFluidState().getFluid())), pos1);
                } else {
                    context.setBlockState(pos4, Blocks.AIR);
                    context.runAtTick(context.getTick() + 50L, () -> context.addInstantFinalTask(() -> {
                        if (!context.getBlockState(pos3).getFluidState().isEmpty()) {
                            context.throwPositionedException(String.format("Expected water to drain itself but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos3).getFluidState().getFluid())), pos3);
                        } else if (!context.getBlockState(pos2).getFluidState().isEmpty()) {
                            context.throwPositionedException(String.format("Expected grating to not be filled with fluid but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos2).getFluidState().getFluid())), pos2);
                        } else if (!context.getBlockState(pos1).getFluidState().isEmpty()) {
                            context.throwPositionedException(String.format("Expected no fluid to be found below grating but found %s instead!", Registry.FLUID.getId(context.getBlockState(pos1).getFluidState().getFluid())), pos1);
                        }
                    }));
                }
            });
        }
    }
}