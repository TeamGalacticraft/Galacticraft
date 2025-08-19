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

import com.llamalad7.mixinextras.sugar.Local;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.mod.Constant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getStates()Lnet/minecraft/world/level/chunk/PalettedContainer;"))
    private static void galacticraft_serializeOxygen(ServerLevel world, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir, @Local(ordinal = 1) CompoundTag sectionTag, @Local LevelChunkSection section) {
        ChunkSectionOxygenAccessor accessor = (ChunkSectionOxygenAccessor) section;
        CompoundTag apiTag = new CompoundTag();
        accessor.galacticraft$writeTag(apiTag);
        if (!apiTag.isEmpty()) sectionTag.put(Constant.Nbt.GC_API, apiTag);
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"))
    private static void galacticraft_deserializeOxygen(ServerLevel world, PoiManager poiStorage, RegionStorageInfo key, ChunkPos chunkPos, CompoundTag nbt, CallbackInfoReturnable<ProtoChunk> cir, @Local(ordinal = 1) CompoundTag sectionTag, @Local LevelChunkSection levelChunkSection) {
        ((ChunkSectionOxygenAccessor) levelChunkSection).galacticraft$readTag(sectionTag.getCompound(Constant.Nbt.GC_API));
    }
}
