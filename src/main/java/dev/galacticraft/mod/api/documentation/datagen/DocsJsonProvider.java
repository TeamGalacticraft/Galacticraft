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
                writeFluidsAndGassesCategoryPage(cache),
                writeAirlockPage(cache)
        );
    }

    private CompletableFuture<?> writeHome(CachedOutput cache) {
        ResourceLocation AIRLOCK_PAGE = Constant.id("air_lock_controller");

        JsonObject root = new HomePageBuilder(SCHEMA)
                .setTitleText(DocsTranslations.HOME_TITLE)
                .addTextBoxNormalized(0.004319654f, 0.2519685f, 0.99568033f, 0.3031496f, DocsTranslations.HOME_CATEGORY_LABEL, "left", 0)
                .addRedirectButtonNormalized(0.0043196543f, 0.2992126f, 0.26349893f, 0.07874016f, DocsTranslations.GENERAL_CATEGORY, Constant.id("general_category"), 1)
                .addTextBoxNormalized(0.0043196213f, 0.122047246f, 0.99568033f, 0.24015749f, DocsTranslations.HOME_INTRO, "left", 2)
                .addRedirectButtonNormalized(0.0043196543f, 0.38188976f, 0.26349893f, 0.07874016f, DocsTranslations.MACHINES_CATEGORY, Constant.id("machines_category"), 3)
                .addRedirectButtonNormalized(0.0043196543f, 0.46456692f, 0.26349893f, 0.07874016f, DocsTranslations.ROCKETS_CATEGORY, Constant.id("rockets_category"), 4)
                .addRedirectButtonNormalized(0.0043196543f, 0.5472441f, 0.26349893f, 0.07874016f, DocsTranslations.PLANETS_CATEGORY, Constant.id("planets_category"), 5)
                .addRedirectButtonNormalized(0.0043196543f, 0.62992126f, 0.26349893f, 0.07874016f, DocsTranslations.ENTITIES_CATEGORY, Constant.id("entities_category"), 6)
                .addRedirectButtonNormalized(0.0043196543f, 0.71259844f, 0.26349893f, 0.07874016f, DocsTranslations.BLOCKS_CATEGORY, Constant.id("blocks_category"), 7)
                .addRedirectButtonNormalized(0.0043196543f, 0.79527557f, 0.26349893f, 0.07874016f, DocsTranslations.ITEMS_CATEGORY, Constant.id("items_category"), 8)
                .addRedirectButtonNormalized(0.0043196543f, 0.87795275f, 0.26349893f, 0.07874016f, DocsTranslations.FLUIDS_AND_GASSES_CATEGORY, Constant.id("fluids_and_gasses_category"), 9)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/home.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeGeneralCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("general_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.GENERAL_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/general_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeMachinesCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("machines_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.MACHINES_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/machines_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeRocketsCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("rockets_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.ROCKETS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/rockets_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writePlanetsCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("planets_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.PLANETS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/planets_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeEntitiesCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("entities_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.ENTITIES_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/entities_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeBlocksCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("blocks_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.BLOCKS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/blocks_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeItemsCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("items_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.ITEMS_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/items_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeFluidsAndGassesCategoryPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("fluids_and_gasses_category");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText(DocsTranslations.FLUIDS_AND_GASSES_CATEGORY)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/fluids_and_gasses_category.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> writeAirlockPage(CachedOutput cache) {
        ResourceLocation PAGE_ID = Constant.id("air_lock_controller");

        JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)
                .setTitleText("doc.gc.airlock.title")
                .bindItem(GCBlocks.AIR_LOCK_CONTROLLER.asItem())
                .addOverviewSection("doc.common.overview", "doc.gc.airlock.overview")
                .addTextBoxNormalized(0.04f, 0.28f, 0.54f, 0.61f, "doc.gc.airlock.overview", "left", 0)
                .addRedirectButtonNormalized(0.04f, 0.70f, 0.24f, 0.10f, "gc.docs.back", Constant.id("home"), 1)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/air_lock_controller.json");
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