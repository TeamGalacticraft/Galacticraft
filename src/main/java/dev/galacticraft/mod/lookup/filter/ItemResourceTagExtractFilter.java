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

package dev.galacticraft.mod.lookup.filter;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

public record ItemResourceTagExtractFilter<R, V extends TransferVariant<R>>(
        ItemApiLookup<Storage<V>, ContainerItemContext> lookup,
        Tag<R> tag) implements ItemFilter {

    @Override
    public boolean test(ItemStack itemStack) {
        Storage<V> storage = ContainerItemContext.withInitial(itemStack).find(this.lookup);
        if (storage != null && storage.supportsExtraction()) {
            V extractableContent = StorageUtil.findExtractableResource(storage, variant -> tag.contains(variant.getObject()), null);
            if (extractableContent != null && !extractableContent.isBlank()) {
                return storage.simulateExtract(extractableContent, Long.MAX_VALUE, null) >= 0;
            }
        }
        return false;
    }
}
