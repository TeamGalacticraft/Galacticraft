package com.hrznstudio.galacticraft.recipes;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftRecipes {
    public static RecipeType<FabricationRecipe> FABRICATION_TYPE;
    public static RecipeType<ShapelessCompressingRecipe> SHAPELESS_COMPRESSING_TYPE;
    public static RecipeType<ShapedCompressingRecipe> SHAPED_COMPRESSING_TYPE;

    static FabricationRecipeSerializer<FabricationRecipe> FABRICATION_SERIALIZER;
    static ShapelessCompressingRecipeSerializer<ShapelessCompressingRecipe> SHAPELESS_COMPRESSING_SERIALIZER;
    static ShapedCompressingRecipeSerializer<ShapedCompressingRecipe> SHAPED_COMPRESSING_SERIALIZER;

    public static void register() {
        // Circuit fabricator recipe stuff
        FABRICATION_TYPE = registerType("circuit_fabricator");
        FABRICATION_SERIALIZER = registerSerializer("circuit_fabricator", new FabricationRecipeSerializer<>(FabricationRecipe::new));

        SHAPELESS_COMPRESSING_TYPE = registerType("compressing_shapeless");
        SHAPELESS_COMPRESSING_SERIALIZER = registerSerializer("compressing_shapeless", new ShapelessCompressingRecipeSerializer<>(ShapelessCompressingRecipe::new));

        SHAPED_COMPRESSING_TYPE = registerType("compressing_shaped");
        SHAPED_COMPRESSING_SERIALIZER = registerSerializer("compressing_shaped", new ShapedCompressingRecipeSerializer<>(ShapedCompressingRecipe::new));
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String id) {
        return Registry.register(Registry.RECIPE_TYPE, new Identifier(Constants.MOD_ID, id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(Constants.MOD_ID, id), serializer);
    }
}