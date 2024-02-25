/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.data.content;

import dev.galacticraft.impl.data.GCDynamicRegistryProvider;
import dev.galacticraft.impl.data.GeneratingBootstrapContext;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstapContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BootstrapDataProvider<T> extends GCDynamicRegistryProvider {
    private final String name;
    private final Consumer<BootstapContext<T>> consumer;

    @Contract(pure = true)
    public static <T> FabricDataGenerator.Pack.@NotNull RegistryDependentFactory<BootstrapDataProvider<T>> create(String name, Consumer<BootstapContext<T>> bootstrap) {
        return (output, registriesFuture) -> new BootstrapDataProvider<>(output, registriesFuture, name, bootstrap);
    }

    private BootstrapDataProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future, String name, Consumer<BootstapContext<T>> consumer) {
        super(output, future);
        this.name = name;
        this.consumer = consumer;
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        this.consumer.accept(new GeneratingBootstrapContext<>(registries, entries));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
