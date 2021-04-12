/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.ChunkSectionOxygenAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.chunk.*;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.ProtoTickList;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Inject(method = "serialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/PalettedContainer;write(Lnet/minecraft/nbt/CompoundTag;Ljava/lang/String;Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void serializeGCR(ServerLevel world, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkPos, CompoundTag compoundTag, CompoundTag compoundTag2, LevelChunkSection[] chunkSections, ListTag listTag, LevelLightEngine lightingProvider, boolean bl, int i, int j, LevelChunkSection chunkSection, DataLayer chunkNibbleArray, DataLayer chunkNibbleArray2, CompoundTag compoundTag3) {
        CompoundTag tag = new CompoundTag();
        tag.putShort("TotalOxygen", ((ChunkSectionOxygenAccessor) chunkSection).getTotalOxygen());
        if (((ChunkSectionOxygenAccessor) chunkSection).getTotalOxygen() > 0) {
            byte[] array = new byte[(16 * 16 * 16) / 8];
            boolean[] oxygenValues = ((ChunkSectionOxygenAccessor) chunkSection).getArray();
            for (int p = 0; p < oxygenValues.length - 8; p += 9) {
                byte serialized = -128;
                serialized += oxygenValues[p] ? 1 : 0;
                serialized += oxygenValues[p + 1] ? 2 : 0;
                serialized += oxygenValues[p + 2] ? 4 : 0;
                serialized += oxygenValues[p + 3] ? 8 : 0;
                serialized += oxygenValues[p + 4] ? 16 : 0;
                serialized += oxygenValues[p + 5] ? 32 : 0;
                serialized += oxygenValues[p + 6] ? 64 : 0;
                serialized += oxygenValues[p + 7] ? 128 : 0;
                array[p / 8] = serialized;
            }
            compoundTag3.putByteArray("Oxygen", array);
        }
        compoundTag3.put("gcr_data", tag);
    }

    @Inject(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;calculateCounts()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void deserializeGCR(ServerLevel world, StructureManager structureManager, PoiManager poiStorage, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, ChunkGenerator chunkGenerator, BiomeSource biomeSource, CompoundTag compoundTag, ChunkBiomeContainer biomeArray, UpgradeData upgradeData, ProtoTickList chunkTickScheduler, ProtoTickList chunkTickScheduler2, boolean bl, ListTag listTag, int i, LevelChunkSection[] chunkSections, boolean bl2, ChunkSource chunkManager, LevelLightEngine lightingProvider, int j, CompoundTag compoundTag2, int k, LevelChunkSection chunkSection) {
        CompoundTag compound = compoundTag2.getCompound("gcr_data");
        ((ChunkSectionOxygenAccessor) chunkSection).setTotalOxygen(compound.getShort("TotalOxygen"));
        if (compound.getShort("TotalOxygen") > 0) {
            boolean[] oxygen = ((ChunkSectionOxygenAccessor) chunkSection).getArray();
            byte[] bytes = compound.getByteArray("Oxygen");
            for (int p = 0; p < 4096 / 8; p++) {
                short b = (short) (bytes[i] + 128);
                oxygen[(p * 8)] = (b & 1) != 0;
                oxygen[(p * 8) + 1] = (b & 2) != 0;
                oxygen[(p * 8) + 2] = (b & 4) != 0;
                oxygen[(p * 8) + 3] = (b & 8) != 0;
                oxygen[(p * 8) + 4] = (b & 16) != 0;
                oxygen[(p * 8) + 5] = (b & 32) != 0;
                oxygen[(p * 8) + 6] = (b & 64) != 0;
                oxygen[(p * 8) + 7] = (b & 128) !=0 ;
            }
        }
    }
}
