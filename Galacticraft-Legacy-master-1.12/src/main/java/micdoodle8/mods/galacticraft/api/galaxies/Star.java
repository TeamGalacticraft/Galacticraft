/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

public class Star extends CelestialBody

{
    protected SolarSystem parentSolarSystem = null;

    public Star(String planetName)
    {
        super(CelestialType.STAR, planetName);
    }

    public SolarSystem getParentSolarSystem()
    {
        return this.parentSolarSystem;
    }

    public Star setParentSolarSystem(SolarSystem galaxy)
    {
        this.parentSolarSystem = galaxy;
        return this;
    }
}
