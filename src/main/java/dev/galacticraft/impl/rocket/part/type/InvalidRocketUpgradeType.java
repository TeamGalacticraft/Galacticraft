/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.impl.rocket.part.type;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.type.RocketUpgradeType;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.impl.rocket.part.config.DefaultRocketUpgradeConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.DefaultTravelPredicateType;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public final class InvalidRocketUpgradeType extends RocketUpgradeType<DefaultRocketUpgradeConfig> {
    public static final InvalidRocketUpgradeType INSTANCE = new InvalidRocketUpgradeType(DefaultRocketUpgradeConfig.CODEC);

    private InvalidRocketUpgradeType(@NotNull Codec<DefaultRocketUpgradeConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public void tick(@NotNull Rocket rocket, @NotNull DefaultRocketUpgradeConfig config) {
    }

    @Override
    public @NotNull Ingredient upgradeRecipe(@NotNull DefaultRocketUpgradeConfig config) {
        return Ingredient.EMPTY;
    }

    @Override
    public @NotNull ConfiguredTravelPredicate<?, ?> travelPredicate(@NotNull DefaultRocketUpgradeConfig config) {
        return DefaultTravelPredicateType.CONFIGURED;
    }
}
