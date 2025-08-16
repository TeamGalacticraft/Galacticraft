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
import dev.galacticraft.machinelib.api.transfer.ResourceFlow;
import dev.galacticraft.machinelib.api.transfer.ResourceType;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.EnergyStorageModuleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@TestSuite("wire")
public class WireTransferTests extends SimpleGameTest {

    @BasicTest(structure = EMPTY_STRUCTURE)
    public Runnable simpleTransfer(GameTestHelper context) {
        var wirePos = new BlockPos(4, 4, 4);
        var sourcePos = wirePos.relative(Direction.UP);
        var sinkPos = wirePos.relative(Direction.DOWN);

        context.setBlock(wirePos, GCBlocks.ALUMINUM_WIRE);
        context.setBlock(sourcePos, GCBlocks.ENERGY_STORAGE_MODULE);
        context.setBlock(sinkPos, GCBlocks.ENERGY_STORAGE_MODULE);

        var source = (EnergyStorageModuleBlockEntity) context.getBlockEntity(sourcePos);
        var wire = (Wire) context.getBlockEntity(wirePos);
        var sink = (EnergyStorageModuleBlockEntity) context.getBlockEntity(sinkPos);

        source.getIOConfig().get(BlockFace.BOTTOM).setOption(ResourceType.ENERGY, ResourceFlow.OUTPUT);
        sink.getIOConfig().get(BlockFace.TOP).setOption(ResourceType.ENERGY, ResourceFlow.INPUT);

        if (wire.getNetwork() == null) context.fail("Expected network to be created!");

        source.energyStorage().setEnergy(source.energyStorage().getCapacity());
        sink.energyStorage().setEnergy(0);

        return () -> {
            if (source.energyStorage().getAmount() == source.energyStorage().getCapacity()) context.fail("Expected energy to be sent!", sourcePos);
            if (sink.energyStorage().getAmount() == 0) context.fail("Expected energy to be received!", sinkPos);
            if (source.energyStorage().getAmount() + sink.energyStorage().getAmount() < source.energyStorage().getCapacity()) context.fail("Energy lost in transfer!", wirePos);
        };
    }

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        return super.registerTests();
    }
}
