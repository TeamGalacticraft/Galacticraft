/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.recipe;

import java.util.function.BooleanSupplier;

import net.minecraft.util.JsonUtils;

import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;

import com.google.gson.JsonObject;

public class ConditionEnabled implements IConditionFactory
{

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json)
    {
        if (JsonUtils.hasField(json, "data"))
        {
            String data = JsonUtils.getString(json, "data");
            if (data.equals("can_default"))
            {
                return () -> !ConfigManagerCore.alternateCanisterRecipe;
            }
            if (data.equals("can_alt"))
            {
                return () -> ConfigManagerCore.alternateCanisterRecipe;
            }
            if (data.equals("aa_loaded"))
            {
                return () -> !CompatibilityManager.modAALoaded;
            }
            GalacticraftCore.logger.error("Unrecognised condition data: " + data);
        }

        throw new IllegalStateException("Galacticraft recipe JSON condition error in recipe for " + CraftingHelper.getItemStack(json, context));
    }
}
