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

package dev.galacticraft.mod.data.recipes;

import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.mod.api.data.recipe.RocketRecipeBuilder;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GCRocketRecipes extends FabricRecipeProvider {
    public GCRocketRecipes(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCBlocks.ROCKET_WORKBENCH)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.HEAVY_NOSE_CONE)
                .define('R', Items.REDSTONE_TORCH)
                .define('P', GCItems.TIER_3_HEAVY_DUTY_PLATE)
                .pattern(" R ")
                .pattern(" P ")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_3_HEAVY_DUTY_PLATE), has(GCItems.TIER_3_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_FIN)
                .define('S', GCTags.COMPRESSED_STEEL)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .pattern(" S ")
                .pattern("PSP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.HEAVY_ROCKET_FIN)
                .define('T', GCItems.TIER_2_HEAVY_DUTY_PLATE)
                .define('P', GCItems.TIER_3_HEAVY_DUTY_PLATE)
                .pattern(" T ")
                .pattern("PTP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_3_HEAVY_DUTY_PLATE), has(GCItems.TIER_3_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.HEAVY_ROCKET_FIN)
                .define('T', GCItems.COMPRESSED_TITANIUM)
                .define('P', GCItems.TIER_3_HEAVY_DUTY_PLATE)
                .pattern(" T ")
                .pattern("PTP")
                .pattern("P P")
                .unlockedBy(getHasName(GCItems.TIER_3_HEAVY_DUTY_PLATE), has(GCItems.TIER_3_HEAVY_DUTY_PLATE))
                .save(output, getItemName(GCItems.HEAVY_ROCKET_FIN) + "_alt");

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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.HEAVY_ROCKET_ENGINE)
                .define('F', Items.FLINT_AND_STEEL)
                .define('B', Items.STONE_BUTTON)
                .define('V', GCItems.OXYGEN_VENT)
                .define('P', GCItems.TIER_3_HEAVY_DUTY_PLATE)
                .define('T', GCItems.TIN_CANISTER)
                .pattern(" FB")
                .pattern("PTP")
                .pattern("PVP")
                .unlockedBy(getHasName(GCItems.TIER_3_HEAVY_DUTY_PLATE), has(GCItems.TIER_3_HEAVY_DUTY_PLATE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, GCItems.ROCKET_BOOSTER)
                .define('M', GCItems.COMPRESSED_METEORIC_IRON)
                .define('Y', Items.YELLOW_WOOL)
                .define('F', GCItems.FUEL_BUCKET)
                .define('P', GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .define('V', GCItems.OXYGEN_VENT)
                .pattern("MYM")
                .pattern("MFM")
                .pattern("PVP")
                .unlockedBy(getHasName(GCItems.COMPRESSED_METEORIC_IRON), has(GCItems.COMPRESSED_METEORIC_IRON))
                .save(output);

        RocketRecipeBuilder.create(GCItems.ROCKET)
                .rocketData(RocketPrefabs.TIER_1)
                .cone(GCItems.NOSE_CONE)
                .body(GCItems.TIER_1_HEAVY_DUTY_PLATE)
                .bodyHeight(4)
                .fins(GCItems.ROCKET_FIN)
                .engine(GCItems.ROCKET_ENGINE)
                .unlockedBy(getHasName(GCItems.TIER_1_HEAVY_DUTY_PLATE), has(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                .save(output);

        // TODO: Rocket Parts
    }

    @Override
    public @NotNull String getName() {
        return "Rocket Recipes";
    }
}
