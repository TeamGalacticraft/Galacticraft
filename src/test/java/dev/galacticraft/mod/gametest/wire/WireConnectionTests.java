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

package dev.galacticraft.mod.gametest.wire;

import dev.galacticraft.machinelib.api.gametest.SimpleGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.BasicTest;
import dev.galacticraft.machinelib.api.gametest.annotation.TestSuite;
import dev.galacticraft.mod.api.wire.NetworkId;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@TestSuite("wire")
public class WireConnectionTests extends SimpleGameTest {
    @BasicTest
    public void linearConnect(GameTestHelper context) {
        var middlePos = new BlockPos(0, 2, 0);
        var bottomPos = middlePos.relative(Direction.DOWN);
        var topPos = middlePos.relative(Direction.UP);

        // Bottom to top, W-W-W
        context.setBlock(bottomPos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(middlePos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(topPos, GCBlocks.ALUMINUM_WIRE);

        var bottom = (Wire) context.getBlockEntity(bottomPos);
        var middle = (Wire) context.getBlockEntity(middlePos);
        var top = (Wire) context.getBlockEntity(topPos);

        if (bottom.getNetwork() != middle.getNetwork()) context.fail("Expected bottom and middle wires to connect!");
        if (middle.getNetwork() != top.getNetwork()) context.fail("Expected middle and top wires to connect!");
    }

    @BasicTest
    public void mergeConnect(GameTestHelper context) {
        var middlePos = new BlockPos(0, 2, 0);
        var bottomPos = middlePos.relative(Direction.DOWN);
        var topPos = middlePos.relative(Direction.UP);

        // W _ W
        context.setBlock(bottomPos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(topPos, GCBlocks.ALUMINUM_WIRE);

        // W-W-W
        context.setBlock(middlePos, GCBlocks.ALUMINUM_WIRE);

        var bottom = (Wire) context.getBlockEntity(bottomPos);
        var middle = (Wire) context.getBlockEntity(middlePos);
        var top = (Wire) context.getBlockEntity(topPos);

        if (bottom.getNetwork() != middle.getNetwork()) context.fail("Expected bottom and middle wires to connect!");
        if (middle.getNetwork() != top.getNetwork()) context.fail("Expected middle and top wires to connect!");
    }

    @BasicTest
    public void gridConnect(GameTestHelper context) {
        // W-W-W
        // W-W-W
        BlockBox box = BlockBox.of(new BlockPos(1, 1, 0), new BlockPos(1, 2, 2));
        for (BlockPos pos : box) {
            context.setBlock(pos, GCBlocks.ALUMINUM_WIRE);
        }
        NetworkId networkId = ((Wire) context.getBlockEntity(BlockPos.containing(box.aabb().getCenter()))).getNetwork();
        if (networkId == null) context.fail("Expected network to form!");
        assert networkId != null;
        for (BlockPos pos : box) {
            if (!networkId.equals(((Wire) context.getBlockEntity(pos)).getNetwork())) context.fail("Expected network to be the same!", pos);
        }
    }

    @BasicTest(structure = EMPTY_STRUCTURE)
    public void mergePropagate(GameTestHelper context) {
        var center = new BlockPos(4, 5, 4);

        //     W
        //     W
        // W-W _ W-W
        //     W
        //     W
        for (Direction direction : Direction.values()) {
            context.setBlock(center.relative(direction), GCBlocks.ALUMINUM_WIRE);
            context.setBlock(center.relative(direction, 2), GCBlocks.ALUMINUM_WIRE);

            if (((Wire) context.getBlockEntity(center.relative(direction))).getNetwork()
                    != ((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork()
                    || ((Wire) context.getBlockEntity(center.relative(direction))).getNetwork() == null) {
                context.fail("Expected network to be created!", center.relative(direction));
            }
        }

        // fill center block - all networks should merge and propagate to peers
        context.setBlock(center, GCBlocks.ALUMINUM_WIRE);

        NetworkId network = ((Wire) context.getBlockEntity(center)).getNetwork();
        if (network == null) context.fail("Expected network to be created!");
        for (Direction direction : Direction.values()) {
            if (((Wire) context.getBlockEntity(center.relative(direction))).getNetwork() != network) {
                context.fail("Expected network to be merged!", center.relative(direction));
            }
            if (((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork() != network) {
                context.fail("Expected merged network to extend outwards!", center.relative(direction));
            }
        }
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        return super.registerTests();
    }
}
