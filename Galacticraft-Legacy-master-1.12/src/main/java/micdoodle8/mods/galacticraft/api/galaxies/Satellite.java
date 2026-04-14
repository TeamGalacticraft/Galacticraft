/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

import net.minecraft.world.WorldProvider;

public class Satellite extends CelestialBody implements IChildBody
{
    protected Planet parentCelestialBody = null;
    protected int dimensionIdStatic = 0;

    public Satellite(String satelliteName)
    {
        super(CelestialType.SATELLITE, satelliteName);
    }

    @Override
    public Planet getParentPlanet()
    {
        return this.parentCelestialBody;
    }

    public Satellite setParentBody(Planet parentCelestialBody)
    {
        this.parentCelestialBody = parentCelestialBody;
        return this;
    }

    public CelestialBody setDimensionInfo(int providerIdDynamic, int providerIdStatic, Class<? extends WorldProvider> providerClass)
    {
        this.dimensionIdStatic = providerIdStatic;
        return super.setDimensionInfo(providerIdDynamic, providerClass, false);
    }

    public int getDimensionIdStatic()
    {
        return dimensionIdStatic;
    }

    @Override
    @Deprecated
    public CelestialBody setDimensionInfo(int providerId, Class<? extends WorldProvider> providerClass, boolean autoRegister)
    {
        throw new UnsupportedOperationException("Satellite registered using an outdated method (setDimensionInfo)! Tell Galacticraft addon authors to update to the latest API.");
    }
}
