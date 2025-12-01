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

package dev.galacticraft.api.registry;

import dev.galacticraft.impl.internal.registry.AcidTransformItemRegistryImpl;
import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface AcidTransformItemRegistry extends Item2ObjectMap<AcidTransformItemRegistry.Entry> {
    AcidTransformItemRegistry INSTANCE = new AcidTransformItemRegistryImpl();

    default void add(ItemLike item, ItemStack transform) {
        this.add(item.asItem(), state -> transform);
    }

    default void add(ItemLike item, Function<ItemStack, ItemStack> transform) {
        this.add(item.asItem(), new Entry(transform));
    }

    default void add(ItemLike item, ItemStack transform, Consumer<Context> callback) {
        this.add(item.asItem(), state -> transform, callback);
    }

    default void add(ItemLike item, Function<ItemStack, ItemStack> transform, Consumer<Context> callback) {
        this.add(item.asItem(), new Entry(transform, callback));
    }

    default void add(TagKey<Item> tag, ItemStack transform) {
        this.add(tag, state -> transform);
    }

    default void add(TagKey<Item> tag, Function<ItemStack, ItemStack> transform) {
        this.add(tag, new Entry(transform));
    }

    default void add(TagKey<Item> tag, ItemStack transform, Consumer<Context> callback) {
        this.add(tag, state -> transform, callback);
    }

    default void add(TagKey<Item> tag, Function<ItemStack, ItemStack> transform, Consumer<Context> callback) {
        this.add(tag, new Entry(transform, callback));
    }

    default @Nullable ItemStack transform(ItemStack original) {
        Entry entry = this.get(original.getItem());
        if (entry != null) {
            ItemStack itemStack = entry.transform(original);
            return original != itemStack ? itemStack : null;
        }
        return null;
    }

    final class Entry {
        private final Function<ItemStack, ItemStack> transform;
        private final Consumer<Context> callback;

        public Entry(Function<ItemStack, ItemStack> transform) {
            this.transform = transform;
            this.callback = context -> {};
        }

        public Entry(Function<ItemStack, ItemStack> transform, Consumer<Context> callback) {
            this.transform = transform;
            this.callback = callback;
        }

        public Function<ItemStack, ItemStack> getTransform() {
            return this.transform;
        }

        public ItemStack transform(ItemStack itemStack) {
            return this.transform.apply(itemStack);
        }

        public Consumer<Context> getCallback() {
            return this.callback;
        }

        public void callback(Context context) {
            this.callback.accept(context);
        }
    }

    public record Context(ItemEntity itemEntity, ItemStack itemStack) {}
}
