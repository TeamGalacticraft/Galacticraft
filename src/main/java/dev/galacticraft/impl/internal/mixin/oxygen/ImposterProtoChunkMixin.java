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

package dev.galacticraft.impl.internal.mixin.oxygen;

import com.google.common.collect.Iterators;
import dev.galacticraft.api.accessor.ChunkOxygenAccessor;
import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenSyncer;
import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(ImposterProtoChunk.class)
public abstract class ImposterProtoChunkMixin implements ChunkOxygenAccessor, ChunkOxygenSyncer {
    @Shadow
    @Final
    private boolean allowWrites;
    @Shadow
    @Final
    private LevelChunk wrapped;

    @Override
    public Iterator<AtmosphereProvider> galacticraft$getProviders(int y) {
        Iterator<AtmosphereProvider> iterator = ((ChunkOxygenAccessor) this.wrapped).galacticraft$getProviders(y);
        return this.allowWrites ? iterator : Iterators.unmodifiableIterator(iterator); //todo: removal based on loading can still occur with blocked writes
    }

    @Override
    public Iterator<BlockPos> galacticraft$getProviderPositions(int y) {
        Iterator<BlockPos> iterator = ((ChunkOxygenAccessor) this.wrapped).galacticraft$getProviderPositions(y);
        return this.allowWrites ? iterator : Iterators.unmodifiableIterator(iterator);
    }

    @Override
    public void galacticraft$markSectionDirty(int sectionIndex) {
        if (this.allowWrites) {
            ((ChunkOxygenAccessor) this.wrapped).galacticraft$markSectionDirty(sectionIndex);
        }
    }

    @Override
    public OxygenUpdatePayload.OxygenData[] galacticraft$getPendingOxygenChanges() {
        return ((ChunkOxygenSyncer) this.wrapped).galacticraft$getPendingOxygenChanges();
    }

    @Override
    public void galacticraft$addAtmosphericProvider(int sectionIndex, BlockPos provider) {
        if (this.allowWrites) {
            ((ChunkOxygenAccessor) this.wrapped).galacticraft$addAtmosphericProvider(sectionIndex, provider);
        }
    }

    @Override
    public void galacticraft$removeAtmosphericProvider(int sectionIndex, BlockPos provider) {
        if (this.allowWrites) {
            ((ChunkOxygenAccessor) this.wrapped).galacticraft$removeAtmosphericProvider(sectionIndex, provider);
        }
    }
}
