/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

import java.util.function.Predicate;

//import lombok.Setter;
import net.minecraft.util.text.translation.I18n;

import lombok.Setter;

public abstract class CelestialObject implements ICelestial
{

    protected final String bodyName;
    @Setter
    private CelestialType type;
    @Setter
    public String ownerId;

    public CelestialObject(CelestialType type, String bodyName)
    {
        this(bodyName);
        this.type = type;
    }

    public CelestialObject(String bodyName)
    {
        this.bodyName = bodyName;
    }

    @Override
    public String getName()
    {
        return this.bodyName;
    }

    public String getTranslationKey()
    {
        return this.getCelestialType().toString() + "." + bodyName;
    }

    @Override
    public CelestialType getCelestialType()
    {
        return this.type;
    }

    public String getOwnerId()
    {
        return this.ownerId;
    }

    public String getTranslatedName()
    {
        return I18n.translateToLocal(this.getTranslationKey());
    }

    public boolean isEqualTo(CelestialType celestialType, String name)
    {
        return type.equals(celestialType) && bodyName.equalsIgnoreCase(name);
    }

    public static Predicate<CelestialObject> filter(String modId)
    {
        return new Predicate<CelestialObject>()
        {

            @Override
            public boolean test(CelestialObject celestialObject)
            {
                return celestialObject.getOwnerId().equals(modId);
            }
        };
    }
}
