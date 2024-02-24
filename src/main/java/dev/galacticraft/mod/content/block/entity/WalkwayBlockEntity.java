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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WalkwayBlockEntity extends BlockEntity implements Walkway {
    private Direction direction;
    private final boolean[] connections = new boolean[6];

    public WalkwayBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.WALKWAY, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.writeConnectionNbt(nbt);
        this.writeWalkwayNbt(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.readConnectionNbt(nbt);
        this.readWalkwayNbt(nbt);
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
    public void updateConnection(BlockState state, BlockPos pos, BlockPos neighborPos, Direction direction) {
        if (this.getDirection() != direction) {
            if (this.level.getBlockEntity(this.getBlockPos().relative(direction)) instanceof WalkwayBlockEntity walkway) {
                if (walkway.getDirection() != direction.getOpposite()) {
                    this.getConnections()[direction.ordinal()] = true;
                }
            }
        }
        this.getConnections()[direction.ordinal()] = false;
    }

    @Override
    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
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