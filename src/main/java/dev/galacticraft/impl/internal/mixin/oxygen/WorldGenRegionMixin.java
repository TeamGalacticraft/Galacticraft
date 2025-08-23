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

import dev.galacticraft.api.accessor.LevelOxygenAccessor;
import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.internal.oxygen.ProviderIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;

@Mixin(WorldGenRegion.class)
public abstract class WorldGenRegionMixin implements LevelOxygenAccessor, LevelHeightAccessor {
    @Shadow @Final private ServerLevel level;

    @Shadow public abstract ChunkAccess getChunk(int chunkX, int chunkZ);

    @Override
    public Iterator<AtmosphereProvider> getAtmosphericProviders(int x, int y, int z) {
        if (this.isOutsideBuildHeight(y)) return ObjectIterators.emptyIterator();
        ChunkAccess chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        Iterator<BlockPos> iterator = ((ChunkOxygenAccessor) chunk).galacticraft$getHandlers(x & 15, y, z & 15);
        return new ProviderIterator(this.level, chunk, iterator);
    }

    @Override
    public Iterator<BlockPos> getAtmosphericProviderLocations(int x, int y, int z) {
        if (this.isOutsideBuildHeight(y)) return ObjectIterators.emptyIterator();
        return ((ChunkOxygenAccessor) this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z))).galacticraft$getHandlers(x & 15, y, z & 15);
    }

    @Override
    public boolean isBreathable(double x, double y, double z) {
        Iterator<AtmosphereProvider> iter = this.getAtmosphericProviders(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(x, y, z)) return true;
        }
        return this.level.isBreathable();
    }

    @Override
    public boolean isBreathable(int x, int y, int z) {
        Iterator<AtmosphereProvider> iter = this.getAtmosphericProviders(x, y, z);
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(x, y, z)) return true;
        }
        return this.level.isBreathable();
    }

    @Override
    public boolean isBreathable(BlockPos pos) {
        Iterator<AtmosphereProvider> iter = this.getAtmosphericProviders(pos.getX(), pos.getY(), pos.getZ());
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(pos)) return true;
        }
        return this.level.isBreathable();
    }

    @Override
    public boolean isBreathable() {
        return this.level.isBreathable();
    }

    @Override
    public void addAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        if (this.isOutsideBuildHeight(y)) return;
        ChunkAccess chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        ((ChunkOxygenAccessor) chunk).galacticraft$markSectionDirty(this.getSectionIndex(y));
        ((ChunkSectionOxygenAccessor) chunk.getSection(this.getSectionIndex(y))).galacticraft$add(x & 15, y & 15, z & 15, providerPos);
    }

    @Override
    public boolean hasAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        if (this.isOutsideBuildHeight(y)) return false;
        ChunkAccess chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        return ((ChunkSectionOxygenAccessor) chunk.getSection(this.getSectionIndex(y))).galacticraft$has(x & 15, y & 15, z & 15, providerPos);
    }

    @Override
    public void removeAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        if (this.isOutsideBuildHeight(y)) return;
        ChunkAccess chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        ((ChunkOxygenAccessor) chunk).galacticraft$markSectionDirty(this.getSectionIndex(y));
        ((ChunkSectionOxygenAccessor) chunk.getSection(this.getSectionIndex(y))).galacticraft$remove(x & 15, y & 15, z & 15, providerPos);
    }
}
