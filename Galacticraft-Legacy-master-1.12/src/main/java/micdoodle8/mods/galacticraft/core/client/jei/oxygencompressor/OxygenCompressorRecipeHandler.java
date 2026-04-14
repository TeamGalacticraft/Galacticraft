/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor;

import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import micdoodle8.mods.galacticraft.core.client.jei.RecipeCategories;

public class OxygenCompressorRecipeHandler implements IRecipeHandler<OxygenCompressorRecipeWrapper>
{

    @Nonnull
    @Override
    public Class<OxygenCompressorRecipeWrapper> getRecipeClass()
    {
        return OxygenCompressorRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid(OxygenCompressorRecipeWrapper recipe)
    {
        return RecipeCategories.OXYGEN_COMPRESSOR_ID;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull OxygenCompressorRecipeWrapper recipe)
    {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull OxygenCompressorRecipeWrapper recipe)
    {
        return true;
    }
}
