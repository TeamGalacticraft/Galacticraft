/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.mars.client.jei.cargorocket;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import micdoodle8.mods.galacticraft.core.client.jei.tier1rocket.Tier1RocketRecipeMaker;

public class CargoRocketRecipeMaker
{

    public static List<INasaWorkbenchRecipe> getRecipesList()
    {
        List<INasaWorkbenchRecipe> recipes = new ArrayList<>();

        int chestCount = -1;
        for (INasaWorkbenchRecipe recipe : GalacticraftRegistry.getCargoRocketRecipes())
        {
            int chests = Tier1RocketRecipeMaker.countChests(recipe);
            if (chests == chestCount)
                continue;
            chestCount = chests;
            recipes.add(recipe);
        }

        return recipes;
    }
}
