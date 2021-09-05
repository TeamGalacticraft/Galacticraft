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

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.WorldRendererAccessor;
import dev.galacticraft.mod.api.block.entity.ColoredBlockEntity;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.block.special.fluidpipe.PipeBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PipeWalkwayBlockEntity extends PipeBlockEntity implements Walkway, ColoredBlockEntity {
    private Direction direction;
    private DyeColor color = DyeColor.WHITE;
    private final boolean[] connections = new boolean[6];

    public PipeWalkwayBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.PIPE_WALKWAY, pos, state, FluidAmount.of(1, 50));
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
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public boolean[] getConnections() {
        return this.connections;
    }

    @Override
    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.fromTagCommon(tag);
        ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).addChunkToRebuild(this.pos);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        this.toTagCommon(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.fromTagCommon(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        this.toTagCommon(nbt);
        return super.writeNbt(nbt);
    }

    public void fromTagCommon(NbtCompound tag) {
        this.direction = Constant.Misc.DIRECTIONS[tag.getByte(Constant.Nbt.DIRECTION)];
        this.color = DyeColor.values()[tag.getByte(Constant.Nbt.COLOR)];
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            this.connections[direction.ordinal()] = tag.getBoolean(direction.asString());
        }
    }

    public void toTagCommon(NbtCompound tag) {
        tag.putByte(Constant.Nbt.DIRECTION, (byte) Objects.requireNonNullElse(this.direction, Direction.UP).ordinal());
        tag.putByte(Constant.Nbt.COLOR, (byte) Objects.requireNonNullElse(this.color, DyeColor.WHITE).ordinal());
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            tag.putBoolean(direction.asString(), this.connections[direction.ordinal()]);
        }
    }

    @Override
    public boolean canConnect(Direction direction) {
        return direction != this.direction;
    }
}
