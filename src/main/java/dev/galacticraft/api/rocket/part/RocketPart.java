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

package dev.galacticraft.api.rocket.part;

import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.config.RocketPartConfig;
import dev.galacticraft.api.rocket.part.type.RocketPartType;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface RocketPart<C extends RocketPartConfig, T extends RocketPartType<C>> permits RocketBody, RocketBooster, RocketEngine, RocketCone, RocketFin, RocketUpgrade {
    static <C extends RocketPartConfig, T extends RocketPartType<? extends C>, P extends RocketPart<? extends C, ? extends T>> Component getName(ResourceKey<? extends P> key) {
        return Component.translatable(getKey(key));
    }

    static <C extends RocketPartConfig, T extends RocketPartType<? extends C>, P extends RocketPart<? extends C, ? extends T>> @NotNull String getKey(ResourceKey<? extends P> key) {
        return key.registry().getPath() + '.' + key.location().getNamespace() + '.' + key.location().getPath();
    }

    @NotNull C config();

    @NotNull T type();

    default @Nullable RocketPartRecipe<?, ?> getRecipe() {
        return this.type().getRecipe(this.config());
    }

    /**
     * Called every tick when this part is applied to a placed rocket.
     * The rocket may not have launched yet.
     *
     * @param rocket the rocket that this part is a part of.
     */
    default void tick(@NotNull Rocket rocket) {
        this.type().tick(rocket, this.config());
    }

    default @NotNull ConfiguredTravelPredicate<?, ?> travelPredicate() {
        return this.type().travelPredicate(this.config());
    }
}
