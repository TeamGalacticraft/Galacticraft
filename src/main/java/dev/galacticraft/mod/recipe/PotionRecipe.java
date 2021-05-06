package dev.galacticraft.mod.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PotionRecipe implements CraftingRecipe {

    final String group;
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;
    private final int width;
    private final int height;

    public PotionRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;

        this.width = 3;
        this.height = 3;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return false;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return null;
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipe.POTION_RECIPE_SERIALIZER;
    }


    // borrowed from vanilla... SharedCompressingRecipe does something similar
    // it'd be ideal making a util class later maybe but this should hopefully work???
    private static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < strings.length; ++i) {
                String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    public static ItemStack getItemStack(JsonObject json) {
        String string = JsonHelper.getString(json, "item");
        Item item = (Item) Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + string + "'");
        });
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = JsonHelper.getInt(json, "count", 1);
            return new ItemStack(item, i);
        }
    }


}
