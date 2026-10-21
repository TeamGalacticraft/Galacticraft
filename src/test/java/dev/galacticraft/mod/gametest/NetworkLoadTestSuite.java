/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.WireNetwork;
import dev.galacticraft.mod.content.GCBlocks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests that wire and pipe networks are built correctly when a chunk with them is loaded.
 */
public class NetworkLoadTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void wireLineLoadTest(GameTestHelper context) {
        List<BlockPos> line = line(1, 6);
        place(context, line, GCBlocks.ALUMINUM_WIRE);
        this.loadAndCheckWires(context, List.of(line));
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void wirePlaneLoadTest(GameTestHelper context) {
        List<BlockPos> plane = new ArrayList<>();
        for (int x = 0; x < 4; x++) for (int z = 0; z < 4; z++) plane.add(new BlockPos(x, 1, z));
        place(context, plane, GCBlocks.ALUMINUM_WIRE);
        this.loadAndCheckWires(context, List.of(plane));
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void mixedWireTiersLoadTest(GameTestHelper context) {
        // A line of wires next to a line of heavy wires, which used to cause infinite recursion (#337)
        List<BlockPos> wires = line(1, 5);
        List<BlockPos> heavyWires = line(2, 5);
        place(context, wires, GCBlocks.ALUMINUM_WIRE);
        place(context, heavyWires, GCBlocks.HEAVY_ALUMINUM_WIRE);
        this.loadAndCheckWires(context, List.of(wires, heavyWires));
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void pipeLineLoadTest(GameTestHelper context) {
        List<BlockPos> line = line(1, 6);
        place(context, line, GCBlocks.GLASS_FLUID_PIPE);
        reload(context, line);
        FluidStorage.SIDED.find(context.getLevel(), context.absolutePos(line.getFirst()), Direction.UP);

        PipeNetwork network = null;
        for (BlockPos pos : line) {
            PipeNetwork pipeNetwork = ((FluidPipe) context.getBlockEntity(pos)).getNetwork();
            if (pipeNetwork == null || pipeNetwork.markedForRemoval()) {
                context.fail("Expected a pipe network that wasn't removed, but found " + pipeNetwork, pos);
                return;
            }
            if (network != null && pipeNetwork != network) {
                context.fail("Expected all pipes to be in the same network", pos);
                return;
            }
            network = pipeNetwork;
        }
        context.succeed();
    }

    /**
     * Loads the wires again and checks that every group of connected wires of the same tier ends up in one network.
     */
    private void loadAndCheckWires(GameTestHelper context, List<List<BlockPos>> groups) {
        List<BlockPos> all = groups.stream().flatMap(List::stream).toList();
        reload(context, all);
        try {
            // Like a machine pushing energy into the first wire
            EnergyStorage.SIDED.find(context.getLevel(), context.absolutePos(all.getFirst()), Direction.UP);
        } catch (StackOverflowError e) {
            context.fail("Building the wire network didn't finish");
            return;
        }

        for (List<BlockPos> group : groups) {
            WireNetwork network = null;
            for (BlockPos pos : group) {
                Wire wire = (Wire) context.getBlockEntity(pos);
                // Networks are built when they are needed, so a group may not have one yet
                WireNetwork wireNetwork = wire.getNetwork();
                if (wireNetwork == null) {
                    EnergyStorage.SIDED.find(context.getLevel(), context.absolutePos(pos), Direction.UP);
                    wireNetwork = wire.getNetwork();
                }
                if (wireNetwork == null || wireNetwork.markedForRemoval()) {
                    context.fail("Expected a wire network that wasn't removed, but found " + wireNetwork, pos);
                    return;
                }
                if (network != null && wireNetwork != network) {
                    context.fail("Expected all connected wires to be in the same network", pos);
                    return;
                }
                network = wireNetwork;
            }
        }
        context.succeed();
    }

    private static List<BlockPos> line(int z, int length) {
        List<BlockPos> line = new ArrayList<>();
        for (int x = 0; x < length; x++) line.add(new BlockPos(x, 1, z));
        return line;
    }

    private static void place(GameTestHelper context, List<BlockPos> positions, Block block) {
        for (BlockPos pos : positions) {
            context.setBlock(pos, block);
            // Like a player placing it, so it knows its connections
            BlockPos absolute = context.absolutePos(pos);
            block.setPlacedBy(context.getLevel(), absolute, context.getLevel().getBlockState(absolute), null, ItemStack.EMPTY);
        }
    }

    /**
     * Replaces the block entities with fresh ones loaded from their saved data, without networks, like loading a chunk does.
     */
    private static void reload(GameTestHelper context, List<BlockPos> positions) {
        ServerLevel level = context.getLevel();
        for (BlockPos relative : positions) {
            BlockPos pos = context.absolutePos(relative);
            CompoundTag tag = level.getBlockEntity(pos).saveWithFullMetadata(level.registryAccess());
            level.removeBlockEntity(pos);
            level.setBlockEntity(BlockEntity.loadStatic(pos, level.getBlockState(pos), tag, level.registryAccess()));
        }
    }
}
