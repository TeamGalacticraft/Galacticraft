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

package dev.galacticraft.impl.rocket.recipe.type;

import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import dev.galacticraft.impl.rocket.recipe.config.PatternedRocketPartRecipeConfig;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PatternedRocketPartRecipeType extends RocketPartRecipeType<PatternedRocketPartRecipeConfig> {
    public static final RocketPartRecipeType<PatternedRocketPartRecipeConfig> INSTANCE = new PatternedRocketPartRecipeType();

    private PatternedRocketPartRecipeType() {
        super(PatternedRocketPartRecipeConfig.CODEC);
    }

    @Override
    public int width(PatternedRocketPartRecipeConfig config) {
        return config.width();
    }

    @Override
    public int height(PatternedRocketPartRecipeConfig config) {
        return config.height();
    }

    @Override
    public @NotNull List<RocketPartRecipeSlot> slots(PatternedRocketPartRecipeConfig config) {
        return config.slots();
    }

    @Override
    public @NotNull ResourceKey<? extends RocketPart<?, ?>> output(PatternedRocketPartRecipeConfig config) {
        return config.output();
    }
}
