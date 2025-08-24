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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.client.network.CapeClientNet;
import dev.galacticraft.mod.misc.cape.*;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class CapeRootScreen extends OptionsSubScreen {
    private ClientCapePrefs prefs;
    private String selectedCapeId;
    private final List<CapeThumb> capeThumbs = new ArrayList<>();

    private static final int CAPE_SRC_U = 1;
    private static final int CAPE_SRC_V = 1;
    private static final int CAPE_SRC_W = 10;
    private static final int CAPE_SRC_H = 16;
    private static final int TEX_W = 64;
    private static final int TEX_H = 32;

    public CapeRootScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable(Translations.Ui.CAPES_TITLE));
    }

    @Override
    protected void addOptions() {
        this.prefs = ClientCapePrefs.load();
        if (this.selectedCapeId == null) this.selectedCapeId = this.prefs.gcCapeId;

        List<AbstractWidget> widgets = new ArrayList<>();

        CycleButton<CapeMode> modeBtn = CycleButton.<CapeMode>builder(v ->
                        Component.translatable(Translations.Ui.CAPES_STATE + v.name().toLowerCase(Locale.ROOT)))
                .withValues(CapeMode.OFF, CapeMode.VANILLA, CapeMode.GC)
                .withInitialValue(prefs.mode)
                .create(Component.translatable(Translations.Ui.CAPE_BUTTON), (btn, value) -> {
                    prefs.mode = value;
                    prefs.save();
                    CapeClientNet.sendSelectionIfOnline(value, value == CapeMode.GC ? prefs.gcCapeId : null);
                    Minecraft.getInstance().setScreen(new CapeRootScreen(this.lastScreen));
                });
        widgets.add(modeBtn);
        this.list.addSmall(widgets);

        if (prefs.mode == CapeMode.GC) {
            widgets.clear();
            capeThumbs.clear();

            CapeRole role = CapesClientRole.getClientRole();

            if (!isCapeAllowedForRole(this.selectedCapeId, role)) {
                String fallback = firstAllowedCapeId(role);
                if (fallback != null) {
                    this.selectedCapeId = fallback;
                    this.prefs.gcCapeId = fallback;
                    this.prefs.save();
                }
            }

            for (CapeRegistry.CapeDef def : CapeRegistry.all()) {
                if (!role.atLeast(def.minRole)) continue;

                CapeThumb thumb = new CapeThumb(def.texture, def.id, def.id.equals(this.selectedCapeId), () -> {
                    this.selectedCapeId = def.id;
                    this.prefs.gcCapeId = def.id;
                    this.prefs.mode = CapeMode.GC;
                    this.prefs.save();
                    CapeClientNet.sendSelectionIfOnline(CapeMode.GC, def.id);
                    for (CapeThumb t : capeThumbs) t.setSelected(t.id.equals(def.id));
                });
                capeThumbs.add(thumb);
                widgets.add(thumb);

                if (widgets.size() == 2) {
                    this.list.addSmall(new ArrayList<>(widgets));
                    widgets.clear();
                }
            }
            if (!widgets.isEmpty()) {
                this.list.addSmall(new ArrayList<>(widgets));
                widgets.clear();
            }
        }
    }

    private static class CapeThumb extends AbstractWidget {
        private final ResourceLocation texture;
        final String id;
        private boolean selected;
        private final Runnable onClick;

        public CapeThumb(ResourceLocation texture, String id, boolean selected, Runnable onClick) {
            super(0, 0, 150, 24, Component.translatable(Translations.Ui.CAPE + id));
            this.texture = texture;
            this.id = id;
            this.selected = selected;
            this.onClick = onClick;
        }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float delta) {
            int x0 = this.getX();
            int y0 = this.getY();
            int w = this.getWidth();
            int h = this.getHeight();

            int bg = this.isHovered() ? 0x50FFFFFF : 0x25FFFFFF;
            g.fill(x0, y0, x0 + w, y0 + h, bg);

            int pad = (h - CAPE_SRC_H) / 2;
            int imgX = x0 + pad;
            int imgY = y0 + pad;

            g.blit(this.texture, imgX, imgY,
                    CAPE_SRC_W, CAPE_SRC_H,
                    CAPE_SRC_U, CAPE_SRC_V,
                    CAPE_SRC_W, CAPE_SRC_H,
                    TEX_W, TEX_H);

            var font = Minecraft.getInstance().font;
            int textX = imgX + CAPE_SRC_W + pad;
            int textY = y0 + (h - 8) / 2;
            g.drawString(font, this.getMessage(), textX, textY, 0xFFEEEEEE, false);

            if (selected) {
                int c = 0xFF00FF00;
                g.fill(x0, y0, x0 + w, y0 + 1, c);
                g.fill(x0, y0 + h - 1, x0 + w, y0 + h, c);
                g.fill(x0, y0, x0 + 1, y0 + h, c);
                g.fill(x0 + w - 1, y0, x0 + w, y0 + h, c);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onClick.run();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
        public void setSelected(boolean sel) { this.selected = sel; }
    }

    private static boolean isCapeAllowedForRole(String id, CapeRole role) {
        if (id == null) return false;
        var def = CapeRegistry.get(id);
        return def != null && role.atLeast(def.minRole);
    }

    private static String firstAllowedCapeId(CapeRole role) {
        for (CapeRegistry.CapeDef def : CapeRegistry.allowedFor(role)) return def.id;
        return null;
    }
}