/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.api.galaxies;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class CelestialType
{

    private static final Map<String, CelestialType> byName = new HashMap<>();
    private static Collection<CelestialType> allTypes = Collections.unmodifiableCollection(byName.values());

    public static final CelestialType SATELLITE = new CelestialType("satellite");
    public static final CelestialType MOON = new CelestialType("moon");
    public static final CelestialType PLANET = new CelestialType("planet");
    public static final CelestialType STAR = new CelestialType("star");
    public static final CelestialType SOLARSYSTEM = new CelestialType("solarsystem");

    private final String name;

    public static CelestialType create(String name)
    {
        return new CelestialType(name.toLowerCase());
    }

    private CelestialType(String name)
    {
        this.name = name;
        byName.put(name, this);
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String getPrefix()
    {
        return name + ".";
    }

    public static Collection<String> getAllNames()
    {
        return allTypes.stream().map(t -> t.getName()).collect(Collectors.toList());
    }

    public static Collection<CelestialType> getAll()
    {
        return allTypes;
    }
}