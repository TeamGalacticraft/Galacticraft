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

import com.google.gson.JsonObject;
import dev.galacticraft.mod.Constant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FabricationRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input = DefaultedList.ofSize(1, Ingredient.EMPTY);
    private final int time;

    public FabricationRecipe(Identifier id, String group, Ingredient input, ItemStack output, int time) {
        this.id = id;
        this.group = group;
        this.input.set(0, input);
        this.output = output;
        this.time = time;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height > 0;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.input.get(0).test(inventory.getStack(0));
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipe.FABRICATION_TYPE;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipe.FABRICATION_SERIALIZER;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.time;
    }

    public enum Serializer implements RecipeSerializer<FabricationRecipe> {
        INSTANCE;

        @Override
        public FabricationRecipe read(Identifier id, JsonObject json) {
            String group = JsonHelper.getString(json, "group", "");
            int time = JsonHelper.getInt(json, "time", 300);
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
            ItemStack result = new ItemStack(ShapedRecipe.getItem(JsonHelper.getObject(json, "result")));
            return new FabricationRecipe(id, group, ingredient, result, time);
        }

        @Override
        public FabricationRecipe read(Identifier id, PacketByteBuf buf) {
            return new FabricationRecipe(id, buf.readString(Constant.Misc.MAX_STRING_READ), Ingredient.fromPacket(buf), buf.readItemStack(), buf.readInt());
        }

        @Override
        public void write(PacketByteBuf buf, FabricationRecipe recipe) {
            buf.writeString(recipe.group);
            recipe.input.get(0).write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.time);
        }
    }
}