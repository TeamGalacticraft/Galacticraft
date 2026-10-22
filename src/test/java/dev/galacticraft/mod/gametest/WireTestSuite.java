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
import dev.galacticraft.mod.api.block.entity.WalkwayHolder;
import dev.galacticraft.mod.api.grid.GridContainer;
import dev.galacticraft.mod.api.grid.GridTypes;
import dev.galacticraft.mod.api.grid.energy.EnergyGrid;
import dev.galacticraft.mod.api.grid.energy.EnergyGridNode;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.content.block.special.walkway.WalkwayAttachable;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

/**
 * Tests for wires and their {@link EnergyGrid}s.
 */
public class WireTestSuite implements GalacticraftGameTest {
    static final String GRID_ROOM = "galacticraft-test:grid_room";

    @GameTest(template = GRID_ROOM)
    public void straightLineHasTwoNodesTest(GameTestHelper context) {
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 9, 0);

        EnergyGrid grid = this.grid(context, 0, 0);
        for (int x = 1; x <= 9; x++) {
            context.assertTrue(this.grid(context, x, 0) == grid, "Expected wire %s to be in the same grid".formatted(x));
        }
        this.assertShape(context, grid, 2, 1);
        context.assertTrue(this.node(context, 0, 0) != null && this.node(context, 9, 0) != null, "Expected nodes at both ends");
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void cornerAndJunctionAreNodesTest(GameTestHelper context) {
        // An L: x 0..4 along z 0, then z 1..4 at x 4
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 4, 0);
        for (int z = 1; z <= 4; z++) {
            context.setBlock(new BlockPos(4, 1, z), GCBlocks.ALUMINUM_WIRE);
        }
        this.assertShape(context, this.grid(context, 0, 0), 3, 2);
        context.assertTrue(this.node(context, 4, 0) != null, "Expected a node at the corner");

        // A branch from the middle of the first leg makes a junction there
        context.setBlock(new BlockPos(2, 1, 1), GCBlocks.ALUMINUM_WIRE);
        context.setBlock(new BlockPos(2, 1, 2), GCBlocks.ALUMINUM_WIRE);
        EnergyGrid grid = this.grid(context, 0, 0);
        this.assertShape(context, grid, 5, 4);
        EnergyGridNode junction = this.node(context, 2, 0);
        context.assertTrue(junction != null && grid.nodeGraph.degree(junction) == 3, "Expected a junction node with 3 edges");
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void removingMiddleWireSplitsGridTest(GameTestHelper context) {
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 4, 0);
        this.grid(context, 0, 0).getStorage().setEnergy(1000);

        context.setBlock(new BlockPos(2, 1, 0), Blocks.AIR);

        EnergyGrid left = this.grid(context, 0, 0);
        EnergyGrid right = this.grid(context, 4, 0);
        context.assertTrue(left != right, "Expected two grids after removing the middle wire");
        context.assertTrue(left == this.grid(context, 1, 0) && right == this.grid(context, 3, 0), "Expected the remaining wires in the grid of their side");
        this.assertShape(context, left, 2, 1);
        this.assertShape(context, right, 2, 1);
        long total = left.getStorage().getEnergy() + right.getStorage().getEnergy();
        context.assertTrue(total == 1000 && left.getStorage().getEnergy() == 500, "Expected the energy divided by node count, got %s and %s".formatted(left.getStorage().getEnergy(), right.getStorage().getEnergy()));
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void placingWireBetweenGridsMergesThemTest(GameTestHelper context) {
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 1, 0);
        this.line(context, GCBlocks.ALUMINUM_WIRE, 3, 4, 0);
        this.grid(context, 0, 0).getStorage().setEnergy(300);
        this.grid(context, 4, 0).getStorage().setEnergy(200);
        context.assertTrue(this.grid(context, 0, 0) != this.grid(context, 4, 0), "Expected two grids");

