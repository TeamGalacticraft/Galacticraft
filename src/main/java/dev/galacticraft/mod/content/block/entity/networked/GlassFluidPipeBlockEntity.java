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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.Pullable;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GlassFluidPipeBlockEntity extends PipeBlockEntity implements Pullable {
    public static final List<Block> COMPATIBLE_BLOCKS = Util.make(new ArrayList<>(), list -> {
        list.addAll(GCBlocks.GLASS_FLUID_PIPES.values());
        list.add(GCBlocks.FLUID_PIPE_WALKWAY);
        list.addAll(GCBlocks.SEALABLE_FLUID_PIPES.values());
    });

    private boolean pull;
    private @NotNull FluidVariant displayedFluid = FluidVariant.blank();

    public GlassFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.GLASS_FLUID_PIPE, pos, state, FluidConstants.BUCKET / 50); //0.4B/s
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.readPullNbt(nbt);

        // Only sent to the client, never saved
        this.displayedFluid = nbt.contains(Constant.Nbt.DISPLAYED_FLUID)
                ? FluidVariant.CODEC.parse(registryLookup.createSerializationContext(NbtOps.INSTANCE), nbt.get(Constant.Nbt.DISPLAYED_FLUID)).result().orElse(FluidVariant.blank())
                : FluidVariant.blank();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        this.writePullNbt(nbt);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbt = super.getUpdateTag(registryLookup);
        if (!this.displayedFluid.isBlank()) {
            FluidVariant.CODEC.encodeStart(registryLookup.createSerializationContext(NbtOps.INSTANCE), this.displayedFluid).result()
                    .ifPresent(tag -> nbt.put(Constant.Nbt.DISPLAYED_FLUID, tag));
        }
        return nbt;
    }

    @Override
    public boolean isPull() {
        return this.pull;
    }

    @Override
    public void setPull(boolean pull) {
        this.pull = pull;
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
}
