/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.util.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;

import com.google.common.collect.ObjectArrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

abstract class Immutable<E> extends ArrayList<E> implements Serializable
{

    private static final long serialVersionUID            = -3175142460104260006L;
    static final int      SPLITERATOR_CHARACTERISTICS = Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED;
    static final Object[] EMPTY_ARRAY                 = new Object[0];

    public Immutable()
    {}

    public Immutable(Collection<E> collection)
    {
        super(collection);
    }

    /**
     * Returns an unmodifiable iterator across the elements in this collection.
     */
    @Override
    public abstract Immutable.Iterator<E> iterator();

    @Override
    public Spliterator<E> spliterator()
    {
        return Spliterators.spliterator(this, SPLITERATOR_CHARACTERISTICS);
    }

    @Override
    public final Object[] toArray()
    {
        int size = size();
        if (size == 0)
        {
            return EMPTY_ARRAY;
        }
        Object[] result = new Object[size];
        copyIntoArray(result, 0);
        return result;
    }

    @Override
    public final <T> T[] toArray(T[] other)
    {
        checkNotNull(other);
        int size = size();
        if (other.length < size)
        {
            other = ObjectArrays.newArray(other, size);
        }
        else if (other.length > size)
        {
            other[size] = null;
        }
        copyIntoArray(other, 0);
        return other;
    }

    int copyIntoArray(Object[] dst, int offset)
    {
        for (E e : this)
        {
            dst[offset++] = e;
        }
        return offset;
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final boolean add(E e)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final boolean remove(Object object)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final boolean addAll(Collection<? extends E> newElements)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final boolean removeAll(Collection<?> oldElements)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final boolean removeIf(Predicate<? super E> filter)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final boolean retainAll(Collection<?> elementsToKeep)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     */
    @Deprecated
    @Override
    public final void clear()
    {
        throw new UnsupportedOperationException();
    }

    public static abstract class Iterator<E> implements ListIterator<E>
    {

        private final int size;
        private int       position;

        /**
         * Returns the element with the specified index. This method is called
         * by {@link #next()}.
         */
        protected abstract E get(int index);

        protected Iterator(int size)
        {
            this(size, 0);
        }

        protected Iterator(int size, int position)
        {
            checkPositionIndex(position, size);
            this.size = size;
            this.position = position;
        }

        protected Iterator()
        {
            this.size = 0;
            this.position = 0;
        }

        @Override
        public final boolean hasNext()
        {
            return position < size;
        }

        @Override
        public final E next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            return get(position++);
        }

        @Override
        public final int nextIndex()
        {
            return position;
        }

        @Override
        public final boolean hasPrevious()
        {
            return position > 0;
        }

        @Override
        public final E previous()
        {
            if (!hasPrevious())
            {
                throw new NoSuchElementException();
            }
            return get(--position);
        }

        @Override
        public final int previousIndex()
        {
            return position - 1;
        }

        /**
         * Guaranteed to throw an exception and leave the underlying data
         * unmodified.
         *
         * @throws UnsupportedOperationException always
         */
        @Deprecated
        @Override
        public final void add(E e)
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Guaranteed to throw an exception and leave the underlying data
         * unmodified.
         *
         * @throws UnsupportedOperationException always
         */
        @Deprecated
        @Override
        public final void set(E e)
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Guaranteed to throw an exception and leave the underlying data
         * unmodified.
         *
         * @throws UnsupportedOperationException always
         */
        @Deprecated
        @Override
        public final void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
