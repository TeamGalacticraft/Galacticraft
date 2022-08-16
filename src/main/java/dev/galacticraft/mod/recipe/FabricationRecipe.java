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

import com.google.gson.JsonObject;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FabricationRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final String group;
    private final ItemStack output;
    private final NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
    private final int time;

    public FabricationRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, int time) {
        this.id = id;
        this.group = group;
        this.input.set(0, input);
        this.output = output;
        this.time = time;
    }

    @Override
    public ItemStack assemble(Container inventory) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 0;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return this.input.get(0).test(inventory.getItem(0));
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipe.FABRICATION_TYPE;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipe.FABRICATION_SERIALIZER;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.time;
    }

    public enum Serializer implements RecipeSerializer<FabricationRecipe> {
        INSTANCE;

        @Override
        public FabricationRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            int time = GsonHelper.getAsInt(json, "time", 300);
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            ItemStack result = new ItemStack(ShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "result")));
            return new FabricationRecipe(id, group, ingredient, result, time);
        }

        @Override
        public FabricationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new FabricationRecipe(id, buf.readUtf(Constant.Misc.MAX_STRING_READ), Ingredient.fromNetwork(buf), buf.readItem(), buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FabricationRecipe recipe) {
            buf.writeUtf(recipe.group);
            recipe.input.get(0).toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.time);
        }
    }
}