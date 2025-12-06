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

package dev.galacticraft.mod.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

import static dev.galacticraft.mod.util.Translations.RecipeCategory.PREFIX;

public class GalacticraftEmiRecipeCategory extends EmiRecipeCategory {
    private final Component name;

    public GalacticraftEmiRecipeCategory(ResourceLocation id, EmiRenderable icon) {
        this(id, icon, icon, PREFIX + id.getPath());
    }

    public GalacticraftEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified) {
        this(id, icon, simplified, PREFIX + id.getPath());
    }

    public GalacticraftEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, Comparator<EmiRecipe> sorter) {
        this(id, icon, simplified, sorter, PREFIX + id.getPath());
    }

    public GalacticraftEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, String translationKey) {
        super(id, icon, icon);
        this.name = Component.translatable(translationKey);
    }

    public GalacticraftEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, String translationKey) {
        super(id, icon, simplified);
        this.name = Component.translatable(translationKey);
    }

    public GalacticraftEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, Comparator<EmiRecipe> sorter, String translationKey) {
        super(id, icon, simplified, sorter);
        this.name = Component.translatable(translationKey);
    }

    @Override
    public Component getName() {
        return this.name;
    }
}