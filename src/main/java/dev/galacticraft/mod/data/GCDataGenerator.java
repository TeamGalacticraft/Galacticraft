/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.data;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.mod.data.content.*;
import dev.galacticraft.mod.data.model.GCModelProvider;
import dev.galacticraft.mod.data.tag.*;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

public class GCDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(GCBlockLootTableProvider::new);
        pack.addProvider(GCRecipeProvider::new);

        // content
        pack.addProvider(GCBiomeProvider::new);
        pack.addProvider(GCCelestialBodyProvider::new);
        pack.addProvider(GCStructureProvider::new);
        pack.addProvider(GCStructureSetProvider::new);
        pack.addProvider(GCStructureTemplatePoolProvider::new);

        // models
        pack.addProvider(GCModelProvider::new);

        // tags
        pack.addProvider(GCBannerTagProvider::new);
        pack.addProvider(GCBiomeTagProvider::new);
        pack.addProvider(GCBlockTagProvider::new);
        pack.addProvider(GCItemTagProvider::new);
        pack.addProvider(GCFluidTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);

        registryBuilder.add(Registries.BIOME, Lifecycle.stable(), GCBiomes::bootstrapRegistries);
    }
}
