package io.github.teamgalacticraft.galacticraft.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.crafting.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CompressingRecipeSerializer<T extends CompressingRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> recipeFactory;

    public CompressingRecipeSerializer(CompressingRecipeSerializer.RecipeFactory<T> factory) {
        this.recipeFactory = factory;
    }

    @Override
    public T read(Identifier id, PacketByteBuf packet) {
        int width = packet.readVarInt();
        int height = packet.readVarInt();
        String string_1 = packet.readString(32767);
        DefaultedList<Ingredient> ingredients = DefaultedList.create(width * height, Ingredient.EMPTY);

        for (int int_3 = 0; int_3 < ingredients.size(); ++int_3) {
            ingredients.set(int_3, Ingredient.fromPacket(packet));
        }

        ItemStack outputStack = packet.readItemStack();
        return this.recipeFactory.create(id, string_1, width, height, ingredients, outputStack);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, T recipe) {
        packetByteBuf.writeVarInt(recipe.getWidth());
        packetByteBuf.writeVarInt(recipe.getHeight());
        packetByteBuf.writeString(recipe.getGroup());
        Iterator var3 = recipe.getInputs().iterator();

        while (var3.hasNext()) {
            Ingredient ingredient_1 = (Ingredient) var3.next();
            ingredient_1.write(packetByteBuf);
        }

        packetByteBuf.writeItemStack(recipe.getOutput());
    }

    private static Map<String, Ingredient> getComponents(JsonObject jsonObject_1) {
        Map<String, Ingredient> map_1 = Maps.newHashMap();
        Iterator var2 = jsonObject_1.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, JsonElement> map$Entry_1 = (Map.Entry) var2.next();
            if (((String) map$Entry_1.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String) map$Entry_1.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(map$Entry_1.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map_1.put(map$Entry_1.getKey(), Ingredient.fromJson((JsonElement) map$Entry_1.getValue()));
        }

        map_1.put(" ", Ingredient.EMPTY);
        return map_1;
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        String string_1 = JsonHelper.getString(json, "group", "");
        Map<String, Ingredient> map_1 = getComponents(JsonHelper.getObject(json, "key"));
        String[] strings_1 = combinePattern(getPattern(JsonHelper.getArray(json, "pattern")));
        int width = strings_1[0].length();
        int height = strings_1.length;

        DefaultedList<Ingredient> ingredients = getIngredients(strings_1, map_1, width, height);
        ItemStack outputStack = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));
        return this.recipeFactory.create(id, string_1, width, height, ingredients, outputStack);
    }

    //Taken from ShapedRecipe
    private static String[] getPattern(JsonArray jsonArray_1) {
        String[] strings_1 = new String[jsonArray_1.size()];
        if (strings_1.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings_1.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int int_1 = 0; int_1 < strings_1.length; ++int_1) {
                String string_1 = JsonHelper.asString(jsonArray_1.get(int_1), "pattern[" + int_1 + "]");
                if (string_1.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (int_1 > 0 && strings_1[0].length() != string_1.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings_1[int_1] = string_1;
            }

            return strings_1;
        }
    }

    //Taken from ShapedRecipe
    @VisibleForTesting
    static String[] combinePattern(String... strings_1) {
        int int_1 = Integer.MAX_VALUE;
        int int_2 = 0;
        int int_3 = 0;
        int int_4 = 0;

        for (int int_5 = 0; int_5 < strings_1.length; ++int_5) {
            String string_1 = strings_1[int_5];
            int_1 = Math.min(int_1, findNextIngredient(string_1));
            int int_6 = findNextIngredientReverse(string_1);
            int_2 = Math.max(int_2, int_6);
            if (int_6 < 0) {
                if (int_3 == int_5) {
                    ++int_3;
                }

                ++int_4;
            } else {
                int_4 = 0;
            }
        }

        if (strings_1.length == int_4) {
            return new String[0];
        } else {
            String[] strings_2 = new String[strings_1.length - int_4 - int_3];

            for (int int_7 = 0; int_7 < strings_2.length; ++int_7) {
                strings_2[int_7] = strings_1[int_7 + int_3].substring(int_1, int_2 + 1);
            }

            return strings_2;
        }
    }

    //Taken from ShapedRecipe
    private static int findNextIngredient(String string_1) {
        int int_1;
        for (int_1 = 0; int_1 < string_1.length() && string_1.charAt(int_1) == ' '; ++int_1) {
        }

        return int_1;
    }

    //Taken from ShapedRecipe
    private static int findNextIngredientReverse(String string_1) {
        int int_1;
        for (int_1 = string_1.length() - 1; int_1 >= 0 && string_1.charAt(int_1) == ' '; --int_1) {
        }

        return int_1;
    }

    //Taken from ShapedRecipe
    private static DefaultedList<Ingredient> getIngredients(String[] strings_1, Map<String, Ingredient> map_1, int int_1, int int_2) {
        DefaultedList<Ingredient> defaultedList_1 = DefaultedList.create(int_1 * int_2, Ingredient.EMPTY);
        Set<String> set_1 = Sets.newHashSet(map_1.keySet());
        set_1.remove(" ");

        for (int int_3 = 0; int_3 < strings_1.length; ++int_3) {
            for (int int_4 = 0; int_4 < strings_1[int_3].length(); ++int_4) {
                String string_1 = strings_1[int_3].substring(int_4, int_4 + 1);
                Ingredient ingredient_1 = (Ingredient) map_1.get(string_1);
                if (ingredient_1 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string_1 + "' but it's not defined in the key");
                }

                set_1.remove(string_1);
                defaultedList_1.set(int_4 + int_1 * int_3, ingredient_1);
            }
        }

        if (!set_1.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set_1);
        } else {
            return defaultedList_1;
        }
    }

    interface RecipeFactory<T extends CompressingRecipe> {
        T create(Identifier id, String var2, int width, int height, DefaultedList<Ingredient> input, ItemStack output);
    }
}