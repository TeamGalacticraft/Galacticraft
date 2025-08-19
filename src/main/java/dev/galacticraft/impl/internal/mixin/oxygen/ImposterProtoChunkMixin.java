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

import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessor;
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
    public Iterator<BlockPos> galacticraft$getHandlers(int x, int y, int z) {
        return ((ChunkOxygenAccessor) this.wrapped).galacticraft$getHandlers(x, y, z);
    }

    @Override
    public void galacticraft$markSectionDirty(int y) {
        if (this.allowWrites) {
            ((ChunkOxygenAccessor) this.wrapped).galacticraft$markSectionDirty(y);
        }
    }

    @Override
    public OxygenUpdatePayload.OxygenData[] galacticraft$getPendingOxygenChanges() {
        return ((ChunkOxygenSyncer) this.wrapped).galacticraft$getPendingOxygenChanges();
    }
}
