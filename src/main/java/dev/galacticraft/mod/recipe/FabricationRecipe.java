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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class FabricationRecipe implements Recipe<Container> {
    private final String group;
    private final ItemStack output;
    private final NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
    private final int time;

    public FabricationRecipe(String group, Ingredient input, ItemStack output, int time) {
        this.group = group;
        this.input.set(0, input);
        this.output = output;
        this.time = time;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 0;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.output;
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
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    @Override
    public RecipeType<?> getType() {
        return GCRecipes.FABRICATION_TYPE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipes.FABRICATION_SERIALIZER;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.time;
    }

    public static class Serializer implements RecipeSerializer<FabricationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Codec<FabricationRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.input.get(0)),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                ExtraCodecs.strictOptionalField(Codec.INT, "time", 300).forGetter(recipe -> recipe.time)
        ).apply(instance, FabricationRecipe::new));

        @Override
        public Codec<FabricationRecipe> codec() {
            return CODEC;
        }

        @Override
        public FabricationRecipe fromNetwork(FriendlyByteBuf buf) {
            return new FabricationRecipe(buf.readUtf(Constant.Misc.MAX_STRING_READ), Ingredient.fromNetwork(buf), buf.readItem(), buf.readInt());
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