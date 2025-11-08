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
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.util.Translations.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class GalacticraftEmiPlugin implements EmiPlugin {
    private static final ResourceLocation SIMPLIFIED_ICONS = Constant.id("textures/gui/simplified_icons_emi.png");

    // Workstations
    public static final EmiStack CIRCUIT_FABRICATOR = EmiStack.of(GCBlocks.CIRCUIT_FABRICATOR);
    public static final EmiStack COMPRESSOR = EmiStack.of(GCBlocks.COMPRESSOR);
    public static final EmiStack ELECTRIC_COMPRESSOR = EmiStack.of(GCBlocks.ELECTRIC_COMPRESSOR);
    public static final EmiStack ELECTRIC_FURNACE = EmiStack.of(GCBlocks.ELECTRIC_FURNACE);
    public static final EmiStack ELECTRIC_ARC_FURNACE = EmiStack.of(GCBlocks.ELECTRIC_ARC_FURNACE);
    public static final EmiStack ROCKET_WORKBENCH = EmiStack.of(GCBlocks.ROCKET_WORKBENCH);

    // Simplified Icons
    public static final EmiRenderable FABRICATION_ICON = new EmiTexture(SIMPLIFIED_ICONS, 16, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable COMPRESSING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 0, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ELECTRIC_COMPRESSING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 0, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ELECTRIC_SMELTING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 0, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ELECTRIC_BLASTING_ICON = new EmiTexture(SIMPLIFIED_ICONS, 0, 0, 16, 16, 16, 16, 64, 64);
    public static final EmiRenderable ROCKET_ICON = new EmiTexture(SIMPLIFIED_ICONS, 32, 0, 16, 16, 16, 16, 64, 64);

    // Categories
    public static final EmiRecipeCategory FABRICATION = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.FABRICATION), CIRCUIT_FABRICATOR, FABRICATION_ICON);
    public static final EmiRecipeCategory COMPRESSING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.COMPRESSING), COMPRESSOR, COMPRESSING_ICON);
    public static final EmiRecipeCategory ELECTRIC_COMPRESSING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ELECTRIC_COMPRESSING), ELECTRIC_COMPRESSOR, ELECTRIC_COMPRESSING_ICON, RecipeCategory.ELECTRIC_COMPRESSOR);
    public static final EmiRecipeCategory ELECTRIC_SMELTING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ELECTRIC_SMELTING), ELECTRIC_FURNACE, ELECTRIC_SMELTING_ICON, RecipeCategory.ELECTRIC_FURNACE);
    public static final EmiRecipeCategory ELECTRIC_BLASTING = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ELECTRIC_BLASTING), ELECTRIC_ARC_FURNACE, ELECTRIC_BLASTING_ICON, RecipeCategory.ELECTRIC_ARC_FURNACE);
    public static final EmiRecipeCategory ROCKET = new GalacticraftEmiRecipeCategory(Constant.id(Constant.Recipe.ROCKET), ROCKET_WORKBENCH, ROCKET_ICON);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FABRICATION);
        registry.addCategory(COMPRESSING);
        registry.addCategory(ELECTRIC_COMPRESSING);
        registry.addCategory(ELECTRIC_SMELTING);
        registry.addCategory(ELECTRIC_BLASTING);
        registry.addCategory(ROCKET);

        registry.addWorkstation(FABRICATION, CIRCUIT_FABRICATOR);
        registry.addWorkstation(COMPRESSING, COMPRESSOR);
        registry.addWorkstation(ELECTRIC_COMPRESSING, ELECTRIC_COMPRESSOR);
        registry.addWorkstation(ELECTRIC_SMELTING, ELECTRIC_FURNACE);
        registry.addWorkstation(ELECTRIC_BLASTING, ELECTRIC_ARC_FURNACE);
        registry.addWorkstation(ROCKET, ROCKET_WORKBENCH);

        RecipeManager manager = registry.getRecipeManager();

        for (RecipeHolder<FabricationRecipe> recipe : manager.getAllRecipesFor(GCRecipes.FABRICATION_TYPE)) {
            registry.addRecipe(new FabricationEmiRecipe(recipe));
        }

        for (RecipeHolder<CompressingRecipe> recipe : manager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE)) {
            registry.addRecipe(new CompressingEmiRecipe(recipe));
            registry.addRecipe(new ElectricCompressingEmiRecipe(recipe));
        }

        for (RecipeHolder<SmeltingRecipe> recipe : manager.getAllRecipesFor(RecipeType.SMELTING)) {
            registry.addRecipe(new ElectricSmeltingEmiRecipe(recipe));
        }

        for (RecipeHolder<BlastingRecipe> recipe : manager.getAllRecipesFor(RecipeType.BLASTING)) {
            registry.addRecipe(new ElectricBlastingEmiRecipe(recipe));
        }

        for (RecipeHolder<RocketRecipe> recipe : manager.getAllRecipesFor(GCRecipes.ROCKET_TYPE)) {
            registry.addRecipe(new RocketEmiRecipe(recipe));
        }
    }
}