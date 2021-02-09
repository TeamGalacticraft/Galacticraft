package com.hrznstudio.galacticraft.misc;

import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ForwardingDefaultList<T> extends DefaultedList<T> {
    private int[] indexes;
    private final T initialElement;
    private final DefaultedList<T> delegate;

    public ForwardingDefaultList(DefaultedList<T> delegate, @Nullable T initialElement, int[] indexes) {
        super(delegate, initialElement);
        this.indexes = indexes;
        this.initialElement = initialElement;
        this.delegate = delegate;
    }

    @Override
    public @NotNull T get(int index) {
        return super.get(indexes[index]);
    }

    @Override
    public T set(int index, T element) {
        return super.set(indexes[index], element);
    }

    @Override
    public void add(int value, T element) {
        super.add(indexes[value], element);
    }

    @Override
    public T remove(int index) {
        return super.remove(indexes[index]);
    }

    @Override
    public int size() {
        return indexes.length;
    }

    @Override
    public void clear() {
        for (int i : indexes) {
            super.set(i, initialElement);
        }
    }

    @Override
    public boolean add(T t) {
        boolean b = super.add(t);
        if (b) {
            indexes = Arrays.copyOf(indexes, indexes.length + 1);
            indexes[indexes.length - 1] = delegate.size() - 1;
        }
        return b;
    }

    @Override
    public int indexOf(Object o) {
        for (int j = 0; j < indexes.length; j++) {
            int i = indexes[j];
            T t = delegate.get(i);
            if (t.equals(o)) {
                return j;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int j = indexes.length - 1; j >= 0; j--) {
            int i = indexes[j];
            T t = delegate.get(i);
            if (t.equals(o)) {
                return j;
            }
        }
        return -1;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return index + 1 < indexes.length;
            }

            @Override
            public T next() {
                return get(++index);
            }
        };
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListIterator<T>() {
            int pos = index;
            int last = -1;

            public boolean hasNext() {
                return this.pos < ForwardingDefaultList.this.size();
            }

            public boolean hasPrevious() {
                return this.pos > 0;
            }

            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    return ForwardingDefaultList.this.get(this.last = this.pos++);
                }
            }

            public T previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                } else {
                    return ForwardingDefaultList.this.get(this.last = --this.pos);
                }
            }

            public int nextIndex() {
                return this.pos;
            }

            public int previousIndex() {
                return this.pos - 1;
            }

            public void add(T k) {
                ForwardingDefaultList.this.add(this.pos++, k);
                this.last = -1;
            }

            public void set(T k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                } else {
                    ForwardingDefaultList.this.set(this.last, k);
                }
            }

            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                } else {
                    ForwardingDefaultList.this.remove(this.last);
                    if (this.last < this.pos) {
                        --this.pos;
                    }

                    this.last = -1;
                }
            }
        };
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }
}
