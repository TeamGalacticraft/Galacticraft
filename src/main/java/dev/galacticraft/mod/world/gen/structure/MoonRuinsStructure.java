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

package dev.galacticraft.mod.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.structure.MoonRuinsGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class MoonRuinsStructure extends Structure {
   public static final Codec<MoonRuinsStructure> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
           StructureSettings.CODEC.fieldOf("config").forGetter((moonRuinsStructure) -> moonRuinsStructure.settings)
   ).apply(instance, MoonRuinsStructure::new));

   public MoonRuinsStructure(Structure.StructureSettings codec) {
      super(codec);
   }

   private static void addPieces(StructurePiecesBuilder collector, Structure.GenerationContext context) {
      BlockPos blockPos = new BlockPos(context.chunkPos().getMinBlockX(), 90, context.chunkPos().getMinBlockZ());
      Rotation blockRotation = Rotation.getRandom(context.random());
      MoonRuinsGenerator.addPiecesToStructure(context.structureTemplateManager(), blockPos, blockRotation, collector, context.random());
   }

   @Override
   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
      return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, collector -> addPieces(collector, context));
   }

   @Override
   public StructureType<?> type() {
      return GCStructureTypes.MOON_RUINS;
   }
}
