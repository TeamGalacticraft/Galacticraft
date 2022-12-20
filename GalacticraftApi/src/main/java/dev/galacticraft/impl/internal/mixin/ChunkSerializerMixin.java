/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.mojang.serialization.Codec;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessorInternal;
import dev.galacticraft.impl.internal.accessor.WorldOxygenAccessorInternal;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.BitSet;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getStates()Lnet/minecraft/world/level/chunk/PalettedContainer;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void galacticraft_serializeOxygen(ServerLevel world, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkPos, CompoundTag nbtCompound, BlendingData blendingData, BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, LevelChunkSection[] chunkSections, ListTag nbtList, LevelLightEngine lightingProvider, Registry<Biome> registry, Codec<PalettedContainerRO<Holder<Biome>>> codec, boolean bl, int i, int j, boolean bl2, DataLayer chunkNibbleArray, DataLayer chunkNibbleArray2, CompoundTag nbtCompound2, LevelChunkSection chunkSection) {
        CompoundTag nbt = new CompoundTag();
        if (((WorldOxygenAccessorInternal) world).getDefaultBreathable() != ((ChunkSectionOxygenAccessorInternal) chunkSection).getDefaultBreathable()) {
            nbt.putBoolean(Constant.Nbt.DEFAULT_BREATHABLE, ((ChunkSectionOxygenAccessorInternal) chunkSection).getDefaultBreathable());
        }
        nbt.putShort(Constant.Nbt.CHANGE_COUNT, ((ChunkSectionOxygenAccessorInternal) chunkSection).getModifiedBlocks());
        if (((ChunkSectionOxygenAccessorInternal) chunkSection).getModifiedBlocks() > 0) {
            BitSet bits = ((ChunkSectionOxygenAccessorInternal) chunkSection).getInversion();
            assert bits != null;
            nbt.putLongArray(Constant.Nbt.OXYGEN, bits.toLongArray());
        }
        nbtCompound.put(Constant.Nbt.GC_API, nbt);
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void galacticraft_deserializeOxygen(ServerLevel world, PoiManager poiStorage, ChunkPos chunkPos, CompoundTag nbt, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos2, UpgradeData upgradeData, boolean bl, ListTag nbtList, int i, LevelChunkSection[] chunkSections, boolean bl2, ChunkSource chunkManager, LevelLightEngine lightingProvider, Registry<Biome> registry, Codec<PalettedContainerRO<Holder<Biome>>> codec, boolean bl3, int j, CompoundTag nbtCompound, int k, int l, PalettedContainer palettedContainer, PalettedContainerRO readableContainer, LevelChunkSection chunkSection) {
        CompoundTag nbtC = nbtCompound.getCompound(Constant.Nbt.GC_API);
        short changedCount = nbtC.getShort(Constant.Nbt.CHANGE_COUNT);
        if (nbtC.contains(Constant.Nbt.DEFAULT_BREATHABLE)) {
            ((ChunkSectionOxygenAccessorInternal) chunkSection).setDefaultBreathable(nbtC.getBoolean(Constant.Nbt.DEFAULT_BREATHABLE));
        } else {
            ((ChunkSectionOxygenAccessorInternal) chunkSection).setDefaultBreathable(((WorldOxygenAccessorInternal) world).getDefaultBreathable());
        }
        ((ChunkSectionOxygenAccessorInternal) chunkSection).setModifiedBlocks(changedCount);
        if (changedCount > 0) {
            ((ChunkSectionOxygenAccessorInternal) chunkSection).setInversion(BitSet.valueOf(nbtC.getLongArray(Constant.Nbt.OXYGEN)));
        }
    }
}
