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
import dev.galacticraft.mod.api.documentation.client.DocsClient;
import dev.galacticraft.mod.api.documentation.client.RenderablesAccessor;
import dev.galacticraft.mod.api.documentation.model.ButtonElement;
import dev.galacticraft.mod.api.documentation.model.Element;
import dev.galacticraft.mod.api.documentation.model.HomeDoc;
import dev.galacticraft.mod.api.documentation.model.ImageElement;
import dev.galacticraft.mod.api.documentation.model.SubDoc;
import dev.galacticraft.mod.api.documentation.model.TextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic docs page screen that renders background, title, and elements (buttons, text boxes, images).
 * Includes an operator-only WYSIWYG editor toggled by holding the Inspect key for 5 seconds.
 */
public final class BlankPageScreen extends Screen {
    private static final ResourceLocation BG = Constant.id("textures/gui/docs/background.png");

    private final ResourceLocation pageId;
    private final String titleKey;

    private boolean isHome;
    private @Nullable ResourceLocation parentId;

    private final List<TextBox> textBoxes = new ArrayList<>();
    private final List<ImageSpec> images = new ArrayList<>();
    private @Nullable TitlePos titlePos;

    private static final long HOLD_MS = 3000L;
    private static final int HANDLE = 6;

    private final List<UiElement> ui = new ArrayList<>();
    private boolean editing = false;
    private long inspectHoldStart = -1L;
    private @Nullable UiElement selected = null;
    private boolean dragging = false;
    private boolean resizing = false;
    private int dragStartX, dragStartY;
    private int origX, origY, origW, origH;

    private @Nullable EditBox inlineEditor = null;
    private @Nullable TextCommit pendingCommit = null;

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
        this.ui.clear();
        this.selected = null;
        this.inlineEditor = null;

        if (!isHome && parentId != null) {
            var back = Button.builder(Component.translatable("gc.docs.back"), b -> {
                        if (!editing) DocsApi.open(parentId);
                    })
                    .pos(10, 10).size(70, 20).build();
            addRenderableWidget(back);
        }

        if (isHome) {
            HomeDoc home = DocsManager.getHomeDoc();
            if (home != null) {
                if (home.titlePos() != null) this.titlePos = new TitlePos(home.titlePos().x(), home.titlePos().y());
                buildElements(home.elements());
                mirrorElementsToUi(home.elements());
            }
        } else {
            SubDoc doc = DocsManager.getDoc(pageId);
            if (doc != null) {
                if (doc.titlePos() != null) this.titlePos = new TitlePos(doc.titlePos().x(), doc.titlePos().y());
                buildElements(doc.elements());
                mirrorElementsToUi(doc.elements());
            }
        }

