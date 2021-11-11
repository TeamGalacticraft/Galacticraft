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

package dev.galacticraft.mod.world.gen.feature;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.structure.MoonRuinsGenerator;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonRuinsFeature extends StructureFeature<DefaultFeatureConfig> {
   public MoonRuinsFeature(Codec<DefaultFeatureConfig> codec) {
      super(codec, MoonRuinsFeature::method_38700);
   }
   private static void method_38700(class_6626 arg, DefaultFeatureConfig featureConfig, class_6622.class_6623 arg2) {
      if (arg2.method_38707(Heightmap.Type.WORLD_SURFACE_WG)) {
         BlockPos blockPos = new BlockPos(arg2.chunkPos().getStartX(), 90, arg2.chunkPos().getStartZ());
         BlockRotation blockRotation = BlockRotation.random(arg2.random());
         MoonRuinsGenerator.addPieces(arg2.structureManager(), blockPos, blockRotation, arg, arg2.random(), featureConfig);
      }
   }
}
