/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public record ShapelessCompressingRecipe(String group,
                                         ItemStack output,
                                         NonNullList<Ingredient> input, int time) implements CompressingRecipe {

   @Override
   public RecipeSerializer<?> getSerializer() {
      return GCRecipes.SHAPELESS_COMPRESSING_SERIALIZER;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      return this.input;
   }

   @Override
   public boolean matches(CraftingContainer inv, Level world) {
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
   public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
      return this.getResultItem(registryAccess).copy();
   }

   @Override
   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.input.size();
   }

   @Override
   public ItemStack getResultItem(RegistryAccess registryAccess) {
      return this.output;
   }

   public static class Serializer implements RecipeSerializer<ShapelessCompressingRecipe> {
      public static final Serializer INSTANCE = new Serializer();
      public static final Codec<ShapelessCompressingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
              ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(ShapelessCompressingRecipe::group),
              ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(ShapelessCompressingRecipe::output),
              Ingredient.CODEC_NONEMPTY
                      .listOf()
                      .fieldOf("ingredients")
                      .flatXmap(
                              list -> {
                                 Ingredient[] ingredients = (Ingredient[])list.stream().filter(ingredient -> !ingredient.isEmpty()).toArray(i -> new Ingredient[i]);
                                 if (ingredients.length == 0) {
                                    return DataResult.error(() -> "No ingredients for shapeless recipe");
                                 } else {
                                    return ingredients.length > 9
                                            ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                            : DataResult.success(NonNullList.<Ingredient>of(Ingredient.EMPTY, ingredients));
                                 }
                              },
                              DataResult::success
                      )
                      .forGetter(ShapelessCompressingRecipe::input),
              ExtraCodecs.strictOptionalField(Codec.INT, "time", 200).forGetter(ShapelessCompressingRecipe::time)
      ).apply(instance, ShapelessCompressingRecipe::new));

      @Override
      public Codec<ShapelessCompressingRecipe> codec() {
         return CODEC;
      }

      @Override
      public ShapelessCompressingRecipe fromNetwork(FriendlyByteBuf buf) {
         String group = buf.readUtf(Constant.Misc.MAX_STRING_READ);
         int time = buf.readInt();
         int size = buf.readVarInt();
         NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);

         for (int i = 0; i < ingredients.size(); ++i) {
            ingredients.set(i, Ingredient.fromNetwork(buf));
         }

         ItemStack itemStack = buf.readItem();
         return new ShapelessCompressingRecipe(group, itemStack, ingredients, time);
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
