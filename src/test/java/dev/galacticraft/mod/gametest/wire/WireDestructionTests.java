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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TestSuite("wire")
public class WireDestructionTests extends SimpleGameTest {
    @BasicTest
    public void edgeDestroy(GameTestHelper context) {
        var middlePos = new BlockPos(0, 2, 0);
        var bottomPos = middlePos.relative(Direction.DOWN);
        var topPos = middlePos.relative(Direction.UP);

        // W-W-X
        context.setBlock(bottomPos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(middlePos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(topPos, GCBlocks.ALUMINUM_WIRE);

        var bottom = (Wire) context.getBlockEntity(bottomPos);
        var middle = (Wire) context.getBlockEntity(middlePos);

        context.destroyBlock(topPos);

        if (bottom.getNetwork() != middle.getNetwork() || bottom.getNetwork() == null) context.fail("Expected wires to maintain the same network!", middlePos);
    }

    @BasicTest
    public void splitDestroy(GameTestHelper context) {
        var middlePos = new BlockPos(0, 2, 0);
        var bottomPos = middlePos.relative(Direction.DOWN);
        var topPos = middlePos.relative(Direction.UP);

        // W-X-W
        context.setBlock(bottomPos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(middlePos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(topPos, GCBlocks.ALUMINUM_WIRE);

        var bottom = (Wire) context.getBlockEntity(bottomPos);
        var top = (Wire) context.getBlockEntity(topPos);

        context.destroyBlock(middlePos);

        if (bottom.getNetwork() == top.getNetwork() && bottom.getNetwork() != null) context.fail("Expected wires to separate into different networks!", middlePos);
    }

    @BasicTest(structure = EMPTY_STRUCTURE)
    public void wireNetworkBreakTest(GameTestHelper context) {
        var center = new BlockPos(4, 4, 4);

        //     W
        //     W
        // W-W-X-W-W
        //     W
        //     W
        context.setBlock(center, GCBlocks.ALUMINUM_WIRE);
        for (Direction direction : Direction.values()) {
            context.setBlock(center.relative(direction), GCBlocks.ALUMINUM_WIRE);
            context.setBlock(center.relative(direction, 2), GCBlocks.ALUMINUM_WIRE);
        }

        context.destroyBlock(center);

        Set<NetworkId> set = new HashSet<>();
        for (Direction direction : Direction.values()) {
            NetworkId network = ((Wire) context.getBlockEntity(center.relative(direction))).getNetwork();
            if (!set.add(network))
                context.fail("Expected network to break into separate subnetworks!", center.relative(direction));
            if (network != ((Wire) context.getBlockEntity(center.relative(direction, 2))).getNetwork()) {
                context.fail("Expected edge networks to be connected!", center.relative(direction));
            }
        }
    }

    @BasicTest
    public void destroyMeshMaintain(GameTestHelper context) {
        // W-X-W
        // W-W-W
        BlockBox box = BlockBox.of(new BlockPos(1, 1, 0), new BlockPos(1, 2, 2));
        for (BlockPos pos : box) {
            context.setBlock(pos, GCBlocks.ALUMINUM_WIRE);
        }
        BlockPos center = BlockPos.containing(box.aabb().getCenter());
        NetworkId networkId = ((Wire) context.getBlockEntity(center)).getNetwork();
        context.destroyBlock(center);
        for (BlockPos pos : box) {
            if (!center.equals(pos) && !networkId.equals(((Wire) context.getBlockEntity(pos)).getNetwork()))
                context.fail("Expected network to be the same!", pos);
        }
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        return super.registerTests();
    }
}
