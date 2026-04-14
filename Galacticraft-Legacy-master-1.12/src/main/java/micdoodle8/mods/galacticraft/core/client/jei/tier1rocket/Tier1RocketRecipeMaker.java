/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.jei.tier1rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class Tier1RocketRecipeMaker
{

    public static List<INasaWorkbenchRecipe> getRecipesList()
    {
        List<INasaWorkbenchRecipe> recipes = new ArrayList<>();

        int chestCount = -1;
        for (INasaWorkbenchRecipe recipe : GalacticraftRegistry.getRocketT1Recipes())
        {
            int chests = Tier1RocketRecipeMaker.countChests(recipe);
            if (chests == chestCount)
                continue;
            chestCount = chests;
            recipes.add(recipe);
        }

        return recipes;
    }

    public static int countChests(INasaWorkbenchRecipe recipe)
    {
        int count = 0;
        NonNullList<ItemStack> woodChests = OreDictionary.getOres("chestWood");
        for (ItemStack woodChest : woodChests)
        {
            for (Entry<Integer, ItemStack> e : recipe.getRecipeInput().entrySet())
            {
                if (ItemStack.areItemsEqual(woodChest, e.getValue()))
                    count++;
            }
        }
        return count;
    }
}
