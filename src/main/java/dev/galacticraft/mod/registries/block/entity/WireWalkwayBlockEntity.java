/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.registries.block.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class WireWalkwayBlockEntity extends WireBlockEntity implements Walkway {
    private Direction direction = null;

    public WireWalkwayBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.WIRE_WALKWAY, pos, state, 240);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putByte(Constant.Nbt.DIRECTION, (byte) Objects.requireNonNullElse(this.direction, Direction.UP).ordinal());
        nbt.putByte(Constant.Nbt.DIRECTION, (byte) Objects.requireNonNullElse(this.direction, Direction.UP).ordinal());
    }

    @Override
    public void load(CompoundTag nbt) {
        this.direction = Constant.Misc.DIRECTIONS[nbt.getByte(Constant.Nbt.DIRECTION)];
        super.load(nbt);
        assert this.level != null;
        if (this.level.isClientSide) Minecraft.getInstance().levelRenderer.setSectionDirty(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ());
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
        this.getConnections()[direction.ordinal()] = false;
        level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
    }

    @Override
    public boolean canConnect(Direction direction) {
        if (this.direction == null) return false;
        return direction != this.direction;
    }
}
