package dev.galacticraft.mod.attribute.misc;

import alexiil.mc.lib.attributes.misc.Reference;

import java.util.function.Predicate;

public record ArrayReference<T>(T[] array, int index, Predicate<T> predicate) implements Reference<T> {
    @Override
    public T get() {
        return this.array[index];
    }

    @Override
    public boolean set(T value) {
        if (this.isValid(value)) {
            this.array[index] = value;
            return true;
        }
        return false;
    }

    @Override
    public boolean isValid(T value) {
        return predicate.test(value);
    }
}
