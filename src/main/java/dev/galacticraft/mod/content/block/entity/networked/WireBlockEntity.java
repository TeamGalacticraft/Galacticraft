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
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.api.wire.NetworkId;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WireBlockEntity extends BlockEntity implements Wire {
    private NetworkId network = null;

    protected WireBlockEntity(BlockEntityType<? extends WireBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.WIRE, pos, state);
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
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        if (this.network != null) nbt.putUUID(Constant.Nbt.NETWORK, this.network.uuid());
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.network = nbt.hasUUID(Constant.Nbt.NETWORK) ? new NetworkId(nbt.getUUID(Constant.Nbt.NETWORK), ((WireBlock) this.getBlockState().getBlock()).getThroughput()) : null;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (this.level != null && !this.level.isClientSide) {
            ((WireNetworkAccessor) this.level).galacticraft$getWireNetworkManager().enqueueWireLoaded(this.worldPosition, this);
        }
    }
}