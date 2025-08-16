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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

final class HomePageBuilder extends BasePageBuilder<HomePageBuilder> {
    private final List<ResourceLocation> featured = new ArrayList<>();

    HomePageBuilder(int schema) {
        super(schema);
    }

    /**
     * Add a “featured” page id to show as a quick link on the home page.
     * Kept for compatibility with earlier formats.
     *
     * @param pageId id of the page to feature
     * @return {@link HomePageBuilder}
     */
    public HomePageBuilder addFeatured(ResourceLocation pageId) {
        this.featured.add(pageId);
        return this;
    }

    @Override
    public JsonObject build() {
        JsonObject root = new JsonObject();
        bakeCommon(root);

        if (!featured.isEmpty()) {
            JsonArray links = new JsonArray();
            for (ResourceLocation rl : featured) links.add(rl.toString());
            root.add("links", links);
        }
        return root;
    }
}