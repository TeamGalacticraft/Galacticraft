/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ShapelessCompressingRecipe implements CompressingRecipe {
    private final ResourceLocation id;

    private final ItemStack output;
    private final NonNullList<Ingredient> input;

    public ShapelessCompressingRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    static ItemStack getStack(JsonObject json) {
        String itemId = GsonHelper.getAsString(json, "item");
        Item ingredientItem = Registry.ITEM.getOptional(new ResourceLocation(itemId)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + itemId + "'"));
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int count = GsonHelper.getAsInt(json, "count", 1);
            return new ItemStack(ingredientItem, count);
        }
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<? extends ShapelessCompressingRecipe> getSerializer() {
        return GalacticraftRecipes.SHAPELESS_COMPRESSING_SERIALIZER;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(Container inv, Level world) {
        StackedContents finder = new StackedContents();
        int int_1 = 0;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                ++int_1;
                finder.accountStack(stack);
            }
        }

        return int_1 == this.input.size() && finder.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(Container inv) {
        return this.output.copy();
    }

    @Environment(EnvType.CLIENT)
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.input.size();
    }

    public NonNullList<Ingredient> getInput() {
        return this.input;
    }
}