/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

import net.minecraft.world.biome.Biome.SpawnListEntry;

public class Planet extends CelestialBody
{
    protected SolarSystem parentSolarSystem = null;

    public Planet(String planetName)
    {
        super(CelestialType.PLANET, planetName);
    }

    public SolarSystem getParentSolarSystem()
    {
        return this.parentSolarSystem;
    }

    public Planet setParentSolarSystem(SolarSystem galaxy)
    {
        this.parentSolarSystem = galaxy;
        return this;
    }
    
    public static void addMobToSpawn(String planetName, SpawnListEntry mobData)
    {
        GalaxyRegistry.getPlanetOrMoonFromTranslationkey("planet." + planetName).addMobInfo(mobData);
    }
}
