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

package dev.galacticraft.mod.machine;

import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class SealerGroupings {
    Set<OxygenSealerBlockEntity> sealers;
    int sealingPower;
    int totalSealedBlocks;
    int totalOutsideBlocks;

    Set<BlockPos> calculatedArea;
    Set<BlockPos> uncalculatedArea;

    boolean sealed;

    public SealerGroupings()
    {
        sealers = new HashSet<>();
        calculatedArea = new HashSet<>();
        uncalculatedArea = new HashSet<>();
        sealingPower = 0;
        totalSealedBlocks = 0;
        totalOutsideBlocks = 0;
        sealed = false;
    }

    public void add(OxygenSealerBlockEntity sealer) {
        sealers.add(sealer);
        sealingPower += sealer.getSealingPower();
    }

    public void add(Set<OxygenSealerBlockEntity> newSealers) {
        for (OxygenSealerBlockEntity sealer : newSealers)
        {
            sealers.add(sealer);
            sealingPower += sealer.getSealingPower();
        }
    }

    public void remove(OxygenSealerBlockEntity sealer) {
        sealers.remove(sealer);
        sealingPower -= sealer.getSealingPower();
    }

    public void setTotalSealedBlocks(int totalSealedBlocks) {
        this.totalSealedBlocks = totalSealedBlocks;
    }

    public void setTotalOutsideBlocks(int totalOutsideBlocks) {
        this.totalOutsideBlocks = totalOutsideBlocks;
    }

    public int getTotalInsideBlocks() {
        return totalSealedBlocks;
    }

    public int getTotalOutsideBlocks() {
        return totalOutsideBlocks;
    }

    public void changeTotalSealedBlocks(int i) {
        this.totalSealedBlocks += i;
    }

    public void changeTotalOutsideBlocks(int i) {
        this.totalOutsideBlocks += i;
    }

    public void setCalculatedArea(Set<BlockPos> calculatedArea) {
        this.calculatedArea = calculatedArea;
    }

    public void addCalculatedArea(Set<BlockPos> calculatedArea) {
        this.calculatedArea.addAll(calculatedArea);
    }

    public void addCalculatedArea(BlockPos calculatedArea) {
        this.calculatedArea.add(calculatedArea);
    }

    public void removeCalculatedArea(Set<BlockPos> calculatedArea) {
        this.calculatedArea.removeAll(calculatedArea);
    }

    public void removeCalculatedArea(BlockPos calculatedArea) {
        this.calculatedArea.remove(calculatedArea);
    }


    public void setUncalculatedArea(Set<BlockPos> uncalculatedArea) {
        this.uncalculatedArea = uncalculatedArea;
    }

    public void addUncalculatedArea(Set<BlockPos> uncalculatedArea) {
        this.uncalculatedArea.addAll(uncalculatedArea);
    }

    public void removeUncalculatedArea(Set<BlockPos> uncalculatedArea) {
        this.uncalculatedArea.removeAll(uncalculatedArea);
    }

    public void removeUncalculatedArea(BlockPos uncalculatedArea) {
        this.uncalculatedArea.remove(uncalculatedArea);
    }

    public Set<BlockPos> getCalculatedArea() {
        return this.calculatedArea;
    }

    public Set<BlockPos> getUncalculatedArea() {
        return this.uncalculatedArea;
    }

    public void set(SealerGroupings sealerGroup) {
        this.sealers = sealerGroup.sealers;
        this.sealingPower = sealerGroup.sealingPower;
        this.totalSealedBlocks = sealerGroup.totalSealedBlocks;
        this.totalOutsideBlocks = sealerGroup.totalOutsideBlocks;
        this.calculatedArea = sealerGroup.calculatedArea;
        this.uncalculatedArea = sealerGroup.uncalculatedArea;
        this.sealed = sealerGroup.sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public boolean getSealed() {
        return this.sealed;
    }

    public Set<OxygenSealerBlockEntity> getSealers() {
        return this.sealers;
    }

    public boolean getBreathable() {
        if (this.totalOutsideBlocks == 0 && this.totalSealedBlocks <= this.sealingPower)
        {
            return true;
        }
        return false;
    }
}
