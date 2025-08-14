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

package dev.galacticraft.mod.api.documentation.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

final class DocsJsonProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int SCHEMA = 2;

    private final FabricDataOutput out;

    private static final ResourceLocation HOME = Constant.id("home");
    private static final ResourceLocation GENERAL = Constant.id("general_category");
    private static final ResourceLocation MACHINES = Constant.id("machines_category");
    private static final ResourceLocation ROCKETS = Constant.id("rockets_category");
    private static final ResourceLocation PLANETS = Constant.id("planets_category");
    private static final ResourceLocation ENTITIES = Constant.id("entities_category");
    private static final ResourceLocation BLOCKS = Constant.id("blocks_category");
    private static final ResourceLocation ITEMS = Constant.id("items_category");
    private static final ResourceLocation FLUIDS_AND_GASES = Constant.id("fluids_and_gases_category");

    private static final ResourceLocation COAL_GENERATOR = Constant.id("coal_generator");
    private static final ResourceLocation BASIC_SOLAR_PANEL = Constant.id("basic_solar_panel");
    private static final ResourceLocation ADVANCED_SOLAR_PANEL = Constant.id("advanced_solar_panel");
    private static final ResourceLocation OXYGEN_COLLECTOR = Constant.id("oxygen_collector");
    private static final ResourceLocation OXYGEN_COMPRESSOR = Constant.id("oxygen_compressor");
    private static final ResourceLocation OXYGEN_DECOMPRESSOR = Constant.id("oxygen_decompressor");
    private static final ResourceLocation OXYGEN_STORAGE_MODULE = Constant.id("oxygen_storage_module");
    private static final ResourceLocation OXYGEN_SEALER = Constant.id("oxygen_sealer");
    private static final ResourceLocation BUBBLE_DISTRIBUTOR = Constant.id("bubble_distributor");
    private static final ResourceLocation REFINERY = Constant.id("refinery");
    private static final ResourceLocation FUEL_LOADER = Constant.id("fuel_loader");
    private static final ResourceLocation ENERGY_STORAGE_MODULE = Constant.id("energy_storage_module");
    private static final ResourceLocation CIRCUIT_FABRICATOR = Constant.id("circuit_fabricator");
    private static final ResourceLocation COMPRESSOR = Constant.id("compressor");
    private static final ResourceLocation ELECTRIC_COMPRESSOR = Constant.id("electric_compressor");
    private static final ResourceLocation ELECTRIC_FURNACE = Constant.id("electric_furnace");
    private static final ResourceLocation ELECTRIC_ARC_FURNACE = Constant.id("electric_arc_furnace");
    private static final ResourceLocation FOOD_CANNER = Constant.id("food_canner");
    private static final ResourceLocation AIRLOCK = Constant.id("airlock");

    DocsJsonProvider(FabricDataOutput out) {
        this.out = out;
    }

    @Override
    public String getName() {
        return "GC Docs JSON";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(
                writeHome(cache),
                writeGeneralCategoryPage(cache),
                writeMachinesCategoryPage(cache),
                writeRocketsCategoryPage(cache),
                writePlanetsCategoryPage(cache),
                writeEntitiesCategoryPage(cache),
                writeBlocksCategoryPage(cache),
                writeItemsCategoryPage(cache),
                writeFluidsAndGasesCategoryPage(cache),
                writeCoalGeneratorPage(cache),
                writeBasicSolarPanelPage(cache),
                writeAdvancedSolarPanelPage(cache),
                writeOxygenCollectorPage(cache),
                writeOxygenCompressorPage(cache),
                writeOxygenDecompressorPage(cache),
                writeOxygenStorageModulePage(cache),
                writeOxygenSealerPage(cache),
                writeBubbleDistributorPage(cache),
                writeRefineryPage(cache),
                writeFuelLoaderPage(cache),
                writeEnergyStorageModulePage(cache),
                writeCircuitFabricatorPage(cache),
                writeCompressorPage(cache),
                writeElectricCompressorPage(cache),
                writeElectricFurnacePage(cache),
                writeElectricArcFurnacePage(cache),
                writeFoodCannerPage(cache),
                writeAirlockPage(cache)
        );
    }

    private CompletableFuture<?> writeHome(CachedOutput cache) {
        JsonObject root = new HomePageBuilder(SCHEMA)
                .setTitleText(DocsTranslations.HOME_TITLE)
                .addTextBoxNormalized(0.004319654f, 0.2519685f, 0.99568033f, 0.3031496f, DocsTranslations.HOME_CATEGORY_LABEL, "left", 0)
                .addRedirectButtonNormalized(0.0043196543f, 0.2992126f, 0.26349893f, 0.07874016f, DocsTranslations.GENERAL_CATEGORY, GENERAL, 1)
                .addTextBoxNormalized(0.0043196213f, 0.122047246f, 0.99568033f, 0.24015749f, DocsTranslations.HOME_INTRO, "left", 2)
                .addRedirectButtonNormalized(0.0043196543f, 0.38188976f, 0.26349893f, 0.07874016f, DocsTranslations.MACHINES_CATEGORY, MACHINES, 3)
                .addRedirectButtonNormalized(0.0043196543f, 0.46456692f, 0.26349893f, 0.07874016f, DocsTranslations.ROCKETS_CATEGORY, ROCKETS, 4)
                .addRedirectButtonNormalized(0.0043196543f, 0.5472441f, 0.26349893f, 0.07874016f, DocsTranslations.PLANETS_CATEGORY, PLANETS, 5)
                .addRedirectButtonNormalized(0.0043196543f, 0.62992126f, 0.26349893f, 0.07874016f, DocsTranslations.ENTITIES_CATEGORY, ENTITIES, 6)
                .addRedirectButtonNormalized(0.0043196543f, 0.71259844f, 0.26349893f, 0.07874016f, DocsTranslations.BLOCKS_CATEGORY, BLOCKS, 7)
                .addRedirectButtonNormalized(0.0043196543f, 0.79527557f, 0.26349893f, 0.07874016f, DocsTranslations.ITEMS_CATEGORY, ITEMS, 8)
                .addRedirectButtonNormalized(0.0043196543f, 0.87795275f, 0.26349893f, 0.07874016f, DocsTranslations.FLUIDS_AND_GASES_CATEGORY, FLUIDS_AND_GASES, 9)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/home.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeGeneralCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, GENERAL)
                .setTitleText(DocsTranslations.GENERAL_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/general_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeMachinesCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, MACHINES)
                .setTitleText(DocsTranslations.MACHINES_CATEGORY)
                .addRedirectButtonNormalized(0.021598272f, 0.20472442f, 0.26349893f, 0.07874016f, DocsTranslations.COAL_GENERATOR, COAL_GENERATOR, 0)
                .addRedirectButtonNormalized(0.021598272f, 0.6692913f, 0.26349893f, 0.07874016f, DocsTranslations.OXYGEN_DECOMPRESSOR, OXYGEN_DECOMPRESSOR, 1)
                .addRedirectButtonNormalized(0.021598272f, 0.8346457f, 0.26349893f, 0.07874016f, DocsTranslations.OXYGEN_SEALER, OXYGEN_SEALER, 2)
                .addRedirectButtonNormalized(0.021598272f, 0.503937f, 0.26349893f, 0.07874016f, DocsTranslations.OXYGEN_COLLECTOR, OXYGEN_COLLECTOR, 3)
                .addRedirectButtonNormalized(0.2937365f, 0.2874016f, 0.26349893f, 0.07874016f, DocsTranslations.FUEL_LOADER, FUEL_LOADER, 4)
                .addRedirectButtonNormalized(0.2937365f, 0.42125985f, 0.26349893f, 0.07874016f, DocsTranslations.ENERGY_STORAGE_MODULE, ENERGY_STORAGE_MODULE, 5)
                .addRedirectButtonNormalized(0.56587476f, 0.20472442f, 0.26349893f, 0.07874016f, DocsTranslations.CIRCUIT_FABRICATOR, CIRCUIT_FABRICATOR, 6)
                .addRedirectButtonNormalized(0.56587476f, 0.2874015f, 0.26349893f, 0.07874016f, DocsTranslations.COMPRESSOR, COMPRESSOR, 7)
                .addRedirectButtonNormalized(0.2937365f, 0.20472442f, 0.26349893f, 0.07874016f, DocsTranslations.REFINERY, REFINERY, 8)
                .addRedirectButtonNormalized(0.021598272f, 0.9173228f, 0.26349893f, 0.07874016f, DocsTranslations.BUBBLE_DISTRIBUTOR, BUBBLE_DISTRIBUTOR, 9)
                .addRedirectButtonNormalized(0.021598272f, 0.5866142f, 0.26349893f, 0.07874016f, DocsTranslations.COMPRESSOR, COMPRESSOR, 10)
                .addRedirectButtonNormalized(0.56587476f, 0.37007874f, 0.26349893f, 0.07874016f, DocsTranslations.ELECTRIC_COMPRESSOR, ELECTRIC_COMPRESSOR, 11)
                .addRedirectButtonNormalized(0.021598272f, 0.37007874f, 0.26349893f, 0.07874016f, DocsTranslations.ADVANCED_SOLAR_PANEL, ADVANCED_SOLAR_PANEL, 12)
                .addRedirectButtonNormalized(0.021598272f, 0.2874016f, 0.26349893f, 0.07874016f, DocsTranslations.BASIC_SOLAR_PANEL, BASIC_SOLAR_PANEL, 13)
                .addRedirectButtonNormalized(0.021598272f, 0.7519685f, 0.26349893f, 0.07874016f, DocsTranslations.OXYGEN_STORAGE_MODULE, ENERGY_STORAGE_MODULE, 14)
                .addRedirectButtonNormalized(0.56587476f, 0.4527559f, 0.26349893f, 0.07874016f, DocsTranslations.ELECTRIC_FURNACE, ELECTRIC_FURNACE, 15)
                .addRedirectButtonNormalized(0.56587476f, 0.53543305f, 0.26349893f, 0.07874016f, DocsTranslations.ELECTRIC_ARC_FURNACE, ELECTRIC_ARC_FURNACE, 16)
                .addRedirectButtonNormalized(0.5658747f, 0.61811024f, 0.26349893f, 0.07874016f, DocsTranslations.FOOD_CANNER, FOOD_CANNER, 17)
                .addRedirectButtonNormalized(0.2937365f, 0.5590551f, 0.26349893f, 0.07874016f, DocsTranslations.AIRLOCK, AIRLOCK, 18)
                .addTextBoxNormalized(0.021598272f, 0.15748031f, 0.28509718f, 0.20472442f, DocsTranslations.GENERATORS, "center", 19)
                .addTextBoxNormalized(0.2937365f, 0.51968503f, 0.5572354f, 0.5669291f, DocsTranslations.MULTIBLOCKS, "center", 20)
                .addTextBoxNormalized(0.2937365f, 0.15748031f, 0.5572354f, 0.20472442f, DocsTranslations.FUEL_MACHINES, "center", 21)
                .addTextBoxNormalized(0.56587476f, 0.15748031f, 0.8293737f, 0.2047244f, DocsTranslations.FUNCTIONAL_MACHINES, "center", 22)
                .addTextBoxNormalized(0.2937365f, 0.37401575f, 0.5572354f, 0.42125985f, DocsTranslations.ENERGY_MACHINES, "center", 23)
                .addTextBoxNormalized(0.021598272f, 0.4566929f, 0.28509718f, 0.503937f, DocsTranslations.OXYGEN_MACHINES, "center", 24)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/machines_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeCoalGeneratorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, COAL_GENERATOR)
                .bindItem(GCBlocks.COAL_GENERATOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.COAL_GENERATOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/coal_generator.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeBasicSolarPanelPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, BASIC_SOLAR_PANEL)
                .bindItem(GCBlocks.BASIC_SOLAR_PANEL.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.BASIC_SOLAR_PANEL)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/basic_solar_panel.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeAdvancedSolarPanelPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ADVANCED_SOLAR_PANEL)
                .bindItem(GCBlocks.ADVANCED_SOLAR_PANEL.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.ADVANCED_SOLAR_PANEL)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/advanced_solar_panel.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeOxygenCollectorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, OXYGEN_COLLECTOR)
                .bindItem(GCBlocks.OXYGEN_COLLECTOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.OXYGEN_COLLECTOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/oxygen_collector.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeOxygenCompressorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, OXYGEN_COMPRESSOR)
                .bindItem(GCBlocks.OXYGEN_COMPRESSOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.OXYGEN_COMPRESSOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/oxygen_compressor.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeOxygenDecompressorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, OXYGEN_DECOMPRESSOR)
                .bindItem(GCBlocks.OXYGEN_DECOMPRESSOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.OXYGEN_DECOMPRESSOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/oxygen_decompressor.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeOxygenStorageModulePage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, OXYGEN_STORAGE_MODULE)
                .bindItem(GCBlocks.OXYGEN_STORAGE_MODULE.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.OXYGEN_STORAGE_MODULE)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/oxygen_storage_module.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeOxygenSealerPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, OXYGEN_SEALER)
                .bindItem(GCBlocks.OXYGEN_SEALER.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.OXYGEN_SEALER)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/oxygen_sealer.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeBubbleDistributorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, BUBBLE_DISTRIBUTOR)
                .bindItem(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.BUBBLE_DISTRIBUTOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/bubble_distributor.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeRefineryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, REFINERY)
                .bindItem(GCBlocks.REFINERY.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.REFINERY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/refinery.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeFuelLoaderPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, FUEL_LOADER)
                .bindItem(GCBlocks.FUEL_LOADER.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.FUEL_LOADER)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/fuel_loader.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeEnergyStorageModulePage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ENERGY_STORAGE_MODULE)
                .bindItem(GCBlocks.ENERGY_STORAGE_MODULE.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.ENERGY_STORAGE_MODULE)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/energy_storage_module.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeCircuitFabricatorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, CIRCUIT_FABRICATOR)
                .bindItem(GCBlocks.CIRCUIT_FABRICATOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.CIRCUIT_FABRICATOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/circuit_fabricator.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeCompressorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, COMPRESSOR)
                .bindItem(GCBlocks.COMPRESSOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.COMPRESSOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/compressor.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeElectricCompressorPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ELECTRIC_COMPRESSOR)
                .bindItem(GCBlocks.ELECTRIC_COMPRESSOR.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.ELECTRIC_COMPRESSOR)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/electric_compressor.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeElectricFurnacePage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ELECTRIC_FURNACE)
                .bindItem(GCBlocks.ELECTRIC_FURNACE.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.ELECTRIC_FURNACE)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/electric_furnace.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeElectricArcFurnacePage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ELECTRIC_ARC_FURNACE)
                .bindItem(GCBlocks.ELECTRIC_ARC_FURNACE.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.ELECTRIC_ARC_FURNACE)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/electric_arc_furnace.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeFoodCannerPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, FOOD_CANNER)
                .bindItem(GCBlocks.FOOD_CANNER.asItem())
                .setParent(MACHINES)
                .setTitleText(DocsTranslations.FOOD_CANNER)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/food_canner.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeAirlockPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, AIRLOCK)
                .setTitleText(DocsTranslations.AIRLOCK)
                .setParent(MACHINES)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/airlock.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeRocketsCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ROCKETS)
                .setTitleText(DocsTranslations.ROCKETS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/rockets_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writePlanetsCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, PLANETS)
                .setTitleText(DocsTranslations.PLANETS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/planets_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeEntitiesCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ENTITIES)
                .setTitleText(DocsTranslations.ENTITIES_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/entities_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeBlocksCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, BLOCKS)
                .setTitleText(DocsTranslations.BLOCKS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/blocks_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeItemsCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, ITEMS)
                .setTitleText(DocsTranslations.ITEMS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/items_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeFluidsAndGasesCategoryPage(CachedOutput cache) {
        JsonObject root = new SubPageBuilder(SCHEMA, FLUIDS_AND_GASES)
                .setTitleText(DocsTranslations.FLUIDS_AND_GASES_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/fluids_and_gases_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> save(CachedOutput cache, Object json, Path path) {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), path);
    }
}