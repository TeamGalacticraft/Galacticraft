package com.hrznstudio.galacticraft.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ShapelessCompressingRecipeSerializer<T extends ShapelessCompressingRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> factory;

    public ShapelessCompressingRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    private static DefaultedList<Ingredient> getIngredients(JsonArray jsonArray_1) {
        DefaultedList<Ingredient> defaultedList_1 = DefaultedList.create();

        for (int int_1 = 0; int_1 < jsonArray_1.size(); ++int_1) {
            Ingredient ingredient_1 = Ingredient.fromJson(jsonArray_1.get(int_1));
            if (!ingredient_1.isEmpty()) {
                defaultedList_1.add(ingredient_1);
            }
        }

        return defaultedList_1;
    }

    @Override
    public void write(PacketByteBuf packet, ShapelessCompressingRecipe recipe) {
//            packet.writeString(recipe.group);
        packet.writeVarInt(recipe.getInput().size());

        for (Ingredient ingredient : recipe.getInput()) {
            ingredient.write(packet);
        }

        packet.writeItemStack(recipe.getOutput());
    }

    @Override
    public T read(Identifier id, PacketByteBuf packet) {
//            String group = packet.readString(32767);
        int ingredientCount = packet.readVarInt();
        DefaultedList<Ingredient> ingredients = DefaultedList.create(ingredientCount, Ingredient.EMPTY);

        for (int index = 0; index < ingredients.size(); ++index) {
            ingredients.set(index, Ingredient.fromPacket(packet));
        }

        ItemStack result = packet.readItemStack();
        return factory.create(id, /*group, */result, ingredients);
    }

    @Override
    public T read(Identifier id, JsonObject json) {
//            String group = JsonHelper.getString(models, "group", "");
        DefaultedList<Ingredient> ingredients = getIngredients(JsonHelper.getArray(json, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for compressing recipe");
        } else if (ingredients.size() > 9) {
            throw new JsonParseException("Too many ingredients for compressing recipe");
        } else {
            ItemStack result = ShapelessCompressingRecipe.getItemStack(JsonHelper.getObject(json, "result"));
            return factory.create(id, /*group, */result, ingredients);
        }
    }

    interface RecipeFactory<T extends ShapelessCompressingRecipe> {
        T create(Identifier id, ItemStack output, DefaultedList<Ingredient> ingredients);
    }
}