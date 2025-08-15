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

package dev.galacticraft.mod.content.block.entity.networked;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.WireNetworkAccessor;
import dev.galacticraft.mod.api.wire.NetworkId;
import dev.galacticraft.mod.api.wire.Wire;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WireBlockEntity extends BlockEntity implements Wire {
    private NetworkId network = null;
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
    public NetworkId getNetwork() {
        return this.network;
    }

    @Override
    public void setNetwork(NetworkId network) {
        this.network = network;
    }

    @Override
    public int getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        this.writeConnectionNbt(nbt);
        nbt.putUUID(Constant.Nbt.NETWORK, this.network.uuid());
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.readConnectionNbt(nbt);
        this.network = nbt.hasUUID(Constant.Nbt.NETWORK) ? new NetworkId(nbt.getUUID(Constant.Nbt.NETWORK), this.maxTransferRate) : null;

        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (this.level != null && !this.level.isClientSide) {
            ((WireNetworkAccessor) this.level).galacticraft$getWireNetworkManager().enqueueWireLoaded(this.worldPosition, this);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag tag = this.saveWithoutMetadata(registryLookup);
        tag.remove(Constant.Nbt.NETWORK);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setConnected(@NotNull Direction direction, boolean connected) {
        this.connections[direction.get3DDataValue()] = connected;
    }

    @Override
    public boolean isConnected(@NotNull Direction direction) {
        return this.connections[direction.get3DDataValue()];
    }
}