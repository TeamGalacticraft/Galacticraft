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

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.transfer.ResourceFlow;
import dev.galacticraft.machinelib.api.transfer.ResourceType;
import dev.galacticraft.machinelib.api.util.BlockFace;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.api.grid.fluid.FluidGrid;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.block.entity.machine.RefineryBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * Tests for fluid pipes and their {@link FluidGrid}s.
 */
public class PipeTestSuite implements GalacticraftGameTest {
    @GameTest(template = WireTestSuite.GRID_ROOM)
    public void straightLineHasTwoNodesTest(GameTestHelper context) {
        this.line(context, GCBlocks.GLASS_FLUID_PIPE, 0, 6, 0);

        FluidGrid grid = this.grid(context, 0, 0);
        context.assertTrue(grid != null && grid == this.grid(context, 6, 0), "Expected the pipes in one grid");
        context.assertTrue(grid.getNodes().size() == 2 && grid.nodeGraph.edges().size() == 1, "Expected 2 nodes and 1 edge but found %s".formatted(grid));
        context.succeed();
    }

    @GameTest(template = WireTestSuite.GRID_ROOM)
    public void pipeColorsTest(GameTestHelper context) {
        Block red = GCBlocks.GLASS_FLUID_PIPES.get(PipeColor.fromDye(DyeColor.RED));
        Block blue = GCBlocks.GLASS_FLUID_PIPES.get(PipeColor.fromDye(DyeColor.BLUE));

        context.setBlock(new BlockPos(0, 1, 0), red);
        context.setBlock(new BlockPos(1, 1, 0), blue);
        context.assertTrue(this.grid(context, 0, 0) != this.grid(context, 1, 0), "Expected red and blue pipes not to connect");

        context.setBlock(new BlockPos(0, 1, 2), red);
        context.setBlock(new BlockPos(1, 1, 2), GCBlocks.GLASS_FLUID_PIPE);
        context.setBlock(new BlockPos(2, 1, 2), blue);
        FluidGrid grid = this.grid(context, 0, 2);
        context.assertTrue(grid == this.grid(context, 1, 2) && grid == this.grid(context, 2, 2), "Expected red, clear and blue pipes to form one grid");
        context.succeed();
    }

    @GameTest(template = WireTestSuite.GRID_ROOM)
    public void differentFluidsDontConnectTest(GameTestHelper context) {
        this.line(context, GCBlocks.GLASS_FLUID_PIPE, 0, 1, 0);
        this.line(context, GCBlocks.GLASS_FLUID_PIPE, 3, 4, 0);
        FluidGrid water = this.grid(context, 0, 0);
        FluidGrid lava = this.grid(context, 4, 0);
        water.getStorage().setFluid(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET);
        lava.getStorage().setFluid(FluidVariant.of(Fluids.LAVA), FluidConstants.BUCKET);

        // The new pipe joins the water grid on its west side, and stays apart from the lava grid
        context.setBlock(new BlockPos(2, 1, 0), GCBlocks.GLASS_FLUID_PIPE);
        context.assertTrue(this.grid(context, 2, 0) == this.grid(context, 0, 0), "Expected the new pipe to join the water grid");
        context.assertTrue(this.grid(context, 2, 0) != this.grid(context, 4, 0), "Expected the water and lava grids to stay apart");
        context.assertFalse(this.pipe(context, 2, 0).isConnected(Direction.EAST), "Expected the pipe not to show a connection to the lava grid");

        // Once the water is gone, the grids connect
        this.grid(context, 0, 0).getStorage().setFluid(FluidVariant.blank(), 0);
        runFinalTaskAt(context, 2, () -> {
            FluidGrid grid = this.grid(context, 0, 0);
            context.assertTrue(grid == this.grid(context, 4, 0), "Expected the grids to connect after the water grid ran empty");
            context.assertTrue(grid.getStorage().getVariant().equals(FluidVariant.of(Fluids.LAVA)), "Expected the merged grid to hold the lava");
            context.assertTrue(this.pipe(context, 2, 0).isConnected(Direction.EAST), "Expected the pipe to show the new connection");
        });
    }

    @GameTest(template = WireTestSuite.GRID_ROOM)
    public void fluidFlowsToMachineTest(GameTestHelper context) {
        BlockPos refineryPos = new BlockPos(4, 1, 0);
        context.setBlock(refineryPos, GCBlocks.REFINERY);
        MachineBlockEntity refinery = (MachineBlockEntity) context.getBlockEntity(refineryPos);
        BlockState state = context.getBlockState(refineryPos);
        refinery.getIOConfig().get(BlockFace.from(state, Direction.WEST)).setOption(ResourceType.FLUID, ResourceFlow.INPUT);
        context.getLevel().updateNeighborsAt(context.absolutePos(refineryPos), state.getBlock());

        this.line(context, GCBlocks.GLASS_FLUID_PIPE, 0, 3, 0);
        FluidGrid grid = this.grid(context, 0, 0);
        long amount = FluidConstants.BUCKET / 100;
        grid.getStorage().setFluid(FluidVariant.of(GCFluids.CRUDE_OIL), amount);

        runFinalTaskAt(context, 3, () -> {
            long stored = refinery.fluidStorage().slot(RefineryBlockEntity.OIL_TANK).getAmount();
            context.assertTrue(stored == amount, "Expected the refinery to receive %s droplets of oil but it has %s".formatted(amount, stored));
            context.assertTrue(this.pipe(context, 1, 0) instanceof PipeBlockEntity pipe
                    && pipe.getDisplayedFluid().equals(FluidVariant.of(GCFluids.CRUDE_OIL)), "Expected the pipes to show the oil");
        });
    }

    private void line(GameTestHelper context, Block block, int fromX, int toX, int z) {
        for (int x = fromX; x <= toX; x++) {
            context.setBlock(new BlockPos(x, 1, z), block);
        }
    }

    private Connected pipe(GameTestHelper context, int x, int z) {
        return (Connected) context.getBlockEntity(new BlockPos(x, 1, z));
    }

    private @Nullable FluidGrid grid(GameTestHelper context, int x, int z) {
        return context.getBlockEntity(new BlockPos(x, 1, z)) instanceof FluidPipe pipe ? pipe.getGrid() : null;
    }
}
