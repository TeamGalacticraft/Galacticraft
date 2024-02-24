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

package dev.galacticraft.mod.content.block.special.fluidpipe;

import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.api.pipe.impl.PipeNetworkImpl;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collections;
import java.util.Iterator;

public abstract class PipeBlockEntity extends BlockEntity implements Pipe, Storage<FluidVariant> {
    private @Nullable PipeNetwork network = null;
    private DyeColor color = DyeColor.WHITE;
    private final long maxTransferRate; // 1 bucket per second
    private final boolean[] connections = new boolean[6];

    public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state, long maxTransferRate) {
        super(type, pos, state);
        this.maxTransferRate = maxTransferRate;
    }

    @Override
    public void forceCreateNetwork() {
        this.createNetwork();
    }

    private void createNetwork() {
        assert this.network == null || this.network.markedForRemoval();
        if (!this.level.isClientSide) {
            this.network = new PipeNetworkImpl((ServerLevel) this.level, this.maxTransferRate, this.getBlockPos());
        }
    }

    @Override
    public void setNetwork(@Nullable PipeNetwork network) {
        if ((this.network == null || this.network.markedForRemoval()) && (network != null && !network.markedForRemoval())) {
            this.network = network;
            this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        } else {
            this.network = network;
        }
    }

    @Override
    public @Nullable PipeNetwork getNetwork() {
        return this.network;
    }

    @Override
    public Storage<FluidVariant> getInsertable() {
        if (this.network == null || this.network.markedForRemoval()) {
            this.createNetwork();
        }
        return this;
    }

    @Override
    public long getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public boolean[] getConnections() {
        return this.connections;
    }

    @Override
    public void updateConnection(BlockState state, BlockPos pos, BlockPos neighborPos, Direction direction) {
        boolean connected = this.canConnect(direction) && EnergyStorage.SIDED.find(this.level, neighborPos, direction.getOpposite()) != null;
        if (this.connections[direction.get3DDataValue()] != connected) {
            this.connections[direction.get3DDataValue()] = connected;
            this.level.sendBlockUpdated(pos, state, state, 0);
            this.level.updateNeighborsAt(pos, state.getBlock());
        }

        if (this.network == null || this.network.markedForRemoval()) {
            this.createNetwork();
        }
        this.network.updateConnection(pos, neighborPos, direction);
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        if (this.network != null) {
            return this.network.insert(resource, Math.min(this.maxTransferRate, maxAmount), transaction);
        }

        return 0;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.readColorNbt(nbt);
        this.readConnectionNbt(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.writeColorNbt(nbt);
        this.writeConnectionNbt(nbt);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }
}