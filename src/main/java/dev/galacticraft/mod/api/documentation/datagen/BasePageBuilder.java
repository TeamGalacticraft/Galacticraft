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

import java.util.Objects;

abstract class BasePageBuilder<T extends BasePageBuilder<T>> {
    protected final int schema;
    protected String titleKey = "doc.gc.home.title";
    protected Integer titleX = null, titleY = null;
    protected final JsonArray elements = new JsonArray();

    protected BasePageBuilder(int schema) {
        this.schema = schema;
    }

    /**
     * Set the page title translation key
     * @param titleKey TRANSLATABLE KEY
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T setTitleText(String titleKey) {
        this.titleKey = Objects.requireNonNull(titleKey, "titleKey");
        return (T) this;
    }

    /**
     * Set the position of the title in pixels.
     * If unset defaults to center
     * @param x X pos in px
     * @param y Y pos in px
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T setTitlePosition(int x, int y) {
        this.titleX = x;
        this.titleY = y;
        return (T) this;
    }

    /**
     * Add a button that navigates to a docs page (by page id).
     * @param x X position of button in px
     * @param y Y position of button in px
     * @param width Width of button in px
     * @param height Height of button in px
     * @param textTranslationKey Translatable key for button text
     * @param targetPage Page to redirect to
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T addRedirectButton(int x, int y, int width, int height, String textTranslationKey, ResourceLocation targetPage) {
        JsonObject btn = new JsonObject();
        btn.addProperty("type", "button");
        btn.addProperty("x", x);
        btn.addProperty("y", y);
        btn.addProperty("w", width);
        btn.addProperty("h", height);
        btn.addProperty("textKey", textTranslationKey);
        btn.addProperty("target", targetPage.toString());
        elements.add(btn);
        return (T) this;
    }

    /**
     * Add a text box with bounds; renderer can auto-wrap. Alignment is optional (left/center/right).
     *
     * @param minX minimum X position in px
     * @param minY minimum Y position in px
     * @param maxX maximum X position in px
     * @param maxY maximum Y position in px
     * @param textTranslationKey translatable key for text content
     * @param align optional alignment ("left", "center", or "right"); may be {@code null}
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T addTextBox(int minX, int minY, int maxX, int maxY, String textTranslationKey, String align /* nullable */) {
        JsonObject tb = new JsonObject();
        tb.addProperty("type", "text");
        tb.addProperty("minX", minX);
        tb.addProperty("minY", minY);
        tb.addProperty("maxX", maxX);
        tb.addProperty("maxY", maxY);
        tb.addProperty("textKey", textTranslationKey);
        if (align != null) tb.addProperty("align", align); // "left" | "center" | "right"
        elements.add(tb);
        return (T) this;
    }

    /**
     * Add a static image to the page.
     *
     * @param x X position of the image in px
     * @param y Y position of the image in px
     * @param width width of the image in px
     * @param height height of the image in px
     * @param texture texture resource location
     * @param u U-coordinate in the texture (px)
     * @param v V-coordinate in the texture (px)
     * @param texW full texture width in px
     * @param texH full texture height in px
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T addImage(int x, int y, int width, int height, ResourceLocation texture, int u, int v, int texW, int texH) {
        JsonObject img = new JsonObject();
        img.addProperty("type", "image");
        img.addProperty("x", x);
        img.addProperty("y", y);
        img.addProperty("w", width);
        img.addProperty("h", height);
        img.addProperty("texture", texture.toString());
        img.addProperty("u", u);
        img.addProperty("v", v);
        img.addProperty("texW", texW);
        img.addProperty("texH", texH);
        elements.add(img);
        return (T) this;
    }

    /**
     * Build common page fields into the provided JSON root object.
     *
     * @param root target JSON object to populate with common fields
     */
    protected void bakeCommon(JsonObject root) {
        root.addProperty("schema", schema);
        root.addProperty("titleKey", titleKey);
        if (titleX != null && titleY != null) {
            JsonObject tp = new JsonObject();
            tp.addProperty("x", titleX);
            tp.addProperty("y", titleY);
            root.add("titlePos", tp);
        }
        if (elements.size() > 0) {
            root.add("elements", elements);
        }
    }

    public abstract JsonObject build();
}