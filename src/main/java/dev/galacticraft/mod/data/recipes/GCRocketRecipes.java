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

package dev.galacticraft.mod.data.recipes;

import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class GCRocketRecipes extends FabricRecipeProvider {
    public GCRocketRecipes(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_WORKBENCH)
                .define('S', GCItems.COMPRESSED_STEEL)
                .define('C', Items.CRAFTING_TABLE)
                .define('L', Items.LEVER)
                .define('W', GCItems.ADVANCED_WAFER)
                .define('R', Items.REDSTONE_TORCH)
                .pattern("SCS")
                .pattern("LWL")
                .pattern("SRS")
                .unlockedBy(getHasName(GCItems.ADVANCED_WAFER), has(GCItems.ADVANCED_WAFER))
                .save(output);

        // Rocket Part Items
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.NOSE_CONE)
                .define('R', Items.REDSTONE_TORCH)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern(" R ")
                .pattern(" P ")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_FIN)
                .define('S', GCTags.COMPRESSED_STEEL)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern(" S ")
                .pattern("PSP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_ENGINE)
                .define('F', Items.FLINT_AND_STEEL)
                .define('B', Items.STONE_BUTTON)
                .define('V', GCItems.OXYGEN_VENT)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('T', GCItems.TIN_CANISTER)
                .pattern(" FB")
                .pattern("PTP")
                .pattern("PVP")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);

        // TODO: Rocket Parts
    }

    @Override
    public String getName() {
        return "Rocket Recipes";
    }
}
