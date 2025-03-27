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

package dev.galacticraft.mod.content;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ClientCannedFoodTooltip implements ClientTooltipComponent {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/background");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final NonNullList<ItemStack> contents;

    public ClientCannedFoodTooltip(CannedFoodTooltip cannedFoodTooltip) {
        this.contents = cannedFoodTooltip.getItems();
    }

    public int getHeight() {
        return this.backgroundHeight() + 4;
    }

    public int getWidth(Font textRenderer) {
        return this.backgroundWidth();
    }

    private int backgroundWidth() {
        return this.gridSizeX() * 18 + 2;
    }

    private int backgroundHeight() {
        return this.gridSizeY() * 20 + 2;
    }

    public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        boolean bl = true;
        int k = 0;

        for (int l = 0; l < j; ++l) {
            for (int m = 0; m < i; ++m) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.renderSlot(n, o, k++, bl, context, textRenderer);
            }
        }

    }

    private void renderSlot(int x, int y, int index, boolean shouldBlock, GuiGraphics context, Font textRenderer) {
        if (!(index >= this.contents.size())) {
            ItemStack itemStack = this.contents.get(index);
            this.blit(context, x, y, Texture.SLOT);
            context.renderItem(itemStack, x + 1, y + 1, index);
            context.renderItemDecorations(textRenderer, itemStack, x + 1, y + 1);
        }
    }

    private void blit(GuiGraphics context, int x, int y, Texture sprite) {
        context.blitSprite(sprite.sprite, x, y, 0, sprite.w, sprite.h);
    }

    private int gridSizeX() {
        return Math.max(2, (int) Math.ceil(Math.sqrt((double) this.contents.size() + 1.0)));
    }

    private int gridSizeY() {
        return (int) Math.ceil(((double) this.contents.size() + 1.0) / (double) this.gridSizeX());
    }

    @Environment(EnvType.CLIENT)
    private static enum Texture {
        BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
        SLOT(ResourceLocation.withDefaultNamespace("container/bundle/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        private Texture(final ResourceLocation resourceLocation, final int j, final int k) {
            this.sprite = resourceLocation;
            this.w = j;
            this.h = k;
        }

    }
}
