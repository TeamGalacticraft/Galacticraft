/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.api.biome;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BiomePropertyType<T> {
    private final T defaultValue;
    private final Text name;

    private BiomePropertyType(T defaultValue, Text name) {
        this.defaultValue = defaultValue;
        this.name = name;
    }

    public BiomeProperty<T> create() {
        return new BiomeProperty<>(this);
    }

    public BiomeProperty<T> create(T value) {
        return new BiomeProperty<>(this, value);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Text getName() {
        return name;
    }

    public static class Builder<T> {
        private T defaultValue = null;
        private Text name = Constants.Misc.EMPTY_TEXT;

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> name(Text name) {
            this.name = name;
            return this;
        }

        public BiomePropertyType<T> build() {
            return new BiomePropertyType<>(defaultValue, name);
        }
    }
}
