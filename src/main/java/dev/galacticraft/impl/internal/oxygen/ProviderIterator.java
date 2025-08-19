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
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ProviderIterator implements Iterator<AtmosphereProvider> {
    private final LevelChunk chunk;
    private final Iterator<BlockPos> iterator;
    private AtmosphereProvider next = null;

    public ProviderIterator(LevelChunk chunk, Iterator<BlockPos> iterator) {
        this.chunk = chunk;
        this.iterator = iterator;
        if (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (this.chunk.getLevel().getBlockEntity(pos) instanceof AtmosphereProvider provider) {
                this.next = provider;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public AtmosphereProvider next() {
        if (this.next == null) throw new NoSuchElementException();
        AtmosphereProvider result = this.next;
        this.next = null;
        if (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (this.chunk.getLevel().getBlockEntity(pos) instanceof AtmosphereProvider provider) {
                this.next = provider;
            } else {
                for (LevelChunkSection section : this.chunk.getSections()) {
                    ((ChunkSectionOxygenAccessor) section).galacticraft$deallocate(pos); //todo dirty
                }
            }
        }

        return result;
    }
}
