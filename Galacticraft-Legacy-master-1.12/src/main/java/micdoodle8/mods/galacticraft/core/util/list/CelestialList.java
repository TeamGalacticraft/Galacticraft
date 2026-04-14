/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialObject;

import com.google.common.collect.ImmutableMap;

public class CelestialList<T extends CelestialObject> extends ArrayList<T>
{

    private static final long serialVersionUID = 1L;
    private Map<String, T>    celestialListMap;

    public static <E extends CelestialObject> CelestialList<E> create()
    {
        return new CelestialList<>();
    }

    @SafeVarargs
    public static <E extends CelestialObject> CelestialList<E> of(CelestialList<E>... lists)
    {
        List<E> temp = new ArrayList<>();
        for (List<E> l : lists)
        {
            temp.addAll(l);
        }
        return new CelestialList<>(temp);
    }

    public CelestialList(Collection<? extends T> s)
    {
        super(s);
    }

    public CelestialList()
    {
        super();
        this.celestialListMap = new HashMap<>();
    }

    private T mapPut(String name, T t)
    {//@noformat
        if (celestialListMap.containsKey(name)) { return celestialListMap.get(name); }
        return celestialListMap.put(name, t);
    }

    @Override
    public boolean add(T e)
    {//@noformat
        if (this.contains(e)) { return false; }
        mapPut(e.getName(), e);
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {//@noformat
        for (T t : c) { add(t); }
        return true;
    }

    public Map<String, T> getRegistered()
    {
        return ImmutableMap.copyOf(this.celestialListMap);
    }
    
    public ImmutableCelestialList<T> toUnmodifiableList()
    {
        return ImmutableCelestialList.unmodifiable(this);
    }
}
