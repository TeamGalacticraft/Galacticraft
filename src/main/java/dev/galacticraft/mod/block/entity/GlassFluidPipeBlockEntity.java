/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.WorldRendererAccessor;
import dev.galacticraft.mod.api.block.entity.ColoredBlockEntity;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.block.special.fluidpipe.PipeBlockEntity;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class GlassFluidPipeBlockEntity extends PipeBlockEntity implements ColoredBlockEntity, Connected, BlockEntityClientSerializable {
    private DyeColor color = DyeColor.WHITE;
    private final boolean[] connections = new boolean[6];
    private boolean pull = false;

    public GlassFluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.GLASS_FLUID_PIPE, pos, state);
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
    public boolean[] getConnections() {
        return this.connections;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.readNbtCommon(nbt);
        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        this.writeNbtCommon(nbt);
        return super.writeNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbtCommon(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(this.pos);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        this.writeNbtCommon(tag);
        return tag;
    }

    public void writeNbtCommon(NbtCompound nbt) {
        nbt.putByte(Constant.Nbt.COLOR, (byte) Objects.requireNonNullElse(this.color, DyeColor.WHITE).ordinal());
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            nbt.putBoolean(direction.asString(), this.connections[direction.ordinal()]);
        }
        nbt.putBoolean(Constant.Nbt.PULL, this.pull);
    }

    public void readNbtCommon(NbtCompound nbt) {
        this.color = DyeColor.values()[nbt.getByte(Constant.Nbt.COLOR)];
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            this.connections[direction.ordinal()] = nbt.getBoolean(direction.asString());
        }
        this.pull = nbt.getBoolean(Constant.Nbt.PULL);
    }

    public boolean isPull() {
        return this.pull;
    }

    public void setPull(boolean pull) {
        this.pull = pull;
    }
}
