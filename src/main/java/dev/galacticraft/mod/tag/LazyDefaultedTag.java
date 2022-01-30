/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.tag;

import com.google.common.base.Suppliers;
import net.fabricmc.fabric.api.tag.FabricTag;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public class LazyDefaultedTag<T> implements Tag.Identified<T>, FabricTag<T> {
    private final Tag.Identified<T> delegate;
    private final Supplier<List<T>> fallback;

    public LazyDefaultedTag(TagFactory<T> factory, Identifier id, Supplier<List<T>> fallback) {
        this.fallback = Suppliers.memoize(fallback::get);
        this.delegate = factory.create(id);
    }

    @Override
    public boolean hasBeenReplaced() {
        return !this.delegate.values().isEmpty();
    }

    @Override
    public Identifier getId() {
        return this.delegate.getId();
    }

    @Override
    public boolean contains(T entry) {
        return this.values().contains(entry);
    }

    @Override
    public List<T> values() {
        List<T> values = this.delegate.values();
        return values.isEmpty() ? this.fallback.get() : values;
    }
}