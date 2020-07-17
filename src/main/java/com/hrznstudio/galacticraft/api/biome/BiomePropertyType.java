package com.hrznstudio.galacticraft.api.biome;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BiomePropertyType<T> {
    private final T defaultValue;
    private final Text name;

    private BiomePropertyType(T defaultValue, Text name) {
        this.defaultValue = defaultValue;
        this.name = name;
    }

    public BiomeProperty<T> create() {
        return new BiomeProperty<>(this);
    }

    public BiomeProperty<T> create(T value) {
        return new BiomeProperty<>(this, value);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Text getName() {
        return name;
    }

    public static class Builder<T> {
        private T defaultValue = null;
        private Text name = new LiteralText("");

        public Builder<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> name(Text name) {
            this.name = name;
            return this;
        }

        public BiomePropertyType<T> build() {
            return new BiomePropertyType<>(defaultValue, name);
        }
    }
}
