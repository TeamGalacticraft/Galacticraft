/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

public interface ICelestial
{

    public String getName();

    public CelestialType getCelestialType();

    public void setOwnerId(String ownerId);
}
