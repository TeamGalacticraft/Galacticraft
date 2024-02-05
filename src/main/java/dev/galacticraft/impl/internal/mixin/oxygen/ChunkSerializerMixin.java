/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.mojang.serialization.Codec;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.mod.Constant;
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

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getStates()Lnet/minecraft/world/level/chunk/PalettedContainer;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void galacticraft_serializeOxygen(ServerLevel world, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkPos, CompoundTag nbtCompound, BlendingData blendingData, BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, LevelChunkSection[] chunkSections, ListTag nbtList, LevelLightEngine lightingProvider, Registry<Biome> registry, Codec<PalettedContainerRO<Holder<Biome>>> codec, boolean bl, int i, int j, boolean bl2, DataLayer chunkNibbleArray, DataLayer chunkNibbleArray2, CompoundTag nbtCompound2, LevelChunkSection section) {
        var accessor = (ChunkSectionOxygenAccessor) section;
        if (accessor.galacticraft$modifiedBlocks() > 0) {
            CompoundTag nbt = new CompoundTag();
            nbt.putShort(Constant.Nbt.CHANGE_COUNT, ((ChunkSectionOxygenAccessor) section).galacticraft$modifiedBlocks());
            if (((ChunkSectionOxygenAccessor) section).galacticraft$modifiedBlocks() > 0) {
                BitSet bits = ((ChunkSectionOxygenAccessor) section).galacticraft$inversionBits();
                assert bits != null;
                nbt.putLongArray(Constant.Nbt.OXYGEN, bits.toLongArray());
            }
            nbtCompound2.put(Constant.Nbt.GC_API, nbt);
        }
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void galacticraft_deserializeOxygen(ServerLevel world, PoiManager poiStorage, ChunkPos chunkPos, CompoundTag nbt, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkPos2, UpgradeData upgradeData, boolean bl, ListTag nbtList, int i, LevelChunkSection[] chunkSections, boolean bl2, ChunkSource chunkManager, LevelLightEngine lightingProvider, Registry<Biome> registry, Codec<PalettedContainerRO<Holder<Biome>>> codec, boolean bl3, int j, CompoundTag nbtCompound, int k, int l, PalettedContainer palettedContainer, PalettedContainerRO readableContainer, LevelChunkSection section) {
        CompoundTag apiCompound = nbtCompound.getCompound(Constant.Nbt.GC_API);
        short changedCount = apiCompound.getShort(Constant.Nbt.CHANGE_COUNT);
        ((ChunkSectionOxygenAccessor) section).galacticraft$setModifiedBlocks(changedCount);
        if (changedCount > 0) {
            ((ChunkSectionOxygenAccessor) section).galacticraft$setInversionBits(BitSet.valueOf(apiCompound.getLongArray(Constant.Nbt.OXYGEN)));
        }
    }
}
