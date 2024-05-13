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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.impl.WireNetworkImpl;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class WireTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void wireConnectionTest(GameTestHelper context) {
        final var pos0 = new BlockPos(0, 1, 0);
        final var pos1 = new BlockPos(0, 2, 0);
        final var pos2 = new BlockPos(0, 3, 0);
        context.setBlock(pos0, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(pos1, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(pos2, GCBlocks.ALUMINUM_WIRE);
        final var be0 = context.getBlockEntity(pos0);
        final var be1 = context.getBlockEntity(pos1);
        final var be2 = context.getBlockEntity(pos2);
        if (!(be0 instanceof Wire wire0)) {
            context.fail(String.format("Expected a wire but found %s!", be0), pos0);
        } else if (!(be1 instanceof Wire wire1)) {
            context.fail(String.format("Expected a wire but found %s!", be1), pos1);
        } else if (!(be2 instanceof Wire wire2)) {
            context.fail(String.format("Expected a wire but found %s!", be2), pos2);
        } else {
            if (wire0.getNetwork() == null) {
                context.fail("Expected a wire network but got null!", pos0);
            } else if (wire1.getNetwork() == null) {
                context.fail("Expected a wire network but got null!", pos1);
            } else if (wire2.getNetwork() == null) {
                context.fail("Expected a wire network but got null!", pos2);
            } else {
                if (wire0.getNetwork() != wire1.getNetwork()) {
                    context.fail(String.format("Expected wire networks at %s and %s to be the same!", pos0, pos1));
                } else if (wire1.getNetwork() != wire2.getNetwork()) {
                    context.fail(String.format("Expected wire networks at %s and %s to be the same!", pos1, pos2));
                } else {
                    if (((WireNetworkImpl) wire0.getNetwork()).getWires().size() != 3) {
                        context.fail("Not all wires are registered in the network!");
                    } else {
                        context.succeedWhen(() -> {
                            context.destroyBlock(pos1);
                            if (((WireNetworkImpl) wire0.getNetwork()).getWires().size() != 1) {
                                context.fail(String.format("Expected wire network with 1 wire but found %s wires!", ((WireNetworkImpl) wire0.getNetwork()).getWires().size()), pos0);
                            } else if (((WireNetworkImpl) wire2.getNetwork()).getWires().size() != 1) {
                                context.fail(String.format("Expected wire network with 1 wire but found %s wires!", ((WireNetworkImpl) wire2.getNetwork()).getWires().size()), pos2);
                            } else if (!wire1.getNetwork().markedForRemoval()) {
                                if (!be1.isRemoved()) {
                                    context.fail("Expected wire to be removed!", pos1);
                                } else {
                                    context.fail("Expected removed wire network to be marked for removal!", pos1);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
