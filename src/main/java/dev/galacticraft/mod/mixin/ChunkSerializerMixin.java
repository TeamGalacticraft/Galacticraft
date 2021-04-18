/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.accessor.ChunkSectionOxygenAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Inject(method = "serialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/PalettedContainer;write(Lnet/minecraft/nbt/CompoundTag;Ljava/lang/String;Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void serializeGCR(ServerWorld world, Chunk chunk, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkPos, CompoundTag compoundTag, CompoundTag compoundTag2, ChunkSection[] chunkSections, ListTag listTag, LightingProvider lightingProvider, boolean bl, int i, int j, ChunkSection chunkSection, ChunkNibbleArray chunkNibbleArray, ChunkNibbleArray chunkNibbleArray2, CompoundTag compoundTag3) {
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
    private static void deserializeGCR(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, ChunkGenerator chunkGenerator, BiomeSource biomeSource, CompoundTag compoundTag, BiomeArray biomeArray, UpgradeData upgradeData, ChunkTickScheduler chunkTickScheduler, ChunkTickScheduler chunkTickScheduler2, boolean bl, ListTag listTag, int i, ChunkSection[] chunkSections, boolean bl2, ChunkManager chunkManager, LightingProvider lightingProvider, int j, CompoundTag compoundTag2, int k, ChunkSection chunkSection) {
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
