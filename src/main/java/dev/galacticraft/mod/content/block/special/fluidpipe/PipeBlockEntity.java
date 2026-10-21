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

package dev.galacticraft.mod.content.block.special.fluidpipe;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidPipeBlock;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.api.block.entity.WalkwayHolder;
import dev.galacticraft.mod.api.grid.GridContainer;
import dev.galacticraft.mod.api.grid.GridHostUpdateable;
import dev.galacticraft.mod.api.grid.GridType;
import dev.galacticraft.mod.api.grid.GridTypes;
import dev.galacticraft.mod.api.grid.fluid.DuctFluidStorage;
import dev.galacticraft.mod.api.grid.fluid.FluidDuct;
import dev.galacticraft.mod.api.grid.fluid.FluidGrid;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PipeBlockEntity extends BlockEntity implements FluidPipe, WalkwayHolder, GridHostUpdateable {
    private @Nullable FluidGrid grid;
    private final long maxTransferRate;
    private final DuctFluidStorage fluidStorage = new DuctFluidStorage(this);
    private final boolean[] connections = new boolean[6];
    private @Nullable Direction walkway = null;
    private @NotNull FluidVariant displayedFluid = FluidVariant.blank();

    public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state, long maxTransferRate) {
        super(type, pos, state);
        this.maxTransferRate = maxTransferRate;
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
    public GridType<FluidGrid> getGridType() {
        return GridTypes.FLUID;
    }

    @Override
    public @Nullable FluidGrid getGrid() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return null;
        }
        if (this.grid == null || this.grid.isRemoved()) {
            this.grid = GridContainer.get(serverLevel).getGrid(GridTypes.FLUID, this.worldPosition);
        }
        return this.grid;
    }

    @Override
    public void setGrid(@Nullable FluidGrid grid) {
        this.grid = grid;
    }

    @Override
    public boolean canConnectToDuct(FluidDuct other, Direction direction) {
        return other instanceof PipeBlockEntity pipe
                && this.getBlockState().getBlock() instanceof FluidPipeBlock thisBlock
                && pipe.getBlockState().getBlock() instanceof FluidPipeBlock otherBlock
                && thisBlock.color.canConnectTo(otherBlock.color);
    }

    @Override
    public void onGridConnectionChanged(Direction direction) {
        if (this.getBlockState().getBlock() instanceof PipeShapedBlock<?> block) {
            block.refreshConnection(this.level, this.worldPosition, direction);
        }
    }

    @Override
    public void update() {
        FluidGrid grid = this.getGrid();
        this.setDisplayedFluid(grid == null ? FluidVariant.blank() : grid.getDisplayedFluid());
    }

    /**
     * {@return the fluid that is flowing through this pipe, as far as the client needs to know to render it}
     */
    public @NotNull FluidVariant getDisplayedFluid() {
        return this.displayedFluid;
    }

    /**
     * Sets the fluid flowing through this pipe and sends it to the client if it changed.
     *
     * @param fluid the fluid flowing through this pipe, or blank if nothing is flowing
     */
    public void setDisplayedFluid(@NotNull FluidVariant fluid) {
        if (this.displayedFluid.equals(fluid)) return;
        this.displayedFluid = fluid;
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Storage<FluidVariant> getFluidStorage() {
        return this.fluidStorage;
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
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.readConnectionNbt(nbt);
        this.walkway = WalkwayHolder.readWalkwayNbt(nbt);
        // Only sent to the client, never saved
        this.displayedFluid = nbt.contains(Constant.Nbt.DISPLAYED_FLUID)
                ? FluidVariant.CODEC.parse(registryLookup.createSerializationContext(NbtOps.INSTANCE), nbt.get(Constant.Nbt.DISPLAYED_FLUID)).result().orElse(FluidVariant.blank())
                : FluidVariant.blank();

        if (this.level != null && this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        this.writeConnectionNbt(nbt);
        this.writeWalkwayNbt(nbt);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbt = this.saveWithoutMetadata(registryLookup);
        if (!this.displayedFluid.isBlank()) {
            FluidVariant.CODEC.encodeStart(registryLookup.createSerializationContext(NbtOps.INSTANCE), this.displayedFluid).result()
                    .ifPresent(tag -> nbt.put(Constant.Nbt.DISPLAYED_FLUID, tag));
        }
        return nbt;
    }
}
