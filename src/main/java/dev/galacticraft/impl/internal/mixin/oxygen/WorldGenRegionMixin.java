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

import dev.galacticraft.api.accessor.ChunkOxygenAccessor;
import dev.galacticraft.api.accessor.LevelOxygenAccessor;
import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.internal.oxygen.ProviderIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

@Mixin(WorldGenRegion.class)
public abstract class WorldGenRegionMixin implements LevelOxygenAccessor, LevelHeightAccessor {
    @Shadow @Final private ServerLevel level;

    @Shadow public abstract ChunkAccess getChunk(int chunkX, int chunkZ);

    @Override
    public Iterator<AtmosphereProvider> galacticraft$getAtmosphereProviders(int x, int y, int z) {
        if (this.isOutsideBuildHeight(y)) return Collections.emptyIterator();
        ChunkAccess chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        int sectionIndex = chunk.getSectionIndex(y);
        ArrayList<BlockPos> positions = ((ChunkSectionOxygenAccessor) chunk.getSections()[sectionIndex]).galacticraft$getRawProviders();
        if (positions == null) return Collections.emptyIterator();
        return new ProviderIterator(this.level, chunk, positions.listIterator(), sectionIndex);
    }

    @Override
    public Iterator<BlockPos> galacticraft$getAtmosphereProviderLocations(int x, int y, int z) {
        if (this.isOutsideBuildHeight(y)) return Collections.emptyIterator();
        return ((ChunkOxygenAccessor) this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z))).galacticraft$getProviderPositions(y);
    }

    @Override
    public void galacticraft$notifyAtmosphereChange(BlockPos pos, BlockState state) {
        state.getBlock().galacticraft$onAtmosphereChange(this.level, pos, state, this.galacticraft$getAtmosphereProviders(pos));
    }

    @Override
    public boolean galacticraft$isBreathable(double x, double y, double z) {
        if (this.level.galacticraft$isBreathable()) return true;
        Iterator<AtmosphereProvider> iter = this.galacticraft$getAtmosphereProviders(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(x, y, z)) return true;
        }
        return false;
    }

    @Override
    public boolean galacticraft$isBreathable(int x, int y, int z) {
        if (this.level.galacticraft$isBreathable()) return true;
        Iterator<AtmosphereProvider> iter = this.galacticraft$getAtmosphereProviders(x, y, z);
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(x, y, z)) return true;
        }
        return false;
    }

    @Override
    public boolean galacticraft$isBreathable(BlockPos pos) {
        if (this.level.galacticraft$isBreathable()) return true;
        Iterator<AtmosphereProvider> iter = this.galacticraft$getAtmosphereProviders(pos.getX(), pos.getY(), pos.getZ());
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(pos)) return true;
        }
        return false;
    }

    @Override
    public boolean galacticraft$isBreathable() {
        return this.level.galacticraft$isBreathable();
    }
}
