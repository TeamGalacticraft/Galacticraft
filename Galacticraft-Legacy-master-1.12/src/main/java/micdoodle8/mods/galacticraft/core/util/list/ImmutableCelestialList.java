/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialObject;

public class ImmutableCelestialList<E extends CelestialObject> extends Immutable<E>
{

    private static final long serialVersionUID = -186156409078238142L;

    @SafeVarargs
    public static <T extends CelestialObject> ImmutableCelestialList<T> from(CelestialList<T>... lists)
    {
        List<T> temp = new ArrayList<>();
        for (List<T> l : lists)
        {
            temp.addAll(l);
        }
        return new ImmutableCelestialList<>(temp);
    }

    public static <E extends CelestialObject> ImmutableCelestialList<E> of(Collection<E> collection)
    {
        return new ImmutableCelestialList<>(collection);
    }

    static <E extends CelestialObject> ImmutableCelestialList<E> unmodifiable(CelestialList<E> celestialList)
    {
        return new ImmutableCelestialList<>(celestialList);
    }

    public ImmutableCelestialList()
    {
        super();
    }

    public ImmutableCelestialList(Collection<E> collection)
    {
        super(collection);
    }

    @Override
    public Immutable.Iterator<E> iterator()
    {
        return listIterator();
    }

    @Override
    public Immutable.Iterator<E> listIterator()
    {
        return listIterator(0);
    }

    @Override
    public Immutable.Iterator<E> listIterator(int index)
    {
        return new Immutable.Iterator<E>(size(), index)
        {

            @Override
            protected E get(int index)
            {
                return ImmutableCelestialList.this.get(index);
            }
        };
    }
}
