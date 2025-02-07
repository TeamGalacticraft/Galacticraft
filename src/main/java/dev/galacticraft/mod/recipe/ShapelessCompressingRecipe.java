/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record ShapelessCompressingRecipe(String group,
                                         ItemStack result,
                                         NonNullList<Ingredient> ingredients, int time) implements CompressingRecipe {

   @Override
   public @NotNull RecipeSerializer<?> getSerializer() {
      return GCRecipes.SHAPELESS_COMPRESSING_SERIALIZER;
   }

   @Override
   public @NotNull String getGroup() {
      return this.group;
   }

   @Override
   public @NotNull NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   @Override
   public boolean matches(CraftingInput inv, Level world) {
      StackedContents recipeFinder = new StackedContents();
      int i = 0;

      for (int j = 0; j < inv.size(); ++j) {
         ItemStack itemStack = inv.getItem(j);
         if (!itemStack.isEmpty()) {
            ++i;
            recipeFinder.accountStack(itemStack, 1);
         }
      }

      return i == this.ingredients.size() && recipeFinder.canCraft(this, null);
   }

   @Override
   public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
      return this.getResultItem(lookup).copy();
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.ingredients.size();
   }

   @Override
   public @NotNull ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
      return this.result;
   }

   public static class Serializer implements RecipeSerializer<ShapelessCompressingRecipe> {
      public static final Serializer INSTANCE = new Serializer();
      public static final MapCodec<ShapelessCompressingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
              Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessCompressingRecipe::group),
              ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
              Ingredient.CODEC_NONEMPTY
                      .listOf()
                      .fieldOf("ingredients")
                      .flatXmap(
                              ingredients -> {
                                 Ingredient[] ingredients2 = ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                                 if (ingredients2.length == 0) {
                                    return DataResult.error(() -> "No ingredients for shapeless recipe");
                                 } else {
                                    return ingredients2.length > 9
                                            ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                            : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients2));
                                 }
                              },
                              DataResult::success
                      )
                      .forGetter(recipe -> recipe.ingredients),
              Codec.INT.optionalFieldOf("time", 200).forGetter(ShapelessCompressingRecipe::time)
      ).apply(instance, ShapelessCompressingRecipe::new));
      private static final StreamCodec<RegistryFriendlyByteBuf, ShapelessCompressingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

      @Override
      public @NotNull MapCodec<ShapelessCompressingRecipe> codec() {
         return CODEC;
      }

      @Override
      public @NotNull StreamCodec<RegistryFriendlyByteBuf, ShapelessCompressingRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static ShapelessCompressingRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
         String group = buf.readUtf();
         int i = buf.readVarInt();
         int time = buf.readVarInt();
         NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
         ingredients.replaceAll(empty -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
         ItemStack result = ItemStack.STREAM_CODEC.decode(buf);

         return new ShapelessCompressingRecipe(group, result, ingredients, time);
      }

      private static void toNetwork(RegistryFriendlyByteBuf buf, ShapelessCompressingRecipe recipe) {
         buf.writeUtf(recipe.group);
         buf.writeVarInt(recipe.ingredients.size());
         buf.writeVarInt(recipe.time);
         for (Ingredient ingredient : recipe.ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
         }

         ItemStack.STREAM_CODEC.encode(buf, recipe.result);
      }
   }

   @Override
   public int getTime() {
      return this.time;
   }
}
