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

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Region {

    boolean sealed;
    Set<BlockPos> insideBlocks;
    Set<BlockPos> outsideBlocks;
    SealerGroupings sealers;

    public Region(Set<BlockPos> insideBlocks, Queue<BlockPos> outsideBlocks, boolean sealed, SealerGroupings sealers) {
        this.insideBlocks = insideBlocks;
        this.outsideBlocks = new HashSet<>(outsideBlocks);
        this.sealed = sealed;
        this.sealers = sealers;
    }

    public Set<BlockPos> getInsideBlocks() {
        return this.insideBlocks;
    }

    public Set<BlockPos> getOutsideBlocks() {
        return this.outsideBlocks;
    }

    public boolean isSealed() {
        return sealed;
    }

    public SealerGroupings getSealerGroups() {
        return this.sealers;
    }
}
