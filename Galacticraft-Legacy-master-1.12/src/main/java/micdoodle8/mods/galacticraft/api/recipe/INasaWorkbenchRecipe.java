/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.recipe;

import java.util.HashMap;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface INasaWorkbenchRecipe
{

    boolean matches(IInventory inventory);

    int getRecipeSize();

    @Nonnull
    ItemStack getRecipeOutput();

    HashMap<Integer, ItemStack> getRecipeInput();
}
