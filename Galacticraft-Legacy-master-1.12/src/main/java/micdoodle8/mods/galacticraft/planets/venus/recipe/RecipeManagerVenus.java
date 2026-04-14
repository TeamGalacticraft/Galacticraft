/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.planets.venus.recipe;

import java.util.Arrays;
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes;
import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks;
import micdoodle8.mods.galacticraft.planets.venus.VenusItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeManagerVenus
{

    public static void loadCompatibilityRecipes()
    {
    }

    public static void addUniversalRecipes()
    {
        OreDictionary.registerOre("ingotLead", new ItemStack(VenusItems.basicItem, 1, 1));
        OreDictionary.registerOre("waferSolarThin", new ItemStack(VenusItems.basicItem, 1, 6));

        // Smelting
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(VenusBlocks.venusBlock, 1, 6), new ItemStack(GCItems.basicItem, 1, 5), 0.5F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(VenusBlocks.venusBlock, 1, 7), new ItemStack(GCItems.basicItem, 1, 3), 0.5F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(VenusBlocks.venusBlock, 1, 8), new ItemStack(VenusItems.basicItem, 1, 1), 1.0F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(VenusBlocks.venusBlock, 1, 9), new ItemStack(Items.QUARTZ), 0.2F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(VenusBlocks.venusBlock, 1, 11), new ItemStack(GCItems.basicItem, 1, 4), 0.5F);

        ItemStack solarPanels = new ItemStack(VenusItems.basicItem, 3, 6);
        ItemStack silicon = new ItemStack(GCItems.basicItem, 1, 2);
        CircuitFabricatorRecipes.addRecipe(solarPanels, Arrays.asList(new ItemStack(Items.DIAMOND), silicon, silicon, new ItemStack(Items.REDSTONE), new ItemStack(VenusItems.basicItem, 1, 4)));
    }
}
