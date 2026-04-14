/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.jei.circuitfabricator;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes;

public class CircuitFabricatorRecipeMaker
{

    public static List<CircuitFabricatorRecipeWrapper> getRecipesList()
    {
        List<CircuitFabricatorRecipeWrapper> recipes = new ArrayList<>();

        int count = 0;
        for (List<Object> entry : CircuitFabricatorRecipes.getRecipes())
        {
            CircuitFabricatorRecipeWrapper wrapper = new CircuitFabricatorRecipeWrapper(entry, CircuitFabricatorRecipes.getOutput(count));
            recipes.add(wrapper);
            count++;
        }

        return recipes;
    }
}
