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

package dev.galacticraft.mod.api.grid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * A kind of grid, such as energy or fluid. The id is saved with every grid.
 *
 * @param id      the id of this grid type
 * @param factory creates an empty grid of this type
 */
public record GridType<G extends Grid<G, ?>>(ResourceLocation id, BiFunction<UUID, ServerLevel, G> factory) {
    private static final Map<ResourceLocation, GridType<?>> TYPES = new HashMap<>();

    public static <G extends Grid<G, ?>> GridType<G> register(ResourceLocation id, BiFunction<UUID, ServerLevel, G> factory) {
        GridType<G> type = new GridType<>(id, factory);
        if (TYPES.putIfAbsent(id, type) != null) {
            throw new IllegalStateException("Duplicate grid type " + id);
        }
        return type;
    }

    public static @Nullable GridType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    public G create(UUID id, ServerLevel level) {
        return this.factory.apply(id, level);
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
