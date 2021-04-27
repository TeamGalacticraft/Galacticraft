package dev.galacticraft.mod.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class PotionRecipeSerializer<T extends PotionRecipe> implements RecipeSerializer<T>  {

    private final RecipeFactory<T> factory;

    public PotionRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        PotionRecipeJsonFormat recipeJson = new Gson().fromJson(json, PotionRecipeJsonFormat.class);


        return null;
    }

    @Override
    public T read(Identifier id, PacketByteBuf packet) {
        String group = packet.readString(32767);
        Ingredient ingredient = Ingredient.fromPacket(packet);
        ItemStack stack = packet.readItemStack();
        return this.factory.create(id, group, ingredient, stack);
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {

    }

    interface RecipeFactory<T extends PotionRecipe> {

        T create(Identifier id, String group, Ingredient ingredient, ItemStack stack);
    }

    private static class PotionRecipeJsonFormat {
        public JsonObject[] itemArray;

    }

}
