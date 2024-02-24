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

package dev.galacticraft.mod.content.block.entity.networked;

import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.WireNetwork;
import dev.galacticraft.mod.api.wire.impl.WireNetworkImpl;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class WireBlockEntity extends BlockEntity implements Wire, EnergyStorage {
    @Nullable
    private WireNetwork network;
    private final int maxTransferRate;
    private final boolean[] connections = new boolean[6];

    public WireBlockEntity(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state, int maxTransferRate) {
        super(type, pos, state);
        this.maxTransferRate = maxTransferRate;
    }

    public static WireBlockEntity createT1(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state) {
        return new WireBlockEntity(type, pos, state, 240);
    }

    public static WireBlockEntity createT2(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state) {
        return new WireBlockEntity(type, pos, state, 480);
    }

    @Override
    public void forceCreateNetwork() {
        this.createNetwork();
    }

    public void createNetwork() {
        assert this.network == null || this.network.markedForRemoval();
        if (!this.level.isClientSide) {
            this.network = new WireNetworkImpl((ServerLevel) this.level, this.maxTransferRate, this.getBlockPos());
        }
    }

    @Override
    public void setNetwork(@Nullable WireNetwork network) {
        if ((this.network == null || this.network.markedForRemoval()) && (network != null && !network.markedForRemoval())) {
            this.network = network;
            this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        } else {
            this.network = network;
        }
    }

    @Override
    @Nullable
    public WireNetwork getNetwork() {
        return this.network;
    }

    @Override
    public EnergyStorage getInsertable() {
        if (this.network == null || this.network.markedForRemoval()) {
            this.createNetwork();
        }
        return this;
    }

    @Override
    public int getMaxTransferRate() {
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
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.writeConnectionNbt(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.readConnectionNbt(nbt);

        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public boolean supportsInsertion() {
        return this.maxTransferRate > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        if (this.network != null) {
            return this.network.insert(Math.min(this.maxTransferRate, maxAmount), transaction);
        }

        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return true; //todo: do we want to expose extraction since we push energy out?
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return this.maxTransferRate;
    }
}