/*
 *
 *  * Copyright (c) 2019-2023 Team Galacticraft
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIfDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *
 */

package dev.galacticraft.mod.data.fixer;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.fixer.fixes.LegacyGCChunkPalettedStorageFix;
import dev.galacticraft.mod.data.fixer.fixes.LegacyGCFlatteningFix;
import dev.galacticraft.mod.data.fixer.schemas.GCLegacyBlockFlatteningSchema;
import dev.galacticraft.mod.data.fixer.schemas.GCLegacyBlockFlatteningSchema_7;
import dev.galacticraft.mod.data.fixer.schemas.GCLegacySchema;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.References;

import java.util.Map;
import java.util.function.UnaryOperator;

public class GCDataFixer {
    public static final int VERSION_1_12_2 = 1343;
    public static final int VERSION_FLATTENING = 1451;
    public static final int VERSION_1_20_1 = 3465;

    public static void buildDataFixers(DataFixerBuilder builder) {
        Schema legacyGC = builder.addSchema(VERSION_1_12_2, GCLegacySchema::new);
        builder.addFixer(new AddNewChoices(legacyGC, "Legacy GC version", References.BLOCK_ENTITY));

//        Schema chunkFixerSchema = builder.addSchema(VERSION_FLATTENING, GCLegacyBlockFlatteningSchema_7::new);
//        builder.addFixer(new LegacyGCChunkPalettedStorageFix(chunkFixerSchema, true));

//        Schema flattenLegacy = builder.addSchema(VERSION_FLATTENING, GCLegacyBlockFlatteningSchema::new);
//        builder.addFixer(new LegacyGCFlatteningFix(flattenLegacy, true));

        Schema legacyTo5 = builder.addSchema(VERSION_1_20_1, Schema::new);
        builder.addFixer(BlockRenameFix.create(legacyTo5, "GC 4 to GC 5", createRenamer(Map.of(
                "galacticraftcore:oxygen_compressor", Constant.MOD_ID + ":" + Constant.Block.OXYGEN_COMPRESSOR,
                "galacticraftcore:distributor", Constant.MOD_ID + ":" + Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR
        ))));
    }
    public static UnaryOperator<String> createRenamer(Map<String, String> map) {
        return string -> map.getOrDefault(string, string);
    }
}