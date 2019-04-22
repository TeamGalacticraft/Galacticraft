package com.hrznstudio.galacticraft.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FabricationRecipeSerializer<T extends FabricationRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> recipeFactory;

    public FabricationRecipeSerializer(FabricationRecipeSerializer.RecipeFactory<T> factory) {
        this.recipeFactory = factory;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, T recipe) {
        packetByteBuf.writeString(recipe.getGroup());
        recipe.getInput().write(packetByteBuf);
        packetByteBuf.writeItemStack(recipe.getOutput());
    }

    @Override
    public T read(Identifier id, PacketByteBuf packet) {
        String string_1 = packet.readString(32767);
        Ingredient ingredient_1 = Ingredient.fromPacket(packet);
        ItemStack itemStack_1 = packet.readItemStack();
        return this.recipeFactory.create(id, string_1, ingredient_1, itemStack_1);
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        Ingredient inputIngredient;
        if (JsonHelper.hasArray(json, "ingredient")) {
            inputIngredient = Ingredient.fromJson(JsonHelper.getArray(json, "ingredient"));
        } else {
            inputIngredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
        }

        String result = JsonHelper.getString(json, "result");
        int count = JsonHelper.getInt(json, "count");
        ItemStack outputItem = new ItemStack(Registry.ITEM.get(new Identifier(result)), count);
        return this.recipeFactory.create(id, group, inputIngredient, outputItem);
    }

    interface RecipeFactory<T extends FabricationRecipe> {
        T create(Identifier id, String var2, Ingredient input, ItemStack output);
    }
}