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

package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessorInternal;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenSyncer;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(EmptyLevelChunk.class)
public abstract class EmptyChunkMixin implements ChunkOxygenAccessor, ChunkOxygenSyncer, ChunkOxygenAccessorInternal {
    @Override
    public boolean isBreathable(int x, int y, int z) {
        return false;
    }

    @Override
    public void setBreathable(int x, int y, int z, boolean value) {
    }

    @Override
    public boolean getDefaultBreathable() {
        return false;
    }

    @Override
    public void setDefaultBreathable(boolean breathable) {
    }
}
