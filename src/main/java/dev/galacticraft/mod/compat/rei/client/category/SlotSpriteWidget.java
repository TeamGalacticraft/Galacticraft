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

package dev.galacticraft.mod.compat.rei.client.category;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SlotSpriteWidget extends Slot {
    private final Point startPoint;
    private final Slot target;
    private final ResourceLocation sprite;

    public SlotSpriteWidget(Point startPoint, ResourceLocation sprite) {
        this.startPoint = startPoint;
        this.target = Widgets.createSlot(startPoint).disableBackground();
        this.sprite = sprite;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.target.getCurrentEntry().isEmpty()) {
            graphics.blit(this.sprite, this.startPoint.x, this.startPoint.y, 0, 0.0F, 0.0F, 16, 16, 16, 16);
        }
        this.target.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {

    }

    public void setNoticeMark(byte mark) {
        this.target.setNoticeMark(mark);
    }

    public byte getNoticeMark() {
        return this.target.getNoticeMark();
    }

    public void setInteractable(boolean interactable) {
        this.target.setInteractable(interactable);
    }

    public boolean isInteractable() {
        return this.target.isInteractable();
    }

    public void setInteractableFavorites(boolean interactableFavorites) {
        this.target.setInteractableFavorites(interactableFavorites);
    }
    
    public boolean isInteractableFavorites() {
        return this.target.isInteractableFavorites();
    }

    public void setHighlightEnabled(boolean highlights) {
        this.target.setHighlightEnabled(highlights);
    }

    public boolean isHighlightEnabled() {
        return this.target.isHighlightEnabled();
    }

    public void setTooltipsEnabled(boolean tooltipsEnabled) {
        this.target.setTooltipsEnabled(tooltipsEnabled);
    }

    public boolean isTooltipsEnabled() {
        return this.target.isTooltipsEnabled();
    }

    public void setBackgroundEnabled(boolean backgroundEnabled) {
        this.target.setBackgroundEnabled(backgroundEnabled);
    }

    public boolean isBackgroundEnabled() {
        return this.target.isBackgroundEnabled();
    }

    public Slot clearEntries() {
        return this.target.clearEntries();
    }

    public Slot entry(EntryStack<?> stack) {
        return this.target.entry(stack);
    }

    public Slot entries(Collection<? extends EntryStack<?>> stacks) {
        return this.target.entries(stacks);
    }

    @Override
    public EntryStack<?> getCurrentEntry() {
        return this.target.getCurrentEntry();
    }

    @Override
    public List<EntryStack<?>> getEntries() {
        return this.target.getEntries();
    }

    @Override
    public Rectangle getBounds() {
        return this.target.getBounds();
    }

    @Override
    public Rectangle getInnerBounds() {
        return this.target.getInnerBounds();
    }
}