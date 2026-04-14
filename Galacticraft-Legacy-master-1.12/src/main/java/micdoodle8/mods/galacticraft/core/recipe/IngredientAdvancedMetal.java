/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import micdoodle8.mods.galacticraft.core.GCItems;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import micdoodle8.mods.galacticraft.planets.venus.VenusItems;

import com.google.gson.JsonObject;

public class IngredientAdvancedMetal implements IIngredientFactory
{

    @Nonnull
    @Override
    public Ingredient parse(JsonContext context, JsonObject json)
    {
        String metal = JsonUtils.getString(json, "metal");
        if (metal.equals("meteoric_iron_ingot"))
        {
            return Ingredient.fromStacks(new ItemStack(GCItems.itemBasicMoon, 1, 0));
        }
        if (metal.equals("meteoric_iron_plate"))
        {
            return Ingredient.fromStacks(new ItemStack(GCItems.itemBasicMoon, 1, 1));
        }
        if (metal.equals("desh_ingot"))
        {
            return Ingredient.fromStacks(new ItemStack(MarsItems.marsItemBasic, 1, 2));
        }
        if (metal.equals("desh_plate"))
        {
            return Ingredient.fromStacks(new ItemStack(MarsItems.marsItemBasic, 1, 5));
        }
        if (metal.equals("titanium_ingot"))
        {
            return Ingredient.fromStacks(new ItemStack(AsteroidsItems.basicItem, 1, 0));
        }
        if (metal.equals("titanium_plate"))
        {
            return Ingredient.fromStacks(new ItemStack(AsteroidsItems.basicItem, 1, 6));
        }
        if (metal.equals("lead_ingot"))
        {
            return Ingredient.fromStacks(new ItemStack(VenusItems.basicItem, 1, 1));
        }
        return Ingredient.fromItem(GCItems.infiniteBatery);
    }
}
