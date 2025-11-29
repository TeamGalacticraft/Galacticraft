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

package dev.galacticraft.mod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmiDefaultRecipeProvider implements DataProvider {
    private static final List<ResourceLocation> defaultRecipes = new ArrayList<>();

    private final FabricDataOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public EmiDefaultRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        JsonArray jsonArray = new JsonArray();
        defaultRecipes.forEach(resourceLocation -> jsonArray.add(resourceLocation.toString()));
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("added", jsonArray);
        return DataProvider.saveStable(writer, jsonObject,
                this.output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "recipe/defaults").json(ResourceLocation.fromNamespaceAndPath("emi", Constant.MOD_ID + "_copy")));
    }

    public static void add(ResourceLocation resourceLocation) {
        defaultRecipes.add(resourceLocation);
    }

    @Override
    public String getName() {
        return "EMI Default Recipes for Galacticraft";
    }
}
