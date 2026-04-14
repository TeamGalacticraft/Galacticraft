/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.mars.client.jei.methanesynth;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.item.ItemStack;

public class MethaneSynthRecipeMaker
{

    public static List<MethaneSynthRecipeWrapper> getRecipesList()
    {
        List<MethaneSynthRecipeWrapper> recipes = new ArrayList<>();

        recipes.add(new MethaneSynthRecipeWrapper(new ItemStack(AsteroidsItems.atmosphericValve), new ItemStack(AsteroidsItems.methaneCanister, 1, 1)));
        recipes.add(new MethaneSynthRecipeWrapper(new ItemStack(MarsItems.carbonFragments, 25, 0), new ItemStack(AsteroidsItems.methaneCanister, 1, 1)));

        return recipes;
    }
}
