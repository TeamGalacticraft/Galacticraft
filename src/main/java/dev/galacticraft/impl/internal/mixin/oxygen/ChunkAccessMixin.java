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
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.network.s2c.OxygenUpdatePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.BitSet;
import java.util.Iterator;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements ChunkOxygenAccessor, ChunkOxygenSyncer {
    @Shadow @Final protected LevelChunkSection[] sections;
    @Shadow @Final protected LevelHeightAccessor levelHeightAccessor;
    @Unique
    private short dirtySections = 0b0;

    @Override
    public Iterator<BlockPos> galacticraft$getHandlers(int x, int y, int z) {
        return ((ChunkSectionOxygenAccessor) this.sections[this.levelHeightAccessor.getSectionIndex(y)]).galacticraft$get(x, y & 15, z);
    }

    @Override
    public void galacticraft$markSectionDirty(int y) {
        this.dirtySections |= (short) (0b1 << y);
    }

    @Override
    public @Nullable OxygenUpdatePayload.OxygenData[] galacticraft$syncOxygenPacketsToClient() {
        if (this.dirtySections != 0b0) {
            int count = 0;
            for (int i = 0; i < this.sections.length; i++) {
                if ((this.dirtySections & (0b1 << i)) != 0) {
                    count++;
                }
            }

            OxygenUpdatePayload.OxygenData[] data = new OxygenUpdatePayload.OxygenData[count];

            int idx = 0;
            for (byte i = 0; i < this.sections.length; i++) {
                if ((this.dirtySections & (0b1 << i)) != 0) {
                    BitSet data1 = ((ChunkSectionOxygenAccessor) this.sections[i]).galacticraft$getBits();
                    data[idx++] = new OxygenUpdatePayload.OxygenData(i, data1 == null ? new BitSet(0) : data1);
                }
            }
            this.dirtySections = 0;
            return data;
        }
        return null;
    }

    @Override
    public void galacticraft$readOxygenUpdate(@NotNull OxygenUpdatePayload.OxygenData[] buf) {
        for (OxygenUpdatePayload.@NotNull OxygenData oxygenData : buf) {
            ((ChunkSectionOxygenAccessor) this.sections[oxygenData.section()]).galacticraft$setBits(oxygenData.data());
        }
    }
}
