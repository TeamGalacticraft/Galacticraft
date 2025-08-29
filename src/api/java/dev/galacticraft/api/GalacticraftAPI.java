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

package dev.galacticraft.api;

import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.part.config.*;
import dev.galacticraft.api.rocket.part.type.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface GalacticraftAPI {
    static GalacticraftAPI get() {
    }

    static void register(GalacticraftAPI api) {}

    // Rocket Recipes
    @Contract(pure = true, value = "_, _ -> new")
    @NotNull <C extends RocketBodyConfig, T extends RocketBodyType<C>> RocketBody<C, T> createRocketBody(@NotNull C config, @NotNull T type);

    @Contract(pure = true, value = "_, _ -> new")
    @NotNull <C extends RocketBoosterConfig, T extends RocketBoosterType<C>> RocketBooster<C, T> createRocketBooster(@NotNull C config, @NotNull T type);

    @Contract(pure = true, value = "_, _ -> new")
    @NotNull <C extends RocketConeConfig, T extends RocketConeType<C>> RocketCone<C, T> createRocketCone(@NotNull C config, @NotNull T type);

    @Contract(pure = true, value = "_, _ -> new")
    @NotNull <C extends RocketEngineConfig, T extends RocketEngineType<C>> RocketEngine<C, T> createRocketEngine(@NotNull C config, @NotNull T type);

    @Contract(pure = true, value = "_, _ -> new")
    @NotNull <C extends RocketFinConfig, T extends RocketFinType<C>> RocketFin<C, T> createRocketFin(@NotNull C config, @NotNull T type);
}
