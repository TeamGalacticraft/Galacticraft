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

package dev.galacticraft.mod.client.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ColorSlider extends AbstractSliderButton {
    private final Consumer<Integer> consumer;
    private final Component colorName;

    public ColorSlider(int x, int y, int width, int height, Component colorName, int value, Consumer<Integer> consumer) {
        super(x, y, width, height, Component.translatable("options.percent_value", colorName, (int) (value / 255.0 * 100.0)), value / 255.0);
        this.consumer = consumer;
        this.colorName = colorName;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.translatable("options.percent_value", this.colorName, (int) (this.value * 100.0)));
    }

    @Override
    protected void applyValue() {
        this.consumer.accept((int) (this.value * 255.0));
    }
}
