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

package dev.galacticraft.mod.api.documentation.client.pages;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.documentation.DocsApi;
import dev.galacticraft.mod.api.documentation.DocsManager;
import dev.galacticraft.mod.api.documentation.model.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BlankPageScreen extends Screen {
    private static final ResourceLocation BG = Constant.id("textures/gui/docs/background.png");

    private final ResourceLocation pageId;
    private final String titleKey;

    private boolean isHome;
    private @Nullable ResourceLocation parentId;

    private final List<TextBox> textBoxes = new ArrayList<>();
    private final List<ImageSpec> images = new ArrayList<>();
    private @Nullable TitlePos titlePos;

    public BlankPageScreen(ResourceLocation pageId, String titleKey) {
        super(Component.translatable(titleKey));
        this.pageId = pageId;
        this.titleKey = titleKey;
    }

    @Override
    protected void init() {
        this.isHome = DocsManager.HOME_ID.equals(pageId);
        this.parentId = DocsManager.parentOf(pageId);

        this.textBoxes.clear();
        this.images.clear();
        this.clearWidgets();

        if (!isHome && parentId != null) {
            var back = Button.builder(Component.translatable("gc.docs.back"), b -> DocsApi.open(parentId))
                    .pos(10, 10).size(70, 20).build();
            addRenderableWidget(back);
        }

        if (isHome) {
            HomeDoc home = DocsManager.getHomeDoc();
            if (home != null) {
                if (home.titlePos() != null) this.titlePos = new TitlePos(home.titlePos().x(), home.titlePos().y());
                buildElements(home.elements());
            }
        } else {
            SubDoc doc = DocsManager.getDoc(pageId);
            if (doc != null) {
                if (doc.titlePos() != null) this.titlePos = new TitlePos(doc.titlePos().x(), doc.titlePos().y());
                buildElements(doc.elements());
            }
        }
    }

    private void buildElements(@Nullable List<Element> elements) {
        if (elements == null || elements.isEmpty()) return;

        for (Element el : elements) {
            if (el instanceof ButtonElement btn) {
                var text = Component.translatable(btn.textKey());
                var target = ResourceLocation.parse(btn.target());
                addRenderableWidget(Button.builder(text, b -> DocsApi.open(target))
                        .pos(btn.x(), btn.y())
                        .size(btn.w(), btn.h())
                        .build());
            } else if (el instanceof TextElement tb) {
                textBoxes.add(new TextBox(tb.minX(), tb.minY(), tb.maxX(), tb.maxY(), tb.textKey(), tb.align()));
            } else if (el instanceof ImageElement img) {
                images.add(new ImageSpec(
                        ResourceLocation.parse(img.texture()),
                        img.x(), img.y(), img.w(), img.h(),
                        img.u(), img.v(), img.texW(), img.texH()
                ));
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float dt) {

        g.blit(BG, 0, 0, 0, 0, this.width, this.height, 256, 256);
        super.render(g, mouseX, mouseY, dt);

        for (ImageSpec img : images) {
            g.blit(img.texture, img.x, img.y, img.u, img.v, img.w, img.h, img.texW, img.texH);
        }

        var title = Component.translatable(titleKey);
        int titleX = (this.titlePos != null) ? this.titlePos.x : (this.width - this.font.width(title)) / 2;
        int titleY = (this.titlePos != null) ? this.titlePos.y : 22;
        g.drawString(this.font, title, titleX, titleY, 0xFFFFFF, false);

        for (TextBox tb : textBoxes) {
            drawWrappedTextBox(g, this.font, tb);
        }
    }

    private void drawWrappedTextBox(GuiGraphics g, Font font, TextBox tb) {
        Component text = Component.translatable(tb.textKey);
        int maxWidth = Math.max(0, tb.maxX - tb.minX);
        if (maxWidth <= 0) return;

        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        int y = tb.minY;
        for (FormattedCharSequence line : lines) {
            int lineWidth = font.width(line);
            int x;
            if ("center".equalsIgnoreCase(tb.align)) {
                x = tb.minX + (maxWidth - lineWidth) / 2;
            } else if ("right".equalsIgnoreCase(tb.align)) {
                x = tb.maxX - lineWidth;
            } else {
                x = tb.minX;
            }

            if (y + font.lineHeight > tb.maxY) break;

            g.drawString(font, line, x, y, 0xFFFFFF, false);
            y += font.lineHeight + 2;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record TitlePos(int x, int y) {}

    private static final class TextBox {
        final int minX, minY, maxX, maxY;
        final String textKey;
        final String align;

        TextBox(int minX, int minY, int maxX, int maxY, String textKey, @Nullable String align) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.textKey = textKey;
            this.align = align == null ? "left" : align;
        }
    }

    private static final class ImageSpec {
        final ResourceLocation texture;
        final int x, y, w, h;
        final int u, v, texW, texH;

        ImageSpec(ResourceLocation texture, int x, int y, int w, int h, int u, int v, int texW, int texH) {
            this.texture = texture;
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.u = u; this.v = v; this.texW = texW; this.texH = texH;
        }
    }
}