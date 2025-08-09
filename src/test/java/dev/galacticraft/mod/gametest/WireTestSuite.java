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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.WireNetworkManager;
import dev.galacticraft.mod.content.GCBlocks;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class WireTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void wireConnectionTest(GameTestHelper context) {
        var bottom = new BlockPos(0, 1, 0);
        var mid = new BlockPos(0, 2, 0);
        var top = new BlockPos(0, 3, 0);

        context.setBlock(bottom, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(mid, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(top, GCBlocks.ALUMINUM_WIRE);
        var beBottom = (Wire) context.getBlockEntity(bottom);
        var beMid = (Wire) context.getBlockEntity(mid);
        var beTop = (Wire) context.getBlockEntity(top);

        if (beBottom.getNetwork() != beMid.getNetwork()) context.fail("Expected bottom and middle wires to connect!");
        if (beMid.getNetwork() != beTop.getNetwork()) context.fail("Expected middle and top wires to connect!");

        context.destroyBlock(mid);
        if (beBottom.getNetwork() == beMid.getNetwork()) context.fail("Expected bottom and top wires to separate into different networks!");

        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void wireNetworkMergeTest(GameTestHelper context) {
        var center = new BlockPos(4, 4, 4);

        for (Direction direction : Direction.values()) {
            context.setBlock(center.relative(direction), GCBlocks.ALUMINUM_WIRE);
            context.setBlock(center.relative(direction, 2), GCBlocks.ALUMINUM_WIRE);

            if (((Wire) context.getBlockEntity(center.relative(direction))).getNetwork()
                    != ((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork()
                    || ((Wire) context.getBlockEntity(center.relative(direction))).getNetwork() == WireNetworkManager.INVALID_NETWORK_ID) {
                context.fail("Expected network to be created!", center.relative(direction));
            }
        }

        context.setBlock(center, GCBlocks.ALUMINUM_WIRE);
        long network = ((Wire) context.getBlockEntity(center)).getNetwork();
        if (network == WireNetworkManager.INVALID_NETWORK_ID) context.fail("Expected network to be created!");
        for (Direction direction : Direction.values()) {
            if (((Wire) context.getBlockEntity(center.relative(direction))).getNetwork() != network) {
                context.fail("Expected network to be merged!", center.relative(direction));
            }
            if (((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork() != network) {
                context.fail("Expected merged network to extend outwards!", center.relative(direction));
            }
        }

        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void wireNetworkBreakTest(GameTestHelper context) {
        var center = new BlockPos(4, 4, 4);

        context.setBlock(center, GCBlocks.ALUMINUM_WIRE);
        for (Direction direction : Direction.values()) {
            context.setBlock(center.relative(direction), GCBlocks.ALUMINUM_WIRE);
            context.setBlock(center.relative(direction, 2), GCBlocks.ALUMINUM_WIRE);

            if (((Wire) context.getBlockEntity(center.relative(direction))).getNetwork()
                    != ((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork()
                    || ((Wire) context.getBlockEntity(center.relative(direction))).getNetwork() == WireNetworkManager.INVALID_NETWORK_ID) {
                context.fail("Expected network to be created!", center.relative(direction));
            }
        }

        context.destroyBlock(center);
        LongSet set = new LongArraySet();
        for (Direction direction : Direction.values()) {
            long network = ((Wire) context.getBlockEntity(center.relative(direction))).getNetwork();
            if (!set.add(network)) context.fail("Expected network to break into separate subnetworks!", center.relative(direction));
            if (network
                    != ((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork()) {
                context.fail("Expected edge networks to be connected!", center.relative(direction));
            }
        }
        context.succeed();
    }
}
