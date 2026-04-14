/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.tile;

import java.util.HashSet;
import micdoodle8.mods.galacticraft.api.entity.IDockable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * The Interface IFuelDock.
 */
public interface IFuelDock
{

    /**
     * Gets the connected tiles.
     *
     * @return the connected tiles
     */
    HashSet<ILandingPadAttachable> getConnectedTiles();

    /**
     * Checks if is block attachable.
     *
     * @param world the world
     * @param pos the pos
     * @return true, if is block attachable
     */
    boolean isBlockAttachable(IBlockAccess world, BlockPos pos);

    /**
     * Gets the docked entity.
     *
     * @return the docked entity
     */
    IDockable getDockedEntity();

    /**
     * Dock entity.
     *
     * @param entity the entity
     */
    void dockEntity(IDockable entity);
}
