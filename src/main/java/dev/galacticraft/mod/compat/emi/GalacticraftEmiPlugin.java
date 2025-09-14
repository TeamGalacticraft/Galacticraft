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

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

public class GalacticraftEmiPlugin implements EmiPlugin {
    // Workstations
    public static final EmiStack COMPRESSOR = EmiStack.of(GCBlocks.COMPRESSOR);
    public static final EmiStack ELECTRIC_COMPRESSOR = EmiStack.of(GCBlocks.ELECTRIC_COMPRESSOR);
    public static final EmiStack CIRCUIT_FABRICATOR = EmiStack.of(GCBlocks.CIRCUIT_FABRICATOR);
    public static final EmiStack ELECTRIC_FURNACE = EmiStack.of(GCBlocks.ELECTRIC_FURNACE);
    public static final EmiStack ELECTRIC_ARC_FURNACE = EmiStack.of(GCBlocks.ELECTRIC_ARC_FURNACE);

    // Categories
    public static final EmiRecipeCategory COMPRESSING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.COMPRESSING), COMPRESSOR);
    public static final EmiRecipeCategory FABRICATION = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.FABRICATION), CIRCUIT_FABRICATOR);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(COMPRESSING);
        registry.addCategory(FABRICATION);

        registry.addWorkstation(COMPRESSING, COMPRESSOR);
        registry.addWorkstation(COMPRESSING, ELECTRIC_COMPRESSOR);
        registry.addWorkstation(FABRICATION, CIRCUIT_FABRICATOR);
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, ELECTRIC_FURNACE);
        registry.addWorkstation(VanillaEmiRecipeCategories.BLASTING, ELECTRIC_ARC_FURNACE);

        RecipeManager manager = registry.getRecipeManager();

        for (RecipeHolder<CompressingRecipe> recipe : manager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE)) {
            registry.addRecipe(new CompressingEmiRecipe(recipe));
        }

        for (RecipeHolder<FabricationRecipe> recipe : manager.getAllRecipesFor(GCRecipes.FABRICATION_TYPE)) {
            registry.addRecipe(new FabricationEmiRecipe(recipe));
        }
    }
}