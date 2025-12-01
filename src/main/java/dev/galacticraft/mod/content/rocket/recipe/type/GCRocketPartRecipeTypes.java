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

package dev.galacticraft.mod.content.rocket.recipe.type;

import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import dev.galacticraft.mod.Constant.RocketPartRecipeTypes;
import dev.galacticraft.mod.content.GCRegistry;

public class GCRocketPartRecipeTypes {
    public static final GCRegistry<RocketPartRecipeType<?>> RECIPE_TYPES = new GCRegistry<>(BuiltInRocketRegistries.ROCKET_PART_RECIPE_TYPE);

    public static final PatternedRocketPartRecipeType WRAP_PATTERNED = RECIPE_TYPES.register(RocketPartRecipeTypes.WRAP_PATTERNED, new PatternedRocketPartRecipeType());
    public static final CenteredPatternedRocketPartRecipeType CENTERED_PATTERNED = RECIPE_TYPES.register(RocketPartRecipeTypes.CENTERED_PATTERNED, new CenteredPatternedRocketPartRecipeType());

    public static void register() {}
}