        context.setBlock(new BlockPos(2, 1, 0), GCBlocks.ALUMINUM_WIRE);
        EnergyGrid grid = this.grid(context, 0, 0);
        context.assertTrue(grid == this.grid(context, 4, 0), "Expected one grid after placing the wire in between");
        this.assertShape(context, grid, 2, 1);
        context.assertTrue(grid.getStorage().getEnergy() == 500, "Expected the energy of both grids, got %s".formatted(grid.getStorage().getEnergy()));
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void tiersAreSeparateGridsTest(GameTestHelper context) {
        context.setBlock(new BlockPos(0, 1, 0), GCBlocks.HEAVY_ALUMINUM_WIRE);
        this.line(context, GCBlocks.ALUMINUM_WIRE, 1, 3, 0);
        context.setBlock(new BlockPos(4, 1, 0), GCBlocks.HEAVY_ALUMINUM_WIRE);

        EnergyGrid normal = this.grid(context, 2, 0);
        context.assertTrue(normal != this.grid(context, 0, 0) && normal != this.grid(context, 4, 0) && this.grid(context, 0, 0) != this.grid(context, 4, 0),
                "Expected the heavy wires and the normal wires in separate grids");
        this.assertShape(context, normal, 2, 1);
        context.assertTrue(this.node(context, 1, 0) != null && this.node(context, 3, 0) != null, "Expected nodes where the normal wires touch the heavy wires");
        context.assertTrue(((Wire) this.wire(context, 0, 0)).isConnected(Direction.EAST), "Expected wires of different tiers to still look connected");
        EnergyGridNode heavy = this.node(context, 4, 0);
        context.assertTrue(heavy != null && heavy.getNodeCapacity() == WireBlockEntity.T2_NODE_CAPACITY, "Expected a heavy wire node to add the heavy buffer size");
        context.assertTrue(this.node(context, 3, 0).getNodeCapacity() == WireBlockEntity.T1_NODE_CAPACITY, "Expected a normal wire node to add the normal buffer size");
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void transferBetweenTiersIsLimitedByLowerTierTest(GameTestHelper context) {
        this.line(context, GCBlocks.HEAVY_ALUMINUM_WIRE, 0, 2, 0);
        this.line(context, GCBlocks.ALUMINUM_WIRE, 3, 5, 0);
        EnergyGrid heavy = this.grid(context, 0, 0);
        EnergyGrid normal = this.grid(context, 5, 0);
        heavy.getStorage().setEnergy(5000);
        long rate = ((Wire) this.wire(context, 3, 0)).getMaxTransferRate();

        runFinalTaskAt(context, 5, () -> {
            long moved = normal.getStorage().getEnergy();
            context.assertTrue(moved > 0, "Expected energy to move from the heavy grid to the normal grid");
            context.assertTrue(moved <= 5 * rate, "Expected at most %s per tick to pass the normal wire, but %s moved in 5 ticks".formatted(rate, moved));
            context.assertTrue(heavy.getStorage().getEnergy() + moved == 5000, "Expected no energy to be lost or made");
        });
    }

    @GameTest(template = GRID_ROOM)
    public void energyDoesNotFlowBackBetweenTiersTest(GameTestHelper context) {
        this.line(context, GCBlocks.HEAVY_ALUMINUM_WIRE, 0, 1, 0);
        this.line(context, GCBlocks.ALUMINUM_WIRE, 2, 3, 0);
        EnergyGrid heavy = this.grid(context, 0, 0);
        EnergyGrid normal = this.grid(context, 3, 0);
        heavy.getStorage().setEnergy(1000);
        normal.getStorage().setEnergy(1000);

        // Both grids are equally full, so nothing should move either way
        runFinalTaskAt(context, 3, () -> context.assertTrue(heavy.getStorage().getEnergy() == 1000 && normal.getStorage().getEnergy() == 1000,
                "Expected equally full grids to keep their energy, got %s and %s".formatted(heavy.getStorage().getEnergy(), normal.getStorage().getEnergy())));
    }

    @GameTest(template = GRID_ROOM)
    public void transferRatePerTierTest(GameTestHelper context) {
        context.setBlock(new BlockPos(0, 1, 0), GCBlocks.ALUMINUM_WIRE);
        context.setBlock(new BlockPos(0, 1, 2), GCBlocks.HEAVY_ALUMINUM_WIRE);
        EnergyStorage normal = this.storage(context, 0, 0);
        EnergyStorage heavy = this.storage(context, 0, 2);
        long normalRate = ((Wire) this.wire(context, 0, 0)).getMaxTransferRate();
        long heavyRate = ((Wire) this.wire(context, 0, 2)).getMaxTransferRate();

        context.assertTrue(this.insert(normal, 10_000) == normalRate, "Expected a normal wire to take %s per tick".formatted(normalRate));
        context.assertTrue(this.insert(normal, 10_000) == 0, "Expected a normal wire to take nothing more this tick");
        context.assertTrue(this.insert(heavy, 10_000) == heavyRate, "Expected a heavy wire to take %s per tick".formatted(heavyRate));
        runFinalTaskNext(context, () -> context.assertTrue(this.insert(normal, 10_000) == normalRate, "Expected the normal wire to take energy again next tick"));
    }

    @GameTest(template = GRID_ROOM)
    public void energyFlowsToMachineTest(GameTestHelper context) {
        BlockPos furnacePos = new BlockPos(5, 1, 0);
        context.setBlock(furnacePos, GCBlocks.ELECTRIC_FURNACE);
        MachineBlockEntity furnace = (MachineBlockEntity) context.getBlockEntity(furnacePos);
        BlockState state = context.getBlockState(furnacePos);
        furnace.getIOConfig().get(BlockFace.from(state, Direction.WEST)).setOption(ResourceType.ENERGY, ResourceFlow.INPUT);
        context.getLevel().updateNeighborsAt(context.absolutePos(furnacePos), state.getBlock());

        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 4, 0);
        context.assertTrue(this.node(context, 4, 0) != null, "Expected a node next to the furnace");
        EnergyStorage wire = this.storage(context, 0, 0);
        context.assertTrue(this.insert(wire, 100) == 100, "Expected the wire to take the energy");

        runFinalTaskAt(context, 3, () -> {
            long amount = furnace.energyStorage().getAmount();
            context.assertTrue(amount == 100, "Expected the furnace to receive 100 gJ but it has %s".formatted(amount));
            context.assertTrue(this.grid(context, 0, 0).getStorage().getEnergy() == 0, "Expected the grid to be empty");
        });
    }

