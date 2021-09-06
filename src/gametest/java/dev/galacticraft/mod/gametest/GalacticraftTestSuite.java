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
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

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
            context.throwPositionedException(String.format("Expected a wire but found %s!", be1), pos1);
        } else {
            if (wire0.getNetworkNullable() == null) {
                context.throwPositionedException("Expected a wire network but got null!", pos1);
            } else if (wire1.getNetworkNullable() == null) {
                context.throwPositionedException("Expected a wire network but got null!", pos1);
            } else if (wire2.getNetworkNullable() == null) {
                context.throwPositionedException("Expected a wire network but got null!", pos1);
            } else {
                if (wire0.getNetworkNullable() != wire1.getNetworkNullable()) {
                    context.throwGameTestException(String.format("Expected wire networks at %s and %s to be the same!", pos0, pos1));
                } else if (wire1.getNetworkNullable() != wire2.getNetworkNullable()) {
                    context.throwGameTestException(String.format("Expected wire networks at %s and %s to be the same!", pos1, pos2));
                } else {
                    context.addInstantFinalTask(() -> {
                        if (wire0.getNetworkNullable().getAllWires().size() != 3) {
                            context.throwGameTestException("Not all wires are registered in the network!");
                        }
                    });
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
            context.throwPositionedException(String.format("Expected a pipe but found %s!", be1), pos1);
        } else {
            if (pipe0.getNetworkNullable() == null) {
                context.throwPositionedException("Expected a pipe network but got null!", pos1);
            } else if (pipe1.getNetworkNullable() == null) {
                context.throwPositionedException("Expected a pipe network but got null!", pos1);
            } else if (pipe2.getNetworkNullable() == null) {
                context.throwPositionedException("Expected a pipe network but got null!", pos1);
            } else {
                if (pipe0.getNetworkNullable() != pipe1.getNetworkNullable()) {
                    context.throwGameTestException(String.format("Expected pipe networks at %s and %s to be the same!", pos0, pos1));
                } else if (pipe1.getNetworkNullable() != pipe2.getNetworkNullable()) {
                    context.throwGameTestException(String.format("Expected pipe networks at %s and %s to be the same!", pos1, pos2));
                } else {
                    context.addInstantFinalTask(() -> {
                        if (pipe0.getNetworkNullable().getAllPipes().size() != 3) {
                            context.throwGameTestException("Not all pipes are registered in the network!");
                        }
                    });
                }
            }
        }
    }
}