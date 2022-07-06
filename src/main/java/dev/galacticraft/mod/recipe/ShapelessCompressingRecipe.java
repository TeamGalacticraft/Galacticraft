/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public record ShapelessCompressingRecipe(ResourceLocation id, String group,
                                         ItemStack output,
                                         NonNullList<Ingredient> input, int time) implements CompressingRecipe {

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return GalacticraftRecipe.SHAPELESS_COMPRESSING_SERIALIZER;
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
      return this.input;
   }

   @Override
   public boolean matches(Container inv, Level world) {
      StackedContents recipeFinder = new StackedContents();
      int i = 0;

      for (int j = 0; j < inv.getContainerSize(); ++j) {
         ItemStack itemStack = inv.getItem(j);
         if (!itemStack.isEmpty()) {
            ++i;
            recipeFinder.accountStack(itemStack, 1);
         }
      }

      return i == this.input.size() && recipeFinder.canCraft(this, null);
   }

   @Override
   public ItemStack assemble(Container inv) {
      return this.output.copy();
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.input.size();
   }

   public enum Serializer implements RecipeSerializer<ShapelessCompressingRecipe> {
      INSTANCE;

      @Override
      public ShapelessCompressingRecipe fromJson(ResourceLocation identifier, JsonObject jsonObject) {
         String string = GsonHelper.getAsString(jsonObject, "group", "");
         int time = GsonHelper.getAsInt(jsonObject, "time", 200);
         NonNullList<Ingredient> defaultedList = getIngredients(GsonHelper.getAsJsonArray(jsonObject, "ingredients"));
         if (defaultedList.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (defaultedList.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack itemStack = new ItemStack(ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(jsonObject, "result")));
            return new ShapelessCompressingRecipe(identifier, string, itemStack, defaultedList, time);
         }
      }

      private static NonNullList<Ingredient> getIngredients(JsonArray json) {
         NonNullList<Ingredient> defaultedList = NonNullList.create();

         for (int i = 0; i < json.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(json.get(i));
            if (!ingredient.isEmpty()) {
               defaultedList.add(ingredient);
            }
         }

         return defaultedList;
      }

      @Override
      public ShapelessCompressingRecipe fromNetwork(ResourceLocation identifier, FriendlyByteBuf buf) {
         String group = buf.readUtf(Constant.Misc.MAX_STRING_READ);
         int time = buf.readInt();
         int size = buf.readVarInt();
         NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);

         for (int i = 0; i < ingredients.size(); ++i) {
            ingredients.set(i, Ingredient.fromNetwork(buf));
         }

         ItemStack itemStack = buf.readItem();
         return new ShapelessCompressingRecipe(identifier, group, itemStack, ingredients, time);
      }

      @Override
      public void toNetwork(FriendlyByteBuf buf, ShapelessCompressingRecipe recipe) {
         buf.writeUtf(recipe.group);
         buf.writeInt(recipe.time);
         buf.writeVarInt(recipe.input.size());

         for (Ingredient ingredient : recipe.input) {
            ingredient.toNetwork(buf);
         }

         buf.writeItem(recipe.output);
      }
   }

   @Override
   public int getTime() {
      return this.time;
   }
}
