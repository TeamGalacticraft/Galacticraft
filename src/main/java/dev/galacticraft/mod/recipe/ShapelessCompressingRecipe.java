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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.galacticraft.mod.Constant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessCompressingRecipe implements CompressingRecipe {
   private final Identifier id;
   private final String group;
   private final ItemStack output;
   private final DefaultedList<Ingredient> input;

   public ShapelessCompressingRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
      this.id = id;
      this.group = group;
      this.output = output;
      this.input = input;
   }

   @Override
   public Identifier getId() {
      return this.id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return GalacticraftRecipe.SHAPELESS_COMPRESSING_SERIALIZER;
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
   public DefaultedList<Ingredient> getPreviewInputs() {
      return this.input;
   }

   @Override
   public boolean matches(Inventory inv, World world) {
      RecipeFinder recipeFinder = new RecipeFinder();
      int i = 0;

      for(int j = 0; j < inv.size(); ++j) {
         ItemStack itemStack = inv.getStack(j);
         if (!itemStack.isEmpty()) {
            ++i;
            recipeFinder.method_20478(itemStack, 1);
         }
      }

      return i == this.input.size() && recipeFinder.findRecipe(this, null);
   }

   @Override
   public ItemStack craft(Inventory inv) {
      return this.output.copy();
   }

   @Override
   @Environment(EnvType.CLIENT)
   public boolean fits(int width, int height) {
      return width * height >= this.input.size();
   }

   public enum Serializer implements RecipeSerializer<ShapelessCompressingRecipe> {
      INSTANCE;

      @Override
      public ShapelessCompressingRecipe read(Identifier identifier, JsonObject jsonObject) {
         String string = JsonHelper.getString(jsonObject, "group", "");
         DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
         if (defaultedList.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (defaultedList.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack itemStack = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new ShapelessCompressingRecipe(identifier, string, itemStack, defaultedList);
         }
      }

      private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
         DefaultedList<Ingredient> defaultedList = DefaultedList.of();

         for(int i = 0; i < json.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(json.get(i));
            if (!ingredient.isEmpty()) {
               defaultedList.add(ingredient);
            }
         }

         return defaultedList;
      }

      @Override
      public ShapelessCompressingRecipe read(Identifier identifier, PacketByteBuf buf) {
         String group = buf.readString(Constant.Misc.MAX_STRING_READ);
         int size = buf.readVarInt();
         DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);

         for(int i = 0; i < ingredients.size(); ++i) {
            ingredients.set(i, Ingredient.fromPacket(buf));
         }

         ItemStack itemStack = buf.readItemStack();
         return new ShapelessCompressingRecipe(identifier, group, itemStack, ingredients);
      }

      @Override
      public void write(PacketByteBuf buf, ShapelessCompressingRecipe recipe) {
         buf.writeString(recipe.group);
         buf.writeVarInt(recipe.input.size());

         for (Ingredient ingredient : recipe.input) {
            ingredient.write(buf);
         }

         buf.writeItemStack(recipe.output);
      }
   }
}
