/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor;

import javax.annotation.Nonnull;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

public class OxygenCompressorRecipeWrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper
{

    @Nonnull private final ItemStack output;

    public OxygenCompressorRecipeWrapper(@Nonnull ItemStack output)
    {
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ingredients.setOutput(ItemStack.class, this.output);
    }
}
