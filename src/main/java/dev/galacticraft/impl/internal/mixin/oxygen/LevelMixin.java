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

import dev.galacticraft.api.accessor.LevelBodyAccessor;
import dev.galacticraft.api.accessor.LevelOxygenAccessor;
import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.impl.internal.oxygen.ProviderIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelOxygenAccessor, LevelAccessor, LevelHeightAccessor {
    @Unique
    private boolean breathable = true;

    @Shadow
    public abstract @NotNull LevelChunk getChunk(int i, int j);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initializeOxygenValues(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder holder, Supplier supplier, boolean bl, boolean bl2, long l, int i, CallbackInfo ci) {
        Holder<CelestialBody<?, ?>> body = ((LevelBodyAccessor) this).galacticraft$getCelestialBody();
        this.breathable = body == null || body.value().atmosphere().breathable();
    }

    @Override
    public Iterator<AtmosphereProvider> getAtmosphericProviders(int x, int y, int z) {
        if (this.isOutsideBuildHeight(y)) return ObjectIterators.emptyIterator();
        LevelChunk chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        Iterator<BlockPos> iterator = ((ChunkOxygenAccessor) chunk).galacticraft$getHandlers(x & 15, y, z & 15);
        return new ProviderIterator((Level) (Object) this, chunk, iterator);
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
        return this.breathable;
    }

    @Override
    public boolean isBreathable(int x, int y, int z) {
        Iterator<AtmosphereProvider> iter = this.getAtmosphericProviders(x, y, z);
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(x, y, z)) return true;
        }
        return this.breathable;
    }

    @Override
    public boolean isBreathable(BlockPos pos) {
        Iterator<AtmosphereProvider> iter = this.getAtmosphericProviders(pos.getX(), pos.getY(), pos.getZ());
        while (iter.hasNext()) {
            AtmosphereProvider next = iter.next();
            if (next.canBreathe(pos)) return true;
        }
        return this.breathable;
    }

    @Override
    public boolean isBreathable() {
        return this.breathable;
    }

    @Override
    public void addAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        if (this.isOutsideBuildHeight(y)) return;
        LevelChunk chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        ((ChunkOxygenAccessor) chunk).galacticraft$markSectionDirty(this.getSectionIndex(y));
        ((ChunkSectionOxygenAccessor) chunk.getSection(this.getSectionIndex(y))).galacticraft$add(x & 15, y & 15, z & 15, providerPos);
    }

    @Override
    public boolean hasAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        if (this.isOutsideBuildHeight(y)) return false;
        LevelChunk chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        return ((ChunkSectionOxygenAccessor) chunk.getSection(this.getSectionIndex(y))).galacticraft$has(x & 15, y & 15, z & 15, providerPos);
    }

    @Override
    public void removeAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        if (this.isOutsideBuildHeight(y)) return;
        LevelChunk chunk = this.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        ((ChunkOxygenAccessor) chunk).galacticraft$markSectionDirty(this.getSectionIndex(y));
        ((ChunkSectionOxygenAccessor) chunk.getSection(this.getSectionIndex(y))).galacticraft$remove(x & 15, y & 15, z & 15, providerPos);
    }
}
