/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.entities;

import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner;

public interface IBoss
{

    void onBossSpawned(TileEntityDungeonSpawner spawner);
}
