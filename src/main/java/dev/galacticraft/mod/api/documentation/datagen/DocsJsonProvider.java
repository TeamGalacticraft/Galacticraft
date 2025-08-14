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
                writeAirlockPage(cache)
        );
    }

    private CompletableFuture<?> writeHome(CachedOutput cache) {
        ResourceLocation AIRLOCK_PAGE = Constant.id("air_lock_controller");

        JsonObject root = new HomePageBuilder(SCHEMA)
                .setTitleText("doc.gc.home.title")
                .addTextBoxNormalized(0.004319654f, 0.2519685f, 0.99568033f, 0.3031496f, "doc.gc.home.category_label", "left", 0)
                .addRedirectButtonNormalized(0.0043196543f, 0.2992126f, 0.26349893f, 0.07874016f, "doc.gc.home.category_general", Constant.id("home"), 1)
                .addTextBoxNormalized(0.0043196213f, 0.122047246f, 0.99568033f, 0.24015749f, "doc.gc.home.intro", "left", 2)
                .addRedirectButtonNormalized(0.0043196543f, 0.38188976f, 0.26349893f, 0.07874016f, "doc.gc.home.category_machines", Constant.id("home"), 3)
                .addRedirectButtonNormalized(0.0043196543f, 0.46456692f, 0.26349893f, 0.07874016f, "doc.gc.home.category_rockets", Constant.id("home"), 4)
                .addRedirectButtonNormalized(0.0043196543f, 0.5472441f, 0.26349893f, 0.07874016f, "doc.gc.home.category_planets", Constant.id("home"), 5)
                .addRedirectButtonNormalized(0.0043196543f, 0.62992126f, 0.26349893f, 0.07874016f, "doc.gc.home.category_entities", Constant.id("home"), 6)
                .addRedirectButtonNormalized(0.0043196543f, 0.71259844f, 0.26349893f, 0.07874016f, "doc.gc.home.category_blocks", Constant.id("home"), 7)
                .addRedirectButtonNormalized(0.0043196543f, 0.79527557f, 0.26349893f, 0.07874016f, "doc.gc.home.category_items", Constant.id("home"), 8)
                .addRedirectButtonNormalized(0.0043196543f, 0.87795275f, 0.26349893f, 0.07874016f, "doc.gc.home.category_fluids_and_gasses", Constant.id("home"), 9)
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/home.json");
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