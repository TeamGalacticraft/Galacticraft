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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> extends Registry<T> {
    @Shadow private boolean frozen;

    @Shadow @Final private Map<RegistryKey<T>, RegistryEntry.Reference<T>> keyToEntry;

    @Shadow @Nullable private Map<T, RegistryEntry.Reference<T>> unfrozenValueToEntry;

    protected SimpleRegistryMixin(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Registry<T> freeze() {
        this.frozen = true;
        List<Identifier> list = this.keyToEntry.entrySet().stream().filter((entry) -> {
            return !entry.getValue().hasKeyAndValue();
        }).map((entry) -> {
            return entry.getKey().getValue();
        }).sorted().toList();
        if (!list.isEmpty()) {
            RegistryKey var10002 = this.getKey();
            throw new IllegalStateException("Unbound values in registry " + var10002 + ": " + list);
        } else {
            if (this.unfrozenValueToEntry != null) {
                List<RegistryEntry.Reference<T>> list2 = this.unfrozenValueToEntry.values().stream().filter((entry) -> {
                    return !entry.hasKeyAndValue();
                }).toList();
                if (!list2.isEmpty()) {
//                    throw new IllegalStateException("Some intrusive holders were not added to registry: " + list2);
                }

                this.unfrozenValueToEntry = null;
            }

            return this;
        }
    }
}
