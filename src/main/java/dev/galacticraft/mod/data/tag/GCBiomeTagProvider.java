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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;

public class GCBiomeTagProvider extends FabricTagProvider<Biome> {
    public GCBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.BIOME, future);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(GCTags.MOON)
                .add(GCBiomes.Moon.COMET_TUNDRA)
                .add(GCBiomes.Moon.BASALTIC_MARE)
                .add(GCBiomes.Moon.LUNAR_HIGHLANDS)
                .add(GCBiomes.Moon.LUNAR_LOWLANDS)
                .add(GCBiomes.Moon.OLIVINE_SPIKES);

        tag(GCTags.VENUS)
                .add(GCBiomes.Venus.VENUS_VALLEY)
                .add(GCBiomes.Venus.VENUS_FLAT)
                .add(GCBiomes.Venus.VENUS_MOUNTAIN);

        tag(GCTags.MOON_PILLAGER_BASE_HAS_STRUCTURE)
                .add(GCBiomes.Moon.BASALTIC_MARE);
        tag(GCTags.MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE)
                .add(GCBiomes.Moon.LUNAR_HIGHLANDS);
        tag(GCTags.MOON_RUINS_HAS_STRUCTURE)
                .add(GCBiomes.Moon.BASALTIC_MARE);
        tag(GCTags.MOON_BOSS_HAS_STRUCTURE)
                .addTag(GCTags.MOON);
    }
}