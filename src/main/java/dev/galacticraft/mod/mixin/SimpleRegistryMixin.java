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

package dev.galacticraft.mod.mixin;

import com.mojang.serialization.Lifecycle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@Mixin(MappedRegistry.class)
public abstract class SimpleRegistryMixin<T> extends Registry<T> {
    @Shadow private boolean frozen;

    @Shadow @Final private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow @Nullable private Map<T, Holder.Reference<T>> intrusiveHolderCache;

    protected SimpleRegistryMixin(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    /**
     * @author AlphaMode
     * @reason Give more debug information for intrusive holders
     */
    @Overwrite
    public Registry<T> freeze() {
        this.frozen = true;
        List<ResourceLocation> list = this.byKey.entrySet().stream().filter((entry) -> {
            return !entry.getValue().isBound();
        }).map((entry) -> {
            return entry.getKey().location();
        }).sorted().toList();
        if (!list.isEmpty()) {
            ResourceKey var10002 = this.key();
            throw new IllegalStateException("Unbound values in registry " + var10002 + ": " + list);
        } else {
            if (this.intrusiveHolderCache != null) {
                List<Holder.Reference<T>> list2 = this.intrusiveHolderCache.values().stream().filter((entry) -> {
                    return !entry.isBound();
                }).toList();
                if (!list2.isEmpty()) {
                    StringBuilder entriesToDisplay = new StringBuilder();
                    for (int i = 0; i < list2.size(); i++) {
                        entriesToDisplay.append(list2.get(i).value().getClass());
                        if (i != list2.size() - 1) {
                            entriesToDisplay.append(", ");
                        }
                    }
                    throw new IllegalStateException("Some intrusive holders were not added to registry: " + entriesToDisplay);
                }

                this.intrusiveHolderCache = null;
            }

            return this;
        }
    }
}
