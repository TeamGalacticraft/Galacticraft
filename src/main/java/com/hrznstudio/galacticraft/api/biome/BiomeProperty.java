package com.hrznstudio.galacticraft.api.biome;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BiomeProperty<T> {
    private final BiomePropertyType<T> type;
    private T value;

    public BiomeProperty(@NotNull BiomePropertyType<T> type) {
        this(type, type.getDefaultValue());
    }

    public BiomeProperty(@NotNull BiomePropertyType<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public BiomePropertyType<T> getType() {
        return type;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiomeProperty<?> that = (BiomeProperty<?>) o;
        return type.equals(that.type) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return "BiomeProperty{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
