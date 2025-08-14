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
    protected final JsonArray elements = new JsonArray();
    protected String titleKey = "doc.gc.home.title";
    protected Integer titleX = null, titleY = null;

    protected BasePageBuilder(int schema) {
        this.schema = schema;
    }

    /**
     * Set the page title translation key
     *
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
     *
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

    @SuppressWarnings("unchecked")
    public T addRedirectButtonNormalized(int nx, int ny, int nw, int nh, String textTranslationKey, ResourceLocation targetPage) {
        return addRedirectButtonNormalized(nx, ny, nw, nh, textTranslationKey, targetPage, elements.size());
    }

    /**
     * Add a button that navigates to a docs page (by page id).
     *
     * @param nx                 X position of button normalized
     * @param ny                 Y position of button normalized
     * @param nw                 Width of button normalized
     * @param nh                 Height of button normalized
     * @param textTranslationKey Translatable key for button text
     * @param targetPage         Page to redirect to
     * @param order              Layer order of button
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T addRedirectButtonNormalized(float nx, float ny, float nw, float nh,
                                         String textTranslationKey, ResourceLocation targetPage, int order) {
        JsonObject btn = new JsonObject();
        btn.addProperty("type", "button");
        btn.addProperty("nx", nx);
        btn.addProperty("ny", ny);
        btn.addProperty("nw", nw);
        btn.addProperty("nh", nh);
        btn.addProperty("textKey", textTranslationKey);
        btn.addProperty("target", targetPage.toString());
        btn.addProperty("order", order);
        elements.add(btn);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addTextBoxNormalized(int nminX, int nminY, int nmaxX, int nmaxY, String textTranslationKey, String align /* nullable */) {
        return addTextBoxNormalized(nminX, nminY, nmaxX, nmaxY, textTranslationKey, align, elements.size());
    }

    /**
     * Add a text box with bounds; renderer can auto-wrap. Alignment is optional (left/center/right).
     *
     * @param nminX              minimum X position normalized
     * @param nminY              minimum Y position normalized
     * @param nmaxX              maximum X position normalized
     * @param nmaxY              maximum Y position normalized
     * @param textTranslationKey translatable key for text content
     * @param align              optional alignment ("left", "center", or "right"); may be {@code null}
     * @param order              Layer order of text box
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T addTextBoxNormalized(float nminX, float nminY, float nmaxX, float nmaxY,
                                  String textTranslationKey, String align, int order) {
        JsonObject tb = new JsonObject();
        tb.addProperty("type", "text");
        tb.addProperty("nminX", nminX);
        tb.addProperty("nminY", nminY);
        tb.addProperty("nmaxX", nmaxX);
        tb.addProperty("nmaxY", nmaxY);
        tb.addProperty("textKey", textTranslationKey);
        if (align != null) tb.addProperty("align", align);
        tb.addProperty("order", order);
        elements.add(tb);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addImageNormalized(int nx, int ny, int nw, int nh, ResourceLocation texture, int u, int v, int texW, int texH) {
        return addImageNormalized(nx, ny, nw, nh, texture, u, v, texW, texH, elements.size());
    }

    /**
     * Add a static image to the page.
     *
     * @param nx      X position of the image normalized
     * @param ny      Y position of the image normalized
     * @param nw      width of the image normalized
     * @param nh      height of the image normalized
     * @param texture texture resource location
     * @param u       U-coordinate in the texture (px)
     * @param v       V-coordinate in the texture (px)
     * @param texW    full texture width normalized
     * @param texH    full texture height normalized
     * @param order   Label order of image
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    public T addImageNormalized(float nx, float ny, float nw, float nh,
                                ResourceLocation texture, int u, int v, int texW, int texH, int order) {
        JsonObject img = new JsonObject();
        img.addProperty("type", "image");
        img.addProperty("nx", nx);
        img.addProperty("ny", ny);
        img.addProperty("nw", nw);
        img.addProperty("nh", nh);
        img.addProperty("texture", texture.toString());
        img.addProperty("u", u);
        img.addProperty("v", v);
        img.addProperty("texW", texW);
        img.addProperty("texH", texH);
        img.addProperty("order", order);
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