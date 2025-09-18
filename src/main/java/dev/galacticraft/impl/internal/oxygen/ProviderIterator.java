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

package dev.galacticraft.impl.internal.oxygen;

import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessorInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ProviderIterator implements Iterator<AtmosphereProvider> {
    private final BlockGetter level;
    private final ChunkAccess access;
    private final ListIterator<BlockPos> positions;
    private final int y;
    private AtmosphereProvider next;

    public ProviderIterator(BlockGetter level, ChunkAccess access, ListIterator<BlockPos> positions, int y) {
        this.level = level;
        this.access = access;
        this.positions = positions;
        this.y = y;
        this.next = this.computeNext();
    }

    private AtmosphereProvider computeNext() {
        if (this.positions.hasNext()) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.positions.next());
            if (blockEntity instanceof AtmosphereProvider provider) {
                return provider;
            } else {
                this.positions.remove();
            }
            return this.computeNext();
        }
        return null;
    }


    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public AtmosphereProvider next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        AtmosphereProvider next1 = this.next;
        this.next = this.computeNext();
        return next1;
    }

    @Override
    public void remove() {
        this.positions.previous();
        this.positions.remove();
        ((ChunkOxygenAccessorInternal) this.access).galacticraft$markSectionDirty(this.y);
    }
}
