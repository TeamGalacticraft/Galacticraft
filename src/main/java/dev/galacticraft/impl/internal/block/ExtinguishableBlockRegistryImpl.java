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

package dev.galacticraft.impl.internal.block;

import dev.galacticraft.api.registry.ExtinguishableBlockRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class ExtinguishableBlockRegistryImpl implements ExtinguishableBlockRegistry {
    private static final ExtinguishableBlockRegistry.Entry REMOVED = new ExtinguishableBlockRegistry.Entry(state -> null);

    private final Map<Block, ExtinguishableBlockRegistry.Entry> registeredEntriesBlock = new IdentityHashMap<>();
    private final Map<TagKey<Block>, ExtinguishableBlockRegistry.Entry> registeredEntriesTag = new HashMap<>();
    private volatile Map<Block, ExtinguishableBlockRegistry.Entry> computedEntries = null;

    public ExtinguishableBlockRegistryImpl() {
        // Reset computed values after tags change since they depend on tags.
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
                this.computedEntries = null;
        });
    }

    private Map<Block, ExtinguishableBlockRegistry.Entry> getEntryMap() {
        Map<Block, ExtinguishableBlockRegistry.Entry> map = this.computedEntries;

        if (map == null) {
            map = new IdentityHashMap<>();

            // tags take precedence over blocks
            for (TagKey<Block> tag : this.registeredEntriesTag.keySet()) {
                ExtinguishableBlockRegistry.Entry entry = this.registeredEntriesTag.get(tag);

                for (Holder<Block> block : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
                    map.put(block.value(), entry);
                }
            }

            map.putAll(this.registeredEntriesBlock);

            this.computedEntries = map;
        }

        return map;
    }

    @Override
    public Entry get(Block block) {
        return this.getEntryMap().get(block);
    }

    @Override
    public void add(Block block, Entry value) {
        registeredEntriesBlock.put(block, value);

        computedEntries = null;
    }

    @Override
    public void add(TagKey<Block> tag, Entry value) {
        registeredEntriesTag.put(tag, value);

        computedEntries = null;
    }

    @Override
    public void remove(Block block) {
        add(block, REMOVED);
    }

    @Override
    public void remove(TagKey<Block> tag) {
        add(tag, REMOVED);
    }

    @Override
    public void clear(Block block) {
        registeredEntriesBlock.remove(block);

        computedEntries = null;
    }

    @Override
    public void clear(TagKey<Block> tag) {
        registeredEntriesTag.remove(tag);

        computedEntries = null;
    }
}
