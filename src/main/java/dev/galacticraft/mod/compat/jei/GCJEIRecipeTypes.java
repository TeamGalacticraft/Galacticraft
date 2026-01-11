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

package dev.galacticraft.mod.compat.jei;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.RocketRecipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public interface GCJEIRecipeTypes {
    RecipeType<FabricationRecipe> FABRICATION = RecipeType.create(Constant.MOD_ID, Constant.Recipe.FABRICATION, FabricationRecipe.class);
    RecipeType<CompressingRecipe> COMPRESSING = RecipeType.create(Constant.MOD_ID, Constant.Recipe.COMPRESSING, CompressingRecipe.class);
    RecipeType<CompressingRecipe> ELECTRIC_COMPRESSING = RecipeType.create(Constant.MOD_ID, Constant.Recipe.ELECTRIC_COMPRESSING, CompressingRecipe.class);
    RecipeType<SmeltingRecipe> ELECTRIC_SMELTING = RecipeType.create(Constant.MOD_ID, Constant.Recipe.ELECTRIC_SMELTING, SmeltingRecipe.class);
    RecipeType<BlastingRecipe> ELECTRIC_BLASTING = RecipeType.create(Constant.MOD_ID, Constant.Recipe.ELECTRIC_BLASTING, BlastingRecipe.class);
    RecipeType<CanningRecipe> CANNING = RecipeType.create(Constant.MOD_ID, Constant.Recipe.CANNING, CanningRecipe.class);
    RecipeType<RocketRecipe> ROCKET = RecipeType.create(Constant.MOD_ID, Constant.Recipe.ROCKET, RocketRecipe.class);
}
