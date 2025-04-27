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

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.jei.category.JEICompressingCategory;
import dev.galacticraft.mod.compat.jei.category.JEIFabricationCategory;
import dev.galacticraft.mod.compat.jei.category.JEIRocketCategory;
import dev.galacticraft.mod.compat.jei.replacers.EmergencyKitRecipeMaker;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.recipe.EmergencyKitRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@JeiPlugin
public class GCJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Constant.id("jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.CIRCUIT_FABRICATOR), GCJEIRecipeTypes.FABRICATION);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.COMPRESSOR), GCJEIRecipeTypes.COMPRESSING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_COMPRESSOR), GCJEIRecipeTypes.COMPRESSING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ROCKET_WORKBENCH), GCJEIRecipeTypes.ROCKET);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_ARC_FURNACE), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_FURNACE), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.COMPRESSOR), RecipeTypes.FUELING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new JEIFabricationCategory(helper),
                new JEICompressingCategory(helper),
                new JEIRocketCategory(helper)
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(MachineScreen.class, new MachineGuiHandler());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(GCJEIRecipeTypes.FABRICATION, manager.getAllRecipesFor(GCRecipes.FABRICATION_TYPE).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(GCJEIRecipeTypes.COMPRESSING, manager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(GCJEIRecipeTypes.ROCKET, manager.getAllRecipesFor(GCRecipes.ROCKET_TYPE).stream().map(RecipeHolder::value).toList());

        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        var craftingRecipes = manager.getAllRecipesFor(RecipeType.CRAFTING);;
        var specialCraftingRecipes = replaceSpecialCraftingRecipes(craftingRecipes, jeiHelpers);
        registration.addRecipes(RecipeTypes.CRAFTING, specialCraftingRecipes);
    }

    private static List<RecipeHolder<CraftingRecipe>> replaceSpecialCraftingRecipes(List<RecipeHolder<CraftingRecipe>> unhandledCraftingRecipes, IJeiHelpers jeiHelpers) {
        Map<Class<? extends CraftingRecipe>, Supplier<List<RecipeHolder<CraftingRecipe>>>> replacers = new IdentityHashMap<>();
        replacers.put(EmergencyKitRecipe.class, EmergencyKitRecipeMaker::createRecipes);

        return unhandledCraftingRecipes.stream()
            .map(RecipeHolder::value)
            .map(CraftingRecipe::getClass)
            .distinct()
            .filter(replacers::containsKey)
            // distinct + this limit will ensure we stop iterating early if we find all the recipes we're looking for.
            .limit(replacers.size())
            .flatMap(recipeClass -> {
                var supplier = replacers.get(recipeClass);
                try {
                    return supplier.get()
                        .stream();
                } catch (RuntimeException e) {
                    Constant.LOGGER.error("Failed to create JEI recipes for {}", recipeClass, e);
                    return Stream.of();
                }
            })
            .toList();
    }
}