    @GameTest(template = GRID_ROOM)
    public void walkwayAttachKeepsGridTest(GameTestHelper context) {
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 2, 0);
        EnergyGrid grid = this.grid(context, 0, 0);

        this.use(context, new BlockPos(1, 1, 0), new ItemStack(GCBlocks.WALKWAY), Direction.UP);
        context.assertTrue(((WalkwayHolder) this.wire(context, 1, 0)).getWalkway() == Direction.UP, "Expected the walkway to be attached");
        context.assertTrue(this.grid(context, 0, 0) == grid && this.grid(context, 2, 0) == grid, "Expected attaching a walkway to keep the grid");
        this.assertShape(context, grid, 2, 1);

        // Breaking just the walkway keeps the grid too
        this.breakPart(context, new BlockPos(1, 1, 0), new Vec3(0.5, 1.0, 0.5));
        context.assertBlockPresent(GCBlocks.ALUMINUM_WIRE, new BlockPos(1, 1, 0));
        context.assertTrue(this.grid(context, 0, 0) == grid && this.grid(context, 1, 0) == grid, "Expected removing the walkway to keep the grid");
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void breakingWireUnderWalkwaySplitsGridTest(GameTestHelper context) {
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 2, 0);
        this.use(context, new BlockPos(1, 1, 0), new ItemStack(GCBlocks.WALKWAY), Direction.UP);