        if (editing) {
            relinkRuntimeButtons();
            relinkRuntimeTextBoxes();
        }
    }

    /** Build live widgets and simple structs used by normal (non-editor) rendering. */
    private void buildElements(@Nullable List<Element> elements) {
        if (elements == null || elements.isEmpty()) return;

        for (Element el : elements) {
            if (el instanceof ButtonElement btn) {
                var text = Component.translatable(btn.textKey());
                var target = ResourceLocation.parse(btn.target());
                addRenderableWidget(Button.builder(text, b -> {
                            if (!editing) DocsApi.open(target);
                        })
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

    /** Mirror JSON elements into editor-manipulable UI elements with resolved (plain) text. */
    private void mirrorElementsToUi(@Nullable List<Element> elements) {
        if (elements == null) return;
        for (Element el : elements) {
            if (el instanceof ButtonElement b) {
                String plain = Component.translatable(b.textKey()).getString();
                var target = ResourceLocation.parse(b.target());
                ui.add(new BtnUi(b.x(), b.y(), b.w(), b.h(), plain, target));
            } else if (el instanceof TextElement t) {
                String plain = Component.translatable(t.textKey()).getString();
                ui.add(new TxtUi(t.minX(), t.minY(), t.maxX(), t.maxY(), plain, t.align()));
            } else if (el instanceof ImageElement img) {
                ui.add(new ImgUi(img.x(), img.y(), img.w(), img.h(),
                        ResourceLocation.parse(img.texture()), img.u(), img.v(), img.texW(), img.texH()));
            }
        }
    }

    /** Pair live Button widgets to our BtnUi elements (so moving/resizing updates the on-screen button). */
    private void relinkRuntimeButtons() {
        for (var widget : ((RenderablesAccessor)((Screen)this)).gc$getRenderables()) {
            if (widget instanceof Button btn) {
                for (var u : ui) {
                    if (u instanceof BtnUi b && b.runtime == null) {
                        if (btn.getX() == b.x && btn.getY() == b.y && btn.getWidth() == b.w && btn.getHeight() == b.h) {
                            b.runtime = btn;
                            b.relayoutRuntime();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void relinkRuntimeTextBoxes() {
        for (var u : ui) {
            if (u instanceof TxtUi t && t.link == null) {
                for (var tb : textBoxes) {
                    boolean sameBounds = (tb.minX == t.minX && tb.minY == t.minY && tb.maxX == t.maxX && tb.maxY == t.maxY);
                    if (sameBounds) {
                        t.link = tb;
                        if (editing) tb.setDebugText(t.text);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float dt) {
        g.blit(BG, 0, 0, 0, 0, this.width, this.height, 256, 256);

        for (ImageSpec img : images) {
            g.blit(img.texture, img.x, img.y, img.u, img.v, img.w, img.h, img.texW, img.texH);
        }

        super.render(g, mouseX, mouseY, dt);

        var title = Component.translatable(titleKey);
        int titleX = (this.titlePos != null) ? this.titlePos.x : (this.width - this.font.width(title)) / 2;
        int titleY = (this.titlePos != null) ? this.titlePos.y : 22;
        g.drawString(this.font, title, titleX, titleY, 0xFFFFFF, false);

        for (TextBox tb : textBoxes) {
            drawWrappedTextBox(g, this.font, tb);
        }

        if (editing) {
            if (selected != null) {
                drawSelectionFrame(g, selected);
            }
            drawEditorHelp(g);
        }
    }

    private void drawWrappedTextBox(GuiGraphics g, Font font, TextBox tb) {
        var lines = tb.getWrappedLines(font);
        int y = tb.minY;
        for (var line : lines) {
            int lineWidth = font.width(line);
            int x;
            if ("center".equalsIgnoreCase(tb.align)) {
                x = tb.minX + (Math.max(0, tb.maxX - tb.minX) - lineWidth) / 2;
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

    private void drawSelectionFrame(GuiGraphics g, UiElement el) {
        int x = el.x(), y = el.y(), w = el.w(), h = el.h();
        g.fill(x - 1, y - 1, x + w + 1, y, 0xAAFFFFFF);
        g.fill(x - 1, y + h, x + w + 1, y + h + 1, 0xAAFFFFFF);
        g.fill(x - 1, y, x, y + h, 0xAAFFFFFF);
        g.fill(x + w, y, x + w + 1, y + h, 0xAAFFFFFF);
        g.fill(x + w - HANDLE, y + h - HANDLE, x + w, y + h, 0xCC00FF00);
    }

    private void drawEditorHelp(GuiGraphics g) {
        var help = Component.literal("[Editor] B: Button  T: Text  I: Image  E: Edit  DEL: Remove  Drag: move  Corner: resize  Hold Inspect 5s: export+close");
        int pad = 6;
        g.fill(0, this.height - 24, this.width, this.height, 0x88000000);
        g.drawString(this.font, help, pad, this.height - 18, 0xFFFFFF, false);
    }

    @Override
    public void tick() {
        super.tick();

        boolean inspectDown = DocsClient.INSPECT.isDown();
        if (inspectDown && isOp()) {
            if (inspectHoldStart < 0) inspectHoldStart = System.currentTimeMillis();
            long held = System.currentTimeMillis() - inspectHoldStart;
            if (!editing && held >= HOLD_MS) {
                enterEditMode();
                inspectHoldStart = System.currentTimeMillis();
            } else if (editing && held >= HOLD_MS) {
                exportToClipboardAndClose();
                inspectHoldStart = System.currentTimeMillis();
            }
        } else {
            inspectHoldStart = -1L;
        }
    }

    private boolean isOp() {
        var p = Minecraft.getInstance().player;
        return p != null && p.hasPermissions(2);
    }

    private void enterEditMode() {
        editing = true;
        relinkRuntimeButtons();
        relinkRuntimeTextBoxes();
        setFocused(null);
        inlineEditor = null;
        selected = null;
    }

    private void exitEditMode() {
        editing = false;
        inlineEditor = null;
        selected = null;
        for (var tb : textBoxes) tb.setDebugText(null);
    }

    private void exportToClipboardAndClose() {
        String snippet = buildExportSnippet();
        Minecraft.getInstance().keyboardHandler.setClipboard(snippet);
        exitEditMode();
        onClose();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (!editing) return super.mouseClicked(mx, my, button);

        if (inlineEditor != null && !inlineEditor.isMouseOver(mx, my)) {
            commitInlineEditor();
        }

        for (UiElement e : ui) {
            if (e.onResizeHandle((int) mx, (int) my)) {
                selected = e;
                resizing = true;
                dragging = false;
                dragStartX = (int) mx;
                dragStartY = (int) my;
                origX = e.x();
                origY = e.y();
                origW = e.w();
                origH = e.h();
                return true;
            }
        }

        for (UiElement e : ui) {
            if (e.hit((int) mx, (int) my)) {
                selected = e;
                dragging = true;
                resizing = false;
                dragStartX = (int) mx;
                dragStartY = (int) my;
                origX = e.x();
                origY = e.y();
                return true;
            }
        }

        selected = null;
        return true;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (!editing || selected == null) return super.mouseDragged(mx, my, button, dx, dy);

        int offX = (int) mx - dragStartX;
        int offY = (int) my - dragStartY;

        if (dragging) {
            selected.setPos(origX + offX, origY + offY);
            return true;
        } else if (resizing) {
            selected.setSize(origW + offX, origH + offY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragging = false;
        resizing = false;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!editing) return super.keyPressed(keyCode, scanCode, modifiers);

        if (inlineEditor != null) {
            boolean handled = inlineEditor.keyPressed(keyCode, scanCode, modifiers) || inlineEditor.canConsumeInput();
            if (handled) return true;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_ESCAPE -> {
                exitEditMode();
                return true;
            }
            case GLFW.GLFW_KEY_DELETE, GLFW.GLFW_KEY_BACKSPACE -> {
                if (selected != null) {
                    if (selected instanceof BtnUi b) {
                        if (b.runtime != null) this.removeWidget(b.runtime);
                        this.ui.remove(b);
                    } else if (selected instanceof TxtUi t) {
                        if (t.link != null) this.textBoxes.remove(t.link);
                        this.ui.remove(t);
                    } else if (selected instanceof ImgUi i) {
                        this.ui.remove(i);
                    }
                    selected = null;
                    return true;
                }
            }
            case GLFW.GLFW_KEY_B -> {
                int x = this.width / 2 - 40, y = this.height / 2 - 10;
                var btn = new BtnUi(x, y, 80, 20, "Button", DocsManager.HOME_ID);
                var runtime = Button.builder(Component.literal(btn.text), b -> {
                            if (!editing) DocsApi.open(btn.target);
                        })
                        .pos(btn.x, btn.y).size(btn.w, btn.h).build();
                this.addRenderableWidget(runtime);
                btn.runtime = runtime;
                ui.add(btn);
                selected = btn;
                return true;
            }
            case GLFW.GLFW_KEY_T -> {
                int x = 20, y = 60, w = 180, h = 60;
                TextBox tb = new TextBox(x, y, x + w, y + h, "_debug.text", "left");
                tb.setDebugText("New text");
                this.textBoxes.add(tb);

                TxtUi uiTxt = new TxtUi(x, y, x + w, y + h, "New text", "left");
                uiTxt.link = tb;

                this.ui.add(uiTxt);
                this.selected = uiTxt;
                return true;
            }
            case GLFW.GLFW_KEY_I -> {
                ui.add(new ImgUi(20, 20, 32, 32, Constant.id("textures/gui/docs/background.png"), 0, 0, 256, 256));
                return true;
            }
            case GLFW.GLFW_KEY_E -> {
                if (selected instanceof BtnUi b) {
                    openInlineEditor(b.text, s -> { b.text = s; b.relayoutRuntime(); });
                    return true;
                } else if (selected instanceof TxtUi t) {
                    openInlineEditor(t.text, s -> {
                        t.text = s;
                        if (t.link != null) t.link.setDebugText(s);
                    });
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private interface TextCommit { void accept(String s); }

    private void openInlineEditor(String initial, TextCommit commit) {
        if (inlineEditor != null) commitInlineEditor();
        inlineEditor = new EditBox(this.font, 20, this.height - 26, this.width - 40, 18, Component.literal("edit"));
        inlineEditor.setValue(initial);
        inlineEditor.setMaxLength(4096);
        this.addRenderableWidget(inlineEditor);
        this.setFocused(inlineEditor);
        this.pendingCommit = commit;
    }

    private void commitInlineEditor() {
        if (inlineEditor != null && pendingCommit != null) {
            pendingCommit.accept(inlineEditor.getValue());
        }
        if (inlineEditor != null) this.removeWidget(inlineEditor);
        inlineEditor = null;
        pendingCommit = null;
    }

    private String buildExportSnippet() {
        StringBuilder sb = new StringBuilder();
        sb.append("JsonObject root = new SubPageBuilder(SCHEMA, PAGE_ID)\n");

        String titlePlain = Component.translatable(this.titleKey).getString();
        sb.append("        .setTitleText(").append(escape(titlePlain)).append(")\n");

        var parent = DocsManager.parentOf(pageId);
        if (parent != null && !DocsManager.HOME_ID.equals(parent)) {
            sb.append("        .setParent(Constant.id(\"").append(parent).append("\"))\n");
        }

        for (var e : ui) {
            sb.append(e.exportLine()).append("\n");
        }
        sb.append("        .build();\n");
        return sb.toString();
    }

    private static String escape(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record TitlePos(int x, int y) {}

    private static final class TextBox {
        int minX, minY, maxX, maxY;
        String textKey;
        String align;

        private int cachedWidth = -1;
        private java.util.List<net.minecraft.util.FormattedCharSequence> cachedLines = java.util.List.of();

        @org.jetbrains.annotations.Nullable String debugOverrideText = null;

        TextBox(int minX, int minY, int maxX, int maxY, String textKey, @org.jetbrains.annotations.Nullable String align) {
            this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY;
            this.textKey = textKey;
            this.align = align == null ? "left" : align;
        }

        void setBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY;
            invalidateWrap();
        }

        void moveBy(int dx, int dy) {
            setBounds(this.minX + dx, this.minY + dy, this.maxX + dx, this.maxY + dy);
        }

        void setDebugText(@org.jetbrains.annotations.Nullable String s) {
            if (!java.util.Objects.equals(this.debugOverrideText, s)) {
                this.debugOverrideText = s;
                invalidateWrap();
            }
        }

        void invalidateWrap() {
            this.cachedWidth = -1;
            this.cachedLines = java.util.List.of();
        }

        java.util.List<net.minecraft.util.FormattedCharSequence> getWrappedLines(net.minecraft.client.gui.Font font) {
            int width = Math.max(0, maxX - minX);
            if (width <= 0) return java.util.List.of();
            if (width != cachedWidth || cachedLines.isEmpty()) {
                cachedWidth = width;
                if (debugOverrideText != null) {
                    cachedLines = font.split(net.minecraft.network.chat.Component.literal(debugOverrideText), width);
                } else {
                    cachedLines = font.split(net.minecraft.network.chat.Component.translatable(textKey), width);
                }
            }
            return cachedLines;
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

    private sealed interface UiElement permits BtnUi, TxtUi, ImgUi {
        int x();
        int y();
        int w();
        int h();
        void setPos(int x, int y);
        void setSize(int w, int h);
        default boolean hit(int mx, int my) { return mx >= x() && mx <= x() + w() && my >= y() && my <= y() + h(); }
        default boolean onResizeHandle(int mx, int my) {
            return mx >= x() + w() - HANDLE && mx <= x() + w() && my >= y() + h() - HANDLE && my <= y() + h();
        }
        String exportLine();
    }

    private final class BtnUi implements UiElement {
        int x, y, w, h;
        String text;
        ResourceLocation target;
        Button runtime;

        BtnUi(int x, int y, int w, int h, String text, ResourceLocation target) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.text = text; this.target = target;
        }

        @Override public int x() { return x; }
        @Override public int y() { return y; }
        @Override public int w() { return w; }
        @Override public int h() { return h; }

        @Override public void setPos(int nx, int ny) { x = nx; y = ny; relayoutRuntime(); }
        @Override public void setSize(int nw, int nh) { w = Math.max(10, nw); h = Math.max(10, nh); relayoutRuntime(); }

        void relayoutRuntime() {
            if (runtime != null) {
                runtime.setX(x);
                runtime.setY(y);
                runtime.setWidth(w);
                runtime.setHeight(h);
                runtime.setMessage(Component.literal(text));
            }
        }

        @Override public String exportLine() {
            return "        .addRedirectButton(" + x + ", " + y + ", " + w + ", " + h + ", "
                    + escape(text) + ", Constant.id(\"" + target + "\"))";
        }
    }

    private static final class TxtUi implements UiElement {
        int minX, minY, maxX, maxY;
        String text;
        String align;
        @org.jetbrains.annotations.Nullable TextBox link;

        TxtUi(int minX, int minY, int maxX, int maxY, String text, String align) {
            this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY;
            this.text = text; this.align = (align == null ? "left" : align);
        }

        @Override public int x() { return minX; }
        @Override public int y() { return minY; }
        @Override public int w() { return maxX - minX; }
        @Override public int h() { return maxY - minY; }

        @Override public void setPos(int nx, int ny) {
            int w = maxX - minX, h = maxY - minY;
            minX = nx; minY = ny; maxX = minX + w; maxY = minY + h;
            if (link != null) link.setBounds(minX, minY, maxX, maxY);
        }

        @Override public void setSize(int nw, int nh) {
            maxX = minX + Math.max(10, nw);
            maxY = minY + Math.max(10, nh);
            if (link != null) link.setBounds(minX, minY, maxX, maxY);
        }

        @Override public String exportLine() {
            return "        .addTextBox(" + minX + ", " + minY + ", " + maxX + ", " + maxY + ", "
                    + escape(text) + ", " + (align == null ? "null" : escape(align)) + ")";
        }
    }

    private static final class ImgUi implements UiElement {
        int x, y, w, h, u, v, tw, th;
        ResourceLocation tex;

        ImgUi(int x, int y, int w, int h, ResourceLocation tex, int u, int v, int tw, int th) {
            this.x = x; this.y = y; this.w = w; this.h = h;
            this.tex = tex; this.u = u; this.v = v; this.tw = tw; this.th = th;
        }

        @Override public int x() { return x; }
        @Override public int y() { return y; }
        @Override public int w() { return w; }
        @Override public int h() { return h; }

        @Override public void setPos(int nx, int ny) { x = nx; y = ny; }
        @Override public void setSize(int nw, int nh) { w = Math.max(10, nw); h = Math.max(10, nh); }

        @Override public String exportLine() {
            return "        .addImage(" + x + ", " + y + ", " + w + ", " + h + ", "
                    + "new ResourceLocation(\"" + tex + "\"), " + u + ", " + v + ", " + tw + ", " + th + ")";
        }
    }
}