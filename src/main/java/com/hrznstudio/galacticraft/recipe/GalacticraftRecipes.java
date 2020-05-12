/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.recipe;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftRecipes {
    public static final RecipeType<FabricationRecipe> FABRICATION_TYPE = registerType("circuit_fabricator");
    public static final RecipeType<ShapelessCompressingRecipe> SHAPELESS_COMPRESSING_TYPE = registerType("compressing_shapeless");
    public static final RecipeType<ShapedCompressingRecipe> SHAPED_COMPRESSING_TYPE = registerType("compressing_shaped");
    public static final RecipeType<RocketAssemblerRecipe> ROCKET_ASSEMBLER_TYPE = Registry.register(Registry.RECIPE_TYPE, new Identifier(Constants.MOD_ID, "rocket_assembler"), new RecipeType<RocketAssemblerRecipe>() {
    });

    public static final FabricationRecipeSerializer<FabricationRecipe> FABRICATION_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Constants.MOD_ID, "circuit_fabricator"), new FabricationRecipeSerializer<>(FabricationRecipe::new));
    public static final ShapelessCompressingRecipeSerializer<ShapelessCompressingRecipe> SHAPELESS_COMPRESSING_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Constants.MOD_ID, "compressing_shapeless"), new ShapelessCompressingRecipeSerializer<>(ShapelessCompressingRecipe::new));
    public static final ShapedCompressingRecipeSerializer<ShapedCompressingRecipe> SHAPED_COMPRESSING_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Constants.MOD_ID, "compressing_shaped"), new ShapedCompressingRecipeSerializer<>(ShapedCompressingRecipe::new));
    public static final RocketAssemblerRecipeSerializer<RocketAssemblerRecipe> ROCKET_ASSEMBLER_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Constants.MOD_ID, "rocket_assembler"), new RocketAssemblerRecipeSerializer<>(RocketAssemblerRecipe::new));

    public static void register() {
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(Constants.MOD_ID, id), new RecipeType<T>() {
            @Override
            public String toString() {
                return id;
            }
        });
    }
}