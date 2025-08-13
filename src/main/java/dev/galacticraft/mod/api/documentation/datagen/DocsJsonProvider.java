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

    DocsJsonProvider(FabricDataOutput out) { this.out = out; }

    @Override public String getName() { return "GC Docs JSON"; }

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
                .setTitlePosition(12, 18)
                .addTextBox(12, 48, 12 + 320, 48 + 60, "doc.gc.home.intro", "left")
                .addRedirectButton(12, 120, 160, 20, "doc.gc.home.btn.airlock", AIRLOCK_PAGE)
                .addFeatured(AIRLOCK_PAGE)
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
                .addTextBox(14, 52, 14 + 320, 52 + 70, "doc.gc.airlock.overview", "left")
                .addRedirectButton(14, 130, 120, 20, "gc.docs.back", Constant.id("home"))
                .build();

        Path p = out.getOutputFolder().resolve("assets/galacticraft/docs/pages/air_lock_controller.json");
        return save(cache, root, p);
    }

    private CompletableFuture<?> save(CachedOutput cache, Object json, Path path) {
        try { Files.createDirectories(path.getParent()); } catch (IOException e) { throw new RuntimeException(e); }
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), path);
    }
}