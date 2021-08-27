/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

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
   private final DefaultedList<Ingredient> inputs;
   private final ItemStack output;
   private final Identifier id;
   private final String group;
   private final int time;

   public ShapedCompressingRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output, int time) {
      this.id = id;
      this.group = group;
      this.width = width;
      this.height = height;
      this.inputs = ingredients;
      this.output = output;
      this.time = time;
   }

   @Override
   public Identifier getId() {
      return this.id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return GalacticraftRecipe.SHAPED_COMPRESSING_SERIALIZER;
   }

   @Override
   @Environment(EnvType.CLIENT)
   public String getGroup() {
      return this.group;
   }

   @Override
   public ItemStack getOutput() {
      return this.output;
   }

   @Override
   public DefaultedList<Ingredient> getIngredients() {
      return this.inputs;
   }

   @Override
   @Environment(EnvType.CLIENT)
   public boolean fits(int width, int height) {
      return width >= this.width && height >= this.height;
   }

   @Override
   public boolean matches(Inventory inv, World world) {
      if (inv.size() != 9) throw new AssertionError();
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

   private boolean matchesSmall(Inventory inv, int offsetX, int offsetY, boolean bl) {
      if (inv.size() != 9) throw new AssertionError();
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

            if (!ingredient.test(inv.getStack(x + y * 3))) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public ItemStack craft(Inventory Inventory) {
      return this.getOutput().copy();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private static DefaultedList<Ingredient> getIngredients(String[] pattern, Map<String, Ingredient> key, int width, int height) {
      DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
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
      String string = JsonHelper.getString(json, "item");
      Item item = Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
      if (json.has("data")) {
         throw new JsonParseException("Disallowed data tag found");
      } else {
         int i = JsonHelper.getInt(json, "count", 1);
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
      public ShapedCompressingRecipe read(Identifier id, JsonObject object) {
         String string = JsonHelper.getString(object, "group", "");
         int time = JsonHelper.getInt(object, "time", 200);
         Map<String, Ingredient> map = ShapedCompressingRecipe.getComponents(JsonHelper.getObject(object, "key"));
         String[] pattern = ShapedCompressingRecipe.combinePattern(ShapedCompressingRecipe.getPattern(JsonHelper.getArray(object, "pattern")));
         int width = pattern[0].length();
         int height = pattern.length;
         DefaultedList<Ingredient> defaultedList = ShapedCompressingRecipe.getIngredients(pattern, map, width, height);
         ItemStack itemStack = ShapedCompressingRecipe.getItemStack(JsonHelper.getObject(object, "result"));
         return new ShapedCompressingRecipe(id, string, width, height, defaultedList, itemStack, time);
      }

      @Override
      public ShapedCompressingRecipe read(Identifier id, PacketByteBuf buf) {
         int width = buf.readVarInt();
         int height = buf.readVarInt();
         String string = buf.readString(Constant.Misc.MAX_STRING_READ);
         int time = buf.readInt();
         DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);

         for(int k = 0; k < ingredients.size(); ++k) {
            ingredients.set(k, Ingredient.fromPacket(buf));
         }

         ItemStack output = buf.readItemStack();
         return new ShapedCompressingRecipe(id, string, width, height, ingredients, output, time);
      }

      @Override
      public void write(PacketByteBuf buf, ShapedCompressingRecipe recipe) {
         buf.writeVarInt(recipe.width);
         buf.writeVarInt(recipe.height);
         buf.writeString(recipe.group);
         buf.writeInt(recipe.time);

         for (Ingredient ingredient : recipe.inputs) {
            ingredient.write(buf);
         }

         buf.writeItemStack(recipe.output);
      }
   }
}