        // A point on the side of the wire
        this.breakPart(context, new BlockPos(1, 1, 0), new Vec3(0.4, 0.5, 0.5));
        context.assertBlockState(new BlockPos(1, 1, 0), state -> state.is(GCBlocks.WALKWAY), () -> "Expected a standalone walkway");
        context.assertTrue(this.grid(context, 0, 0) != this.grid(context, 2, 0), "Expected the grid to split when the wire under the walkway breaks");
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void wireIntoWalkwayMergesGridsTest(GameTestHelper context) {
        context.setBlock(new BlockPos(0, 1, 0), GCBlocks.ALUMINUM_WIRE);
        context.setBlock(new BlockPos(1, 1, 0), GCBlocks.WALKWAY.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
        context.setBlock(new BlockPos(2, 1, 0), GCBlocks.ALUMINUM_WIRE);
        context.assertTrue(this.grid(context, 0, 0) != this.grid(context, 2, 0), "Expected two grids next to the walkway");

        this.use(context, new BlockPos(1, 1, 0), new ItemStack(GCBlocks.ALUMINUM_WIRE), Direction.UP);
        context.assertBlockPresent(GCBlocks.ALUMINUM_WIRE, new BlockPos(1, 1, 0));
        EnergyGrid grid = this.grid(context, 0, 0);
        context.assertTrue(grid == this.grid(context, 1, 0) && grid == this.grid(context, 2, 0), "Expected one grid after putting a wire in the walkway");
        this.assertShape(context, grid, 2, 1);
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void gridSaveRoundTripTest(GameTestHelper context) {
        this.line(context, GCBlocks.HEAVY_ALUMINUM_WIRE, 0, 4, 0);
        context.setBlock(new BlockPos(4, 1, 1), GCBlocks.HEAVY_ALUMINUM_WIRE);
        EnergyGrid grid = this.grid(context, 0, 0);
        grid.getStorage().setEnergy(1234);

        var registries = context.getLevel().registryAccess();
        CompoundTag tag = grid.save(registries);
        EnergyGrid loaded = GridTypes.ENERGY.create(grid.getId(), context.getLevel());
        loaded.load(tag, registries);

        context.assertTrue(loaded.getNodes().keySet().equals(grid.getNodes().keySet()), "Expected the same nodes after loading");
        context.assertTrue(loaded.nodeGraph.edges().size() == grid.nodeGraph.edges().size(), "Expected the same edges after loading");
        context.assertTrue(loaded.getStorage().getEnergy() == 1234, "Expected the energy to be saved");
        context.assertTrue(loaded.getNodes().get(context.absolutePos(new BlockPos(4, 1, 1))).getNodeCapacity() == WireBlockEntity.T2_NODE_CAPACITY, "Expected the heavy node buffer size to be saved");
        context.succeed();
    }

    @GameTest(template = GRID_ROOM)
    public void wiresWithoutGridAreAdoptedTest(GameTestHelper context) {
        this.line(context, GCBlocks.ALUMINUM_WIRE, 0, 3, 0);
        GridContainer container = GridContainer.get(context.getLevel());
        // Like a world from before grids: the wires exist, but no grid knows them
        for (int x = 0; x <= 3; x++) {
            container.onDuctRemoved((Wire) this.wire(context, x, 0));
        }
        context.assertTrue(this.grid(context, 0, 0) == null, "Expected the wires to have no grid");

        for (int x = 0; x <= 3; x++) {
            container.onDuctLoaded((Wire) this.wire(context, x, 0));
        }
        runFinalTaskNext(context, () -> {
            EnergyGrid grid = this.grid(context, 0, 0);
            context.assertTrue(grid != null && grid == this.grid(context, 3, 0), "Expected the loaded wires to join one grid");
            this.assertShape(context, grid, 2, 1);
        });
    }

    private void line(GameTestHelper context, Block block, int fromX, int toX, int z) {
        for (int x = fromX; x <= toX; x++) {
            context.setBlock(new BlockPos(x, 1, z), block);
        }
    }

    private Object wire(GameTestHelper context, int x, int z) {
        return context.getBlockEntity(new BlockPos(x, 1, z));
    }

    private @Nullable EnergyGrid grid(GameTestHelper context, int x, int z) {
        return this.wire(context, x, z) instanceof Wire wire ? wire.getGrid() : null;
    }

    private @Nullable EnergyGridNode node(GameTestHelper context, int x, int z) {
        return this.wire(context, x, z) instanceof Wire wire ? wire.getNode() : null;
    }

    private EnergyStorage storage(GameTestHelper context, int x, int z) {
        return EnergyStorage.SIDED.find(context.getLevel(), context.absolutePos(new BlockPos(x, 1, z)), Direction.WEST);
    }

    private long insert(EnergyStorage storage, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(amount, transaction);
            transaction.commit();
            return inserted;
        }
    }

    private void assertShape(GameTestHelper context, @Nullable EnergyGrid grid, int nodes, int edges) {
        context.assertTrue(grid != null, "Expected a grid");
        context.assertTrue(grid.getNodes().size() == nodes && grid.nodeGraph.edges().size() == edges,
                "Expected %s nodes and %s edges but found %s".formatted(nodes, edges, grid));
    }

    private void use(GameTestHelper context, BlockPos pos, ItemStack stack, Direction side) {
        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos absolute = context.absolutePos(pos);
        BlockState state = context.getLevel().getBlockState(absolute);
        state.useItemOn(stack, context.getLevel(), player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absolute), side, absolute, false));
    }

    private void breakPart(GameTestHelper context, BlockPos pos, Vec3 local) {
        BlockPos absolute = context.absolutePos(pos);
        BlockState state = context.getLevel().getBlockState(absolute);
        WalkwayHolder holder = (WalkwayHolder) context.getLevel().getBlockEntity(absolute);
        WalkwayAttachable.breakPart(context.getLevel(), context.makeMockPlayer(GameType.CREATIVE), absolute, state, holder, Vec3.atLowerCornerOf(absolute).add(local));
    }
}
