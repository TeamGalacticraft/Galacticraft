/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor;

import java.util.ArrayList;
import java.util.List;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot;
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank;
import net.minecraft.item.ItemStack;

public class OxygenCompressorRecipeMaker
{

    public static List<OxygenCompressorRecipeWrapper> getRecipesList()
    {
        List<OxygenCompressorRecipeWrapper> recipes = new ArrayList<>();

        for (ItemStack stack : GalacticraftRegistry.listAllGearForSlot(EnumExtendedInventorySlot.LEFT_TANK))
        {
            if (stack != null && stack.getItem() instanceof ItemOxygenTank)
            {
                recipes.add(new OxygenCompressorRecipeWrapper(stack.copy()));
            }
        }

        return recipes;
    }
}
