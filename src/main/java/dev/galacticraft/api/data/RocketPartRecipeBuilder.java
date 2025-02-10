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

package dev.galacticraft.api.data;

import com.google.common.collect.ImmutableList;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.impl.rocket.recipe.config.CenteredPatternedRocketPartRecipeConfig;
import dev.galacticraft.impl.rocket.recipe.config.PatternedRocketPartRecipeConfig;
import dev.galacticraft.impl.rocket.recipe.type.CenteredPatternedRocketPartRecipeType;
import dev.galacticraft.impl.rocket.recipe.type.PatternedRocketPartRecipeType;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class RocketPartRecipeBuilder {
    private final Char2ObjectMap<Ingredient> ingredients = new Char2ObjectArrayMap<>();
    private final Char2IntMap spacing = new Char2IntArrayMap();

    private final List<String> right = new ArrayList<>();
    private final List<String> left = new ArrayList<>();
    private final List<String> center = new ArrayList<>();

    public RocketPartRecipeBuilder() {
        this.spacing.put(' ', 18);
        this.spacing.put('.', 9);
    }

    public static RocketPartRecipeBuilder create() {
        return new RocketPartRecipeBuilder();
    }

    public RocketPartRecipeBuilder define(char c, Ingredient ingredient) {
        assert !this.ingredients.containsKey(c) && this.spacing.containsKey(c);
        this.ingredients.put(c, ingredient);
        return this;
    }

    public RocketPartRecipeBuilder define(char c, int spacing) {
        assert !this.ingredients.containsKey(c) && this.spacing.containsKey(c);
        this.spacing.put(c, spacing);
        return this;
    }

    public RocketPartRecipeBuilder right(String pattern) {
        assert this.center.isEmpty();
        this.right.add(pattern);
        return this;
    }

    public RocketPartRecipeBuilder left(String pattern) {
        assert this.center.isEmpty();
        this.left.add(pattern);
        return this;
    }

    @ApiStatus.Experimental
    public RocketPartRecipeBuilder center(String pattern) {
        assert this.left.isEmpty() && this.right.isEmpty();
        this.center.add(pattern);
        return this;
    }


    public RocketPartRecipe<?, ?> build() {
        if (!this.left.isEmpty() || !this.right.isEmpty()) {
            return PatternedRocketPartRecipeType.INSTANCE.configure(PatternedRocketPartRecipeConfig.parse(this.spacing, this.ingredients, this.left, this.right));
        } else if (!this.center.isEmpty()) {
            ImmutableList.Builder<RocketPartRecipeSlot> left = ImmutableList.builder();
            int[] widths = new int[this.center.size()];
            for (int i = 0; i < this.center.size(); i++) {
                String s = this.center.get(i);
                int x = 0;
                for (char c : s.toCharArray()) {
                    if (spacing.containsKey(c)) {
                        x += spacing.get(c);
                    } else {
                        assert ingredients.containsKey(c);
                        x += 18;
                    }
                }
                widths[i] = x;
            }

            int y = 0;
            List<String> strings = this.center;
            for (int i = 0; i < strings.size(); i++) {
                int x = 0;
                for (char c : strings.get(i).toCharArray()) {
                    if (spacing.containsKey(c)) {
                        x += spacing.get(c);
                    } else {
                        left.add(RocketPartRecipeSlot.create(-(widths[i] / 2) + x, y, ingredients.get(c)));
                        x += 18;
                    }
                }
                y += 18;
            }

            ImmutableList<RocketPartRecipeSlot> leftB = left.build();
            NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(leftB.size());
            for (RocketPartRecipeSlot slot : leftB) {
                ingredients.add(slot.ingredient());
            }
            return CenteredPatternedRocketPartRecipeType.INSTANCE.configure(new CenteredPatternedRocketPartRecipeConfig(y, leftB, ingredients));
        } else {
            throw new RuntimeException();
        }
    }
}
