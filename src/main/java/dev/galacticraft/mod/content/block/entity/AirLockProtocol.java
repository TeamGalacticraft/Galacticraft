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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.content.block.special.AirlockBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.HashSet;

class AirLockProtocol {
    private ArrayList<BlockPos> adjacentAirLocks;
    private HashSet<BlockPos> checked;
    private final Level world;
    private final BlockEntity head;
    private final int maxLoops;

    public int minX = 6000000;
    public int maxX = -6000000;
    public int minY = 6000000;
    public int maxY = -6000000;
    public int minZ = 6000000;
    public int maxZ = -6000000;

    public AirLockProtocol(BlockEntity head) {
        this.adjacentAirLocks = new ArrayList<>();
        this.checked = new HashSet<>();
        this.world = head.getLevel();
        this.head = head;
        this.maxLoops = 26;
    }

    public boolean isValidFrame(BlockPos pos) {
        return this.world.getBlockState(pos).getBlock() instanceof AirlockBlock;
    }

    private void loopThrough(BlockPos pos, int loops) {
        int xAligned = this.head.getBlockPos().getX();
        int zAligned = this.head.getBlockPos().getZ();
        for (int x = -1; x <= 1; x++) {
            int xTest = pos.getX() + x;
            for (int z = -1; z <= 1; z++) {
                int zTest = pos.getZ() + z;

                if ((xTest == xAligned || zTest == zAligned)) {
                    for (int y = -1; y <= 1; y++) {
                        if (!(x == 0 && y == 0 && z == 0)) {
                            final BlockPos testPos = new BlockPos(xTest, pos.getY() + y, zTest);
                            if (!this.checked.contains(testPos)) {
                                this.checked.add(testPos);
                                if (this.isValidFrame(testPos)) {
                                    this.adjacentAirLocks.add(testPos);
                                    if (loops > 1) {
                                        this.loopThrough(testPos, loops - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void loopThroughHorizontal(BlockPos pos, int loops) {
        int yTest = pos.getY();
        for (int x = -1; x <= 1; x++) {
            int xTest = pos.getX() + x;
            for (int z = -1; z <= 1; z++) {
                if (!(x == 0 && z == 0)) {
                    final BlockPos testPos = new BlockPos(xTest, yTest, pos.getZ() + z);
                    if (!this.checked.contains(testPos)) {
                        this.checked.add(testPos);
                        if (this.isValidFrame(testPos)) {
                            this.adjacentAirLocks.add(testPos);
                            if (loops > 1) {
                                this.loopThroughHorizontal(testPos, loops - 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public int calculate(boolean horizontal) {
        if (this.world.isClientSide()) {
            return -1;
        }

        this.minX = 6000000;
        this.maxX = -6000000;
        this.minY = 6000000;
        this.maxY = -6000000;
        this.minZ = 6000000;
        this.maxZ = -6000000;

        this.adjacentAirLocks = new ArrayList<>();
        this.checked.clear();
        final BlockPos headPos = this.head.getBlockPos();
        this.checked.add(headPos);
        this.adjacentAirLocks.add(headPos);

        if (horizontal) {
            this.loopThroughHorizontal(headPos, this.maxLoops);
        } else {
            this.loopThrough(headPos, this.maxLoops);
        }

        for (final BlockPos airLock : this.adjacentAirLocks) {
            if (airLock.getX() < this.minX) {
                this.minX = airLock.getX();
            }

            if (airLock.getX() > this.maxX) {
                this.maxX = airLock.getX();
            }

            if (airLock.getY() < this.minY) {
                this.minY = airLock.getY();
            }

            if (airLock.getY() > this.maxY) {
                this.maxY = airLock.getY();
            }

            if (airLock.getZ() < this.minZ) {
                this.minZ = airLock.getZ();
            }

            if (airLock.getZ() > this.maxZ) {
                this.maxZ = airLock.getZ();
            }
        }

        final int count = this.maxX - this.minX + this.maxZ - this.minZ + this.maxY - this.minY;

        if (count > 24 || this.maxX - this.minX <= 1 && this.maxZ - this.minZ <= 1 || !horizontal && this.maxY - this.minY <= 1) {
            return -1;
        }

        if (horizontal && (this.maxX - this.minX <= 1 || this.maxZ - this.minZ <= 1)) {
            return -1;
        }

        if (horizontal ? this.incompleteFrameHorizontal() : this.incompleteFrame()) {
            return -1;
        }

        return this.adjacentAirLocks.size();
    }

    private boolean incompleteFrame() {
        for (int y = this.minY + 1; y < this.maxY; y++) {
            if (!this.isValidFrame(new BlockPos(this.minX, y, this.minZ))) {
                return true;
            } else if (!this.isValidFrame(new BlockPos(this.maxX, y, this.maxZ))) {
                return true;
            }
        }

        if (this.minX < this.maxX) {
            for (int x = this.minX + 1; x < this.maxX; x++) {
                if (!this.isValidFrame(new BlockPos(x, this.maxY, this.maxZ))) {
                    return true;
                } else if (!this.isValidFrame(new BlockPos(x, this.minY, this.maxZ))) {
                    return true;
                }
            }
        } else if (this.minZ < this.maxZ) {
            for (int z = this.minZ + 1; z < this.maxZ; z++) {
                if (!this.isValidFrame(new BlockPos(this.maxX, this.maxY, z))) {
                    return true;
                } else if (!this.isValidFrame(new BlockPos(this.maxX, this.minY, z))) {
                    return true;
                }
            }
        } else {
            return true;
        }

        return false;
    }

    private boolean incompleteFrameHorizontal() {
        for (int x = this.minX + 1; x < this.maxX; x++) {
            if (!this.isValidFrame(new BlockPos(x, this.maxY, this.maxZ))) {
                return true;
            } else if (!this.isValidFrame(new BlockPos(x, this.minY, this.maxZ))) {
                return true;
            }
        }

        for (int z = this.minZ + 1; z < this.maxZ; z++) {
            if (!this.isValidFrame(new BlockPos(this.maxX, this.maxY, z))) {
                return true;
            } else if (!this.isValidFrame(new BlockPos(this.maxX, this.minY, z))) {
                return true;
            }
        }

        return false;
    }
}