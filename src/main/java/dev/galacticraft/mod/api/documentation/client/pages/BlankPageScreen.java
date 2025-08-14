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
import dev.galacticraft.mod.api.documentation.model.HomeDoc;
import dev.galacticraft.mod.api.documentation.model.SubDoc;
import dev.galacticraft.mod.api.documentation.model.elements.ButtonElement;
import dev.galacticraft.mod.api.documentation.model.elements.Element;
import dev.galacticraft.mod.api.documentation.model.elements.ImageElement;
import dev.galacticraft.mod.api.documentation.model.elements.TextElement;
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
import java.util.Objects;

/**
 * Generic docs page screen that renders background, title, and elements (buttons, text boxes, images).
 * Includes an operator-only WYSIWYG editor toggled by holding the Inspect key for 3 seconds.
 * Editor UI uses a top-right "burger" button to open a right-hand sidebar with keybinds,
 * layer controls (bring forward / push back), and a live Properties panel.
 */
public final class BlankPageScreen extends Screen {
    private static final ResourceLocation BG = Constant.id("textures/gui/docs/background.png");
    private static final long HOLD_MS = 3000L;
    private static final int HANDLE = 6;
    private static final int SIDEBAR_W = 220;
    private static final int SIDEBAR_LAYERS_TOP = 80;
    private static final int SCROLLBAR_W = 4;
    private final ResourceLocation pageId;
    private final String titleKey;
    private final List<TextBox> textBoxes = new ArrayList<>();
    private final List<ImageSpec> images = new ArrayList<>();
    private final List<UiElement> ui = new ArrayList<>();
    private final List<PropertyRow> propertyRows = new ArrayList<>();
    private boolean isHome;
    private @Nullable ResourceLocation parentId;
    private @Nullable Button backBtn;
    private @Nullable TitlePos titlePos;
    private boolean editing = false;
    private long inspectHoldStart = -1L;
    private @Nullable UiElement selected = null;
    private boolean dragging = false;
    private boolean resizing = false;
    private int dragStartX, dragStartY;
    private int origX, origY, origW, origH;
    private boolean sidebarOpen = false;
    private @Nullable Button burgerBtn;
    private @Nullable Button bringFwdBtn, pushBackBtn;
    private @Nullable Button alignCycleBtn;
    private int propScrollY = 0;
    private int propContentHeight = 0;
    private boolean draggingScrollbar = false;
    private int dragStartMouseY = 0;
    private int dragStartScrollY = 0;
    private @Nullable EditBox inlineEditor = null;
    private @Nullable TextCommit pendingCommit = null;
    public BlankPageScreen(ResourceLocation pageId, String titleKey) {
        super(Component.translatable(titleKey));
        this.pageId = pageId;
        this.titleKey = titleKey;
    }

