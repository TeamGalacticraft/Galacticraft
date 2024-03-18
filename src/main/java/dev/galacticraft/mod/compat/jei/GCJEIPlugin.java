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

package dev.galacticraft.mod.compat.jei;

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.jei.category.JEICompressingCategory;
import dev.galacticraft.mod.compat.jei.category.JEIFabricationCategory;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.recipe.GCRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

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
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_ARC_FURNACE), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_FURNACE), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.COMPRESSOR), RecipeTypes.FUELING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new JEIFabricationCategory(helper),
                new JEICompressingCategory(helper)
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
    }
}
