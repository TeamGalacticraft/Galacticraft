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

import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.screen.ingame.CircuitFabricatorScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.CompressorScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.ElectricArcFurnaceScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.ElectricCompressorScreen;
import dev.galacticraft.mod.client.gui.screen.ingame.ElectricFurnaceScreen;
import dev.galacticraft.mod.compat.jei.category.*;
import dev.galacticraft.mod.compat.jei.handlers.*;
import dev.galacticraft.mod.compat.jei.replacers.*;
import dev.galacticraft.mod.compat.jei.subtypes.*;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.EmergencyKitRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.CompressorMenu;
import dev.galacticraft.mod.screen.FoodCannerMenu;
import dev.galacticraft.mod.screen.GCMenuTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
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
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(GCItems.BATTERY, BatterySubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(GCItems.SMALL_OXYGEN_TANK, OxygenTankSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(GCItems.MEDIUM_OXYGEN_TANK, OxygenTankSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(GCItems.LARGE_OXYGEN_TANK, OxygenTankSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(GCItems.ROCKET, RocketSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(GCItems.CANNED_FOOD, CannedFoodSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(GCBlocks.PARACHEST.asItem(), ParachestSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CircuitFabricatorScreen.class, 79, 50, 83, 20, GCJEIRecipeTypes.FABRICATION);
        registration.addRecipeClickArea(
                CompressorScreen.class,
                Constant.Compressor.PROGRESS_X - 1,
                Constant.Compressor.PROGRESS_Y - 3,
                Constant.Compressor.PROGRESS_WIDTH + 2,
                3 + Math.min(Constant.Compressor.PROGRESS_HEIGHT, Constant.Compressor.FUEL_Y - Constant.Compressor.PROGRESS_Y - 2),
                GCJEIRecipeTypes.COMPRESSING
        );
        registration.addRecipeClickArea(
                ElectricCompressorScreen.class,
                Constant.ElectricCompressor.PROGRESS_X - 1,
                Constant.ElectricCompressor.PROGRESS_Y - 3,
                Constant.ElectricCompressor.PROGRESS_WIDTH + 2,
                Constant.ElectricCompressor.PROGRESS_HEIGHT + 6,
                GCJEIRecipeTypes.ELECTRIC_COMPRESSING
        );
        registration.addRecipeClickArea(
                ElectricFurnaceScreen.class,
                Constant.ElectricFurnace.PROGRESS_X - 1,
                Constant.ElectricFurnace.PROGRESS_Y - 3,
                Constant.ElectricFurnace.PROGRESS_WIDTH + 2,
                Constant.ElectricFurnace.PROGRESS_HEIGHT + 6,
                GCJEIRecipeTypes.ELECTRIC_SMELTING
        );
        registration.addRecipeClickArea(
                ElectricArcFurnaceScreen.class,
                Constant.ElectricArcFurnace.PROGRESS_X - 1,
                Constant.ElectricArcFurnace.PROGRESS_Y - 3,
                Constant.ElectricArcFurnace.PROGRESS_WIDTH + 2,
                Constant.ElectricArcFurnace.PROGRESS_HEIGHT + 6,
                GCJEIRecipeTypes.ELECTRIC_BLASTING
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(RecipeMachineMenu.class, null, GCJEIRecipeTypes.FABRICATION,
                CircuitFabricatorBlockEntity.DIAMOND_SLOT, 5, CircuitFabricatorBlockEntity.OUTPUT_SLOT + 1, 36);
        registration.addRecipeTransferHandler(CompressorMenu.class, GCMenuTypes.COMPRESSOR, GCJEIRecipeTypes.COMPRESSING,
                CompressorBlockEntity.INPUT_SLOTS, CompressorBlockEntity.INPUT_LENGTH, CompressorBlockEntity.OUTPUT_SLOT + 1, 36);
        registration.addRecipeTransferHandler(RecipeMachineMenu.class, null, GCJEIRecipeTypes.ELECTRIC_COMPRESSING,
                ElectricCompressorBlockEntity.INPUT_SLOTS, ElectricCompressorBlockEntity.INPUT_LENGTH,
                ElectricCompressorBlockEntity.OUTPUT_SLOTS + ElectricCompressorBlockEntity.OUTPUT_LENGTH, 36);
        registration.addRecipeTransferHandler(RecipeMachineMenu.class, null, GCJEIRecipeTypes.ELECTRIC_SMELTING,
                ElectricFurnaceBlockEntity.INPUT_SLOT, 1, ElectricFurnaceBlockEntity.OUTPUT_SLOT + 1, 36);
        registration.addRecipeTransferHandler(RecipeMachineMenu.class, null, GCJEIRecipeTypes.ELECTRIC_BLASTING,
                ElectricArcFurnaceBlockEntity.INPUT_SLOT, 1, ElectricArcFurnaceBlockEntity.OUTPUT_SLOTS + ElectricArcFurnaceBlockEntity.OUTPUT_LENGTH, 36);
        registration.addRecipeTransferHandler(FoodCannerMenu.class, null, GCJEIRecipeTypes.CANNING,
                FoodCannerBlockEntity.INPUT_SLOT, FoodCannerBlockEntity.INPUT_LENGTH, FoodCannerBlockEntity.OUTPUT_SLOT + 1, 36);
        registration.addRecipeTransferHandler(new RocketRecipeTransferHandler(registration.getTransferHelper()), GCJEIRecipeTypes.ROCKET);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.CIRCUIT_FABRICATOR), GCJEIRecipeTypes.FABRICATION);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.COMPRESSOR), GCJEIRecipeTypes.COMPRESSING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_COMPRESSOR), GCJEIRecipeTypes.ELECTRIC_COMPRESSING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ROCKET_WORKBENCH), GCJEIRecipeTypes.ROCKET);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_ARC_FURNACE), GCJEIRecipeTypes.ELECTRIC_BLASTING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.ELECTRIC_FURNACE), GCJEIRecipeTypes.ELECTRIC_SMELTING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.COMPRESSOR), RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.FOOD_CANNER), GCJEIRecipeTypes.CANNING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new JEIFabricationCategory(helper),
                new JEICompressingCategory(helper),
                new JEIElectricCompressingCategory(helper),
                new JEIElectricFurnaceCategory(helper),
                new JEIElectricArcFurnaceCategory(helper),
                new JEICanningCategory(helper),
                new JEIRocketCategory(helper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        List<CompressingRecipe> compressingRecipes = manager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE).stream().map(RecipeHolder::value)
                .filter(recipe -> recipe.getIngredients().stream().allMatch(ingredient -> !ingredient.isEmpty())).toList();

        registration.addRecipes(GCJEIRecipeTypes.FABRICATION, manager.getAllRecipesFor(GCRecipes.FABRICATION_TYPE).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(GCJEIRecipeTypes.COMPRESSING, compressingRecipes);
        registration.addRecipes(GCJEIRecipeTypes.ELECTRIC_COMPRESSING, compressingRecipes);
        registration.addRecipes(GCJEIRecipeTypes.ELECTRIC_SMELTING, manager.getAllRecipesFor(RecipeType.SMELTING).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(GCJEIRecipeTypes.ELECTRIC_BLASTING, manager.getAllRecipesFor(RecipeType.BLASTING).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(GCJEIRecipeTypes.ROCKET, manager.getAllRecipesFor(GCRecipes.ROCKET_TYPE).stream().map(RecipeHolder::value).toList());

        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        var craftingRecipes = manager.getAllRecipesFor(RecipeType.CRAFTING);;
        var specialCraftingRecipes = replaceSpecialCraftingRecipes(craftingRecipes, jeiHelpers);
        registration.addRecipes(RecipeTypes.CRAFTING, specialCraftingRecipes);
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addTypedRecipeManagerPlugin(GCJEIRecipeTypes.CANNING, new CanningRecipeManagerPlugin());
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
