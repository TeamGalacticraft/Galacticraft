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

package dev.galacticraft.mod.gametest.test;

import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void pipeConnectionTest(GameTestHelper context) {
        final var pos0 = new BlockPos(0, 0, 0);
        final var pos1 = new BlockPos(0, 1, 0);
        final var pos2 = new BlockPos(0, 2, 0);
        context.setBlock(pos0, GalacticraftBlock.GLASS_FLUID_PIPE);
        context.setBlock(pos1, GalacticraftBlock.GLASS_FLUID_PIPE);
        context.setBlock(pos2, GalacticraftBlock.GLASS_FLUID_PIPE);
        final var be0 = context.getBlockEntity(pos0);
        final var be1 = context.getBlockEntity(pos1);
        final var be2 = context.getBlockEntity(pos2);
        if (!(be0 instanceof Pipe pipe0)) {
            context.fail(String.format("Expected a pipe but found %s!", be0), pos0);
        } else if (!(be1 instanceof Pipe pipe1)) {
            context.fail(String.format("Expected a pipe but found %s!", be1), pos1);
        } else if (!(be2 instanceof Pipe pipe2)) {
            context.fail(String.format("Expected a pipe but found %s!", be2), pos2);
        } else {
            if (pipe0.getNetwork() == null) {
                context.fail("Expected a pipe network but got null!", pos0);
            } else if (pipe1.getNetwork() == null) {
                context.fail("Expected a pipe network but got null!", pos1);
            } else if (pipe2.getNetwork() == null) {
                context.fail("Expected a pipe network but got null!", pos2);
            } else {
                if (pipe0.getNetwork() != pipe1.getNetwork()) {
                    context.fail(String.format("Expected pipe networks at %s and %s to be the same!", pos0, pos1));
                } else if (pipe1.getNetwork() != pipe2.getNetwork()) {
                    context.fail(String.format("Expected pipe networks at %s and %s to be the same!", pos1, pos2));
                } else {
                    if (pipe0.getNetwork().getAllPipes().size() != 3) {
                        context.fail("Not all pipes are registered in the network!");
                    } else {
                        context.succeedWhen(() -> {
                            context.destroyBlock(pos1);
                            if (pipe0.getNetwork().getAllPipes().size() != 1) {
                                context.fail(String.format("Expected pipe network with 1 pipe but found %s pipes!", pipe0.getNetwork().getAllPipes().size()), pos0);
                            } else if (pipe2.getNetwork().getAllPipes().size() != 1) {
                                context.fail(String.format("Expected pipe network with 1 pipe but found %s pipes!", pipe2.getNetwork().getAllPipes().size()), pos2);
                            } else if (!pipe1.getNetwork().markedForRemoval()) {
                                if (!be1.isRemoved()) {
                                    context.fail("Expected pipe to be removed!", pos1);
                                } else {
                                    context.fail("Expected removed pipe network to be marked for removal!", pos1);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
