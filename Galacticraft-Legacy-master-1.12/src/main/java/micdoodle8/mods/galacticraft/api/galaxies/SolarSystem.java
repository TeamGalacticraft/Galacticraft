/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

public class SolarSystem extends CelestialObject
{

    protected Vector3 mapPosition = null;
    protected Star mainStar = null;
    protected String unlocalizedGalaxyName;

    public SolarSystem(String solarSystem, String parentGalaxy)
    {
        super(CelestialType.SOLARSYSTEM, solarSystem);
        this.unlocalizedGalaxyName = parentGalaxy;
    }

    @Override
    public void setOwnerId(String ownerId)
    {
        super.setOwnerId(ownerId);
    }

    public Vector3 getMapPosition()
    {
        return this.mapPosition;
    }

    public SolarSystem setMapPosition(Vector3 mapPosition)
    {
        mapPosition.scale(500D);
        this.mapPosition = mapPosition;
        return this;
    }

    public Star getMainStar()
    {
        return this.mainStar;
    }

    public SolarSystem setMainStar(Star star)
    {
        this.mainStar = star;
        return this;
    }

    public String getTranslatedParentGalaxyName()
    {
        return super.getTranslatedName();
    }

    public String getParentGalaxyTranslationKey()
    {
        return "galaxy." + this.unlocalizedGalaxyName;
    }

    // DEPRECATED METHODS

    @Deprecated
    @ReplaceWith("getTranslationKey()")
    public String getUnlocalizedName()
    {
        return getTranslationKey();
    }

    @Deprecated
    @ReplaceWith("getTranslatedName()")
    public String getLocalizedName()
    {
        return getTranslatedName();
    }

    @Deprecated
    @ReplaceWith("getTranslatedParentGalaxyName()")
    public String getLocalizedParentGalaxyName()
    {
        return getTranslatedParentGalaxyName();
    }

    @Deprecated
    @ReplaceWith("getParentGalaxyTranslationKey()")
    public String getUnlocalizedParentGalaxyName()
    {
        return "galaxy." + this.unlocalizedGalaxyName;
    }
}
