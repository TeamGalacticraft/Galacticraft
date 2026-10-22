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

package dev.galacticraft.mod.content.block.entity.networked;

import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.api.block.entity.WalkwayHolder;
import dev.galacticraft.mod.api.grid.Duct;
import dev.galacticraft.mod.api.grid.GridContainer;
import dev.galacticraft.mod.api.grid.GridType;
import dev.galacticraft.mod.api.grid.GridTypes;
import dev.galacticraft.mod.api.grid.energy.DuctEnergyStorage;
import dev.galacticraft.mod.api.grid.energy.EnergyGrid;
import dev.galacticraft.mod.api.wire.Wire;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class WireBlockEntity extends BlockEntity implements Wire, WalkwayHolder {
    /**
     * How much a wire adds to the energy buffer of its grid when it is a node.
     */
    public static final long T1_NODE_CAPACITY = 500;
    public static final long T2_NODE_CAPACITY = 1000;

    private @Nullable EnergyGrid grid;
    private final int maxTransferRate;
    private final long nodeCapacity;
    private final DuctEnergyStorage energyStorage = new DuctEnergyStorage(this);
    private final boolean[] connections = new boolean[6];
    private @Nullable Direction walkway = null;

    public WireBlockEntity(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state, int maxTransferRate, long nodeCapacity) {
        super(type, pos, state);
        this.maxTransferRate = maxTransferRate;
        this.nodeCapacity = nodeCapacity;
    }

    public static WireBlockEntity createT1(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state) {
        return new WireBlockEntity(type, pos, state, (int) Galacticraft.CONFIG.wireTransferLimit(), T1_NODE_CAPACITY);
    }

    public static WireBlockEntity createT2(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state) {
        return new WireBlockEntity(type, pos, state, (int) Galacticraft.CONFIG.heavyWireTransferLimit(), T2_NODE_CAPACITY);
    }

    @Override
    public Level getHostLevel() {
        return this.level;
    }

    @Override
    public BlockPos getHostPos() {
        return this.worldPosition;
    }

    @Override
    public GridType<EnergyGrid> getGridType() {
        return GridTypes.ENERGY;
    }

    @Override
    public @Nullable EnergyGrid getGrid() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return null;
        }
        if (this.grid == null || this.grid.isRemoved()) {
            this.grid = GridContainer.get(serverLevel).getGrid(GridTypes.ENERGY, this.worldPosition);
        }
        return this.grid;
    }

    @Override
    public void setGrid(@Nullable EnergyGrid grid) {
        this.grid = grid;
    }

    /**
     * Wires only share a grid with wires of the same tier. Wires of another tier are connected like any other block,
     * so the energy that passes between them is limited by the lower tier.
     */
    @Override
    public boolean canConnectTo(Duct<?, ?> other, Direction direction) {
        return other instanceof WireBlockEntity wire && wire.getType() == this.getType();
    }

    @Override
    public void onGridConnectionChanged(Direction direction) {
        if (this.getBlockState().getBlock() instanceof PipeShapedBlock<?> block) {
            block.refreshConnection(this.level, this.worldPosition, direction);
        }
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    @Override
    public int getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public long getNodeCapacity() {
        return this.nodeCapacity;
    }

    @Override
    public boolean[] getConnections() {
        return this.connections;
    }

    @Override
    public @Nullable Direction getWalkway() {
        return this.walkway;
    }

    @Override
    public void setWalkway(@Nullable Direction walkway) {
        this.walkway = walkway;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void updateConnection(BlockState state, BlockPos pos, BlockPos neighborPos, Direction direction) {
        // The grid follows block updates by itself, see PipeShapedBlock
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        this.writeConnectionNbt(nbt);
        this.writeWalkwayNbt(nbt);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.readConnectionNbt(nbt);
        this.walkway = WalkwayHolder.readWalkwayNbt(nbt);

        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
