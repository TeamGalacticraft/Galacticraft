/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.tile;

/**
 * The Interface ILockable.
 */
public interface ILockable
{

    /**
     * Gets the locked.
     *
     * @return the locked
     */
    boolean getLocked();

    /**
     * Clear locked inventory.
     */
    void clearLockedInventory();
}
