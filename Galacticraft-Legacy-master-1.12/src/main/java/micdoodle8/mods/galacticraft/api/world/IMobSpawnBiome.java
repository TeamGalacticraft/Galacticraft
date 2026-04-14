/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.world;

import java.util.LinkedList;
import net.minecraft.world.biome.Biome.SpawnListEntry;

/**
 * Implement this on any Galacticraft World Provider biome registered for a
 * Celestial Body
 */
public interface IMobSpawnBiome
{

    public void initialiseMobLists(LinkedList<SpawnListEntry> mobInfo);
}
