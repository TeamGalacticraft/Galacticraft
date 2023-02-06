/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.galacticraft.mod.Constant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ShapedCompressingRecipe implements CompressingRecipe {
   private final int width;
   private final int height;
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;
   private final ResourceLocation id;
   private final String group;
   private final int time;

   public ShapedCompressingRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack output, int time) {
      this.id = id;
      this.group = group;
      this.width = width;
      this.height = height;
      this.inputs = ingredients;
      this.output = output;
      this.time = time;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return GalacticraftRecipe.SHAPED_COMPRESSING_SERIALIZER;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public ItemStack getResultItem() {
      return this.output;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return width >= this.width && height >= this.height;
   }

   @Override
   public boolean matches(Container inv, Level world) {
      if (inv.getContainerSize() != 9) throw new AssertionError();
      for(int i = 0; i <= 3 - this.width; ++i) {
         for(int j = 0; j <= 3 - this.height; ++j) {
            if (this.matchesSmall(inv, i, j, true)) {
               return true;
            }

            if (this.matchesSmall(inv, i, j, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean matchesSmall(Container inv, int offsetX, int offsetY, boolean bl) {
      if (inv.getContainerSize() != 9) throw new AssertionError();
      for(int x = 0; x < 3; ++x) {
         for(int y = 0; y < 3; ++y) {
            int k = x - offsetX;
            int l = y - offsetY;
            Ingredient ingredient = Ingredient.EMPTY;
            if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
               if (bl) {
                  ingredient = this.inputs.get(this.width - k - 1 + l * this.width);
               } else {
                  ingredient = this.inputs.get(k + l * this.width);
               }
            }

            if (!ingredient.test(inv.getItem(x + y * 3))) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack assemble(Container Inventory) {
      return this.getResultItem().copy();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private static NonNullList<Ingredient> getIngredients(String[] pattern, Map<String, Ingredient> key, int width, int height) {
      NonNullList<Ingredient> defaultedList = NonNullList.withSize(width * height, Ingredient.EMPTY);
      Set<String> set = new HashSet<>(key.keySet());
      set.remove(" ");

      for(int i = 0; i < pattern.length; ++i) {
         for(int j = 0; j < pattern[i].length(); ++j) {
            String string = pattern[i].substring(j, j + 1);
            Ingredient ingredient = key.get(string);
            if (ingredient == null) {
               throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
            }

            set.remove(string);
            defaultedList.set(j + width * i, ingredient);
         }
      }

      if (!set.isEmpty()) {
         throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
      } else {
         return defaultedList;
      }
   }

   @VisibleForTesting
   static String[] combinePattern(String... lines) {
      int i = Integer.MAX_VALUE;
      int j = 0;
      int k = 0;
      int l = 0;

      for(int m = 0; m < lines.length; ++m) {
         String string = lines[m];
         i = Math.min(i, findNextIngredient(string));
         int n = findNextIngredientReverse(string);
         j = Math.max(j, n);
         if (n < 0) {
            if (k == m) {
               ++k;
            }

            ++l;
         } else {
            l = 0;
         }
      }

      if (lines.length == l) {
         return new String[0];
      } else {
         String[] strings = new String[lines.length - l - k];

         for(int o = 0; o < strings.length; ++o) {
            strings[o] = lines[o + k].substring(i, j + 1);
         }

         return strings;
      }
   }

   private static int findNextIngredient(String pattern) {
      int i = 0;
      while (i < pattern.length() && pattern.charAt(i) == ' ') {
         ++i;
      }

      return i;
   }

   private static int findNextIngredientReverse(String pattern) {
      int i = pattern.length() - 1;
      while (i >= 0 && pattern.charAt(i) == ' ') {
         --i;
      }

      return i;
   }

   private static String[] getPattern(JsonArray json) {
      String[] strings = new String[json.size()];
      if (strings.length > 3) {
         throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
      } else if (strings.length == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         for(int i = 0; i < strings.length; ++i) {
            String string = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");
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

   private static Map<String, Ingredient> getComponents(JsonObject json) {
      Map<String, Ingredient> map = Maps.newHashMap();

      for (Entry<String, JsonElement> entry : json.entrySet()) {
         if (entry.getKey().length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
         }

         if (" ".equals(entry.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
         }

         map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
      }

      map.put(" ", Ingredient.EMPTY);
      return map;
   }

   public static ItemStack getItemStack(JsonObject json) {
      String string = GsonHelper.getAsString(json, "item");
      Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
      if (json.has("data")) {
         throw new JsonParseException("Disallowed data tag found");
      } else {
         int i = GsonHelper.getAsInt(json, "count", 1);
         return new ItemStack(item, i);
      }
   }

   @Override
   public int getTime() {
      return this.time;
   }

   public enum Serializer implements RecipeSerializer<ShapedCompressingRecipe> {
      INSTANCE;

      @Override
      public ShapedCompressingRecipe fromJson(ResourceLocation id, JsonObject object) {
         String string = GsonHelper.getAsString(object, "group", "");
         int time = GsonHelper.getAsInt(object, "time", 200);
         Map<String, Ingredient> map = ShapedCompressingRecipe.getComponents(GsonHelper.getAsJsonObject(object, "key"));
         String[] pattern = ShapedCompressingRecipe.combinePattern(ShapedCompressingRecipe.getPattern(GsonHelper.getAsJsonArray(object, "pattern")));
         int width = pattern[0].length();
         int height = pattern.length;
         NonNullList<Ingredient> defaultedList = ShapedCompressingRecipe.getIngredients(pattern, map, width, height);
         ItemStack itemStack = ShapedCompressingRecipe.getItemStack(GsonHelper.getAsJsonObject(object, "result"));
         return new ShapedCompressingRecipe(id, string, width, height, defaultedList, itemStack, time);
      }

      @Override
      public ShapedCompressingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
         int width = buf.readVarInt();
         int height = buf.readVarInt();
         String string = buf.readUtf(Constant.Misc.MAX_STRING_READ);
         int time = buf.readInt();
         NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

         for(int k = 0; k < ingredients.size(); ++k) {
            ingredients.set(k, Ingredient.fromNetwork(buf));
         }

         ItemStack output = buf.readItem();
         return new ShapedCompressingRecipe(id, string, width, height, ingredients, output, time);
      }

      @Override
      public void toNetwork(FriendlyByteBuf buf, ShapedCompressingRecipe recipe) {
         buf.writeVarInt(recipe.width);
         buf.writeVarInt(recipe.height);
         buf.writeUtf(recipe.group);
         buf.writeInt(recipe.time);

         for (Ingredient ingredient : recipe.inputs) {
            ingredient.toNetwork(buf);
         }

         buf.writeItem(recipe.output);
      }
   }
}
