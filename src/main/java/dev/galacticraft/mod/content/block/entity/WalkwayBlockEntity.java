/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WalkwayBlockEntity extends BlockEntity implements Walkway {
    private Direction direction = null;
    private final boolean[] connections = new boolean[6];

    public WalkwayBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.WALKWAY, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        for (int i = 0; i < 6; i++) {
            nbt.putBoolean(Constant.Misc.DIRECTIONS[i].getSerializedName(), this.connections[i]);
        }
        nbt.putByte(Constant.Nbt.DIRECTION, (byte) this.direction.ordinal());
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        for (int i = 0; i < 6; i++) {
            this.connections[i] = nbt.getBoolean(Constant.Misc.DIRECTIONS[i].getSerializedName());
        }
        this.direction = Constant.Misc.DIRECTIONS[nbt.getByte(Constant.Nbt.DIRECTION)];
        assert this.level != null;
        if (this.level.isClientSide) Minecraft.getInstance().levelRenderer.setSectionDirty(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ());
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
    public void calculateConnections() {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            if (getDirection() != direction) {
                if (level.getBlockEntity(mutable.set(getBlockPos()).move(direction)) instanceof Walkway walkway) {
                    if (walkway.getDirection() != direction.getOpposite()) {
                        getConnections()[direction.ordinal()] = true;
                        continue;
                    }
                }
            }
            getConnections()[direction.ordinal()] = false;
        }
    }

    @Override
    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
    }
}
