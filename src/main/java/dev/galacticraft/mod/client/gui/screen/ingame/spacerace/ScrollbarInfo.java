/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.screen.ingame.spacerace;

import net.minecraft.util.Mth;

public record ScrollbarInfo(
        ScrollbarType type,
        ScrollbarAxis axis,
        int x,
        int y,
        int length,
        int totalEntries,
        int visibleEntries,
        int scroll
) {
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_MIN_THUMB_HEIGHT = 14;

    public int maxScroll() {
        return Math.max(0, this.totalEntries - this.visibleEntries);
    }

    public boolean isInteractive() {
        return this.maxScroll() > 0 && this.length > 0;
    }

    public int thumbLength() {
        if (!this.isInteractive()) {
            return Math.max(1, this.length);
        }
        int calculated = (int) Math.round((this.visibleEntries / (double) this.totalEntries) * this.length);
        return Mth.clamp(calculated, SCROLLBAR_MIN_THUMB_HEIGHT, this.length);
    }

    public int thumbPosition() {
        int maxScroll = this.maxScroll();
        if (maxScroll <= 0) {
            return this.trackStart();
        }
        int thumbLength = this.thumbLength();
        int travelRange = Math.max(0, this.length - thumbLength);
        return this.trackStart() + (int) Math.round((this.scroll / (double) maxScroll) * travelRange);
    }

    public int trackStart() {
        return this.axis == ScrollbarAxis.VERTICAL ? this.y : this.x;
    }

    public boolean contains(double mouseX, double mouseY) {
        if (this.axis == ScrollbarAxis.VERTICAL) {
            return mouseX >= this.x && mouseX < this.x + SCROLLBAR_WIDTH && mouseY >= this.y && mouseY < this.y + this.length;
        }
        return mouseX >= this.x && mouseX < this.x + this.length && mouseY >= this.y && mouseY < this.y + SCROLLBAR_WIDTH;
    }
}