    private static String escape(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    @Override
    protected void init() {
        this.isHome = DocsManager.HOME_ID.equals(pageId);
        this.parentId = DocsManager.parentOf(pageId);

        this.textBoxes.clear();
        this.images.clear();
        this.clearWidgets();
        this.ui.clear();
        this.alignCycleBtn = null;
        this.selected = null;
        this.inlineEditor = null;

        if (editing) {
            int burgerSize = 16;
            this.burgerBtn = Button.builder(Component.literal("≡"), b -> toggleSidebar())
                    .pos(this.width - burgerSize - 6, 6)
                    .size(burgerSize, burgerSize)
                    .build();
            this.addRenderableWidget(this.burgerBtn);
        }

        if (!isHome && parentId != null) {
            this.backBtn = Button.builder(Component.translatable("gc.docs.back"), b -> {
                        if (!editing) DocsApi.open(parentId);
                    })
                    .pos(10, 10).size(70, 20).build();
            addRenderableWidget(this.backBtn);
        } else {
            this.backBtn = null;
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

        relinkRuntimeButtons();
        relinkRuntimeTextBoxes();

        if (editing && sidebarOpen) buildSidebar();
    }

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

    private void mirrorElementsToUi(@Nullable List<Element> elements) {
        if (elements == null) return;
        int fallback = 0;
        for (Element el : elements) {
            int ord;
            if (el instanceof dev.galacticraft.mod.api.documentation.model.elements.ButtonElement bjson) {
                ord = bjson.order();
                String plain = Component.translatable(bjson.textKey()).getString();
                var target = ResourceLocation.parse(bjson.target());
                ui.add(new BtnUi(bjson.x(), bjson.y(), bjson.w(), bjson.h(), plain, target, ord));
            } else if (el instanceof dev.galacticraft.mod.api.documentation.model.elements.TextElement tjson) {
                ord = tjson.order();
                String plain = Component.translatable(tjson.textKey()).getString();
                ui.add(new TxtUi(tjson.minX(), tjson.minY(), tjson.maxX(), tjson.maxY(), plain, tjson.align(), ord));
            } else if (el instanceof dev.galacticraft.mod.api.documentation.model.elements.ImageElement ijson) {
                ord = ijson.order();
                ui.add(new ImgUi(ijson.x(), ijson.y(), ijson.w(), ijson.h(),
                        ResourceLocation.parse(ijson.texture()), ijson.u(), ijson.v(), ijson.texW(), ijson.texH(), ord));
            } else {
                ord = fallback;
            }
            fallback++;
        }
    }

    private java.util.List<UiElement> ordered() {
        return ui;
    }

    private void setRuntimeButtonsActive(boolean active) {
        for (var u : ui) {
            if (u instanceof BtnUi b && b.runtime != null) {
                b.runtime.active = active;
            }
        }
    }

    private void relinkRuntimeButtons() {
        for (var widget : ((RenderablesAccessor) (Screen) this).gc$getRenderables()) {
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

        var title = Component.translatable(titleKey);
        int titleX = (this.titlePos != null) ? this.titlePos.x : (this.width - this.font.width(title)) / 2;
        int titleY = (this.titlePos != null) ? this.titlePos.y : 22;
        g.drawString(this.font, title, titleX, titleY, 0xFFFFFF, false);

        float z = 0.0f;
        for (UiElement el : ordered()) {
            g.pose().pushPose();
            g.pose().translate(0.0f, 0.0f, z);

            if (el instanceof ImgUi i) {
                g.blit(i.tex, i.x, i.y, i.u, i.v, i.w, i.h, i.tw, i.th);
            } else if (el instanceof TxtUi t) {
                if (t.link != null) drawWrappedTextBox(g, this.font, t.link);
                else g.drawString(this.font, Component.literal(t.text), t.minX, t.minY, 0xFFFFFF, false);
            } else if (el instanceof BtnUi b && b.runtime != null) {
                b.runtime.render(g, mouseX, mouseY, dt);
            }

            g.pose().popPose();
            g.flush();
            z += 1f;
        }

        if (backBtn != null) backBtn.render(g, mouseX, mouseY, dt);

        if (editing && burgerBtn != null) {
            burgerBtn.render(g, mouseX, mouseY, dt);
        }

        if (editing && selected != null) {
            drawSelectionFrame(g, selected);
        }

        if (editing && sidebarOpen) {
            g.pose().pushPose();
            g.pose().translate(0.0f, 0.0f, 1000.0f);
            drawSidebar(g);
            renderEditorSidebarWidgets(g, mouseX, mouseY, dt);
            g.pose().popPose();
        }
    }

    private void renderEditorSidebarWidgets(GuiGraphics g, int mouseX, int mouseY, float dt) {
        if (bringFwdBtn != null) bringFwdBtn.render(g, mouseX, mouseY, dt);
        if (pushBackBtn != null) pushBackBtn.render(g, mouseX, mouseY, dt);
        if (alignCycleBtn != null) alignCycleBtn.render(g, mouseX, mouseY, dt);

        for (var row : propertyRows) {
            if (row.box.visible) row.box.render(g, mouseX, mouseY, dt);
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
        g.pose().pushPose();
        g.pose().translate(0.0f, 0.0f, 100.0f);

        int x = el.x(), y = el.y(), w = el.w(), h = el.h();
        g.fill(x - 1, y - 1, x + w + 1, y, 0xAAFFFFFF);
        g.fill(x - 1, y + h, x + w + 1, y + h + 1, 0xAAFFFFFF);
        g.fill(x - 1, y, x, y + h, 0xAAFFFFFF);
        g.fill(x + w, y, x + w + 1, y + h, 0xAAFFFFFF);
        g.fill(x + w - HANDLE, y + h - HANDLE, x + w, y + h, 0xCC00FF00);

        g.pose().popPose();
    }


    private void toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        if (editing) {
            if (sidebarOpen) buildSidebar();
            else clearSidebarWidgets();
        }
    }

    private void buildSidebar() {
        clearSidebarWidgets();

        int x0 = this.width - SIDEBAR_W + 8;
        int y0 = SIDEBAR_LAYERS_TOP;
        bringFwdBtn = Button.builder(Component.literal("Bring forward"), b -> bringForward())
                .pos(x0, y0).size(SIDEBAR_W - 16, 20).build();
        pushBackBtn = Button.builder(Component.literal("Push back"), b -> pushBack())
                .pos(x0, y0 + 24).size(SIDEBAR_W - 16, 20).build();
        addRenderableWidget(bringFwdBtn);
        addRenderableWidget(pushBackBtn);
        refreshLayerButtonsEnabled();

        buildPropertiesForSelection();
    }

    private void refreshLayerButtonsEnabled() {
        boolean sel = selected != null;
        if (bringFwdBtn != null) bringFwdBtn.active = sel;
        if (pushBackBtn != null) pushBackBtn.active = sel;
    }

    private void clearSidebarWidgets() {
        if (bringFwdBtn != null) {
            removeWidget(bringFwdBtn);
            bringFwdBtn = null;
        }
        if (pushBackBtn != null) {
            removeWidget(pushBackBtn);
            pushBackBtn = null;
        }
        if (alignCycleBtn != null) {
            removeWidget(alignCycleBtn);
            alignCycleBtn = null;
        }

        for (var row : propertyRows) removeWidget(row.box);
        propertyRows.clear();
    }

    private void drawSidebar(GuiGraphics g) {
        int x1 = this.width - SIDEBAR_W;
        int y1 = 0;
        int x2 = this.width;
        int y2 = this.height;

        g.fill(x1, y1, x2, y2, 0xAA000000);
        int pad = 8;
        int cx = x1 + pad;
        int cy = y1 + pad;
        g.drawString(this.font, Component.literal("Editor"), cx, cy, 0xFFFFFF, false);
        cy += 12;
        cy += 4;
        g.drawString(this.font, Component.literal("B: New Button"), cx, cy, 0xAAAAAA, false);
        cy += 10;
        g.drawString(this.font, Component.literal("T: New Text"), cx, cy, 0xAAAAAA, false);
        cy += 10;
        g.drawString(this.font, Component.literal("I: New Image"), cx, cy, 0xAAAAAA, false);
        cy += 10;
        g.drawString(this.font, Component.literal("DEL: Remove"), cx, cy, 0xAAAAAA, false);
        cy += 10;
        g.drawString(this.font, Component.literal("Drag: Move  Corner: Resize"), cx, cy, 0xAAAAAA, false);

        int vx = viewX(), vy = viewY(), vw = viewW(), vh = viewH();

        g.enableScissor(vx, vy, vx + vw, vy + vh);

        for (var row : propertyRows) {
            int labelY = row.logicalY - propScrollY;
            int boxY = labelY + 12;

            if (labelY > vy - 12 && labelY < vy + vh) {
                g.drawString(this.font, row.label, vx, labelY, 0xFFFFFF, false);
            }

            row.box.setX(vx);
            row.box.setY(boxY);
            row.box.visible = (boxY + row.box.getHeight() > vy) && (boxY < vy + vh);
        }

        g.disableScissor();

        int tx = trackX(), tw = trackW();
        g.fill(tx, vy, tx + tw, vy + vh, 0x33000000);

        int maxScroll = Math.max(0, propContentHeight - vh);
        if (maxScroll > 0) {
            int thumbH = Math.max(16, (int) ((vh / (float) propContentHeight) * vh));
            int thumbY = vy + (int) ((propScrollY / (float) maxScroll) * (vh - thumbH));
            g.fill(tx, thumbY, tx + tw, thumbY + thumbH, 0x77FFFFFF);
        }
    }

    private void buildPropertiesForSelection() {
        for (var row : propertyRows) removeWidget(row.box);
        propertyRows.clear();
        if (alignCycleBtn != null) {
            removeWidget(alignCycleBtn);
            alignCycleBtn = null;
        }

        if (selected == null || !sidebarOpen) return;

        int x = this.width - SIDEBAR_W + 8;
        int w = SIDEBAR_W - 16;
        var ref = new Object() {
            int y = SIDEBAR_LAYERS_TOP + 50;
        };
        final int LABEL_H = 10;
        final int BOX_H = 18;
        final int GAP = 6;

        java.util.function.BiFunction<String, EditBox, Integer> addRow = (label, box) -> {
            box.setWidth(w);
            box.setHeight(BOX_H);
            this.addRenderableWidget(box);
            propertyRows.add(new PropertyRow(label, box, ref.y, LABEL_H + 2 + BOX_H + GAP));
            return ref.y + LABEL_H + 2 + BOX_H + GAP;
        };

        ref.y = addRow.apply("X", makeNumberBox(w, selected.x(), v -> selected.setPos(v, selected.y())));
        ref.y = addRow.apply("Y", makeNumberBox(w, selected.y(), v -> selected.setPos(selected.x(), v)));
        ref.y = addRow.apply("W", makeNumberBox(w, selected.w(), v -> selected.setSize(v, selected.h())));
        ref.y = addRow.apply("H", makeNumberBox(w, selected.h(), v -> selected.setSize(selected.w(), v)));

        if (selected instanceof BtnUi b) {
            ref.y = addRow.apply("Text", makeTextBox(w, b.text, s -> {
                b.text = s;
                b.relayoutRuntime();
            }));
            ref.y = addRow.apply("Target (ns:path)", makeTextBox(w, b.target.toString(), s -> {
                try {
                    b.target = ResourceLocation.parse(s);
                } catch (Exception ignored) {
                }
            }));
        } else if (selected instanceof TxtUi t) {
            ref.y = addRow.apply("Text", makeTextBox(w, t.text, s -> {
                t.text = s;
                if (t.link != null) t.link.setDebugText(s);
            }));

            int btnY = ref.y;
            alignCycleBtn = Button.builder(Component.literal("Align: " + t.align), b -> {
                t.align = switch (t.align.toLowerCase()) {
                    case "left" -> "center";
                    case "center" -> "right";
                    default -> "left";
                };
                if (alignCycleBtn != null) alignCycleBtn.setMessage(Component.literal("Align: " + t.align));
            }).pos(x, btnY).size(w, 20).build();
            addRenderableWidget(alignCycleBtn);
            ref.y = btnY + 20 + GAP;
        } else if (selected instanceof ImgUi i) {
            ref.y = addRow.apply("Texture (ns:path)", makeTextBox(w, i.tex.toString(), s -> {
                try {
                    i.tex = ResourceLocation.parse(s);
                } catch (Exception ignored) {
                }
            }));
            ref.y = addRow.apply("U", makeNumberBox(w, i.u, v -> i.u = v));
            ref.y = addRow.apply("V", makeNumberBox(w, i.v, v -> i.v = v));
            ref.y = addRow.apply("TexW", makeNumberBox(w, i.tw, v -> i.tw = v));
            ref.y = addRow.apply("TexH", makeNumberBox(w, i.th, v -> i.th = v));
        }

        propContentHeight = ref.y - (SIDEBAR_LAYERS_TOP + 50);
        int viewH = this.height - (SIDEBAR_LAYERS_TOP + 50);
        propScrollY = Math.max(0, Math.min(propScrollY, Math.max(0, propContentHeight - viewH)));
    }

    private EditBox makeTextBox(int w, String initial, java.util.function.Consumer<String> onChange) {
        EditBox eb = new EditBox(this.font, 0, 0, w, 18, Component.literal(""));
        eb.setMaxLength(Integer.MAX_VALUE);
        eb.setValue(initial);
        eb.setCursorPosition(eb.getValue().length());
        eb.setHighlightPos(eb.getCursorPosition());
        eb.setResponder(onChange::accept);
        return eb;
    }

    private EditBox makeNumberBox(int w, int initial, java.util.function.IntConsumer onChange) {
        EditBox eb = new EditBox(this.font, 0, 0, w, 18, Component.literal(""));
        eb.setMaxLength(Integer.MAX_VALUE);
        eb.setValue(String.valueOf(initial));
        eb.setCursorPosition(eb.getValue().length());
        eb.setHighlightPos(eb.getCursorPosition());
        eb.setResponder(s -> {
            try {
                int v = Integer.parseInt(s.trim());
                onChange.accept(v);
                if (selected instanceof TxtUi t && t.link != null) {
                    t.link.setBounds(t.minX, t.minY, t.maxX, t.maxY);
                }
            } catch (NumberFormatException ignored) {
            }
        });
        return eb;
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

        if (this.burgerBtn == null) {
            int burgerSize = 16;
            this.burgerBtn = Button.builder(Component.literal("≡"), b -> toggleSidebar())
                    .pos(this.width - burgerSize - 6, 6)
                    .size(burgerSize, burgerSize)
                    .build();
            this.addRenderableWidget(this.burgerBtn);
        }

        setRuntimeButtonsActive(false);

        if (sidebarOpen) buildSidebar();
    }

    private void exitEditMode() {
        editing = false;
        inlineEditor = null;
        selected = null;
        for (var tb : textBoxes) tb.setDebugText(null);
        clearSidebarWidgets();

        setRuntimeButtonsActive(true);

        if (this.burgerBtn != null) {
            this.removeWidget(this.burgerBtn);
            this.burgerBtn = null;
        }
    }

    private void exportToClipboardAndClose() {
        String snippet = buildExportSnippet();
        Minecraft.getInstance().keyboardHandler.setClipboard(snippet);
        exitEditMode();
        onClose();
    }

    private int viewX() {
        return this.width - SIDEBAR_W + 8;
    }

    private int viewY() {
        return SIDEBAR_LAYERS_TOP + 50;
    }

    private int viewW() {
        return SIDEBAR_W - 16 - SCROLLBAR_W;
    }

    private int viewH() {
        return this.height - viewY();
    }

    private int trackX() {
        return viewX() + viewW();
    }

    private int trackW() {
        return SCROLLBAR_W;
    }

    private boolean handleSidebarScroll(double mx, double my, double scrollAmount) {
        if (!(editing && sidebarOpen)) return false;

        int vx = viewX(), vy = viewY(), vw = viewW() + trackW();
        int vh = viewH();

        if (mx >= vx && mx <= vx + vw && my >= vy && my <= vy + vh) {
            int maxScroll = Math.max(0, propContentHeight - vh);
            if (maxScroll > 0) {
                int step = (int) Math.round(scrollAmount * 20.0);
                propScrollY = Math.max(0, Math.min(maxScroll, propScrollY - step));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontalAmount, double verticalAmount) {
        if (handleSidebarScroll(mx, my, verticalAmount)) return true;
        return super.mouseScrolled(mx, my, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (!editing) return super.mouseClicked(mx, my, button);

        if (sidebarOpen) {
            int vx = viewX(), vy = viewY(), vw = viewW(), vh = viewH();
            int tx = trackX(), tw = trackW();
            if (mx >= tx && mx <= tx + tw && my >= vy && my <= vy + vh) {
                int maxScroll = Math.max(0, propContentHeight - vh);
                if (maxScroll > 0) {
                    draggingScrollbar = true;
                    dragStartMouseY = (int) my;
                    dragStartScrollY = propScrollY;
                    return true;
                }
            }
        }

        if (sidebarOpen && mx >= this.width - SIDEBAR_W) {
            return super.mouseClicked(mx, my, button);
        }
        if (burgerBtn != null && burgerBtn.isMouseOver(mx, my)) {
            return super.mouseClicked(mx, my, button);
        }

        for (UiElement e : ui) {
            if (e.onResizeHandle((int) mx, (int) my)) {
                select(e);
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
                select(e);
                dragging = true;
                resizing = false;
                dragStartX = (int) mx;
                dragStartY = (int) my;
                origX = e.x();
                origY = e.y();
                return true;
            }
        }

        select(null);
        dragging = false;
        resizing = false;
        return true;
    }

    private void select(@Nullable UiElement e) {
        if (!Objects.equals(this.selected, e)) {
            this.selected = e;
            refreshLayerButtonsEnabled();
            if (sidebarOpen) buildPropertiesForSelection();
        }
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (draggingScrollbar && sidebarOpen) {
            int viewY = SIDEBAR_LAYERS_TOP + 50;
            int viewH = this.height - viewY;
            int maxScroll = Math.max(0, propContentHeight - viewH);
            if (maxScroll > 0) {
                int thumbSpace = Math.max(1, viewH - Math.max(16, (int) ((viewH / (float) propContentHeight) * viewH)));
                int dyPix = (int) my - dragStartMouseY;
                float pct = (float) dyPix / (float) thumbSpace;
                propScrollY = Math.max(0, Math.min(maxScroll, dragStartScrollY + (int) (pct * maxScroll)));
            }
            return true;
        }
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
        draggingScrollbar = false;
        dragging = false;
        resizing = false;
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!editing) return super.keyPressed(keyCode, scanCode, modifiers);

        for (var row : propertyRows) {
            if (row.box.isFocused()) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }

        if (inlineEditor != null) {
            boolean handled = inlineEditor.keyPressed(keyCode, scanCode, modifiers) || inlineEditor.canConsumeInput();
            if (handled) return true;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_ESCAPE -> {
                exportToClipboardAndClose();
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
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
                    select(null);
                    return true;
                }
            }
            case GLFW.GLFW_KEY_B -> {
                int x = this.width / 2 - 40, y = this.height / 2 - 10;
                int ord = ui.size();
                var btn = new BtnUi(x, y, 100, 20, "Button", DocsManager.HOME_ID, ord);
                var runtime = Button.builder(Component.literal(btn.text), b -> {
                            if (!editing) DocsApi.open(btn.target);
                        })
                        .pos(btn.x, btn.y).size(btn.w, btn.h).build();
                this.addRenderableWidget(runtime);
                btn.runtime = runtime;
                ui.add(btn);
                select(btn);
                return true;
            }
            case GLFW.GLFW_KEY_T -> {
                int x = 20, y = 60, w = 200, h = 70;
                int ord = ui.size();
                TextBox tb = new TextBox(x, y, x + w, y + h, "_debug.text", "left");
                tb.setDebugText("New text");
                this.textBoxes.add(tb);
                TxtUi uiTxt = new TxtUi(x, y, x + w, y + h, "New text", "left", ord);
                uiTxt.link = tb;
                this.ui.add(uiTxt);
                select(uiTxt);
                return true;
            }
            case GLFW.GLFW_KEY_I -> {
                int ord = ui.size();
                ImgUi img = new ImgUi(20, 20, 32, 32, Constant.id("textures/gui/docs/background.png"), 0, 0, 256, 256, ord);
                this.ui.add(img);
                select(img);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void bringForward() {
        if (selected == null) return;
        int i = ui.indexOf(selected);
        if (i < 0 || i == ui.size() - 1) return;
        ui.remove(i);
        ui.add(i + 1, selected);
    }

    private void pushBack() {
        if (selected == null) return;
        int i = ui.indexOf(selected);
        if (i <= 0) return;
        ui.remove(i);
        ui.add(i - 1, selected);
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

        for (int i = 0; i < ui.size(); i++) {
            sb.append(ui.get(i).exportLine(i)).append("\n");
        }
        sb.append("        .build();\n");
        return sb.toString();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @SuppressWarnings("unused")
    private void openInlineEditor(String initial, TextCommit commit) {
        if (inlineEditor != null) commitInlineEditor();
        inlineEditor = new EditBox(this.font, 20, this.height - 26, this.width - 40, 18, Component.literal("edit"));
        inlineEditor.setValue(initial);
        inlineEditor.setMaxLength(Integer.MAX_VALUE);
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

    private int findImageSpecIndex(ImgUi i) {
        for (int idx = 0; idx < images.size(); idx++) {
            ImageSpec s = images.get(idx);
            if (s.texture.equals(i.tex)
                    && s.x == i.x && s.y == i.y
                    && s.w == i.w && s.h == i.h
                    && s.u == i.u && s.v == i.v
                    && s.texW == i.tw && s.texH == i.th) {
                return idx;
            }
        }
        return -1;
    }

    private void moveImageSpec(ImgUi i, boolean toFront) {
        int idx = findImageSpecIndex(i);
        if (idx >= 0) {
            ImageSpec spec = images.remove(idx);
            if (toFront) images.add(spec);
            else images.add(0, spec);
        }
    }

    private sealed interface UiElement permits BtnUi, TxtUi, ImgUi {
        int x();

        int y();

        int w();

        int h();

        int order();

        void setOrder(int o);

        void setPos(int x, int y);

        void setSize(int w, int h);

        default boolean hit(int mx, int my) {
            return mx >= x() && mx <= x() + w() && my >= y() && my <= y() + h();
        }

        default boolean onResizeHandle(int mx, int my) {
            return mx >= x() + w() - HANDLE && mx <= x() + w() && my >= y() + h() - HANDLE && my <= y() + h();
        }

        String exportLine(int orderIndex);
    }

    private interface TextCommit {
        void accept(String s);
    }

    private static final class PropertyRow {
        String label;
        EditBox box;
        int logicalY;
        int rowHeight;

        PropertyRow(String label, EditBox box, int logicalY, int rowHeight) {
            this.label = label;
            this.box = box;
            this.logicalY = logicalY;
            this.rowHeight = rowHeight;
        }
    }

    private record TitlePos(int x, int y) {
    }

    private static final class TextBox {
        int minX, minY, maxX, maxY;
        String textKey;
        String align;
        @org.jetbrains.annotations.Nullable String debugOverrideText = null;
        private int cachedWidth = -1;
        private java.util.List<net.minecraft.util.FormattedCharSequence> cachedLines = java.util.List.of();

        TextBox(int minX, int minY, int maxX, int maxY, String textKey, @org.jetbrains.annotations.Nullable String align) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.textKey = textKey;
            this.align = align == null ? "left" : align;
        }

        void setBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            invalidateWrap();
        }

        void setDebugText(@org.jetbrains.annotations.Nullable String s) {
            if (!Objects.equals(this.debugOverrideText, s)) {
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
        int x, y, w, h;
        int u, v, texW, texH;

        ImageSpec(ResourceLocation texture, int x, int y, int w, int h, int u, int v, int texW, int texH) {
            this.texture = texture;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.u = u;
            this.v = v;
            this.texW = texW;
            this.texH = texH;
        }
    }

    private static final class TxtUi implements UiElement {
        int minX, minY, maxX, maxY;
        int order;
        String text;
        String align;
        @Nullable TextBox link;

        TxtUi(int minX, int minY, int maxX, int maxY, String text, String align, int order) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.text = text;
            this.align = (align == null ? "left" : align);
            this.order = order;
        }

        @Override
        public int x() {
            return minX;
        }

        @Override
        public int y() {
            return minY;
        }

        @Override
        public int w() {
            return maxX - minX;
        }

        @Override
        public int h() {
            return maxY - minY;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public void setOrder(int o) {
            this.order = o;
        }

        @Override
        public void setPos(int nx, int ny) {
            int w = maxX - minX, h = maxY - minY;
            minX = nx;
            minY = ny;
            maxX = minX + w;
            maxY = minY + h;
            if (link != null) link.setBounds(minX, minY, maxX, maxY);
        }

        @Override
        public void setSize(int nw, int nh) {
            maxX = minX + Math.max(10, nw);
            maxY = minY + Math.max(10, nh);
            if (link != null) link.setBounds(minX, minY, maxX, maxY);
        }

        @Override
        public String exportLine(int orderIndex) {
            return "        .addTextBox(" + minX + ", " + minY + ", " + maxX + ", " + maxY + ", "
                    + escape(text) + ", " + (align == null ? "null" : escape(align)) + ", " + orderIndex + ")";
        }
    }

    private static final class ImgUi implements UiElement {
        int x, y, w, h, u, v, tw, th;
        int order;
        ResourceLocation tex;

        ImgUi(int x, int y, int w, int h, ResourceLocation tex, int u, int v, int tw, int th, int order) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.tex = tex;
            this.u = u;
            this.v = v;
            this.tw = tw;
            this.th = th;
            this.order = order;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public int w() {
            return w;
        }

        @Override
        public int h() {
            return h;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public void setOrder(int o) {
            this.order = o;
        }

        @Override
        public void setPos(int nx, int ny) {
            x = nx;
            y = ny;
        }

        @Override
        public void setSize(int nw, int nh) {
            w = Math.max(10, nw);
            h = Math.max(10, nh);
        }

        @Override
        public String exportLine(int orderIndex) {
            return "        .addImage(" + x + ", " + y + ", " + w + ", " + h + ", "
                    + "new ResourceLocation(\"" + tex + "\"), " + u + ", " + v + ", " + tw + ", " + th + ", " + orderIndex + ")";
        }
    }

    private final class BtnUi implements UiElement {
        int x, y, w, h;
        int order;
        String text;
        ResourceLocation target;
        Button runtime;

        BtnUi(int x, int y, int w, int h, String text, ResourceLocation target, int order) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.text = text;
            this.target = target;
            this.order = order;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }

        @Override
        public int w() {
            return w;
        }

        @Override
        public int h() {
            return h;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public void setOrder(int o) {
            this.order = o;
        }

        @Override
        public void setPos(int nx, int ny) {
            x = nx;
            y = ny;
            relayoutRuntime();
        }

        @Override
        public void setSize(int nw, int nh) {
            w = Math.max(10, nw);
            h = Math.max(10, nh);
            relayoutRuntime();
        }

        void relayoutRuntime() {
            if (runtime != null) {
                runtime.setX(x);
                runtime.setY(y);
                runtime.setWidth(w);
                runtime.setHeight(h);
                runtime.setMessage(Component.literal(text));
            }
        }

        @Override
        public String exportLine(int orderIndex) {
            return "        .addRedirectButton(" + x + ", " + y + ", " + w + ", " + h + ", "
                    + escape(text) + ", Constant.id(\"" + target + "\"), " + orderIndex + ")";
        }
    }
}