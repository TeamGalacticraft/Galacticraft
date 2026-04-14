/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.event;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

import micdoodle8.mods.galacticraft.core.Constants;

public class LootHandlerGC
{

    public static ResourceLocation TABLE_CRASHED_PROBE;

    public static void registerAll()
    {
        TABLE_CRASHED_PROBE = register("crashed_probe");
    }

    private static ResourceLocation register(String table)
    {
        return LootTableList.register(new ResourceLocation(Constants.MOD_ID_CORE, table));
    }
}
